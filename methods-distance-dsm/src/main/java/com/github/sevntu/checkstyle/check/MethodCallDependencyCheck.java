package com.github.sevntu.checkstyle.check;

import com.github.sevntu.checkstyle.analysis.ClassDefinition;
import com.github.sevntu.checkstyle.analysis.Dependencies;
import com.github.sevntu.checkstyle.analysis.DependencyInformationConsumer;
import com.github.sevntu.checkstyle.analysis.MethodCall;
import com.github.sevntu.checkstyle.analysis.MethodDefinition;
import com.github.sevntu.checkstyle.analysis.RefCall;
import com.github.sevntu.checkstyle.analysis.ResolvedCall;
import com.github.sevntu.checkstyle.analysis.UnexpectedTokenTypeException;
import com.github.sevntu.checkstyle.ordering.Ordering;
import com.github.sevntu.checkstyle.reordering.MethodReorderer;
import com.github.sevntu.checkstyle.reordering.TopologicalMethodReorderer;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MethodCallDependencyCheck extends AbstractCheck {

    private static final String MSG_KEY = "method.call.dependencies.moveMethod";

    private static final int DEFAULT_SCREEN_LINES_COUNT = 50;

    private DetailAST topLevelClass;

    private List<DetailAST> methodInvocations = new ArrayList<>();

    private final Optional<DependencyInformationConsumer> consumer;

    private int screenLinesCount = DEFAULT_SCREEN_LINES_COUNT;

    public MethodCallDependencyCheck() {
        consumer = Optional.of(new ViolationReporterDependencyInformationConsumer());
    }

    public MethodCallDependencyCheck(final DependencyInformationConsumer dic) {
        consumer = Optional.of(dic);
    }

    public void setScreenLinesCount(final int screenLinesCount) {
        this.screenLinesCount = screenLinesCount;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.METHOD_CALL, TokenTypes.METHOD_REF, TokenTypes.CLASS_DEF};
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        topLevelClass = null;
        methodInvocations.clear();
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_CALL:
            case TokenTypes.METHOD_REF:
                methodInvocations.add(ast);
                break;
            case TokenTypes.CLASS_DEF:
                if (topLevelClass == null) {
                    topLevelClass = ast;
                }
                break;
            default:
                throw new UnexpectedTokenTypeException(ast);
        }
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        if (topLevelClass != null) {
            consumer.ifPresent(dic -> {
                final Dependencies dependencies =
                    buildDependencies(topLevelClass, methodInvocations, screenLinesCount);
                final String inputFilePath = getFileContents().getFileName();
                dic.accept(inputFilePath, dependencies);
            });
        }
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private static Dependencies buildDependencies(final DetailAST topLevelClass,
            final List<DetailAST> methodInvocations, final int screenLinesCount) {
        final ClassDefinition classDefinition = new ClassDefinition(topLevelClass);
        final List<ResolvedCall> callOccurrences = new ArrayList<>();
        for (final DetailAST invocation : methodInvocations) {
            if (classDefinition.isInsideMethodOfClass(invocation)) {
                final ResolvedCall occurrence = tryResolveCall(classDefinition, invocation);
                if (occurrence != null) {
                    callOccurrences.add(occurrence);
                }
            }
        }
        return new Dependencies(classDefinition, callOccurrences);
    }

    private static ResolvedCall tryResolveCall(
            final ClassDefinition classDefinition, final DetailAST invocation) {
        ResolvedCall callOccurrence = null;
        switch (invocation.getType()) {
            case TokenTypes.METHOD_CALL:
                final MethodCall mc = new MethodCall(invocation);
                if (mc.isThisClassMethodCall()) {
                    callOccurrence = classDefinition.getMethodsByName(mc.getMethodName())
                        .stream()
                        .filter(method ->
                                method.isVarArg() && method.getArgCount() <= mc.getArgCount()
                                        || mc.getArgCount() == method.getArgCount())
                        .findFirst()
                        .map(callee -> {
                            final MethodDefinition caller = classDefinition.getMethodByAstNode(
                                    mc.getEnclosingMethod());
                            return new ResolvedCall(invocation, caller, callee);
                        })
                        .orElse(null);
                }
                break;
            case TokenTypes.METHOD_REF:
                final RefCall call = new RefCall(classDefinition, invocation);
                if (call.isRefToMethodOfEnclosingClass()) {
                    final List<MethodDefinition> possibleMethods =
                            call.isRefToStaticMethodOfEnclosingClass()
                            ? classDefinition.getStaticMethodsByName(call.getMethodName())
                            : classDefinition.getInstanceMethodsByName(call.getMethodName());
                    if (!possibleMethods.isEmpty()) {
                        final MethodDefinition callee = possibleMethods.get(0);
                        final MethodDefinition caller =
                                classDefinition.getMethodByAstNode(call.getEnclosingMethod());
                        callOccurrence = new ResolvedCall(invocation, caller, callee);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Expected METHOD_CALL or METHOD_REF, "
                    + "got " + TokenUtils.getTokenName(invocation.getType()));
        }
        return callOccurrence;
    }

    private final class ViolationReporterDependencyInformationConsumer
        implements DependencyInformationConsumer {

        private final MethodReorderer reorderer = new TopologicalMethodReorderer();

        @Override
        public void accept(final String filePath, final Dependencies dependencies) {
            final Ordering initialOrdering = new Ordering(dependencies);
            final Ordering optimizedOrdering = reorderer.reorder(initialOrdering);
            logFirstMethodOutOfOrder(optimizedOrdering);
        }

        private void logFirstMethodOutOfOrder(final Ordering optimizedOrdering) {
            optimizedOrdering.getMethods().stream()
                .filter(method ->
                    optimizedOrdering.getMethodIndex(method) != method.getInitialIndex())
                .findFirst()
                .ifPresent(method -> {
                    final int difference =
                        method.getInitialIndex() - optimizedOrdering.getMethodIndex(method);
                    log(method.getInitialLineNo(), MSG_KEY, method.getSignature(), difference);
                });
        }
    }
}
