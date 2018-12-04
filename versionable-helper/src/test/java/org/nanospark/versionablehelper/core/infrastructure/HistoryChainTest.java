package org.nanospark.versionablehelper.core.infrastructure;

import org.junit.Before;
import org.junit.Test;
import org.nanospark.versionablehelper.core.infrastructure.po.VersionablePO;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nanospark.versionablehelper.core.infrastructure.VersionablePOTestUtils.isMatchAllDate;
import static org.nanospark.versionablehelper.core.infrastructure.VersionablePOTestUtils.setDatesToVersionablePO;
import static org.nanospark.versionablehelper.core.infrastructure.po.VersionablePO.MAX_LOCAL_DATE;

public class HistoryChainTest {

    private HistoryChain<TestEntry> SUT = new HistoryChain<>();
    private List<TestEntry> valueList;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        Field field = Whitebox.getField(HistoryChain.class, "valueList");
        valueList = (List<TestEntry>) Whitebox.getFieldValue(field, SUT);
    }


    @Test
    public void shouldReturnMAX_LOCAL_DATEWhenChainIsEmpty() {
        //when
        LocalDate result = SUT.getChainExpiredDate();

        //then
        assertThat(result).isEqualTo(MAX_LOCAL_DATE);
    }

    @Test
    public void shouldReturnMAX_LOCAL_DATEWhenGetInitialEffectiveDateButChainIsEmpty() {
        //when
        LocalDate result = SUT.getChainInitialEffectiveDate();

        //then
        assertThat(result).isEqualTo(MAX_LOCAL_DATE);
    }

    @Test
    public void shouldReturnFirstEffectiveExpiredDateWhenChainIsNotEmpty() {
        //given
        TestEntry entry = new TestEntry();
        LocalDate effectiveDate = LocalDate.of(2018, 1, 1),
                expiredDate = LocalDate.of(2019, 1, 1);
        setDatesToVersionablePO(entry, effectiveDate, effectiveDate, expiredDate);
        entry.setData(1);
        valueList.add(entry);

        //when
        LocalDate result = SUT.getChainInitialEffectiveDate();

        //then
        assertThat(result).isEqualTo(effectiveDate);
    }

    @Test
    public void canAddToHistoryChain() {
        //given
        TestEntry entry = new TestEntry();
        entry.setData(1);
        LocalDate effDate = LocalDate.of(2018, 12, 2);
        entry.setEffectiveDate(effDate);

        //when
        SUT.add(entry);

        //then
        assertThat(SUT.size()).isEqualTo(1);
        assertThat(isMatchAllDate(entry, effDate, effDate, MAX_LOCAL_DATE))
                .isTrue();
        assertThat(SUT.getChainInitialEffectiveDate()).isEqualTo(effDate);
        assertThat(SUT.getChainExpiredDate()).isEqualTo(MAX_LOCAL_DATE);
    }

    @Test
    public void canAddToFirstWhenGivenEffDateIsEarlierThanFirstEffDate() {
        //given
        TestEntry eailierEntry = new TestEntry(), laterEntry = new TestEntry();
        eailierEntry.setData(1);
        laterEntry.setData(2);
        LocalDate earlierEffDate = LocalDate.of(2018, 12, 2),
                laterEffDate = LocalDate.of(2019, 1, 1);
        setDatesToVersionablePO(eailierEntry, earlierEffDate, earlierEffDate, laterEffDate);
        setDatesToVersionablePO(laterEntry, earlierEffDate, laterEffDate, MAX_LOCAL_DATE);
        valueList.addAll(Arrays.asList(eailierEntry, laterEntry));

        TestEntry entry = new TestEntry();
        entry.setData(3);
        LocalDate moreEarlierEffDate = LocalDate.of(2018, 1, 1);
        entry.setEffectiveDate(moreEarlierEffDate);

        //when
        SUT.add(entry);

        //then
        assertThat(SUT.getChainInitialEffectiveDate()).isEqualTo(moreEarlierEffDate);
        assertThat(SUT.getChainExpiredDate()).isEqualTo(MAX_LOCAL_DATE);
        validateValueList(entry);
    }

    @Test
    public void canAddToSecondWhenGivenEffDateIsBetweenTheFirstAndSecondEffDate() {
        //given
        TestEntry eailierEntry = new TestEntry(), laterEntry = new TestEntry();
        eailierEntry.setData(1);
        laterEntry.setData(2);
        LocalDate earlierEffDate = LocalDate.of(2018, 1, 1),
                laterEffDate = LocalDate.of(2019, 1, 1);
        setDatesToVersionablePO(eailierEntry, earlierEffDate, earlierEffDate, laterEffDate);
        setDatesToVersionablePO(laterEntry, earlierEffDate, laterEffDate, MAX_LOCAL_DATE);
        valueList.addAll(Arrays.asList(eailierEntry, laterEntry));

        TestEntry entry = new TestEntry();
        entry.setData(3);
        LocalDate middleEffDate = LocalDate.of(2018, 12, 1);
        entry.setEffectiveDate(middleEffDate);

        //when
        SUT.add(entry);

        //then
        assertThat(SUT.getChainInitialEffectiveDate()).isEqualTo(earlierEffDate);
        assertThat(SUT.getChainExpiredDate()).isEqualTo(MAX_LOCAL_DATE);
        validateValueList(entry);
    }

    @Test
    public void canAddToLastWhenGivenEffDateIsLaterThanLastEffDate() {
        //given
        TestEntry eailierEntry = new TestEntry(), laterEntry = new TestEntry();
        eailierEntry.setData(1);
        laterEntry.setData(2);
        LocalDate earlierEffDate = LocalDate.of(2018, 1, 1),
                laterEffDate = LocalDate.of(2019, 1, 1);
        setDatesToVersionablePO(eailierEntry, earlierEffDate, earlierEffDate, laterEffDate);
        setDatesToVersionablePO(laterEntry, earlierEffDate, laterEffDate, MAX_LOCAL_DATE);
        valueList.addAll(Arrays.asList(eailierEntry, laterEntry));

        TestEntry entry = new TestEntry();
        entry.setData(3);
        LocalDate moreLaterEffDate = LocalDate.of(2020, 11, 1);
        entry.setEffectiveDate(moreLaterEffDate);

        //when
        SUT.add(entry);

        //then
        assertThat(SUT.getChainInitialEffectiveDate()).isEqualTo(earlierEffDate);
        assertThat(SUT.getChainExpiredDate()).isEqualTo(MAX_LOCAL_DATE);
        validateValueList(entry);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointExceptionWhenGivenEffectiveIsNull() {
        //given
        TestEntry entry = new TestEntry();

        //when
        SUT.add(entry);

    }

    @Test
    public void canGetAVersionOfGivenEffectiveDate() {
        //given
        TestEntry eailierEntry = new TestEntry(), laterEntry = new TestEntry();
        eailierEntry.setData(1);
        laterEntry.setData(2);
        LocalDate earlierEffDate = LocalDate.of(2018, 1, 1),
                laterEffDate = LocalDate.of(2019, 1, 1);
        setDatesToVersionablePO(eailierEntry, earlierEffDate, earlierEffDate, laterEffDate);
        setDatesToVersionablePO(laterEntry, earlierEffDate, laterEffDate, MAX_LOCAL_DATE);
        valueList.addAll(Arrays.asList(eailierEntry, laterEntry));
        LocalDate effectiveDate = LocalDate.of(2018, 2, 1);

        //when
        TestEntry result = SUT.getHistoryVersionOf(effectiveDate);

        //then
        assertThat(result).isEqualTo(eailierEntry);
    }

    @Test
    public void returnNullWhenNoVersionOfGivenEffectiveDate() {
        //given
        TestEntry eailierEntry = new TestEntry(), laterEntry = new TestEntry();
        eailierEntry.setData(1);
        laterEntry.setData(2);
        LocalDate earlierEffDate = LocalDate.of(2018, 1, 1),
                laterEffDate = LocalDate.of(2019, 1, 1);
        setDatesToVersionablePO(eailierEntry, earlierEffDate, earlierEffDate, laterEffDate);
        setDatesToVersionablePO(laterEntry, earlierEffDate, laterEffDate, MAX_LOCAL_DATE);
        valueList.addAll(Arrays.asList(eailierEntry, laterEntry));
        LocalDate effectiveDate = LocalDate.of(2017, 1, 1);

        //when
        TestEntry result = SUT.getHistoryVersionOf(effectiveDate);

        //then
        assertThat(result).isNull();
    }

    private void validateValueList(TestEntry entry) {
        assertThat(valueList).allMatch(po -> po.getInitialEffectiveDate().isEqual(SUT.getChainInitialEffectiveDate()));
        assertThat(valueList).contains(entry);
        assertThat(isHistoryChain(valueList)).isTrue();
    }

    private boolean isHistoryChain(List<? extends VersionablePO> list) {
        if (list.isEmpty() || list.size() == 1) {
            return true;
        }
        Iterator<? extends VersionablePO> iterator = list.iterator();
        VersionablePO previousPO = iterator.next();
        while (iterator.hasNext()) {
            VersionablePO versionablePO = iterator.next();
            if (!previousPO.getExpiredDate().isEqual(versionablePO.getEffectiveDate()))
                return false;
            previousPO = versionablePO;
        }
        return true;
    }

    class TestEntry implements VersionablePO {

        private Integer data;

        private LocalDate initialEffectiveDate;

        private LocalDate effectiveDate;

        private LocalDate expiredDate;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TestEntry)) return false;
            TestEntry entry = (TestEntry) o;
            return Objects.equals(data, entry.data) &&
                    Objects.equals(initialEffectiveDate, entry.initialEffectiveDate) &&
                    Objects.equals(effectiveDate, entry.effectiveDate) &&
                    Objects.equals(expiredDate, entry.expiredDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data, initialEffectiveDate, effectiveDate, expiredDate);
        }

        @Override
        public String toString() {
            return "TestEntry{" +
                    "data=" + data +
                    ", initialEffectiveDate=" + initialEffectiveDate +
                    ", effectiveDate=" + effectiveDate +
                    ", expiredDate=" + expiredDate +
                    '}';
        }

        public Integer getData() {
            return data;
        }

        public void setData(Integer data) {
            this.data = data;
        }

        @Override
        public LocalDate getInitialEffectiveDate() {
            return initialEffectiveDate;
        }

        @Override
        public void setInitialEffectiveDate(LocalDate initialEffectiveDate) {
            this.initialEffectiveDate = initialEffectiveDate;
        }

        @Override
        public LocalDate getEffectiveDate() {
            return effectiveDate;
        }

        @Override
        public void setEffectiveDate(LocalDate effectiveDate) {
            this.effectiveDate = effectiveDate;
        }

        @Override
        public LocalDate getExpiredDate() {
            return expiredDate;
        }

        @Override
        public void setExpiredDate(LocalDate expiredDate) {
            this.expiredDate = expiredDate;
        }
    }
}

