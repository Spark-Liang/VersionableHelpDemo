package org.nanospark.versionablehelper.core.infrastructure.po;

import java.time.LocalDate;

public interface VersionablePO {


    LocalDate MAX_LOCAL_DATE = LocalDate.of(9999, 12, 31);

    LocalDate getInitialEffectiveDate();

    void setInitialEffectiveDate(LocalDate initialEffectiveDate);

    LocalDate getEffectiveDate();

    void setEffectiveDate(LocalDate effectiveDate);

    LocalDate getExpiredDate();

    void setExpiredDate(LocalDate expiredDate);

    default boolean isEffectiveRecord() {
        return getEffectiveDate().isBefore(getExpiredDate());
    }
}
