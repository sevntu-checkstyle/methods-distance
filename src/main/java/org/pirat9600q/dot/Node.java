package org.pirat9600q.dot;

import java.util.HashMap;
import java.util.Map;

public class Node implements Element, AttributeHolder {

    private final String id;

    private final Map<String, String> attrs = new HashMap<>();

    public Node(final String id) {
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

    public void setColor(final Color color) {
        addAttribute("color", color.asString());
    }

    public void setShape(final Shape shape) {
        addAttribute("shape", shape.asString());
    }
}
