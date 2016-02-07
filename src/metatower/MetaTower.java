package metatower;

import core.Cam;
import core.CamParam;
import core.Site;
import tower.Tower;

import processing.core.PApplet;
import processing.core.PVector;

/* TODO
 * make a function that forces the tower to its max height
 * figure out bug in logic update!!
 * update camera z position for 2 preset
 * fade towers
 * play with tower colors?
 * oh boy. Sit down and figure out how to combine Tower and MetaTower classes into a single interface
 * fix beam width/length weirdness in Tower; make it same as MetaTower
 */

public class MetaTower extends Site {

    // Inherits from core.Site class
    // parent, Tubeworld PApplet
    // center
    // rad_site
    // rad_inf
    // init
    // reset_frames

    // for individual tower structures
    public int num_beams_x;                 // length of x side in number of beams
    public int num_beams_y;                 // length of y side in number of beams
    public int num_beams_z;                 // length of y side in number of beams
    public float beam_side_width;           // width (~circumference) of beams
    public float beam_side_len;             // length of beam (JUST distance between nodes; different than length in tower.TowerBeam class)
    public float tower_side_len_x;          // total side length along x-direction
    public float tower_side_len_y;          // total side length along y-direction
    public float tower_side_len_z;          // total side length along y-direction
    public int num_girders;                 // number of girders within each tower object
    public float beam_extend_frames; 	    // number of frames needed for extension of beam
    public float beam_pause_frames;		    // number of frames paused during beam extensions

    // for overall metatower structure
    public int num_towers_x;                // length of x side in number of towers
    public int num_towers_y;                // length of y side in number of towers
    public int num_towers_z;                // length of z side in number of towers
    public float junction_len_x;              // length of junction boxes in x-direction
    public float junction_len_y;              // length of junction boxes in y-direction
    public float junction_len_z;              // length of junction boxes in z-direction
    public float meta_total_side_len_x;     // total side length along x-direction
    public float meta_total_side_len_y;     // total side length along y-direction
    public int num_init_towers;           // number of towers to start with; num_tot_towers will be initialized
    public int max_num_towers;            // total number of towers
    public int curr_num_towers;           // current number of towers being drawn
    public int tower_indx;                // index into tower array that points to current tower
    public tower.Tower[] tower;            // array of tower objects

    // for junctions
    public int max_num_junctions;         // maximum number of junctions to store
    public int curr_num_junctions;        // current number of junctions
    public int junction_indx;              // index into tower array that points to current junction
    public Junction[] junction;           // store junction info for drawing

    // for collision detection and dynamics
    public boolean[][][] meta_is_occupied;	// logical array for collision detection
    private float num_poss_pos;			// keep track of number of possible positions to later determine trans_probs
    private float[] trans_probs;		// 0 +x; 1 -x; 2 +y; 3 -y;
    private int[][] tower_locs;         // sketch way to keep track of tower targets in meta_is_occupied array (PVector doesn't take ints)
    private int top_level;				// top level (highest among girders) in is_occupied
    private int temp_top_level;			// temp top level; necessary so that multiple girders don't move up unnecessarily on same iteration
    private int temp_top_level_z;		// z-value of temp_top_level, for resets
    private int num_levels;				// maximum number of levels to store in meta_is_occupied
    public int update_type;				// 0 for forcing beams upward at same rate, 1 for death and respawning
    public int stopping_behavior; 		// 0 to mod everything by max_num_levels, 1 to stop updating dynamics
    private boolean dynamics_stopped;	// boolean specifying whether dynamics should be updated or not
    private float tower_pause_frames;   // number of frames to pause upon reaching junctions
    private float[] fr_count;           // frame count for controlling pauses
    private boolean[] is_finishing_tower;   // boolean indicating if tower is in final phase of updating

    // state variables
    public boolean[] is_extending_beam;       // beam should extend on this frame; if false, updating logic

