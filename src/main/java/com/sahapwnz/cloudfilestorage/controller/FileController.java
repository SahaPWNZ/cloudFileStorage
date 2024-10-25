package com.sahapwnz.cloudfilestorage.controller;

import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

@Controller
public class FileController {
    MinioClient minioClient;

    @Autowired
    public FileController(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    private static final String UPLOAD_DIR = "src/main/resources/uploads/";

    @PostMapping("/load")
    String loadFile(@RequestParam("myFile") MultipartFile file, Principal principal) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        String login = principal.getName();


//            Path path = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
//            Files.createDirectories(path.getParent());
//            Files.write(path, file.getBytes());
        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket("user-files").build())) {
            System.out.println("Бакет юзер-файлс есть");
        }
        minioClient.listObjects(ListObjectsArgs.builder().bucket(login + "-files").build()).forEach(item -> {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(login + "-files")
                        .object(item.get().objectName())
                        .build());
                System.out.println("Deleted object: " + item.get().objectName());
            } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }

        });
        minioClient.removeBucket(RemoveBucketArgs.builder()
                .bucket(login + "-files")
                .build());

        minioClient.makeBucket(MakeBucketArgs.builder().bucket(login + "-files").build());
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(login + "-files")
                .object(file.getOriginalFilename())
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());

        InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(login + "-files")
                        .object(file.getOriginalFilename())
                        .build()
        );
        Path uploadsDir = Path.of("src/main/resources/uploads/");
        Path targetPath = uploadsDir.resolve(file.getOriginalFilename());
        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "home";
    }
}
