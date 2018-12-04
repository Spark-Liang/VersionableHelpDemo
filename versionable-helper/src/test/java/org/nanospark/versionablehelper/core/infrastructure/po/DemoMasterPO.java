package org.nanospark.versionablehelper.core.infrastructure.po;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "DemoMasterPO")
public class DemoMasterPO extends DemoBasePO {

    private Long id;

    @Override
    public String toString() {
        return "DemoMasterPO{" +
                "id=" + id +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DemoMasterPO)) return false;
        if (!super.equals(o)) return false;
        DemoMasterPO that = (DemoMasterPO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
