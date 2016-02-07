package tower;

import core.Cam;
import core.CamParam;
import core.Site;

import processing.core.PApplet;
import processing.core.PVector;

/* TODO
 *
 */

public class Tower extends Site {
	
	// Inherits from core.Site class
	// parent, Tubeworld PApplet
	// center
	// rad_site
	// rad_inf
	// init
	// reset_frames
	 
	// for overall tower structure
	public int num_beams_x;				// length of x side in number of beams
	public int num_beams_y;				// length of y side in number of beams
	public int num_beams_z; 			// length of z side in number of beams
	public float beam_side_width;		// width (~circumference) of beams
	public float beam_side_len;			// length of beam (JUST distance between nodes; different than length in tower.TowerBeam class)
	public float total_side_len_x;		// total side length along x-direction
	public float total_side_len_y; 		// total side length along y-direction
	public int num_girders;				// number of tower.TowerGirder objects
	public TowerGirder[] girder; 		// array of girder objects to draw
	public int tower_orientation;		// orientation of tower; 0/1 along +/-x dir, 2/3 along +/-y dir, 4 along +z dir
	public float fr_count;				// keep track of extending and pausing beams
	private float xf;					// temp x location in drawing method
	private float yf;					// temp y location in drawing method
	private float zf;					// temp z location in drawing method
	
	// for collision detection and dynamics
	public boolean[][][] is_occupied;	// logical array for collision detection
	private float num_poss_pos;			// keep track of number of possible positions to later determine trans_probs
	private float[] trans_probs;			// 0 +x; 1 -x; 2 +y; 3 -y;
	private float rand;					// random number for determining location of new beams
	private int x;						// temp x location in is_occupied
	private int y;						// temp y location in is_occupied
	private int z;						// temp z location in is_occupied
	private int curr_level;				// temp level in is_occupied
	private int color;					// temp color for creation of new beams
	private int top_level;				// top level (highest among girders) in is_occupied
	private int temp_top_level;			// temp top level; necessary so that multiple girders don't move up unnecessarily on same iteration
	private int temp_top_level_z;		// z-value of temp_top_level, for resets
	private int num_levels;				// maximum number of levels to store in is_occupied
	public int update_type;				// 0 for forcing beams upward at same rate, 1 for death and respawning
	public int stopping_behavior; 		// 0 to mod everything by max_num_levels, 1 to stop updating dynamics
	public boolean dynamics_stopped;	// boolean specifying whether dynamics should be updated or not
	public float beam_extend_frames; 	// number of frames needed for extension of beam
	public float beam_pause_frames;		// number of frames paused during beam extensions

	// state variables
	public boolean is_extending_beam;	// beam should extend on this frame; if false, updating logic

	// camera updates
	public float cam_fr_count;			// frame counter
	public float dir_mult;				// speed multiplier for linear movement
	public float rot_rad;				// angle for rotational movements
	public CamParam preset_cam;			// for camera updates
	public PVector cam_center;			// coordinates for center of scene to use for updating preset_cam
	public float top_level_extending;	// frame count for camera updates
	
