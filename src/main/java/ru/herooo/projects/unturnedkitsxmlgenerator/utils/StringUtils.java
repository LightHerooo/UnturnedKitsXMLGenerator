package ru.herooo.projects.unturnedkitsxmlgenerator.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtils {
    public static String createLocalDateTimeStr(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static boolean isNull(String str) {
        return str == null || str.isEmpty() || str.isBlank();
    }
}
