/*   FILE: Graph.java
 *   DATE OF CREATION:   Apr 4 2005
 *   AUTHOR :            Eric Mounhem (skbo@lri.fr)
 *   Copyright (c) INRIA, 2004-2005. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 * 
 * $Id: Graph.java 576 2007-03-29 18:32:53Z epietrig $
 */

package net.claribole.zgrviewer.dot;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * Root of a GraphViz graph. This class contains global variables used for
 * many purpose: layout, order...
 * @author Eric Mounhem
 */
public class Graph {
    /* Drawing direction */
    /**
     * Top to Bottom
     */
    public static final int       TB                  = 0;
    /**
     * Left to Right
     */
    public static final int       LR                  = 1;
    /**
     * Bottom to Top
     */
    public static final int       BT                  = 2;
    /**
     * Right to Left
     */
    public static final int       RL                  = 3;

    /* Page direction */
    /**
     * Bottom to Top, Left to Right
     */
    public static final int       BL                  = 4;
    /**
     * Bottom to Top, Right to Left
     */
    public static final int       BR                  = 5;
    /**
     * Top to Bottom, Left to Right
     */
    public static final int       TL                  = 6;
    /**
     * Top to Bottom, Right to Left
     */
    public static final int       TR                  = 7;
    /**
     * Right to Left, Bottom to Top
     */
    public static final int       RB                  = 8;
    /**
     * Right to Left, Top to Bottom
     */
    public static final int       RT                  = 9;
    /**
     * Left to Right, Bottom to Top
     */
    public static final int       LB                  = 10;
    /**
     * Left to Right, Top to Bottom
     */
    public static final int       LT                  = 11;

    /* Label justification and location */
    /**
     * Center clusters label
     */
    public static final int       CENTER              = 12;
    /**
     * Left justify clusters label
     */
    public static final int       LEFT                = 13;
    /**
     * Right justify clusters label
     */
    public static final int       RIGHT               = 14;
    /**
     * Place graph or clusters label at top
     */
    public static final int       TOP                 = 15;
    /**
     * Place graph or clusters label at bottom
     */
    public static final int       BOTTOM              = 16;

    /* Output mode */
    /**
     * Draw nodes and edges when they come, may lead to edges over nodes and
     * nodes over edges
     */
    public static final int       BREADTHFIRST        = 17;
    /**
     * Draw nodes first, get only edges over nodes (no misunderstandings)
     */
    public static final int       NODESFIRST          = 18;
    /**
     * Draw edges first, get only nodes over edges (eye-candy)
     */
    public static final int       EDGESFIRST          = 19;

    /* Pack mode */
    /**
     * Pack only around the node, letting other nodes interleave into the graph
     */
    public static final int       NODE                = 20;
    /**
     * Keep top-level clusters intact
     */
    public static final int       CLUST               = 21;
    /**
     * Use the element's bounding box to pack them
     */
    public static final int       GRAPH               = 22;

    /* Ratio */
    /**
     * No ratio
     */
    public static final double    NO_RATIO            = -1;
    /**
     * Scale node positions for the graph to be drawn filling the size given
     * with the size attribute
     */
    public static final double    FILL                = -2;
    /**
     * Compress the layout to fit in the size given with the size attribute
     */
    public static final double    COMPRESS_RATIO      = -3;
    /**
     * Expand node positions for width or height reach the size attribute's
     * value
     */
    public static final double    EXPAND              = -4;
    /**
     * Find the best size for the graph to be drawn on multiple pages (if it
     * can't be on one)
     */
    public static final double    AUTO                = -5;

    public static final String[]  ratioAttributeNames = { "", "fill", "compress",
            "expand", "auto"                  };

    /* ClusterRank */
    /**
     * No special cluster processing
     */
    public static final int       NONE                = 23;
    /**
     * @see Graph#NONE
     */
    public static final int       GLOBAL              = 24;
    /**
     * SubGraphs can have Cluster properties
     */
    public static final int       LOCAL               = 25;

    /* Model */
    /**
     * Shortpath model
     */
    public static final int       SHORTPATH           = 26;
    /**
     * Circuit model
     */
    public static final int       CIRCUIT             = 27;
    /**
     * Subset model
     */
    public static final int       SUBSET              = 28;

    /* Start Style */
    /**
     * Start with the nodes regularly placed around a circle
     */
    public static final int       REGULAR             = 29;
    /**
     * Start with neato placed nodes
     */
    public static final int       SELF                = 30;
    /**
     * Start with randomly placed nodes
     */
    public static final int       RANDOM              = 31;

    /* Splines */
    /**
     * Don't draw edges as splines
     */
    public static final int       NO_SPLINES          = 32;
    /**
     * Draw edges as splines (non overlapping nodes required)
     */
    public static final int       SPLINES             = 33;
    /**
     * Draw the edges to avoid clusters
     */
    public static final int       COMPOUND            = 34;

    /**
     * Retain node overlaps
     */
    public static final int       RETAIN              = 35;
    /**
     * Uniformly scale x and y to remove overlaps
     */
    public static final int       SCALE               = 36;
    /**
     * Use a Voronoi-based technique to remove overlaps
     */
    public static final int       VORONOI             = 37;
    /**
     * Scale x and y separately to remove overlaps
     */
    public static final int       SCALEXY             = 38;
    /**
     * Optimize x and y axis, starting with the x one, to remove overlaps
     */
    public static final int       ORTHOXY             = 39;
    /**
     * Optimize x and y axis, starting with the y one, to remove overlaps
     */
    public static final int       ORTHOYX             = 40;
    /**
     * Scale down the layout without creating overlaps
     */
    public static final int       COMPRESS            = 41;

    /* Ordering */
    // XXX: find how ordering works (may be related to node's evaluation order)
    /**
     * No node's ordering in the graph
     */
    public static final int       NO_ORDERING         = 42;
    /**
     * Ordering exiting nodes in the order they appear
     */
    public static final int       IN                  = 43;
    /**
     * Ordering entering nodes in the order they appear
     */
    public static final int       OUT                 = 44;

    /* Mode */
    /**
     * Major optimization mode
     */
    public static final int       MAJOR               = 45;
    /**
     * KK optimization mode
     */
    public static final int       KK                  = 46;