    // camera updates
    public float cam_fr_count;            // frame counter
    public float dir_mult;                // speed multiplier for linear movement
    public float rot_rad;                // angle for rotational movements
    public CamParam preset_cam;            // for camera updates
    public PVector cam_center;            // coordinates for center of scene to use for updating preset_cam
    public float top_level_extending;    // frame count for camera updates

    // temp variables
    private float rand;					// random number for determining location of new beams
    private int x;						// temp x location in meta_is_occupied
    private int y;						// temp y location in meta_is_occupied
    private int z;						// temp z location in meta_is_occupied
    private int curr_level;				// temp level in is_occupied
    private float xf;					// temp x location in drawing method
    private float yf;					// temp y location in drawing method
    private float zf;					// temp z location in drawing method

    /************************************* CONSTRUCTOR ***************************************/
    public MetaTower(PApplet parent_, PVector center_, float rad_site_, float rad_inf_, CamParam init_, int reset_frames_) {

        // pass arguments to parent constructor; set Site properties
        super(parent_, center_, rad_site_, rad_inf_, init_, reset_frames_);

        // for individual tower structures
        num_beams_x = 5;                // length of x side in number of beams
        num_beams_y = 5;                // length of y side in number of beams
        num_beams_z = 10;               // length of z side in number of beams
        beam_side_width = 5;            // width (~circumference) of beams (in pixels)
        beam_side_len = 50;             // length of beam (JUST distance between nodes; different than length in tower.TowerBeam class)
        tower_side_len_x = ((float) num_beams_x)*beam_side_len;        // width (~circumference) of towers (should be multiple of beam_side_len)
        tower_side_len_y = ((float) num_beams_y)*beam_side_len;        // length of beam (JUST distance between nodes (should be multiple of beam_side_len)
        tower_side_len_z = ((float) num_beams_z)*beam_side_len;        // height of individual towers
        num_girders = 3;                // number of girders in each tower
        beam_extend_frames = 10;
        beam_pause_frames = 1;

        // for overall metatower structure
        num_towers_x = 2;            // length of x side in number of towers
        num_towers_y = 2;            // length of y side in number of towers
        num_towers_z = 5;            // length of z side in number of towers
        junction_len_x = tower_side_len_x;
        junction_len_y = tower_side_len_y;
        junction_len_z = tower_side_len_x; // arbitrary; x or y will work
        meta_total_side_len_x = ((float) num_towers_x)*(tower_side_len_z+junction_len_x)+junction_len_x;        // total side length along x-direction
        meta_total_side_len_y = ((float) num_towers_y)*(tower_side_len_z+junction_len_y)+junction_len_y;        // total side length along y-direction
        num_init_towers = 1;             // number of starting towers
        max_num_towers = 15;             // maximum number of towers
        curr_num_towers = num_init_towers;
        tower_indx = num_init_towers-1;     // index of last tower in tower queue

        // for junctions
        max_num_junctions = max_num_towers;         // maximum number of junctions to store
        curr_num_junctions = 0;        // current number of junctions
        junction_indx = -1;            // index of last junction in junction queue
        junction = new Junction[max_num_junctions]; // store junction info for drawing
        for (int i = 0; i < max_num_junctions; i++){
            junction[i] = new Junction(parent_,0,0,0,junction_len_x,junction_len_y,junction_len_z);
        }

        // initialize logical array for collision detection
        trans_probs = new float[4];
        top_level = 1;			// current "top_level" in is_occupied; starts at 1
        temp_top_level_z = 1;	// z-value of temp_top_level variable; used for resetting girders to highest level
        num_levels = 5; 		// total number of levels to keep track of; will be used in a modular manner
        stopping_behavior = 0; 	// behavior when num_towers_z is reached
        dynamics_stopped = false;
        meta_is_occupied = new boolean[num_towers_x+1][num_towers_y+1][num_levels]; // plus ones for extra node on ends
        for (int i = 0; i < num_towers_x; i++){
            for (int j = 0; j < num_towers_y; j++){
                for (int k = 0; k < num_levels; k++){
                    meta_is_occupied[i][j][k] = false;
                }
            }
        }

        // initialize tower.Tower objects
        boolean valid_indices = false;
        tower = new tower.Tower[max_num_towers];                  // array of tower objects
        tower_locs = new int[max_num_towers][4];                   // array of tower object locations; x,y,z and curr_level (z%num_levels)
        // initialize tower.Tower objects and tower_locs
        for (int i = 0; i < max_num_towers; i++){
            // initialize all tower objects, but only get starting coordinates for number of active tower objects
            if (i < num_init_towers) {
                // loop through until valid initial indices are found
                while (!valid_indices) {
                    x = (int) parent.random((float) num_towers_x);
                    y = (int) parent.random((float) num_towers_y);
                    z = 0;
                    if (!meta_is_occupied[x][y][z]) {
                        valid_indices = true;
                        // update logical array for valid indices
                        meta_is_occupied[x][y][z] = true;
                    }
                }
                tower_locs[i][0] = x;     // keep track of tower target location in meta_is_occupied
                tower_locs[i][1] = y;     // keep track of tower target location in meta_is_occupied
                tower_locs[i][2] = z+1;   // keep track of tower target location in meta_is_occupied
                tower_locs[i][3] = z+1;
                valid_indices = false;                  // reset
            } else {
                tower_locs[i][0] = 0;     // keep track of tower location in meta_is_occupied
                tower_locs[i][1] = 0;     // keep track of tower location in meta_is_occupied
                tower_locs[i][2] = 0;     // keep track of tower location in meta_is_occupied
                tower_locs[i][3] = 0;
            }
            /* initialize new tower.Tower objects
             * center (second argument) is really x=0,y=0,z=0 corner of a tower object
             * x/y values are x/y times (height+width) of towers to take junctions into account
             */
            tower[i] = new Tower(parent_,new PVector(((float) x)*(tower_side_len_z+tower_side_len_x),((float) y)*(tower_side_len_z+tower_side_len_y),0),
                                                     5000,1000,init,120);
            // set necessary properties
            tower[i].num_beams_x = num_beams_x;
            tower[i].num_beams_y = num_beams_y;
            tower[i].beam_side_width = beam_side_width; // (in pixels)
            tower[i].beam_side_len = beam_side_len;  // (in pixels)
            tower[i].total_side_len_x = tower_side_len_x;
            tower[i].total_side_len_y = tower_side_len_y;
            tower[i].num_girders = num_girders;
            tower[i].tower_orientation = 4; // start off moving in +z direction
            tower[i].beam_extend_frames = beam_extend_frames;
            tower[i].beam_pause_frames = beam_pause_frames;
            tower[i].update_type = 0; 		// 0 for forcing beams upward at same rate, 1 for death and respawning, which needs more num_levels
            tower[i].num_beams_z = num_beams_z; 	// total number of levels to draw
            tower[i].stopping_behavior = 1; 	// behavior when max_num_levels is reached; 0 for reset, 1 for stop
        }

        // other variables for tracking dynamics
        tower_pause_frames = beam_extend_frames + beam_pause_frames;
        fr_count = new float[max_num_towers];           // frame count for controlling pauses
        is_finishing_tower = new boolean[max_num_towers];   // boolean indicating if tower is in final phase of updating
        for (int i = 0; i < max_num_towers; i++){
            fr_count[i] = 0;
            is_finishing_tower[i] = false;
        }

        // initialize camera update variables
        cam_fr_count = 0;
        // core.CamParam object
		/*
		core.CamParam preset_cam;     // initial camera dir,loc,sc and down for camera presets
	    PVector dir;             // xyz coordinates of direction vector
	    PVector loc;             // xyz coordinates of camera
	    PVector sc;              // xyz coordinates of scene center
	    PVector down;            // xyz coordinates of downward direction of camera
	    */
        // reset parts of init to be at the center, given the number of towers
        init.loc = new PVector(center.x+meta_total_side_len_x/2,center.y+meta_total_side_len_y/2,center.z+4000);
        init.sc  = new PVector(center.x+meta_total_side_len_x/2,center.y+meta_total_side_len_y/2,center.z);
        init.dir = new PVector(0,0,-1);
        init.down = new PVector(0,-1,0);
        cam_center = new PVector(center.x+meta_total_side_len_x/2,center.y+meta_total_side_len_x/2,center.z);
        preset_cam = new CamParam(new PVector(-1,0,0),
                new PVector(center.x+2*meta_total_side_len_x,center.y+meta_total_side_len_y/2,center.z+1000),
                new PVector(center.x+meta_total_side_len_x/2,center.y+meta_total_side_len_y/2,center.z),
                new PVector(0,0,-1));
    }

