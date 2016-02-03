/*   FILE: SubGraph.java
 *   DATE OF CREATION:   Apr 4 2005
 *   AUTHOR :            Eric Mounhem (skbo@lri.fr)
 *   Copyright (c) INRIA, 2004-2005. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 * 
 * $Id: SubGraph.java 576 2007-03-29 18:32:53Z epietrig $
 */

package net.claribole.zgrviewer.dot;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * A SubGraph may be considered as a graph inside another. Its elements (nodes,
 * subgraphs ou cluster) can point or be pointed with edges.
 * @author Eric Mounhem
 */
public class SubGraph extends Node {
    /**
     * All nodes ar placed on the same rank
     */
    static final int      SAME                = 0;
    /**
     * All nodes are placed on the minimum rank
     */
    static final int      MIN                 = 1;
    /**
     * All nodes are placed on the minimum rank, and the only nodes on the
     * minimum rank belong to some subgraph whose rank attribute is SOURCE or
     * MIN
     */
    static final int      SOURCE              = 2;
    /**
     * @see SubGraph#MIN
     */
    static final int      MAX                 = 3;
    /**
     * @see SubGraph#SOURCE
     */
    final static int      SINK                = 4;

    final static String[] attributeNames      = { "same", "min", "source",
            "max", "sink"                    };

    static final String[] ratioAttributeNames = { "", "fill", "compress",
            "expand", "auto"                 };

    /**
     * Rank constraints on the nodes in a subgraph
     */
    private int                   rank;

    //Graph rootGraph;

    /**
     * Create a new SubGraph
     * @param root root of the graph
     * @param id identificator of the subGraph
     * @throws Exception
     */
    public SubGraph(Object root, String id) throws Exception {
        super(root, id);
        //нинада
//        this.genericNode = new BasicNode(this, "\"node\"");
//        this.genericRecord = new Record(this, "\"node\"");
//        this.genericEdge = new Edge(this, this.genericNode, this.genericRecord);
        this.setGenericGraph(new Graph());
        if (root instanceof Graph)
            ((Graph) root).addNode(this);
        else
            ((SubGraph) root).addNode(this);
    }

    public SubGraph(Object root) throws Exception {
        super(root);
        this.setGenericNode(new BasicNode(this, "node"));
        this.setGenericRecord(new Record(this, "node"));
        this.setGenericEdge(new Edge(this, this.getGenericNode(), this.getGenericRecord()));
        this.setGenericGraph(new Graph());
        if (root instanceof Graph)
            ((Graph) root).addNode(this);
        else
            ((SubGraph) root).addNode(this);
    }

    /*private*/private Node[] nodes;

    /*private*/private Edge[] edges;

