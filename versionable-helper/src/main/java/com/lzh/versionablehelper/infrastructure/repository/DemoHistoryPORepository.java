package com.lzh.versionablehelper.infrastructure.repository;

import com.lzh.versionablehelper.infrastructure.po.DemoHistoryPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoHistoryPORepository extends JpaRepository<DemoHistoryPO,Long> {
}
