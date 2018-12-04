package org.nanospark.versionablehelper.core.infrastructure.repository;

import org.nanospark.versionablehelper.core.infrastructure.po.DemoMasterPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoMasterPORepository extends JpaRepository<DemoMasterPO,Long> {
}
