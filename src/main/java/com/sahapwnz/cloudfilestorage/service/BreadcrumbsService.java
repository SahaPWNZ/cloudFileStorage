package com.sahapwnz.cloudfilestorage.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class BreadcrumbsService {
    public String getBreadcrumbsForPath(String path) {
        if (path == null) {
            return "";
        } else if (!path.contains("/")) {
            return "/";
        } else {
            String[] parts = path.split("/");
            if (parts.length <= 2) {
                return parts[0];
            } else {
                String[] subParts = Arrays.copyOfRange(parts, 0, parts.length - 1);
                return String.join("/", subParts);
            }
        }
    }
}
