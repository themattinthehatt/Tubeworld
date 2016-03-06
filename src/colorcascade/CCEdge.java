/* possible types:
line (easiest)
cylinder
rectanguloid
 */

package colorcascade;

import processing.core.PApplet;

public abstract class CCEdge {

    // properties
    public PApplet parent;          // parent PApplet (Tubeworld) for rendering
    public int[] color;             // color of vertex in rgb
    public int[] parent_indxs;      // indices of parent vertices; manipulated by ColorCascade class; 0 parent, 1 child

    public CCEdge(PApplet parent_, int parent_indx, int child_indx) {

        // copy properties from input
        parent = parent_;
        parent_indxs[0] = parent_indx;
        parent_indxs[1] = child_indx;

        // set initial color to almost black
        color[0] = 20; color[1] = 20; color[2] = 20;

    }

    public abstract void setColor(int[] color1, int[] color2);
    // interpolate between two parent colors

    public abstract void drawEdge(float[] point1, float[] point2);

}