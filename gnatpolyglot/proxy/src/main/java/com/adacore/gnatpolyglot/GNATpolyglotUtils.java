//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GNATpolyglotUtils {
    public enum VerboseLevel {
        /** Suppress warnings and infos */
        QUIET,
        /** Suppress infos */
        NORMAL,
        /** Keep all emissions */
        VERBOSE
    }

    private static VerboseLevel verbose = VerboseLevel.NORMAL;

    public static void setVerbose(VerboseLevel level) {
        verbose = level;
    }

    private static void emit(String location, String kind, String messsage) {
        System.err.print(location);
        System.err.print(": ");
        System.err.print(kind);
        System.err.print(": ");
        System.err.println(messsage);
    }

    private static void emit(String kind, String messsage) {
        System.err.print(kind);
        System.err.print(": ");
        System.err.println(messsage);
    }

    public static void emitWarning(String location, String messsage) {
        if (!verbose.equals(VerboseLevel.QUIET)) {
            emit(location, "warning", messsage);
        }
    }

    public static void emitWarning(String messsage) {
        if (!verbose.equals(VerboseLevel.QUIET)) {
            emit("warning", messsage);
        }
    }

    public static void emitError(String location, String messsage) {
        emit(location, "error", messsage);
    }

    public static void emitError(String messsage) {
        emit("error", messsage);
    }

    public static void emitInfo(String location, String messsage) {
        if (verbose.equals(VerboseLevel.QUIET)) {
            emit(location, "info", messsage);
        }
    }

    public static void emitInfo(String messsage) {
        if (verbose.equals(VerboseLevel.QUIET)) {
            emit("info", messsage);
        }
    }

    public static void emitStackTrace(Exception exc) {
        if (verbose.equals(VerboseLevel.VERBOSE)) {
            exc.printStackTrace(System.err);
        }
    }

    private static List<String> headerContent = List.of();

    public static void setHeaderFile(String file) throws java.io.IOException {
        headerContent = Files.readAllLines(Path.of(file));
    }

    public static List<String> getHeaderContent() {
        return headerContent;
    }
}
