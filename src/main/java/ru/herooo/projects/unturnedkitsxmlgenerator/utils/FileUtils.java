package ru.herooo.projects.unturnedkitsxmlgenerator.utils;

import java.io.File;

public class FileUtils {
    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getExtension(File file) {
        return getExtension(file.getName());
    }
}
