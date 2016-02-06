/* core.CamCtrl class is used to update Processing's camera function in response to keyboard
   input or predetermined motion settings 
   keyboard controls can be found in core.KeyHandler class
*/

package core;
import processing.core.PApplet;

public class CamCtrl {
  
	// properties
	public PApplet parent;
	public Cam cam;                 // camera object

	public float rot_rad;          // number of radians to rotate for LEFT or RIGHT key press per frame
	public float dir_mult;         // multiplier for direction to increase forward velocity
	public float pan_mult;         // multiplier for direction to pan
	public int state;               /* rendering state of the program
           		                 	0: reset mode (interactivity disabled)
                	            	1: free view (interactivity enabled through keyboard) (default)
                    	        	2: preprogrammed camera track 1
                        		   	 */
	
	/*************************************** CONSTRUCTOR ************************************/
	public CamCtrl(PApplet parent_, CamParam cam_init_){
    
		parent = parent_;
		
		// initial direction vector and camera inputs 
		cam = new Cam(cam_init_);
    
		// speed multipliers
		rot_rad = PApplet.PI/128;  // number of radians to rotate for LEFT or RIGHT key press per frame
		dir_mult = 20;      // multiplier for direction to increase forward velocity
		pan_mult = 20;      // multiplier for direction to pan
    
		// start off in free viewing mode
		state = 1;
    
  }
  
	/*************************** UPDATE CAMERA LOCATION AND SCENE CENTER ***********************/
	public int update(boolean[] keys_pressed, boolean[] keys_toggled, Site active_site) {

		if (state == 1) { // free viewing setting

			// update speed multipliers
			if (keys_pressed[101]){ 
				if (dir_mult > 2){
					--dir_mult;
				}
			}
			if (keys_pressed[114]) {
				if (dir_mult < 256){
					++dir_mult;
				}
			}
			if (keys_pressed[116]) {
				rot_rad = rot_rad*((float) 0.99);
			}
			if (keys_pressed[121]) {
				rot_rad = rot_rad*((float) 1.01); 
			}
        
			/**************** DISPLACEMENTS **********************/
			if (keys_pressed[2]) { 
				if (keys_pressed[14]) { // move up
					cam.moveUpward(dir_mult);
				} else {             	// move forward
					cam.moveForward(dir_mult);
				}
			}
			if (keys_pressed[3]) { 
				if (keys_pressed[14]) { // move down
					cam.moveDownward(dir_mult);
				} else { 				// move backward
					cam.moveBackward(dir_mult);
				}
			}
			if (keys_pressed[0]) { // pan left
				cam.panLeft(pan_mult);
			}
			if (keys_pressed[1]) { // pan right
				cam.panRight(pan_mult);
			}
        
			/***************** ROTATIONS ***********************/
			if (keys_pressed[97]) { // rotate left
				cam.rotLeft(rot_rad);
			}
			if (keys_pressed[100]) { // rotate right
				cam.rotRight(rot_rad);
			} 
			if (keys_pressed[119]) { // rotate up
				cam.rotUp(rot_rad);
			}
			if (keys_pressed[115]) { // rotate down
				cam.rotDown(rot_rad);          
			}
			if (keys_pressed[122]) { // rotate ccw
				cam.rotCCW(rot_rad);
			}	
			if (keys_pressed[120]) { // rotate cw
				cam.rotCW(rot_rad);
			}
        
			/******************* PRESETS ***************************/
			if (keys_pressed[49]) { // do nothing
				state = 1;
			}
			if (keys_pressed[48]) { // reset camera position
				state = 0;           // begin reset on next frame draw
				// clear keys_pressed
				for (int i = 0; i < keys_pressed.length; i++) {
					keys_pressed[i] = false;
				}
			}
			if (keys_pressed[50]) { // preset 1
				state = 2;           // begin reset on next frame draw  
				// clear keys_pressed
				for (int i = 0; i < keys_pressed.length; i++) {
					keys_pressed[i] = false;
				}
			}
        
		} else { // custom animations
			state = active_site.updateCam(cam,state,keys_pressed); // cam object, calling state
		} 
     
		// update camera
		parent.camera(cam.curr.loc.x,cam.curr.loc.y,cam.curr.loc.z,
			   cam.curr.sc.x,cam.curr.sc.y,cam.curr.sc.z,
			   cam.curr.down.x,cam.curr.down.y,cam.curr.down.z);
//		parent.spotLight(255, 255, 255, cam.curr.loc.x, cam.curr.loc.y, cam.curr.loc.z,
//				cam.curr.dir.x, cam.curr.dir.y, cam.curr.dir.z, PApplet.PI, 1);
		
		return state;
      
  } // end update method 
} // end

