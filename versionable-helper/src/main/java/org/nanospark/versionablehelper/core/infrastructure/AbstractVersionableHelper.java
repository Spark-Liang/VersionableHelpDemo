package org.nanospark.versionablehelper.core.infrastructure;

import org.nanospark.versionablehelper.core.exception.SystemException;
import org.nanospark.versionablehelper.core.exception.VersionableException;
import org.nanospark.versionablehelper.core.infrastructure.po.HistoryPO;
import org.nanospark.versionablehelper.core.infrastructure.po.VersionableBasePO;
import org.nanospark.versionablehelper.core.infrastructure.po.VersionablePO;
import org.nanospark.versionablehelper.core.util.CrossDayChangePropertyFilter;
import org.nanospark.versionablehelper.core.util.FilteringBeanCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.nanospark.versionablehelper.core.infrastructure.po.HistoryPO.MASTER_ID;
import static org.nanospark.versionablehelper.core.infrastructure.po.VersionableBasePO.EFFECTIVE_DATE;
import static org.nanospark.versionablehelper.core.infrastructure.po.VersionableBasePO.EXPIRED_DATE;

public abstract class AbstractVersionableHelper<M extends VersionableBasePO, MPK, H extends VersionableBasePO & HistoryPO<MPK>, HPK> implements VersionableHelper<M, MPK, H, HPK> {

    private static final Predicate[] EMPTY_PREDICATE_ARRAY = new Predicate[0];

    private static final Comparator<VersionablePO> VERSIONABLE_PO_COMPARATOR
            = Comparator.comparing(VersionablePO::getEffectiveDate);

    private static final BiPredicate<PropertyDescriptor, PropertyDescriptor> CROSS_DAY_CHANGE_PROPERTY_FILTER = new CrossDayChangePropertyFilter();
    private static final BeanCopier fromVersionablePOToVersionablePO = BeanCopier.create(VersionableBasePO.class, VersionableBasePO.class, false);
    @Autowired
    private EntityManager entityManager;
    private Class<M> masterPOClass;
    private Class<H> historyPOClass;
    private Supplier<M> masterPOConstructor;
    private Supplier<H> historyPOConstructor;
    private Function<M, MPK> masterPOKeyExtractor;
    private Function<H, HPK> historyPOKeyExtractor;
    private BeanCopier fromHistoryToMaster;
    private BeanCopier fromMasterToHistory;

    private FilteringBeanCopier<M, H> crossDayChangeCopier;

    protected AbstractVersionableHelper() {
        ConstructorParameter<M, MPK, H, HPK> parameter = getConstructorParameter();
        if (!parameter.isValidated()) {
            throw new IllegalArgumentException();
        }
        this.masterPOClass = parameter.masterPOClass;
        this.historyPOClass = parameter.historyPOClass;

        this.masterPOConstructor = parameter.masterPOConstructor;
        this.historyPOConstructor = parameter.historyPOConstructor;

        this.masterPOKeyExtractor = parameter.masterPOKeyExtractor;
        this.historyPOKeyExtractor = parameter.historyPOKeyExtractor;

        fromHistoryToMaster = BeanCopier.create(historyPOClass, masterPOClass, false);
        fromMasterToHistory = BeanCopier.create(masterPOClass, historyPOClass, false);

        crossDayChangeCopier = FilteringBeanCopier.create(masterPOClass, historyPOClass, CROSS_DAY_CHANGE_PROPERTY_FILTER);
    }


