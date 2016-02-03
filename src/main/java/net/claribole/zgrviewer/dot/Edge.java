/*   FILE: Edge.java
 *   DATE OF CREATION:   Apr 4 2005
 *   AUTHOR :            Eric Mounhem (skbo@lri.fr)
 *   Copyright (c) INRIA, 2004-2005. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 * 
 * $Id: Edge.java 576 2007-03-29 18:32:53Z epietrig $
 */

package net.claribole.zgrviewer.dot;

import java.awt.Color;
import java.util.Vector;

/**
 * Defines a GraphViz edge linking a node to another. GraphViz handle both
 * directed and undirected graphs, but its edges are all coded directed, and
 * only changing edge's drawing.
 * @author Eric Mounhem
 */
public class Edge {
    /**
     * Draw an edge pointing the ending node (directed graph's default)
     */
    static final int       FORWARD        = 0;
    /**
     * Draw an edge pointing the starting node
     */
    static final int       BACK           = 1;
    /**
     * Draw an edge pointing both starting and ending nodes
     */
    static final int       BOTH           = 2;
    /**
     * Draw a single line between starting and ending nodes, without pointing
     * (undirected graph's default)
     */
    static final int       NONE           = 3;

    /**
     * Calculate the best len value to use
     */
    static final double    AUTO_LEN       = -1;

    final private String[] attributeNames = { "forward", "back", "both", "none" };

    private Style                  style          = new Style();

    private int                    dir;
    private int                    minlen         = 1;
    private int                    showboxes      = 0;
    private Node                   start          = null;
    private Node                   end            = null;
    private String                 label          = "";
    private String                 URL;
    private String                 fontname       = "Times-Roman";
    private String                 labelfontname;                                         //  = "Times-Roman";
    private String                 tooltip        = "";
    private String                 target;
    private String                 samehead       = "";
    private String                 sametail       = "";
    /**
     * The root graph object that this edge belongs
     */
    /*Graph*/Object       rootGraph;
    private Color[]                color;
    private Color                  fontcolor      = Color.black;
    private Color                  labelfontcolor;                                        // = Color.black;
    private boolean                constraint     = true;
    private boolean                decorate       = false;
    private boolean                labelfloat     = false;
    private boolean                nojustify      = false;
    private double                 fontsize       = 14;
    private double                 labelangle     = -25;
    private double                 labeldistance  = 1;
    private double                 labelfontsize  = -1;                                   //  = 11;
    private double                 len            = AUTO_LEN;
    private double                 weight         = 1;
    private ArrowEnd               head           = new ArrowEnd(
                                                  new Shape(Shape.NORMAL),
                                                  new Shape(Shape.NONE));
    private ArrowEnd               tail           = new ArrowEnd(new Shape(Shape.NONE),
                                                  new Shape(Shape.NONE));
    private double                 arrowSize;
    private Point                  lp;
    private Cluster                lhead;
    private Cluster                ltail;
    //Spline[]               pos;
    private Spline                 pos;
    private String[]               layer;


    /**
     * Add a new color to the color's list of an edge
     * @param edgeColor new color to add
     */
    public void addColor(Color edgeColor) {
        if (this.getColor() == null) {
            this.setColor(new Color[1]);
            this.getColor()[0] = edgeColor;
        } else {
            Color[] tmp = new Color[this.getColor().length + 1];
            System.arraycopy(this.getColor(), 0, tmp, 0, this.getColor().length);
            tmp[tmp.length - 1] = edgeColor;
            this.setColor(tmp);
        }
        //        System.err.println("adding color: " + edgeColor + " - " + this.color.length);
    }

    /**
     * Remove the last color added
     * @param edgeColor the color to remove
     */
    public void removeColor(Color edgeColor) {
        if (this.getColor() != null) {
            for (int i = this.getColor().length - 1; i >= this.getColor().length; i--) {
                if (this.getColor()[i].equals(edgeColor)) {
                    Color[] tmp = new Color[this.getColor().length - 1];
                    System.arraycopy(this.getColor(), 0, tmp, 0, i);
                    System.arraycopy(this.getColor(), i + 1, tmp, i,
                            this.getColor().length - i - 1);
                    this.setColor(tmp);
                    break;
                }
            }
            if (this.getColor().length == 0) {
                this.setColor(null);
            }
        }
    }

