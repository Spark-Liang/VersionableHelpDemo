package org.nanospark.versionablehelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EntityScan("org.nanospark.versionablehelper.core.infrastructure.po")
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class VersionableHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(VersionableHelperApplication.class, args);
    }
}
