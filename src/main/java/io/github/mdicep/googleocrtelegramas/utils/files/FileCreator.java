package io.github.mdicep.googleocrtelegramas.utils.files;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase utilitaria para la creación y manipulación de archivos y directorios.
 */
@Getter
public class FileCreator {
    /**
     * -- GETTER --
     *  Obtiene el nombre del archivo o directorio.
     *
     * @return El nombre.
     */
    private final String name;
    /**
     * -- GETTER --
     *  Obtiene el archivo asociado.
     *
     * @return El archivo.
     */
    private final File file;
    /**
     * -- GETTER --
     *  Obtiene la lista de archivos padres.
     *
     * @return Lista de archivos padres.
     */
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
     * Obtiene el directorio asociado.
     *
     * @return El directorio.
     */
    public File getDirectory() {
        return file;
    }
}
