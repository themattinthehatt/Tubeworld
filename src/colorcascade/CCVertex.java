/* possible types:
squashed cylinder
circle
sphere
cube
 */

package colorcascade;

import processing.core.PApplet;
import processing.core.PVector;

public abstract class CCVertex {

    // properties
    public PApplet parent;          // parent PApplet (Tubeworld) for rendering
    public int[] color;             // color of vertex in rgb
    public int[] parent_indxs;      // indices of parent vertices; manipulated by ColorCascade class
    public int[] child_indxs;       // indices of child vertices; manipulated by ColorCascade class
    public PVector origin;          // center of cubic region that vertex is allowed to move around in
    public PVector loc;             // xyz coordinates of vertex location
    public float[] taus;            // diffusion time constants on rgb channels; dictates diffusion from parent vertices

    public CCVertex(PApplet parent_, int[] parent_indxs_, int[] child_indxs_, PVector origin_, PVector loc_, float[] taus_) {

        // initialize PVectors
        origin = new PVector(0, 0, 0);
        loc = new PVector(0, 0, 0);

        // copy properties from input
        parent = parent_;
        parent_indxs = parent_indxs_;
        child_indxs = child_indxs_;
        origin.set(origin_);
        loc.set(loc_);
        taus = taus_;

        // set initial color to almost black
        color = new int[3]; // values for rgb channels
        color[0] = 255; color[1] = 120; color[2] = 20;

    }

    public abstract void updatePosition();

    public abstract void updateColor();

    public abstract void drawVertex(int color_channel_id);

}