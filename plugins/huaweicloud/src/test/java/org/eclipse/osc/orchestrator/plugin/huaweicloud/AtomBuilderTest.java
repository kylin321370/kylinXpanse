package org.eclipse.osc.orchestrator.plugin.huaweicloud;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.apache.karaf.minho.boot.service.ConfigService;
import org.eclipse.osc.orchestrator.OrchestratorStorage;
import org.eclipse.osc.orchestrator.plugin.huaweicloud.builders.HuaweiEnvBuilder;
import org.eclipse.osc.orchestrator.plugin.huaweicloud.builders.HuaweiImageBuilder;
import org.eclipse.osc.orchestrator.plugin.huaweicloud.builders.HuaweiResourceBuilder;
import org.eclipse.osc.modules.ocl.loader.Ocl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class AtomBuilderTest {

    private HuaweiEnvBuilder envBuilder;
    private HuaweiImageBuilder imageBuilder;
    private HuaweiResourceBuilder resourceBuilder;
    private BuilderContext ctx;
    private Ocl ocl;
    private OrchestratorStorage storage;

    @BeforeEach
    public void mockBuilder() {
        ocl = new Ocl();

        ConfigService conf = new ConfigService();
        conf.setProperties(
            Map.of(HuaweiEnvBuilder.ACCESS_KEY, "test_access_key", HuaweiEnvBuilder.SECRET_KEY,
                "test_secret_key", HuaweiEnvBuilder.REGION_NAME, "test_region_name"));
        ctx = new BuilderContext();
        ctx.setConfig(conf);
        storage = spy(new FileOrchestratorStorage());
        ctx.setStorage(storage);
        envBuilder = spy(new HuaweiEnvBuilder(ocl));
        imageBuilder = spy(new HuaweiImageBuilder(ocl));
        resourceBuilder = spy(new HuaweiResourceBuilder(ocl));
        ctx.setPluginName("osc-orchestrator-huaweicloud");
        ctx.setServiceName("My-test-service");
    }

    @Test
    public void builderListTest() {
        resourceBuilder.addSubBuilder(imageBuilder);
        doReturn(true).when(envBuilder).create(any());
        doReturn(true).when(imageBuilder).create(any());
        doReturn(true).when(resourceBuilder).create(any());
        Assertions.assertTrue(envBuilder.build(ctx));
        Assertions.assertTrue(resourceBuilder.build(ctx));
        verify(envBuilder, times(1)).build(ctx);
        verify(resourceBuilder, times(1)).build(ctx);
    }

    @Test
    public void builderListFailedTest() {
        resourceBuilder.addSubBuilder(imageBuilder);
        doReturn(true).when(envBuilder).create(any());
        doReturn(false).when(imageBuilder).create(any());
        doReturn(true).when(resourceBuilder).create(any());
        Assertions.assertTrue(envBuilder.build(ctx));
        Assertions.assertTrue(resourceBuilder.build(ctx));
        verify(envBuilder, times(1)).build(ctx);
        verify(resourceBuilder, times(1)).build(ctx);
    }

    @Test
    public void builderListRollbackTest() {
        imageBuilder.addSubBuilder(envBuilder);
        resourceBuilder.addSubBuilder(imageBuilder);

        doReturn(true).when(envBuilder).destroy(any());
        doReturn(true).when(imageBuilder).destroy(any());
        doReturn(true).when(resourceBuilder).destroy(any());
        Assertions.assertTrue(resourceBuilder.rollback(ctx));

        verify(envBuilder, times(1)).rollback(ctx);
        verify(envBuilder, times(1)).destroy(ctx);
    }

    @Test
    public void builderTreeRollbackTest() {
        HuaweiEnvBuilder envBuilder2 = spy(new HuaweiEnvBuilder(ocl));

        imageBuilder.addSubBuilder(envBuilder);
        imageBuilder.addSubBuilder(envBuilder2);
        resourceBuilder.addSubBuilder(imageBuilder);

        doReturn(true).when(envBuilder).destroy(any());
        doReturn(true).when(envBuilder2).destroy(any());
        doReturn(true).when(imageBuilder).destroy(any());
        doReturn(true).when(resourceBuilder).destroy(any());
        Assertions.assertTrue(resourceBuilder.rollback(ctx));

        verify(envBuilder, times(1)).rollback(ctx);
        verify(envBuilder, times(1)).destroy(ctx);
        verify(envBuilder2, times(1)).rollback(ctx);
        verify(envBuilder2, times(1)).destroy(ctx);
        verify(imageBuilder, times(1)).destroy(ctx);
    }

    @Test
    public void builderListRollbackFailedTest() {
        imageBuilder.addSubBuilder(envBuilder);
        resourceBuilder.addSubBuilder(imageBuilder);

        doReturn(false).when(envBuilder).destroy(any());
        doReturn(true).when(imageBuilder).destroy(any());
        doReturn(true).when(resourceBuilder).destroy(any());

        Assertions.assertFalse(resourceBuilder.rollback(ctx));

        verify(envBuilder, times(1)).rollback(ctx);
        verify(envBuilder, times(1)).destroy(ctx);
    }
}
