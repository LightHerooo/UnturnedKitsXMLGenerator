package ru.herooo.projects.unturnedkitsxmlgenerator.directories;

import java.io.File;
import java.io.IOException;

public class DirectoryWithExample extends Directory {
    private final FileType EXAMPLE_FILE_TYPE;
    public DirectoryWithExample(String path, FileType exampleFileType) {
        super(path);
        this.EXAMPLE_FILE_TYPE = exampleFileType;
    }

    @Override
    public File[] getFiles() {
        File[] files = super.getFiles();
        if (files == null || files.length == 0) {
            createExample();
            files = DIRECTORY.listFiles();
        }

        return files;
    }

    protected File createExample() {
        try {
            return createFile(String.format("example%s", EXAMPLE_FILE_TYPE.TYPE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
