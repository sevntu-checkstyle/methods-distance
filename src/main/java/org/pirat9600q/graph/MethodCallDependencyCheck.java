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

public class MethodCallDependencyCheck extends Check { //SUPPRESS CHECKSTYLE, yes, its too big

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

    private Dependencies dependencies;

    private final Optional<DependencyInformationConsumer> consumer;

    public MethodCallDependencyCheck() {
        consumer = Optional.empty();
    }

    public MethodCallDependencyCheck(final DependencyInformationConsumer dic) {
        consumer = Optional.of(dic);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.METHOD_CALL, TokenTypes.METHOD_REF, TokenTypes.CLASS_DEF};
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        topLevelClass = null;
        dependencies = null;
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
            dependencies = buildDependencies(topLevelClass, methodInvocations);
            final String inputFilePath = getFileContents().getFileName();
            consumer.ifPresent(dic -> dic.accept(inputFilePath, dependencies));
        }
    }

    public Dependencies getDependencies() {
        return dependencies;
    }

    private static Dependencies buildDependencies(final DetailAST topLevelClass,
            final List<DetailAST> methodInvocations) {
        final ClassDefinition  classDefinition = new ClassDefinition(topLevelClass);
        final List<MethodCallOccurrence> callOccurrences = new ArrayList<>();
        for (final DetailAST invocation : methodInvocations) {
            if (classDefinition.isInsideMethodOfClass(invocation)) {
                final MethodCallOccurrence occurrence = tryResolveCall(classDefinition, invocation);
                if (occurrence != null) {
                    callOccurrences.add(occurrence);
                }
            }
        }
        return new Dependencies(classDefinition, callOccurrences);
    }

    private static MethodCallOccurrence tryResolveCall(
            final ClassDefinition classDefinition, final DetailAST invocation) {
        MethodCallOccurrence callOccurrence = null;
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
                            return new MethodCallOccurrence(invocation, caller, callee);
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
                        callOccurrence = new MethodCallOccurrence(invocation, caller, callee);
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
