package mengersponge;

import core.Cam;
import core.CamParam;
import core.Site;
import processing.core.PApplet;
import processing.core.PVector;
import java.awt.event.KeyEvent;

public class MengerSponge extends Site {

	// Inherits from core.Site class
	// parent, Tubeworld PApplet
	// origin
	// render_radius
	// init
	// reset_frames

	public float side_len;
	public MengerCube[] cubes;
	public int fr_count;
	public float dir_mult;
	public float rot_rad;
	public float order;
	public float num_cubes_side;
	public float num_cubes_tot;
	public float num_cubes_rendered;
	public float cube_side_len;
	public PVector cam_center;			// for camera updates

	public MengerSponge(PApplet parent_, PVector origin_, float render_radius_, CamParam init_, float reset_frames_){

		// pass arguments to parent constructor
		super(parent_,origin_,render_radius_,init_,reset_frames_);
		
		// define parameters for cube
		side_len = 540; // side_len of whole sponge
		order = 2;
		
		// define derived parameters
		num_cubes_side = PApplet.pow(3,order);
		num_cubes_tot = PApplet.pow(num_cubes_side,3);
		double base = 20.0/27.0;
		num_cubes_rendered = PApplet.pow((float) base,order)*num_cubes_tot;
		cube_side_len = side_len/num_cubes_side;
		
		// instantiate cube array
		cubes = new MengerCube[(int) PApplet.ceil(num_cubes_rendered)];
		
		// store properties of individual cubes
		float x = 0;
		float y = 0;
		float z = 0;
		int cube_color = parent.color(255,255,255); 
		
		// keep track of coordinate ranges of centers of disallowed blocks; interval in [0,1]
		// get total number of intervals		
		int num_intervals = 0;
		for (int i = 0; i < order; i++) {
			num_intervals = num_intervals + (int) PApplet.pow(3, i); // hierarchical increase
		}
		// get coordinates of intervals
		float[] coordMins = new float[num_intervals]; // min coordinates for different intervals
		float[] coordMaxs = new float[num_intervals]; // corresponding max coordinate		
		int counter = 0;
		float interval_width = 0;
		for (int i = 0; i < order; i++){
			interval_width = 1/PApplet.pow(3, i+1);
			for (int j = 0; j < PApplet.pow(3, i); j++){
				coordMins[counter] = ((j*3+1)/PApplet.pow(3, i+1))*side_len;
				coordMaxs[counter] = ((j*3+1)/PApplet.pow(3, i+1)+interval_width)*side_len;
				counter++;
			}
		}
		
		// storage
		float[] xIndicator = new float[num_intervals]; // keep track if cube x-val is in interval
		float[] yIndicator = new float[num_intervals]; // keep track if cube y-val is in interval
		float[] zIndicator = new float[num_intervals]; // keep track if cube z-val is in interval
		boolean inSponge = true;
		int cube_counter = 0;
		// iterate through all possible cubes, just instantiate those in sponge
		for (int i = 0; i < num_cubes_tot; i++){
			// make xyz values fractions of 1, multiply by side length of cube, then shift back by half a side length
			// since we're going to 1:num_cubes_side
			x = ((((float) i) % num_cubes_side)+1)*cube_side_len-cube_side_len/2;
			y = (((PApplet.floor(((float) i) / num_cubes_side)) % num_cubes_side)+1)*cube_side_len-cube_side_len/2;
			z = ((PApplet.floor(PApplet.floor(((float) i) / num_cubes_side) / num_cubes_side))+1)*cube_side_len-cube_side_len/2;

			// check for location inside disallowed intervals
			for (int j = 0; j < num_intervals; j++){
				xIndicator[j] = 0;
				yIndicator[j] = 0;
				zIndicator[j] = 0;
				if (x > coordMins[j] && x < coordMaxs[j]){
					xIndicator[j] = 1;
				} 
				if (y > coordMins[j] && y < coordMaxs[j]){
					yIndicator[j] = 1;
				}
				if (z > coordMins[j] && z < coordMaxs[j]){
					zIndicator[j] = 1;
				}
			}
			
			// see if 2 or more conditions were met within a single iteration (order)
			// if so, cube is not in sponge
			float sum = 0;
			counter = 0;
			inSponge = true;
			for (int j = 0; j < order; j++){
				sum = 0;
				for (int k = 0; k < PApplet.pow(3, j); k++){
					sum = sum + xIndicator[counter] + yIndicator[counter] + zIndicator[counter];
					counter++;
				}
				if (sum > 1){
					inSponge = false;
				}
			}
			
			// assign cube color based on sponge membership
			if (inSponge) {
				cube_color = parent.color((int) x*255/(side_len),(int) y*255/(side_len),(int) z*255/(side_len));
				cubes[cube_counter] = new MengerCube(parent_,cube_side_len,new PVector(x,y,z),cube_color);
				cube_counter++;
			}
		} // end cube iteration

		// camera stuff
		fr_count = 0;
		// redefine init based on size of sponge
		init.dir = new PVector(-1,0,0);
		init.down = new PVector(0,0,-1);
		init.loc = new PVector(origin.x+2*side_len, origin.y+side_len/2, origin.z+side_len/2);
		init.sc = new PVector(origin.x+side_len/2, origin.y+side_len/2, origin.z+side_len/2);
		// define another center of the sponge for camera presets ('center' property should really be called origin)
		cam_center = new PVector(origin.x+side_len/2,origin.y+side_len/2,origin.z+side_len/2);

	} // end Constructor
	
