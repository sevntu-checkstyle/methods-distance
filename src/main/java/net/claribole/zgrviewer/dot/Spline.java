/*   FILE: Spline.java
 *   DATE OF CREATION:   Apr 4 2005
 *   AUTHOR :            Eric Mounhem (skbo@lri.fr)
 *   Copyright (c) INRIA, 2004-2005. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file COPYING.
 * 
 * $Id: Spline.java 576 2007-03-29 18:32:53Z epietrig $
 */

package net.claribole.zgrviewer.dot;


/**
 * Control point positions of a spline
 * @author Eric Mounhem
 */
public class Spline {
    private Point startingPoint = null;
    private Point endingPoint   = null;
    private Point[] controls;

    public void addControls(Point point) {
        if (this.controls == null) {
            this.controls = new Point[1];
            this.controls[0] = point;
        } else {
            Point[] tmp = new Point[this.controls.length + 1];
            System.arraycopy(this.controls, 0, tmp, 0, this.controls.length);
            tmp[tmp.length - 1] = point;
            this.controls = tmp;
        }
    }

    /**
     * Getter for the controls attribute
     * 
     * @return the list of control points
     */
    public Point[] getControls() {
        return this.controls;
    }

    /**
     * Create a new Spline object. You must add other points with
     * addControls(Point p1, Point p2, Point p3).
     */
    public Spline() {
        // Nothing to do
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        String s = "";
        s += printPoint("e", this.getEndingPoint());
        s += printPoint("s", this.getStartingPoint());
        s += printControls();
        return s;
    }

    private String printPoint(String control, Point/*2D.Double*/ point) {
        String o="";
        if (point != null) {
            for (int i = 0; i < point.getCoords().length; i++) {
                if (i > 0 && i < point.getCoords().length)
                    o += ",";
                o += point.getCoords()[i];
                if (i == point.getCoords().length - 1)
                    o += (point.isChange() ? "" : "!");
            }
            return control+","+o+" ";
        }
        return "";
    }

    private String printControls() {
        String o = "";
        for (int i = 0; i < this.controls.length; i++) {
            if (i > 0 && i < this.controls.length)
                o += " ";
            Point p = this.controls[i];
            for (int j = 0; j < p.getCoords().length; j++) {
                if (j > 0 && j < p.getCoords().length)
                    o += ",";
                o += p.getCoords()[j];
                if (j == p.getCoords().length - 1)
                    o += (p.isChange() ? "" : "!");
            }
        }
        return o;
    }

    /**
     * Start of the Spline
     */
    public Point getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(Point startingPoint) {
        this.startingPoint = startingPoint;
    }

    /**
     * End of the Spline
     */
    public Point getEndingPoint() {
        return endingPoint;
    }

    public void setEndingPoint(Point endingPoint) {
        this.endingPoint = endingPoint;
    }
}
