package com.sahapwnz.cloudfilestorage.service;

import com.sahapwnz.cloudfilestorage.exception.ApplicationException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class FileService {
    MinioClient minioClient;

    @Autowired
    public FileService(MinioClient minioClient) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        this.minioClient = minioClient;
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket("user-files").build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("user-files").build());
        }
    }

    public ArrayList<String> getInfoForThisFolder(String prefix) {
        ArrayList<String> allPathForThisPrefix = new ArrayList<>();
        try {
            ListObjectsArgs lArgs = ListObjectsArgs.builder()
                    .bucket("user-files")
                    .prefix(prefix)
                    .recursive(true)
                    .build();

            Iterable<Result<Item>> resp = minioClient.listObjects(lArgs);
            for (Result<Item> res : resp) {
                Item i = res.get();
                System.out.println("::" + i.objectName());
                String path = i.objectName().substring(prefix.length() + 1);
//                System.out.println("----" + path + "----");
                if (!path.isEmpty()) {
                    if (path.endsWith("/") && path.split("/").length == 1
                            || !path.endsWith("/") && path.split("/").length == 1) {
                        allPathForThisPrefix.add(path);
                    }
                }
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR) {
            };
        }
        return allPathForThisPrefix;
    }

    public void deleteObject(String fullPath) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket("user-files")
                    .object(fullPath)
                    .build());
            System.out.println("Deleted object: " + fullPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void putObject(String prefix, MultipartFile file) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket("user-files")
                    .object(prefix + "/" + file.getOriginalFilename())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void createFolder(String folderName, String prefix) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("user-files")
                            .object(prefix + "/" + folderName + "/")
                            .stream(InputStream.nullInputStream(), 0, -1)
                            .build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void renameFile(String oldFileName, String newFileName, String prefix) {
        try {
            CopySource source = CopySource.builder()
                    .bucket("user-files")
                    .object(prefix + "/" + oldFileName)
                    .build();
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket("user-files")
                            .object(prefix + "/" + newFileName) // Новое имя файла
                            .source(source) // Путь к исходному файлу
                            .build()
            );

            deleteObject(prefix + "/" + oldFileName);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteFolder(String fullPathToFolder) {
        try {
            ListObjectsArgs lArgs = ListObjectsArgs.builder()
                    .bucket("user-files")
                    .prefix(fullPathToFolder)
                    .recursive(true)
                    .build();

            Iterable<Result<Item>> results = minioClient.listObjects(lArgs);
            for (Result<Item> result : results) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket("user-files")
                                .object(result.get().objectName())
                                .build()
                );
                System.out.println("Удален объект: " + result.get().objectName());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void putFolder(MultipartFile[] files, String prefix) {
        Set<String> setUniquePaths = new HashSet<>();
        Arrays.stream(files).forEach(file -> {
            int lastSlashIndex = file.getOriginalFilename().lastIndexOf('/');
            if (lastSlashIndex != -1) {
                setUniquePaths.add(file.getOriginalFilename().substring(0, lastSlashIndex)); // Включаем слэш +1,у нас без ласт слэша
            }
        });

        setUniquePaths.forEach(path -> createFolder(path, prefix));

        Arrays.stream(files).forEach(file -> {
            try {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket("user-files")
                        .object(prefix + "/" + file.getOriginalFilename())
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    public void createRootFolder(Long id) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("user-files")
                            .object("user-" + id + "-files/")
                            .stream(InputStream.nullInputStream(), 0, -1)
                            .build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}