    /* Charset */
    /**
     * UTF-8 charset
     */
    public static final int       UTF_8               = 47;
    /**
     * Latin1 charset
     */
    public static final int       ISO_8859_1          = 48;

    /**
     * Use the default random seed
     */
    public static final int       NO_SEED             = -1;

    final private String[] attributeNames      = { "TB", "LR", "BT", "RL",
            "BL", "BR", "TL", "TR", "RB", "RT", "LB", "LT", "c", "l", "r", "t",
            "b", "breadthfirst", "nodesfirst", "edgesfirst", "node", "clust",
            "graph", "none", "global", "local", "shortpath", "circuit",
            "subset", "regular", "self", "random", "false", "true", "compound",
            "true", "scale", "false", "scalexy", "orthoxy", "orthoyx",
            "compress", "", "in", "out", "major", "KK", "UTF-8", "iso-8859-1" };

    Node[]                 nodes;
    Node[]                 genericNodes;

    Edge[]                 edges;


    /**
     * Identification string of a graph
     */
    private String                 id;
    /**
     * Hyperlink associated to a graph.
     */
    private String                 URL;
    /**
     * Name of the font used to write graph's label
     */
    private String                 fontname            = "Times-Roman";
    /**
     * Label of the graph
     */
    private String                 label               = "";
    /**
     * Target of the URL
     */
    private String                 target;
    /**
     * XML stylesheet for SVG output
     */
    private String                 stylesheet;
    /**
     * Specifies the character encoding used. One of the following:
     * <ul>
     * <li>UTF_8</li>
     * <li>ISO_8859_1</li>
     * </ul>
     */
    private int                    charset             = UTF_8;
    /**
     * Tells to GD where to find fonts
     */
    private String[]               fontpath            = null;
    /**
     * List of layer names for output
     */
    private String[]               layers              = null;
    /**
     * Layers separators
     */
    private String                 layersep            = "";
    /**
     * Directed or undirected graph
     */
    private boolean                directed;
    /**
     * Center the drawing on the canvas
     */
    private boolean                center              = false;
    /**
     * Allow edges betwenn clusters
     */
    private boolean                compound            = false;
    /**
     * Use edges concentrators
     */
    private boolean                concentrate         = false;
    /**
     * Don't justify multilines labels
     */
    private boolean                nojustify           = false;
    /**
     * Use the first point as origin and make the first edge horizontal
     */
    private boolean                normalize           = false;
    /**
     * Run cross minimization on multiple clusters
     */
    private boolean                remincross          = false;
    /**
     * Use a truecolor color model for bitmap rendering
     */
    private Boolean                truecolor           = null;
    /**
     * Direction of graph layout. One of the following:
     * <ul>
     * <li>TB</li>
     * <li>LR</li>
     * <li>BT</li>
     * <li>RL</li>
     * </ul>
     */
    private int                    rankdir             = TB;
    /**
     * Number of dimensions for the layout
     */
    private int                    dim                 = 2;
    /**
     * Label justification (CENTER, LEFT or RIGHT)
     */
    private int                    labeljust           = CENTER;
    /**
     * Label localisation (TOP or BOTTOM)
     */
    private int                    labelloc            = BOTTOM;
    /**
     * Specify drawing order of nodes and edges. One of the following:
     * <ul>
     * <li>BREADTHFIRST</li>
     * <li>NODESFIRST</li>
     * <li>EDGESFIRST</li>
     * </ul>
     */
    private int                    outputorder         = BREADTHFIRST;
    /**
     * Used to activate or deactivate packing.
     * -1: false
     * >0: true with the value as a margin
     * (0: true, with the margin at 8...)
     */
    private boolean                pack                = false;
    /**
     * Value of pack margin when pack is true
     */
    private int                    packValue           = 8;
    /**
     * Packing method to use. May be one of the following:
     * <ul>
     * <li>NODE</li>
     * <li>CLUST</li>
     * <li>GRAPH</li>
     * </ul>
     */
    private int                    packmode            = NODE;
    /**
     * Specifies the order in which the pages are emitted. May be one of the
     * following:
     * <ul>
     * <li>BL</li>
     * <li>BR</li>
     * <li>TL</li>
     * <li>TR</li>
     * <li>RB</li>
     * <li>RT</li>
     * <li>LB</li>
     * <li>LT</li>
     * </ul>
     */
    private int                    pagedir             = BL;
    /**
     * Set graph orientation
     */
    private int                    rotate              = 0;
    /**
     * Number of points used to represent circles and ellipses
     */
    private int                    samplepoints        = 8;
    /**
     * Maximum number of negative cut edges to search for minimum cut value
     */
    private int                    searchsize          = 30;
    /**
     * Show PostScript guide boxes for debugging
     */
    private int                    showboxes           = 0;
    /**
     * Mode used for handling clusters. May be one of the following:
     * <ul>
     * <li>NONE</li>
     * <li>GLOBAL</li>
     * <li>LOCAL</li>
     * </ul>
     */
    private int                    clusterrank         = LOCAL;
    /**
     * How the distance matrix is computed. May be one of the following:
     * <ul>
     * <li>SHORTPATH</li>
     * <li>CIRCUIT</li>
     * <li>SUBSET</li>
     * </ul>
     */
    private int                    model               = SHORTPATH;
    /**
     * Control node placement at start. May be one of the following:
     * <ul>
     * <li>REGULAR</li>
     * <li>SELF</li>
     * <li>RANDOM</li>
     * </ul>
     */
    private int                    startStyle          = RANDOM;
    /**
     * Specifies a seed for the random number generator (NO_SEED is for
     * unspecified seed)
     */
    private int                    startSeed           = NO_SEED;
    /**
     * Method of drawing edges. May be one of the following:
     * <ul>
     * <li>NO_SPLINES</li>
     * <li>SPLINES</li>
     * <li>COMPOUND</li>
     * </ul>
     */
    private int                    splines             = NO_SPLINES;
    /**
     * How node overlaps are removed. May be one of the following:
     * <ul>
     * <li>RETAIN</li>
     * <li>SCALE</li>
     * <li>VORONOI</li>
     * <li>SCALEXY</li>
     * <li>ORTHOXY</li>
     * <li>ORTHOYX</li>
     * <li>COMPRESS</li>
     * </ul>
     */
    private int                    overlap             = RETAIN;
    /**
     * Define in which order nodes have to appear.
     * May be one of the following:
     * <ul>
     * <li>NO_ORDERING</li>
     * <li>IN</li>
     * <li>OUT</li>
     * </ul>
     */
    private int                    ordering            = NO_ORDERING;
    /**
     * Set the number of iteration used
     */
    private int                    maxiter;                                              // XXX: program dependant
    /**
     * Use the MAJOR mode, else use the KK one
     */
    private int                    mode                = MAJOR;
    /**
     * Factor damping force motions.
     */
    private double                 damping             = 0.99;
    /**
     * Spring constant used in virtual physical model
     */
    private double                 k                   = 0.3;
    /**
     * Text's size in point
     */
    private double                 fontsize            = 14;
    /**
     * Multiplicative scale factor used to alter the parameters used during
     * crossing minimization
     */
    private double                 mclimit             = 1;
    /**
     * Specifies the minimum separation between all nodes
     */
    private double                 mindist             = 1;
    /**
     * Minamum space between two adjacent nodes in the same rank (in inches)
     */
    private double                 nodesep             = 0.25;
    /**
     * Round label dimensions to integral multiples of the quantum
     */
    private double                 quantum             = 0;
    /**
     * Gives desired rank separation (in inches)
     */
    private double                 ranksep;
    /**
     * Desired aspect ratio. May also be one of the following:
     * <ul>
     * <li>NO_RATIO</li>
     * <li>FILL</li>
     * <li>COMPRESS_RATIO</li>
     * <li>EXPAND</li>
     * <li>AUTO</li>
     * </ul>
     */
    private double                 ratio               = NO_RATIO;
    /**
     * Fraction to increase polygons in order to determine overlapping
     */
    private double                 sep                 = 0.01;
    /**
     * Factor to scale up drawing to allow margin for expansion in Voronoi
     * technique
     */
    private double                 voro_margin         = 0.05;

