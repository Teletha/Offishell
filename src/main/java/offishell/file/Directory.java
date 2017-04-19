/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package offishell.file;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import javafx.stage.FileChooser.ExtensionFilter;

import filer.Filer;
import kiss.I;
import offishell.UI;

/**
 * @version 2016/07/16 17:03:37
 */
public class Directory {

    /** The root directory to search. */
    private Path directory;

    private String findingText;

    private List<ExtensionFilter> findingFilter = new ArrayList();

    private Path findingDirectory;

    private boolean delete;

    private String memorize;

    /**
     * 
     */
    private Directory() {
    }

    /**
     * @param directory2
     */
    private Directory(Path directory) {
        try {
            if (!Files.isDirectory(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw I.quiet(e);
        }

        this.directory = directory;
    }

    /**
     * <p>
     * Specify finding UI text.
     * </p>
     * 
     * @param text
     * @return
     */
    public Directory searchText(String text) {
        this.findingText = text;

        return this;
    }

    /**
     * <p>
     * Specify finding UI filters.
     * </p>
     * 
     * @param text
     * @return
     */
    public Directory searchFilter(String description, String... filters) {
        this.findingFilter.add(new ExtensionFilter(description, filters));

        return this;
    }

    /**
     * <p>
     * Specify finding directory.
     * </p>
     * 
     * @param text
     * @return
     */
    public Directory searchDirectory(Path directory) {
        this.findingDirectory = directory;

        return this;
    }

    /**
     * <p>
     * 検索ディレクトリを記憶して次回に使用します。
     * </p>
     * 
     * @param key
     * @return
     */
    public Directory memorizeSearchDirectory() {
        this.memorize = new Error().getStackTrace()[1].getMethodName();

        return this;
    }

    /**
     * <p>
     * Setting delete mode.
     * </p>
     * 
     * @return
     */
    public Directory deleteOriginalOnCopy() {
        this.delete = true;

        return this;
    }

    /**
     * <p>
     * Specify the file name which you want to select.
     * </p>
     * 
     * @param fileNameWithoutExtension
     * @return
     */
    public Path file(String fileName, FileType... types) {
        try {
            FileName file = new FileName(fileName);
            List<Path> candidates = scan(directory, name -> name.match(file, types));

            if (!candidates.isEmpty()) {
                return candidates.get(0);
            } else {
                MemorizedDirectory directories = I.make(MemorizedDirectory.class);

                if (findingText == null) {
                    findingText = fileName + "を選択してください";
                }

                Path selected = UI.selectFile(findingText, directories.get(memorize), findingFilter);
                FileName selectedFileName = new FileName(selected);

                Path output = directory.resolve(file.name + "." + selectedFileName.extension);
                Filer.copy(selected, output);
                directories.put(memorize, selected.getParent());

                if (delete) {
                    Filer.delete(selected);
                }
                return output;
            }
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Scan directory.
     * </p>
     * 
     * @param directory
     * @param filter
     * @return
     */
    private List<Path> scan(Path directory, Predicate<FileName> filter) {
        List<Path> list = new ArrayList();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, path -> filter.test(new FileName(path)))) {
            for (Path path : stream) {
                list.add(path);
            }
        } catch (Exception e) {
            throw I.quiet(e);
        }
        return list;
    }

    /**
     * <p>
     * Create {@link Directory} for the specified directory.
     * </p>
     * 
     * @param directory
     * @return
     */
    public static Directory of(Path directory) {
        return new Directory(directory);
    }

    /**
     * @param category
     * @return
     */
    public static Path by(String category) {
        return I.make(MemorizedDirectory.class).computeIfAbsent(category, key -> UI.selectDirectory(category));
    }

    private static class MemorizedDirectory extends HashMap<String, Path> {
    }
}
