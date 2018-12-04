package org.nanospark.versionablehelper.core.infrastructure.po;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.util.Objects;

@MappedSuperclass
public class BasePO {

    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasePO basePO = (BasePO) o;
        return Objects.equals(version, basePO.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }

    @Version
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
