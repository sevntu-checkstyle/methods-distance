package org.pirat9600q.graph;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

//CSOFF:
public final class DependencyInfoMatrixSerializer {

    private DependencyInfoMatrixSerializer() { }

    public static void writeToFile(final DependencyInfo info, final String fileName) {
        try (final PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(serialize(info));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String serialize(final DependencyInfo info) {
        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        final Template template = engine.getTemplate("org/pirat9600q/graph/matrix.vm");
        final VelocityContext context = new VelocityContext();
        context.put("info", info);
        context.put("javaScript", getJavaScript());
        context.put("css", getStyles());
        final StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private static String getStyles() {
        return readEntireStream(
                DependencyInfoMatrixSerializer.class.getResourceAsStream("styles.css"));
    }

    private static String getJavaScript() {
        return readEntireStream(
                DependencyInfoMatrixSerializer.class.getResourceAsStream("interactive.js"));
    }

    private static String readEntireStream(final InputStream stream) {
        try (final Scanner scanner = new Scanner(stream).useDelimiter("\\Z")) {
            return scanner.next();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
