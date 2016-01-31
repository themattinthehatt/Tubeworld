import processing.core.PApplet;
import processing.core.PVector;

/* TODO
 * camera presets
 * don't force movement upwards
 */

public class Tower extends Site {
	
	// Inherits from Site class
	// parent, Tubeworld PApplet
	// center
	// rad_site
	// rad_inf
	// init
	// reset_frames
	 
	// for overall tower structure
	int num_beams_x;			// length of x side in number of beams
	int num_beams_y;			// length of y side in number of beams
	float beam_side_width;		// width (~circumference) of beam  
	float beam_side_len;		// length of beam (JUST distance between nodes; different than length in TowerBeam class)
	float total_side_len_x;		// total side length along x-direction
	float total_side_len_y; 	// total side length along y-direction
	int num_girders;			// number of TowerGirder objects
	TowerGirder[] girder; 		// array of girder objects to draw
	
	// for collision detection
	boolean[][][] is_occupied;	// logical array for collision detection
	float num_poss_pos;			// keep track of number of possible positions to later determine trans_probs
	float[] trans_probs;		// 0 +x; 1 -x; 2 +y; 3 -y;
	float rand;					// random number for determining location of new beams
	int x;						// temp x location in is_occupied
	int y;						// temp y location in is_occupied
	int z;						// temp z location in is_occupied
	int curr_level;				// temp level in is_occupied
	int color;					// temp color for creation of new beams
	int[] girder_order;			// specify order in which to access girders
	int top_level;				// top level (highest among girders) in is_occupied
	int temp_top_level;			// temp top level; necessary so that multiple girders don't move up unnecessarily on same iteration
	int temp_top_level_z;		// z-value of temp_top_level, for resets
	int num_levels;				// maximum number of levels to store in is_occupied
	int update_type;			// 0 for forcing beams upward at same rate, 1 for death and respawning
	
	// state variables
	boolean is_extending_beam;	// beam should extend on this frame; if false, updating logic
	boolean reset;				// resets tower
	
	// camera updates
	float beam_extend_frames; 	// number of frames needed for extension of beam
	float beam_pause_frames;	// number of frames paused during beam extensions
	float fr_count;				// frame counter
	float dir_mult;				// speed multiplier for linear movement	
	float rot_rad;				// angle for rotational movements
	
	Tower(PApplet parent_, PVector center_, float rad_site_, float rad_inf_, CamParam init_, int reset_frames_){
		// pass arguments to parent constructor
		super(parent_,center_,rad_site_,rad_inf_,init_,reset_frames_);
		
		// initialize tower structure properties
		num_beams_x = 10;
		num_beams_y = 10;
		beam_side_width = 5;
		beam_side_len = 50;
		total_side_len_x = ((float) num_beams_x)*beam_side_len;
		total_side_len_y = ((float) num_beams_y)*beam_side_len;
		num_girders = 3;
		girder = new TowerGirder[num_girders];
		
		// initialize logical array for collision detection
		trans_probs = new float[4];
		top_level = 0;		// current "top_level" in is_occupied; should be matched in initialization of TowerGirder
		temp_top_level_z = 0;
		num_levels = 5; 	// total number of levels to keep track of; will be used in a modular manner
		update_type = 0; 	// 0 for forcing beams upward at same rate, 1 for death and respawning, which needs more num_levels 
		is_occupied = new boolean[num_beams_x+1][num_beams_y+1][num_levels]; // plus ones for extra node on end
		for (int i = 0; i < num_beams_x; i++){
			for (int j = 0; j < num_beams_y; j++){
				for (int k = 0; k < num_levels; k++){
					is_occupied[i][j][k] = false;
				}
			}
		}
				
		// initialize TowerGirder objects
		boolean valid_indices = false; 
		for (int i = 0; i < num_girders; i++){
			// loop through until valid initial indices are found
			while (!valid_indices){
				x = (int) parent.random((float) num_beams_x);
				y = (int) parent.random((float) num_beams_y);
				z = (int) 0;
				if (!is_occupied[x][y][z]){
					valid_indices = true;
					// update logical array for valid indices
					is_occupied[x][y][z] = true;
				}
			}
			valid_indices = false;	// reset
			
			// set color
			if (i%3 == 0) {
				color = parent.color(255,0,0);
			} else if (i%3 == 1){
				color = parent.color(0,255,0);
			} else if (i%3 == 2){
				color = parent.color(0,0,255);
			}
			// initialize new TowerGirder object
			girder[i] = new TowerGirder(parent_,x,y,z,color,beam_side_width,beam_side_len);
		}
		
		// initialize state variables
		is_extending_beam = false;	// will begin with pause period, and a new beam will be added to each girder
		
		// initialize camera update variables
		beam_extend_frames = 10;
		beam_pause_frames = 1;
		fr_count = 0;
				
	}
  
