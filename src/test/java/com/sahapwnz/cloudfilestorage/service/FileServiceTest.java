package com.sahapwnz.cloudfilestorage.service;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class, initializers = ConfigDataApplicationContextInitializer.class)
@Testcontainers
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    private final Long testUserId = 1L;
    private final String testRootPath = "user-1-files";

    @Container
    static GenericContainer<?> minioContainer = new FixedHostPortGenericContainer<>("minio/minio:latest")
            .withFixedExposedPort(9000, 9000)
            .withFixedExposedPort(9001, 9001)
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
            .withCommand("server", "/data", "--console-address", ":9001");

    @Test
    @Order(1)
    void testCreateRootFolder() {
        assertDoesNotThrow(() -> {
            fileService.createRootFolder(testUserId);
        });
        log.info("RootFolder for test user was created!");
    }

    @Test
    @Order(2)
    void testCreateFolder() {
        fileService.createFolder("testFolder", testRootPath);
        Assertions.assertEquals(fileService.getAllPathInThisFolder(testRootPath).size(), 1);
        log.info("in rootFolder was created testFolder");
    }

    @Test
    @Order(3)
    void testPutObjectInFolder() {

        String fileName = "test.txt";
        String contentType = "text/plain";
        byte[] content = "Hello, World!".getBytes();
        MultipartFile file = new MockMultipartFile(fileName, fileName, contentType, content);

        fileService.putObject(testRootPath + "/testFolder", file);
        Assertions.assertEquals(fileService.getAllPathInThisFolder(testRootPath + "/testFolder").size(), 1);
        log.info("in rootFolder was created file");
    }

    @Test
    @Order(4)
    void testDeleteObject() {
        fileService.deleteFolder(testRootPath + "/testFolder/");
        Assertions.assertEquals(fileService.getAllPathInThisFolder(testRootPath).size(), 0);
        log.info("in rootFolder was deleted");
    }
}
