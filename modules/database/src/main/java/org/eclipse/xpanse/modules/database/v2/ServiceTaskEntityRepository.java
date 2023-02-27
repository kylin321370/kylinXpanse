/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.modules.database.v2;

import java.util.UUID;
import org.eclipse.xpanse.modules.database.ServiceStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface to access default JPA methods.
 */
@Repository
public interface ServiceTaskEntityRepository extends JpaRepository<ServiceTaskEntity, UUID> {
}
