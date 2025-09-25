package br.com.thallysprojetos.ms_pagamentos.configs;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

//    @Bean
//    @DependsOn("dataSource")
//    public Flyway flyway(DataSourceProperties dataSourceProperties) {
//        Flyway flyway = Flyway.configure().dataSource(dataSourceProperties.initializeDataSourceBuilder().build()).load();
//        flyway.repair();
//        flyway.migrate();
//        return flyway;
//    }

}