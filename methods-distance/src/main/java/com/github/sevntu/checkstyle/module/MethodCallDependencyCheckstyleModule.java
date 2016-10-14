package com.github.sevntu.checkstyle.module;

import com.github.sevntu.checkstyle.domain.ClassDefinition;
import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.domain.MethodCall;
import com.github.sevntu.checkstyle.domain.MethodDefinition;
import com.github.sevntu.checkstyle.domain.RefCall;
import com.github.sevntu.checkstyle.domain.ResolvedCall;
import com.github.sevntu.checkstyle.common.UnexpectedTokenTypeException;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MethodCallDependencyCheckstyleModule extends AbstractCheck {

    public static final String MSG_KEY = "method.call.dependencies.moveMethod";

    private Optional<DetailAST> topLevelClass;

    private List<DetailAST> methodInvocations = new ArrayList<>();

    private final Optional<DependencyInformationConsumer> consumer;

    public MethodCallDependencyCheckstyleModule(DependencyInformationConsumer dic) {
        consumer = Optional.of(dic);
    }

    /**
     * Estimated line count of source code that fit on screen at once.
     *
     * @param screenLinesCount
     */
    public void setScreenLinesCount(int screenLinesCount) {
        // This setter is created only to allow putting this property to CS module config.
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.METHOD_CALL, TokenTypes.METHOD_REF, TokenTypes.CLASS_DEF};
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        topLevelClass = Optional.empty();
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
                if (!topLevelClass.isPresent()) {
                    topLevelClass = Optional.of(ast);
                }
                break;
            default:
                throw new UnexpectedTokenTypeException(ast);
        }
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        topLevelClass.ifPresent(enclosingClass ->
            consumer.ifPresent(informationConsumer -> {
                final Dependencies dependencies =
                    buildDependencies(enclosingClass, methodInvocations);
                final String inputFilePath =
                    new File(getFileContents().getFileName()).toURI().getPath();
                informationConsumer.accept(inputFilePath, dependencies);
            }));
    }

    private static Dependencies buildDependencies(DetailAST topLevelClass,
            List<DetailAST> methodInvocations) {

        final ClassDefinition classDefinition = new ClassDefinition(topLevelClass);
        final List<ResolvedCall> callOccurrences = new ArrayList<>();
        for (final DetailAST invocation : methodInvocations) {
            if (classDefinition.isInsideMethodOfClass(invocation)) {
                final Optional<ResolvedCall> occurrence =
                    tryResolveCall(classDefinition, invocation);
                occurrence.ifPresent(callOccurrences::add);
            }
        }
        return new Dependencies(classDefinition, callOccurrences);
    }

    private static Optional<ResolvedCall> tryResolveCall(
        ClassDefinition classDefinition, DetailAST invocation) {

        switch (invocation.getType()) {
            case TokenTypes.METHOD_CALL: return tryResolveMethodCall(classDefinition, invocation);
            case TokenTypes.METHOD_REF: return tryResolveRefCall(classDefinition, invocation);
            default:
                throw new IllegalArgumentException("Expected METHOD_CALL or METHOD_REF, "
                    + "got " + TokenUtils.getTokenName(invocation.getType()));
        }
    }

    private static Optional<ResolvedCall> tryResolveMethodCall(
        ClassDefinition classDefinition, DetailAST callNode) {
        final MethodCall mc = new MethodCall(callNode);
        return Optional.of(mc)
            .filter(MethodCall::isThisClassMethodCall)
            .flatMap(call -> classDefinition.getMethodsByName(call.getMethodName())
                .stream()
                .filter(method ->
                    method.isVarArg() && method.getArgCount() <= call.getArgCount()
                        || call.getArgCount() == method.getArgCount())
                .findFirst()
                .map(callee -> {
                    final MethodDefinition caller = classDefinition.getMethodByAstNode(
                        call.getEnclosingMethod());
                    return new ResolvedCall(callNode, caller, callee);
                }));
    }

    private static Optional<ResolvedCall> tryResolveRefCall(
        ClassDefinition classDefinition, DetailAST refCallNode) {

        final RefCall call = new RefCall(classDefinition, refCallNode);
        return Optional.of(call)
            .filter(RefCall::isRefToMethodOfEnclosingClass)
            .map(refCall -> refCall.isRefToStaticMethodOfEnclosingClass()
                ? classDefinition.getStaticMethodsByName(refCall.getMethodName())
                : classDefinition.getInstanceMethodsByName(refCall.getMethodName()))
            .filter(list -> !list.isEmpty())
            .map(possibleMethods -> {
                final MethodDefinition callee = possibleMethods.get(0);
                final MethodDefinition caller =
                    classDefinition.getMethodByAstNode(call.getEnclosingMethod());
                return new ResolvedCall(refCallNode, caller, callee);
            });
    }
}