    /**
     * Set the style of a node
     * @param style one of the following:
     * <ul>
     * <li>Node.DASHED</li>
     * <li>Node.DOTTED</li>
     * <li>Node.SOLID</li>
     * <li>Node.INVIS</li>
     * <li>Node.BOLD</li>
     * </ul>
     * @param value value you want to apply to a style
     */
    void setStyle(int style, boolean value) {
        this.getStyle().setStyle(style, value);
    }

    /**
     * Get the style of a node
     * @param s One of the following style:
     * <ul>
     * <li>Node.DASHED</li>
     * <li>Node.DOTTED</li>
     * <li>Node.SOLID</li>
     * <li>Node.INVIS</li>
     * <li>Node.BOLD</li>
     * </ul>
     * @return State of a style (activated or not)
     */
    boolean getStyle(int s) {
        return this.getStyle().getStyle(s);
    }

    /**
     * Create an Edge object going from a node to another
     * 
     * @param rootGraph
     *            Root of the graph
     * @param start
     *            Starting node
     * @param end
     *            Ending node
     */
    /*    public Edge(Graph rootGraph, Node start, Node end) {
     this.rootGraph = rootGraph;
     this.dir = (rootGraph.directed) ? FORWARD : NONE;
     setStart(start);
     setEnd(end);
     rootGraph.addEdge(this);
     }

     public Edge(SubGraph rootGraph, Node start, Node end) {
     this.rootGraph = rootGraph;
     this.dir = (rootGraph.getRootGraph().directed) ? FORWARD : NONE;
     setStart(start);
     setEnd(end);
     rootGraph.addEdge(this);
     }
     */
    public Edge(Object rootGraph, Node start, Node end) {
        this.rootGraph = rootGraph;
        setStart(start);
        setEnd(end);
        if (rootGraph instanceof Graph) {
            this.setDir((((Graph) rootGraph).isDirected()) ? FORWARD : NONE);
            ((Graph) rootGraph).addEdge(this);
        } else {
            this.setDir((((SubGraph) rootGraph).getRootGraph().isDirected()) ? FORWARD
                    : NONE);
            ((SubGraph) rootGraph).addEdge(this);
        }

        this.getGenericAttributes();
    }

