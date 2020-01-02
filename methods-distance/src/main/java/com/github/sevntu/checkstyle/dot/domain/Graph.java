////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
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
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.dot.domain;

import java.util.ArrayList;
import java.util.List;

public class Graph implements CompositeElement {

    private final String name;

    private final List<Element> componentList = new ArrayList<>();

    private Rankdirs rankdir = Rankdirs.LR;

    public Graph(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Rankdirs getRankdir() {
        return rankdir;
    }

    public void setRankdir(Rankdirs rankdir) {
        this.rankdir = rankdir;
    }

    @Override
    public List<Element> components() {
        return componentList;
    }
}
