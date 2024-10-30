package com.sahapwnz.cloudfilestorage.controller;

import com.sahapwnz.cloudfilestorage.service.FileService;
import com.sahapwnz.cloudfilestorage.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

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

    @PostMapping("/loadFolder")
    String loadFolder(@RequestParam("myFolder") MultipartFile[] files, @RequestParam("prefix") String prefix){
        Arrays.stream(files).forEach(file-> System.out.println("folder:: "+file.getOriginalFilename()));
        fileService.purFolder(files, prefix);
        return "redirect:/";
    }

    @PostMapping("/delete-file")
    String deleteFile(@RequestParam("path") String pathToFile, @RequestParam("prefix") String prefix) {
        fileService.deleteObject(prefix + "/" + pathToFile);
        return "redirect:/";
    }

    @PostMapping("/create-folder")
    String createFolder(@RequestParam("folderName") String folderName, @RequestParam("prefix") String prefix) {
        fileService.createFolder(folderName, prefix);
        return "redirect:/";
    }

    @PostMapping("/rename-file")
    String renameFile(@RequestParam("oldFileName") String oldFileName, @RequestParam("newFileName") String newFileName,
                      @RequestParam("prefix") String prefix) {
        fileService.renameFile(oldFileName, newFileName, prefix);

        return "redirect:/";
    }

}
