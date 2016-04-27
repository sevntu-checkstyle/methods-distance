/*   FILE: BasicNode.java
 *   DATE OF CREATION:   Apr 4 2005
 *   AUTHOR :            Eric Mounhem (skbo@lri.fr)
 *   Copyright (c) INRIA, 2004-2005. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 * 
 * $Id: BasicNode.java 576 2007-03-29 18:32:53Z epietrig $
 */

package net.claribole.zgrviewer.dot;

import java.util.Vector;


/**
 * Simple kind of node used as default in GraphViz
 * @author Eric Mounhem
 */
public class BasicNode extends CommonNode {
    /*
     * CIRCLE = ELLIPSE + regular
     * PARALLELOGRAM = POLYGON + sides=4 + skew=0.6
     * TRIANGLE = POLYGON + sides=3
     * [HEX|OCT|SEPT|...]AGON = POLYGON + sides=[6|8|7|...] 
     * [DOUBLE|TRIPLE]x = x + peripheries=[3|4]
     * DIAMOND = POLYGON + regular + orientation=45
     */
    /* Polygons */
    /**
     * Use the shapefile attribute for a shape
     */
    final static int      USER           = 0;
    /**
     * No node, only the label (or ID) of the node
     */
    public final static int      NONE           = 1;
    /**
     * Ellipse node shape
     */
    public final static int      ELLIPSE        = 2;
    /**
     * Polygon node shape
     */
    public final static int      POLYGON        = 3;
    /**
     * Point node shape
     */
    public final static int      POINT          = 4;
    /**
     * Egg node shape
     */
    public final static int      EGG            = 5;
    /**
     * Diamond node shape
     */
    //final static int       DIAMOND        = 6;
    /**
     * Trapezium node shape
     */
    public final static int      TRAPEZIUM      = 6;
    /**
     * House node shape
     */
    public final static int      HOUSE          = 7;
    /**
     * Inverted triangle node shape
     */
    public final static int      INVTRIANGLE    = 8;
    /**
     * Inverted trapezium node shape
     */
    public final static int      INVTRAPEZIUM   = 9;
    /**
     * Inverted house node shape
     */
    public final static int      INVHOUSE       = 10;

    private final static String[] attributeNames = { "user", "none", "ellipse",
            "polygon", "point", "egg", /*"diamond",*/"trapezium", "house",
            "invtriangle", "invtrapezium", "invhouse" };

    /**
     * Type of node shape. One of the following:
     * <ul>
     * <li>USER</li>
     * <li>NONE</li>
     * <li>ELLIPSE</li>
     * <li>POLYGON</li>
     * <li>POINT</li>
     * <li>EGG</li>
     * <li>DIAMOND (not anymore)</li>
     * <li>TRAPEZIUM</li>
     * <li>PARALLELOGRAM</li>
     * <li>HOUSE</li>
     * <li>INVTRIANGLE</li>
     * <li>INVTRAPEZIUM</li>
     * <li>INVHOUSE</li>
     * </ul>
     */
    private int                   shape          = ELLIPSE;
    /**
     * File containing description of a user-defined shape
     */
    private String                shapefile      = "";

    /**
     * Number of polygon's size
     */
    private int                   sides          = 4;
    /**
     * Polygon's distortion factor
     */
    private double                distortion     = 0;
    /**
     * Skew factor
     */
    private double                skew           = 0;
    /**
     * Geometric shape forced to be regular
     */
    private boolean               regular        = false;
    /**
     * Rectangles for fields of records (in points)
     */
    private Rectangle             rects;

    /**
     * Create a default node type in GraphViz
     * 
     * @param root
     *            parent graph
     * @param id
     *            identificator of the node
     * @throws RuntimeException
     *             Exception thrown when no ID is given
     */
    public BasicNode(Object root, String id) {
        super(root, id);
        this.setLabel(id);

        this.getGenericAttributes();
    }

    public static String[] getAttributeNames() {
        return attributeNames;
    }

