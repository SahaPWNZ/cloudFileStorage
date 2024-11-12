package com.sahapwnz.cloudfilestorage.util;

import com.sahapwnz.cloudfilestorage.exception.ExistException;
import com.sahapwnz.cloudfilestorage.exception.InvalidNameException;
import com.sahapwnz.cloudfilestorage.service.FileService;

public class ValidationUtil {

    public static void isValidNewFolderName(String folderName, String prefix, FileService fileService) {
        String nameWithoutSlash = removeSlashesFromStartAndEnd(folderName);
        validateName(folderName, nameWithoutSlash);
        checkFolderExist(prefix, nameWithoutSlash, fileService);
    }

    public static void isValidRenameFolderName(String folderName, String prefix, FileService fileService) {
        if (!folderName.endsWith("/")) {
            throw new InvalidNameException("The folder name must end with a slash \"/\". Please choose another folder name");
        }
        String folderWithoutSlash = removeSlashesFromStartAndEnd(folderName);
        validateName(folderName, folderWithoutSlash);
        checkFolderExist(prefix, folderWithoutSlash, fileService);
    }

    public static void isValidRenameFileName(String newFileName, String prefix, FileService fileService) {
        validateName(newFileName);
        checkFileExist(prefix, newFileName, fileService);
    }

    private static void validateName(String name, String str) {
        if (str.isEmpty() || str.contains("/") || str.equals(".") || str.equals("..")) {
            throw new InvalidNameException("This name: " + name + " is invalid");
        }
    }

    private static void validateName(String name) {
        if (name.isEmpty() || name.contains("/") || name.equals(".") || name.equals("..")) {
            throw new InvalidNameException("This name: " + name + " is invalid");
        }
    }

    private static void checkFolderExist(String prefix, String nameWithoutSlash, FileService fileService) {
        String fullPath = prefix + "/" + nameWithoutSlash + "/";
        if (fileService.isObjectExist(fullPath)) {
            throw new ExistException("Folder with name: " + nameWithoutSlash + " is already in use. Please choose another folder name");
        }
    }

    private static void checkFileExist(String prefix, String name, FileService fileService) {
        String fullPath = prefix + "/" + name;
        if (fileService.isObjectExist(fullPath)) {
            throw new ExistException("File with name: " + name + " is already in use. Please choose another file name");
        }
    }

    private static String removeSlashesFromStartAndEnd(String str) {
        return str.replaceAll("^/+|/+$", "");
    }

}
