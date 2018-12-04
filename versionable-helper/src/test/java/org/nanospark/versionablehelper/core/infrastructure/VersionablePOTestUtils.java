package org.nanospark.versionablehelper.core.infrastructure;

import org.nanospark.versionablehelper.core.infrastructure.po.VersionablePO;

import java.time.LocalDate;
import java.util.Objects;

public class VersionablePOTestUtils {

    static boolean isMatchAllDate(VersionablePO po, LocalDate initialEffectiveDate, LocalDate effectiveDate, LocalDate expiredDate) {
        return Objects.equals(po.getInitialEffectiveDate(), initialEffectiveDate)
                && Objects.equals(po.getEffectiveDate(), effectiveDate)
                && Objects.equals(po.getExpiredDate(), expiredDate);
    }

    static void setDatesToVersionablePO(VersionablePO po, LocalDate initialEffectiveDate, LocalDate effectiveDate, LocalDate expriedDate) {
        po.setInitialEffectiveDate(initialEffectiveDate);
        po.setEffectiveDate(effectiveDate);
        po.setExpiredDate(expriedDate);
    }

    static boolean isEffectivePOOfTheEffectiveDate(LocalDate effectiveDate, VersionablePO po) {
        return (po.getEffectiveDate().isBefore(effectiveDate) || po.getEffectiveDate().isEqual(effectiveDate))
                && po.getExpiredDate().isAfter(effectiveDate);
    }
}
