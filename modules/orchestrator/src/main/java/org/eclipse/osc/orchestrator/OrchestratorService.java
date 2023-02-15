package org.eclipse.osc.orchestrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.karaf.minho.boot.service.LifeCycleService;
import org.apache.karaf.minho.boot.service.ServiceRegistry;
import org.apache.karaf.minho.boot.spi.Service;
import org.eclipse.osc.modules.ocl.loader.Ocl;
import org.eclipse.osc.modules.ocl.loader.OclLoader;
import org.eclipse.osc.modules.ocl.loader.OclResources;

@Slf4j
@Data
public class OrchestratorService implements Service {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public static ExecutorService executorService = new ThreadPoolExecutor(CPU_COUNT * 2,
        CPU_COUNT * 4,
        300, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100));
    public static Map<String, Ocl> managedOcl = new HashMap<>(16);
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<OrchestratorPlugin> plugins = new ArrayList<>();
    private OrchestratorStorage storage;
    private OclLoader oclLoader;

    @Override
    public String name() {
        return "osc-orchestrator";
    }

    @Override
    public void onRegister(ServiceRegistry serviceRegistry) throws Exception {
        log.info("Registering OSC orchestrator service ...");

        oclLoader = serviceRegistry.get(OclLoader.class);
        if (oclLoader == null) {
            throw new IllegalStateException("OCL Loader service is not present");
        }

        storage = serviceRegistry.get(OrchestratorStorage.class);
        if (storage == null) {
            log.warn(
                "No orchestrator storage service found in the service registry, using default "
                    + "file orchestrator storage");
            storage = new FileOrchestratorStorage(serviceRegistry);
        }

        LifeCycleService lifeCycleService = serviceRegistry.get(LifeCycleService.class);

        lifeCycleService.onStart(() -> {
            log.info("Loading OSC orchestrator plugins");
            plugins = serviceRegistry.getAll().values().stream()
                .filter(service -> service instanceof OrchestratorPlugin)
                .map(service -> (OrchestratorPlugin) service).collect(Collectors.toList());
        });
    }

    /**
     * Register a managed service on all orchestrator plugins, using OCL descriptor location.
     *
     * @param oclLocation the location of the OCL descriptor.
     * @throws Exception if registration fails.
     */
    public void registerManagedService(String oclLocation) throws Exception {
        Ocl ocl = oclLoader.getOcl(new URL(oclLocation));
        registerManagedService(ocl);
    }

    /**
     * Register a managed service on all orchestrator plugins, directly using OCL descriptor.
     *
     * @param ocl the OCL descriptor.
     * @throws Exception if registration fails.
     */
    public void registerManagedService(Ocl ocl) throws Exception {
        if (ocl == null) {
            throw new IllegalArgumentException("registering invalid ocl. ocl = null");
        }
        if (ocl.getName() == null) {
            throw new IllegalArgumentException("Managed service name is required");
        }
        if (OrchestratorService.managedOcl.containsKey(ocl.getName())){
            throw new IllegalArgumentException("Managed service name already registered.");
        }
        OrchestratorService.managedOcl.put(ocl.getName(), ocl);
        plugins.forEach(plugin -> {
            plugin.registerManagedService(ocl);
        });
        storage.store(ocl.getName());
    }

    /**
     * Update existing managed service with a new/updated OCL descriptor, at the given location
     *
     * @param managedServiceName the managed service to update, identified by the given name.
     * @param oclLocation        the new/updated OCL descriptor location.
     * @throws Exception if the update fails.
     */
    public void updateManagedService(String managedServiceName, String oclLocation)
        throws Exception {
        Ocl ocl = oclLoader.getOcl(new URL(oclLocation));
        if (ocl == null) {
            throw new IllegalArgumentException("Invalid ocl. ocl = null");
        }
        managedOcl.put(managedServiceName, ocl);
        updateManagedService(managedServiceName, ocl);
    }

    /**
     * Update existing managed service with a new/updated OCL descriptor.
     *
     * @param managedServiceName the managed service to update, identified by the given name.
     * @param ocl                the new/update OCL descriptor.
     * @throws Exception if the update fails.
     */
    public void updateManagedService(String managedServiceName, Ocl ocl) throws Exception {
        if (ocl == null) {
            throw new IllegalArgumentException("Invalid ocl. ocl = null");
        }
        if (ocl.getName() == null) {
            throw new IllegalArgumentException("Managed service name is required");
        }
        managedOcl.put(managedServiceName, ocl);
        plugins.forEach(plugin -> {
            plugin.updateManagedService(managedServiceName, ocl);
        });
    }

    /**
     * Start (expose to users) a managed service on all orchestrator plugins.
     *
     * @param managedServiceName the managed service name.
     * @throws Exception if start fails.
     */
    public void startManagedService(String managedServiceName) throws Exception {
        if (!storage.exists(managedServiceName)) {
            throw new IllegalStateException("Managed service " + managedServiceName + " not found");
        }
        if (!managedOcl.containsKey(managedServiceName)) {
            throw new IllegalArgumentException("Service:" + managedServiceName + "not registered.");
        }

        Ocl ocl = managedOcl.get(managedServiceName).deepCopy();
        if (ocl == null) {
            throw new IllegalStateException("Ocl object is null.");
        }
        OclResources oclResources = getOclResources(managedServiceName);
        if (oclResources != null && oclResources.getState().equals("building")) {
            log.info("Managed service {} already in active.", managedServiceName);
            throw new IllegalStateException("Managed service already in building.");
        }
        if (oclResources != null && oclResources.getState().equals("success")) {
            log.info("Managed service {} already in active.", managedServiceName);
            throw new IllegalStateException("Managed service already in active.");
        }
        plugins.forEach(plugin -> {
            executorService.execute(
                () -> {
                    Thread.currentThread()
                        .setName("Plugin_startManagedService_" + managedServiceName);
                    plugin.startManagedService(managedServiceName);
                });
        });
    }

    /**
     * Stop (managed service is not visible to users anymore) a managed service on all orchestrator
     * plugins.
     *
     * @param managedServiceName the managed service name.
     * @throws Exception if stop fails.
     */
    public void stopManagedService(String managedServiceName) throws Exception {
        if (!managedOcl.containsKey(managedServiceName)) {
            throw new IllegalArgumentException("Service:" + managedServiceName + "not registered.");
        }
        if (!storage.exists(managedServiceName)) {
            throw new IllegalStateException("Managed service " + managedServiceName + " not found");
        }
        plugins.forEach(plugin -> {
            plugin.stopManagedService(managedServiceName);
        });
    }

    /**
     * Unregister a managed service and destroy/clean all associated resources on all orchestrator
     * plugins.
     *
     * @param managedServiceName the managed service name.
     * @throws Exception if unregister fails.
     */
    public void unregisterManagedService(String managedServiceName) throws Exception {
        if (!storage.exists(managedServiceName)) {
            throw new IllegalStateException("Managed service " + managedServiceName + " not found");
        }
        if (!managedOcl.containsKey(managedServiceName)) {
            throw new IllegalArgumentException("Service:" + managedServiceName + "not registered.");
        }
        managedOcl.remove(managedServiceName);
        plugins.forEach(plugin -> {
            plugin.unregisterManagedService(managedServiceName);
        });
        storage.remove(managedServiceName);
    }

    /**
     * Get the runtime state of the managed service.
     *
     * @param managedServiceName the managed service name.
     */
    public String getManagedServiceState(String managedServiceName) throws Exception {
        if (!storage.exists(managedServiceName)) {
            throw new IllegalStateException("Managed service " + managedServiceName + " not found");
        }
        StringBuilder response = new StringBuilder("[\n");
        plugins.forEach(plugin -> {
            if (plugin instanceof Service) {
                response.append(storage.getKey(managedServiceName, ((Service) plugin).name(),
                    "state"));
                response.append("\n");
            }
        });
        response.append("]\n");

        return response.toString();
    }

    public OclResources getOclResources(String managedServiceName) {
        OclResources oclResources;
        String oclResourceStr;
        try {
            if (storage != null) {
                oclResourceStr = storage.getKey(managedServiceName, name(), "state");
                oclResources = objectMapper.readValue(oclResourceStr, OclResources.class);
            } else {
                oclResources = new OclResources();
            }
        } catch (JsonProcessingException ex) {
            log.error("Serial OCL object to json failed.", ex);
            oclResources = new OclResources();
        }
        return oclResources;
    }

}
