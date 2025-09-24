package br.com.thallysprojetos.ms_produtos.configs;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class FlywayConfiguration {

    @Bean
    @DependsOn("dataSource")
    public Flyway flyway(DataSourceProperties dataSourceProperties) {
        Flyway flyway = Flyway.configure().dataSource(dataSourceProperties.initializeDataSourceBuilder().build()).load();
        flyway.repair();
        flyway.migrate();
        return flyway;
    }

}