    /**
     * Specifies the expected number of pixels per inch on a display device
     */
    private double                 dpi                 = 96;
    /**
     * Distance between nodes
     */
    private double                 defaultdist;                                          // XXX: program dependant
    /**
     * Terminating condition
     */
    private double                 epsilon;                                              // XXX: program dependant
    /**
     * Color used as the background for entire canvas
     */
    private Color                  bgcolor;
    /**
     * Text's color
     */
    private Color                  fontcolor           = Color.black;

    /**
     * Node to use as the center of the graph's layout
     */
    private Node                   graphRoot;
    /**
     * Drawing bounding box
     */
    private Rectangle              bb;
    /**
     * Label position (in points)
     */
    private Point                  lp;
    /**
     * Width and height of output pages (in inches)
     */
    private Point2D.Double         page;
    /**
     * Maximum width and height of drawing (in inches)
     */
    private Point2D.Double         size;
    /**
     * Margins around the graph (in inches)
     */
    private Point2D.Double         margin              = new Point2D.Double(0.11, 0.055);
    /**
     * Clipping window on final drawing
     */
    private ViewPort               viewPort;

    private BasicNode              genericNode         = null;
    private Record                 genericRecord       = null;
    private Edge                   genericEdge         = null;

    /**
     * Determine if the generic attribute is a classic node or a record
     */
    private boolean                record              = false;

    /**
     * Create a new empty graph
     * @throws Exception 
     */
    public Graph() throws Exception {
        //нинада
//        this.genericNode = new BasicNode(this, "node");
//        this.genericRecord = new Record(this, "node");
//        this.genericEdge = new Edge(this, this.genericNode, this.genericRecord);
    }

    /**
     * Create a new empty graph
     *@param id a unique ID for this graph
     *@throws Exception 
     */
    public Graph(String id) throws Exception {
        //нинада
//        this.genericNode = new BasicNode(this, "node");
//        this.genericRecord = new Record(this, "node");
//        this.genericEdge = new Edge(this, this.genericNode, this.genericRecord);
	this.setId(id);
    }

    /**
     * Add a node to the nodes list
     * 
     * @param node
     *            New node to add
     */
    public void addNode(Node node) {
        if (this.genericNodes == null) {
            this.nodes = new Node[1];
            this.nodes[0] = node;
            this.genericNodes = this.nodes;
        } else {
            boolean addNode = !(node instanceof SubRecord);
            for (int i = 0; i < this.genericNodes.length; i++) {
                if (this.genericNodes[i].getId() != null)
                    addNode &= (!this.genericNodes[i].getId().equals(node.getId()));
                if (!addNode)
                    break;
            }
            if (addNode) {
                if (this.nodes == null) {
                    this.nodes = new Node[1];
                    this.nodes[0] = node;
                } else {
                    Node[] tmp = new Node[this.nodes.length + 1];
                    System.arraycopy(this.nodes, 0, tmp, 0, this.nodes.length);
                    tmp[tmp.length - 1] = node;
                    this.nodes = tmp;
                }
                addGenericNode(node);
            }
        }
    }

    public void addGenericNode(Node node) {
        if (this.genericNodes == null) {
            this.genericNodes = new Node[1];
            this.genericNodes[0] = node;
        } else {
            boolean addNode = !(node instanceof SubRecord);
            for (int i = 0; i < this.genericNodes.length; i++) {
                if (this.genericNodes[i].getId() != null)
                    addNode &= (!this.genericNodes[i].getId().equals(node.getId()));
                if (!addNode)
                    break;
            }
            if (addNode) {
                Node[] tmp = new Node[this.genericNodes.length + 1];
                System.arraycopy(this.genericNodes, 0, tmp, 0,
                        this.genericNodes.length);
                tmp[tmp.length - 1] = node;
                this.genericNodes = tmp;
            }
        }
    }