    private void getAttributes(Edge edge) {
        if (this.getArrowSize() != edge.getArrowSize())
            this.setArrowSize(edge.getArrowSize());

        if (this.getColor() != null) {
            if (!this.getColor().equals(edge.getColor()))
                this.setColor(edge.getColor());
        } else
            this.setColor(edge.getColor());


        if (this.isConstraint() != edge.isConstraint())
            this.setConstraint(edge.isConstraint());
        if (this.isDecorate() != edge.isDecorate())
            this.setDecorate(edge.isDecorate());
        if (this.getDir() != edge.getDir())
            this.setDir(edge.getDir());

        if (this.getFontcolor() == null)
            this.setFontcolor(edge.getFontcolor());
        else if (!this.getFontcolor().equals(edge.getFontcolor()))
            this.setFontcolor(edge.getFontcolor());

        if (!this.getFontname().equals(edge.getFontname()))
            this.setFontname(edge.getFontname());
        if (this.getFontsize() != edge.getFontsize())
            this.setFontsize(edge.getFontsize());
        if (!this.getHead().equals(edge.getHead()))
            this.setHead(edge.getHead());
        if (!this.getLabel().equals(edge.getLabel()))
            this.setLabel(edge.getLabel());
        if (this.getLabelangle() != edge.getLabelangle())
            this.setLabelangle(edge.getLabelangle());
        if (this.getLabeldistance() != edge.getLabeldistance())
            this.setLabeldistance(edge.getLabeldistance());
        if (this.isLabelfloat() != edge.isLabelfloat())
            this.setLabelfloat(edge.isLabelfloat());

        if (this.getLabelfontcolor() == null)
            this.setLabelfontcolor(edge.getLabelfontcolor());
        else if (!this.getLabelfontcolor().equals(edge.getLabelfontcolor()))
            this.setLabelfontcolor(edge.getLabelfontcolor());

        if (this.getLabelfontname() == null)
            this.setLabelfontname(edge.getLabelfontname());
        else if (!this.getLabelfontname().equals(edge.getLabelfontname()))
            this.setLabelfontname(edge.getLabelfontname());

        if (this.getLabelfontsize() != edge.getLabelfontsize())
            this.setLabelfontsize(edge.getLabelfontsize());
        if (this.getLayer() != null)
            if (!this.getLayer().equals(edge.getLayer()))
                this.setLayer(edge.getLayer());
        if (this.getLen() != edge.getLen())
            this.setLen(edge.getLen());
        if (this.getLhead() != null)
            if (!this.getLhead().equals(edge.getLhead()))
                this.setLhead(edge.getLhead());
        if (this.getLp() != null)
            if (!this.getLp().equals(edge.getLp()))
                this.setLp(edge.getLp());
        if (this.getLtail() != null)
            if (!this.getLtail().equals(edge.getLtail()))
                this.setLtail(edge.getLtail());
        if (this.getMinlen() != edge.getMinlen())
            this.setMinlen(edge.getMinlen());
        if (this.isNojustify() != edge.isNojustify())
            this.setNojustify(edge.isNojustify());
        if (this.getPos() != null)
            if (!this.getPos().equals(edge.getPos()))
                this.setPos(edge.getPos());
        if (this.getSamehead() != null)
            if (!this.getSamehead().equals(edge.getSamehead()))
                this.setSamehead(edge.getSamehead());
        if (this.getSametail() != null)
            if (!this.getSametail().equals(edge.getSametail()))
                this.setSametail(edge.getSametail());
        if (this.getShowboxes() != edge.getShowboxes())
            this.setShowboxes(edge.getShowboxes());
        if (!this.getStyle().equals(edge.getStyle()))
            this.setStyle(edge.getStyle());
        if (!this.getTail().equals(edge.getTail()))
            this.setTail(edge.getTail());
        if (this.getTarget() != null)
            if (!this.getTarget().equals(edge.getTarget()))
                this.setTarget(edge.getTarget());
        if (this.getTooltip() != null)
            if (!this.getTooltip().equals(edge.getTooltip()))
                this.setTooltip(edge.getTooltip());
        if (this.getURL() != null)
            if (!this.getURL().equals(edge.getURL()))
                this.setURL(edge.getURL());
        if (this.getWeight() != edge.getWeight())
            this.setWeight(edge.getWeight());
    }

    private void getGenericAttributes() {
        Object root = this.rootGraph;
        Vector roots = new Vector();

        while (root instanceof SubGraph) {
            roots.add(root);
            root = ((SubGraph) root).getRoot();
        }

        if (((Graph) root).getGenericEdge() != null)
            getAttributes(((Graph) root).getGenericEdge());

        for (int i = roots.size() - 1; i == 0; i--) {
            Edge generic = ((SubGraph) roots.get(i)).getGenericEdge();
            if (generic != null)
                getAttributes(generic);
        }
    }

    /**
     * Define the starting node
     * 
     * @param start
     */
    public void setStart(Node start) {
        if (this.start != null)
            this.start.removeInEdge(this);
        this.start = start;
        this.start.addInEdge(this);
    }

    /**
     * Define the ending node
     * 
     * @param end
     */
    public void setEnd(Node end) {
        if (this.end != null)
            this.end.removeOutEdge(this);
        this.end = end;
        this.end.addOutEdge(this);
    }


