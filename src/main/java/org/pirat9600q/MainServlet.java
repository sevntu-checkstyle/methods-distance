package org.pirat9600q;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.pirat9600q.graph.Dependencies;
import org.pirat9600q.graph.DependencyInfoGraphSerializer;
import org.pirat9600q.graph.DependencyInfoMatrixSerializer;
import org.pirat9600q.graph.DependencyInformationConsumer;
import org.pirat9600q.graph.MethodCallDependencyCheck;
import org.pirat9600q.utils.FileUtils;

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
import java.util.Collections;

public class MainServlet extends HttpServlet {

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

    private void processDsm(final URL sourceUrl, final HttpServletResponse resp) throws
            CheckstyleException, IOException {
        final Configuration config = getCheckConfiguration();
        processWithConsumer(sourceUrl, config, new DependencyInformationConsumer() {
            @Override
            public void accept(String filePath, Dependencies dependencies) {
                try {
                    final String javaSource = FileUtils.getFileContents(filePath);
                    final String html =
                        DependencyInfoMatrixSerializer.serialize(dependencies, javaSource, config);
                    resp.setContentType("text/html");
                    resp.getWriter().append(html);
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void processDot(final URL sourceUrl, final HttpServletResponse resp) throws
            CheckstyleException, IOException {
        final Configuration config = getCheckConfiguration();
        processWithConsumer(sourceUrl, config, new DependencyInformationConsumer() {
            @Override
            public void accept(String filePath, Dependencies dependencies) {
                try {
                    final String dot = DependencyInfoGraphSerializer.serialize(dependencies);
                    resp.setContentType("text/vnd.graphviz");
                    resp.getWriter().append(dot);
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void processWithConsumer(final URL sourceUrl, final Configuration config,
        final DependencyInformationConsumer consumer) throws CheckstyleException, IOException {
        final MethodCallDependencyCheckInvoker invoker =
            new MethodCallDependencyCheckInvoker(config, consumer);
        invoker.invoke(Collections.singletonList(downloadSource(sourceUrl)));
    }

    private void processNotFound(final HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    private Configuration getCheckConfiguration() {
        final DefaultConfiguration config =
            new DefaultConfiguration(MethodCallDependencyCheck.class.getCanonicalName());
        config.addAttribute("screenLinesCount", "50");
        return config;
    }

    private static File downloadSource(final URL sourceUrl) throws IOException {
        final File tmpFile = File.createTempFile("source", ".java");
        try (final InputStream input = sourceUrl.openStream();
            final OutputStream output = new FileOutputStream(tmpFile)) {
            final int bufferSize = 1024 * 4;
            final byte[] buffer = new byte[bufferSize];
            do {
                final int bytesRead = input.read(buffer);
                output.write(buffer, 0, bytesRead);
            } while (input.available() > 0);
        }
        return tmpFile;
    }
}
