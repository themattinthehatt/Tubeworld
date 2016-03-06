package colorcascade;

import processing.core.PApplet;
import processing.core.PVector;

public class EdgeRect extends CCEdge {

    // properties
    //    public PApplet parent;          // parent PApplet (Tubeworld) for rendering
    //    public int[] color;             // color of vertex in rgb
    //    public int[] parent_indxs;      // indices of parent vertices; manipulated by ColorCascade class; 0 parent, 1 child

    // box properties
    PVector edge_dims;               // dimensions of edge; standard reference frame is long axis along y-axis
    PVector edge_center;            // center of edge; updated on every iteration
    PVector edge_rotations;         // values in radians of edge rotations

    // temp variables for calculating rotations
    PVector temp1;
    PVector temp2;

    public EdgeRect(PApplet parent_, int parent_indx, int child_indx) {

        // pass arguments to parent constructor (CCEdge constructor); sets CCEdge properties (also sets color)
        super(parent_, parent_indx, child_indx);

        // set initial edge values
        edge_dims = new PVector(15, 5, 5);
        edge_center = new PVector(0, 0, 0);
        edge_rotations = new PVector(0, 0, 0);

        // initialize temp variables
        temp1 = new PVector(0, 0);
        temp2 = new PVector(0, 0);

    }

    public void setColor(int[] parent_color, int[] child_color) {
        // interpolate between two parent colors
        color[0] = ((parent_color[0] + child_color[0]) / 2 ) % 255;
        color[1] = ((parent_color[1] + child_color[1]) / 2 ) % 255;
        color[2] = ((parent_color[2] + child_color[2]) / 2 ) % 255;
    }

    public void drawEdge(PVector parent_center, PVector child_center, int color_channel_id) {

        // find center of edge
        edge_center = PVector.add(parent_center, child_center);
        edge_center.div(2);

        // find needed rotations
        // x-rotation - project out x component
        temp1.set(parent_center.y, parent_center.z);
        temp2.set(child_center.y, child_center.z);
        temp1.sub(temp2);
        edge_rotations.x = temp1.heading();

        // y-rotation - project out y component
        temp1.set(parent_center.x, parent_center.z);
        temp2.set(child_center.x, child_center.z);
        temp1.sub(temp2);
        edge_rotations.y = temp1.heading();

        // z-rotation - project out z component
        temp1.set(parent_center.x, parent_center.y);
        temp2.set(child_center.x, child_center.y);
        temp1.sub(temp2);
        edge_rotations.z = temp1.heading();

        // update edge length
        edge_dims.x = PVector.dist(parent_center, child_center);

        // draw edge
        parent.pushMatrix();
        parent.translate(edge_center.x, edge_center.y, edge_center.z);

        // set color depending on color_channel_id
        if (color_channel_id == 0) {
            // no channels
            parent.stroke(0,0,0);
            parent.fill(0,0,0);
        } else if (color_channel_id == 1) {
            // red channel
            parent.stroke(color[0],0,0);
            parent.fill(color[0],0,0);
        } else if (color_channel_id == 2) {
            // green channel
            parent.stroke(0, color[1], 0);
            parent.fill(0, color[1], 0);
        } else if (color_channel_id == 3) {
            // red and green
            parent.stroke(color[0], color[1], 0);
            parent.fill(color[0], color[1], 0);
        } else if (color_channel_id == 4) {
            // blue channel
            parent.stroke(0, 0, color[2]);
            parent.fill(0, 0, color[2]);
        } else if (color_channel_id == 5) {
            // blue and red channels
            parent.stroke(color[0], 0, color[2]);
            parent.fill(color[0], 0, color[2]);
        } else if (color_channel_id == 6) {
            // blue and green channels
            parent.stroke(0, color[1], color[2]);
            parent.fill(0, color[1], color[2]);
        } else if (color_channel_id == 7) {
            // all channels
            parent.stroke(color[0],color[1],color[2]);
            parent.fill(color[0],color[1],color[2]);
        }

        parent.rotateX(edge_rotations.x);
        parent.rotateY(edge_rotations.y);
        parent.rotateZ(edge_rotations.z);
        parent.box(edge_dims.x, edge_dims.y, edge_dims.z);
        parent.popMatrix();

    }

}
