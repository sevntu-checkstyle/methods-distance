////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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

package com.github.sevntu.checkstyle.ordering;

public class PenaltyCalculator {

    public static final int DEFAULT_CTOR_GROUP_DIVISION_PENALTY = 4;

    public static final int DEFAULT_OVERRIDE_GROUP_DIVISION_PENALTY = 3;

    public static final int DEFAULT_OVERLOAD_GROUP_DIVISION_PENALTY = 5;

    public static final int DEFAULT_ACCESSOR_GROUP_DIVISION_PENALTY = 3;

    public static final int DEFAULT_DECLARATION_BEFORE_USAGE_PENALTY = 4;

    public static final int DEFAULT_RELATIVE_ORDER_INCONSISTENCY_PENALTY = 1;

    public static final int DEFAULT_DISTANT_METHODS_DEPENDENCIES_PENALTY = 3;

    private float ctorGroupDivisionPenalty = DEFAULT_CTOR_GROUP_DIVISION_PENALTY;

    private float overrideGroupDivisionPenalty = DEFAULT_OVERRIDE_GROUP_DIVISION_PENALTY;

    private float overloadGroupDivisionPenalty = DEFAULT_OVERLOAD_GROUP_DIVISION_PENALTY;

    private float accessorsGroupDivisionPenalty = DEFAULT_ACCESSOR_GROUP_DIVISION_PENALTY;

    private float declarationBeforeFirstUsagePenalty = DEFAULT_DECLARATION_BEFORE_USAGE_PENALTY;

    private float relativeOrderInconsistencyPenalty = DEFAULT_RELATIVE_ORDER_INCONSISTENCY_PENALTY;

    private float dependenciesBetweenDistantMethodsPenalty =
        DEFAULT_DISTANT_METHODS_DEPENDENCIES_PENALTY;

    public float getPenalty(MethodOrder dep, int screenLinesCount) {
        return dep.getTotalSumOfMethodDistances()
            + dep.getDeclarationBeforeUsageCases() * declarationBeforeFirstUsagePenalty
            + dep.getCtorGroupsSplitCases() * ctorGroupDivisionPenalty
            + dep.getOverloadGroupsSplitCases() * overloadGroupDivisionPenalty
            + dep.getOverrideGroupSplitCases() * overrideGroupDivisionPenalty
            + dep.getAccessorsSplitCases() * accessorsGroupDivisionPenalty
            + dep.getRelativeOrderInconsistencyCases() * relativeOrderInconsistencyPenalty
            + dep.getDependenciesBetweenDistantMethodsCases(screenLinesCount)
                * dependenciesBetweenDistantMethodsPenalty;
    }

    public float getCtorGroupDivisionPenalty() {
        return ctorGroupDivisionPenalty;
    }

    public void setCtorGroupDivisionPenalty(float ctorGroupDivisionPenalty) {
        this.ctorGroupDivisionPenalty = ctorGroupDivisionPenalty;
    }

    public float getOverrideGroupDivisionPenalty() {
        return overrideGroupDivisionPenalty;
    }

    public void setOverrideGroupDivisionPenalty(float overrideGroupDivisionPenalty) {
        this.overrideGroupDivisionPenalty = overrideGroupDivisionPenalty;
    }

    public float getOverloadGroupDivisionPenalty() {
        return overloadGroupDivisionPenalty;
    }

    public void setOverloadGroupDivisionPenalty(float overloadGroupDivisionPenalty) {
        this.overloadGroupDivisionPenalty = overloadGroupDivisionPenalty;
    }

    public float getDeclarationBeforeFirstUsagePenalty() {
        return declarationBeforeFirstUsagePenalty;
    }

    public void setDeclarationBeforeFirstUsagePenalty(float declarationBeforeFirstUsagePenalty) {
        this.declarationBeforeFirstUsagePenalty = declarationBeforeFirstUsagePenalty;
    }

    public float getRelativeOrderInconsistencyPenalty() {
        return relativeOrderInconsistencyPenalty;
    }

    public void setRelativeOrderInconsistencyPenalty(float relativeOrderInconsistencyPenalty) {
        this.relativeOrderInconsistencyPenalty = relativeOrderInconsistencyPenalty;
    }

    public float getAccessorsGroupDivisionPenalty() {
        return accessorsGroupDivisionPenalty;
    }

    public void setAccessorsGroupDivisionPenalty(float accessorsGroupDivisionPenalty) {
        this.accessorsGroupDivisionPenalty = accessorsGroupDivisionPenalty;
    }

    public float getDependenciesBetweenDistantMethodsPenalty() {
        return dependenciesBetweenDistantMethodsPenalty;
    }

    public void setDependenciesBetweenDistantMethodsPenalty(
            float dependenciesBetweenDistantMethodsPenalty) {

        this.dependenciesBetweenDistantMethodsPenalty = dependenciesBetweenDistantMethodsPenalty;
    }
}
