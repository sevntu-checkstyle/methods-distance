package org.pirat9600q.graph;

import com.google.common.collect.ImmutableSet;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MethodCallDependencyCheck extends Check {

    private static final int DEFAULT_SCREEN_LINES_COUNT = 50;

    private static final Set<Integer> PRIMITIVE_TOKEN_TYPES = ImmutableSet.of(
            TokenTypes.LITERAL_VOID,
            TokenTypes.LITERAL_BOOLEAN,
            TokenTypes.LITERAL_CHAR,
            TokenTypes.LITERAL_BYTE,
            TokenTypes.LITERAL_SHORT,
            TokenTypes.LITERAL_INT,
            TokenTypes.LITERAL_LONG,
            TokenTypes.LITERAL_DOUBLE
    );

    private DetailAST topLevelClass;

    private List<DetailAST> methodInvocations = new ArrayList<>();

    private final Optional<DependencyInformationConsumer> consumer;

    private int screenLinesCount = DEFAULT_SCREEN_LINES_COUNT;

    public MethodCallDependencyCheck() {
        consumer = Optional.empty();
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

    private static Dependencies buildDependencies(final DetailAST topLevelClass,
            final List<DetailAST> methodInvocations, final int screenLinesCount) {
        final ClassDefinition  classDefinition = new ClassDefinition(topLevelClass);
        final List<ResolvedCall> callOccurrences = new ArrayList<>();
        for (final DetailAST invocation : methodInvocations) {
            if (classDefinition.isInsideMethodOfClass(invocation)) {
                final ResolvedCall occurrence = tryResolveCall(classDefinition, invocation);
                if (occurrence != null) {
                    callOccurrences.add(occurrence);
                }
            }
        }
        return new Dependencies(classDefinition, callOccurrences, screenLinesCount);
    }

    private static ResolvedCall tryResolveCall(
            final ClassDefinition classDefinition, final DetailAST invocation) {
        ResolvedCall callOccurrence = null;
        switch (invocation.getType()) {
            case TokenTypes.METHOD_CALL:
                final MethodCall mc = new MethodCall(classDefinition, invocation);
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
                throw new RuntimeException("Expected METHOD_CALL or METHOD_REF, "
                    + "got " + TokenUtils.getTokenName(invocation.getType()));
        }
        return callOccurrence;
    }
}
