/*   FILE: ArrowEnd.java
 *   DATE OF CREATION:   Apr 4 2005
 *   AUTHOR :            Eric Mounhem (skbo@lri.fr)
 *   Copyright (c) INRIA, 2004-2005. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 * 
 * $Id: ArrowEnd.java 576 2007-03-29 18:32:53Z epietrig $
 */

package net.claribole.zgrviewer.dot;

/**
 * This class defines the end of an arrow. It may be the head or the tail.
 * Basically, arrows can have multiple similar properties, like shapes or
 * labels.
 * @author Eric Mounhem
 */
public class ArrowEnd {
    /* Compass points */
    /**
     * Nort compass point
     */
    static final int       NORTH          = 0;
    /**
     * North east compass point
     */
    static final int       NORTH_EAST     = 1;
    /**
     * East compass point
     */
    static final int       EAST           = 2;
    /**
     * South east compass point
     */
    static final int       SOUTH_EAST     = 3;
    /**
     * South compass point
     */
    static final int       SOUTH          = 4;
    /**
     * South west compass point
     */
    static final int       SOUTH_WEST     = 5;
    /**
     * West compass point
     */
    static final int       WEST           = 6;
    /**
     * North west compass point
     */
    static final int       NORTH_WEST     = 7;
    /**
     * Center compass point
     */
    static final int       CENTER         = 8;

    private Shape                  innerShape;             //     = new Shape(Shape.NONE);
    private Shape                  outerShape;             //     = new Shape(Shape.NORMAL);
    private String                 URL;
    private String                 label          = "";
    private String                 target;
    private String                 tooltip        = "";
    private boolean                clip           = true;
    private int                    port           = CENTER;

    final private String[] attributeNames = { /*"", "l", "r",*/"n", "ne", "e",
            "se", "s", "sw", "w", "nw", "" };

    /**
     * @param out
     * @param in
     */
    public ArrowEnd(Shape out, Shape in) {
        this.setOuterShape(out);
        this.setInnerShape(in);
    }

    public String toString() {
        String s = "";
        if (this.getPort() != CENTER)
            s = ":" + this.attributeNames[this.getPort()];
        return s;
    }

    /**
     * Print arrow options the right way
     * @param attribute Arrows may be on head or tail
     * @return Correct name of an arrow
     */
    public String printArrowEndOptions(String attribute) {
        String o = "";
        if (!this.isClip())
            o += printOption(attribute + "clip", this.isClip());
        if (!this.getLabel().equals(""))
            o += printOption(attribute + "label", this.getLabel());
        if (this.getPort() != CENTER)
            o += printNamedOption(attribute + "port", this.getPort());

        o += printOption("arrow" + attribute, this.getOuterShape(), this.getInnerShape());

        if (this.getURL() != null)
            o += printOption(attribute + "URL", this.getURL());
        return o;
    }

    private String printOption(String attribute, Shape out, Shape in) {
        String value;
        if (out.getShape() == Shape.NONE && in.getShape() == Shape.NONE) {
            if (attribute.equals("arrowtail"))
                return "";
            value = Shape.attributeNames[Shape.NONE];
        } else
            value = (out.getShape() == Shape.NONE ? "" : out.toString())
                    + (in.getShape() == Shape.NONE ? "" : in.toString());
        if (value.equals("normal") && attribute.equals("arrowhead"))
            return "";
        return attribute + "=\"" + value + "\" ";
    }

    private String printOption(String attribute, String value) {
        return attribute + "=\"" + value + "\" ";
    }

    private String printOption(String attribute, boolean value) {
        return attribute + "=\"" + value + "\" ";
    }

    private String printNamedOption(String attribute, int i) {
        return attribute + "=\"" + this.attributeNames[i] + "\" ";
    }

    /**
     * Arrow shape located on the inside of the edge
     */
    public Shape getInnerShape() {
        return innerShape;
    }

    public void setInnerShape(Shape innerShape) {
        this.innerShape = innerShape;
    }

    /**
     * Arrow shape located on the outside of the edge
     */
    public Shape getOuterShape() {
        return outerShape;
    }

    public void setOuterShape(Shape outerShape) {
        this.outerShape = outerShape;
    }

    /**
     * Hyperlink associated to an edge's end
     */
    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    /**
     * Label placed near the edge's end
     */
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Determines the window's name to open
     */
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Tooltip annotation of this end
     */
    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Indicate if the arrow stops at the border of a node or go from/to its
     * center
     */
    public boolean isClip() {
        return clip;
    }

    public void setClip(boolean clip) {
        this.clip = clip;
    }

    /**
     * Compass point where the edge must be aimed
     */
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
