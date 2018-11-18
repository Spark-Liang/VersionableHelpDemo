package com.lzh.versionablehelper.infrastructure.po;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "DemoHistoryPO")
public class DemoHistoryPO extends DemoBasePO implements VersionablePO<DemoMasterPO> {

    private DemoMasterPO masterPO;

    private LocalDate effectiveDate;

    private LocalDate expiredDate;

    @Override
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "masterId")
    public DemoMasterPO getMasterPO() {
        return masterPO;
    }

    public void setMasterPO(DemoMasterPO masterPO) {
        this.masterPO = masterPO;
    }

    @Override
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Override
    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }
}
