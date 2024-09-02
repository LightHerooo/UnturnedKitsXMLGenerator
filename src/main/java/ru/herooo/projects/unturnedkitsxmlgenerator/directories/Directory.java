package ru.herooo.projects.unturnedkitsxmlgenerator.directories;

import java.io.File;
import java.io.IOException;

public class Directory {
    protected final File DIRECTORY;

    public Directory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        this.DIRECTORY = directory;
    }

    public String getPath() {
        return DIRECTORY.getPath();
    }

    public String getName() {
        return DIRECTORY.getName();
    }

    public File[] getFiles() {
        return DIRECTORY.listFiles();
    }

    public File createFile(String fileName) throws IOException {
        File file = new File(String.format("%s/%s", getPath(), fileName));
        File directory = file.getParentFile();
        if (directory != null) {
            directory.mkdirs();
        }
        file.createNewFile();

        return file;
    }
}
