package org.pirat9600q.dot;

import java.util.HashMap;
import java.util.Map;

public class Edge implements Element, AttributeHolder {

    private final Node start;

    private final Node end;

    private final Map<String, String> attrs = new HashMap<>();

    public Edge(final Node start, final Node end) {
        this.start = start;
        this.end = end;
    }

    public void setLabel(final String label) {
        addAttribute("label", label);
    }

    @Override
    public String serialize() {
        if(hasAttributes()) {
            return String.format("\"%s\" -> \"%s\" %s;\n", start.getId(), end.getId(),
                serializeAttributes());
        }
        else {
            return String.format("\"%s\" -> \"%s\";", start.getId(), end.getId());
        }
    }

    @Override
    public Map<String, String> attributes() {
        return attrs;
    }
}
