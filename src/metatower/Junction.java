package metatower;

import processing.core.PApplet;

public class Junction {

    public PApplet parent;
    public float x;
    public float y;
    public float z;
    public float x_dim;
    public float y_dim;
    public float z_dim;

    public Junction(PApplet parent_, float x_, float y_, float z_, float x_dim_, float y_dim_, float z_dim_){
        parent = parent_;
        x = x_;
        y = y_;
        z = z_;
        x_dim = x_dim_;
        y_dim = y_dim_;
        z_dim = z_dim_;
    }

    public void drawJunction(){
        parent.pushMatrix();
        parent.translate(x,y,z);
        parent.stroke(255, 255, 255);
        parent.fill(255); //0, 0, 0, 0);
        parent.box(x_dim, y_dim, z_dim);
        parent.popMatrix();
    }
}
