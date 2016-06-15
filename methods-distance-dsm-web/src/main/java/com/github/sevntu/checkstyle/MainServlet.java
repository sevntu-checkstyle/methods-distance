package com.github.sevntu.checkstyle;

import com.github.sevntu.checkstyle.module.MethodCallDependencyModule;
import com.github.sevntu.checkstyle.common.MethodCallDependencyCheckInvoker;
import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.ordering.Ordering;
import com.github.sevntu.checkstyle.utils.FileUtils;
import com.github.sevntu.checkstyle.vizualization.DependencyInfoGraphSerializer;
import com.github.sevntu.checkstyle.vizualization.DependencyInfoMatrixSerializer;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servlet which handles requests for generation of DSM and DOT from
 * source code.
 */
public class MainServlet extends HttpServlet {

    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {

        final String requestPath = req.getPathInfo();
        final String sourceUrl = req.getParameter("source_url");
        try {
            final URL url = new URL(sourceUrl);
            switch (requestPath) {
                case "/dsm":
                    processDsm(url, resp);
                    break;
                case "/dot":
                    processDot(url, resp);
                    break;
                default:
                    processNotFound(resp);
            }
        }
        catch (final MalformedURLException e) {
            resp.getWriter().print("Malformed url provided: " + sourceUrl);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (final CheckstyleException e) {
            resp.getWriter().print("Checkstyle exception occurred: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void processDsm(URL sourceUrl, HttpServletResponse resp)
        throws CheckstyleException, IOException {

        class DsmDependencyInformationConsumer implements DependencyInformationConsumer {

            private Optional<Configuration> configuration;

            public void setConfiguration(final Configuration configuration) {
                this.configuration = Optional.of(configuration);
            }

            @Override
            public void accept(MethodCallDependencyModule module, String filePath,
                               Dependencies dependencies) {

                configuration.ifPresent(config -> {
                    try {
                        final String javaSource = FileUtils.getFileContents(filePath);
                        final Ordering ordering = new Ordering(dependencies);
                        final String html =
                            DependencyInfoMatrixSerializer.serialize(ordering, javaSource, config);
                        resp.setContentType("text/html");
                        resp.getWriter().append(html);
                    }
                    catch (final IOException | CheckstyleException e) {
                        throw new ResponseGenerationException(e);
                    }
                });
            }
        }

        final Map<String, String> config = getCheckConfiguration();
        final DsmDependencyInformationConsumer consumer = new DsmDependencyInformationConsumer();
        final MethodCallDependencyCheckInvoker invoker =
            new MethodCallDependencyCheckInvoker(config, consumer);
        consumer.setConfiguration(invoker.getConfiguration());
        invoker.invoke(Collections.singletonList(downloadSource(sourceUrl)));
    }

    private void processDot(URL sourceUrl, HttpServletResponse resp)
        throws CheckstyleException, IOException {

        final DependencyInformationConsumer consumer = (module, filePath, dependencies) -> {
            try {
                final String dot = DependencyInfoGraphSerializer.serialize(dependencies);
                resp.setContentType("text/vnd.graphviz");
                resp.getWriter().append(dot);
            }
            catch (final IOException e) {
                throw new ResponseGenerationException(e);
            }
        };

        final Map<String, String> config = getCheckConfiguration();
        final MethodCallDependencyCheckInvoker invoker =
            new MethodCallDependencyCheckInvoker(config, consumer);
        invoker.invoke(Collections.singletonList(downloadSource(sourceUrl)));
    }

    private void processNotFound(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    private Map<String, String> getCheckConfiguration() {
        final Map<String, String> config = new HashMap<>();
        config.put("screenLinesCount", "50");
        return config;
    }

    private static File downloadSource(URL sourceUrl) throws IOException {
        final File tmpFile = File.createTempFile("source", ".java");
        final URLConnection connection = sourceUrl.openConnection();
        connection.connect();
        try (final InputStream input = connection.getInputStream();
            final OutputStream output = new FileOutputStream(tmpFile)) {
            final int fileSize = connection.getHeaderFieldInt(CONTENT_LENGTH_HEADER, 0);
            final int bufferSize = 1024 * 4;
            final byte[] buffer = new byte[bufferSize];
            int downloaded = 0;
            while (downloaded < fileSize) {
                final int bytesRead = input.read(buffer);
                downloaded += bytesRead;
                output.write(buffer, 0, bytesRead);
            }
        }
        return tmpFile;
    }

    private static final class ResponseGenerationException extends RuntimeException {

        private ResponseGenerationException(Throwable cause) {
            super(cause);
        }
    }
}
