package org.nanospark.versionablehelper.core.infrastructure.po;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "DemoHistoryPO")
public class DemoHistoryPO extends DemoBasePO implements HistoryPO<Long> {

    private Long histId;

    private Long masterId;

    @Override
    public String toString() {
        return "DemoHistoryPO{" +
                "histId=" + histId +
                ", masterId=" + masterId +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DemoHistoryPO)) return false;
        if (!super.equals(o)) return false;
        DemoHistoryPO historyPO = (DemoHistoryPO) o;
        return Objects.equals(histId, historyPO.histId) &&
                Objects.equals(masterId, historyPO.masterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), histId, masterId);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getHistId() {
        return histId;
    }

    public void setHistId(Long histId) {
        this.histId = histId;
    }

    @Column(name = MASTER_ID, nullable = false)
    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }
}
