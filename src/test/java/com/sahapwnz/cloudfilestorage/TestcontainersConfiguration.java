package com.sahapwnz.cloudfilestorage;

//@TestConfiguration(proxyBeanMethods = false)
//class TestcontainersConfiguration {
//
//    @Bean(initMethod = "start", destroyMethod = "stop")
//    @ServiceConnection
//    PostgreSQLContainer<?> postgresContainer() {
//        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
//    }
//
//    @Bean
//    public DataSource dataSource(PostgreSQLContainer<?> postgresContainer) {
//        var hicariDataSource = new HikariDataSource();
//        hicariDataSource.setJdbcUrl(postgresContainer.getJdbcUrl());
//        hicariDataSource.setUsername(postgresContainer.getUsername());
//        hicariDataSource.setPassword(postgresContainer.getPassword());
//        return hicariDataSource;
//    }

//    @Bean
//    @ServiceConnection(name = "redis")
//    GenericContainer<?> redisContainer() {
//        return new GenericContainer<>(DockerImageName.parse("redis:latest")).withExposedPorts(6379);
//    }

//}