    public void changeOption(String name, String value) /*throws Exception*/{
        String v;
        if (value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
            v = value.substring(1, value.length() - 1);
        } else
            v = value;
        // Removing \ followed by \n
        v = v.replaceAll("\\\\\\n", "");
        //v = v.replaceAll("\\n", "\n");

        if (name.equals("arrowhead")) {
            //this.head = DotUtils.readArrow(v);
        } else if (name.equals("arrowtail")) {
            //this.tail = DotUtils.readArrow(v);
        } else if (name.equals("arrowsize"))
            this.setArrowSize(DotUtils.readDouble(v));
        else if (name.equals("color")) {
            Color[] colors = DotUtils.readColors(v);
            for (int i = 0; i < colors.length; i++)
                addColor(colors[i]);
        } else if (name.equals("constraint"))
            this.setConstraint(DotUtils.readBoolean(v));
        else if (name.equals("decorate"))
            this.setDecorate(DotUtils.readBoolean(v));
        else if (name.equals("dir"))
            this.setDir(DotUtils.readInteger(v));
        else if (name.equals("fontcolor"))
            this.setFontcolor(DotUtils.readColor(v));
        else if (name.equals("fontname"))
            this.setFontname(v);
        else if (name.equals("fontsize"))
            this.setFontsize(DotUtils.readDouble(v));
        else if (name.equals("label"))
            this.setLabel(v);
        else if (name.equals("labelangle"))
            this.setLabelangle(DotUtils.readDouble(v));
        else if (name.equals("labeldistance"))
            this.setLabeldistance(DotUtils.readDouble(v));
        else if (name.equals("labelfloat"))
            this.setLabelfloat(DotUtils.readBoolean(v));
        else if (name.equals("labelfontcolor"))
            this.setLabelfontcolor(DotUtils.readColor(v));
        else if (name.equals("labelfontname"))
            this.setLabelfontname(v);
        else if (name.equals("labelfontsize"))
            this.setLabelfontsize(DotUtils.readDouble(v));
        else if (name.equals("layer"))
            this.setLayer(v.split(" "));
        else if (name.equals("len")) {
            double d = DotUtils.readDouble(v);
            this.setLen((d <= 0) ? AUTO_LEN : d);
        } else if (name.equals("lp"))
            this.setLp(DotUtils.readPoint(v));
        else if (name.equals("minlen"))
            this.setMinlen(DotUtils.readInteger(v));
        else if (name.equals("nojustify"))
            this.setNojustify(DotUtils.readBoolean(v));
        else if (name.equals("pos"))
            this.setPos(DotUtils.readSpline(v));
        else if (name.equals("samehead"))
            this.setSamehead(v);
        else if (name.equals("sametail"))
            this.setSametail(v);
        else if (name.equals("showboxes"))
            this.setShowboxes(DotUtils.readInteger(v));
        else if (name.equals("style"))
            DotUtils.readStyle(this.getStyle(), v);
        else if (name.equals("target"))
            this.setTarget(v);
        else if (name.equals("tooltip"))
            this.setTooltip(v);
        else if (name.equals("URL") || name.equals("href"))
            this.setURL(v);
        else if (name.equals("weight"))
            this.setWeight(DotUtils.readDouble(v));
       
        else if(name.equals("headport"))
            this.getHead().setPort(readAttributeNumber(v));
        else if(name.equals("tailport"))
            this.getTail().setPort(readAttributeNumber(v));
       
        /* else if (name.equals("id")) {
         // XXX: don't know this attribute
         }*/

        else {
            //throw new Exception("Edge attribute \"" + name
            //        + "\" does not exist");
            System.err
                    .println("Edge attribute \"" + name + "\" does not exist");
        }
    }
    
    private int readAttributeNumber(String v) {
        for (int i = 0; i < this.attributeNames.length; i++) {
            if (this.attributeNames[i].equalsIgnoreCase(v))
                return i;
        }
        System.err.println("Edge value \"" + v + "\" does not exist");
        return -1;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        String e = "";
        if (this.getStart() instanceof SubRecord)
            e = ((SubRecord) (this.getStart())).getRootRecord().getId() + ":";
        e += ((this.getStart() instanceof Cluster) ? "cluster" : "") + this.getStart().getId();
        e += this.getTail();
        e += (getRootGraph().isDirected() ? " -> " : " -- ");
        if (this.getEnd() instanceof SubRecord)
            e += ((SubRecord) (this.getEnd())).getRootRecord().getId() + ":";
        e += ((this.getEnd() instanceof Cluster) ? "cluster" : "") + this.getEnd().getId();
        e += this.getHead();
        String o = edgeOptions();
        if (!o.equals(" "))
            e += " [" + o + "]";
        return e + ";\n";
    }

