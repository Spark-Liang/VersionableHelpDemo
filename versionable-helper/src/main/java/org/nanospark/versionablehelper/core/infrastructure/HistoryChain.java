package org.nanospark.versionablehelper.core.infrastructure;

import org.nanospark.versionablehelper.core.infrastructure.po.VersionablePO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.nanospark.versionablehelper.core.infrastructure.po.VersionablePO.MAX_LOCAL_DATE;

/**
 * A collection to describe an history flow.This feature of the collection are listed as below.
 * <ul>
 * <li>all entry will have the effectiveDate and will sorted by the effectiveDate.The earlier will in a more advanced position.</li>
 * <li>The expiredDate of previous entry will equals to its effectiveDate.</li>
 * <li>The initialEffectiveDate of the chain will equals to the effectiveDate of the first one.</li>
 * <li>Each initialEffectiveDate of the entry will equals to the initialEffectiveDate of the chain</li>
 * <li>The expiredDate of the chain will always equals {@link VersionablePO#MAX_LOCAL_DATE}</li>
 * </ul>
 *
 * @param <E>
 */
public class HistoryChain<E extends VersionablePO> {

    private final List<E> valueList = new ArrayList<>();

    public HistoryChain() {
    }

    public HistoryChain(Collection<E> values) {
        addAll(values);
    }

    public void add(E e) {
        LocalDate givenEffDate = e.getEffectiveDate();
        if (givenEffDate == null) {
            throw new NullPointerException("effective of the given object is null");
        }

        if (valueList.isEmpty()) {
            e.setInitialEffectiveDate(e.getEffectiveDate());
            e.setExpiredDate(MAX_LOCAL_DATE);
            valueList.add(e);
            return;
        }

        Integer firstIndBehindGivenEffDate = null;
        for (int i = 0, maxInd = valueList.size(); i < maxInd; i++) {
            if (givenEffDate.isBefore(valueList.get(i).getEffectiveDate())) {
                firstIndBehindGivenEffDate = i;
                break;
            }
        }

        if (firstIndBehindGivenEffDate == null) {
            e.setInitialEffectiveDate(getChainInitialEffectiveDate());
            e.setExpiredDate(MAX_LOCAL_DATE);
            E previousPO = valueList.get(valueList.size() - 1);
            previousPO.setExpiredDate(e.getEffectiveDate());
            valueList.add(e);
        } else {
            e.setExpiredDate(valueList.get(firstIndBehindGivenEffDate).getEffectiveDate());
            valueList.add(firstIndBehindGivenEffDate, e);
            if (firstIndBehindGivenEffDate == 0) {
                valueList.forEach(po -> po.setInitialEffectiveDate(givenEffDate));
            } else {
                e.setInitialEffectiveDate(getChainInitialEffectiveDate());
                E previousPO = valueList.get(firstIndBehindGivenEffDate - 1);
                previousPO.setExpiredDate(givenEffDate);
            }
        }
    }

    public void addAll(Collection<E> values) {
        values.forEach(this::add);
    }

    public LocalDate getChainExpiredDate() {
        return MAX_LOCAL_DATE;
    }

    public LocalDate getChainInitialEffectiveDate() {
        if (valueList.isEmpty()) {
            return MAX_LOCAL_DATE;
        } else {
            return valueList.get(0).getInitialEffectiveDate();
        }
    }

    public List<E> getValueList() {
        return new ArrayList<>(valueList);
    }

    public int size() {
        return valueList.size();
    }

    public E getHistoryVersionOf(LocalDate effectiveDate) {
        E result = null;
        for (E value : valueList) {
            if (value.getEffectiveDate().isBefore(effectiveDate)) {
                result = value;
                break;
            }
        }
        return result;
    }

    public List<E> getHistoryVersionOfAndAfter(LocalDate effectiveDate) {
        int ind = 0, maxInd = valueList.size();
        for (; ind < maxInd; ind++) {
            E value = valueList.get(ind);
            if (value.getEffectiveDate().isBefore(effectiveDate)) {
                break;
            }
        }
        List<E> result = new ArrayList<>(maxInd - ind);
        for (; ind < maxInd; ind++) {
            result.add(valueList.get(ind));
        }
        return result;
    }
}
