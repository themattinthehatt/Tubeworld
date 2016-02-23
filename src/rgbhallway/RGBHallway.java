package rgbhallway;

import core.Cam;
import core.CamParam;
import core.Site;
import processing.core.PApplet;
import processing.core.PVector;
import java.awt.event.KeyEvent;

public class RGBHallway extends Site {
  
	// Inherits from core.Site class
	// parent, Tubeworld PApplet
	// origin
	// render_radius
	// init
	// reset_frames

	public float rect_width;
	public float hall_width;
	public int num_rects;
	public float fr_count;
	public float dir_mult;
	public float rot_rad;

	public RGBHallway(PApplet parent_, PVector origin_, float render_radius, CamParam init_, float reset_frames_){
		// pass arguments to parent constructor
		super(parent_,origin_,render_radius,init_,reset_frames_);
		rect_width = 200;
		hall_width = 100;
		num_rects = 20;
		fr_count = 0;
	}
  
	/************************************ UPDATE PHYSICS ************************************/
	public void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled){
	}    
	
	/************************************ DRAW SITE *****************************************/
	public void drawSite(){
		
		parent.pushMatrix();
		//set initial center
		parent.translate(origin.x, origin.y, origin.z);
    
		for (int i = 0; i < num_rects; i++) {
			parent.translate(-rect_width,hall_width/2,0);
			parent.stroke(0);
			if ((i+1)%3 == 1) {
				parent.fill(255,0,0);
				parent.box(rect_width,0,rect_width);
				parent.translate(0,-hall_width,0);
				parent.fill(255,0,0);
				parent.box(rect_width,0,rect_width);
				parent.translate(0,hall_width/2,0);
			} else if ((i+1)%3 == 2) {
				parent.fill(0,255,0);
				parent.box(rect_width,0,rect_width);
				parent.translate(0,-hall_width,0);
				parent.fill(0,255,0);
				parent.box(rect_width,0,rect_width);
				parent.translate(0,hall_width/2,0);
			} else {
				parent.fill(0,0,255);
				parent.box(rect_width,0,rect_width);
				parent.translate(0,-hall_width,0);
				parent.fill(0,0,255);
				parent.box(rect_width,0,rect_width);
				parent.translate(0,hall_width/2,0);
			}
		}
		parent.popMatrix();
	}
  
	/************************************ UPDATE CAMERA *************************************/
	public int updateCam(Cam cam, int state, boolean[] key_pressed, boolean[] keys_toggled){
		if (state == 0) { // reset mode
			state = cam.smoothLinPursuit(init,reset_frames,0,1); // calling state, return state
		} else if (state == 2) { // roller coaster mode
			if (fr_count == 0){
				dir_mult = 5;
				rot_rad = PApplet.PI/256;
			}
			if (fr_count < reset_frames) {
				state = cam.smoothLinPursuit(init,reset_frames,2,2);    
				fr_count++;
			} else if (fr_count >= reset_frames) {
				// allow some amount of camera control; exit if other key press after initial reset
				// update speed multipliers
				if (key_pressed[KeyEvent.VK_E]){
					if (dir_mult > 2){--dir_mult;}
				}
				if (key_pressed[KeyEvent.VK_R]) {
					if (dir_mult < 256){++dir_mult;}
				}
				if (key_pressed[KeyEvent.VK_T]) {
					rot_rad = rot_rad*(0.99f);
				}
				if (key_pressed[KeyEvent.VK_Y]) {
					rot_rad = rot_rad*(1.01f);
				}
				if (key_pressed[KeyEvent.VK_UP]) { // move forward (inward)
					cam.moveForward(dir_mult);
				}
				if (key_pressed[KeyEvent.VK_DOWN]) { // move backward (outward)
					cam.moveBackward(dir_mult);
				}
				if (key_pressed[KeyEvent.VK_Z]) { // rotate ccw
					cam.rotCCW(rot_rad);
				}
				if (key_pressed[KeyEvent.VK_X]) { // rotate cw
					cam.rotCW(rot_rad);
				} 
				if (key_pressed[KeyEvent.VK_1]) { // state 1
					state = 1;
					fr_count = 0;
				}
				// reset position before running out of hallway
				if (cam.curr.loc.x < origin.x - rect_width*10){
					cam.curr.loc.x = origin.x-rect_width;
				}
			}
		}
		return state;
	} // updateCam method

} 
