////////////////////////////////////////////////////////////////////////////////
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
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.html;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.json.JsonSerializer;
import com.puppycrawl.tools.checkstyle.api.Configuration;

/**
 * Serializes method-call dependency information into a self-contained HTML report.
 *
 * The report embeds the analysis JSON directly into the HTML template located at
 * {@code html-report-template.html} on the classpath.
 *
 * @author Zhu Tianyi
 */
public final class HtmlSerializer {

    /** Placeholder replaced with the dependency analysis JSON object. */
    private static final String DATA_PLACEHOLDER = "/*REPORT_DATA*/";

    /** Placeholder replaced with the JSON-encoded source code string. */
    private static final String SOURCE_PLACEHOLDER = "/*SOURCE_CODE*/";

    /** Classpath location of the HTML template resource. */
    private static final String TEMPLATE_RESOURCE = "html-report-template.html";

    private HtmlSerializer() {
        // no code
    }

    /**
     * Generates an HTML report file from the given dependencies.
     *
     * @param javaSource   path of the analysed Java source file
     * @param dependencies analysis result
     * @param config       checkstyle configuration (kept for API consistency)
     * @param fileName     path of the output HTML file to write
     */
    public static void writeToFile(String javaSource, Dependencies dependencies,
            Configuration config, String fileName) {
        try (PrintWriter out = new PrintWriter(fileName, "UTF-8")) {
            out.print(serialize(dependencies, javaSource, config));
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("Failed to write HTML report to: " + fileName, ex);
        }
    }

    /**
     * Builds the full HTML string by injecting the JSON payload and source code
     * into the template.
     *
     * @param dependencies analysis result
     * @param javaSource   path of the analysed Java source file
     * @param config       checkstyle configuration
     * @return complete HTML document as a string
     */
    public static String serialize(Dependencies dependencies, String javaSource,
            Configuration config) {
        final String template = loadTemplate();
        final String json = JsonSerializer.serialize(dependencies, javaSource, config);
        final String sourceJson = encodeSourceAsJson(javaSource);
        return template
            .replace(DATA_PLACEHOLDER, json)
            .replace(SOURCE_PLACEHOLDER, sourceJson);
    }

    /**
     * Reads the HTML template from the classpath.
     *
     * @return template content as a string
     */
    private static String loadTemplate() {
        try (final InputStream is =
            HtmlSerializer.class.getResourceAsStream(TEMPLATE_RESOURCE)) {
            if (is == null) {
                throw new IllegalStateException(
                    "HTML report template not found on classpath: " + TEMPLATE_RESOURCE);
            }
            return new String(readAllBytes(is), StandardCharsets.UTF_8);
        }
        catch (final IOException ex) {
            throw new IllegalStateException(
                "Failed to read HTML report template: " + ex.getMessage(), ex);
        }
    }

    /**
     * Reads the Java source file and returns it as a JSON-encoded string literal
     * (including surrounding double-quotes), safe for direct embedding in JavaScript.
     *
     * @param javaSource path to the Java source file
     * @return JSON string literal, e.g. {@code "line1\nline2\n..."}
     */
    private static String encodeSourceAsJson(String javaSource) {
        String encoded;
        try {
            final byte[] bytes = Files.readAllBytes(Paths.get(javaSource));
            final String content = new String(bytes, StandardCharsets.UTF_8);
            encoded = new ObjectMapper().writeValueAsString(content);
        }
        catch (final IOException ex) {
            encoded = "\"\"";
        }
        return encoded;
    }

    /**
     * Java-8-compatible replacement for {@code InputStream.readAllBytes()} (Java 9+).
     *
     * @param is the input stream to drain
     * @return all bytes read from the stream
     * @throws IOException if an I/O error occurs
     */
    private static byte[] readAllBytes(final InputStream is) throws IOException {
        final byte[] bytes = new byte[is.available()];
        try (DataInputStream dataInputStream = new DataInputStream(is)) {
            dataInputStream.readFully(bytes);
        }
        return bytes;
    }
}
