package io.github.agus5534.googleocrtelegramas.utils.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Clase utilitaria para la creación y manipulación de archivos y directorios.
 */
public class FileCreator {
    private final String name;
    private final File file;
    private final List<FileCreator> parentFiles;

    /**
     * Constructor principal.
     *
     * @param path          Directorio padre.
     * @param name          Nombre del archivo o directorio.
     * @param deleteOnExit  Indica si el archivo debe eliminarse al salir.
     * @throws RuntimeException Si ocurre un error al inicializar el archivo.
     */
    public FileCreator(File path, String name, boolean deleteOnExit) {
        this.name = name;
        this.file = new File(path, name);
        this.parentFiles = new ArrayList<>();

        try {
            init(deleteOnExit);
        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar el archivo", e);
        }
    }

    /**
     * Constructor alternativo.
     *
     * @param path Directorio padre.
     * @param name Nombre del archivo o directorio.
     */
    public FileCreator(File path, String name) {
        this(path, name, false);
    }

    private void init(boolean deleteOnExit) throws IOException {
        if (!file.exists()) {
            if (name.endsWith("/")) {
                file.mkdir();
            } else {
                file.createNewFile();
            }
        }

        if (deleteOnExit) {
            file.deleteOnExit();
        }
    }

    /**
     * Obtiene el archivo asociado.
     *
     * @return El archivo.
     */
    public File getFile() {
        return file;
    }

    /**
     * Obtiene el nombre del archivo o directorio.
     *
     * @return El nombre.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtiene el contenido del directorio.
     *
     * @return Un array de archivos.
     */
    public File[] getContents() {
        return file.listFiles();
    }

    /**
     * Verifica si el directorio contiene un archivo con el nombre dado.
     *
     * @param name Nombre del archivo a verificar.
     * @return `true` si el archivo existe, de lo contrario `false`.
     */
    public boolean hasFile(String name) {
        for (File file : getContents()) {
            if (file != null && file.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene un archivo por su nombre.
     *
     * @param name Nombre del archivo.
     * @return El archivo correspondiente al nombre.
     */
    public File getFile(String name) {
        for (File file : getContents()) {
            if (file != null && file.getName().equalsIgnoreCase(name)) {
                return file;
            }
        }
        return null;
    }

    /**
     * Registra archivos padres.
     *
     * @param fileCreators Arreglo de archivos padres.
     */
    public void registerParentFiles(FileCreator... fileCreators) {
        parentFiles.addAll(Arrays.asList(fileCreators));
    }

    /**
     * Obtiene la lista de archivos padres.
     *
     * @return Lista de archivos padres.
     */
    public List<FileCreator> getParentFiles() {
        return parentFiles;
    }

    /**
     * Obtiene el directorio asociado.
     *
     * @return El directorio.
     */
    public File getDirectory() {
        return file;
    }
}
