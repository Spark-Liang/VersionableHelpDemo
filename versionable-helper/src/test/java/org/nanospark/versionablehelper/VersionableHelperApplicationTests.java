package org.nanospark.versionablehelper;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanospark.test.common.util.XlsDataSetLoader;
import org.nanospark.versionablehelper.core.infrastructure.po.DemoHistoryPO;
import org.nanospark.versionablehelper.core.infrastructure.po.DemoMasterPO;
import org.nanospark.versionablehelper.core.infrastructure.repository.DemoHistoryPORepository;
import org.nanospark.versionablehelper.core.infrastructure.repository.DemoMasterPORepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DbUnitConfiguration(
        databaseConnection = {"druidDataSource"}
        ,dataSetLoader = XlsDataSetLoader.class)
public class VersionableHelperApplicationTests {
    Logger logger = LoggerFactory.getLogger(VersionableHelperApplicationTests.class);

    protected static final String DATA_PATH_PREFIX = "classpath:/";

    private static final String DATA_PATH = "com/lzh/versionablehelper/";

    @Autowired
    private DemoMasterPORepository demoMasterPORepository;

    @Autowired
    private DemoHistoryPORepository demoHistoryPORepository;

    @Test
    @DatabaseSetup(value = DATA_PATH_PREFIX + DATA_PATH + "TestLoadData.xls"
            ,type = DatabaseOperation.CLEAN_INSERT)
    public void contextLoads() {
        logger.info("load context success");
        Long id = 1L;

        DemoMasterPO demoMasterPO = demoMasterPORepository.findById(id).get();
        DemoHistoryPO demoHistoryPO = demoHistoryPORepository.findById(id).get();

        assertThat(demoMasterPO).isNotNull();
        assertThat(demoHistoryPO).isNotNull();
    }

}