    /**
     * Drawing bounding box
     */
    private Rectangle          bb;
    /**
     * Color used as the background for entire canvas
     */
    private Color              bgcolor;
    /**
     * Center the drawing on the canvas
     */
    //boolean center = false;
    /**
     * Specifies the character encoding used. One of the following: <ul> <li>UTF_8</li> <li>ISO_8859_1</li> </ul>
     */
    //int charset = UTF_8;
    /**
     * Mode used for handling clusters. May be one of the following: <ul> <li>NONE</li> <li>GLOBAL</li> <li>LOCAL</li> </ul>
     */
    //int clusterrank = LOCAL;
    /**
     * Allow edges betwenn clusters
     */
    //boolean compound = false;
    /**
     * Use edges concentrators
     */
    private boolean            concentrate   = false;
    /**
     * Factor damping force motions.
     */
    //double damping = 0.99;
    /**
     * Distance between nodes
     */
    private double             defaultdist;
    /**
     * Number of dimensions for the layout
     */
    private int                dim           = 2;
    /**
     * Directed or undirected graph
     */
    private boolean            directed;
    /**
     * Specifies the expected number of pixels per inch on a display device
     */
    //double dpi = 96;
    //Edge[] edges;
    /**
     * Terminating condition
     */
    //double epsilon;
    /**
     * Text's color
     */
    //Color fontcolor = Color.black;
    /**
     * Name of the font used to write graph's label
     */
    //String fontname = "Times-Roman";
    /**
     * Tells to GD where to find fonts
     */
    //String[] fontpath = null;
    /**
     * Text's size in point
     */
    //double fontsize = 14;
    private Edge               genericEdge   = null;
    private BasicNode          genericNode   = null;
    private Record             genericRecord = null;
    private Graph              genericGraph  = null;
    /**
     * Node to use as the center of the graph's layout
     */
    //Node graphRoot;
    /**
     * Identification string of a graph
     */
    //String id;
    /**
     * Spring constant used in virtual physical model
     */
    //double k = 0.3;
    /**
     * Label of the graph
     */
    //String label = "";
    /**
     * Label justification (CENTER, LEFT or RIGHT)
     */
    private int                labeljust     = Graph.CENTER;
    /**
     * Label localisation (TOP or BOTTOM)
     */
    private int                labelloc      = Graph.BOTTOM;
    /**
     * List of layer names for output
     */
    //String[] layers = null;
    /**
     * Layers separators
     */
    //String layersep = "";
    /**
     * Label position (in points)
     */
    private Point              lp;
    /**
     * Margins around the graph (in inches)
     */
    //Point2D.Double margin = new Point2D.Double(0.11, 0.055);
    /**
     * Set the number of iteration used
     */
    //int maxiter;
    /**
     * Multiplicative scale factor used to alter the parameters used during crossing minimization
     */
    //double mclimit = 1;
    /**
     * Specifies the minimum separation between all nodes
     */
    //double mindist = 1;
    /**
     * Use the MAJOR mode, else use the KK one
     */
    //int mode = Graph.MAJOR;
    /**
     * How the distance matrix is computed. May be one of the following: <ul> <li>SHORTPATH</li> <li>CIRCUIT</li> <li>SUBSET</li> </ul>
     */
    //int model = Graph.SHORTPATH;
    //Node[] nodes;
    /**
     * Minamum space between two adjacent nodes in the same rank (in inches)
     */
    private double             nodesep       = 0.25;
    /**
     * Don't justify multilines labels
     */
    //boolean nojustify = false;
    /**
     * Use the first point as origin and make the first edge horizontal
     */
    private boolean            normalize     = false;
    /**
     * Define in which order nodes have to appear. May be one of the following: <ul> <li>NO_ORDERING</li> <li>IN</li> <li>OUT</li> </ul>
     */
    private int                ordering      = Graph.NO_ORDERING;
    /**
     * Specify drawing order of nodes and edges. One of the following: <ul> <li>BREADTHFIRST</li> <li>NODESFIRST</li> <li>EDGESFIRST</li> </ul>
     */
    private int                outputorder   = Graph.BREADTHFIRST;
    /**
     * How node overlaps are removed. May be one of the following: <ul> <li>RETAIN</li> <li>SCALE</li> <li>VORONOI</li> <li>SCALEXY</li> <li>ORTHOXY</li> <li>ORTHOYX</li> <li>COMPRESS</li> </ul>
     */
    private int                overlap       = Graph.RETAIN;
    /**
     * Used to activate or deactivate packing. -1: false >0: true with the value as a margin (0: true, with the margin at 8...)
     */
    private boolean            pack          = false;
    /**
     * Packing method to use. May be one of the following: <ul> <li>NODE</li> <li>CLUST</li> <li>GRAPH</li> </ul>
     */
    private int                packmode      = Graph.NODE;
    /**
     * Value of pack margin when pack is true
     */
    private int                packValue     = 8;
    /**
     * Width and height of output pages (in inches)
     */
    private Point2D.Double     page;
    /**
     * Specifies the order in which the pages are emitted. May be one of the following: <ul> <li>BL</li> <li>BR</li> <li>TL</li> <li>TR</li> <li>RB</li> <li>RT</li> <li>LB</li> <li>LT</li> </ul>
     */
    private int                pagedir       = Graph.BL;
    /**
     * Round label dimensions to integral multiples of the quantum
     */
    private double             quantum       = 0;
    /**
     * Direction of graph layout. One of the following: <ul> <li>TB</li> <li>LR</li> <li>BT</li> <li>RL</li> </ul>
     */
    private int                rankdir       = Graph.TB;
    /**
     * Gives desired rank separation (in inches)
     */
    private double             ranksep;
    /**
     * Desired aspect ratio. May also be one of the following: <ul> <li>NO_RATIO</li> <li>FILL</li> <li>COMPRESS_RATIO</li> <li>EXPAND</li> <li>AUTO</li> </ul>
     */
    private double             ratio         = Graph.NO_RATIO;
    /**
     * Determine if the generic attribute is a classic node or a record
     */
    private boolean            record        = false;
    /**
     * Run cross minimization on multiple clusters
     */
    private boolean            remincross    = false;
    /**
     * Set graph orientation
     */
    private int                rotate        = 0;
    /**
     * Number of points used to represent circles and ellipses
     */
    private int                samplepoints  = 8;
    /**
     * Maximum number of negative cut edges to search for minimum cut value
     */
    private int                searchsize    = 30;
    /**
     * Fraction to increase polygons in order to determine overlapping
     */
    private double             sep           = 0.01;
    /**
     * Show PostScript guide boxes for debugging
     */
    private int                showboxes     = 0;
    /**
     * Maximum width and height of drawing (in inches)
     */
    private Point2D.Double     size;
    /**
     * Method of drawing edges. May be one of the following: <ul> <li>NO_SPLINES</li> <li>SPLINES</li> <li>COMPOUND</li> </ul>
     */
    //int splines = Graph.NO_SPLINES;
    /**
     * Specifies a seed for the random number generator (NO_SEED is for unspecified seed)
     */
    //int startSeed = Graph.NO_SEED;
    /**
     * Control node placement at start. May be one of the following: <ul> <li>REGULAR</li> <li>SELF</li> <li>RANDOM</li> </ul>
     */
    //int startStyle = Graph.RANDOM;
    /**
     * XML stylesheet for SVG output
     */
    private String             stylesheet;
    /**
     * Target of the URL
     */
    //String target;
    /**
     * Use a truecolor color model for bitmap rendering
     */
    //Boolean truecolor = null;
    /**
     * Hyperlink associated to a graph.
     */
    //String URL;
    /**
     * Clipping window on final drawing
     */
    //ViewPort viewPort;
    /**
     * Factor to scale up drawing to allow margin for expansion in Voronoi technique
     */
    private double             voro_margin   = 0.05;

