package com.sahapwnz.cloudfilestorage.controller;

import com.sahapwnz.cloudfilestorage.service.FileService;
import com.sahapwnz.cloudfilestorage.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {
    FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/load")
    String loadFile(@RequestParam("myFile") MultipartFile file, @RequestParam("prefix") String prefix, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        fileService.putObject(prefix, file);
        return "redirect:/";
    }

    @PostMapping("/delete-file")
    String deleteFile(@RequestParam("path") String pathToFile, @RequestParam("prefix") String prefix) {
        fileService.deleteObject(prefix + "/" + pathToFile);
        return "redirect:/";
    }

}
