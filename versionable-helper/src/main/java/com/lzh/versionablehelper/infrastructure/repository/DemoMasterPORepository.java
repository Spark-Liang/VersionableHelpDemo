package com.lzh.versionablehelper.infrastructure.repository;

import com.lzh.versionablehelper.infrastructure.po.DemoMasterPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoMasterPORepository extends JpaRepository<DemoMasterPO,Long> {
}
