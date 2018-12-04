package org.nanospark.versionablehelper.core.infrastructure;

import org.nanospark.versionablehelper.core.infrastructure.po.VersionableBasePO;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface VersionableHelper<M extends VersionableBasePO, MPK, H extends VersionableBasePO, HPK> {

    boolean DEFAULT_CROSS_DAY_CHANGE = true;

    /**
     * get the history version by the given predication .
     *
     * @param params the given predication to find historyPO
     * @return list instance of {@link H}
     */
    List<H> getHistoryPOByParametertForm(ParameterForm<MPK> params);

    /**
     * find current version of the MasterPO from historyPORepository.
     *
     * @param mpk master key
     * @return instance of {@link M}
     * @see VersionableHelper#getVersionByMasterIdAndEffective(Object, LocalDate)
     */
    default M getCurrentVersionByMasterId(MPK mpk) {
        return getVersionByMasterIdAndEffective(mpk, LocalDate.now());
    }

    /**
     * find a version of the MasterPo in the given effectiveDate from HistoryPORepository,
     * and merge all the common properties to an masterPO instance which is find by the masterKey
     * from the masterPORepository.
     *
     * @param mpk           masterKey.
     * @param effectiveDate the date of the version to find.
     * @return instance of the {@link M}.If not record match than return null.
     */
    M getVersionByMasterIdAndEffective(MPK mpk, LocalDate effectiveDate);

    /**
     * @param versionPO
     * @see VersionableHelper#persist(VersionableBasePO, boolean)
     */
    default void persist(M versionPO) {
        persist(versionPO, DEFAULT_CROSS_DAY_CHANGE);
    }

    /**
     * @param versionPO      a change point of the history
     * @param crossDayChange the flag to choose whether the change properties will influence the future record
     */
    void persist(M versionPO, boolean crossDayChange);

    default void persistAll(Collection<M> versionPOs) {
        persistAll(versionPOs, DEFAULT_CROSS_DAY_CHANGE);
    }

    void persistAll(Collection<M> versionPOs, boolean crossDayChange);

    /**
     * the predication object of the {@link VersionableHelper}.
     *
     * @param <MPK> type of master key
     */
    class ParameterForm<MPK> {

        private MPK masterId;

        private LocalDate effectiveDate;

        private LocalDate expiredDate;

        private Map<String, Object> params;

        public MPK getMasterId() {
            return masterId;
        }

        public void setMasterId(MPK masterId) {
            this.masterId = masterId;
        }

        public LocalDate getEffectiveDate() {
            return effectiveDate;
        }

        public void setEffectiveDate(LocalDate effectiveDate) {
            this.effectiveDate = effectiveDate;
        }

        public LocalDate getExpiredDate() {
            return expiredDate;
        }

        public void setExpiredDate(LocalDate expiredDate) {
            this.expiredDate = expiredDate;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
    }
}