    /************************************ UPDATE PHYSICS ************************************/
    public void updatePhysics(boolean [] keys_pressed, boolean[] keys_toggled){

        // to see if updates should be paused
        if (!keys_toggled[32]){
            // not paused; check current phase of updating for each tower - extending or paused at node
            for (int i = 0; i < curr_num_towers; i++) {
                // if dynamics (updating) is not stopped for tower, continue to update
                if (!tower[i].dynamics_stopped){
                    tower[i].updatePhysics(keys_pressed, keys_toggled);
                    if (tower[i].dynamics_stopped){
                        // newly stopped; there will be a final updating of beams to track
                        fr_count[i] = 0;
                        is_finishing_tower[i] = true;
                    }
                } else if (is_finishing_tower[i]) {
                    // tower has moved into final update mode; keep track of number of frames
                    if (fr_count[i] < beam_extend_frames) {
                        // update towers
                        tower[i].updatePhysics(keys_pressed, keys_toggled);
                        fr_count[i]++;                    // update frame count
                        if (fr_count[i] == beam_extend_frames) {
                            // at end of final update; add new junction, add new tower and flag event
                            fr_count[i] = 0;                // reset frame count for pause phase
                            is_finishing_tower[i] = false;  // set flag to false
                            addJunction(i);                 // add new junction if possible; new tower added at end of pause
                        }
                        // shift camera center up if a beam on uppermost level is extending
                        if (top_level_extending > 0) {
                            cam_center.z = cam_center.z + beam_side_len / beam_extend_frames;
                            preset_cam.loc.z = preset_cam.loc.z + beam_side_len / beam_extend_frames;
                            preset_cam.sc.z = preset_cam.sc.z + beam_side_len / beam_extend_frames;
                            top_level_extending--;
                        }
                    } // end frame_count check
                } else if (!dynamics_stopped && fr_count[i] != tower_pause_frames) {
                    // in pause mode between extensions; if last of these frames, perform next logic update
                    fr_count[i]++;                            // update frame count
                    if (fr_count[i] == tower_pause_frames) {
                        // at end of paused phase
                        addTower(i);                 // tell addTower where to look in meta_is_occupied
                        // fr_count[i] is now stuck at tower_pause_frames; no more evals for this tower
                    } // end frame_count check
                } // end phase check
            } //end tower loop
        } // end paused check
        if (keys_pressed[8]){
            resetMetaTower();
        }
    }

