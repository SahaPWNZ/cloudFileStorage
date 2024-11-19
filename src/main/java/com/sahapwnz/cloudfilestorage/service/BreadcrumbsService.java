package com.sahapwnz.cloudfilestorage.service;

import org.springframework.stereotype.Service;

@Service
public class BreadcrumbsService {
    public String getBreadcrumbsForPath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        int lastSlashIndex = path.lastIndexOf("/");
        return lastSlashIndex <= 0 ? "/" : path.substring(0, lastSlashIndex);
    }
}
