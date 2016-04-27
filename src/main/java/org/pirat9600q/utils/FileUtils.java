package org.pirat9600q.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public final class FileUtils {

    private FileUtils() { }

    public static String getFileContents(final String filePath) {
        try (final InputStream stream = new FileInputStream(filePath)) {
            return getTextStreamContents(stream);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTextStreamContents(final InputStream input) {
        final Scanner scanner = new Scanner(input);
        scanner.useDelimiter("\\Z");
        return scanner.next();
    }
}
