package com.sahapwnz.cloudfilestorage.controller;

import com.sahapwnz.cloudfilestorage.service.FileService;
import com.sahapwnz.cloudfilestorage.service.UserDetailsImpl;
import com.sahapwnz.cloudfilestorage.util.ControllerUtil;
import com.sahapwnz.cloudfilestorage.util.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Controller
public class FileController {
    FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/loadFile")
    String loadFile(@RequestParam("myFile") MultipartFile file,
                    @RequestParam("prefix") String prefix,
                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                    HttpServletRequest request) {
        String rootPath = ControllerUtil.getRootPath(userDetails);

        ValidationUtil.isValidLoadFileName(rootPath + prefix, file.getOriginalFilename(), fileService);
        fileService.putObject(rootPath + prefix, file);

        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/delete-file")
    String deleteFile(@RequestParam("path") String pathToFile,
                      @RequestParam("prefix") String prefix,
                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                      HttpServletRequest request) {

        String rootPath = ControllerUtil.getRootPath(userDetails);
        fileService.deleteObject(rootPath + prefix + "/" + pathToFile);

        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/rename-file")
    String renameFile(@RequestParam("oldFileName") String oldFileName,
                      @RequestParam("newFileName") String newFileName,
                      @RequestParam("prefix") String prefix,
                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                      HttpServletRequest request) {
        String rootPath = ControllerUtil.getRootPath(userDetails);
        ValidationUtil.isValidRenameFileName(newFileName, rootPath + prefix, fileService);

        fileService.renameFile(oldFileName, newFileName, rootPath + prefix);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/download-file")
    ResponseEntity<Resource> downloadFile(@RequestParam("prefix") String prefix,
                                          @RequestParam("path") String fileName,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String rootPath = ControllerUtil.getRootPath(userDetails);
        InputStream inputStream = fileService.downloadFile(rootPath + prefix + "/" + fileName);
        Resource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/search")
    String search(Model model,
                  @RequestParam("query") String query,
                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String rootPath = ControllerUtil.getRootPath(userDetails);
        var results = fileService.search(query, rootPath);

        model.addAttribute("query", query);
        model.addAttribute("results", results);
        return "search";
    }
}