    /**
     * Add an edge to the edges list
     * 
     * @param edge
     *            New edge to add
     */
    public void addEdge(Edge edge) {
        if (this.edges == null) {
            this.edges = new Edge[1];
            this.edges[0] = edge;
        } else {
            boolean addEdge = true;
            for (int i = 0; i < this.edges.length; i++) {
                addEdge &= !(this.edges[i].getStart().equals(
                        edge.getStart()) && this.edges[i].getEnd()
                        .equals(edge.getEnd()));
                if (!addEdge)
                    break;
            }
            if (addEdge) {
                Edge[] tmp = new Edge[this.edges.length + 1];
                System.arraycopy(this.edges, 0, tmp, 0, this.edges.length);
                tmp[tmp.length - 1] = edge;
                this.edges = tmp;
            }
        }
        Graph.addNode(edge.getStart().getRoot(), edge.getStart());
        Graph.addNode(edge.getEnd().getRoot(), edge.getEnd());
    }

    /**
     * Add a given node either to a Graph or a SubGraph, depending on the node
     * root's type
     * @param root
     *              Root element to add the node
     * @param element
     *              Node to add
     */
    public static void addNode(Object root, Node element) {
        if (root instanceof Graph) {
            ((Graph) root).addNode(element);
        } else if (root instanceof SubGraph) {
            ((SubGraph) root).addNode(element);
        }
        /*        if (root instanceof Graph) {
         ((Graph) root).addNode(element);
         } else if (root instanceof SubGraph) {
         ((SubGraph) root).addNode(element);
         }*/
    }

    /**
     * Add a new Layer name in the graph's list
     * @param layer name of a new layer
     */
    public void addLayer(String layer) {
        if (this.getLayers() == null) {
            this.setLayers(new String[1]);
            this.getLayers()[0] = layer;
        } else {
            boolean add = true;
            for (int i = 0; i < this.getLayers().length; i++) {
                add &= !this.getLayers()[i].equals(layer);
                if (!add)
                    break;
            }
            if (add) {
                String[] tmp = new String[this.getLayers().length + 1];
                System.arraycopy(this.getLayers(), 0, tmp, 0, this.getLayers().length);
                tmp[tmp.length - 1] = layer;
                this.setLayers(tmp);
            }
        }
    }

    /**
     * Add a new path to the system's list of font path
     * @param path new path to add
     */
    public void addFontPath(String path) {
        if (this.getFontpath() == null) {
            this.setFontpath(new String[1]);
            this.getFontpath()[0] = path;
        } else {
            boolean add = true;
            for (int i = 0; i < this.getFontpath().length; i++) {
                add &= !this.getFontpath()[i].equals(path);
                if (!add)
                    break;
            }
            if (add) {
                String[] tmp = new String[this.getFontpath().length + 1];
                System
                        .arraycopy(this.getFontpath(), 0, tmp, 0,
                                this.getFontpath().length);
                tmp[tmp.length - 1] = path;
                this.setFontpath(tmp);
            }
        }
    }

    /**
     * Remove a node to the nodes list
     * 
     * @param node
     *            Node to remove (if it's in the list)
     */
    public void removeNode(Node node) {
        // First, remove incoming and outcoming edges from that particular node
        Edge[] in = node.getIn(), out = node.getOut();
        if (in != null)
            for (int i = 0; i < in.length; i++)
                removeEdge(in[i]);
        if (out != null)
            for (int i = 0; i < out.length; i++)
                removeEdge(out[i]);
        if (this.nodes != null) {
            for (int i = 0; i < this.nodes.length; i++) {
                if (this.nodes[i].equals(node)) {
                    Node[] tmp = new Node[this.nodes.length - 1];
                    System.arraycopy(this.nodes, 0, tmp, 0, i);
                    System.arraycopy(this.nodes, i + 1, tmp, i,
                            this.nodes.length - i - 1);
                    this.nodes = tmp;
                    break;
                }
            }
            if (this.nodes.length == 0) {
                this.nodes = null;
            }
        }
    }

    /**
     * Remove an edge to the edges list
     * 
     * @param edge
     *            Edge to remove (if it's in the list)
     */
    public void removeEdge(Edge edge) {
        if (this.edges != null) {
            for (int i = 0; i < this.edges.length; i++) {
                if (this.edges[i].equals(edge)) {
                    Edge[] tmp = new Edge[this.edges.length - 1];
                    System.arraycopy(this.edges, 0, tmp, 0, i);
                    System.arraycopy(this.edges, i + 1, tmp, i,
                            this.edges.length - i - 1);
                    this.edges = tmp;
                    break;
                }
            }
            if (this.edges.length == 0) {
                this.edges = null;
            }
        }
    }

    /**
     * Remove a layer to the layer's list
     * @param layer layer to remove
     */
    public void removeLayer(String layer) {
        if (this.getLayers() != null) {
            for (int i = 0; i < this.getLayers().length; i++) {
                if (this.getLayers()[i].equals(layer)) {
                    String[] tmp = new String[this.getLayers().length - 1];
                    System.arraycopy(this.getLayers(), 0, tmp, 0, i);
                    System.arraycopy(this.getLayers(), i + 1, tmp, i,
                            this.getLayers().length - i - 1);
                    this.setLayers(tmp);
                }
            }
            if (this.getLayers().length == 0) {
                this.setLayers(null);
            }
        }
    }

    /**
     * Remove a path to the system's list of font paths
     * @param path a path to remove
     */
    public void removeFontPath(String path) {
        if (this.getFontpath() != null) {
            for (int i = 0; i < this.getFontpath().length; i++) {
                if (this.getFontpath()[i].equals(path)) {
                    String[] tmp = new String[this.getFontpath().length - 1];
                    System.arraycopy(this.getFontpath(), 0, tmp, 0, i);
                    System.arraycopy(this.getFontpath(), i + 1, tmp, i,
                            this.getFontpath().length - i - 1);
                    this.setFontpath(tmp);
                }
            }
            if (this.getFontpath().length == 0) {
                this.setFontpath(null);
            }
        }
    }