    private String edgeOptions() {
        String o = " ";
        if (this.getColor() != null)
            o += printOption("color", this.getColor());
        if (!this.isConstraint())
            o += printOption("constraint", this.isConstraint());
        if (this.isDecorate())
            o += printOption("decorate", this.isDecorate());
        Graph root = getRootGraph();
        if ((root.isDirected() && this.getDir() != FORWARD)
                || (!root.isDirected() && this.getDir() != NONE))
            o += printNamedOption("dir", this.getDir());
        if (!this.getFontcolor().equals(Color.black))
            o += printOption("fontcolor", this.getFontcolor());
        if (!this.getFontname().equals("Times-Roman"))
            o += printOption("fontname", this.getFontname());
        if (this.getFontsize() != 14)
            o += printOption("fontsize", this.getFontsize());
        if (this.getHead() != null)
            o += printOption("head", this.getHead());
        if (this.getTail() != null)
            o += printOption("tail", this.getTail());
        if (!this.getLabel().equals(""))
            o += printOption("label", this.getLabel());
        if (this.getLabelangle() != -25)
            o += printOption("labelangle", this.getLabelangle());
        if (this.getLabeldistance() != 1)
            o += printOption("labeldistance", this.getLabeldistance());
        if (this.isLabelfloat())
            o += printOption("labelfloat", this.isLabelfloat());
        if (this.getLabelfontcolor() != null) //.equals(Color.black))
            o += printOption("labelfontcolor", this.getLabelfontcolor());
        if (this.getLabelfontname() != null) //.equals("Times-Roman"))
            o += printOption("labelfontname", this.getLabelfontname());
        if (this.getLabelfontsize() != -1 && this.getLabelfontsize() != 11)
            o += printOption("labelfontsize", this.getLabelfontsize());
        if (this.getLayer() != null)
            o += printOption("layer", this.getLayer());
        if (this.getLen() != AUTO_LEN)
            o += printOption("len", this.getLen());
        if (this.getLhead() != null)
            o += printOption("lhead", this.getLhead());
        if (this.getLtail() != null)
            o += printOption("ltail", this.getLtail());
        if (this.getLp() != null)
            o += printOption("lp", this.getLp());
        if (this.getMinlen() != 1)
            o += printOption("minlen", this.getMinlen());
        if (this.isNojustify())
            o += printOption("nojustify", this.isNojustify());
        if (this.getPos() != null)
            o += printOption("pos", this.getPos());
        if (!this.getSamehead().equals(""))
            o += printOption("samehead", this.getSamehead());
        if (!this.getSametail().equals(""))
            o += printOption("sametail", this.getSametail());
        if (this.getShowboxes() != 0)
            o += printOption("showboxes", this.getShowboxes());


        o += printOption("style", this.getStyle());


        if (this.getTarget() != null)
            o += printOption("target", this.getTarget());
        if (!this.getTooltip().equals(""))
            o += printOption("tooltip", this.getTooltip());
        if (this.getURL() != null)
            o += printOption("URL", this.getURL());
        if (this.getWeight() != 1)
            o += printOption("weight", this.getWeight());
        return o;
    }

    private String printOption(String attribute, Style value) {
        String o = value.toString();
        if (o.equals(""))
            return "";
        return attribute + "=" + value + " ";
    }

    private String printOption(String attribute, boolean value) {
        return attribute + "=\"" + value + "\" ";
    }

    private String printOption(String attribute, Color[] value) {
        String o = "";
        for (int i = 0; i < value.length; i++) {
            if (i > 0)
                o += ":";
            o += printColor(value[i]);
        }
        return attribute + "=\"" + o + "\" ";
    }

