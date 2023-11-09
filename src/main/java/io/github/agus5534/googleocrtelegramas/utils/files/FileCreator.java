package io.github.agus5534.googleocrtelegramas.utils.files;

import io.github.agus5534.googleocrtelegramas.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileCreator {
    private final String name;
    private final File file;

    private List<FileCreator> parentFiles;

    public FileCreator(File path, String name, boolean deleteOnExit) {
        this.name = name;
        file = new File(path + "/" + name);
        parentFiles = new ArrayList<>();

        if(!Main.debugMode) { return; }

        try {
            this.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(deleteOnExit) {
            file.deleteOnExit();
        }
    }
    public FileCreator(File path, String name) {
        this(path, name, false);
    }

    private void init() throws IOException {
        if(!file.exists()) {
            if(this.name.endsWith("/")) {
                file.mkdir();

                return;
            }

            file.createNewFile();
        }
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public File[] getContents() {
        return getFile().listFiles();
    }

    public boolean hasFile(String name) {
        boolean f = false;

        for (File file : getContents()) {
            if(file == null) { continue; }
            if(file.getName().equalsIgnoreCase(name)) {
                f = true;
            }
        }

        return f;
    }

    public File getFile(String name) {
        File f = null;

        for (File file : getContents()) {
            if(file.getName().equalsIgnoreCase(name)) {
                f = file;
            }
        }

        return f;
    }

    public void registerParentFiles(FileCreator... fileCreator) {
        Arrays.stream(fileCreator).forEach(parentFiles::add);
    }

    public List<FileCreator> getParentFiles() {
        return parentFiles;
    }

    public File getDirectory() {
        return file;
    }

}