    /************************************ ADD BEAM ******************************************/
    public void addTower(int i) {
        // find next spot to move to having just come from tower i
		/* temp_top_level allows towers updated after another tower was forced upwards on this iteration
		 * to continue updating on the level below; updating the top_level after all girder updates will
		 * force the upwards move of remaining girders on next iteration
		 */
        temp_top_level = top_level;
        // check for height limit
        if (temp_top_level_z == num_towers_z){
            if (stopping_behavior == 0){
                // continue tower; just reset for now; need to fix camera presets and such
                resetMetaTower();
                return;
            } else if (stopping_behavior == 1){
                // freeze metatower
                dynamics_stopped = true;
                // this needs to be fixed
            }
        } else {
            // fill out temp variables for ease of interpretation
            x = tower_locs[i][0];
            y = tower_locs[i][1];
            z = tower_locs[i][2];
            curr_level = tower_locs[i][3];

            if (update_type == 0) {
                // check to see if the current girder needs to be forced upwards
                if (curr_level != top_level) {
                    // should be forcing up here; lazy for now
                    // forceToTop or something
                    findVacantSpot(i);
                } else {
                    // randomly select new position to move to in same level
                    findVacantSpot(i);                        // i just indexes which tower we'll be updating
                }

            } else if (update_type == 1) {

                /* if current level is right above temp_top_level, this means the top of meta_is_occupied is looping back around
                 * and about to catch this girder. To fix this situation we'll force the current girder upwards and then
                 * clear that level of is_occupied to make room for the beams in top_level that will soon be moving up
                 * again
                 * This handles case when current beam is forced up from below in is_occupied
                 * case where current beam forces up beams above is handled in findVacantSpot
                 */
                if (curr_level == (temp_top_level + 1) % num_levels) {
                    curr_level = (curr_level + 1) % num_levels;    // reassign curr_level

                    // force upwards; this deals with case when another tower has moved up first, and this girder has
                    // respond
                    if (meta_is_occupied[x][y][curr_level]) {
                        /* tower can't move upwards - needs to reset :(
                         * Note that meta_is_occupied is not updated to reflect the beam leaving this level - this will be
                         * handled when a beam from the top level moves into this level, i.e. a complete reset of values
                         */
                        reinitializeTower(meta_is_occupied, temp_top_level, temp_top_level_z, num_towers_x, num_towers_y, i);
                        // update of meta_is_occupied done in resetGirder
                        findVacantSpot(i);
                    } else {
                        // girder can move upwards
                        initializeTower(x,y,z,4);				// add new tower
                        // update target position of next tower; initTower should update tower_indx and num_curr_towers
                        tower_locs[tower_indx][0] = x;
                        tower_locs[tower_indx][1] = y;
                        tower_locs[tower_indx][2] = z+1;
                        tower_locs[tower_indx][3] = curr_level;
                        meta_is_occupied[x][y][curr_level] = true;    // update collision array
                    }
                } else {
                    // girder is free to update normally
                    findVacantSpot(i);
                }

            } // end update_type check
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

        // fill out temp variables for ease of interpretation; x,y,z and curr_level represent TARGET of tower i
        x = tower_locs[i][0];
        y = tower_locs[i][1];
        z = tower_locs[i][2];
        curr_level = tower_locs[i][3];

        // initialize relevant variables; trans_probs entries should add to 4
        num_poss_pos = 4;
        trans_probs[0] = 1; // +x
        trans_probs[1] = 1; // -x
        trans_probs[2] = 1; // +y
        trans_probs[3] = 1; // -y

        // check for boundaries and already occupied nodes
        if ((x == num_towers_x) || meta_is_occupied[x+1][y][curr_level]){
            trans_probs[0] = 0;	// don't increase x value
            num_poss_pos--;		// decrement
        }
        if ((x == 0) || meta_is_occupied[x-1][y][curr_level]){
            trans_probs[1] = 0; // don't decrease x value
            num_poss_pos--;		// decrement
        }
        if ((y == num_towers_y) || meta_is_occupied[x][y+1][curr_level]){
            trans_probs[2] = 0; // don't increase y value
            num_poss_pos--;		// decrement
        }
        if ((y == 0) || meta_is_occupied[x][y-1][curr_level]){
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
                initializeTower(x,y,z,4);				// add new beam to girder
                // update target position of next tower; initTower should update tower_indx and num_curr_towers if necessary
                tower_locs[tower_indx][0] = x;
                tower_locs[tower_indx][1] = y;
                tower_locs[tower_indx][2] = z+1;
                tower_locs[tower_indx][3] = curr_level;

                // one level above temp_top_level, first girder on this level
                temp_top_level = curr_level;
                temp_top_level_z++;
                // reset is_occupied at this level
                for (int j = 0; j < num_towers_x; j++) {
                    for (int k = 0; k < num_towers_y; k++) {
                        meta_is_occupied[j][k][curr_level] = false;
                    }
                }
                meta_is_occupied[x][y][curr_level] = true;	// update collision array
            } else if (meta_is_occupied[x][y][curr_level]){
                // girder CAN'T move upwards, and we're not on top layer - needs to reinitialize :(
                reinitializeTower(meta_is_occupied,temp_top_level,temp_top_level_z,
                        num_towers_x,num_towers_y,i); // will call initializeTower once it finds a spot in temp_top_level
                // update meta_is_occupied done in reinitializeTower
                // call function recursively; location of i is redefined in reinitializeTower
                findVacantSpot(i);
            } else {
                // [x][y][curr_level] is NOT occupied, and we're not moving into an old logic layer; move upwards
                initializeTower(x,y,z,4);				// add new beam to girder
                // update current position of next tower
                tower_locs[tower_indx][0] = x;
                tower_locs[tower_indx][1] = y;
                tower_locs[tower_indx][2] = z+1;
                tower_locs[tower_indx][3] = curr_level;
                meta_is_occupied[x][y][curr_level] = true;	// update collision array
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
                initializeTower(x,y,z,0);
                tower_locs[tower_indx][0] = x+1;
                tower_locs[tower_indx][1] = y;
                tower_locs[tower_indx][2] = z;
                tower_locs[tower_indx][3] = curr_level;
                meta_is_occupied[x+1][y][curr_level] = true;	// update collision array
            } else if (rand < trans_probs[0]+trans_probs[1]){
                initializeTower(x,y,z,1);
                tower_locs[tower_indx][0] = x-1;
                tower_locs[tower_indx][1] = y;
                tower_locs[tower_indx][2] = z;
                tower_locs[tower_indx][3] = curr_level;
                meta_is_occupied[x-1][y][curr_level] = true;	// update collision array
            } else if (rand < trans_probs[0]+trans_probs[1]+trans_probs[2]){
                initializeTower(x,y,z,2);
                tower_locs[tower_indx][0] = x;
                tower_locs[tower_indx][1] = y+1;
                tower_locs[tower_indx][2] = z;
                tower_locs[tower_indx][3] = curr_level;
                meta_is_occupied[x][y+1][curr_level] = true;	// update collision array
            } else {
                initializeTower(x,y,z,3);
                tower_locs[tower_indx][0] = x;
                tower_locs[tower_indx][1] = y-1;
                tower_locs[tower_indx][2] = z;
                tower_locs[tower_indx][3] = curr_level;
                meta_is_occupied[x][y-1][curr_level] = true;	// update collision array
            }
        }
    }

