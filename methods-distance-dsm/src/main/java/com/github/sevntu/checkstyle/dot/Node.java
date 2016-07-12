package com.github.sevntu.checkstyle.dot;

import java.util.HashMap;
import java.util.Map;

/* Class name is sufficiently descriptive. */
@SuppressWarnings("PMD.ShortClassName")
public class Node implements Element, AttributeHolder {

    private final String id;

    private final Map<String, String> attrs = new HashMap<>();

    public Node(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String serialize() {
        if (hasAttributes()) {
            return String.format("\"%s\" %s;\n", id, serializeAttributes());
        }
        else {
            return String.format("\"%s\";\n", id);
        }
    }

    @Override
    public Map<String, String> attributes() {
        return attrs;
    }

    public void setColor(Colors color) {
        addAttribute("color", color.asString());
    }

    public void setShape(Shapes shape) {
        addAttribute("shape", shape.asString());
    }
}
