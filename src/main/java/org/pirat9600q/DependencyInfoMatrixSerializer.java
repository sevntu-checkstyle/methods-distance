package org.pirat9600q;

import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.pirat9600q.analysis.Dependencies;
import org.pirat9600q.analysis.PenaltyCalculator;
import org.pirat9600q.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class DependencyInfoMatrixSerializer {

    private DependencyInfoMatrixSerializer() { }

    public static void writeToFile(final String javaSource, final Dependencies dependencies,
        final Configuration config, final String fileName) {
        try (final PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(serialize(dependencies, javaSource, config));
        }
        catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String serialize(final Dependencies dependencies, final String javaSource,
        final Configuration config) {
        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty(
            "classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        final VelocityContext context = new VelocityContext();
        context.put("info", dependencies);
        context.put("javaScript", getJavaScript());
        context.put("css", getStyles());
        context.put("javaSource", javaSource);
        context.put("calculator", getPenaltyCalculator());
        context.put("configuration", config);
        final StringWriter writer = new StringWriter();
        final Template template = engine.getTemplate("org/pirat9600q/matrix.vm");
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
