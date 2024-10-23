package com.sahapwnz.cloudfilestorage;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }

    @Bean
    public DataSource dataSource(PostgreSQLContainer<?> postgresContainer) {
        var hicariDataSource = new HikariDataSource();
        hicariDataSource.setJdbcUrl(postgresContainer.getJdbcUrl());
        hicariDataSource.setUsername(postgresContainer.getUsername());
        hicariDataSource.setPassword(postgresContainer.getPassword());
        return hicariDataSource;
    }

//    @Bean
//    @ServiceConnection(name = "redis")
//    GenericContainer<?> redisContainer() {
//        return new GenericContainer<>(DockerImageName.parse("redis:latest")).withExposedPorts(6379);
//    }

}