    /************************************ ADD BEAM HELPER ***********************************/
    public void initializeTower(int x, int y, int z, int orientation){

        // add tower to end of tower array queue
        tower_indx = (tower_indx+1) % max_num_towers;
        // update number of towers
        if (curr_num_towers < max_num_towers){
            curr_num_towers++;
        } else {
            // we're moving back through the tower array; need to reset tower at that index
            tower[tower_indx].resetTower();
        }

        // reset necessary properties
        tower[tower_indx].tower_orientation = orientation; // start off moving in +z direction
        tower[tower_indx].center = new PVector(x*(tower_side_len_z+tower_side_len_x),
                                               y*(tower_side_len_z+tower_side_len_y),
                                               z*(tower_side_len_z+tower_side_len_x)-tower_side_len_x);

        // add additional offsets as dictated by orientation
        switch(orientation){
            case 0:
                // +x direction
                tower[tower_indx].center.x = tower[tower_indx].center.x+tower_side_len_x;
                break;
            case 1:
                // -x direction
                // nothing
                break;
            case 2:
                // +y direction
                tower[tower_indx].center.y = tower[tower_indx].center.y+tower_side_len_y;
                break;
            case 3:
                // -y direction
                // nothing
                break;
            case 4:
                // +z direction
                tower[tower_indx].center.z = tower[tower_indx].center.z+tower_side_len_x;
                break;
        }

    }

