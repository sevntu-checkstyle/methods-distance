package com.github.sevntu.checkstyle.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public final class FileUtils {

    private FileUtils() { }

    public static String getFileContents(String filePath) {
        try (InputStream stream = new FileInputStream(filePath)) {
            return getTextStreamContents(stream);
        }
        catch (final IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String getTextStreamContents(InputStream input) {
        final Scanner scanner = new Scanner(input);
        scanner.useDelimiter("\\Z");
        return scanner.next();
    }
}
