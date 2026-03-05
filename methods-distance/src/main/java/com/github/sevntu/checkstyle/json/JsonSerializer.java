package com.github.sevntu.checkstyle.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.sevntu.checkstyle.domain.ClassDefinition;
import com.github.sevntu.checkstyle.domain.Dependencies;
import com.github.sevntu.checkstyle.domain.MethodDefinition;
import com.github.sevntu.checkstyle.domain.ResolvedCall;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

public final class JsonSerializer {

    private JsonSerializer() {
        // no code
    }

    public static void writeToFile(String javaSource, Dependencies dependencies,
                                   final Configuration config, final String fileName) {

        try (PrintWriter file = new PrintWriter(new File(fileName))) {
            file.write(serialize(dependencies, javaSource, config));
        } catch (final CheckstyleException | FileNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static String serialize(Dependencies dependencies, String javaSource, Configuration config) throws CheckstyleException {
        ClassDefinition classDefinition = dependencies.getClassDefinition();
        List<ResolvedCall> resolvedCalls = dependencies.getResolvedCalls();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        ObjectNode classDefinitionNode = mapper.createObjectNode();
        classDefinitionNode.put("className", classDefinition.getClassName());

        ArrayNode methodsNode = mapper.createArrayNode();
        for (MethodDefinition methodDefinition : classDefinition.getMethods()) {
            ObjectNode methodNode = mapper.createObjectNode();

            methodNode.put("index", methodDefinition.getIndex());
            methodNode.put("name", methodDefinition.getName());
            methodNode.put("lineNo", methodDefinition.getLineNo());
            methodNode.put("length", methodDefinition.getLength());
            methodNode.put("signature", methodDefinition.getSignature());
            methodNode.put("accessibility",
                methodDefinition.getAccessibility().toString());
            methodNode.put("argCount", methodDefinition.getArgCount());
            methodNode.put("accessiblePropertyName",
                methodDefinition.getAccessiblePropertyName());
            methodNode.put("isCtor", methodDefinition.isCtor());
            methodNode.put("isVoid", methodDefinition.isVoid());
            methodNode.put("isVarArg", methodDefinition.isVarArg());
            methodNode.put("isStatic", methodDefinition.isStatic());
            methodNode.put("isInstance", methodDefinition.isInstance());
            methodNode.put("isOverride", methodDefinition.isOverride());
            methodNode.put("isOverloaded", methodDefinition.isOverloaded());
            methodNode.put("isSetter", methodDefinition.isSetter());
            methodNode.put("isGetter", methodDefinition.isGetter());
            methodsNode.add(methodNode);
        }
        classDefinitionNode.set("methods", methodsNode);

        root.set("classDefinition", classDefinitionNode);

        ArrayNode resolvedCallsNode = mapper.createArrayNode();
        for (ResolvedCall resolvedCall : resolvedCalls) {
            ObjectNode resolvedCallNode = mapper.createObjectNode();

            resolvedCallNode.put("callerIndex", resolvedCall.getCaller().getIndex());
            resolvedCallNode.put("calleeIndex", resolvedCall.getCallee().getIndex());
            resolvedCallNode.put("isMethodRef", resolvedCall.isMethodRef());

            resolvedCallsNode.add(resolvedCallNode);
        }
        root.set("resolvedCalls", resolvedCallsNode);

        try {
            return mapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize JSON", e);
        }
    }
}