    /**
     * Add a node to the nodes list
     * 
     * @param node
     *            New node to add
     */
    public void addNode(Node node) {
        Graph graph = getRootGraph();
        if (graph.genericNodes == null) {
            this.setNodes(new Node[1]);
            this.getNodes()[0] = node;
            graph.genericNodes = this.getNodes();
        } else {
            boolean addNode = !(node instanceof SubRecord);
            for (int i = 0; i < graph.genericNodes.length; i++) {
                if (graph.genericNodes[i].getId() != null)
                    addNode &= (!graph.genericNodes[i].getId().equals(node.getId()));
                if (!addNode)
                    break;
            }
            if (addNode) {
                if (this.getNodes() == null) {
                    this.setNodes(new Node[1]);
                    this.getNodes()[0] = node;
                } else {
                    Node[] tmp = new Node[this.getNodes().length + 1];
                    System.arraycopy(this.getNodes(), 0, tmp, 0, this.getNodes().length);
                    tmp[tmp.length - 1] = node;
                    this.setNodes(tmp);
                }
                graph.addGenericNode(node);
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
        if (this.getEdges() == null) {
            this.setEdges(new Edge[1]);
            this.getEdges()[0] = edge;
        } else {
            boolean addEdge = true;
            for (int i = 0; i < this.getEdges().length; i++) {
                addEdge &= (this.getEdges()[i].getStart() != edge.getStart() && this.getEdges()[i].getEnd() != edge.getEnd());
                if (!addEdge)
                    break;
            }
            if (addEdge) {
                Edge[] tmp = new Edge[this.getEdges().length + 1];
                System.arraycopy(this.getEdges(), 0, tmp, 0, this.getEdges().length);
                tmp[tmp.length - 1] = edge;
                this.setEdges(tmp);
            }
        }
        Graph.addNode(edge.getStart().getRoot(), edge.getStart());
        Graph.addNode(edge.getEnd().getRoot(), edge.getEnd());
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
        for (int i = 0; i < in.length; i++) {
            removeEdge(in[i]);
        }
        for (int i = 0; i < out.length; i++) {
            removeEdge(out[i]);
        }
        if (this.getNodes() != null) {
            for (int i = 0; i < this.getNodes().length; i++) {
                if (this.getNodes()[i] == node) {
                    Node[] tmp = new Node[this.getNodes().length - 1];
                    System.arraycopy(this.getNodes(), 0, tmp, 0, i);
                    System.arraycopy(this.getNodes(), i + 1, tmp, i,
                            this.getNodes().length - i - 1);
                    this.setNodes(tmp);
                    break;
                }
            }
            if (this.getNodes().length == 0) {
                this.setNodes(null);
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
        if (this.getEdges() != null) {
            for (int i = 0; i < this.getEdges().length; i++) {
                if (this.getEdges()[i] == edge) {
                    Edge[] tmp = new Edge[this.getEdges().length - 1];
                    System.arraycopy(this.getEdges(), 0, tmp, 0, i);
                    System.arraycopy(this.getEdges(), i + 1, tmp, i,
                            this.getEdges().length - i - 1);
                    this.setEdges(tmp);
                    break;
                }
            }
            if (this.getEdges().length == 0) {
                this.setEdges(null);
            }
        }
    }

    /**
     * @see Node#toString()
     */
    public String toString() {
        // TODO: print all SubGraph and Cluster options
        String g;
        if (this.getId() == null) {
            g = "{\n";
        } else {
            g = "subgraph " + ((this instanceof Cluster) ? "cluster" : "")
                    + this.getId() + " {\n";
        }
        if (this.getNodes() != null)
            for (int i = 0; i < this.getNodes().length; i++) {
                g += this.getNodes()[i];
            }
        if (this.getEdges() != null) {
            for (int i = 0; i < this.getEdges().length; i++) {
                g += this.getEdges()[i];
            }
        }
        return g + "}\n";
    }

    public void changeOption(String name, String value) throws Exception {
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
            if (name.equals("bb"))
                this.setBb(DotUtils.readRectangle(v));
            else if (name.equals("bgcolor"))
                this.setBgcolor(DotUtils.readColor(v));
            /*else if (name.equals("center"))
             this.center = DotUtils.readBoolean(v);
             else if (name.equals("charset"))
             this.charset = readAttributeNumber(v);
             else if (name.equals("clusterrank"))
             this.clusterrank = readAttributeNumber(v);
             else if (name.equals("compound"))
             this.compound = DotUtils.readBoolean(v);*/
            else if (name.equals("concentrate"))
                this.setConcentrate(DotUtils.readBoolean(v));
            /*else if (name.equals("damping"))
             this.damping = DotUtils.readDouble(v);*/
            else if (name.equals("defaultdist"))
                this.setDefaultdist(DotUtils.readDouble(v));
            else if (name.equals("dim"))
                this.setDim(DotUtils.readInteger(v));
            /*else if (name.equals("dpi")
             || name.equals("resolution")) // resolution
             this.dpi = DotUtils.readDouble(v);
             else if (name.equals("epsilon"))
             this.epsilon = DotUtils.readDouble(v);*/
            else if (name.equals("fontcolor"))
                this.setFontcolor(DotUtils.readColor(v));
            else if (name.equals("fontname"))
                this.setFontname(v);
            /*else if (name.equals("fontpath"))
             addFontPath(v);*/
            else if (name.equals("fontsize"))
                this.setFontsize(DotUtils.readDouble(v));
            /*else if (name.equals("k"))
             this.k = DotUtils.readDouble(v);*/
            else if (name.equals("label"))
                this.setLabel(v);
            else if (name.equals("labeljust"))
                this.setLabeljust(readAttributeNumber(v));
            else if (name.equals("labelloc"))
                this.setLabelloc(readAttributeNumber(v));
            /*else if (name.equals("layers"))
             addLayer(v);
             else if (name.equals("layersep"))
             this.layersep = v;*/
            else if (name.equals("lp"))
                this.setLp(DotUtils.readPoint(v));
            /*else if (name.equals("margin"))
             this.margin = DotUtils.readPointf(v);*/
            /*else if (name.equals("maxiter"))
             this.maxiter = DotUtils.readInteger(v);
             else if (name.equals("mclimit"))
             this.mclimit = DotUtils.readDouble(v);
             else if (name.equals("mindist"))
             this.mindist = DotUtils.readDouble(v);
             else if (name.equals("mode"))
             this.mode = readAttributeNumber(v);
             else if (name.equals("model"))
             this.model = readAttributeNumber(v);*/
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
                } catch (NumberFormatException ex) {
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
                //System.err.println("ratio read");
                this.setRatio(readRatio(v));
            //this.ratio = readAttributeNumber(v);
            else if (name.equals("remincross"))
                this.setRemincross(DotUtils.readBoolean(v));
            else if (name.equals("rotate"))
                this.setRotate(DotUtils.readInteger(v));
            else if (name.equals("orientation"))
                if (this.getRotate() == 0)
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
                /*else if (name.equals("splines"))
                 this.splines = readAttributeNumber(v);*/
                else if (name.equals("start")) {
                    //this.k = DotUtils.readDouble(v);
                } else if (name.equals("stylesheet"))
                    this.setStylesheet(v);
                else if (name.equals("target"))
                    this.setTarget(v);
                /*else if (name.equals("truecolor"))
                 this.truecolor = new Boolean(DotUtils.readBoolean(v));*/ // FIXME: test at end
                else if (name.equals("URL") || name.equals("href"))
                    this.setURL(v);
                /*else if (name.equals("viewport"))
                 this.viewPort = DotUtils.readViewPort(v);*/
                else if (name.equals("voro_margin"))
                    this.setVoro_margin(DotUtils.readDouble(v));
                else if (name.equals("rank"))
                    this.setRank(readAttributeNumber(v));
                else
                    System.err.println("SubGraph attribute \"" + name
                            + "\" does not exist");
        }
    }

    private double readRatio(String v) {
        for (int i = 0; i < ratioAttributeNames.length; i++) {
            if (ratioAttributeNames[i].equalsIgnoreCase(v))
                return -i;
        }
        return -1;
    }

    private int readAttributeNumber(String v) {
        int value;
        boolean found = false;
        for (value = 0; value < SubGraph.attributeNames.length; value++) {
            found = v.equals(SubGraph.attributeNames[value]);
            if (found)
                break;
        }
        if (found)
            return value;
        return -1;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    public Edge[] getEdges() {
        return edges;
    }

    public void setEdges(Edge[] edges) {
        this.edges = edges;
    }

    public Rectangle getBb() {
        return bb;
    }

    public void setBb(Rectangle bb) {
        this.bb = bb;
    }

    public Color getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(Color bgcolor) {
        this.bgcolor = bgcolor;
    }

    public boolean isConcentrate() {
        return concentrate;
    }

    public void setConcentrate(boolean concentrate) {
        this.concentrate = concentrate;
    }

    public double getDefaultdist() {
        return defaultdist;
    }

    public void setDefaultdist(double defaultdist) {
        this.defaultdist = defaultdist;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public Edge getGenericEdge() {
        return genericEdge;
    }

    public void setGenericEdge(Edge genericEdge) {
        this.genericEdge = genericEdge;
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

    public Graph getGenericGraph() {
        return genericGraph;
    }

    public void setGenericGraph(Graph genericGraph) {
        this.genericGraph = genericGraph;
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

    public Point getLp() {
        return lp;
    }

    public void setLp(Point lp) {
        this.lp = lp;
    }

    public double getNodesep() {
        return nodesep;
    }

    public void setNodesep(double nodesep) {
        this.nodesep = nodesep;
    }

    public boolean isNormalize() {
        return normalize;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    public int getOutputorder() {
        return outputorder;
    }

    public void setOutputorder(int outputorder) {
        this.outputorder = outputorder;
    }

    public int getOverlap() {
        return overlap;
    }

    public void setOverlap(int overlap) {
        this.overlap = overlap;
    }

    public boolean isPack() {
        return pack;
    }

    public void setPack(boolean pack) {
        this.pack = pack;
    }

    public int getPackmode() {
        return packmode;
    }

    public void setPackmode(int packmode) {
        this.packmode = packmode;
    }

    public int getPackValue() {
        return packValue;
    }

    public void setPackValue(int packValue) {
        this.packValue = packValue;
    }

    public Point2D.Double getPage() {
        return page;
    }

    public void setPage(Point2D.Double page) {
        this.page = page;
    }

    public int getPagedir() {
        return pagedir;
    }

    public void setPagedir(int pagedir) {
        this.pagedir = pagedir;
    }

    public double getQuantum() {
        return quantum;
    }

    public void setQuantum(double quantum) {
        this.quantum = quantum;
    }

    public int getRankdir() {
        return rankdir;
    }

    public void setRankdir(int rankdir) {
        this.rankdir = rankdir;
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

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public boolean isRemincross() {
        return remincross;
    }

    public void setRemincross(boolean remincross) {
        this.remincross = remincross;
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

    public double getSep() {
        return sep;
    }

    public void setSep(double sep) {
        this.sep = sep;
    }

    public int getShowboxes() {
        return showboxes;
    }

    public void setShowboxes(int showboxes) {
        this.showboxes = showboxes;
    }

    public Point2D.Double getSize() {
        return size;
    }

    public void setSize(Point2D.Double size) {
        this.size = size;
    }

    public String getStylesheet() {
        return stylesheet;
    }

    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    public double getVoro_margin() {
        return voro_margin;
    }

    public void setVoro_margin(double voro_margin) {
        this.voro_margin = voro_margin;
    }
}
