package com.sahapwnz.cloudfilestorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileController {
    private static final String UPLOAD_DIR = "src/main/resources/uploads/";
    @PostMapping("/load")
    String loadFile(@RequestParam("myFile")MultipartFile file){
        try{
            Path path = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "home";
    }
}
