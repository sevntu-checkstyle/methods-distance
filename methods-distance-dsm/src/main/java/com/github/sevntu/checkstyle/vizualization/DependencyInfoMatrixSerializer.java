package com.github.sevntu.checkstyle.vizualization;

import com.github.sevntu.checkstyle.ordering.PenaltyCalculator;
import com.github.sevntu.checkstyle.ordering.Ordering;
import com.github.sevntu.checkstyle.utils.FileUtils;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class DependencyInfoMatrixSerializer {

    private DependencyInfoMatrixSerializer() { }

    public static void writeToFile(String javaSource, Ordering ordering,
        final Configuration config, final String fileName) {

        try (final PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(serialize(ordering, javaSource, config));
        }
        catch (final CheckstyleException | FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String serialize(Ordering ordering, String javaSource,
        Configuration config) throws CheckstyleException {

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty(
            "classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        final PenaltyCalculator calculator = getPenaltyCalculator();
        final int screenLinesCount = Integer.parseInt(config.getAttribute("screenLinesCount"));
        final VelocityContext context = new VelocityContext();
        context.put("info", ordering);
        context.put("javaScript", getJavaScript());
        context.put("css", getStyles());
        context.put("javaSource", javaSource);
        context.put("calculator", calculator);
        context.put("penaltyValue", calculator.getPenalty(ordering, screenLinesCount));
        context.put("configuration", config);
        final StringWriter writer = new StringWriter();
        final Template template =
            engine.getTemplate("com/github/sevntu/checkstyle/vizualization/matrix.vm");
        template.merge(context, writer);
        return writer.toString();
    }

    private static PenaltyCalculator getPenaltyCalculator() {
        return new PenaltyCalculator();
    }

    private static String getStyles() {
        return FileUtils.getTextStreamContents(
                DependencyInfoMatrixSerializer.class.getResourceAsStream("styles.css"));
    }

    private static String getJavaScript() {
        return FileUtils.getTextStreamContents(
            DependencyInfoMatrixSerializer.class.getResourceAsStream("interactive.js"));
    }
}
