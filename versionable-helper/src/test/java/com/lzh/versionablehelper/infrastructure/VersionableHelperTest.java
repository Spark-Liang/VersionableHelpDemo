package com.lzh.versionablehelper.infrastructure;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.lzh.versionablehelper.VersionableHelperApplicationTests;
import com.lzh.versionablehelper.exception.VersionableException;
import com.lzh.versionablehelper.infrastructure.dto.ParameterForm;
import com.lzh.versionablehelper.infrastructure.po.DemoHistoryPO;
import com.lzh.versionablehelper.infrastructure.po.DemoMasterPO;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.lzh.versionablehelper.exception.VersionableException.MessageConstants.SHOULD_PROVIDE_EFFECTIVE_DATE_OR_MASTER_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class VersionableHelperTest extends VersionableHelperApplicationTests {

    private static final String DATA_PATH = DATA_PATH_PREFIX + "com/lzh/versionablehelper/infrastructure/";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    @Qualifier("demoVersionableHelperForTestAbstractVersionableHelper")
    private AbstractVersionableHelper<DemoMasterPO,DemoHistoryPO> versionableHelper;



    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test {@link AbstractVersionableHelper#getVersionablePOByParamtertForm(ParameterForm)}
     */
    @Test
    @DatabaseSetup(value = DATA_PATH + "TestGetVersionablePOByParameterForm.xls"
            ,type = DatabaseOperation.CLEAN_INSERT)
    public void testGetVersionablePOByEffective() {
        //given
        ParameterForm params = new ParameterForm();
        LocalDate effectiveDate = LocalDate.of(2018,11,18);
        params.setEffectiveDate(effectiveDate);

        //when
        List<DemoHistoryPO> demoHistoryPOs = versionableHelper.getVersionablePOByParamtertForm(params);

        //give
        assertThat(demoHistoryPOs.size()).isEqualTo(1);
        DemoHistoryPO demoHistoryPO = demoHistoryPOs.get(0);
        assertThat(demoHistoryPO.getId()).isEqualTo(3);
        assertThat(demoHistoryPO.isEffectiveRecord()).isTrue();
        assertThat(demoHistoryPO.getEffectiveDate()).isEqualTo(LocalDate.of(2018,11,1));
    }

    /**
     * Test {@link AbstractVersionableHelper#getVersionablePOByParamtertForm(ParameterForm)}
     */
    @Test
    @DatabaseSetup(value = DATA_PATH + "TestGetVersionablePOByParameterForm.xls"
            ,type = DatabaseOperation.CLEAN_INSERT)
    public void testGetVersionablePOByMasterId() {
        //given
        ParameterForm params = new ParameterForm();
        Long masterId = 1L;
        params.setMasterId(masterId);

        //when
        List<DemoHistoryPO> demoHistoryPOs = versionableHelper.getVersionablePOByParamtertForm(params);

        //give
        assertThat(demoHistoryPOs.size()).isEqualTo(3);
        assertThat(demoHistoryPOs.get(0).getId()).isEqualTo(1);
        assertThat(demoHistoryPOs.get(1).getId()).isEqualTo(3);
        assertThat(demoHistoryPOs.get(2).getId()).isEqualTo(4);
    }

    @Test
    public void throwExceptionWhenNotProviceEffectiveDateOrMasterId(){
        //given
        expectedException.expect(VersionableException.class);
        expectedException.expectMessage(SHOULD_PROVIDE_EFFECTIVE_DATE_OR_MASTER_ID);
        ParameterForm params = new ParameterForm();

        //when
        versionableHelper.getVersionablePOByParamtertForm(params);
    }

    @Test
    @DatabaseSetup(value = DATA_PATH + "TestGetVersionablePOByParameterForm.xls"
            ,type = DatabaseOperation.CLEAN_INSERT)
    public void canFindAllHistoryPOBetweenEffectiveDateAndExpiredDate(){
        //given
        ParameterForm params = new ParameterForm();
        LocalDate effectiveDate = LocalDate.of(2018,1,1),
                expiredDate = LocalDate.of(2019,12,31);
        params.setEffectiveDate(effectiveDate);
        params.setExpiredDate(expiredDate);

        //when
        List<DemoHistoryPO> demoHistoryPOs = versionableHelper.getVersionablePOByParamtertForm(params);

        //give
        assertThat(demoHistoryPOs.size()).isEqualTo(2);
        assertThat(demoHistoryPOs.get(0).getId()).isEqualTo(1);
        assertThat(demoHistoryPOs.get(1).getId()).isEqualTo(3);
    }

    @Test
    public void getHistoryPOsByMasterPO() {
    }

    @Test
    public void persist() {
    }

    @Test
    public void persistAll() {
    }

    @Test
    public void persist1() {
    }

    @Test
    public void persistAll1() {
    }
}

@Repository("demoVersionableHelperForTestAbstractVersionableHelper")
 class DemoVersionableHelperForTestAbstractVersionableHelper extends AbstractVersionableHelper<DemoMasterPO,DemoHistoryPO>{
    @Override
    public Class<DemoMasterPO> getMasterPOClass() {
        return DemoMasterPO.class;
    }

    @Override
    public Class<DemoHistoryPO> getHistoryPOClass() {
        return DemoHistoryPO.class;
    }
}