package rgbhallway;

import core.Cam;
import core.CamParam;
import core.Site;
import processing.core.PApplet;
import processing.core.PVector;

public class RGBHallway extends Site {
  
	// Inherits from core.Site class
	// parent, Tubeworld PApplet
	// center
	// rad_site
	// rad_inf
	// init
	// reset_frames
	public float rect_width;
	public float hall_width;
	public int num_rects;
	public int fr_count;
	public float dir_mult;
	public float rot_rad;

	public RGBHallway(PApplet parent_, PVector center_, float rad_site_, float rad_inf_, CamParam init_, int reset_frames_){
		// pass arguments to parent constructor
		super(parent_,center_,rad_site_,rad_inf_,init_,reset_frames_);
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
		parent.translate(center.x, center.y, center.z);
    
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
	public int updateCam(Cam cam, int state, boolean[] key_pressed){
		if (state == 0) { // reset mode
			state = cam.smoothLinPursuit(init,reset_frames,0,1); // calling state, return state
		} else if (state == 2) { // roller coaster mode
			if (fr_count <= reset_frames) {
				state = cam.smoothLinPursuit(init,reset_frames,2,2);    
				fr_count++;
				dir_mult = 5;
				rot_rad = PApplet.PI/256;
			} else if (fr_count > reset_frames) {
				// allow some amount of camera control; exit if other key press after initial reset
				// update speed multipliers
				if (key_pressed[101]){ 
					if (dir_mult > 2){
						--dir_mult;
					}
				}
				if (key_pressed[114]) {
					if (dir_mult < 256){
						++dir_mult;
					}
				}
				if (key_pressed[116]) {
					rot_rad = rot_rad*((float) 0.99);
				}
				if (key_pressed[121]) {
					rot_rad = rot_rad*((float) 1.01); 
				}
				if (key_pressed[2]) { // move forward (inward)
					cam.moveForward(dir_mult);
				}
				if (key_pressed[3]) { // move backward (outward)
					cam.moveBackward(dir_mult);
				}
				if (key_pressed[122]) { // rotate ccw
					cam.rotCCW(rot_rad);
				}
				if (key_pressed[120]) { // rotate cw
					cam.rotCW(rot_rad);
				} 
				if (parent.keyPressed == true && !(key_pressed[2] || key_pressed[3] || key_pressed[122] || key_pressed[120] || 
						key_pressed[101] || key_pressed[114] || key_pressed[116] || key_pressed[121])) {
					state = 1;
					fr_count = 0;
				}
				// reset position before running out of hallway
				if (cam.curr.loc.x < center.x - rect_width*10){
					cam.curr.loc.x = center.x-rect_width;
				}
			}
			
		} else if (state == 3) {
		}
		return state;
	} // updateCam method

} 