    private String printOption(String attribute, Color value) {
        return attribute + "=\"" + printColor(value) + "\" ";
    }

    private String printOption(String attribute, double value) {
        return attribute + "=" + value + " ";
    }

    private String printOption(String attribute, int value) {
        return attribute + "=" + value + " ";
    }

    private String printOption(String attribute, String value) {
        return attribute + "=\"" + value + "\" ";
    }

    private String printOption(String attribute, String[] value) {
        String ret = attribute + "=\"";
        for (int i = 0; i < value.length; i++) {
            if (i > 0)
                ret += " ";
            ret += value[i];
        }
        return ret + "\" ";
    }

    private String printOption(String attribute, ArrowEnd value) {
        return value.printArrowEndOptions(attribute);
    }

    private String printOption(String attribute, Point value) {
        String o = attribute + "=\"";
        for (int i = 0; i < value.getCoords().length; i++) {
            if (i > 0)
                o += ",";
            o += value.getCoords()[i];
        }
        return o + "\"" + (value.isChange() ? "" : "!") + " ";
    }

    private String printOption(String attribute, Cluster value) {
        return attribute + "=\"" + value.getId() + "\" ";
    }

    private String printOption(String attribute, Spline value) {
        String o = attribute + "=\"";
        /*for (int i = 0; i < value.length; i++) {
         if (i > 0)
         o += "; ";
         o += value[i];
         }*/
        return o + value + "\" ";
    }

    private String printColor(Color value) {
        String r = Integer.toHexString(value.getRed()), g = Integer
                .toHexString(value.getGreen()), b = Integer.toHexString(value
                .getBlue()), a = Integer.toHexString(value.getAlpha());
        if (r.length() == 1)
            r = "0" + r;
        if (g.length() == 1)
            g = "0" + g;
        if (b.length() == 1)
            b = "0" + b;
        if (a.length() == 1)
            a = "0" + a;
        return "#" + r + g + b + a;
    }

    private String printNamedOption(String attribute, int i) {
        return attribute + "=\"" + this.attributeNames[i] + "\" ";
    }

    private Graph getRootGraph() {
        if (this.rootGraph instanceof Graph)
            return (Graph) this.rootGraph;
        return ((SubGraph) this.rootGraph).getRootGraph();
        //return null;
    }

    /**
     * Values of edge styles
     */
    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    /**
     * Direction of the edge, one of the following:
     * <ul>
     * <li>FORWARD</li>
     * <li>BACK</li>
     * <li>BOTH</li>
     * <li>NONE</li>
     * </ul>
     */
    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    /**
     * Minimum edge length, where the length is the rank's difference between
     * the two nodes
     */
    public int getMinlen() {
        return minlen;
    }

    public void setMinlen(int minlen) {
        this.minlen = minlen;
    }

    /**
     * PostScript debug mode
     */
    public int getShowboxes() {
        return showboxes;
    }

    public void setShowboxes(int showboxes) {
        this.showboxes = showboxes;
    }

    /**
     * Starting node of the edge
     */
    public Node getStart() {
        return start;
    }

    /**
     * Ending node of the edge
     */
    public Node getEnd() {
        return end;
    }

    /**
     * Label of the edge
     */
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Hyperlink associated to an edge
     */
    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    /**
     * PostScript name of the font to use on text label
     */
    public String getFontname() {
        return fontname;
    }

    public void setFontname(String fontname) {
        this.fontname = fontname;
    }

    /**
     * PostScript name of the font to use on arrow ends
     */
    public String getLabelfontname() {
        return labelfontname;
    }

    public void setLabelfontname(String labelfontname) {
        this.labelfontname = labelfontname;
    }

    /**
     * Tooltip annotation of this edge
     */
    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
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
     * Groupe heads in the samehead group
     */
    public String getSamehead() {
        return samehead;
    }

    public void setSamehead(String samehead) {
        this.samehead = samehead;
    }

    /**
     * Groupe tails in the sametail group
     */
    public String getSametail() {
        return sametail;
    }

