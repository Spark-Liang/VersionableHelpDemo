package org.nanospark.versionablehelper.core.infrastructure.repository;

import org.nanospark.versionablehelper.core.infrastructure.po.DemoHistoryPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemoHistoryPORepository extends JpaRepository<DemoHistoryPO, Long> {

    List<DemoHistoryPO> findByMasterId(Long masterId);
}