	/************************************ UPDATE PHYSICS ************************************/
	void updatePhysics(boolean [] keys_pressed, boolean[] keys_toggled){
		
		// to see if updates should be paused
		if (!keys_toggled[32]){ 
			// not paused; check current phase of updating - extending or paused at node
			if (is_extending_beam){
				if (fr_count < beam_extend_frames){
					// update newest beam in each girder 
					for (int i = 0; i < num_girders; i++){
						girder[i].updateBeam(beam_extend_frames,beam_side_len);
					}
					fr_count++; 					// update frame count
					if (fr_count == beam_extend_frames) {
						// at end of extending phase
						is_extending_beam = false;	// begin paused at node phase on next draw command
						fr_count = 0;				// reset frame count
					}
				} // end frame_count check 
			} else {
				// in pause mode between extensions; if last of these frames, perform next logic update
				fr_count++;							// update frame count
				if (fr_count == beam_pause_frames){
					// at end of paused phase
					addBeamToGirders();
					is_extending_beam = true;	// begin extending beam on next draw command
					fr_count = 0;				// reset frame count
				} // end frame_count check
			} // end phase check
		} // end paused check
		if (keys_pressed[8]){
			resetTower();
		} 
	}
  
	/************************************ ADD BEAM ******************************************/
	void addBeamToGirders() {
		// iterate through girders and find next spot to move to 
		/* temp_top_level allows girders updated after another girder was forced upwards on this iteration
		 * to continue updating on the level below; updating the top_level after all girder updates will
		 * force the upwards move of remaining girders on next iteration
		 */
		temp_top_level = top_level;
		for (int i = 0; i < num_girders; i++){
			
			// fill out temp variables for ease of interpretation
			x = girder[i].curr_pos_x;
			y = girder[i].curr_pos_y;
			z = girder[i].curr_pos_z;
		
			if (update_type == 0){
				// check to see if the current girder needs to be forced upwards
				if (girder[i].curr_level != top_level) {
					// top level updated on last iteration, no error checking
					girder[i].addBeam(4);					// add new beam to girder
					girder[i].curr_pos_z++; 				// update current position
					girder[i].curr_level = top_level;		// update current level
					is_occupied[x][y][top_level] = true;	// update collision array
				} else { 
					// randomly select new position to move to in same level
					findVacantSpot(i);
				}
				
			} else if (update_type == 1) {
				curr_level = girder[i].curr_level;
				
				// if top level is right below current top level, force upwards
				if (curr_level == (temp_top_level+1) % num_levels){
					curr_level = (curr_level+1) % num_levels;	// reassign curr_level
					
					// force upwards
					if (is_occupied[x][y][curr_level]){
						// girder can't move upwards - needs to die :(
						girder[i].resetGirder(is_occupied,temp_top_level,temp_top_level_z,
											  num_beams_x,num_beams_y,beam_side_len,beam_side_width);
						is_occupied[girder[i].curr_pos_x][girder[i].curr_pos_y][temp_top_level] = true;
						findVacantSpot(i);
					} else {
						// girder can move upwards
						girder[i].addBeam(4);					// add new beam to girder
						girder[i].curr_pos_z++; 				// update current position
						girder[i].curr_level = curr_level; 		// update current level
						is_occupied[x][y][curr_level] = true;	// update collision array
					}
					
				} else {
					// girder is free to update normally
					findVacantSpot(i);
				}
					
			} // end update_type check
			
			// update colors; should be moved to addBeam method
			for (int j = 0; j < girder[i].num_beams; j++) {
				if (i%3 == 0){
					float r = parent.red(girder[i].beam[j].color);
					r = r-2;
					girder[i].beam[j].color = parent.color(r,0,0);
				} else if (i%3 == 1) {
					float g = parent.green(girder[i].beam[j].color);
					g = g-2;
					girder[i].beam[j].color = parent.color(0,g,0);
				} else if (i%3 == 2) {
					float b = parent.blue(girder[i].beam[j].color);
					b = b-2;
					girder[i].beam[j].color = parent.color(0,0,b);
				}
			}
		} // end girder loop
		
		top_level = temp_top_level; // finally update top_level
	}