    public void changeOption(String name, String value) {
        String v;
        if (value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
            v = value.substring(1, value.length() - 1);
        } else
            v = value;
        // Removing \ followed by \n
        v = v.replaceAll("\\\\\\n", "");
        //v = v.replaceAll("\\n", "\n");

        if (name.equals("bb"))
            this.setBb(DotUtils.readRectangle(v));
        else if (name.equals("bgcolor"))
            this.setBgcolor(DotUtils.readColor(v));
        else if (name.equals("center"))
            this.setCenter(DotUtils.readBoolean(v));
        else if (name.equals("charset"))
            this.setCharset(readAttributeNumber(v));
        else if (name.equals("clusterrank"))
            this.setClusterrank(readAttributeNumber(v));
        else if (name.equals("compound"))
            this.setCompound(DotUtils.readBoolean(v));
        else if (name.equals("concentrate"))
            this.setConcentrate(DotUtils.readBoolean(v));
        else if (name.equals("damping"))
            this.setDamping(DotUtils.readDouble(v));
        else if (name.equals("defaultdist"))
            this.setDefaultdist(DotUtils.readDouble(v));
        else if (name.equals("dim"))
            this.setDim(DotUtils.readInteger(v));
        else if (name.equals("dpi")
                || name.equals("resolution")) // resolution
            this.setDpi(DotUtils.readDouble(v));
        else if (name.equals("epsilon"))
            this.setEpsilon(DotUtils.readDouble(v));
        else if (name.equals("fontcolor"))
            this.setFontcolor(DotUtils.readColor(v));
        else if (name.equals("fontname"))
            this.setFontname(v);
        else if (name.equals("fontpath"))
            addFontPath(v);
        else if (name.equals("fontsize"))
            this.setFontsize(DotUtils.readDouble(v));
        else if (name.equals("k"))
            this.setK(DotUtils.readDouble(v));
        else if (name.equals("label"))
            this.setLabel(v);
        else if (name.equals("labeljust"))
            this.setLabeljust(readAttributeNumber(v));
        else if (name.equals("labelloc"))
            this.setLabelloc(readAttributeNumber(v));
        else if (name.equals("layers"))
            addLayer(v);
        else if (name.equals("layersep"))
            this.setLayersep(v);
        else if (name.equals("lp"))
            this.setLp(DotUtils.readPoint(v));
        else if (name.equals("margin"))
            this.setMargin(DotUtils.readPointf(v));
        else if (name.equals("maxiter"))
            this.setMaxiter(DotUtils.readInteger(v));
        else if (name.equals("mclimit"))
            this.setMclimit(DotUtils.readDouble(v));
        else if (name.equals("mindist"))
            this.setMindist(DotUtils.readDouble(v));
        else if (name.equals("mode"))
            this.setMode(readAttributeNumber(v));
        else if (name.equals("model"))
            this.setModel(readAttributeNumber(v));
        else if (name.equals("nodesep"))
            this.setNodesep(DotUtils.readDouble(v));
        else if (name.equals("nojustify"))
            this.setNojustify(DotUtils.readBoolean(v));
        else if (name.equals("normalize"))
            this.setNormalize(DotUtils.readBoolean(v));
        else if (name.equals("ordering"))
            this.setOrdering(readAttributeNumber(v));
        else if (name.equals("outputorder"))
            this.setOutputorder(readAttributeNumber(v));
        else if (name.equals("overlap"))
            this.setOverlap(readAttributeNumber(v));
        else if (name.equals("pack"))
            try {
                this.setPackValue(DotUtils.readInteger(v));
                this.setPack(true);
            } catch (NumberFormatException e) {
                this.setPack(DotUtils.readBoolean(v));
            }
        else if (name.equals("packmode"))
            this.setPackmode(readAttributeNumber(v));
        else if (name.equals("page"))
            this.setPage(DotUtils.readPointf(v));
        else if (name.equals("pagedir"))
            this.setPagedir(readAttributeNumber(v));
        else if (name.equals("quantum"))
            this.setQuantum(DotUtils.readDouble(v));
        else if (name.equals("rankdir"))
            this.setRankdir(readAttributeNumber(v));
        else if (name.equals("ranksep"))
            this.setRanksep(DotUtils.readDouble(v));
        else if (name.equals("ratio"))
            this.setRatio(readRatio(v));
        else if (name.equals("remincross"))
            this.setRemincross(DotUtils.readBoolean(v));
        else if (name.equals("rotate"))
            this.setRotate(DotUtils.readInteger(v));
        else if(name.equals("orientation"))
            if(this.getRotate() == 0)
                this.setRotate(DotUtils.readOrientation(v));
        else if (name.equals("samplepoints"))
            this.setSamplepoints(DotUtils.readInteger(v));
        else if (name.equals("searchsize"))
            this.setSearchsize(DotUtils.readInteger(v));
        else if (name.equals("sep"))
            this.setSep(DotUtils.readDouble(v));
        else if (name.equals("showboxes"))
            this.setShowboxes(DotUtils.readInteger(v));
        else if (name.equals("size"))
            this.setSize(DotUtils.readPointf(v));
        else if (name.equals("splines"))
            this.setSplines(readAttributeNumber(v));
        else if (name.equals("start")) {
            //this.k = DotUtils.readDouble(v);
        } else if (name.equals("stylesheet"))
            this.setStylesheet(v);
        else if (name.equals("target"))
            this.setTarget(v);
        else if (name.equals("truecolor")) // FIXME: must be checked at the end
            this.setTruecolor(new Boolean(DotUtils.readBoolean(v)));
        else if (name.equals("URL") || name.equals("href"))
            this.setURL(v);
        else if (name.equals("viewport"))
            this.setViewPort(DotUtils.readViewPort(v));
        else if (name.equals("voro_margin"))
            this.setVoro_margin(DotUtils.readDouble(v));
        else
            System.err.println("Graph attribute \"" + name
                    + "\" does not exist");
    }

    private double readRatio(String v) {
        for (int i = 0; i < ratioAttributeNames.length; i++) {
            if (ratioAttributeNames[i].equalsIgnoreCase(v))
                return -i;
        }
        return -1;
    }

    private int readAttributeNumber(String v) {
        for (int i = 0; i < this.attributeNames.length; i++) {
            if (this.attributeNames[i].equalsIgnoreCase(v))
                return i;
        }
        System.err.println("Graph value \"" + v + "\" does not exist");
        return -1;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        String g = (this.isDirected() ? "di" : "") + "graph " + this.getId() + " {\n";
        g += graphOptions();
        if (this.nodes != null)
            for (int i = 0; i < this.nodes.length; i++) {
                g += this.nodes[i];
            }
        if (this.edges != null) {
            for (int i = 0; i < this.edges.length; i++) {
                g += this.edges[i];
            }
        }
        return g + "}";
    }

