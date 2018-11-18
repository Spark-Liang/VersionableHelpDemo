package com.lzh.versionablehelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EntityScan("com.lzh.versionablehelper.infrastructure.po")
@EnableTransactionManagement
public class VersionableHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(VersionableHelperApplication.class, args);
    }
}