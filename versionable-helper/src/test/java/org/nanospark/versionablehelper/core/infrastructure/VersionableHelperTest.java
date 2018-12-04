package org.nanospark.versionablehelper.core.infrastructure;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nanospark.versionablehelper.core.JPABaseTest;
import org.nanospark.versionablehelper.core.exception.SystemException;
import org.nanospark.versionablehelper.core.exception.VersionableException;
import org.nanospark.versionablehelper.core.infrastructure.VersionableHelper.ParameterForm;
import org.nanospark.versionablehelper.core.infrastructure.constants.Gender;
import org.nanospark.versionablehelper.core.infrastructure.po.DemoHistoryPO;
import org.nanospark.versionablehelper.core.infrastructure.po.DemoMasterPO;
import org.nanospark.versionablehelper.core.infrastructure.po.VersionableBasePO;
import org.nanospark.versionablehelper.core.infrastructure.repository.DemoHistoryPORepository;
import org.nanospark.versionablehelper.core.infrastructure.repository.DemoMasterPORepository;
import org.nanospark.versionablehelper.core.infrastructure.repository.DemoVersionableHelperForTestAbstractVersionableHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nanospark.versionablehelper.core.infrastructure.po.VersionablePO.MAX_LOCAL_DATE;

public class VersionableHelperTest extends JPABaseTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

