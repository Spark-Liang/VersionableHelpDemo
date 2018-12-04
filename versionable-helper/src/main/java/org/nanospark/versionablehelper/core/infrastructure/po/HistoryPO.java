package org.nanospark.versionablehelper.core.infrastructure.po;

public interface HistoryPO<MPK> {

    String MASTER_ID = "masterId";

    MPK getMasterId();

    void setMasterId(MPK masertId);
}
