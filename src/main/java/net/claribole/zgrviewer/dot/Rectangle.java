/*   FILE: Rectangle.java
 *   DATE OF CREATION:   Apr 4 2005
 *   AUTHOR :            Eric Mounhem (skbo@lri.fr)
 *   Copyright (c) INRIA, 2004-2005. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 * 
 * $Id: Rectangle.java 576 2007-03-29 18:32:53Z epietrig $
 */

package net.claribole.zgrviewer.dot;

/**
 * Defines a rectangle with its lower left and upper right points
 * @author Eric Mounhem
 */
public class Rectangle {
    private long x1;
    private long x2;
    private long y1;
    private long y2;

    /**
     * Create a new rectangle from 2 point
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public Rectangle(long x1, long y1, long x2, long y2) {
        this.setX1(x1);
        this.setY1(y1);
        this.setX2(x2);
        this.setY2(y2);
    }

    public boolean equals(Object obj) {
        Rectangle test = (Rectangle) obj;
        return (this.getX1() == test.getX1() && this.getX2() == test.getX2() && this.getY1() == test.getY1() && this.getY2() == test.getY2());
    }

    public String toString() {
        return this.getX1() + "," + this.getY1() + "," + this.getX2() + "," + this.getY2();
    }

    /**
     * Horizontal value of the first point
     */
    public long getX1() {
        return x1;
    }

    public void setX1(long x1) {
        this.x1 = x1;
    }

    /**
     * Horizontal value of the second point
     */
    public long getX2() {
        return x2;
    }

    public void setX2(long x2) {
        this.x2 = x2;
    }

    /**
     * Vertical value of the first point
     */
    public long getY1() {
        return y1;
    }

    public void setY1(long y1) {
        this.y1 = y1;
    }

    /**
     * Vertical value of the second point
     */
    public long getY2() {
        return y2;
    }

    public void setY2(long y2) {
        this.y2 = y2;
    }
}
