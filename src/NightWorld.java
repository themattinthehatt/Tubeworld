import processing.core.PApplet;
import processing.core.PVector;

public class NightWorld extends Site {

    // Inherits from Site class
    // parent, Tubeworld PApplet
    // center
    // rad_site
    // rad_inf
    // init
    // reset_frames

    NightWorld(PApplet parent_, PVector center_, float rad_site_, float rad_inf_, CamParam init_, int reset_frames_){
        // pass arguments to parent constructor
        super(parent_,center_,rad_site_,rad_inf_,init_,reset_frames_);
    }

    void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled){
    }

    void drawSite(){

        parent.background(0);   // draw background

        /*************** draw windows ****************/
//		parent.pushMatrix();
//		parent.translate(3000,0,0);
//		parent.noStroke();
//		parent.fill(64,64,255);
//		parent.box(200,0,200);
//		parent.popMatrix();
//
//		parent.pushMatrix();
//		parent.translate(-3000,0,0);
//		parent.noStroke();
//		parent.fill(64,64,255);
//		parent.box(200,0,200);
//		parent.popMatrix();
//
//		parent.pushMatrix();
//		parent.translate(0,3000,0);
//		parent.noStroke();
//		parent.fill(64,64,255);
//		parent.box(0,200,200);
//		parent.popMatrix();
//
//		parent.pushMatrix();
//		parent.translate(0,-3000,0);
//		parent.noStroke();
//		parent.fill(64,64,255);
//		parent.box(0,200,200);
//		parent.popMatrix();

        /*********** draw origin **************/
        parent.pushMatrix();
        parent.translate(25,0,0);
        parent.fill(255,0,0);
        parent.box(50,5,5);
        parent.popMatrix();

        parent.pushMatrix();
        parent.translate(0,25,0);
        parent.fill(0,255,0);
        parent.box(5,50,5);
        parent.popMatrix();

        parent.pushMatrix();
        parent.translate(0,0,25);
        parent.fill(0,0,255);
        parent.box(5,5,50);
        parent.popMatrix();

        parent.fill(255,255,255);
        parent.box(5,5,5);

    }

    int updateCam(Cam cam, int state, boolean[] keys_pressed){
        return state;
    }

}
