package org.trips.service_framework.audit;

/**
 * @author anomitra on 26/08/24
 */

import com.zaxxer.hikari.HikariDataSource;
import org.javers.repository.sql.ConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JaversDatasourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.configuration")
    public HikariDataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean("javersDataSourceProperties")
    @ConfigurationProperties("javers.datasource")
    public DataSourceProperties javersDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Value("${javers.datasource.hikari.minimum-idle}")
    private String minIdleConns;

    @Value("${javers.datasource.hikari.maximum-pool-size}")
    private String maxPoolSize;

    @Bean(name = "JpaHibernateConnectionProvider")
    @Primary
    public ConnectionProvider jpaConnectionProvider(@Qualifier("javersDataSourceProperties") DataSourceProperties javersDataSourceProperties) {
        HikariDataSource dataSource = javersDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setMaximumPoolSize(Integer.parseInt(maxPoolSize));
        dataSource.setMinimumIdle(Integer.parseInt(minIdleConns));
        return new JaversConnectionProvider(dataSource);
    }
}