	/************************************ CONSTRUCTOR ***************************************/
	public Tower(PApplet parent_, PVector center_, float rad_site_, float rad_inf_, CamParam init_, int reset_frames_){

		// pass arguments to parent constructor; set Site properties
		super(parent_,center_,rad_site_,rad_inf_,init_,reset_frames_);
		
		// initialize tower structure properties
		num_beams_x = 5;
		num_beams_y = 5;
		num_beams_z = 100; 		// total number of levels to draw
		beam_side_width = 5; 	// (in pixels)
		beam_side_len = 50;  	// (in pixels)
		total_side_len_x = ((float) num_beams_x)*beam_side_len;
		total_side_len_y = ((float) num_beams_y)*beam_side_len;
		num_girders = 3;
		girder = new TowerGirder[num_girders];
		tower_orientation = 4;
		// reset parts of init to be at the center, given the number of beams
		init.loc = new PVector(center.x+total_side_len_x/2,center.y+total_side_len_y/2,center.z+600);
		init.sc  = new PVector(center.x+total_side_len_x/2,center.y+total_side_len_y/2,center.z);
		init.dir = new PVector(0,0,-1);
		init.down = new PVector(0,-1,0);
		
		// initialize logical array for collision detection
		trans_probs = new float[4];
		top_level = 0;			// current "top_level" in is_occupied
		temp_top_level_z = 0;	// z-value of temp_top_level variable; used for resetting girders to highest level
		num_levels = 3; 		// total number of levels to keep track of; will be used in a modular manner
		update_type = 0; 		// 0 for forcing beams upward at same rate, 1 for death and respawning, which needs more num_levels
		stopping_behavior = 0; 	// behavior when num_beams_z is reached
		dynamics_stopped = false;
		is_occupied = new boolean[num_beams_x+1][num_beams_y+1][num_levels]; // plus ones for extra node on ends
		for (int i = 0; i < num_beams_x; i++){
			for (int j = 0; j < num_beams_y; j++){
				for (int k = 0; k < num_levels; k++){
					is_occupied[i][j][k] = false;
				}
			}
		}
				
		// initialize tower.TowerGirder objects
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
			// initialize new tower.TowerGirder object
			girder[i] = new TowerGirder(parent_,x,y,z,color,beam_side_width,beam_side_len);
		}
		
		// initialize state variables
		is_extending_beam = false;	// will begin with pause period, and a new beam will be added to each girder
		
