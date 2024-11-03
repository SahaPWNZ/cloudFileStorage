package com.sahapwnz.cloudfilestorage.controller;

import com.sahapwnz.cloudfilestorage.service.FileService;
import com.sahapwnz.cloudfilestorage.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.ZipOutputStream;

@Controller
public class FileController {
    FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/load")
    String loadFile(@RequestParam("myFile") MultipartFile file, @RequestParam("prefix") String prefix, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("prefix: " + prefix);
        String rootPath = "user-" + userDetails.getUser().getId() + "-files";
        fileService.putObject(rootPath + prefix, file);
        return "redirect:/";
    }

    @PostMapping("/loadFolder")
    String loadFolder(@RequestParam("myFolder") MultipartFile[] files, @RequestParam("prefix") String prefix, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String rootPath = "user-" + userDetails.getUser().getId() + "-files";
        Arrays.stream(files).forEach(file -> System.out.println("folder:: " + file.getOriginalFilename()));
        fileService.putFolder(files, rootPath + prefix);
        return "redirect:/";
    }

    @PostMapping("/delete-file")
    String deleteFile(@RequestParam("path") String pathToFile, @RequestParam("prefix") String prefix, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("prefix: " + prefix);
        String rootPath = "user-" + userDetails.getUser().getId() + "-files";
        fileService.deleteObject(rootPath + prefix + "/" + pathToFile);
        return "redirect:/";
    }

    @PostMapping("/delete-folder")
    String deleteFolder(@RequestParam("path") String pathToFolder, @RequestParam("prefix") String prefix, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("delete-folder:" + pathToFolder);
        System.out.println("delete-folder:" + prefix);
        String rootPath = "user-" + userDetails.getUser().getId() + "-files";
        fileService.deleteFolder(rootPath + prefix + "/" + pathToFolder);
        return "redirect:/";
    }

    @PostMapping("/create-folder")
    String createFolder(@RequestParam("folderName") String folderName, @RequestParam("prefix") String prefix, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("prefix: " + prefix);
        String rootPath = "user-" + userDetails.getUser().getId() + "-files";
        fileService.createFolder(folderName, rootPath + prefix);
        return "redirect:/";
    }

    @PostMapping("/rename-file")
    String renameFile(@RequestParam("oldFileName") String oldFileName, @RequestParam("newFileName") String newFileName,
                      @RequestParam("prefix") String prefix, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String rootPath = "user-" + userDetails.getUser().getId() + "-files";
        fileService.renameFile(oldFileName, newFileName, rootPath + prefix);
        return "redirect:/";
    }

    @PostMapping("/rename-folder")
    String renameFolder(@RequestParam("oldFolderName") String oldFolderName, @RequestParam("newFolderName") String newFolderName,
                        @RequestParam("prefix") String prefix, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String rootPath = "user-" + userDetails.getUser().getId() + "-files";
        fileService.renameFolder(oldFolderName, newFolderName, rootPath + prefix + "/");
        return "redirect:/";
    }


    @GetMapping("/download-file")
    ResponseEntity<Resource> downloadFile(@RequestParam("prefix") String prefix, @RequestParam("path") String fileName,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String rootPath = "user-" + userDetails.getUser().getId() + "-files";
        InputStream inputStream = fileService.downloadFile(rootPath + prefix + "/" + fileName);
        Resource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/download-folder")
    ResponseEntity<byte[]> downloadFolder(@RequestParam("prefix") String prefix, @RequestParam("path") String folderName,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse response) throws IOException {
        String rootPath = "user-" + userDetails.getUser().getId() + "-files";

        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(zipOutputStream);

        fileService.downloadFolder(rootPath + prefix + "/" + folderName, zipOut);
        zipOut.close();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipOutputStream.toByteArray());
    }

}
