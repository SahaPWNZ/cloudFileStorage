package com.sahapwnz.cloudfilestorage.service;

import com.sahapwnz.cloudfilestorage.dto.FileResponseDTO;
import com.sahapwnz.cloudfilestorage.exception.ApplicationException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class FileService {
    private final String BUCKET_NAME;
    MinioClient minioClient;

    @Autowired
    public FileService(MinioClient minioClient, @Value("${application.bucket.name: default-bucket-name}") String bucketName) {
        this.BUCKET_NAME = bucketName;
        this.minioClient = minioClient;
        createBucketIfNotExists();
    }

    private void createBucketIfNotExists() throws ApplicationException {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
                log.info("Bucket '{}' created successfully.", BUCKET_NAME);
            } else {
                log.info("Bucket '{}' already exists.", BUCKET_NAME);
            }
        } catch (Exception e) {
            log.error("Error while checking or creating bucket '{}': {}", BUCKET_NAME, e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
    }

    public boolean isObjectExist(String folderPath) {
        try {
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(folderPath)
                    .build()) != null;
        } catch (ErrorResponseException e) {
            log.info("Object not found: {}", folderPath);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while checking object existence for {}: {}", folderPath, e.getMessage());
            throw new ApplicationException("Problem with Minio folder") {
            };
        }
    }


    public List<String> getAllPathInThisFolder(String prefix) {
        List<String> allPathForThisPrefix = new ArrayList<>();
        try {
            ListObjectsArgs lArgs = ListObjectsArgs.builder()
                    .bucket(BUCKET_NAME)
                    .prefix(prefix)
                    .recursive(true)
                    .build();

            for (Result<Item> res : minioClient.listObjects(lArgs)) {
                String relativePath = res.get().objectName();
                log.info("path: " + relativePath);
                if (relativePath.substring(prefix.length()).startsWith("/")) {
                    String path = relativePath.substring(prefix.length() + 1);
                    if (!path.isEmpty() && path.split("/").length == 1) {
                        allPathForThisPrefix.add(path);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
        return allPathForThisPrefix;
    }

    public void deleteObject(String fullPath) {
        if (!isObjectExist(fullPath)) {
            throw new ApplicationException("This object is already not in your storage");
        }
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(fullPath)
                    .build());
            log.info("Deleted object: " + fullPath);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
    }

    public void putObject(String prefix, MultipartFile file) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(prefix + "/" + file.getOriginalFilename())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
    }

    public void createFolder(String folderName, String prefix) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(prefix + "/" + folderName + "/")
                            .stream(InputStream.nullInputStream(), 0, -1)
                            .build());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
    }

    public void renameFile(String oldFileName, String newFileName, String prefix) {
        String oldFilePath = prefix + "/" + oldFileName;
        String newFilePath = prefix + "/" + newFileName;

        if (!isObjectExist(oldFilePath)) {
            throw new ApplicationException("This object is already not in your storage!");
        }

        try {

            CopySource source = CopySource.builder()
                    .bucket(BUCKET_NAME)
                    .object(oldFilePath)
                    .build();

            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(newFilePath)
                            .source(source)
                            .build()
            );

            deleteObject(oldFilePath);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
    }

    public void renameFolder(String oldFolderName, String newFolderName, String prefix) {

        if (!isObjectExist(oldFolderName)) {
            throw new ApplicationException("This object is already not in your storage!");
        }

        try {
            ListObjectsArgs lArgs = ListObjectsArgs.builder()
                    .bucket(BUCKET_NAME)
                    .prefix(prefix + oldFolderName) // Указываем, что ищем именно папку
                    .recursive(true)
                    .build();
            Iterable<Result<Item>> filesInFolder = minioClient.listObjects(lArgs);

            for (Result<Item> pathToFile : filesInFolder) {
                String oldPathToFile = pathToFile.get().objectName();

                // Формируем новый путь, заменяя только первый сегмент
                String newPathToFile = oldPathToFile.replaceFirst(
                        "^" + prefix + oldFolderName,
                        prefix + newFolderName
                );

                copyObject(oldPathToFile, newPathToFile);
                deleteObject(oldPathToFile);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
    }

    private void copyObject(String oldPathToFile, String newPathToFile) throws Exception {
        CopySource source = CopySource.builder()
                .bucket(BUCKET_NAME)
                .object(oldPathToFile)
                .build();

        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(newPathToFile)
                        .source(source)
                        .build());
    }

    public void deleteFolder(String fullPathToFolder) {

        if (!isObjectExist(fullPathToFolder)) {
            throw new ApplicationException("This object is already not in your storage!");
        }
        try {
            ListObjectsArgs lArgs = ListObjectsArgs.builder()
                    .bucket(BUCKET_NAME)
                    .prefix(fullPathToFolder)
                    .recursive(true)
                    .build();
            Iterable<Result<Item>> results = minioClient.listObjects(lArgs);

            for (Result<Item> result : results) {
                deleteObject(result.get().objectName());
            }
            log.info("Delete folder: " + fullPathToFolder);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
    }

    public void putFolder(MultipartFile[] files, String prefix) {
        Set<String> uniquePaths = getUniquePaths(files);
        uniquePaths.forEach(path -> createFolder(path, prefix));

        Arrays.stream(files).forEach(file -> {
            putObject(prefix, file);
        });
    }

    private Set<String> getUniquePaths(MultipartFile[] files) {
        Set<String> uniquePaths = new HashSet<>();
        Arrays.stream(files).forEach(file -> {
            int lastSlashIndex = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf('/');
            if (lastSlashIndex != -1) {
                uniquePaths.add(file.getOriginalFilename().substring(0, lastSlashIndex)); // Включаем слэш +1,у нас без ласт слэша
            }
        });
        return uniquePaths;
    }

    public void createRootFolder(Long id) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object("user-" + id + "-files/")
                            .stream(InputStream.nullInputStream(), 0, -1)
                            .build());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
    }


    public InputStream downloadFile(String objectPath) {
        if (!isObjectExist(objectPath)) {
            throw new ApplicationException("This object is already not in your storage!");
        }

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectPath)
                            .build()
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
    }

    public void downloadFolder(String folderPath, ZipOutputStream zipOut) {
        if (!isObjectExist(folderPath)) {
            throw new ApplicationException("This object is already not in your storage!");
        }
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET_NAME)
                            .prefix(folderPath)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                InputStream inputStream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(item.objectName())
                                .build()
                );

                ZipEntry zipEntry = new ZipEntry(item.objectName().substring(folderPath.length()));
                zipOut.putNextEntry(zipEntry);
                IOUtils.copy(inputStream, zipOut);
                zipOut.closeEntry();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
    }

    public List<FileResponseDTO> search(String query, String rootPath) {
        List<FileResponseDTO> listFiles = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET_NAME)
                            .prefix(rootPath)
                            .recursive(true)
                            .build());

            for (Result<Item> result : results) {
                String objectName = result.get().objectName();

                if (objectName.contains(query)) {
                    String[] parts = objectName.split("/");
                    log.info("search parts: " + Arrays.toString(parts));

                    if (parts[parts.length - 1].contains(query)) {
                        listFiles.add(FileResponseDTO.builder()
                                .name(parts[parts.length - 1])
                                .prefix(buildPath(parts))
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Problem with Minio folder");
        }
        return listFiles;
    }

    private String buildPath(String[] parts) {
        if (parts.length <= 2) {
            return "";
        }
        String[] subParts = Arrays.copyOfRange(parts, 1, parts.length - 1);
        return String.join("/", subParts);
    }
}
