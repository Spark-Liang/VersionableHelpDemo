package org.nanospark.versionablehelper.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nanospark.versionablehelper.VersionableHelperApplication;
import org.nanospark.versionablehelper.core.infrastructure.repository.DemoHistoryPORepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = VersionableHelperApplication.class)
@ComponentScan(basePackages = {"org.nanospark"})
@DataJpaTest
@PrepareForTest
public class JPABaseTest {

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    private DemoHistoryPORepository demoHistoryPORepository;

    @Test
    public void loadContext() {
        assertThat(entityManager).isNotNull();
    }

}
