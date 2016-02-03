/*   FILE: SubRecord.java
 *   DATE OF CREATION:   Apr 4 2005
 *   AUTHOR :            Eric Mounhem (skbo@lri.fr)
 *   Copyright (c) INRIA, 2004-2005. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 * 
 * $Id: SubRecord.java 576 2007-03-29 18:32:53Z epietrig $
 */

package net.claribole.zgrviewer.dot;

/**
 * Part of a Record, may also be considered as a node as it can point or be
 * pointed with an edge
 * @author Eric Mounhem
 */
public class SubRecord extends Record {
    private Rectangle rect;

    private void init(Record directParentRecord) {
        this.setRootRecord(directParentRecord.getRootRecord());
        Graph.addNode(this.getRoot(), this.getRootRecord());
        Graph.addNode(this.getRoot(), directParentRecord);
        directParentRecord.addSubRecord(this);
    }

    /**
     * Create a SubRecord
     * @param root root of the graph
     * @param id identificator of the subRecord
     * @param directParentRecord direct Record on top of the new one
     * @throws Exception
     */
    public SubRecord(Object root, String id, Record directParentRecord)
            throws Exception {
        super(root, id);
        init(directParentRecord);
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
    }
}
