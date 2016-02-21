package recursivetower;

import processing.core.PApplet;

public class Junction {

    public PApplet parent;
    public float x;
    public float y;
    public float z;
    public float x_dim;
    public float y_dim;
    public float z_dim;
    public int tower_orientation;       // orientation of parent tower
    public float[] fill_color;              // int; -1 for noFill
    public float[] stroke_color;            // int; -1 for noStroke
    public String fill_color_inheritance_type;       // specifies how color should be inherited from link that produces it
    public String stroke_color_inheritance_type;     // specifies how color should be inherited from link that produces it
                                        // "inherit" inherits color
                                        // "initial" keeps color junction was initialized with

    public Junction(PApplet parent_, float x_, float y_, float z_, float x_dim_, float y_dim_, float z_dim_,
                                                    float[] fill_color_, float[] stroke_color_, int tower_orientation_ ){
        parent = parent_;
        x = x_;
        y = y_;
        z = z_;
        x_dim = x_dim_;
        y_dim = y_dim_;
        z_dim = z_dim_;
        fill_color = fill_color_;
        stroke_color = stroke_color_;
        tower_orientation = tower_orientation_;
        stroke_color_inheritance_type = "inherit";
        fill_color_inheritance_type = "inherit";
    }

    public void updateFillColor(float[] color) {
        if (fill_color_inheritance_type.equals("inherit")) {
            fill_color = color;
        }
    }

    public void updateStrokeColor(float[] color) {
        if (stroke_color_inheritance_type.equals("inherit")) {
            stroke_color = color;
        }
    }

    public void drawJunction() {
        parent.pushMatrix();
        if (stroke_color[0] != -1) {
            int[] rgb = RecursiveTower.hsvToRgb(stroke_color[0], stroke_color[1], stroke_color[2]);
            parent.stroke(rgb[0], rgb[1], rgb[2]);
        } else {
            parent.noStroke();
        }
        if (fill_color[0] != -1) {
            int[] rgb = RecursiveTower.hsvToRgb(fill_color[0], fill_color[1], fill_color[2]);
            parent.fill(rgb[0], rgb[1], rgb[2]);
        } else {
            parent.noFill();
        }
        switch (tower_orientation) {
            case 0:
                // moving in +x direction
                // +x becomes +y, +y becomes +z, +z becomes +x
                parent.translate(z, x, y);
                parent.box(z_dim, x_dim, y_dim);
                break;
            case 1:
                // moving in -x direction
                // +x becomes +y, +y becomes +z, +z becomes -x
                parent.translate(-z, x, y);
                parent.box(z_dim, x_dim, y_dim);
                break;
            case 2:
                // moving in +y direction
                // x stays the same, +y becomes +z, +z becomes +y
                parent.translate(x, z, y);
                parent.box(x_dim, z_dim, y_dim);
                break;
            case 3:
                // moving in -y direction
                // x stays the same, +y becomes +z, +z becomes -y
                parent.translate(x, -z, y);
                parent.box(x_dim, z_dim, y_dim);
                break;
            case 4:
                // moving in +z direction
                // xyz coordinates stay the same
                parent.translate(x, y, z);
                parent.box(x_dim, y_dim, z_dim);
                break;
        }

        parent.popMatrix();
    }
}
