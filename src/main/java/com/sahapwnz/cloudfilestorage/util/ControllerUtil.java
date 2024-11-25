package com.sahapwnz.cloudfilestorage.util;

import com.sahapwnz.cloudfilestorage.service.UserDetailsImpl;

public class ControllerUtil {
    public static String getRootPath(UserDetailsImpl userDetails) {
        return "user-" + userDetails.getUser().getId() + "-files";
    }
}