    /************************************ ADD BEAM HELPER ***********************************/
    public void reinitializeTower(boolean[][][] meta_is_occupied, int temp_top_level, int temp_top_level_z,
                                  int num_towers_x, int num_towers_y, int indx){

        // function will call initializeTower once it finds a spot in temp_top_level
        boolean valid_indices = false;
        // loop through until valid initial indices are found
        while (!valid_indices) {
            x = (int) parent.random((float) num_towers_x);
            y = (int) parent.random((float) num_towers_y);
            z = temp_top_level;
            if (!meta_is_occupied[x][y][z]) {
                valid_indices = true;
                // update logical array for valid indices
                meta_is_occupied[x][y][z] = true;
            }
        }
        tower_locs[indx][0] = x;     // keep track of tower target location in meta_is_occupied
        tower_locs[indx][1] = y;     // keep track of tower target location in meta_is_occupied
        tower_locs[indx][2] = temp_top_level_z;   // keep track of tower target location in meta_is_occupied
        tower_locs[indx][3] = temp_top_level;
    }

    /************************************ ADD JUNCTION **************************************/
    public void addJunction(int indx){
        // get location of junction from indx
        junction_indx = (junction_indx+1) % max_num_junctions;
        // update number of junctions
        if (curr_num_junctions < max_num_junctions){
            curr_num_junctions++;
        }

        // set necessary properties; subtract tower_side_len_z/2 because bottom nodes are flat
        junction[junction_indx].x = ((float) tower_locs[indx][0])*(tower_side_len_z+tower_side_len_x)+tower_side_len_x/2;
        junction[junction_indx].y = ((float) tower_locs[indx][1])*(tower_side_len_z+tower_side_len_y)+tower_side_len_y/2;
        junction[junction_indx].z = ((float) tower_locs[indx][2])*(tower_side_len_z+tower_side_len_x)-tower_side_len_x/2;

    }

