import processing.core.PApplet;
import processing.core.PVector;

public class GlassCube extends Site {
  
	// Inherits from Site class
	// parent, Tubeworld PApplet
	// center
	// rad_site
	// rad_inf
	// init
	// reset_frames
	float side_length;
	ShimmerCube[] cubes;
	int num_cubes;
	int fr_count;
	float dir_mult;
	float rot_rad;
  
	GlassCube(PApplet parent_,PVector center_, float rad_site_, float rad_inf_, CamParam init_, int reset_frames_){
		// pass arguments to parent constructor
		super(parent_,center_,rad_site_,rad_inf_,init_,reset_frames_);
		side_length = 200;
		num_cubes = 5;
		// cube velocities found in cube class
		cubes = new ShimmerCube[num_cubes];
		for (int i = 0; i < num_cubes; i++){
			cubes[i] = new ShimmerCube(parent,side_length,parent.random(20,30));
		}
		fr_count = 0;
	}
	
	/************************************ UPDATE PHYSICS ************************************/
	void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled){
		if (!keys_toggled[32]){ // if not paused
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
	void drawSite(){
		parent.pushMatrix();
		parent.translate(center.x, center.y, center.z);
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
	int updateCam(Cam cam, int state, boolean[] key_pressed){
		if (state == 0) { // reset mode
			state = cam.smoothLinPursuit(init,reset_frames,0,1); // calling state, return state
		} else if (state == 2) { // roller coaster mode
			float theta;
			if (fr_count <= reset_frames) {
				state = cam.smoothSphPursuit(init,center,reset_frames,2,2);    
				fr_count++;
				dir_mult = 5;
				rot_rad = PApplet.PI/246;
			} else if (fr_count > reset_frames) {
				cam.sphMoveTheta(center,PApplet.PI/1024,"center");
				theta = cam.getTheta(center,cam.curr.loc);
				cam.sphSetPhi(center,PApplet.PI/2+PApplet.PI/8*PApplet.sin(theta),"none");
				
				// allow some amount of camera control; exit if other key press after initial reset
				// update speed multipliers
				if (key_pressed[113]){ 
					if (dir_mult > 2){
						--dir_mult;
					}
				}
				if (key_pressed[119]) {
					if (dir_mult < 256){
						++dir_mult;
					}
				}
				if (key_pressed[101]) {
					rot_rad = rot_rad*((float) 0.99);
				}
				if (key_pressed[114]) {
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
						key_pressed[113] || key_pressed[119] || key_pressed[101] || key_pressed[114])) {
					state = 1;
					fr_count = 0;
				} // if keyPressed 
			} // frameCount
    
		} else if (state == 3) {
		}
		return state;
	}
  
}   
    
