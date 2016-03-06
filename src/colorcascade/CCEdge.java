/* possible types:
line (easiest)
cylinder
box
 */

package colorcascade;

import processing.core.PApplet;
import processing.core.PVector;

public abstract class CCEdge {

    // properties
    public PApplet parent;          // parent PApplet (Tubeworld) for rendering
    public int[] color;             // color of vertex in rgb
    public int[] parent_indxs;      // indices of parent vertices; manipulated by ColorCascade class; 0 parent, 1 child

    public CCEdge(PApplet parent_, int parent_indx, int child_indx) {

        // copy properties from input
        parent = parent_;
        parent_indxs = new int[2];
        parent_indxs[0] = parent_indx;
        parent_indxs[1] = child_indx;

        // set initial color to almost black
        color = new int[3];
        color[0] = 20; color[1] = 20; color[2] = 20;

    }

    public abstract void setColor(int[] parent_color, int[] child_color);
    // interpolate between two parent colors

    public abstract void drawEdge(PVector parent_center, PVector child_center, int color_channel_id);

}