    @Override
    public List<H> getHistoryPOByParametertForm(ParameterForm<MPK> param) {
        Specification<H> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            LocalDate effectiveDate = param.getEffectiveDate();
            MPK masterId = param.getMasterId();
            if (effectiveDate == null && masterId == null) {
                throw new VersionableException(VersionableException.MessageConstants.SHOULD_PROVIDE_EFFECTIVE_DATE_OR_MASTER_ID);
            }

            if (effectiveDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(EFFECTIVE_DATE), effectiveDate));
                predicates.add(cb.greaterThan(root.get(EXPIRED_DATE), effectiveDate));
            } else {
                predicates.add(cb.lessThanOrEqualTo(root.get(EFFECTIVE_DATE), root.get(EXPIRED_DATE)));
            }

            if (masterId != null) {
                predicates.add(cb.equal(root.get(MASTER_ID), masterId));
            }

            try {
                Map<String, Object> paramsMap = param.getParams();
                if (paramsMap != null) {
                    paramsMap.forEach((path, value) -> {
                        predicates.add(cb.equal(root.get(path), value));
                    });
                }
            } catch (Exception e) {
                throw new SystemException(SystemException.MessageConstants.PROPERTY_IS_NOT_EXISTS, e);
            }

            query.where(predicates.toArray(EMPTY_PREDICATE_ARRAY));
            return null;
        };
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<H> criteriaQuery = criteriaBuilder.createQuery(historyPOClass);
        specification.toPredicate(criteriaQuery.from(historyPOClass),
                criteriaQuery,
                criteriaBuilder);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public M getCurrentVersionByMasterId(MPK mpk) {
        return null;
    }

    @Override
    public M getVersionByMasterIdAndEffective(MPK mpk, LocalDate effectiveDate) {
        Specification<H> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(MASTER_ID), mpk));
            predicates.add(cb.lessThanOrEqualTo(root.get(EFFECTIVE_DATE), effectiveDate));
            predicates.add(cb.greaterThan(root.get(EXPIRED_DATE), effectiveDate));
            query.where(predicates.toArray(EMPTY_PREDICATE_ARRAY));
            return null;
        };
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<H> criteriaQuery = criteriaBuilder.createQuery(historyPOClass);
        specification.toPredicate(criteriaQuery.from(historyPOClass),
                criteriaQuery,
                criteriaBuilder);
        List<H> historyPOs = entityManager.createQuery(criteriaQuery).getResultList();
        if (historyPOs.size() > 1) {
            throw new SystemException(SystemException.MessageConstants.DUPLICATED_EFFECTIVE_RECORD);
        } else if (historyPOs.size() == 0) {
            return null;
        }
        H historyPO = historyPOs.get(0);
        M masterPO = entityManager.find(masterPOClass, mpk);
        fromHistoryToMaster.copy(historyPO, masterPO, null);
        return masterPO;

    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void persist(M versionPO, boolean crossDayChange) {
        LocalDate givenEffDate = versionPO.getEffectiveDate();
        if (givenEffDate == null) {
            throw new SystemException(SystemException.MessageConstants.EFFECTIVE_DATE_IS_NULL);
        }
        MPK masterId = masterPOKeyExtractor.apply(versionPO);
        if (masterId == null) {
            handleInsertVersionPO(versionPO);
        } else {
            handleUpdateVersionPO(versionPO, masterId, crossDayChange);
        }
    }

    private void handleUpdateVersionPO(M versionPO, MPK masterId, boolean crossDayChange) {
        LocalDate givenEffDate = versionPO.getEffectiveDate();
        H historyToPersist = historyPOConstructor.get();
        historyToPersist.setMasterId(masterId);
        fromMasterToHistory.copy(versionPO, historyToPersist, null);

        ParameterForm<MPK> parameterForm = new ParameterForm<>();
        parameterForm.setMasterId(masterId);
        List<H> historyPOs = getHistoryPOByParametertForm(parameterForm);

        HistoryChain<H> historyChain = new HistoryChain<>(historyPOs);
        historyChain.add(historyToPersist);
        List<H> historyPOsToPersist = historyChain.getValueList();
        if (crossDayChange) {
            List<H> historyPOToCrossDayChange = historyChain.getHistoryVersionOfAndAfter(givenEffDate);
            historyPOToCrossDayChange.forEach(po -> crossDayChangeCopier.copy(versionPO, po));
        }
        historyPOsToPersist.forEach(entityManager::persist);

        if (givenEffDate.isBefore(LocalDate.now())) {
            fromVersionablePOToVersionablePO.copy(historyToPersist, versionPO, null);
            entityManager.merge(versionPO);
        }
    }

    private void handleInsertVersionPO(M versionPO) {
        MPK masterId;
        versionPO.setInitialEffectiveDate(versionPO.getEffectiveDate());
        versionPO.setExpiredDate(VersionablePO.MAX_LOCAL_DATE);
        entityManager.persist(versionPO);
        masterId = masterPOKeyExtractor.apply(versionPO);

        H historyPO = historyPOConstructor.get();
        historyPO.setMasterId(masterId);
        fromMasterToHistory.copy(versionPO, historyPO, null);
        entityManager.persist(historyPO);
    }

    @Override
    public void persistAll(Collection<M> versionPOs, boolean crossDayChange) {

    }

    protected abstract ConstructorParameter<M, MPK, H, HPK> getConstructorParameter();

    protected static class ConstructorParameter<M, MPK, H, HPK> {
        public Class<M> masterPOClass;
        public Class<H> historyPOClass;

        public Supplier<M> masterPOConstructor;
        public Supplier<H> historyPOConstructor;

        public Function<M, MPK> masterPOKeyExtractor;
        public Function<H, HPK> historyPOKeyExtractor;

        public ConstructorParameter() {
        }

        boolean isValidated() {
            //TODO
            return true;
        }

    }
}