    private void getAttributes(BasicNode node) {
        if (this.getDistortion() != node.getDistortion())
            this.setDistortion(node.getDistortion());
        if (this.getRects() != null && node.getRects() != null)
            if (!this.getRects().equals(node.getRects())) // FIXME: handle Rectangle comparisons
                this.setRects(node.getRects());
        if (this.isRegular() != node.isRegular())
            this.setRegular(node.isRegular());
        if (this.getShape() != node.getShape())
            this.setShape(node.getShape());
        if (this.getSides() != node.getSides())
            this.setSides(node.getSides());
        if (this.getSkew() != node.getSkew())
            this.setSkew(node.getSkew());

        // CommonNode attributes
        if (this.isFixedsize() != node.isFixedsize())
            this.setFixedsize(node.isFixedsize());
        if (!this.getGroup().equals(node.getGroup()))
            this.setGroup(node.getGroup());
        if (this.getHeight() != node.getHeight())
            this.setHeight(node.getHeight());
        if (this.getLayer() != null && node.getLayer() != null)
            if (!this.getLayer().equals(node.getLayer())) // FIXME: handle arrays comparisons
                this.setLayer(node.getLayer());
        if (!this.getMargin().equals(node.getMargin()))
            this.setMargin(node.getMargin());
        if (this.isPin() != node.isPin())
            this.setPin(node.isPin());
        if (this.getPos() != null && node.getPos() != null)
            if (!this.getPos().equals(node.getPos())) // FIXME: handle splines comparisons
                this.setPos(node.getPos());
        if (this.getRotate() != node.getRotate())
            this.setRotate(node.getRotate());
        if (this.getShowboxes() != node.getShowboxes())
            this.setShowboxes(node.getShowboxes());
        if (!this.getTooltip().equals(node.getTooltip()))
            this.setTooltip(node.getTooltip());
        if (this.getVertices() != null && node.getVertices() != null)
            if (!this.getVertices().equals(node.getVertices())) // FIXME: handle arrays comparisons
                this.setVertices(node.getVertices());
        if (this.getWidth() != node.getWidth())
            this.setWidth(node.getWidth());
        if (this.getZ() != node.getZ())
            this.setZ(node.getZ());

        // Node attributes
        if (this.getColor() == null)
            this.setColor(node.getColor());
        else if (!this.getColor().equals(node.getColor()))
            this.setColor(node.getColor());
        if (this.getFillcolor() == null)
            this.setFillcolor(node.getFillcolor());
        else if (!this.getFillcolor().equals(node.getFillcolor()))
            this.setFillcolor(node.getFillcolor());
        if (this.getFontcolor() == null)
            this.setFontcolor(node.getFontcolor());
        else if (!this.getFontcolor().equals(node.getFontcolor()))
            this.setFontcolor(node.getFontcolor());
        if (!this.getFontname().equals(node.getFontname()))
            this.setFontname(node.getFontname());
        if (this.getFontsize() != node.getFontsize())
            this.setFontsize(node.getFontsize());
        if (!this.getLabel().equals(node.getLabel()) && !node.getLabel().equals("node"))
            this.setLabel(node.getLabel());
        if (this.isNojustify() != node.isNojustify())
            this.setNojustify(node.isNojustify());
        if (this.getPeripheries() != node.getPeripheries())
            this.setPeripheries(node.getPeripheries());
        if (!this.getStyle().equals(node.getStyle()))
            this.setStyle(node.getStyle());
        if (this.getTarget() != null && node.getTarget() != null)
            if (!this.getTarget().equals(node.getTarget()))
                this.setTarget(node.getTarget());
        if (this.getURL() != null && node.getURL() != null)
            if (!this.getURL().equals(node.getURL()))
                this.setURL(node.getURL());
    }

    private void getGenericAttributes() {
        Object rootGraph = this.getRoot();
        Vector roots = new Vector();

        while (rootGraph instanceof SubGraph) {
            roots.add(rootGraph);
            rootGraph = ((SubGraph) rootGraph).getRoot();
        }

        if (((Graph) rootGraph).getGenericNode() != null)
            getAttributes(((Graph) rootGraph).getGenericNode());

        for (int i = roots.size() - 1; i == 0; i--) {
            BasicNode generic = ((SubGraph) roots.get(i)).getGenericNode();
            if (generic != null)
                getAttributes(generic);
        }
    }

    protected void changeOption(String name, String value) {
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

            if (name.equals("distortion"))
                this.setDistortion(DotUtils.readDouble(v));
            else if (name.equals("rect")) // FIXME: only to test if a Record is not used as a BasicNode
                this.setRects(DotUtils.readRectangle(v));
            else if (name.equals("regular"))
                this.setRegular(DotUtils.readBoolean(v));
            else if (name.equals("shape")) {
                if (v.endsWith("record"))
                    ((Graph) this.getRoot()).setRecord(true);
                DotUtils.readShape(this, v);
            } else if (name.equals("shapefile"))
                this.setShapefile(v);
            else if (name.equals("sides"))
                this.setSides(DotUtils.readInteger(v));
            else if (name.equals("skew"))
                this.setSkew(DotUtils.readDouble(v));
            else
                System.err.println("BasicNode attribute \"" + name
                        + "\" does not exist");
        }
    }

    /**
     * @see net.claribole.zgrviewer.dot.CommonNode#nodeOptions()
     */
    protected String nodeOptions() {
        String o = super.nodeOptions();
        if (!this.getShapefile().equals(""))
            o += printOption("shapefile", this.getShapefile());
        if (this.getSides() != 4)
            o += printOption("sides", this.getSides());
        if (this.getDistortion() != 0)
            o += printOption("distortion", this.getDistortion());
        if (this.getSkew() != 0)
            o += printOption("skew", this.getSkew());
        if (this.isRegular())
            o += printOption("regular", this.isRegular());
        if (this.getShape() != ELLIPSE)
            o += printNamedOption("shape", this.getShape());
        if (this.getRects() != null)
            o += printOption("rects", this.getRects());
        return o;
    }

    /**
     * Write a Rect option
     * @param attribute
     * @param value
     * @return A GraphViz Rect value
     */
    private String printOption(String attribute, Rectangle value) {
        return attribute + "=\"" + /*printRectangle(*/value/*)*/+ "\" ";
    }

    private String printNamedOption(String attribute, int i) {
        return attribute + "=\"" + BasicNode.getAttributeNames()[i] + "\" ";
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public String getShapefile() {
        return shapefile;
    }

    public void setShapefile(String shapefile) {
        this.shapefile = shapefile;
    }

    public int getSides() {
        return sides;
    }

    public void setSides(int sides) {
        this.sides = sides;
    }

    public double getDistortion() {
        return distortion;
    }

    public void setDistortion(double distortion) {
        this.distortion = distortion;
    }

    public double getSkew() {
        return skew;
    }

    public void setSkew(double skew) {
        this.skew = skew;
    }

    public boolean isRegular() {
        return regular;
    }

    public void setRegular(boolean regular) {
        this.regular = regular;
    }

    public Rectangle getRects() {
        return rects;
    }

    public void setRects(Rectangle rects) {
        this.rects = rects;
    }
}
