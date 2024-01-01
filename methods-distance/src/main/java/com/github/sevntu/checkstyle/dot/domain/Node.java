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

package com.github.sevntu.checkstyle.dot.domain;

import java.util.HashMap;
import java.util.Map;

/* Class name is sufficiently descriptive. */
@SuppressWarnings("PMD.ShortClassName")
public class Node implements Element, AttributeHolder {

    private final String id;

    private final Map<String, String> attrs = new HashMap<>();

    public Node(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public Map<String, String> attributes() {
        return attrs;
    }

    public void setColor(Colors color) {
        addAttribute("color", color.asString());
    }

    public void setShape(Shapes shape) {
        addAttribute("shape", shape.asString());
    }
}