	void findVacantSpot(int i){
		// i is index into girder array
		// top_level (update_type = 0) or curr_level (update_type = 1)

		// fill out temp variables for ease of interpretation
		x = girder[i].curr_pos_x;
		y = girder[i].curr_pos_y;
		z = girder[i].curr_pos_z;
		curr_level = girder[i].curr_level;
		
		// initialize relevant variables; trans_probs entries should add to 4
		num_poss_pos = 4;
		trans_probs[0] = 1; // +x
		trans_probs[1] = 1; // -x
		trans_probs[2] = 1; // +y
		trans_probs[3] = 1; // -y
		
		// check for boundaries and already occupied nodes
		if ((girder[i].curr_pos_x == num_beams_x) || is_occupied[x+1][y][curr_level]){
			trans_probs[0] = 0;	// don't increase x value
			num_poss_pos--;		// decrement 
		}
		if ((girder[i].curr_pos_x == 0) || is_occupied[x-1][y][curr_level]){
			trans_probs[1] = 0; // don't decrease x value
			num_poss_pos--;		// decrement
		}
		if ((girder[i].curr_pos_y == num_beams_y) || is_occupied[x][y+1][curr_level]){
			trans_probs[2] = 0; // don't increase y value
			num_poss_pos--;		// decrement
		}
		if ((girder[i].curr_pos_y == 0) || is_occupied[x][y-1][curr_level]){
			trans_probs[3] = 0; // don't decrease y value
			num_poss_pos--;		// decrement
		}
		
		// update transition probabilities and move
		if (num_poss_pos == 0){					
			// girder is forced in upwards direction
			
			curr_level = (curr_level+1) % num_levels;		// reassign level
			
			// see if girder can move upwards
			if (is_occupied[x][y][curr_level] && (curr_level != (temp_top_level+1) % num_levels)){
				// above space is blocked, and NOT because we're looping around is_occupied
				// girder can't move upwards - needs to die :(
				girder[i].resetGirder(is_occupied,temp_top_level,temp_top_level_z,
									  num_beams_x,num_beams_y,beam_side_len,beam_side_width);
				is_occupied[girder[i].curr_pos_x][girder[i].curr_pos_y][temp_top_level] = true;
				// call function recursively
				findVacantSpot(i);
			} else {
				// girder can move upwards
				girder[i].addBeam(4);				// add new beam to girder
				girder[i].curr_pos_z++; 			// update current position
				girder[i].curr_level = curr_level;	// update current level
			
				// check to see if any other girders have moved to this level; 
				// if not, reset is_occupied and update temp_top_level, but NOT top_level
				if (girder[i].curr_level == (temp_top_level+1) % num_levels){
					// one level above temp_top_level, first girder on this level
					temp_top_level = girder[i].curr_level;
					temp_top_level_z++;
					// reset is_occupied at this level
					for (int j = 0; j < num_beams_x; j++) {
						for (int k = 0; k < num_beams_y; k++) {
							is_occupied[j][k][temp_top_level] = false;
						}
					}
				}
			}
		
			is_occupied[x][y][temp_top_level] = true;	// update collision array
		
		} else {
			// at least one horizontal space is open 
			
			// update probabilities
			trans_probs[0] = trans_probs[0]/num_poss_pos;
			trans_probs[1] = trans_probs[1]/num_poss_pos;
			trans_probs[2] = trans_probs[2]/num_poss_pos;
			trans_probs[3] = trans_probs[3]/num_poss_pos;
			
			// randomly update direction using random number and cumulative probabilities
			// note: need to addBeam before updating position due to the way updating is handled
			rand = parent.random(1);					// random number between 0 and 1
			if (rand < trans_probs[0]) {
				girder[i].addBeam(0);					// add new beam to girder
				girder[i].curr_pos_x++;					// update current position
				is_occupied[x+1][y][curr_level] = true;	// update collision array
			} else if (rand < trans_probs[0]+trans_probs[1]){
				girder[i].addBeam(1);					// add new beam to girder
				girder[i].curr_pos_x--;					// update current position
				is_occupied[x-1][y][curr_level] = true;	// update collision array
			} else if (rand < trans_probs[0]+trans_probs[1]+trans_probs[2]){
				girder[i].addBeam(2);					// add new beam to girder
				girder[i].curr_pos_y++; 				// update current position
				is_occupied[x][y+1][curr_level] = true;	// update collision array
			} else {
				girder[i].addBeam(3);					// add new beam to girder
				girder[i].curr_pos_y--;					// update current position
				is_occupied[x][y-1][curr_level] = true;	// update collision array
			}
		}
	}
	
	/************************************ DRAW SITE *****************************************/
	void drawSite(){
		
		parent.pushMatrix();
		parent.translate(center.x, center.y, center.z);

		// draw bounding box
		parent.pushMatrix();
		parent.translate(total_side_len_x/2,total_side_len_y/2,0);
		parent.stroke(255,255,255);
		parent.fill(0,0,0,0);
		parent.box(total_side_len_x+2*beam_side_len,total_side_len_y+2*beam_side_len,0);
		parent.popMatrix();
		
		// draw girders
		for (int i = 0; i < num_girders; i++){
			girder[i].drawGirder();
		}
		
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
				} // if keyPressed 
			} // frameCount
    
		} else if (state == 3) {
		}
		return state;
	}
  
	
	/************************************ RESET *********************************************/
	void resetTower(){
		
		// reinitialize logical array for collision detection
		top_level = 0;	// current "top_level" in is_occupied
		temp_top_level_z = 0;
		for (int i = 0; i < num_beams_x; i++){
			for (int j = 0; j < num_beams_y; j++){
				for (int k = 0; k < num_levels; k++){
					is_occupied[i][j][k] = false;
				}
			}
		}
				
		// reinitialize TowerGirder objects
		for (int i = 0; i < num_girders; i++){
			girder[i].resetGirder(is_occupied,0,temp_top_level_z,num_beams_x,num_beams_y,
								  beam_side_len,beam_side_width);
			// update logical array for valid indices
			is_occupied[girder[i].curr_pos_x][girder[i].curr_pos_y][girder[i].curr_pos_z] = true;
		}
		
		// reinitialize state variables
		is_extending_beam = false;	// will begin with pause period, and a new beam will be added to each girder
		fr_count = 0;
	}

}
