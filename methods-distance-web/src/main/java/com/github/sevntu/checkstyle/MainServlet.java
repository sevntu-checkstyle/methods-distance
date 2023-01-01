///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2023 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
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

package com.github.sevntu.checkstyle;

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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.sevntu.checkstyle.common.MethodCallDependencyCheckInvoker;
import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.dot.DependencyInfoGraphSerializer;
import com.github.sevntu.checkstyle.dsm.DependencyInfoMatrixSerializer;
import com.github.sevntu.checkstyle.module.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.ordering.MethodOrder;
import com.github.sevntu.checkstyle.utils.FileUtils;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

/**
 * Servlet which handles requests for generation of DSM and DOT from
 * source code.
 *
 * @author Zuy Alexey
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
        catch (final MalformedURLException ex) {
            resp.getWriter().print("Malformed url provided: " + sourceUrl);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (final CheckstyleException ex) {
            resp.getWriter().print("Checkstyle exception occurred: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private static void processDsm(URL sourceUrl, HttpServletResponse resp)
            throws CheckstyleException, IOException {

        class DsmDependencyInformationConsumer implements DependencyInformationConsumer {

            private Configuration configuration;

            @Override
            public void setConfiguration(Configuration configuration) {
                this.configuration = configuration;
            }

            @Override
            public void accept(String filePath, Dependencies dependencies) {
                try {
                    final String javaSource = FileUtils.getFileContents(filePath);
                    final MethodOrder methodOrder = new MethodOrder(dependencies);
                    final String html = DependencyInfoMatrixSerializer.serialize(
                        methodOrder, javaSource, configuration);
                    resp.setContentType("text/html");
                    resp.getWriter().append(html);
                }
                catch (final IOException | CheckstyleException ex) {
                    throw new ResponseGenerationException(ex);
                }
            }
        }

        final Map<String, String> config = getCheckConfiguration();
        final DsmDependencyInformationConsumer consumer = new DsmDependencyInformationConsumer();
        final MethodCallDependencyCheckInvoker invoker =
            new MethodCallDependencyCheckInvoker(config, consumer);
        consumer.setConfiguration(invoker.getConfiguration());
        invoker.invoke(Collections.singletonList(downloadSource(sourceUrl)));
    }

    private static void processDot(URL sourceUrl, HttpServletResponse resp)
            throws CheckstyleException, IOException {

        final DependencyInformationConsumer consumer = (filePath, dependencies) -> {
            try {
                final String dot = DependencyInfoGraphSerializer.serializeInfo(dependencies);
                resp.setContentType("text/vnd.graphviz");
                resp.getWriter().append(dot);
            }
            catch (final IOException ex) {
                throw new ResponseGenerationException(ex);
            }
        };

        final Map<String, String> config = getCheckConfiguration();
        final MethodCallDependencyCheckInvoker invoker =
            new MethodCallDependencyCheckInvoker(config, consumer);
        invoker.invoke(Collections.singletonList(downloadSource(sourceUrl)));
    }

    private static void processNotFound(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    private static Map<String, String> getCheckConfiguration() {
        final Map<String, String> config = new HashMap<>();
        config.put("screenLinesCount", "50");
        return config;
    }

    private static File downloadSource(URL sourceUrl) throws IOException {
        final File tmpFile = File.createTempFile("source", ".java");
        final URLConnection connection = sourceUrl.openConnection();
        connection.connect();
        try (InputStream input = connection.getInputStream();
            OutputStream output = new FileOutputStream(tmpFile)) {
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
