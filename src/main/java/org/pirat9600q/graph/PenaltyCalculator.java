package org.pirat9600q.graph;

//CSOFF:
public class PenaltyCalculator {

    private float overrideGroupDivisionPenalty = 3;

    private float overloadGroupDivisionPenalty = 5;

    private float accessorsGroupDivisionPenalty = 3;

    private float declarationBeforeFirstUsagePenalty = 4;

    private float relativeOrderInconsistencyPenalty = 1;

    public float getPenalty(final Dependencies dep) {
        return dep.getTotalSumOfMethodDistances()
            + dep.getDeclarationBeforeUsageCases() * declarationBeforeFirstUsagePenalty
            + dep.getOverloadGroupSplitCases() * overloadGroupDivisionPenalty
            + dep.getOverrideGroupSplitCases() * overrideGroupDivisionPenalty
            + dep.getAccessorsSplitCases() * accessorsGroupDivisionPenalty
            + dep.getRelativeOrderInconsistencyCases() * relativeOrderInconsistencyPenalty;
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
}