    private String graphOptions() {
        String o = "";
        if (this.getDamping() != 0.99)
            o += printOption("Damping", this.getDamping());
        if (this.getK() != 0.3)
            o += printOption("K", this.getK());
        if (this.getURL() != null)
            o += printOption("URL", this.getURL());
        if (this.getBb() != null)
            o += printOption("bb", this.getBb());
        if (this.getBgcolor() != null)
            o += printOption("bgcolor", this.getBgcolor());
        if (this.isCenter())
            o += printOption("center", this.isCenter());
        if (this.getCharset() != UTF_8)
            o += printNamedOption("charset", this.getCharset());
        if (this.getClusterrank() != LOCAL)
            o += printNamedOption("clusterrank", this.getClusterrank());
        if (this.isCompound())
            o += printOption("compound", this.isCompound());
        if (this.isConcentrate())
            o += printOption("concentrate", this.isConcentrate());
        if (this.getDim() != 2)
            o += printOption("dim", this.getDim());
        if (this.getDpi() != 96)
            o += printOption("dpi", this.getDpi());
        if (!this.getFontcolor().equals(Color.black))
            o += printOption("fontcolor", this.getFontcolor());
        if (!this.getFontname().equals("Times-Roman"))
            o += printOption("fontname", this.getFontname());
        if (this.getFontsize() != 14)
            o += printOption("fontsize", this.getFontsize());
        if (!this.getLabel().equals(""))
            o += printOption("label", this.getLabel());
        if (this.getLabeljust() != CENTER)
            o += printNamedOption("labeljust", this.getLabeljust());
        if (this.getLabelloc() != BOTTOM)
            o += printNamedOption("labelloc", this.getLabelloc());
        if (this.getLayers() != null)
            o += printOption("layers", this.getLayers());
        if (!this.getLayersep().equals(""))
            o += printOption("layersep", this.getLayersep());
        if (this.getLp() != null)
            o += printOption("lp", this.getLp());
        if (this.getMargin() != null)
            if (this.getMargin().x != 0.11 || this.getMargin().y != 0.055)
                o += printOption("margin", this.getMargin());
        if (this.getMclimit() != 1)
            o += printOption("mclimit", this.getMclimit());
        if (this.getMindist() != 1)
            o += printOption("mindist", this.getMindist());
        if (this.getMode() != MAJOR)
            o += printNamedOption("mode", this.getMode());
        if (this.getModel() != SHORTPATH)
            o += printNamedOption("model", this.getModel());
        if (this.getNodesep() != 0.25)
            o += printOption("nodesep", this.getNodesep());
        if (this.isNojustify())
            o += printOption("nojustify", this.isNojustify());
        if (this.isNormalize())
            o += printOption("normalize", this.isNormalize());
        if (this.getOrdering() != NO_ORDERING)
            o += printNamedOption("ordering", this.getOrdering());
        if (this.getRotate() != 0)
            o += printOption("rotate", this.getRotate());
        if (this.getOutputorder() != BREADTHFIRST)
            o += printNamedOption("outputorder", this.getOutputorder());
        if (this.getOverlap() != RETAIN)
            o += printNamedOption("overlap", this.getOverlap());
        if (this.isPack()) // TODO: use packValue
            o += printOption("pack", this.isPack());
        if (this.getPackmode() != NODE)
            o += printNamedOption("packmode", this.getPackmode());
        if (this.getPage() != null)
            o += printOption("page", this.getPage());
        if (this.getPagedir() != BL)
            o += printNamedOption("pagedir", this.getPagedir());
        if (this.getQuantum() != 0)
            o += printOption("quantum", this.getQuantum());
        if (this.getRankdir() != TB)
            o += printNamedOption("rankdir", this.getRankdir());
        if (this.getRatio() != NO_RATIO)
            o += printRatioOption("ratio", this.getRatio());
        if (this.isRemincross())
            o += printOption("remincross", this.isRemincross());
        if (this.getGraphRoot() != null)
            o += printOption("root", this.getGraphRoot());
        if (this.getSamplepoints() != 8)
            o += printOption("samplepoints", this.getSamplepoints());
        if (this.getSearchsize() != 30)
            o += printOption("searchsize", this.getSearchsize());
        if (this.getSep() != 0.01)
            o += printOption("sep", this.getSep());
        if (this.getShowboxes() != 0)
            o += printOption("showboxes", this.getShowboxes());
        if (this.getSize() != null)
            o += printOption("size", this.getSize());
        if (this.getSplines() != NO_SPLINES)
            o += printNamedOption("splines", this.getSplines());
        if (this.getStartStyle() != RANDOM)
            o += printStartTypeOption("start", this.getStartStyle());
        if (this.getStylesheet() != null)
            o += printOption("stylesheet", this.getStylesheet());
        if (this.getTarget() != null)
            o += printOption("target", this.getTarget());
        if (this.getTruecolor() != null)
            o += printOption("truecolor", this.getTruecolor().booleanValue());
        if (this.getViewPort() != null)
            o += printOption("viewport", this.getViewPort());
        if (this.getVoro_margin() != 0.05)
            o += printOption("voro_margin", this.getVoro_margin());
        if (this.getFontpath() != null)
            o += printOption("fontpath", this.getFontpath());
        // o += printOption("defaultdist", this.defaultdist);
        // o += printOption("epsilon", this.epsilon);
        // o += printOption("maxiter", this.maxiter);
        // o += printOption("ranksep", this.ranksep);
        return o;
    }

    private String printOption(String attribute, Node rootNode) {
        return attribute + " = " + rootNode.getId() + ";\n";
    }

    private String printNamedOption(String attribute, int i) {
        if (i < 0)
            return "";
        return attribute + " = \"" + this.attributeNames[i] + "\";\n";
    }

    private String printStartTypeOption(String attribute, int i) {
        return attribute + " = \"" + this.attributeNames[i]
                + (this.getStartSeed() == NO_SEED ? "" : (" " + this.getStartSeed()))
                + "\";\n";
    }

