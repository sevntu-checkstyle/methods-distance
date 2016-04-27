/*   FILE: CommonNode.java
 *   DATE OF CREATION:   Apr 4 2005
 *   AUTHOR :            Eric Mounhem (skbo@lri.fr)
 *   Copyright (c) INRIA, 2004-2005. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 * 
 * $Id: CommonNode.java 576 2007-03-29 18:32:53Z epietrig $
 */

package net.claribole.zgrviewer.dot;

import java.awt.geom.Point2D;

/**
 * This class contains a number of common attributes between pure simple nodes
 * and records
 * @author Eric Mounhem
 */
abstract class CommonNode extends Node {

    /**
     * Common denominator between BasicNodes and Records
     * 
     * @param root
     *            parent Graph
     * @param id
     *            identificator of the node
     * @throws RuntimeException
     *             Exception thrown when no ID is given
     */
    CommonNode(Object root, String id) {
        super(root, id);
    }

    /**
     * Node's group
     */
    private String           group     = "";
    /**
     * Force the usage of height and width, forgetting label's size
     */
    private boolean          fixedsize = false;
    /**
     * Tooltip annotation used with URL
     */
    private String           tooltip   = "";
    /**
     * Rotation angle in degrees
     */
    private double           rotate    = 0;
    /**
     * z coordinate
     */
    private double           z         = 0;
    /**
     * Height of node in inches
     */
    private double           height    = 0.5;
    /**
     * Width of node in inches
     */
    private double           width     = 0.75;
    /**
     * Use only the pos attribute and don't move the node anywhere
     */
    private boolean          pin       = false;
    /**
     * Show PostScript guide boxes for debugging
     */
    private int              showboxes = 0;

    /**
     * If the input graph defines this attribute, the node is polynomial, and
     * output is dot or xdot, this attribute provides the coordinates of the
     * vertices of the node's polynomial, in inches. If the node is an ellipse
     * or circle, the samplepoints attribute affects the output.
     */
    private Point2D.Double[] vertices;
    /**
     * Margins around the node (in inches)
     */
    private Point2D.Double   margin    = new Point2D.Double(0.11, 0.055);
    /**
     * Position of the node
     */
    private Point            pos;
    /**
     * Layers in which the node is present
     */
    private String[]         layer;


    protected void changeOption(String name, String value) throws Exception {
        try {
            super.changeOption(name, value);
        } catch (Exception e) {
            String v;
            if (value.charAt(0) == '"'
                    && value.charAt(value.length() - 1) == '"') {
                v = value.substring(1, value.length() - 1);
            } else
                v = value;
            // Removing \ followed by \n
            v = v.replaceAll("\\\\\\n", "");

            if (name.equals("fixedsize"))
                this.setFixedsize(DotUtils.readBoolean(v));
            else if (name.equals("group"))
                this.setGroup(v);
            else if (name.equals("height"))
                this.setHeight(DotUtils.readDouble(v));
            else if (name.equals("layer"))
                this.setLayer(v.split(((Graph) this.getRoot()).getLayersep()));
            else if (name.equals("margin"))
                this.setMargin(DotUtils.readPointf(v));
            else if (name.equals("pin"))
                this.setPin(DotUtils.readBoolean(v));
            else if (name.equals("pos"))
                this.setPos(DotUtils.readPoint(v));
            else if (name.equals("rotate")
                    || name.equals("orientation"))
                this.setRotate(DotUtils.readDouble(v));
            else if (name.equals("showboxes"))
                this.setShowboxes(DotUtils.readInteger(v));
            else if (name.equals("tooltip"))
                this.setTooltip(v);
            else if (name.equals("vertices"))
                this.setVertices(DotUtils.readPointfList(v));
            else if (name.equals("width"))
                this.setWidth(DotUtils.readDouble(v));
            else if (name.equals("z"))
                this.setZ(DotUtils.readDouble(v));
            else
                throw new Exception("CommonNode attribute \"" + name
                        + "\" does not exist");
        }
    }

