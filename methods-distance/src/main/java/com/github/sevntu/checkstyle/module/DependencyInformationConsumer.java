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

package com.github.sevntu.checkstyle.module;

import com.github.sevntu.checkstyle.domain.Dependencies;
import com.puppycrawl.tools.checkstyle.api.Configuration;

public interface DependencyInformationConsumer {

    /**
     * Override this method to get instance of {@link Configuration}.
     *
     * @param configuration
     *     The {@link Configuration} being set.
     */
    default void setConfiguration(Configuration configuration) {
        // default implementation
    }

    /**
     * Override this method to get instance of {@link MethodCallDependencyCheckstyleModule}.
     *
     * @param module
     *     The {@link MethodCallDependencyCheckstyleModule} being set.
     */
    default void setModule(MethodCallDependencyCheckstyleModule module) {
        // default implementation
    }

    void accept(String filePath, Dependencies dependencies);
}
