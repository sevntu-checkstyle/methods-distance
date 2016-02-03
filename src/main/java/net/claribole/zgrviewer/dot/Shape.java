/*   FILE: Shape.java
 *   DATE OF CREATION:   Apr 4 2005
 *   AUTHOR :            Eric Mounhem (skbo@lri.fr)
 *   Copyright (c) INRIA, 2004-2005. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 * 
 * $Id: Shape.java 576 2007-03-29 18:32:53Z epietrig $
 */

package net.claribole.zgrviewer.dot;

/**
 * Defines a single arrow shape, with its side (BOTH, LEFT, RIGHT) and open
 * modifiers
 * @author Eric Mounhem
 */
class Shape {
    /* Shapes available */
    /**
     * No arrow at all
     */
    static final int      NONE           = 0;
    /**
     * Normal pointing triangle arrow shape
     */
    static final int      NORMAL         = 1;
    /**
     * Inverted triangle arrow shape
     */
    static final int      INV            = 2;
    /**
     * Box arrow shape
     */
    static final int      BOX            = 3;
    /**
     * Crow arrow shape (inverted Vee)
     */
    static final int      CROW           = 4;
    /**
     * Diamond arrow shape
     */
    static final int      DIAMOND        = 5;
    /**
     * Dot arrow shape
     */
    static final int      DOT            = 6;
    /**
     * Tee arrow shape
     */
    static final int      TEE            = 7;
    /**
     * Vee arrow shape
     */
    static final int      VEE            = 8;

    /* Side modifiers */
    /**
     * Show both sides of the shape
     */
    static final int      BOTH           = 9;
    /**
     * Show only left side of the shape
     */
    static final int      LEFT           = 10;
    /**
     * Show only right side of the shape
     */
    static final int      RIGHT          = 11;

    /**
     * Shape's output attribute names
     */
    final static String[] attributeNames = { "none", "normal", "inv", "box",
            "crow", "diamond", "dot", "tee", "vee", "", "l", "r" };

    private int                   shape;
    private int                   side;
    private boolean               fill;

    /**
     * Create a basic arrow based solely on a shape.
     * 
     * @param shape
     *            Shape to use
     */
    public Shape(int shape) {
        this.setShape(shape);
        this.setSide(BOTH);
        this.setFill(true);
    }

    private String printNamedOption(int i) {
        return Shape.attributeNames[i];
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        String o = "";
        if (!this.isFill())
            o += "o";
        o += printNamedOption(this.getSide());
        o += printNamedOption(this.getShape());
        return o;
    }

    /**
     * One of the following GraphViz' shape:
     * <ul>
     * <li>NONE</li>
     * <li>NORMAL</li>
     * <li>INV</li>
     * <li>BOX</li>
     * <li>CROW</li>
     * <li>DIAMOND</li>
     * <li>DOT</li>
     * <li>TEE</li>
     * <li>VEE</li>
     * </ul>
     */
    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    /**
     * Side of the shape to draw. May be BOTH, LEFT or
     * RIGHT. BOTH by default.
     */
    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    /**
     * Use a filled shape
     */
    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }
}
