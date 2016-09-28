package com.github.sevntu.checkstyle.module;

import com.github.sevntu.checkstyle.domain.Dependencies;
import com.puppycrawl.tools.checkstyle.api.Configuration;

public interface DependencyInformationConsumer {

    /**
     * Override this method to get instance of {@link Configuration}
     *
     * @param configuration
     */
    default void setConfiguration(Configuration configuration) {
        // default implementation
    }

    /**
     * Override this method to get instance of {@link MethodCallDependencyModule}.
     *
     * @param module
     */
    default void setModule(MethodCallDependencyModule module) {
        // default implementation
    }

    void accept(String filePath, Dependencies dependencies);
}