    /**
     * @see Node#nodeOptions()
     */
    protected String nodeOptions() {
        String o = super.nodeOptions();
        if (!this.getGroup().equals(""))
            o += printOption("group", this.getGroup());
        if (this.isFixedsize())
            o += printOption("fixedsize", this.isFixedsize());
        if (!this.getTooltip().equals(""))
            o += printOption("tooltip", this.getTooltip());
        if (this.getRotate() != 0)
            o += printOption("orientation", this.getRotate());
        if (this.getZ() != 0)
            o += printOption("z", this.getZ());
        if (this.getHeight() != 0.5)
            o += printOption("height", this.getHeight());
        if (this.getWidth() != 0.75)
            o += printOption("width", this.getWidth());
        if (this.isPin())
            o += printOption("pin", this.isPin());
        if (this.getShowboxes() != 0)
            o += printOption("showboxes", this.getShowboxes());
        if (this.getVertices() != null)
            o += printOption("vertices", this.getVertices());
        if (this.getMargin().x != 0.11 || this.getMargin().y != 0.055)
            o += printOption("margin", this.getMargin());
        if (this.getPos() != null)
            o += printOption("pos", this.getPos());
        if (this.getLayer() != null)
            o += printOption("layer", this.getLayer());
        return o;
    }

    /**
     * Write a StringList option
     * @param attribute
     * @param value
     * @return A GraphViz StringList value
     */
    private String printOption(String attribute, String[] value) {
        String ret = attribute + "=\"";
        for (int i = 0; i < value.length; i++) {
            if (i > 0)
                ret += " ";
            ret += value[i];
        }
        return ret + "\" ";
    }

    /**
     * Write a Pointf option
     * @param attribute
     * @param value
     * @return A GraphViz Pointf value
     */
    private String printOption(String attribute, Point2D.Double value) {
        return attribute + "=" + printPoint2DOption(value) + " ";
    }

    private String printPoint2DOption(Point2D.Double value) {
        return "\"" + value.x + "," + value.y + "\"";
    }

    /**
     * Write a PointfList option
     * @param attribute
     * @param value
     * @return A GraphViz PointfList value
     */
    private String printOption(String attribute, Point2D.Double[] value) {
        String o = attribute + "=";
        for (int i = 0; i < value.length; i++) {
            if (i > 0)
                o += " ";
            o += printPoint2DOption(value[i]);
        }
        return o;
    }

    /**
     * Write a Point option
     * @param attribute
     * @param value
     * @return A GraphViz Point value
     */
    private String printOption(String attribute, Point value) {
        String o = attribute + "=\"";
        for (int i = 0; i < value.getCoords().length; i++) {
            if (i > 0)
                o += ",";
            o += value.getCoords()[i];
        }
        return o + "\"" + (value.isChange() ? "" : "!") + " ";
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isFixedsize() {
        return fixedsize;
    }

    public void setFixedsize(boolean fixedsize) {
        this.fixedsize = fixedsize;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public double getRotate() {
        return rotate;
    }

    public void setRotate(double rotate) {
        this.rotate = rotate;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public boolean isPin() {
        return pin;
    }

    public void setPin(boolean pin) {
        this.pin = pin;
    }

    public int getShowboxes() {
        return showboxes;
    }

    public void setShowboxes(int showboxes) {
        this.showboxes = showboxes;
    }

    public Point2D.Double[] getVertices() {
        return vertices;
    }

    public void setVertices(Point2D.Double[] vertices) {
        this.vertices = vertices;
    }

    public Point2D.Double getMargin() {
        return margin;
    }

    public void setMargin(Point2D.Double margin) {
        this.margin = margin;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public String[] getLayer() {
        return layer;
    }

    public void setLayer(String[] layer) {
        this.layer = layer;
    }

    /**
     * Write a Rectangle
     * @param value
     * @return A rectangle value
     */
    /*    protected String printRectangle(Rectangle value) {
     return value.x1 + "," + value.y1 + "," + value.x2 + "," + value.y2;
     }*/

}
