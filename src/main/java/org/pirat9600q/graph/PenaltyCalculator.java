package org.pirat9600q.graph;

//--CSOFF:
public class PenaltyCalculator {

    public float overrideGroupDivisionPenalty = 3;

    public float overloadGroupDivisionPenalty = 5;

    public float declarationBeforeFirstUsagePenalty = 4;

    public float relativeOrderCorrespondenceViolationPenalty = 1;

    public float getPenalty(final Dependencies d) {
        return 0;
    }

//    private int getDeclarationBeforeFirstUsageOccurrencesCount(final Dependencies d) {
//        d.getMethodCalls().stream()
//                .filter(mco -> mco.getCaller().getLineDistanceTo())
//    }
}
