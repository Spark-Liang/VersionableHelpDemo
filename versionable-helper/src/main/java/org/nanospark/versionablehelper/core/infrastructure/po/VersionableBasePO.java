package org.nanospark.versionablehelper.core.infrastructure.po;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;
import java.util.Objects;

@MappedSuperclass
public class VersionableBasePO extends BasePO implements VersionablePO {

    public static final String INITIAL_EFFECTIVE_DATE = "initialEffectiveDate";
    public static final String EFFECTIVE_DATE = "effectiveDate";
    public static final String EXPIRED_DATE = "expiredDate";

    private LocalDate initialEffectiveDate;

    private LocalDate effectiveDate;

    private LocalDate expiredDate;

    @Override
    public String toString() {
        return "VersionableBasePO{" +
                "initialEffectiveDate=" + initialEffectiveDate +
                ", effectiveDate=" + effectiveDate +
                ", expiredDate=" + expiredDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VersionableBasePO)) return false;
        if (!super.equals(o)) return false;
        VersionableBasePO that = (VersionableBasePO) o;
        return Objects.equals(initialEffectiveDate, that.initialEffectiveDate) &&
                Objects.equals(effectiveDate, that.effectiveDate) &&
                Objects.equals(expiredDate, that.expiredDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), initialEffectiveDate, effectiveDate, expiredDate);
    }

    @Override
    @Column(name = INITIAL_EFFECTIVE_DATE, nullable = false)
    public LocalDate getInitialEffectiveDate() {
        return initialEffectiveDate;
    }

    public void setInitialEffectiveDate(LocalDate initialEffectiveDate) {
        this.initialEffectiveDate = initialEffectiveDate;
    }

    @Override
    @Column(name = EFFECTIVE_DATE, nullable = false)
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Override
    @Column(name = EXPIRED_DATE, nullable = false)
    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }
}
