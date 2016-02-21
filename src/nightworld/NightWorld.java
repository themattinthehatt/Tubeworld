package nightworld;

import core.Cam;
import core.CamParam;
import core.Site;
import processing.core.PApplet;
import processing.core.PVector;

public class NightWorld extends Site {

    // Inherits from core.Site class
    // parent, Tubeworld PApplet
    // origin
    // render_radius
    // init
    // reset_frames

    int skybox_indx;                // index for determining skybox
    int num_skyboxes;               // total number of skybox options
    boolean toggle_off;
    boolean skybox_switch;


    public NightWorld(PApplet parent_, PVector origin_, float render_radius_, CamParam init_, float reset_frames_){
        // pass arguments to parent constructor
        super(parent_,origin_,render_radius_,init_,reset_frames_);
        skybox_indx = 0;
        num_skyboxes = 3;
        skybox_switch = false;
        toggle_off = true;
    }

    public void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled){
        // update background through shift+tab
        if (keys_pressed[14] && keys_pressed[9]) {
            toggle_off = false;
        } else if (!keys_pressed[9] && !toggle_off) {
            skybox_switch = true;
        }

        if (skybox_switch) {
            skybox_indx = (skybox_indx + 1) % num_skyboxes;
            toggle_off = true;
            skybox_switch = false;
        }
    }

    public void drawSite(){

        /*********** draw origin **************/
        parent.pushMatrix();
        parent.translate(25,0,0);
        parent.stroke(255,0,0);
        parent.fill(255,0,0);
        parent.box(50,5,5);
        parent.popMatrix();

        parent.pushMatrix();
        parent.translate(0,25,0);
        parent.stroke(0,255,0);
        parent.fill(0,255,0);
        parent.box(5,50,5);
        parent.popMatrix();

        parent.pushMatrix();
        parent.translate(0,0,25);
        parent.stroke(0,0,255);
        parent.fill(0,0,255);
        parent.box(5,5,50);
        parent.popMatrix();

        parent.stroke(255,255,255);
        parent.fill(255,255,255);
        parent.box(5,5,5);

    }

    public int updateCam(Cam cam, int state, boolean[] keys_pressed, boolean[] keys_toggled){

        /*********** draw skybox **************/
        if (skybox_indx == 0) {
            // black background
            parent.background(0);
        } else if (skybox_indx == 1) {
            // white background
            parent.background(255);
        } else if (skybox_indx == 2) {
            //
            parent.background(22,0,15);
//            parent.pushMatrix();
//            parent.translate(cam.curr.loc.x,cam.curr.loc.y,cam.curr.loc.z);
//            for (int i = -10; i < 22; i++) {
//                parent.stroke(255);
//                parent.line(((float) i)*1000,-10000,-8000,((float) i)*1000,10000,-8000);
//            }
//            parent.popMatrix();
        }

        return state;
    }

}
