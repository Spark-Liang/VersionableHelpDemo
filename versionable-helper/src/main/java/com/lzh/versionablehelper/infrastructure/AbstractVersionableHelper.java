package com.lzh.versionablehelper.infrastructure;

import com.lzh.versionablehelper.exception.VersionableException;
import com.lzh.versionablehelper.infrastructure.dto.ParameterForm;
import com.lzh.versionablehelper.infrastructure.po.BasePO;
import com.lzh.versionablehelper.infrastructure.po.VersionablePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.lzh.versionablehelper.exception.VersionableException.MessageConstants.SHOULD_PROVIDE_EFFECTIVE_DATE_OR_MASTER_ID;

@Repository
public abstract class AbstractVersionableHelper<M extends BasePO,H extends BasePO & VersionablePO<M>> implements VersionableHelper<M,H> {

    private static final Predicate[] EMPTY_PREDICATE_ARRAY = new Predicate[0];

    public static final String EFFECTIVE_DATE = "effectiveDate";
    public static final String EXPIRED_DATE = "expiredDate";
    public static final String MASTER_PO = "masterPO";

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<H> getVersionablePOByParamtertForm(ParameterForm params) {
        Specification<H> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            LocalDate effectiveDate = params.getEffectiveDate();
            Long masterId = params.getMasterId();
            if(effectiveDate == null && masterId == null){
                throw new VersionableException(SHOULD_PROVIDE_EFFECTIVE_DATE_OR_MASTER_ID);
            }

            if (effectiveDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(EFFECTIVE_DATE),effectiveDate));
                predicates.add(cb.greaterThanOrEqualTo(root.get(EXPIRED_DATE),effectiveDate));
            }else{
                predicates.add(cb.lessThanOrEqualTo(root.get(EFFECTIVE_DATE),root.get(EXPIRED_DATE)));
            }

            if(masterId != null){
                predicates.add(cb.equal(root.get(MASTER_PO),masterId));
            }

            query.where(predicates.toArray(EMPTY_PREDICATE_ARRAY));
            return null;
        };
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<H> criteriaQuery = criteriaBuilder.createQuery(getHistoryPOClass());
        specification.toPredicate(criteriaQuery.from(getHistoryPOClass()),
                criteriaQuery,
                criteriaBuilder);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<H> getHistoryPOsByMasterPO(M masterPO, ParameterForm params) {
        return null;
    }

    @Override
    public void persist(M masterPO) {

    }

    @Override
    public void persistAll(Collection<M> masterPOs) {

    }

    @Override
    public void persist(H historyPO, boolean crossDayChange) {

    }

    @Override
    public void persistAll(Map<H, Boolean> historyPOs) {

    }
}