//    @Autowired
//    @Qualifier("demoVersionableHelperForTestAbstractVersionableHelper")
//    private AbstractVersionableHelper<DemoMasterPO,Long,DemoHistoryPO,Long> SUT;

    @Autowired
    private DemoVersionableHelperForTestAbstractVersionableHelper SUT;

    @Autowired
    private DemoHistoryPORepository demoHistoryPORepository;

    @Autowired
    private DemoMasterPORepository demoMasterPORepository;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test {@link AbstractVersionableHelper#getHistoryPOByParametertForm(ParameterForm)}}
     */
    @Test
    public void testGetVersionablePOOnlyByEffective() {
        //given
        ParameterForm<Long> params = new ParameterForm<>();
        LocalDate effectiveDate = LocalDate.of(2018, 11, 18);
        params.setEffectiveDate(effectiveDate);

        //when
        List<DemoHistoryPO> historyPOs = SUT.getHistoryPOByParametertForm(params);

        //give
        isEffectivePOsOfTheEffectiveDate(historyPOs, effectiveDate);
    }

    /**
     * Test {@link AbstractVersionableHelper#getHistoryPOByParametertForm(ParameterForm)}
     */
    @Test
    public void testGetVersionablePOByMasterId() {
        //given
        ParameterForm<Long> params = new ParameterForm<>();
        Long masterId = 1L;
        params.setMasterId(masterId);

        //when
        List<DemoHistoryPO> demoHistoryPOs = SUT.getHistoryPOByParametertForm(params);

        //give
        assertThat(demoHistoryPOs.size()).isEqualTo(3);
        assertThat(demoHistoryPOs.get(0).getHistId()).isEqualTo(1);
        assertThat(demoHistoryPOs.get(1).getHistId()).isEqualTo(3);
        assertThat(demoHistoryPOs.get(2).getHistId()).isEqualTo(4);
    }

    /**
     * Test {@link AbstractVersionableHelper#getHistoryPOByParametertForm(ParameterForm)}
     */
    @Test
    public void throwExceptionWhenNotProviceEffectiveDateOrMasterId() {
        //given
        expectedException.expect(VersionableException.class);
        expectedException.expectMessage(VersionableException.MessageConstants.SHOULD_PROVIDE_EFFECTIVE_DATE_OR_MASTER_ID);
        ParameterForm<Long> params = new ParameterForm<>();

        //when
        SUT.getHistoryPOByParametertForm(params);
    }

    @Test
    public void canUsePropertyPathToDoFilter() {
        //given
        String propertyPath = "name";
        String propertyValue = "test1_3";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(propertyPath, propertyValue);
        LocalDate effectiveDate = LocalDate.of(2018, 11, 30);
        ParameterForm<Long> param = new ParameterForm<>();
        param.setEffectiveDate(effectiveDate);
        param.setParams(paramMap);

        //when
        List<DemoHistoryPO> results = SUT.getHistoryPOByParametertForm(param);

        //then
        assertThat(results).allMatch(po -> {
            return po.getName().equals(propertyValue);
        });
        assertThat(results).allMatch(po -> {
            return VersionablePOTestUtils.isEffectivePOOfTheEffectiveDate(effectiveDate, po);
        });
    }

    @Test
    public void throwExceptionWhenTheGivenPropertyPathIsNotExists() {
        //given
        expectedException.expect(SystemException.class);
        expectedException.expectMessage(SystemException.MessageConstants.PROPERTY_IS_NOT_EXISTS);
        String propertyPath = "test";
        String propertyValue = "test";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(propertyPath, propertyValue);
        LocalDate effectiveDate = LocalDate.of(2018, 11, 30);
        ParameterForm<Long> param = new ParameterForm<>();
        param.setEffectiveDate(effectiveDate);
        param.setParams(paramMap);

        //when
        SUT.getHistoryPOByParametertForm(param);
    }

    /**
     * Test {@link AbstractVersionableHelper#getVersionByMasterIdAndEffective(Object, LocalDate)}
     */
    @Test
    public void canGetVersionByMasterIdAndEffectiveDate() {
        //given
        LocalDate effectiveDate = LocalDate.of(2018, 11, 18);
        Long masterId = 1L;

        //when
        DemoMasterPO masterPO = SUT.getVersionByMasterIdAndEffective(masterId, effectiveDate);

        //then
        assertThat(masterPO).isNotNull();
        assertThat(masterPO.getEffectiveDate()).isBefore(effectiveDate);
        assertThat(masterPO.getExpiredDate()).isAfter(effectiveDate);
    }

    /**
     * Test {@link AbstractVersionableHelper#getVersionByMasterIdAndEffective(Object, LocalDate)}
     */
    @Test
    public void throwExceptionWhenFindMoreThanOneEffectiveRecordInOneEffectiveDate() {
        //given
        expectedException.expect(SystemException.class);
        expectedException.expectMessage(SystemException.MessageConstants.DUPLICATED_EFFECTIVE_RECORD);
        String name = "Test_name";
        Gender gender = Gender.MAN;
        Integer age = 18;
        LocalDate effectiveDate = LocalDate.of(2018, 11, 18),
                expiredDate = LocalDate.of(2018, 12, 1),
                effectiveDateParam = LocalDate.of(2018, 11, 30);
        Long masterId = Long.MAX_VALUE;
        DemoHistoryPO historyPO1 = getNormalHistoryPO(name, gender, age),
                historyPO2 = getNormalHistoryPO(name, gender, age);
        historyPO1.setMasterId(masterId);
        historyPO1.setInitialEffectiveDate(effectiveDate);
        historyPO1.setEffectiveDate(effectiveDate);
        historyPO1.setExpiredDate(expiredDate);
        historyPO2.setMasterId(masterId);
        historyPO2.setInitialEffectiveDate(effectiveDate);
        historyPO2.setEffectiveDate(effectiveDate);
        historyPO2.setExpiredDate(expiredDate);
        entityManager.persist(historyPO1);
        entityManager.persist(historyPO2);

        //when
        SUT.getVersionByMasterIdAndEffective(masterId, effectiveDateParam);

        //then
    }

    /**
     * Test {@link AbstractVersionableHelper#getVersionByMasterIdAndEffective(Object, LocalDate)}
     */
    @Test
    public void returnNullWhenNotExistsCorrespondingRecord() {
        //given
        Long masterId = Long.MAX_VALUE;
        LocalDate effectiveDateParam = LocalDate.of(2018, 11, 30);

        //when
        DemoMasterPO result = SUT.getVersionByMasterIdAndEffective(masterId, effectiveDateParam);

        //then
        assertThat(result).isNull();
    }


    @Test
    public void persistAll() {
    }

    /**
     * Test {@link AbstractVersionableHelper#persist(VersionableBasePO)}
     */
    @Test
    public void shouldAddARecordToMasterAndHistoryWhenNoRecordInHistoryPO() {
        //given
        LocalDate effectiveDate = LocalDate.of(2018, 11, 29);
        DemoMasterPO masterPO = getNormalMasterPO("Test", Gender.MAN, 18);
        masterPO.setEffectiveDate(effectiveDate);

        //when
        SUT.persist(masterPO);

        //then
        Long masterId = masterPO.getId();
        assertThat(masterId).isNotNull();
        assertThat(entityManager.find(DemoMasterPO.class, masterId)).isNotNull();
        Assertions.assertThat(VersionablePOTestUtils.isMatchAllDate(masterPO, effectiveDate, effectiveDate, MAX_LOCAL_DATE))
                .isTrue();
        List<DemoHistoryPO> historyPOs = findByMasterId(masterId);
        assertThat(historyPOs).hasSize(1);
        DemoHistoryPO historyPO = historyPOs.get(0);
        assertThat(isHistoryPOOfMasterPO(historyPO, masterPO)).isTrue();
        Assertions.assertThat(VersionablePOTestUtils.isMatchAllDate(historyPO, effectiveDate, effectiveDate, MAX_LOCAL_DATE))
                .isTrue();
    }

    @Test
    public void shouldUpdateTheInitialEffectiveDateWhenEffectiveDateOfVersionIsEarlierThanInitialEffective() {
        //given
        LocalDate earlierEffDate = LocalDate.of(2018, 11, 1),
                laterEffDate = LocalDate.of(2018, 11, 29);
        List<DemoHistoryPO> historyPOsInDB = getHistoriesByEffDates(earlierEffDate, laterEffDate);
        DemoHistoryPO demoHistoryPOInDB = historyPOsInDB.get(0);
        DemoMasterPO masterPOInDB = new DemoMasterPO();
        copyFromHistoryToMaster(demoHistoryPOInDB, masterPOInDB);
        entityManager.persist(masterPOInDB);
        Long masterId = masterPOInDB.getId();
        historyPOsInDB.forEach(po -> po.setMasterId(masterId));
        historyPOsInDB.forEach(entityManager::persist);

        LocalDate moreEarlierEffDate = LocalDate.of(2018, 1, 1);
        DemoMasterPO versionPO = getNormalMasterPO("Test_Name", Gender.MAN, 18);
        versionPO.setId(masterId);
        versionPO.setEffectiveDate(moreEarlierEffDate);

        //when
        SUT.persist(versionPO);

        //then
        List<DemoHistoryPO> historyPOs = findByMasterId(masterId);
        DemoHistoryPO historyPO1 = historyPOs.get(0),
                historyPO2 = historyPOs.get(1),
                historyPO3 = historyPOs.get(2);
        Assertions.assertThat(VersionablePOTestUtils.isMatchAllDate(historyPO1, moreEarlierEffDate, moreEarlierEffDate, earlierEffDate))
                .isTrue();
        Assertions.assertThat(VersionablePOTestUtils.isMatchAllDate(historyPO2, moreEarlierEffDate, earlierEffDate, laterEffDate))
                .isTrue();
        Assertions.assertThat(VersionablePOTestUtils.isMatchAllDate(historyPO3, moreEarlierEffDate, laterEffDate, MAX_LOCAL_DATE))
                .isTrue();
    }

    @Test
    public void shouldChangeFirstExpiredDateWhenEffectiveDateOfVersionIsBetweenTheFirstEffDateAndSecondDate() {
        //given
        LocalDate earlierEffDate = LocalDate.of(2018, 11, 1),
                laterEffDate = LocalDate.of(2018, 11, 29);
        List<DemoHistoryPO> historyPOsInDB = getHistoriesByEffDates(earlierEffDate, laterEffDate);
        DemoHistoryPO demoHistoryPOInDB = historyPOsInDB.get(0);
        DemoMasterPO masterPOInDB = new DemoMasterPO();
        copyFromHistoryToMaster(demoHistoryPOInDB, masterPOInDB);
        entityManager.persist(masterPOInDB);
        Long masterId = masterPOInDB.getId();
        historyPOsInDB.forEach(po -> po.setMasterId(masterId));
        historyPOsInDB.forEach(entityManager::persist);

        LocalDate effectiveDate = LocalDate.of(2018, 11, 2);
        DemoMasterPO versionPO = getNormalMasterPO("Test_Name", Gender.MAN, 18);
        versionPO.setId(masterId);
        versionPO.setEffectiveDate(effectiveDate);

        //when
        SUT.persist(versionPO);

        //then
        List<DemoHistoryPO> historyPOs = findByMasterId(masterId);
        DemoHistoryPO historyPO1 = historyPOs.get(0),
                historyPO2 = historyPOs.get(1),
                historyPO3 = historyPOs.get(2);
        Assertions.assertThat(VersionablePOTestUtils.isMatchAllDate(historyPO1, earlierEffDate, earlierEffDate, effectiveDate))
                .isTrue();
        Assertions.assertThat(VersionablePOTestUtils.isMatchAllDate(historyPO2, earlierEffDate, effectiveDate, laterEffDate))
                .isTrue();
        Assertions.assertThat(VersionablePOTestUtils.isMatchAllDate(historyPO3, earlierEffDate, laterEffDate, MAX_LOCAL_DATE))
                .isTrue();
    }

    @Test
    public void shouldChangeSecondExpiredDateWhenEffectiveDateOfVersionIsLaterThanSecondEffDate() {
        //given
        LocalDate earlierEffDate = LocalDate.of(2018, 11, 1),
                laterEffDate = LocalDate.of(2018, 11, 29);
        List<DemoHistoryPO> historyPOsInDB = getHistoriesByEffDates(earlierEffDate, laterEffDate);
        DemoHistoryPO demoHistoryPOInDB = historyPOsInDB.get(0);
        DemoMasterPO masterPOInDB = new DemoMasterPO();
        copyFromHistoryToMaster(demoHistoryPOInDB, masterPOInDB);
        entityManager.persist(masterPOInDB);
        Long masterId = masterPOInDB.getId();
        historyPOsInDB.forEach(po -> po.setMasterId(masterId));
        historyPOsInDB.forEach(entityManager::persist);

        LocalDate effectiveDate = LocalDate.of(2019, 1, 1);
        DemoMasterPO versionPO = getNormalMasterPO("Test_Name", Gender.MAN, 18);
        versionPO.setId(masterId);
        versionPO.setEffectiveDate(effectiveDate);

        //when
        SUT.persist(versionPO);

        //then
        List<DemoHistoryPO> historyPOs = findByMasterId(masterId);
        DemoHistoryPO historyPO1 = historyPOs.get(0),
                historyPO2 = historyPOs.get(1),
                historyPO3 = historyPOs.get(2);
        Assertions.assertThat(VersionablePOTestUtils.isMatchAllDate(historyPO1, earlierEffDate, earlierEffDate, laterEffDate))
                .isTrue();
        Assertions.assertThat(VersionablePOTestUtils.isMatchAllDate(historyPO2, earlierEffDate, laterEffDate, effectiveDate))
                .isTrue();
        Assertions.assertThat(VersionablePOTestUtils.isMatchAllDate(historyPO3, earlierEffDate, effectiveDate, MAX_LOCAL_DATE))
                .isTrue();
    }

    @Test
    public void shouldThrowExceptionWhenGivenEffDateIsNull() {
        //given
        expectedException.expect(SystemException.class);
        expectedException.expectMessage(SystemException.MessageConstants.EFFECTIVE_DATE_IS_NULL);
        DemoMasterPO versionPO = new DemoMasterPO();

        //when
        SUT.persist(versionPO);
    }

    @Test
    public void shouldUpdateMasterPOWhenGivenEffDateEarlierThenNow() {
        //given
        LocalDate now = LocalDate.now();
        LocalDate earlierEffDate = now.minusDays(10),
                laterEffDate = now.plusDays(10);
        List<DemoHistoryPO> historyPOsInDB = getHistoriesByEffDates(earlierEffDate, laterEffDate);
        DemoHistoryPO demoHistoryPOInDB = historyPOsInDB.get(0);
        DemoMasterPO masterPOInDB = new DemoMasterPO();
        copyFromHistoryToMaster(demoHistoryPOInDB, masterPOInDB);
        entityManager.persist(masterPOInDB);
        Long masterId = masterPOInDB.getId();
        historyPOsInDB.forEach(po -> po.setMasterId(masterId));
        historyPOsInDB.forEach(entityManager::persist);

        LocalDate effectiveDate = now.minusDays(1);
        DemoMasterPO versionPO = getNormalMasterPO("Test_Name", Gender.MAN, 18);
        versionPO.setId(masterId);
        versionPO.setEffectiveDate(effectiveDate);

        //when
        SUT.persist(versionPO);

        //then
        List<DemoHistoryPO> historyPOs = findByMasterId(masterId);
        masterPOInDB = entityManager.find(DemoMasterPO.class, masterId);
        assertThat(versionPO).isEqualToIgnoringGivenFields(masterPOInDB, "version");
    }

    /**
     * Test {@link VersionableHelper#persist(VersionableBasePO, boolean)}
     */
    @Test
    public void canDoCrossDayChange() {
        //given
        LocalDate earlierEffDate = LocalDate.of(2018, 11, 1),
                laterEffDate = LocalDate.of(2019, 1, 1);
        List<DemoHistoryPO> historyPOsInDB = getHistoriesByEffDates(earlierEffDate, laterEffDate);
        DemoHistoryPO demoHistoryPOInDB = historyPOsInDB.get(0);
        DemoMasterPO masterPOInDB = new DemoMasterPO();
        copyFromHistoryToMaster(demoHistoryPOInDB, masterPOInDB);
        entityManager.persist(masterPOInDB);
        Long masterId = masterPOInDB.getId();
        historyPOsInDB.forEach(po -> po.setMasterId(masterId));
        historyPOsInDB.forEach(entityManager::persist);

        LocalDate effectiveDate = LocalDate.of(2018, 12, 1);
        String name = "Test_Name";
        Integer age = 20;
        DemoMasterPO versionPO = getNormalMasterPO(name, Gender.MAN, age);
        versionPO.setId(masterId);
        versionPO.setEffectiveDate(effectiveDate);
        //when
        SUT.persist(versionPO, true);

        //then
        List<DemoHistoryPO> historyPOs = findByMasterId(masterId);
        assertThat(historyPOs).allMatch(historyPO -> name.equals(historyPO.getName()));
        DemoHistoryPO historyPOJustUpdated = historyPOs.get(1),
                historyPOAlreadyExists = historyPOs.get(2);
        assertThat(historyPOJustUpdated.getAge()).isEqualTo(age);
        assertThat(historyPOAlreadyExists.getAge()).isNotEqualTo(age);
    }

    @Test
    public void persistAll1() {
    }

    private void copyFromHistoryToMaster(DemoHistoryPO historyPO, DemoMasterPO masterPO) {
        masterPO.setName(historyPO.getName());
        masterPO.setGender(historyPO.getGender());
        masterPO.setAge(historyPO.getAge());
        masterPO.setInitialEffectiveDate(historyPO.getInitialEffectiveDate());
        masterPO.setEffectiveDate(historyPO.getEffectiveDate());
        masterPO.setExpiredDate(historyPO.getExpiredDate());
    }

    private List<DemoHistoryPO> getHistoriesByEffDates(LocalDate... effDates) {
        Arrays.sort(effDates);
        int num = 1;
        List<DemoHistoryPO> result = new ArrayList<>(effDates.length);
        for (int ind = 0, maxInd = effDates.length; ind < maxInd; ind++) {
            LocalDate effDate = effDates[ind],
                    expiredDate = ind + 1 < maxInd ? effDates[ind + 1] : MAX_LOCAL_DATE;
            DemoHistoryPO demoHistoryPO = getNormalHistoryPO("ForTest_" + num, Gender.MAN, 18);
            VersionablePOTestUtils.setDatesToVersionablePO(demoHistoryPO, effDates[0], effDate, expiredDate);
            result.add(demoHistoryPO);
        }
        return result;
    }

    private List<DemoHistoryPO> findByMasterId(Long masterId) {
        List<DemoHistoryPO> historyPOs = demoHistoryPORepository.findByMasterId(masterId);
        return historyPOs.stream()
                .filter(po -> {
                    return po.getEffectiveDate().isBefore(po.getExpiredDate());
                })
                .sorted(Comparator.comparing(DemoHistoryPO::getEffectiveDate))
                .collect(Collectors.toList());

    }

    private boolean isHistoryPOOfMasterPO(DemoHistoryPO historyPO, DemoMasterPO masterPO) {
        return Objects.equals(historyPO.getName(), masterPO.getName())
                && Objects.equals(historyPO.getGender(), masterPO.getGender())
                && Objects.equals(historyPO.getAge(), masterPO.getAge());
    }

    @SuppressWarnings("unchecked")
    private void isEffectivePOsOfTheEffectiveDate(List<? extends VersionableBasePO> versionPOs, LocalDate effectiveDate) {
        assertThat(versionPOs).allMatch(po -> {
            return VersionablePOTestUtils.isEffectivePOOfTheEffectiveDate(effectiveDate, po);
        });
    }


    private DemoHistoryPO getNormalHistoryPO(String name, Gender gender, Integer age) {
        DemoHistoryPO historyPO = new DemoHistoryPO();
        historyPO.setName(name);
        historyPO.setGender(gender);
        historyPO.setAge(age);
        return historyPO;
    }

    private DemoMasterPO getNormalMasterPO(String name, Gender gender, Integer age) {
        DemoMasterPO masterPO = new DemoMasterPO();
        masterPO.setName(name);
        masterPO.setGender(gender);
        masterPO.setAge(age);
        return masterPO;
    }

}

