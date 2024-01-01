///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2024 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public final class FileUtils {

    private FileUtils() {
        // no code
    }

    public static String getFileContents(String filePath) {
        try (InputStream stream = new FileInputStream(filePath)) {
            return getTextStreamContents(stream);
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static String getTextStreamContents(InputStream input) {
        final Scanner scanner = new Scanner(input);
        scanner.useDelimiter("\\Z");
        return scanner.next();
    }
}
