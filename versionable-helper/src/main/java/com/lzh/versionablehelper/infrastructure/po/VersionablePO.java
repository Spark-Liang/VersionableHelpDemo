package com.lzh.versionablehelper.infrastructure.po;

import javax.persistence.*;
import java.time.LocalDate;

import static com.lzh.versionablehelper.infrastructure.AbstractVersionableHelper.EFFECTIVE_DATE;
import static com.lzh.versionablehelper.infrastructure.AbstractVersionableHelper.EXPIRED_DATE;

@MappedSuperclass
public interface VersionablePO<M extends BasePO> {

    M getMasterPO();

    @Column(name = EFFECTIVE_DATE,nullable = false)
    LocalDate getEffectiveDate();

    @Column(name = EXPIRED_DATE,nullable = false)
    LocalDate getExpiredDate();

    default boolean isEffectiveRecord () {
        return getEffectiveDate().isBefore(getExpiredDate());
    }
}
