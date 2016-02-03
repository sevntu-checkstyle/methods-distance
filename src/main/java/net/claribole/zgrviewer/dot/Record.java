/*   FILE: Record.java
 *   DATE OF CREATION:   Apr 4 2005
 *   AUTHOR :            Eric Mounhem (skbo@lri.fr)
 *   Copyright (c) INRIA, 2004-2005. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 * 
 * $Id: Record.java 576 2007-03-29 18:32:53Z epietrig $
 */

package net.claribole.zgrviewer.dot;

import java.util.Vector;

// TODO: defines HTML labels

/**
 * Create a record, which is a special node, with some properties
 * @author Eric Mounhem
 */
class Record extends CommonNode {

    private Record                rootRecord;

    private Rectangle[]           rects;

    /**
     * List of the Record's elemnts that can be used on edges' start and ending
     * nodes
     */
    protected SubRecord[] subRecords;

    /**
     * Create a new Record object
     * 
     * @param root
     *            root of the graph
     * @param id
     *            identificator of the Record
     * @throws Exception
     */
    public Record(Object root, String id) throws Exception {
        super(root, id);
        if (!(this instanceof SubRecord)) {
            this.setLabel(id);
            this.setRootRecord(this);
        }

        this.getGenericAttributes();
    }

    private void getAttributes(Record record) {
        // Record attributes
        if (this.getRects() != null && record.getRects() != null)
            if (this.getRects().equals(record.getRects()))
                this.setRects(record.getRects());
        if (this.isRounded() != record.isRounded())
            this.setRounded(record.isRounded());

        // CommonNode attributes
        if (this.isFixedsize() != record.isFixedsize())
            this.setFixedsize(record.isFixedsize());
        if (!this.getGroup().equals(record.getGroup()))
            this.setGroup(record.getGroup());
        if (this.getHeight() != record.getHeight())
            this.setHeight(record.getHeight());
        if (this.getLayer() != null && record.getLayer() != null)
            if (!this.getLayer().equals(record.getLayer())) // FIXME: handle arrays comparisons
                this.setLayer(record.getLayer());
        if (!this.getMargin().equals(record.getMargin()))
            this.setMargin(record.getMargin());
        if (this.isPin() != record.isPin())
            this.setPin(record.isPin());
        if (this.getPos() != null && record.getPos() != null)
            if (!this.getPos().equals(record.getPos())) // FIXME: handle splines comparisons
                this.setPos(record.getPos());
        if (this.getRotate() != record.getRotate())
            this.setRotate(record.getRotate());
        if (this.getShowboxes() != record.getShowboxes())
            this.setShowboxes(record.getShowboxes());
        if (!this.getTooltip().equals(record.getTooltip()))
            this.setTooltip(record.getTooltip());
        if (this.getVertices() != null && record.getVertices() != null)
            if (!this.getVertices().equals(record.getVertices())) // FIXME: handle arrays comparisons
                this.setVertices(record.getVertices());
        if (this.getWidth() != record.getWidth())
            this.setWidth(record.getWidth());
        if (this.getZ() != record.getZ())
            this.setZ(record.getZ());

        // Node attributes
        if (this.getColor() == null)
            this.setColor(record.getColor());
        else if (!this.getColor().equals(record.getColor()))
            this.setColor(record.getColor());
        if (this.getFillcolor() == null)
            this.setFillcolor(record.getFillcolor());
        else if (!this.getFillcolor().equals(record.getFillcolor()))
            this.setFillcolor(record.getFillcolor());
        if (this.getFontcolor() == null)
            this.setFontcolor(record.getFontcolor());
        else if (!this.getFontcolor().equals(record.getFontcolor()))
            this.setFontcolor(record.getFontcolor());
        if (!this.getFontname().equals(record.getFontname()))
            this.setFontname(record.getFontname());
        if (this.getFontsize() != record.getFontsize())
            this.setFontsize(record.getFontsize());
        if (this.getLabel() != null && record.getLabel() != null)
            if (!this.getLabel().equals(record.getLabel())
                    && !record.getLabel().equals("node"))
                this.setLabel(record.getLabel());
        if (this.isNojustify() != record.isNojustify())
            this.setNojustify(record.isNojustify());
        if (this.getPeripheries() != record.getPeripheries())
            this.setPeripheries(record.getPeripheries());
        if (!this.getStyle().equals(record.getStyle()))
            this.setStyle(record.getStyle());
        if (this.getTarget() != null && record.getTarget() != null)
            if (!this.getTarget().equals(record.getTarget()))
                this.setTarget(record.getTarget());
        if (this.getURL() != null && record.getURL() != null)
            if (!this.getURL().equals(record.getURL()))
                this.setURL(record.getURL());
    }

    private void getGenericAttributes() {
        Object rootGraph = this.getRoot();
        Vector roots = new Vector();

        while (rootGraph instanceof SubGraph) {
            roots.add(rootGraph);
            rootGraph = ((SubGraph) rootGraph).getRoot();
        }

        if (((Graph) rootGraph).getGenericRecord() != null)
            getAttributes(((Graph) rootGraph).getGenericRecord());

        for (int i = roots.size() - 1; i == 0; i--) {
            Record generic = ((SubGraph) roots.get(i)).getGenericRecord();
            if (generic != null)
                getAttributes(generic);
        }
    }