    public void setSametail(String sametail) {
        this.sametail = sametail;
    }

    /**
     * Colors of graphics
     */
    public Color[] getColor() {
        return color;
    }

    public void setColor(Color[] color) {
        this.color = color;
    }

    /**
     * Color of text
     */
    public Color getFontcolor() {
        return fontcolor;
    }

    public void setFontcolor(Color fontcolor) {
        this.fontcolor = fontcolor;
    }

    /**
     * Color of arrow ends texts
     */
    public Color getLabelfontcolor() {
        return labelfontcolor;
    }

    public void setLabelfontcolor(Color labelfontcolor) {
        this.labelfontcolor = labelfontcolor;
    }

    /**
     * Used to know if an edge count in rank assignment
     */
    public boolean isConstraint() {
        return constraint;
    }

    public void setConstraint(boolean constraint) {
        this.constraint = constraint;
    }

    /**
     * Attach the label text to the edge with a segment
     */
    public boolean isDecorate() {
        return decorate;
    }

    public void setDecorate(boolean decorate) {
        this.decorate = decorate;
    }

    /**
     * Let label text be on top of edges
     */
    public boolean isLabelfloat() {
        return labelfloat;
    }

    public void setLabelfloat(boolean labelfloat) {
        this.labelfloat = labelfloat;
    }

    /**
     * Don't justify multilines labels
     */
    public boolean isNojustify() {
        return nojustify;
    }

    public void setNojustify(boolean nojustify) {
        this.nojustify = nojustify;
    }

    /**
     * Text's font size (in points)
     */
    public double getFontsize() {
        return fontsize;
    }

    public void setFontsize(double fontsize) {
        this.fontsize = fontsize;
    }

    /**
     * Angle of arrow end labels
     */
    public double getLabelangle() {
        return labelangle;
    }

    public void setLabelangle(double labelangle) {
        this.labelangle = labelangle;
    }

    /**
     * Scaling factor for arrow end label's position
     */
    public double getLabeldistance() {
        return labeldistance;
    }

    public void setLabeldistance(double labeldistance) {
        this.labeldistance = labeldistance;
    }

    /**
     * Arrow end label's font size
     */
    public double getLabelfontsize() {
        return labelfontsize;
    }

    public void setLabelfontsize(double labelfontsize) {
        this.labelfontsize = labelfontsize;
    }

    /**
     * Preferred edge length in inches
     */
    public double getLen() {
        return len;
    }

    public void setLen(double len) {
        this.len = len;
    }

    /**
     * Weight of edge.
     */
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Style of head arrow end
     */
    public ArrowEnd getHead() {
        return head;
    }

    public void setHead(ArrowEnd head) {
        this.head = head;
    }

    /**
     * Style of tail arrow end
     */
    public ArrowEnd getTail() {
        return tail;
    }

    public void setTail(ArrowEnd tail) {
        this.tail = tail;
    }

    /**
     * Size of arrow ends
     */
    public double getArrowSize() {
        return arrowSize;
    }

    public void setArrowSize(double arrowSize) {
        this.arrowSize = arrowSize;
    }

    /**
     * Label position (in points)
     */
    public Point getLp() {
        return lp;
    }

    public void setLp(Point lp) {
        this.lp = lp;
    }

    /**
     * Cluster where the arrow must stop
     */
    public Cluster getLhead() {
        return lhead;
    }

    public void setLhead(Cluster lhead) {
        this.lhead = lhead;
    }

    /**
     * Cluster where the arrow must start
     */
    public Cluster getLtail() {
        return ltail;
    }

    public void setLtail(Cluster ltail) {
        this.ltail = ltail;
    }

    /**
     * Position of the control nodes of the edge
     */
    public Spline getPos() {
        return pos;
    }

    public void setPos(Spline pos) {
        this.pos = pos;
    }

    /**
     * Layers in which the node is present
     */
    public String[] getLayer() {
        return layer;
    }

    public void setLayer(String[] layer) {
        this.layer = layer;
    }
}
