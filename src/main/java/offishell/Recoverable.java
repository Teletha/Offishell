/*
 * Copyright (C) 2020 offishell Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package offishell;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import kiss.I;
import offishell.macro.Window;
import psychopath.File;
import psychopath.Locator;

/**
 * @version 2016/08/01 17:41:04
 */
public class Recoverable {

    private static final List<Runnable> reverts = new ArrayList();

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            for (Runnable run : reverts) {
                run.run();
            }
        });
    }

    /**
     * <p>
     * ロールバック可能なファイル操作基盤を提供します。
     * </p>
     * 
     * @param file
     * @param operation
     */
    public static void write(Path file, Consumer<OutputStream> operation) {
        boolean exist = Window.existByTitle(file);

        if (exist) {
            Window.close(file);
        }

        try {
            File original = Locator.file(file);
            File backup = Locator.temporaryFile();

            original.copyTo(backup);

            reverts.add(() -> {
                // ロールバック
                original.delete();
                backup.copyTo(original);
            });

            try (OutputStream output = Files.newOutputStream(file);) {
                operation.accept(output);
            } catch (Throwable e) {
                throw I.quiet(e);
            }
        } finally {
            if (exist) {
                Window.open(file);
            }
        }
    }
}