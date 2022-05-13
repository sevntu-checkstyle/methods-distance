///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2022 the original author or authors.
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

package com.github.sevntu.checkstyle.ordering;

import com.github.sevntu.checkstyle.domain.MethodDefinition;

/* This class is data-heavy. */
@SuppressWarnings("PMD.TooManyFields")
public class Method {

    private final String signature;

    private final int initialIndex;

    private final int argCount;

    private final boolean varArg;

    private final String name;

    private final boolean instance;

    private final MethodDefinition.Accessibility accessibility;

    private final boolean override;

    private final boolean overloaded;

    private final String accessiblePropertyName;

    private final boolean setter;

    private final boolean getter;

    private final boolean ctor;

    private final boolean returnsVoid;

    private final int initialLineNo;

    private final int columnNo;

    private final int length;

    public Method(MethodDefinition methodDefinition) {
        signature = methodDefinition.getSignature();
        initialIndex = methodDefinition.getIndex();
        argCount = methodDefinition.getArgCount();
        varArg = methodDefinition.isVarArg();
        name = methodDefinition.getName();
        instance = methodDefinition.isInstance();
        accessibility = methodDefinition.getAccessibility();
        override = methodDefinition.isOverride();
        overloaded = methodDefinition.isOverloaded();
        setter = methodDefinition.isSetter();
        getter = methodDefinition.isGetter();
        if (getter || setter) {
            accessiblePropertyName = methodDefinition.getAccessiblePropertyName();
        }
        else {
            accessiblePropertyName = null;
        }
        ctor = methodDefinition.isCtor();
        returnsVoid = !ctor && methodDefinition.isVoid();
        initialLineNo = methodDefinition.getLineNo();
        columnNo = methodDefinition.getAstNode().getColumnNo();
        length = methodDefinition.getLength();
    }

    public boolean isVarArg() {
        return varArg;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public int getInitialIndex() {
        return initialIndex;
    }

    public int getArgCount() {
        return argCount;
    }

    public boolean isInstance() {
        return instance;
    }

    public boolean isStatic() {
        return !instance;
    }

    public MethodDefinition.Accessibility getAccessibility() {
        return accessibility;
    }

    public boolean isOverride() {
        return override;
    }

    public boolean isOverloaded() {
        return overloaded;
    }

    public String getAccessiblePropertyName() {
        return accessiblePropertyName;
    }

    public boolean isSetter() {
        return setter;
    }

    public boolean isGetter() {
        return getter;
    }

    public boolean isCtor() {
        return ctor;
    }

    public boolean isReturnsVoid() {
        return returnsVoid;
    }

    public int getInitialLineNo() {
        return initialLineNo;
    }

    public int getColumnNo() {
        return columnNo;
    }

    public int getLength() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        else {
            return o == this || signature.equals(((Method) o).signature);
        }
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }

    @Override
    public String toString() {
        return signature;
    }
}
