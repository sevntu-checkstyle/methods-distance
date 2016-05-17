package com.github.sevntu.checkstyle.ordering;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// CSOFF: This class is experimental
public class TspMethodSorter implements MethodSorter {

    private static final float INITIAL_DISTANCE = 10;

    public Ordering sort(final Ordering ordering) {
        final Map<MethodPair, Float> distance = getDistanceMap(ordering);
        final List<Method> chain = new ArrayList<>();
        while (chain.size() < ordering.getMethods().size()) {
            final Optional<MethodPair> maybePair = distance.entrySet().stream()
                .filter(e -> {
                    if (chain.isEmpty()) {
                        return true;
                    }
                    else {
                        final MethodPair methodPair = e.getKey();
                        final boolean canBePrepended = methodPair.getTo().equals(chain.get(0))
                            && !chain.contains(methodPair.getFrom());
                        final boolean canBeAppended =
                            methodPair.getFrom().equals(chain.get(chain.size() - 1))
                                && !chain.contains(methodPair.getTo());
                        return canBePrepended || canBeAppended;
                    }
                })
                .sorted((lhs, rhs) -> Float.compare(lhs.getValue(), rhs.getValue()))
                .findFirst()
                .map(Map.Entry::getKey);
            if (maybePair.isPresent()) {
                final MethodPair pair = maybePair.get();
                if(chain.isEmpty()) {
                    chain.add(pair.getFrom());
                    chain.add(pair.getTo());
                }
                else if (chain.get(0).equals(pair.getTo())) {
                    chain.add(0, pair.getFrom());
                }
                else if (chain.get(chain.size() - 1).equals(pair.getFrom())) {
                    chain.add(pair.getTo());
                }
                else {
                    throw new IllegalStateException("Pair " + pair + " can`t be added to chain "
                        + chain.stream().map(Object::toString).collect(Collectors.joining("; ")));
                }
            }
            else {
                throw new IllegalStateException("Pair not found!");
            }
        }
        return ordering.reorder(chain);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private static Map<MethodPair, Float> getDistanceMap(final Ordering ordering) {
        final Map<MethodPair, Float> distances = new HashedMap<>();
        for (final Method from : ordering.getMethods()) {
            for (final Method to : ordering.getMethods()) {
                if (!from.equals(to)) {
                    float distance = INITIAL_DISTANCE;
                    final boolean fromCallsTo = ordering.isMethodDependsOn(from, to);
                    final boolean toCallsFrom = ordering.isMethodDependsOn(to, from);
                    if (fromCallsTo) {
                        distance *= 0.3;
                    }
                    else if (toCallsFrom) {
                        distance *= 0.9;
                    }
                    if (from.isOverride() && to.isOverride()) {
                        distance *= 0.8;
                    }
                    if (from.isOverloaded() && to.isOverloaded()
                        && from.getName().equals(to.getName())) {
                        distance *= 0.7;
                    }
                    distances.put(new MethodPair(from, to), distance);
                }
            }
        }
        return distances;
    }

    private static class MethodPair {

        private final Method from;

        private final Method to;

        public MethodPair(final Method from, final Method to) {
            this.from = from;
            this.to = to;
        }

        public Method getFrom() {
            return from;
        }

        public Method getTo() {
            return to;
        }

        @Override
        public boolean equals(final Object o) {
            if(o == null || o.getClass() != this.getClass()) {
                return false;
            }
            else if(o == this) {
                return true;
            }
            else {
                final MethodPair mp = (MethodPair) o;
                return from.equals(mp.from) && to.equals(mp.to);
            }
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                .append(from)
                .append(to)
                .toHashCode();
        }

        @Override
        public String toString() {
            return String.format("\"%s\" : \"%s\"", from, to);
        }
    }
}
