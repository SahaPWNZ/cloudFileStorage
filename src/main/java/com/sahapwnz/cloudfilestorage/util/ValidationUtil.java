package com.sahapwnz.cloudfilestorage.util;

import com.sahapwnz.cloudfilestorage.exception.NewFolderException;
import com.sahapwnz.cloudfilestorage.service.FileService;

public class ValidationUtil {

    public static void isValidNewFolderName(String folderName, String prefix, FileService fileService) {
        String folder = removeSlashesFromStartAndEnd(folderName);
        validateFolderName(folderName, folder);
        checkFolderExistence(prefix, folder, fileService);
    }

    private static void validateFolderName(String folderName, String folder) {
        if (folder.isEmpty() || folder.contains("/") || folder.equals(".") || folder.equals("..")) {
            throw new NewFolderException("This folder name: " + folderName + " is invalid");
        }
    }

    private static void checkFolderExistence(String prefix, String folder, FileService fileService) {
        String fullPath = prefix + "/" + folder + "/";
        if (fileService.isObjectExist(fullPath)) {
            throw new NewFolderException("Folder with name: " + folder + " is already in use. Please choose another folder name");
        }
    }

    private static String removeSlashesFromStartAndEnd(String str) {
        return str.replaceAll("^/+|/+$", "");
    }
}