	/************************************ UPDATE PHYSICS ************************************/
	public void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled){
	}    
  
	/************************************ DRAW SITE *****************************************/
	public void drawSite(){
		parent.pushMatrix();
		parent.translate(origin.x,origin.y,origin.z);

		// draw cubes
		for (int i = 0; i < num_cubes_rendered; i++){
			parent.pushMatrix();
			parent.translate(cubes[i].loc.x,cubes[i].loc.y,cubes[i].loc.z);
			parent.stroke(0);
			parent.fill(cubes[i].cube_color);
			parent.box(cubes[i].side_len);
			parent.popMatrix();
		}
		parent.popMatrix();
	}
  
	/************************************ UPDATE CAMERA *************************************/
	public int updateCam(Cam cam, int state, boolean[] keys_pressed, boolean[] keys_toggled){
		if (state == 0) { // reset mode
			state = cam.smoothLinPursuit(init,reset_frames,0,1); // calling state, return state
		} else if (state == 2) { // roller coaster mode
			float theta;
			if (fr_count == 0){
				dir_mult = 5;
				rot_rad = PApplet.PI/246;
			}
			if (fr_count < reset_frames) {
				state = cam.smoothSphPursuit(init,cam_center,reset_frames,2,2);
				fr_count++;
			} else if (fr_count >= reset_frames) {
				cam.sphMoveTheta(cam_center,PApplet.PI/1024,"center");
				theta = cam.getTheta(cam_center,cam.curr.loc);
				cam.sphSetPhi(cam_center,PApplet.PI/2+PApplet.PI/8*PApplet.sin(theta),"none");

				// allow some amount of camera control; exit if other key press after initial reset
				// update speed multipliers
				if (keys_pressed[KeyEvent.VK_E]){
					if (dir_mult > 2){--dir_mult;}
				}
				if (keys_pressed[KeyEvent.VK_R]) {
					if (dir_mult < 256){++dir_mult;}
				}
				if (keys_pressed[KeyEvent.VK_T]) {
					rot_rad = rot_rad*(0.99f);
				}
				if (keys_pressed[KeyEvent.VK_Y]) {
					rot_rad = rot_rad*(1.01f);
				}
				if (keys_pressed[KeyEvent.VK_UP]) { // move forward (inward)
					cam.moveForward(dir_mult);
				}
				if (keys_pressed[KeyEvent.VK_DOWN]) { // move backward (outward)
					cam.moveBackward(dir_mult);
				}
				if (keys_pressed[KeyEvent.VK_Z]) { // rotate ccw
					cam.rotCCW(rot_rad);
				}
				if (keys_pressed[KeyEvent.VK_X]) { // rotate cw
					cam.rotCW(rot_rad);
				}
				if (keys_pressed[KeyEvent.VK_1]) { // return to state 1
					state = 1;
					fr_count = 0;
				} // if keyPressed
			}
		}
		return state;
	}
  
}   
    
