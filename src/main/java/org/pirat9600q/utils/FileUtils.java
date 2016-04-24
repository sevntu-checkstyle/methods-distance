package org.pirat9600q.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public final class FileUtils {

    private FileUtils() { }

    public static String getFileContents(final String filePath) {
        try (final Scanner scanner = new Scanner(new FileInputStream(filePath))) {
            scanner.useDelimiter("\\Z");
            return scanner.next();
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