    private String printRatioOption(String attribute, double d) {
        String value;
        if (d != 0) {
            if (d == FILL)
                value = "\"fill\"";
            else if (d == COMPRESS_RATIO)
                value = "\"compress\"";
            else if (d == EXPAND)
                value = "\"expand\"";
            else if (d == AUTO)
                value = "\"auto\"";
            else
                value = Double.toString(d);
            return attribute + " = \"" + value + "\";\n";
        }
        return "";
    }

    private String printOption(String attribute, ViewPort value) {
        if (value != null)
            return attribute + " = \"" + value.getDimensions().x + ","
                    + value.getDimensions().y + "," + value.getZ() + ","
                    + value.getPosition().x + "," + value.getPosition().y + "\";\n";
        return "";
    }

    private String printOption(String attribute, Point2D.Double value) {
        return attribute + " = \"" + value.x + "," + value.y + "\";\n";
    }

    private String printOption(String attribute, Point value) {
        String o = attribute + "=\"";
        for (int i = 0; i < value.getCoords().length; i++) {
            if (i > 0)
                o += ",";
            o += value.getCoords()[i];
        }
        return o + "\"" + (value.isChange() ? "" : "!") + ";\n";
    }

    private String printOption(String attribute, Rectangle value) {
        return attribute + " = \"" + value.getX1() + ", " + value.getY1() + ", "
                + value.getX2() + ", " + value.getY2() + "\";\n";
    }

    private String printOption(String attribute, Color value) {
        if (value != null) {
            String r = Integer.toHexString(value.getRed()), g = Integer
                    .toHexString(value.getGreen()), b = Integer
                    .toHexString(value.getBlue()), a = Integer
                    .toHexString(value.getAlpha());
            if (r.length() == 1)
                r = "0" + r;
            if (g.length() == 1)
                g = "0" + g;
            if (b.length() == 1)
                b = "0" + b;
            if (a.length() == 1)
                a = "0" + a;
            return attribute + " = \"#" + r + g + b + a + "\";\n";
        }
        return "";
    }

    private String printOption(String attribute, String[] value) {
        String o = attribute + " = \"";
        for (int i = 0; i < value.length; i++) {
            if (i > 0)
                o += " ";
            o += value[i];
        }
        return o + "\";\n";
    }

    private String printOption(String attribute, String value) {
        if (value != null)
            return attribute + " = \"" + value + "\";\n";
        return "";
    }

    private String printOption(String attribute, boolean value) {
        return attribute + " = \"" + value + "\";\n";
    }

    private String printOption(String attribute, double value) {
        return attribute + " = " + value + ";\n";
    }

    private String printOption(String attribute, int value) {
        return attribute + " = " + value + ";\n";
    }

    /**
     * @return Returns the edges.
     */
    public Edge[] getEdges() {
        return this.edges;
    }

    /**
     * @return Returns the nodes.
     */
    public Node[] getNodes() {
        return this.nodes;
    }

