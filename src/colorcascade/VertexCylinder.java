package colorcascade;

import processing.core.PApplet;
import processing.core.PVector;

public class VertexCylinder extends CCVertex {

    // properties inherited from CCVertex
    //    public PApplet parent;          // parent PApplet (Tubeworld) for rendering
    //    public int[] color;             // color of vertex in rgb
    //    public int[] parent_indxs;      // indices of parent vertices; manipulated by ColorCascade class
    //    public int[] child_indxs;       // indices of child vertices; manipulated by ColorCascade class
    //    public PVector origin;          // center of cubic region that vertex is allowed to move around in
    //    public float[] loc;             // xyz coordinates of vertex location
    //    public float[] taus;            // diffusion time constants on rgb channels; dictates diffusion from parent vertices

    // cylinder properties
    public float radius;        // radius of cylinder
    public float height;        // height of cylinder
    public int num_sides;       // number of sides in cylinder (resolution)

    public float[] y;           // y-vals of vertices (assuming axis of cylinder is oriented in x-direction
    public float[] z;           // z-vals of vertices (assuming axis of cylinder is oriented in x-direction

    public float noise_offset;  // noise offset for randomly varying vertex color;

    public VertexCylinder(PApplet parent_, int[] parent_indxs_, int[] child_indxs_, PVector origin_, PVector loc_,
                          float[] taus_, float radius_) {

        // pass arguments to parent constructor (Site constructor); sets Site properties
        super(parent_, parent_indxs_, child_indxs_, origin_, loc_, taus_);

        // set size values
        radius = radius_;
        height = 10;
        num_sides = 12;

        // construct y and z values
        float angle;
        y = new float[num_sides+1];
        z = new float[num_sides+1];

        //get the x and y position on a circle for all the sides
        for(int i = 0; i < num_sides+1; i++){
            angle = PApplet.TWO_PI / (num_sides) * i;
            y[i] = PApplet.cos(angle) * radius;
            z[i] = PApplet.sin(angle) * radius;
        }

        noise_offset = 0;
    }

    public void updatePosition() {

    }

    public void updateColor() {
        color[0] = (int) (255*parent.noise(noise_offset,      (origin.y+loc.y)*0.002f, (origin.z+loc.z)*0.002f));
        color[1] = (int) (255*parent.noise(noise_offset+200f, (origin.y+loc.y)*0.002f, (origin.z+loc.z)*0.002f));
        color[2] = (int) (255*parent.noise(noise_offset+50f,  (origin.y+loc.y)*0.002f, (origin.z+loc.z)*0.002f));
        noise_offset += 0.01;
    }


    public void drawVertex(int color_channel_id) {

        // adapted from https://forum.processing.org/one/topic/draw-a-cone-cylinder-in-p3d.html
        parent.pushMatrix();
        parent.translate(origin.x,origin.y,origin.z);   // move to center of vertex region
        parent.translate(loc.x,loc.y,loc.z);            // move to center of vertex

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


        //draw the bottom of the cylinder
        parent.beginShape(PApplet.TRIANGLE_FAN);
        parent.vertex(-height/2,0,0);
        for (int i = 0; i < num_sides+1; i++) {
            parent.vertex(-height/2, y[i], z[i]);
        }
        parent.endShape();

        //draw the center of the cylinder
        parent.beginShape(PApplet.QUAD_STRIP);
        for (int i = 0; i < num_sides+1; i++) {
            parent.vertex(-height/2, y[i], z[i]);
            parent.vertex(height/2, y[i], z[i]);
        }
        parent.endShape();

        //draw the top of the cylinder
        parent.beginShape(PApplet.TRIANGLE_FAN);
        parent.vertex(-height/2,0,0);
        for (int i = 0; i < num_sides+1; i++) {
            parent.vertex(-height/2, y[i], z[i]);
        }
        parent.endShape();

        parent.popMatrix();
    }
}