    /**
     * Add a new SubRecord to the subRecord's list
     * 
     * @param subRecord
     *            SubRecord to add
     */
    public void addSubRecord(SubRecord subRecord) {
        if (this.subRecords == null) {
            this.subRecords = new SubRecord[1];
            this.subRecords[0] = subRecord;
        } else {
            SubRecord[] tmp = new SubRecord[this.subRecords.length + 1];
            System
                    .arraycopy(this.subRecords, 0, tmp, 0,
                            this.subRecords.length);
            tmp[tmp.length - 1] = subRecord;
            this.subRecords = tmp;
        }
    }

    /**
     * Add a new Rectangle to the rects' list
     * 
     * @param rect
     *            Rectangle to add
     */
    public void addRectangle(Rectangle rect) {
        if (this.getRects() == null) {
            this.setRects(new Rectangle[1]);
            this.getRects()[0] = rect;
        } else {
            Rectangle[] tmp = new Rectangle[this.getRects().length + 1];
            System.arraycopy(this.getRects(), 0, tmp, 0, this.getRects().length);
            tmp[tmp.length - 1] = rect;
            this.setRects(tmp);
        }
    }

    /**
     * Remove a SubRecord from the subRecord's list
     * 
     * @param subRecord
     *            SubRecord to remove
     */
    public void removeSubRecord(SubRecord subRecord) {
        if (this.subRecords != null) {
            for (int i = 0; i < this.subRecords.length; i++) {
                if (this.subRecords[i] == subRecord) {
                    SubRecord[] tmp = new SubRecord[this.subRecords.length - 1];
                    System.arraycopy(this.subRecords, 0, tmp, 0, i);
                    System.arraycopy(this.subRecords, i + 1, tmp, i,
                            this.subRecords.length - i - 1);
                    this.subRecords = tmp;
                    break;
                }
            }
            if (this.subRecords.length == 0) {
                this.subRecords = null;
            }
        }
    }

    /**
     * Remove a Rectangle from the rects' list
     * 
     * @param rect
     *            Rectangle to remove
     */
    public void removeRectangle(Rectangle rect) {
        if (this.getRects() != null) {
            for (int i = 0; i < this.getRects().length; i++) {
                if (this.getRects()[i] == rect) {
                    Rectangle[] tmp = new Rectangle[this.getRects().length - 1];
                    System.arraycopy(this.getRects(), 0, tmp, 0, i);
                    System.arraycopy(this.getRects(), i + 1, tmp, i,
                            this.getRects().length - i - 1);
                    this.setRects(tmp);
                    break;
                }
            }
            if (this.getRects().length == 0) {
                this.setRects(null);
            }
        }
    }

    /**
     * @return Returns the rounded attribute.
     */
    public boolean isRounded() {
        return this.getStyle().getStyle(Style.ROUNDED);
    }

    /**
     * @param rounded
     *            The rounded value to set.
     */
    public void setRounded(boolean rounded) {
        this.getStyle().setStyle(Style.ROUNDED, rounded);
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

            //System.err.println(name);
            if (name.equals("rects")) {
                this.setRects(DotUtils.readRectangleList(v));
            } else if (name.equals("shape")) {
                /*this.shape =*/DotUtils.readShape(this, v);
            } else
                System.err.println("Record attribute \"" + name
                        + "\" does not exist");
        }
    }

    /**
     * @see Node#toString()
     */
    public String toString() {
        return this.getId() + " [" + nodeOptions() + "];\n";
    }

    /**
     * @see net.claribole.zgrviewer.dot.CommonNode#nodeOptions()
     */
    protected String nodeOptions() {
        String o = super.nodeOptions();
        o += printShapeOption();
        String recordLabel = printRecordLabel();
        if (!recordLabel.equals(""))
            o += "label=\"" + recordLabel + "\" ";
        if (this.getRects() != null) {
            o += printOption("rects", this.getRects());
        }
        return o;
    }

    private String printOption(String attribute, Rectangle[] value) {
        String s = "";
        for (int i = 0; i < value.length; i++) {
            if (i > 0)
                s += " ";
            s += /*printRectangle(*/value[i]/*)*/;
        }
        return attribute + "=\"" + s + "\" ";
    }

    /**
     * Write the shape of a record
     * @return Correct record' shape option line
     */
    protected String printShapeOption() {
        return "shape=\"" + (isRounded() ? "M" : "") + "record\" ";
    }

    /**
     * Write a record's label
     * @return A record's label with the GraphViz syntax
     */
    protected String printRecordLabel() {
        String l = "";
        if (this.subRecords != null)
            for (int i = 0; i < this.subRecords.length; i++) {
                if (i > 0)
                    l += " | ";
                if (!this.subRecords[i].getId().equals(""))
                    l += "<" + this.subRecords[i].getId() + ">";
                if (this.subRecords[i].getLabel() != null)
                    l += " " + this.subRecords[i].getLabel().trim();
                if (this.subRecords[i].subRecords != null)
                    l += "{ " + this.subRecords[i].printRecordLabel() + " }";
            }
        return l;
    }

    /**
     * Top Record containing all SubRecords
     */
    public Record getRootRecord() {
        return rootRecord;
    }

    public void setRootRecord(Record rootRecord) {
        this.rootRecord = rootRecord;
    }

    /**
     * Rectangles for fields of records (in points)
     */
    public Rectangle[] getRects() {
        return rects;
    }

    public void setRects(Rectangle[] rects) {
        this.rects = rects;
    }
}
