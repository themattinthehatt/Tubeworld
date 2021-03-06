package glasscube;

import core.Cam;
import core.CamParam;
import core.Site;
import processing.core.PApplet;
import processing.core.PVector;
import java.awt.event.KeyEvent;

public class GlassCube extends Site {
  
	// Inherits from core.Site class
	// parent, Tubeworld PApplet
	// origin
	// render_radius
	// init
	// reset_frames

	public float side_length;
	public 	ShimmerCube[] cubes;
	public int num_cubes;
	public int fr_count;
	public float dir_mult;
	public float rot_rad;

	public GlassCube(PApplet parent_, PVector origin_, float render_radius, CamParam init_, float reset_frames_){
		// pass arguments to parent constructor
		super(parent_,origin_,render_radius,init_,reset_frames_);
		side_length = 200;							// lenght of large cube side
		num_cubes = 5;								// number of smaller cubes within larger cube
		cubes = new ShimmerCube[num_cubes];			// cube velocities and sizes found in cube class
		for (int i = 0; i < num_cubes; i++){
			cubes[i] = new ShimmerCube(parent,side_length,parent.random(20,30));
		}
		fr_count = 0;
	}
	
	/************************************ UPDATE PHYSICS ************************************/
	public void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled){
		if (!keys_toggled[KeyEvent.VK_SPACE]){ // if not paused
			for (int i = 0; i < num_cubes; i++){
				cubes[i].updatePos();
				cubes[i].detectBoundaries();
				for (int j = i+1; j < num_cubes; j++){
					cubes[i].detectCollision(cubes[j]);
				}
				cubes[i].shimmer();
			}
		}
	}    
  
	/************************************ DRAW SITE *****************************************/
	public void drawSite(){
		parent.pushMatrix();
		parent.translate(origin.x, origin.y, origin.z);
		// draw colliding cubes
		for (int i = 0; i < num_cubes; i++){
			cubes[i].drawCube();
		}
		// draw boundary cube
		parent.stroke(200);
		parent.noFill();
		//fill(128,128,255,0);
		parent.box(side_length);
		parent.popMatrix();
	}
  
	/************************************ UPDATE CAMERA *************************************/
	public int updateCam(Cam cam, int state, boolean[] key_pressed, boolean[] keys_toggled){
		if (state == 0) { // reset mode
			state = cam.smoothLinPursuit(init,reset_frames,0,1); // calling state, return state
		} else if (state == 2) { // roller coaster mode
			float theta;
			if (fr_count == 0){
				dir_mult = 5;
				rot_rad = PApplet.PI/246;
			}
			if (fr_count < reset_frames) {
				state = cam.smoothSphPursuit(init,origin,reset_frames,2,2);
				fr_count++;
			} else if (fr_count >= reset_frames) {
				cam.sphMoveTheta(origin,PApplet.PI/1024,"center");
				theta = cam.getTheta(origin,cam.curr.loc);
				cam.sphSetPhi(origin,PApplet.PI/2+PApplet.PI/8*PApplet.sin(theta),"none");
				
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
				if (key_pressed[KeyEvent.VK_1]){  // return to state 1
					state = 1;
					fr_count = 0;
				} // if keyPressed 
			} // frameCount
    
		}
		return state;
	}
  
}   
    
