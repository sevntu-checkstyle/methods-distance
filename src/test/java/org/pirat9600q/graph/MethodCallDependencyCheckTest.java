package org.pirat9600q.graph;

import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

import javax.security.auth.login.Configuration;
import java.io.IOException;

public class MethodCallDependencyCheckTest extends BaseCheckTestSupport {

    @Test
    public void testThatWeCanWriteTests() throws Exception {
        final DefaultConfiguration config = createCheckConfig(MethodCallDependencyCheck.class);
        verify(config, getInputPath("InputIndependentMethods.java"));
    }

    private static String getInputPath(final String fileName) {
        return MethodCallDependencyCheckTest.class.getResource(fileName).getPath();
    }
}
