package org.pirat9600q.analysis;

public class PenaltyCalculator {

    public static final int DEFAULT_OVERRIDE_GROUP_DIVISION_PENALTY = 3;

    public static final int DEFAULT_OVERLOAD_GROUP_DIVISION_PENALTY = 5;

    public static final int DEFAULT_ACCESSOR_GROUP_DIVISION_PENALTY = 3;

    public static final int DEFAULT_DECLARATION_BEFORE_USAGE_PENALTY = 4;

    public static final int DEFAULT_RELATIVE_ORDER_INCONSISTENCY_PENALTY = 1;

    public static final int DEFAULT_DISTANT_METHODS_DEPENDENCIES_PENALTY = 3;

    private float overrideGroupDivisionPenalty = DEFAULT_OVERRIDE_GROUP_DIVISION_PENALTY;

    private float overloadGroupDivisionPenalty = DEFAULT_OVERLOAD_GROUP_DIVISION_PENALTY;

    private float accessorsGroupDivisionPenalty = DEFAULT_ACCESSOR_GROUP_DIVISION_PENALTY;

    private float declarationBeforeFirstUsagePenalty = DEFAULT_DECLARATION_BEFORE_USAGE_PENALTY;

    private float relativeOrderInconsistencyPenalty = DEFAULT_RELATIVE_ORDER_INCONSISTENCY_PENALTY;

    private float dependenciesBetweenDistantMethodsPenalty =
        DEFAULT_DISTANT_METHODS_DEPENDENCIES_PENALTY;

    public float getPenalty(final Dependencies dep) {
        return dep.getTotalSumOfMethodDistances()
            + dep.getDeclarationBeforeUsageCases() * declarationBeforeFirstUsagePenalty
            + dep.getOverloadGroupSplitCases() * overloadGroupDivisionPenalty
            + dep.getOverrideGroupSplitCases() * overrideGroupDivisionPenalty
            + dep.getAccessorsSplitCases() * accessorsGroupDivisionPenalty
            + dep.getRelativeOrderInconsistencyCases() * relativeOrderInconsistencyPenalty
            + dep.getDependenciesBetweenDistantMethodsCases()
                * dependenciesBetweenDistantMethodsPenalty;
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