    /************************************ DRAW SITE *****************************************/
    public void drawSite(){

        parent.pushMatrix();
        parent.translate(center.x, center.y, center.z);

        // draw bounding box for initial towers; take tower orientation into account
        xf = tower_side_len_x + 2 * beam_side_len;  // temp variable to store dim of bounding box
        yf = tower_side_len_y + 2 * beam_side_len;  // temp variable to store dim of bounding box
        zf = 0;                                     // temp variable to store dim of bounding box
        for (int i = 0; i < num_towers_x+1; i++) {
            for (int j = 0; j < num_towers_y+1; j++) {

                parent.pushMatrix();
                parent.translate(((float) i)*(tower_side_len_z+tower_side_len_x)+tower_side_len_x/2,
                                 ((float) j)*(tower_side_len_z+tower_side_len_y)+tower_side_len_y/2,0);
                switch (4) {
                    case 0:
                        // moving in +x direction
                        // +x becomes +y, +y becomes +z, +z becomes +x
                        parent.pushMatrix();
                        parent.translate(0, tower_side_len_x / 2, tower_side_len_y / 2);
                        parent.stroke(255, 255, 255);
                        parent.fill(0, 0, 0, 0);
                        parent.box(zf, xf, yf);
                        parent.popMatrix();
                        break;
                    case 1:
                        // moving in -x direction
                        // +x becomes +y, +y becomes +z, +z becomes -x
                        parent.pushMatrix();
                        parent.translate(0, tower_side_len_x / 2, tower_side_len_y / 2);
                        parent.stroke(255, 255, 255);
                        parent.fill(0, 0, 0, 0);
                        parent.box(-zf, xf, yf);
                        parent.popMatrix();
                        break;
                    case 2:
                        // moving in +y direction
                        // x stays the same, +y becomes +z, +z becomes +y
                        parent.pushMatrix();
                        parent.translate(tower_side_len_x / 2, 0, tower_side_len_y / 2);
                        parent.stroke(255, 255, 255);
                        parent.fill(0, 0, 0, 0);
                        parent.box(xf, zf, yf);
                        parent.popMatrix();
                        break;
                    case 3:
                        // moving in -y direction
                        // x stays the same, +y becomes +z, +z becomes -y
                        parent.pushMatrix();
                        parent.translate(tower_side_len_x / 2, 0, tower_side_len_y / 2);
                        parent.stroke(255, 255, 255);
                        parent.fill(0, 0, 0, 0);
                        parent.box(xf, -zf, yf);
                        parent.popMatrix();
                        break;
                    case 4:
                        // xyz coordinates stay the same
//                        parent.pushMatrix();
//                        parent.translate(tower_side_len_x / 2, tower_side_len_y / 2, 0);
                        parent.stroke(255, 255, 255);
                        parent.fill(0, 0, 0, 0);
                        parent.box(xf, yf, zf);
//                        parent.popMatrix();
                        break;
                } // switch
                parent.popMatrix();
            } // end draw bounding box j
        } // end draw bounding box i

        // draw junctions
        for (int i = 0; i < curr_num_junctions; i++){
            junction[i].drawJunction();
        }
        // draw towers
        for (int i = 0; i < curr_num_towers; i++){
            tower[i].drawSite();
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
    public void resetMetaTower(){

        // reinitialize logical array for collision detection
        top_level = 0;	// current "top_level" in is_occupied
        temp_top_level_z = 0;
        for (int i = 0; i < num_towers_x; i++){
            for (int j = 0; j < num_towers_y; j++){
                for (int k = 0; k < num_levels; k++){
                    meta_is_occupied[i][j][k] = false;
                }
            }
        }

        // reinitialize tower.Tower objects
        boolean valid_indices = false;
        for (int i = 0; i < max_num_towers; i++){
            // initialize all tower objects, but only get starting coordinates for number of active tower objects
            if (i < num_init_towers) {
                // loop through until valid initial indices are found
                while (!valid_indices) {
                    x = (int) parent.random((float) num_towers_x);
                    y = (int) parent.random((float) num_towers_y);
                    z = (int) 0;
                    if (!meta_is_occupied[x][y][z]) {
                        valid_indices = true;
                        // update logical array for valid indices
                        meta_is_occupied[x][y][z] = true;
                    }
                }
                valid_indices = false;    // reset
            } else {
                x = 0; y = 0; z = 0;      // assign dummy locations to unrendered tower objects
            }
            // reset necessary properties
            tower[i].tower_orientation = 4; // start off moving in +z direction
        }

        // reinitialize counters
        curr_num_towers = num_init_towers;
        tower_indx = num_init_towers-1;
        curr_num_junctions = 0;
        junction_indx = -1;

        // reinitialize state variables
        //is_extending_beam = false;	// will begin with pause period, and a new beam will be added to each girder
        cam_fr_count = 0;

        // reinitialize cam presets
        cam_center = new PVector(center.x+meta_total_side_len_x/2,center.y+meta_total_side_len_x/2,center.z);
        preset_cam = new CamParam(new PVector(-1,0,0),
                new PVector(center.x+2*meta_total_side_len_x,center.y+meta_total_side_len_y/2,center.z+500),
                new PVector(center.x+meta_total_side_len_x/2,center.y+meta_total_side_len_y/2,center.z),
                new PVector(0,0,-1));
    }
}
