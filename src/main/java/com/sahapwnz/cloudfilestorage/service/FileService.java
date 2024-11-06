package com.sahapwnz.cloudfilestorage.service;

import com.sahapwnz.cloudfilestorage.dto.FileResponseDTO;
import com.sahapwnz.cloudfilestorage.exception.ApplicationException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

            for (Result<Item> res : minioClient.listObjects(lArgs)) {
                String relativePath = res.get().objectName();
                if (relativePath.substring(prefix.length()).startsWith("/")) {
                    String path = relativePath.substring(prefix.length() + 1);
                    if (!path.isEmpty() && path.split("/").length == 1) {
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

    private boolean isValidPath(String objectName, String prefix) {
        String relativePath = objectName.substring(prefix.length());
        return relativePath.startsWith("/") && relativePath.split("/").length == 1;
    }

    private String extractPath(String objectName, String prefix) {
        return objectName.substring(prefix.length() + 1);
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

    public void renameFolder(String oldFolderName, String newFolderName, String prefix) {
        try {
            ListObjectsArgs lArgs = ListObjectsArgs.builder()
                    .bucket("user-files")
                    .prefix(prefix + oldFolderName)
                    .recursive(true)
                    .build();
            Iterable<Result<Item>> filesInFolder = minioClient.listObjects(lArgs);
            for (Result<Item> pathToFile : filesInFolder) {
                String oldPathToFile = pathToFile.get().objectName();
                String newPathToFile = oldPathToFile.replace(oldFolderName, newFolderName);

                CopySource source = CopySource.builder()
                        .bucket("user-files")
                        .object(oldPathToFile)
                        .build();

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket("user-files")
                                .object(newPathToFile) // Новое имя файла
                                .source(source) // Путь к исходному файлу
                                .build());

                deleteObject(oldPathToFile);
            }
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
            int lastSlashIndex = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf('/');
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


    public InputStream downloadFile(String objectPath) {
        InputStream stream = null;
        try {
            stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("user-files")
                            .object(objectPath)
                            .build()
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return stream;
    }

    public void downloadFolder(String folderPath, ZipOutputStream zipOut) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket("user-files")
                            .prefix(folderPath)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                InputStream inputStream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket("user-files")
                                .object(item.objectName())
                                .build()
                );

                ZipEntry zipEntry = new ZipEntry(item.objectName().substring(folderPath.length()));
                zipOut.putNextEntry(zipEntry);
                IOUtils.copy(inputStream, zipOut);
                zipOut.closeEntry();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public ArrayList<FileResponseDTO> search(String query, String rootPath) {
        ArrayList<FileResponseDTO> list = new ArrayList<>();
        try {


            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket("user-files")
                            .prefix(rootPath)
                            .recursive(true)
                            .build());
            for (Result<Item> result : results) {
                if (result.get().objectName().contains(query)) {
                    String str = result.get().objectName();
                    String[] parts = str.split("/");
                    System.out.println(Arrays.toString(parts));
                    if (parts[parts.length - 1].contains(query)) {
                        list.add(FileResponseDTO.builder()
                                .name(parts[parts.length - 1])
                                .prefix(buildPath(parts))
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    private String buildPath(String[] parts) {
        if (parts.length <= 2) {
            return ""; // Если массив содержит только один элемент, возвращаем пустую строку
        }
        String[] subParts = Arrays.copyOfRange(parts, 1, parts.length - 1); // Копируем подмассив начиная со второго элемента
        return String.join("/", subParts); // Объединяем с "/" и добавляем перед ним "/"
    }
}

//for (Result<Item> res : minioClient.listObjects(lArgs)) {
//        Item item = res.get();
//        String relativePath = item.objectName().substring(prefix.length());
//
//        if (!relativePath.isEmpty() && relativePath.startsWith("/")) {
//        String path = relativePath.substring(1);
//        if (path.split("/").length == 1) {
//        System.out.println("---" + path + "---");
//        allPathForThisPrefix.add(path);
//        }
//        }
//        }