		// initialize camera update variables
		beam_extend_frames = 10;
		beam_pause_frames = 1;
		fr_count = 0;
		// core.CamParam object
		/*
		core.CamParam preset_cam;     // initial camera dir,loc,sc and down for camera presets
	    PVector dir;             // xyz coordinates of direction vector
	    PVector loc;             // xyz coordinates of camera
	    PVector sc;              // xyz coordinates of scene center
	    PVector down;            // xyz coordinates of downward direction of camera
	    */
		cam_center = new PVector(center.x+total_side_len_x/2,center.y+total_side_len_y/2,center.z);
		preset_cam = new CamParam(new PVector(-1,0,0),
					 new PVector(center.x+2*total_side_len_x,center.y+total_side_len_y/2,center.z+500),
					 new PVector(center.x+total_side_len_x/2,center.y+total_side_len_y/2,center.z),
					 new PVector(0,0,-1));		
	}
  
	/************************************ UPDATE PHYSICS ************************************/
	public void updatePhysics(boolean [] keys_pressed, boolean[] keys_toggled){
		
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
					// shift camera center up if a beam on uppermost level is extending
					if (top_level_extending > 0){
						cam_center.z = cam_center.z + beam_side_len/beam_extend_frames;
						preset_cam.loc.z = preset_cam.loc.z + beam_side_len/beam_extend_frames;
						preset_cam.sc.z = preset_cam.sc.z + beam_side_len/beam_extend_frames;
						top_level_extending--;
					}
				} // end frame_count check 
			} else if (!dynamics_stopped){
				// in pause mode between extensions; if last of these frames, perform next logic update
				fr_count++;							// update frame count
				if (fr_count == beam_pause_frames){
					// at end of paused phase
					is_extending_beam = true;	// begin extending beam on next draw command
					fr_count = 0;				// reset frame count
					addBeamToGirders();
				} // end frame_count check
			} // end phase check
		} // end paused check
		if (keys_pressed[8]){
			resetTower();
		} 
	}
  
	/************************************ ADD BEAM ******************************************/
	public void addBeamToGirders() {
		// iterate through girders and find next spot to move to 
		/* temp_top_level allows girders updated after another girder was forced upwards on this iteration
		 * to continue updating on the level below; updating the top_level after all girder updates will
		 * force the upwards move of remaining girders on next iteration
		 */
		temp_top_level = top_level;
		// check for height limit
		if (temp_top_level_z == num_beams_z){
			if (stopping_behavior == 0){
				// continue tower; just reset for now; need to fix camera presets and such
				resetTower();
				return;
			} else if (stopping_behavior == 1){
				// freeze tower
				dynamics_stopped = true;
				// update any girders that are not on temp_top_level; this really only looks good for update_type = 0;
				for (int i = 0; i < num_girders; i++){
					if (girder[i].curr_level != temp_top_level){
						// move up if possible
						if (!is_occupied[girder[i].curr_pos_x][girder[i].curr_pos_y][temp_top_level]){
							// girder can move upwards
							girder[i].addBeam(4);                    // add new beam to girder
							girder[i].curr_pos_z++;                // update current position
							girder[i].curr_level = curr_level;        // update current level
							is_occupied[girder[i].curr_pos_x][girder[i].curr_pos_y][curr_level] = true;    // update collision array
						}
					} else {
						girder[i].addBeam(5); // add in backwards direction
					}
				}
				is_extending_beam = true;
			}
		} else {
			// iterate through girders
			for (int i = 0; i < num_girders; i++) {
				// fill out temp variables for ease of interpretation
				x = girder[i].curr_pos_x;
				y = girder[i].curr_pos_y;
				z = girder[i].curr_pos_z;

				if (update_type == 0) {
					// check to see if the current girder needs to be forced upwards
					if (girder[i].curr_level != top_level) {
						// top level updated on last iteration, no error checking
						girder[i].addBeam(4);                    // add new beam to girder
						girder[i].curr_pos_z++;                // update current position
						girder[i].curr_level = top_level;        // update current level; can do this since beam can only be on top_level-1 if not on top level
						is_occupied[x][y][top_level] = true;    // update collision array
					} else {
						// randomly select new position to move to in same level
						findVacantSpot(i);                        // i just indexes which girder we'll be updating
					}

				} else if (update_type == 1) {
					curr_level = girder[i].curr_level;

					/* if current level is right above temp_top_level, this means the top of is_occupied is looping back around
					 * and about to catch this girder. To fix this situation we'll force the current girder upwards and then
					 * clear that level of is_occupied to make room for the beams in top_level that will soon be moving up
					 * again
					 * This handles case when current beam is forced up from below in is_occupied
					 * case where current beam forces up beams above is handled in findVacantSpot
					 */
					if (curr_level == (temp_top_level + 1) % num_levels) {
						curr_level = (curr_level + 1) % num_levels;    // reassign curr_level

						// force upwards; this deals with case when another girder has moved up first, and this girder has
						// respond
						if (is_occupied[x][y][curr_level]) {
							/* girder can't move upwards - needs to die :(
							 * Note that is_occupied is not updated to reflect the beam leaving this level - this will be
							 * handled when a beam from the top level moves into this level, i.e. a complete reset of values
							 */
							girder[i].resetGirder(is_occupied, temp_top_level, temp_top_level_z,
									num_beams_x, num_beams_y, beam_side_len, beam_side_width);
							// update is_occupied done in resetGirder
							findVacantSpot(i);
							PApplet.println("1");
						} else {
							// girder can move upwards
							girder[i].addBeam(4);                    // add new beam to girder
							girder[i].curr_pos_z++;                // update current position
							girder[i].curr_level = curr_level;        // update current level
							is_occupied[x][y][curr_level] = true;    // update collision array
						}

					} else {
						// girder is free to update normally
						findVacantSpot(i);
					}

				} // end update_type check

				// update colors; should be moved to addBeam method
				for (int j = 0; j < girder[i].num_beams; j++) {
					if (i % 3 == 0) {
						float r = parent.red(girder[i].beam[j].color);
						r = r - 2;
						girder[i].beam[j].color = parent.color(r, 0, 0);
					} else if (i % 3 == 1) {
						float g = parent.green(girder[i].beam[j].color);
						g = g - 2;
						girder[i].beam[j].color = parent.color(0, g, 0);
					} else if (i % 3 == 2) {
						float b = parent.blue(girder[i].beam[j].color);
						b = b - 2;
						girder[i].beam[j].color = parent.color(0, 0, b);
					}
				}
			} // end girder loop
			if (temp_top_level != top_level) {
				// we've moved up; set flag so we can increase center for cam preset
				top_level_extending = beam_extend_frames;
			}
			top_level = temp_top_level; // finally update top_level
		} // end check height limit
	}

	/************************************ ADD BEAM HELPER ***********************************/
	public void findVacantSpot(int i){
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
		if ((x == num_beams_x) || is_occupied[x+1][y][curr_level]){
			trans_probs[0] = 0;	// don't increase x value
			num_poss_pos--;		// decrement 
		}
		if ((x == 0) || is_occupied[x-1][y][curr_level]){
			trans_probs[1] = 0; // don't decrease x value
			num_poss_pos--;		// decrement
		}
		if ((y == num_beams_y) || is_occupied[x][y+1][curr_level]){
			trans_probs[2] = 0; // don't increase y value
			num_poss_pos--;		// decrement
		}
		if ((y == 0) || is_occupied[x][y-1][curr_level]){
			trans_probs[3] = 0; // don't decrease y value
			num_poss_pos--;		// decrement
		}
		
		// update transition probabilities and move
		if (num_poss_pos == 0){					
			// girder is forced in upwards direction
			
			curr_level = (curr_level+1) % num_levels;		// reassign level
			
			// see if girder can move upwards
			/* if the node above is blocked, and there could be 2 reasons for this.
			 * 1. the node is actually blocked, and we can't move there. in this case the girder can't move upwards
			 * and needs to die :(
			 * 2. the beam is on the top level, and the level above is the old logic setup from several levels
			 * BEHIND the top level; in this case we DO want to move into that space, and reset the is_occupied
			 * array while we're at it. There is no possibility that an actual beam might still occupy this location,
			 * since these beams will have been forced upwards during the previous iteration
			 *
			 * This handles case where current beam forces up beams above
			 * case when current beam is forced up from below in is_occupied is handled in addBeamToGirders
			*/
			if (curr_level == (temp_top_level+1) % num_levels) {
				// girder CAN move upwards because we're moving into an old logic layer
				girder[i].addBeam(4);				// add new beam to girder
				girder[i].curr_pos_z++; 			// update current position
				girder[i].curr_level = curr_level;	// update current level

				// one level above temp_top_level, first girder on this level
				temp_top_level = curr_level;
				temp_top_level_z++;
				// reset is_occupied at this level
				for (int j = 0; j < num_beams_x; j++) {
					for (int k = 0; k < num_beams_y; k++) {
						is_occupied[j][k][curr_level] = false;
					}
				}
				is_occupied[x][y][curr_level] = true;	// update collision array
			} else if (is_occupied[x][y][curr_level]){
				// girder CAN'T move upwards, and we're not on top layer - needs to die :(
				girder[i].resetGirder(is_occupied,temp_top_level,temp_top_level_z,
									  num_beams_x,num_beams_y,beam_side_len,beam_side_width);
				// update is_occupied  done in resetGirder
				// call function recursively
				findVacantSpot(i);
			} else {
				// [x][y][curr_level] is NOT occupied, and we're not moving into an old logic layer; move upwards
				girder[i].addBeam(4);					// add new beam to girder
				girder[i].curr_pos_z++; 				// update current position
				girder[i].curr_level = curr_level;		// update current level
				is_occupied[x][y][curr_level] = true;	// update collision array
			}
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
	public void drawSite(){
		
		parent.pushMatrix();
		parent.translate(center.x, center.y, center.z);

//		// draw bounding box; take tower orientation into account
//		xf = total_side_len_x + 2*beam_side_len;
//		yf = total_side_len_y + 2*beam_side_len;
//		zf = 0;
//
//		switch (tower_orientation) {
//			case 0:
//				// moving in +x direction
//				// +x becomes +y, +y becomes +z, +z becomes +x
//				parent.pushMatrix();
//				parent.translate(0, total_side_len_x / 2, total_side_len_y / 2);
//				parent.stroke(255, 255, 255);
//				parent.fill(0, 0, 0, 0);
//				parent.box(zf, xf, yf);
//				parent.popMatrix();
//				break;
//			case 1:
//				// moving in -x direction
//				// +x becomes +y, +y becomes +z, +z becomes -x
//				parent.pushMatrix();
//				parent.translate(0, total_side_len_x / 2, total_side_len_y / 2);
//				parent.stroke(255, 255, 255);
//				parent.fill(0, 0, 0, 0);
//				parent.box(-zf, xf, yf);
//				parent.popMatrix();
//				break;
//			case 2:
//				// moving in +y direction
//				// x stays the same, +y becomes +z, +z becomes +y
//				parent.pushMatrix();
//				parent.translate(total_side_len_x / 2, 0, total_side_len_y / 2);
//				parent.stroke(255, 255, 255);
//				parent.fill(0, 0, 0, 0);
//				parent.box(xf, zf, yf);
//				parent.popMatrix();
//				break;
//			case 3:
//				// moving in -y direction
//				// x stays the same, +y becomes +z, +z becomes -y
//				parent.pushMatrix();
//				parent.translate(total_side_len_x / 2, 0, total_side_len_y / 2);
//				parent.stroke(255, 255, 255);
//				parent.fill(0, 0, 0, 0);
//				parent.box(xf, -zf, yf);
//				parent.popMatrix();
//				break;
//			case 4:
//				// xyz coordinates stay the same
//				parent.pushMatrix();
//				parent.translate(total_side_len_x / 2, total_side_len_y / 2, 0);
//				parent.stroke(255, 255, 255);
//				parent.fill(0, 0, 0, 0);
//				parent.box(xf, yf, zf);
//				parent.popMatrix();
//				break;
//		}
		
		// draw girders
		for (int i = 0; i < num_girders; i++){
			girder[i].drawGirder(tower_orientation);
		}
		
		parent.popMatrix();
	}
  	
	/************************************ UPDATE CAM ****************************************/
	public int updateCam(Cam cam, int state, boolean[] key_pressed){
		if (state == 0) { // reset mode
			state = cam.smoothLinPursuit(init,reset_frames,0,1); // calling state, return state
		} else if (state == 2) { // roller coaster mode
			if (cam_fr_count == 0) {
				dir_mult = 5;
				rot_rad = PApplet.PI/246;
			}
			if (cam_fr_count <= reset_frames-1){
				state = cam.smoothLinPursuit(preset_cam,reset_frames,2,2);    
				cam_fr_count++;
			} else if (cam_fr_count >= reset_frames) {
				
				// shift upwards if beams are moving upwards
				if (top_level_extending > 0){
					cam.curr.loc.z = cam.curr.loc.z + beam_side_len/beam_extend_frames;
					cam.curr.sc.z = cam.curr.sc.z + beam_side_len/beam_extend_frames;
				}
				
				// rotate
				cam.sphMoveTheta(cam_center,PApplet.PI/1024,"center");
				cam_fr_count++;
				
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
				if (key_pressed[2]) { // move forward (inward)
					cam.moveForward(dir_mult);
				}
				if (key_pressed[3]) { // move backward (outward)
					cam.moveBackward(dir_mult);
				} 
				if (parent.keyPressed == true && !(key_pressed[2] || key_pressed[3] || 
						key_pressed[101] || key_pressed[114] || key_pressed[32])) {
					state = 1;
					cam_fr_count = 0;
				} // if keyPressed 
			} // frameCount
    
		} else if (state == 3) {
		}
		return state;
	}
  
	/************************************ RESET *********************************************/
	public void resetTower(){
		
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
				
		// reinitialize tower.TowerGirder objects
		for (int i = 0; i < num_girders; i++){
			girder[i].resetGirder(is_occupied,0,temp_top_level_z,num_beams_x,num_beams_y,
								  beam_side_len,beam_side_width);
			// update logical array for valid indices done in resetGirder
		}
		
		// reinitialize state variables
		is_extending_beam = false;	// will begin with pause period, and a new beam will be added to each girder
		fr_count = 0;
		dynamics_stopped = false;
		
		// reinitialize cam presets
		cam_fr_count = 0;
		cam_center = new PVector(center.x+total_side_len_x/2,center.y+total_side_len_y/2,center.z);
		preset_cam = new CamParam(new PVector(-1,0,0), 
					 new PVector(center.x+2*total_side_len_x,center.y+total_side_len_y/2,cam_center.z+500),
					 cam_center,
					 new PVector(0,0,-1));
	}
}