    /**
     * Calling method in order to test
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            // FSM.dot
            Graph g = new Graph();
            g.setId("finite_state_machine");
            g.setDirected(true);
            g.setRankdir(Graph.LR);
            BasicNode LR_0 = new BasicNode(g, "LR_0"), LR_3 = new BasicNode(g,
                    "LR_3"), LR_4 = new BasicNode(g, "LR_4"), LR_8 = new BasicNode(
                    g, "LR_8"), LR_1 = new BasicNode(g, "LR_1"), LR_2 = new BasicNode(
                    g, "LR_2"), LR_5 = new BasicNode(g, "LR_5"), LR_6 = new BasicNode(
                    g, "LR_6"), LR_7 = new BasicNode(g, "LR_7");

            g.addNode(LR_0);
            g.addNode(LR_3);
            g.addNode(LR_4);
            g.addNode(LR_8);

            LR_0.setRegular(true);
            LR_0.setPeripheries(2);
            LR_3.setRegular(true);
            LR_3.setPeripheries(2);
            LR_4.setRegular(true);
            LR_4.setPeripheries(2);
            LR_8.setRegular(true);
            LR_8.setPeripheries(2);

            LR_1.setRegular(true);
            LR_2.setRegular(true);
            LR_5.setRegular(true);
            LR_6.setRegular(true);
            LR_7.setRegular(true);

            Edge e1 = new Edge(g, LR_0, LR_2), e2 = new Edge(g, LR_0, LR_1), e3 = new Edge(
                    g, LR_1, LR_3), e4 = new Edge(g, LR_2, LR_6), e5 = new Edge(
                    g, LR_2, LR_5), e6 = new Edge(g, LR_2, LR_4), e7 = new Edge(
                    g, LR_5, LR_7), e8 = new Edge(g, LR_5, LR_5), e9 = new Edge(
                    g, LR_6, LR_6), e10 = new Edge(g, LR_6, LR_5), e11 = new Edge(
                    g, LR_7, LR_8), e12 = new Edge(g, LR_7, LR_5), e13 = new Edge(
                    g, LR_8, LR_6), e14 = new Edge(g, LR_8, LR_5);

            e1.setLabel("SS(B)");
            e2.setLabel("SS(S)");
            e3.setLabel("SS($end)");
            e4.setLabel("SS(b)");
            e5.setLabel("SS(a)");
            e6.setLabel("S(A)");
            e7.setLabel("S(b)");
            e8.setLabel("S(a)");
            e9.setLabel("S(b)");
            e10.setLabel("S(a)");
            e11.setLabel("S(b)");
            e12.setLabel("S(a)");
            e13.setLabel("S(b)");
            e14.setLabel("S(a)");

            System.out.println(g);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getFontname() {
        return fontname;
    }

    public void setFontname(String fontname) {
        this.fontname = fontname;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getStylesheet() {
        return stylesheet;
    }

    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    public int getCharset() {
        return charset;
    }

    public void setCharset(int charset) {
        this.charset = charset;
    }

    public String[] getFontpath() {
        return fontpath;
    }

    public void setFontpath(String[] fontpath) {
        this.fontpath = fontpath;
    }

    public String[] getLayers() {
        return layers;
    }

    public void setLayers(String[] layers) {
        this.layers = layers;
    }

    public String getLayersep() {
        return layersep;
    }

    public void setLayersep(String layersep) {
        this.layersep = layersep;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public boolean isCenter() {
        return center;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    public boolean isCompound() {
        return compound;
    }

    public void setCompound(boolean compound) {
        this.compound = compound;
    }

    public boolean isConcentrate() {
        return concentrate;
    }

    public void setConcentrate(boolean concentrate) {
        this.concentrate = concentrate;
    }

    public boolean isNojustify() {
        return nojustify;
    }

    public void setNojustify(boolean nojustify) {
        this.nojustify = nojustify;
    }

    public boolean isNormalize() {
        return normalize;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }

    public boolean isRemincross() {
        return remincross;
    }

    public void setRemincross(boolean remincross) {
        this.remincross = remincross;
    }

    public Boolean getTruecolor() {
        return truecolor;
    }

    public void setTruecolor(Boolean truecolor) {
        this.truecolor = truecolor;
    }

    public int getRankdir() {
        return rankdir;
    }

    public void setRankdir(int rankdir) {
        this.rankdir = rankdir;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public int getLabeljust() {
        return labeljust;
    }

    public void setLabeljust(int labeljust) {
        this.labeljust = labeljust;
    }

    public int getLabelloc() {
        return labelloc;
    }

    public void setLabelloc(int labelloc) {
        this.labelloc = labelloc;
    }

    public int getOutputorder() {
        return outputorder;
    }

    public void setOutputorder(int outputorder) {
        this.outputorder = outputorder;
    }

    public boolean isPack() {
        return pack;
    }

    public void setPack(boolean pack) {
        this.pack = pack;
    }

    public int getPackValue() {
        return packValue;
    }

    public void setPackValue(int packValue) {
        this.packValue = packValue;
    }

    public int getPackmode() {
        return packmode;
    }

    public void setPackmode(int packmode) {
        this.packmode = packmode;
    }

    public int getPagedir() {
        return pagedir;
    }

    public void setPagedir(int pagedir) {
        this.pagedir = pagedir;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public int getSamplepoints() {
        return samplepoints;
    }

    public void setSamplepoints(int samplepoints) {
        this.samplepoints = samplepoints;
    }

    public int getSearchsize() {
        return searchsize;
    }

    public void setSearchsize(int searchsize) {
        this.searchsize = searchsize;
    }

    public int getShowboxes() {
        return showboxes;
    }

    public void setShowboxes(int showboxes) {
        this.showboxes = showboxes;
    }

    public int getClusterrank() {
        return clusterrank;
    }

    public void setClusterrank(int clusterrank) {
        this.clusterrank = clusterrank;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public int getStartStyle() {
        return startStyle;
    }

    public void setStartStyle(int startStyle) {
        this.startStyle = startStyle;
    }

    public int getStartSeed() {
        return startSeed;
    }

    public void setStartSeed(int startSeed) {
        this.startSeed = startSeed;
    }

    public int getSplines() {
        return splines;
    }

    public void setSplines(int splines) {
        this.splines = splines;
    }

    public int getOverlap() {
        return overlap;
    }

    public void setOverlap(int overlap) {
        this.overlap = overlap;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    public int getMaxiter() {
        return maxiter;
    }

    public void setMaxiter(int maxiter) {
        this.maxiter = maxiter;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public double getDamping() {
        return damping;
    }

    public void setDamping(double damping) {
        this.damping = damping;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getFontsize() {
        return fontsize;
    }

    public void setFontsize(double fontsize) {
        this.fontsize = fontsize;
    }

    public double getMclimit() {
        return mclimit;
    }

    public void setMclimit(double mclimit) {
        this.mclimit = mclimit;
    }

    public double getMindist() {
        return mindist;
    }

    public void setMindist(double mindist) {
        this.mindist = mindist;
    }

    public double getNodesep() {
        return nodesep;
    }

    public void setNodesep(double nodesep) {
        this.nodesep = nodesep;
    }

    public double getQuantum() {
        return quantum;
    }

    public void setQuantum(double quantum) {
        this.quantum = quantum;
    }

    public double getRanksep() {
        return ranksep;
    }

    public void setRanksep(double ranksep) {
        this.ranksep = ranksep;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getSep() {
        return sep;
    }

    public void setSep(double sep) {
        this.sep = sep;
    }

    public double getVoro_margin() {
        return voro_margin;
    }

    public void setVoro_margin(double voro_margin) {
        this.voro_margin = voro_margin;
    }

    public double getDpi() {
        return dpi;
    }

    public void setDpi(double dpi) {
        this.dpi = dpi;
    }

    public double getDefaultdist() {
        return defaultdist;
    }

    public void setDefaultdist(double defaultdist) {
        this.defaultdist = defaultdist;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public Color getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(Color bgcolor) {
        this.bgcolor = bgcolor;
    }

    public Color getFontcolor() {
        return fontcolor;
    }

    public void setFontcolor(Color fontcolor) {
        this.fontcolor = fontcolor;
    }

    public Node getGraphRoot() {
        return graphRoot;
    }

    public void setGraphRoot(Node graphRoot) {
        this.graphRoot = graphRoot;
    }

    public Rectangle getBb() {
        return bb;
    }

    public void setBb(Rectangle bb) {
        this.bb = bb;
    }

    public Point getLp() {
        return lp;
    }

    public void setLp(Point lp) {
        this.lp = lp;
    }

    public Point2D.Double getPage() {
        return page;
    }

    public void setPage(Point2D.Double page) {
        this.page = page;
    }

    public Point2D.Double getSize() {
        return size;
    }

    public void setSize(Point2D.Double size) {
        this.size = size;
    }

    public Point2D.Double getMargin() {
        return margin;
    }

    public void setMargin(Point2D.Double margin) {
        this.margin = margin;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    public void setViewPort(ViewPort viewPort) {
        this.viewPort = viewPort;
    }

    public BasicNode getGenericNode() {
        return genericNode;
    }

    public void setGenericNode(BasicNode genericNode) {
        this.genericNode = genericNode;
    }

    public Record getGenericRecord() {
        return genericRecord;
    }

    public void setGenericRecord(Record genericRecord) {
        this.genericRecord = genericRecord;
    }

    public Edge getGenericEdge() {
        return genericEdge;
    }

    public void setGenericEdge(Edge genericEdge) {
        this.genericEdge = genericEdge;
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }
}
