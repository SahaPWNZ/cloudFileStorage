package com.sahapwnz.cloudfilestorage.controller;

import com.sahapwnz.cloudfilestorage.service.FileService;
import com.sahapwnz.cloudfilestorage.service.UserDetailsImpl;
import com.sahapwnz.cloudfilestorage.util.ControllerUtil;
import com.sahapwnz.cloudfilestorage.util.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Arrays;
import java.util.zip.ZipOutputStream;

@Controller
public class FolderController {
    FileService fileService;

    public FolderController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/create-folder")
    String createFolder(@RequestParam("folderName") String folderName,
                        @RequestParam("prefix") String prefix,
                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                        HttpServletRequest request) {
        String rootPath = ControllerUtil.getRootPath(userDetails);
        ValidationUtil.isValidNewFolderName(folderName, rootPath + prefix, fileService);

        fileService.createFolder(folderName, rootPath + prefix);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/loadFolder")
    String loadFolder(@RequestParam("myFolder") MultipartFile[] files,
                      @RequestParam("prefix") String prefix,
                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                      HttpServletRequest request) {
        String rootPath = ControllerUtil.getRootPath(userDetails);
        Arrays.stream(files).forEach(file -> System.out.println("folder:: " + file.getOriginalFilename()));
        ValidationUtil.isValidLoadFolderName(rootPath + prefix, files, fileService);

        fileService.putFolder(files, rootPath + prefix);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/delete-folder")
    String deleteFolder(@RequestParam("path") String pathToFolder,
                        @RequestParam("prefix") String prefix,
                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                        HttpServletRequest request) {
        String rootPath = ControllerUtil.getRootPath(userDetails);
        fileService.deleteFolder(rootPath + prefix + "/" + pathToFolder);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/rename-folder")
    String renameFolder(@RequestParam("oldFolderName") String oldFolderName,
                        @RequestParam("newFolderName") String newFolderName,
                        @RequestParam("prefix") String prefix,
                        @AuthenticationPrincipal UserDetailsImpl userDetails,
                        HttpServletRequest request) {
        String rootPath = ControllerUtil.getRootPath(userDetails);
        ValidationUtil.isValidRenameFolderName(newFolderName, rootPath + prefix, fileService);

        fileService.renameFolder(oldFolderName, newFolderName, rootPath + prefix + "/");
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/download-folder")
    ResponseEntity<byte[]> downloadFolder(@RequestParam("prefix") String prefix,
                                          @RequestParam("path") String folderName,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        String rootPath = ControllerUtil.getRootPath(userDetails);

        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(zipOutputStream);

        fileService.downloadFolder(rootPath + prefix + "/" + folderName, zipOut);
        zipOut.close();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + folderName.substring(0, folderName.length() - 1) + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipOutputStream.toByteArray());
    }
}
