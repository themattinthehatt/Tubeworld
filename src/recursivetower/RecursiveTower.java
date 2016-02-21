/*
 The RecursiveTower class creates a structure with nodes located on a rectangular 3D lattice, connected
 by links. Initially there are no links, and the placement of the links follows a random walk around
 the lattice with the constraint that a node can only be visited once, and that the link updates
 occur within a single level of the lattice. When the current link reaches a node from where it cannot
 move anywhere else in the lattice at the current level, it moves upwards to the next level and continues
 the random walk.

 The RecursiveTower class extends the Site abstract class, and as such can be treated like any of the other
 sites in Tubeworld.

 The RecursiveTower class also implements the TowerLink interface, which allows the tower itself to be a
 link in a larger tower structure. Recursion of the tower structure is performed outside of the
 RecursiveTower class itself, inside the Tubeworld class.

 0th order recursion Tower example, where links in the top RecursiveTower object are taken from the
 TowerBeam class:

 1st order recursion Tower example, where links in the top RecursiveTower object are taken from the
 RecursiveTower class, and the links in those objects are taken from the TowerBeam class:
 */

/* TODO
1. have junctions and inherit color from links; have links inherit color from junctions; need color property to be float[][],
   so it can hold colors of all constituent links, and these colors can be passed on if desired. The color passed on to the
   junction, again if desired, is [0][:]; this should happen in setLinkProperties
2. fade colors
3. interpolate colors
 */

package recursivetower;

import core.Cam;
import core.CamParam;
import core.Site;

import processing.core.PApplet;
import processing.core.PVector;

public class RecursiveTower extends Site implements TowerLink {

    // Inherits from core.Site class
    // PApplet parent (Tubeworld object)
    // PVector origin                       // origin of site
    // float render_radius                  // site will be rendered when camera is within this distance from Site origin
    // CamParam init                        // initial camera dir,loc,sc and down for camera presets
    // float reset_frames                   // number of frames for resetting camera loc when inside render_radius

    // link properties
    public int num_links_x;                 // length of x side in number of links
    public int num_links_y;                 // length of y side in number of links
    public int num_links_z;                 // length of y side in number of links
    public int num_init_links;              // number of links to start with; max_num_links will be initialized
    public int num_max_links;               // total number of links
    public int curr_num_links;              // current number of links being drawn
    public int links_indx;                  // index into links array that points to current links
    public TowerLink[] links;               // array of link objects

    // other tower properties
    public float link_len_x;                // side length of individual beams along x-direction
    public float link_len_y;                // side length of individual beams along y-direction
    public float link_len_z;                // side length of individual beams along z-direction
    public float side_len_x;                // total side length along x-direction
    public float side_len_y;                // total side length along y-direction
    public float side_len_z;                // total side length along y-direction
    public int orientation;                 // orientation of tower; 0/1 along +/-x dir, 2/3 along +/-y dir, 4 along +z dir
    public int parent_orientation;          // orientation of parent tower; defaults to 4 if none

    // color properties - all color properties are in terms of hue, saturation and value
    public float[] init_color;              // initial color of tower; for drawing bounding boxes
    public float[] color;                   // color of links in tower; used to denote color of first link that reaches max height
    public int[] rgb;                       // for storing rgb info
    public String color_scheme;             // "rgb", "hsv_random", "custom"
    public boolean fade_colors;             // true to fade
    public boolean interpolate_colors;      // true to move through hue space with hsv; false to stay at initial color
    public float hue;                       // in [0 1]
    public float saturation;                // in [0 1]
    public float value;                     // in [0 1]

    // junction properties
    public int num_init_junctions;          // number of junctions to start with; max_num_junctions will be initialized
    public int num_max_junctions;           // maximum number of junctions to store
    public int curr_num_junctions;          // current number of junctions
    public int junctions_indx;              // index into tower array that points to current junction
    public Junction[] junctions;            // store junction info for drawing

    // properties for collision detection
    private int num_levels;				    // maximum number of levels to store in is_occupied
    public boolean[][][] is_occupied;	    // logical array for collision detection
    private float num_poss_pos;			    // keep track of number of possible positions to later determine trans_probs
    private float[] trans_probs;		    // transition probabilities within current level; 0 +x; 1 -x; 2 +y; 3 -y;
    private int[][] link_locs;              // sketchy way to keep track of link target locations in is_occupied array (PVector doesn't take ints)
                                            // for each link, this records the location in is_occupied that the link is heading towards (its target)

    // properties for dynamics of links
    private int top_level;				    // top level (highest among links) in is_occupied
    private int temp_top_level;			    // temp top level; necessary so that multiple links don't get forced up unnecessarily on same iteration
    private int temp_top_level_z;		    // z-value of temp_top_level, for reinitializing link locations to current top level
    public int update_type;				    // 0 for forcing beams upward at same rate, 1 for allowing asynchronous upward movement and respawning
    public int stopping_behavior; 		    // 0 to mod everything by max_num_levels, 1 to stop updating dynamics at num_links_z
    private boolean dynamics_stopped;	    // boolean specifying whether dynamics should be updated or not
    private float junction_pause_frames;    // number of frames to pause upon reaching junctions
    private float[] fr_count;               // frame count for controlling pauses
    private float links_stopped_counter;    // for determining when all links have been drawn

    // properties for camera updates
    public float cam_fr_count;              // frame counter
    public float dir_mult;                  // speed multiplier for linear movement
    public float rot_rad;                   // angle for rotational movements
    public PVector cam_center;              // coordinates for center of scene to use for updating preset2_cam_pos; moves upwards with tower
    public CamParam preset2_cam_pos;        // location for camera to move to upong hitting preset 2 for camera updates
    public float top_level_extend_frames;   // current frame count for camera updates; fraction of max_top_level_extend_frames
    public float max_top_level_extend_frames;// frame count for camera updates

    // temp variables
    private float rand;					    // random number for determining location of new links
    private int x;						    // temp x location in is_occupied
    private int y;						    // temp y location in is_occupied
    private int z;						    // temp z location in is_occupied
    private int curr_level;				    // temp level in is_occupied
    private float xf;					    // temp x location in drawing method
    private float yf;					    // temp y location in drawing method
    private float zf;					    // temp z location in drawing method


    /************************************ CONSTRUCTOR ***************************************/
    public RecursiveTower(PApplet parent_, PVector origin_, float render_radius_, CamParam init_, float reset_frames_) {
        // pass arguments to parent constructor (Site constructor); sets Site properties
        super(parent_, origin_, render_radius_, init_, reset_frames_);

        // link properties
        // good numbers for one level of recursion
        num_links_x = 4;                        // length of x side in number of links
        num_links_y = 4;                        // length of y side in number of links
        num_links_z = 4;                        // length of z side in number of links
        num_init_links = 3;                     // number of links to start with; num_max_links will be initialized
        num_max_links = 64;                     // total number of links
        // set update_type to 1, stopping_behavior to 1

        // good numbers for single tower
//        num_links_x = 6;                        // length of x side in number of links
//        num_links_y = 6;                        // length of y side in number of links
//        num_links_z = 100;                      // length of z side in number of links
//        num_init_links = 3;                     // number of links to start with; num_max_links will be initialized
//        num_max_links = 256;                    // total number of links
        // set update_type to 0


        curr_num_links = num_init_links;        // current number of links being drawn
        links_indx = num_init_links-1;          // index into links array that points to current links
        links = new TowerLink[num_max_links];   // array of link objects; initialize below

        // other tower properties
        link_len_x = 5;                         // side length of individual beams along x-direction
        link_len_y = 5;                         // side length of individual beams along y-direction
        link_len_z = 10*link_len_x;             // side length of individual beams along z-direction; should be a multiple of
                                                // side_len_x, so that if the link is itself a tower there will be an integer
                                                // number of beams in the x-, y- and z-directions; if the link is a TowerBeam
                                                // object then this isn't as important
        side_len_x = ((float) num_links_x)*(link_len_z + link_len_x) + link_len_x;  // total side length along x-direction
        side_len_y = ((float) num_links_y)*(link_len_z + link_len_y) + link_len_y;  // total side length along y-direction
        side_len_z = ((float) num_links_z)*(link_len_z + link_len_x) - link_len_x;  // total side length along z-direction
        orientation = 4;                        // orientation of tower; 0/1 along +/-x dir, 2/3 along +/-y dir, 4 along +z dir
        parent_orientation = 4;                 // orientation of parent tower; defaults to 4 if none

        // color properties
        init_color = new float[3];              // default init color to white
        init_color[0] = 0; init_color[1] = 0; init_color[2] = 1;
        color = new float[3];                   // default color to white
        color[0] = 0; color[1] = 0; color[2] = 1;
        color_scheme = "hsv";                   // "rgb", "hsv", "custom"
        fade_colors = true;                     // true to fade
        interpolate_colors = true;              // true to move through hue space with hsv; false to stay at initial color

        // junction properties
        num_init_junctions = 0;                 // number of junctions to start with; num_max_junctions will be initialized
        num_max_junctions = num_max_links;      // maximum number of junctions to store
        curr_num_junctions = 0;                 // current number of junctions
        junctions_indx = -1;                    // index into tower array that points to current junction
        junctions = new Junction[num_max_junctions]; // store junction info for drawing; initialize below

        // properties for collision detection
        num_levels = 3;     				    // maximum number of levels to store in is_occupied
        is_occupied = new boolean[num_links_x+1][num_links_y+1][num_links_z+1];   // logical array for collision detection;
                                                // plus 1s for extra node on ends; initialize below
        trans_probs = new float[4];
        link_locs = new int[num_max_links][4];  // sketchy way to keep track of link locations in is_occupied array (PVector doesn't take ints)
                                                // each link has an associated x,y,z and curr_level (z % num_levels) which gives 4 for 2nd dim

        // properties for dynamics of links
        top_level = 1;      				    // top level (highest among links) in is_occupied; links initialize on level 0 heading upwards
        temp_top_level_z = 1;       		    // z-value of temp_top_level, for reinitializing link locations to current top level
        update_type = 0;    				    // 0 for forcing beams upward at same rate, 1 for allowing asynchronous upward movement and respawning
        stopping_behavior = 1;       		    // 0 to mod everything by max_num_levels, 1 to stop updating dynamics at num_links_z
        dynamics_stopped = false;         	    // boolean specifying whether dynamics should be updated or not
        junction_pause_frames = 1;              // number of frames to pause upon reaching junctions
        fr_count = new float[num_max_links];    // frame count for controlling pauses during junctions; 1 for each link
        max_top_level_extend_frames = 60;       //beam_extend_frames*num_beams_z;
        links_stopped_counter = 0;              // for determining when all links have been drawn

        // properties for camera updates
        cam_fr_count = 0;                       // frame counter
        // core.CamParam object
		/*
		core.CamParam preset2_cam_pos;  // initial camera dir,loc,sc and down for camera presets
	    PVector dir;                    // xyz coordinates of direction vector
	    PVector loc;                    // xyz coordinates of camera
	    PVector sc;                     // xyz coordinates of scene center
	    PVector down;                   // xyz coordinates of downward direction of camera
	    */
        // cam center will be at the center of the tower and follow the movement upwards
        cam_center = new PVector(origin.x+side_len_x/2,origin.y+side_len_y/2,origin.z);
        // preset_cam_pos will be outside the tower and slightly above the current level, looking inwards
        preset2_cam_pos = new CamParam(new PVector(-1,0,0),
                new PVector(origin.x+2*side_len_x,origin.y+side_len_y/2,origin.z+400),
                new PVector(origin.x+side_len_x/2,origin.y+side_len_y/2,origin.z),
                new PVector(0,0,-1));
        // reset init to be at the center of the tower, given the number of links; init will be looking down at the initial tower,
        // and this is where the camera will return upon resetting the camera position (key 0)
        init.loc = new PVector(origin.x+side_len_x/2,origin.y+side_len_y/2,origin.z+1000);
        init.sc  = new PVector(origin.x+side_len_x/2,origin.y+side_len_y/2,origin.z);
        init.dir = new PVector(0,0,-1);
        init.down = new PVector(0,-1,0);



        // INITIALIZE VARIOUS ARRAYS
        // junction array
        for (int i = 0; i < num_max_junctions; i++){
            junctions[i] = new Junction(parent_, 0, 0, 0, link_len_x, link_len_y, link_len_x,
                                    new float[] {0,0,1}, new float[] {0,0,1}, orientation);
        }

        // is_occupied array
        for (int i = 0; i < num_links_x+1; i++){
            for (int j = 0; j < num_links_y+1; j++){
                for (int k = 0; k < num_levels; k++){
                    is_occupied[i][j][k] = false;
                }
            }
        }

        // fr_count arrays
        for (int i = 0; i < num_max_links; i++){
            fr_count[i] = 0;
        }

        // initialize TowerLink objects and link_locs
        boolean valid_indices = false;                  // local variable
        curr_level = 1;                                 // for initializeNewLink
        // loop through all links to initialize
        for (int i = 0; i < num_max_links; i++){
            // initialize all link objects, but only get starting coordinates for initial link objects
            if (i < num_init_links) {
                // loop through until valid initial indices are found
                while (!valid_indices) {
                    x = (int) parent.random((float) num_links_x+1);
                    y = (int) parent.random((float) num_links_y+1);
                    z = 0;
                    if (!is_occupied[x][y][z]) {
                        valid_indices = true;
                        // update logical array for valid indices; both the base location (at z = 0) and the destination
                        // location (at z = 1) should be marked as occupied
                        is_occupied[x][y][z] = true;
                        is_occupied[x][y][z+1] = true;
                        // update link_locs
                        link_locs[i][0] = x;     // keep track of link location in is_occupied
                        link_locs[i][1] = y;     // keep track of link location in is_occupied
                        link_locs[i][2] = z+1;   // keep track of link location in is_occupied
                        link_locs[i][3] = z+1;   // current level is initially same as z-value
                    }
                }
                valid_indices = false;   // reset valid_indices for next initial link
            } else {
                // update link_locs
                link_locs[i][0] = 0;     // keep track of link location in is_occupied
                link_locs[i][1] = 0;     // keep track of link location in is_occupied
                link_locs[i][2] = 0;     // keep track of link location in is_occupied
                link_locs[i][3] = 0;     // current level is initially same as z-value
            }

            /* initialize new TowerLink objects
             * the default is to use TowerBeam objects, so that an infinite recursion doesn't start. To make various levels
             * of recursion, after initializing the first RecursiveTower object call the RecursiveTower.reinitializeLinkObject
             * to reset the link object to a RecursiveTower object (or another object for that matter, but no other options
             * are currently supported.
             * x/y values are x/y times (height+width) of links to take junctions into account; location points to origin,
             * rather than center, of beam; correction is accounted for during drawSite (this is so Beam and Tower links
             * can be treated similarly)
             */
            // side_width, side_len, orientation, loc, color; get location from link_locs
            links[i] = new TowerBeam(parent_,link_len_x,link_len_z,4,
                                    new PVector(((float) link_locs[i][0])*(link_len_z+link_len_x),
                                                ((float) link_locs[i][1])*(link_len_z+link_len_y),
                                                0),
                                    RecursiveTower.assignInitColor(parent,i,color_scheme),orientation);
        }
    }





    /**================================== METHODS FOR SITE CLASS ============================/

    /************************************ UPDATE PHYSICS ************************************/
    public void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled){
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    updatePhysics method loops through all current links and either updates them, adds a new
    junction if necessary or adds a new link if necessary. Entire reset of tower is also called
    from here.

    AS A METHOD IN THE TowerLink INTERFACE:
    updatePhysics method updates the growth of the link; this will no longer
    be called once the link is fully formed

    Calls: addLink
           addJunction
           TowerLink.updatePhysics
    Called by: Tubeworld.draw
     */

        // to see if updates should be paused
        if (!keys_toggled[32]){

            // Tubeworld dynamics not paused; check current phase of updating for each link - extending or paused at node
            for (int i = 0; i < curr_num_links; i++) {
                // if dynamics (updating) is not stopped for link, continue to update
                if (!links[i].getDynamicsStopped()){
                    links[i].updatePhysics(keys_pressed, keys_toggled);
                    if (links[i].getDynamicsStopped()){
                        // newly stopped; move to pause mode
                        fr_count[i] = 0;
                    }
                } else if (!dynamics_stopped && fr_count[i] != junction_pause_frames) {
                    /* in pause mode at junction
                       if first of these frames, add a junction - as long as the links are not at the top of the tower
                       if last of these frames, add a link and perform next logic update
                      */
                    if (fr_count[i] == 0) {
                        // check for height limit
                        if (link_locs[i][2] == num_links_z){ // let links extend to level specified by 'num_links_z'
                            // link in indx 'i' is at very top; we don't want to add another junction or another link
                            if (stopping_behavior == 0){
                                // continue tower; just reset for now; need to fix camera presets and such
                                reset();
                                return;
                            } else if (stopping_behavior == 1){
                                // update counter of stopped links
                                links_stopped_counter++;
                                if (links_stopped_counter == 1) {
                                    color = links[i].getColor();    // update color of tower to reflect winning beam
                                }
                                fr_count[i] = junction_pause_frames; // ensures new link is never added
                            }
                        } else {
                            addJunction(i);
                        }
                    }
                    if (fr_count[i] == junction_pause_frames-1) {
                        // at end of paused phase
                        addLink(i);                 // tell addLink where to begin search in is_occupied
                        // fr_count[i] will now be stuck at junction_pause_frames; no more updates for this link

                        // after adding new link, decrement colors if desired

                    }
                    fr_count[i]++;                            // update frame count
                } // end phase check
            } //end tower loop

            // check to see if dynamics of tower have stopped
            if (links_stopped_counter == num_init_links) {
                dynamics_stopped = true;
            }

            // shift camera presets upwards if links are moving upwards
            if (top_level_extend_frames > 0){
                cam_center.z = cam_center.z + (link_len_z+link_len_x) / max_top_level_extend_frames;  // side_len_x takes care of height of junctions
                preset2_cam_pos.loc.z = preset2_cam_pos.loc.z + (link_len_z+link_len_x) / max_top_level_extend_frames;
                preset2_cam_pos.sc.z = preset2_cam_pos.sc.z + (link_len_z+link_len_x) / max_top_level_extend_frames;
                top_level_extend_frames--;
            }

        } // end paused check
        if (keys_pressed[8]){
            reset();
            // initialize TowerLink objects and link_locs
            boolean valid_indices = false;                  // local variable
            curr_level = 1;                                 // for initializeNewLink
            // loop through all links to initialize
            for (int i = 0; i < num_max_links; i++) {
                // initialize all link objects, but only get starting coordinates for initial link objects
                if (i < num_init_links) {
                    // loop through until valid initial indices are found
                    while (!valid_indices) {
                        x = (int) parent.random((float) num_links_x + 1);
                        y = (int) parent.random((float) num_links_y + 1);
                        z = 0;
                        if (!is_occupied[x][y][z]) {
                            valid_indices = true;
                            // update logical array for valid indices; both the base location (at z = 0) and the destination
                            // location (at z = 1) should be marked as occupied
                            is_occupied[x][y][z] = true;
                            is_occupied[x][y][curr_level] = true;    // update collision array
                            initializeNewLink(x, y, z, 4, RecursiveTower.assignInitColor(parent,i,color_scheme));
                        }
                    }
                    valid_indices = false;   // reset valid_indices for next initial link
                } else {
                    // update link_locs
                    link_locs[i][0] = 0;     // keep track of link location in is_occupied
                    link_locs[i][1] = 0;     // keep track of link location in is_occupied
                    link_locs[i][2] = 0;     // keep track of link location in is_occupied
                    link_locs[i][3] = 0;     // cyrrent level is initially same as z-value
                }
            }
        }
    }

    /************************************ DRAW SITE *****************************************/
    public void drawSite(){
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    drawSite method loops through all current links and all current junctions and draws them

    AS A METHOD IN THE TowerLink INTERFACE:
    draws the link - named so that it coincides with a method in the Site abstract
    class. This way, recursive tower can satisfy both extended the Site abstract
    class and implementing the TowerLink interface with a single draw function. Same
    is true of the naming for updatePhysics.

    Calls: drawJunction
           TowerLink.drawSite
    Called by: Tubeworld.draw
    */

        parent.pushMatrix();
        parent.translate(origin.x, origin.y, origin.z);

        // draw bounding box for whole tower
        drawBoundingBoxTower();

        // draw bounding box for each link, but only if the link is of type 'RecursiveTower'
        if ( links[0].getClass().toString().equals( "RecursiveTower") ) {
            drawBoundingBoxLinks();
        }

        // draw junctions
        for (int i = 0; i < curr_num_junctions; i++){
            junctions[i].drawJunction();
        }

        // draw towers
        for (int i = 0; i < curr_num_links; i++){
            links[i].drawSite();
        }

        parent.popMatrix();
    }

    /************************************ UPDATE CAMERA *************************************/
    public int updateCam(Cam cam, int state, boolean[] keys_pressed, boolean[] keys_toggled){
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    This method updates the camera information during pre-set movement patterns. The state is 
    passed into the method from the CamCtrl object, which determines the camera behavior. 
    (Default behavior is user control of the camera, in which case this method does not get
    called.) 
    This method in turn calls methods from the Cam class to implement the movement patterns.
    Properties like top_level_extend_frames are updated during
    calls to the updatePhysics method, so that the camera presets can dynamically interface with
    the state of the tower.

    Calls: various methods in Cam class
    Called by: CamCtrl.update
     */
        
        if (state == 0) { // reset mode
            state = cam.smoothLinPursuit(init,reset_frames,0,1); // calling state, return state
        } else if (state == 2) { // roller coaster mode and not paused
            if (cam_fr_count == 0) {
                dir_mult = 5;
                rot_rad = PApplet.PI/246;
            }
            if (cam_fr_count < reset_frames){
                state = cam.smoothLinPursuit(preset2_cam_pos,reset_frames,2,2);
                cam_fr_count++;
            } else if (cam_fr_count >= reset_frames) {

                // shift upwards if beams are moving upwards
                if (top_level_extend_frames > 0 && !keys_toggled[32]){
                    cam.curr.loc.z = cam.curr.loc.z + (link_len_z+link_len_x) / max_top_level_extend_frames;
                    cam.curr.sc.z = cam.curr.sc.z + (link_len_z+link_len_x) / max_top_level_extend_frames;
                }

                // rotate
                cam.sphMoveTheta(cam_center,PApplet.PI/1024,"center");
                cam_fr_count++;

                // allow some amount of camera control; exit if other key press after initial reset
                // update speed multipliers
                if (keys_pressed[101]){
                    if (dir_mult > 2){--dir_mult;}
                }
                if (keys_pressed[114]) {
                    if (dir_mult < 256){++dir_mult;}
                }
                if (keys_pressed[2]) { // move forward (inward)
                    cam.moveForward(dir_mult);
                }
                if (keys_pressed[3]) { // move backward (outward)
                    cam.moveBackward(dir_mult);
                }
                if (keys_pressed[49]) { // return to state 1
                    state = 1;
                    cam_fr_count = 0;
                } // if keyPressed
            } // frameCount

        }
        return state;
    }






    /**================================== METHODS FOR LINK INTERFACE ========================/

    /************************************ SET LINK PROPERTIES *******************************/
    public void setLinkProperties(PVector origin_, int orientation_, int parent_orientation_) {
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    this method sets properties like the origin and orientation of a new link as it is added
    to the tower. If this method is being called it is because there is at least one level of
    recursion, so we need to make sure and update the orientation information for each of this
    tower's links, and set origins according to link_locs

    AS A METHOD IN THE TowerLink INTERFACE:
    this method is called when adding a new link to the RecursiveTower structure. The
    method sets all the necessary properties of the link object, such as the origin,
    color and orientation.

    Calls:
    Called by: initializeNewLink
    */

        // set the necessary properties in the current link;
        origin = origin_;
        orientation = orientation_;
        parent_orientation = parent_orientation_;
//        init_color = color; // color of tower has just been set; update init_color as well, for drawing bounding boxes

        // initialize TowerLink objects and link_locs
        boolean valid_indices = false;                  // local variable
        curr_level = 1;                                 // for initializeNewLink
        // loop through all links to initialize
        for (int i = 0; i < num_max_links; i++) {
            // initialize all link objects, but only get starting coordinates for initial link objects
            if (i < num_init_links) {
                // loop through until valid initial indices are found
                while (!valid_indices) {
                    x = (int) parent.random((float) num_links_x + 1);
                    y = (int) parent.random((float) num_links_y + 1);
                    z = 0;
                    if (!is_occupied[x][y][z]) {
                        valid_indices = true;
                        // update logical array for valid indices; both the base location (at z = 0) and the destination
                        // location (at z = 1) should be marked as occupied
                        is_occupied[x][y][z] = true;
                        is_occupied[x][y][curr_level] = true;	// update collision array
                        initializeNewLink(x,y,z,4,RecursiveTower.assignInitColor(parent,i,color_scheme));
                    }
                }
                valid_indices = false;   // reset valid_indices for next initial link
            } else {
                // update link_locs
                link_locs[i][0] = 0;     // keep track of link location in is_occupied
                link_locs[i][1] = 0;     // keep track of link location in is_occupied
                link_locs[i][2] = 0;     // keep track of link location in is_occupied
                link_locs[i][3] = 0;     // cyrrent level is initially same as z-value
            }
        }
    }

    /************************************ SET TOWER PROPERTIES ******************************/
    public void setTowerProperties(int num_links_x_, int num_links_y_, int num_links_z_, int num_init_links_,
                                   int num_max_links_, int update_type_) {
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    this method is called when updating a link that is a RecursiveTower object. The
    method sets all the necessary properties of the tower object that we might want to
    modify, which is a hacky way of changing variables in the 'link' array

    Calls: N/A
    Called by: reinitializeLinkObject
    */

        // set properties
        num_links_x = num_links_x_;
        num_links_y = num_links_y_;
        num_links_z = num_links_z_;
        num_init_links = num_init_links_;
        num_max_links = num_max_links_;
        update_type = update_type_;

        // set derived properties that use these properties
        num_max_junctions = num_max_links;
        side_len_x = ((float) num_links_x)*(link_len_z + link_len_x) + link_len_x; // total side length along x-direction
        side_len_y = ((float) num_links_y)*(link_len_z + link_len_y) + link_len_y; // total side length along y-direction
        side_len_z = ((float) num_links_z)*(link_len_z + link_len_x) - 2*link_len_x; // total side length along z-direction; no junction on bottom or top

        // initialize new arrays
        links = new TowerBeam[num_max_links];
        junctions = new Junction[num_max_junctions];
        is_occupied = new boolean[num_links_x+1][num_links_y+1][num_links_z+1];   // logical array for collision detection;
        link_locs = new int[num_max_links][4];  // sketchy way to keep track of link locations in is_occupied array (PVector doesn't take ints)
        fr_count = new float[num_max_links];    // frame count for controlling pauses during junctions; 1 for each link

        // links array
        for (int i = 0; i < num_max_links; i++) {
            links[i] = new TowerBeam(parent, link_len_x, link_len_z, 4, new PVector(0,0,0), RecursiveTower.assignInitColor(parent,i,color_scheme), orientation);
        }

        // junction array
        for (int i = 0; i < num_max_junctions; i++){
            junctions[i] = new Junction(parent, 0, 0, 0, link_len_x, link_len_y, link_len_x, new float[] {0,0,1}, new float[] {0,0,1}, orientation);
        }

        // the remaining arrays will be reset during the call to the reset() method

    }

    /************************************ GETTING METHODS ***********************************/
    public boolean getDynamicsStopped() {
    /* this method returns the dynamics_stopped variable, that specifies whether or not
       a link is still updating
     */
        return dynamics_stopped;
    }
    public float[] getColor() {
    /* this method returns the color property of the link
     */
        return color;
    }

    /************************************ SETTING METHODS ***********************************/
    public void setColor(float[] color_) {
    /* this method sets the color property of the link object
     */
        color = color_;
    }

    /************************************ RESET *********************************************/
    public void reset(){
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    this method resets all properties of the tower, including is_occupied and all of its links,
    and begins updating/rendering as if the tower was newly constructed.

    AS A METHOD IN THE TowerLink CLASS:
    this method resets all relevant properties of the link so that the object can be
    reused. In the case of a RecursiveTower object, this includes resetting is_occupied
    */

        // reset counters
        curr_num_links = 0;                     // current number of links being drawn
        links_indx = -1;                        // index into links array that points to current links
        curr_num_junctions = 0;                 // current number of junctions
        junctions_indx = -1;                    // index into tower array that points to current junction

        // properties for dynamics of links
        top_level = 1;      				    // top level (highest among links) in is_occupied; links initialize on level 0 heading upwards
        temp_top_level_z = 1;       		    // z-value of temp_top_level, for reinitializing link locations to current top level
        dynamics_stopped = false;         	    // boolean specifying whether dynamics should be updated or not
        links_stopped_counter = 0;              // for determining when all links have been drawn

        // color properties
        init_color[0] = 0; init_color[1] = 0; init_color[2] = 1;
        color[0] = 0; color[1] = 0; color[2] = 1;

        // properties for camera updates; just update z-values
        //cam_fr_count = 0;                     // frame counter
        top_level_extend_frames = 0;            // so that we don't go flying off into space
        cam_center.z = origin.z;
        preset2_cam_pos.loc.z = origin.z+1000;
        preset2_cam_pos.sc.z = origin.z;

        // REINITIALIZE ARRAYS
        // is_occupied array
        for (int i = 0; i < num_links_x+1; i++){
            for (int j = 0; j < num_links_y+1; j++){
                for (int k = 0; k < num_levels; k++){
                    is_occupied[i][j][k] = false;
                }
            }
        }

        // fr_count arrays and reset
        for (int i = 0; i < num_max_links; i++){
            fr_count[i] = 0;
            links[i].reset();
        }
    }





    /**================================== OTHER METHODS =====================================/

    /************************************ ADD JUNCTION **************************************/
    public void addJunction(int indx) {
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    Adds a new junction to 'junctions' array, upon full extension of a link.

    Calls: N/A
    Called by: updatePhysics
    */

        // set new location in junctions array
        junctions_indx = (junctions_indx+1) % num_max_junctions;
        // update number of junctions
        if (curr_num_junctions < num_max_junctions){
            curr_num_junctions++;
        }

        // set necessary properties using indx of link that has just finished extending.
        // subtract side_len_z/2 because bottom nodes are flat
        junctions[junctions_indx].x = ((float) link_locs[indx][0])*(link_len_z+link_len_x)+link_len_x/2;
        junctions[junctions_indx].y = ((float) link_locs[indx][1])*(link_len_z+link_len_y)+link_len_y/2;
        junctions[junctions_indx].z = ((float) link_locs[indx][2])*(link_len_z+link_len_x)-link_len_x/2;
        junctions[junctions_indx].tower_orientation = orientation;
        junctions[junctions_indx].updateFillColor(links[indx].getColor());
        junctions[junctions_indx].updateStrokeColor(links[indx].getColor());

    }

    /************************************ ADD LINK ******************************************/
    public void addLink(int indx) {
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    Adds a new link to 'links' array; not as straightforward as addJunction, because we need to
    figure out where the link can be added; this method also deals with the type of update method
    we want

    Calls: reset
           findVacantSpot
           reinitializeLinkLocation
           initializeNewLink

    Called by: updatePhysics
    */

        // find next spot to move to having just come from link 'indx'
		/* temp_top_level allows links updated after another link was forced upwards on this iteration
		 * to continue updating on the level below; updating the top_level after all link updates will
		 * force the upwards move of new links on next iteration
		 */

        // store top level in a temp variable for later manipulation
        temp_top_level = top_level;

        // fill out temp variables for ease of interpretation
        x = link_locs[indx][0];
        y = link_locs[indx][1];
        z = link_locs[indx][2];
        curr_level = link_locs[indx][3];

        if (update_type == 0) {
            // check to see if the current link needs to be forced upwards
            if (curr_level != top_level) {
                curr_level = (curr_level + 1) % num_levels;    // reassign curr_level

                // force upwards; this deals with case when another tower has moved up first, and this link has
                // respond
                if (is_occupied[x][y][curr_level]) {
                    /* link can't move upwards - needs to reinitialize its location on temp_top_level
                     * Note that is_occupied is not updated to reflect the beam leaving this level - this will be
                     * handled when a beam from the top level moves into this level, i.e. a complete reset of values
                     */
                    reinitializeLinkLocation(indx);
                    findVacantSpot(indx);                   // try again; location of indx was redefined in reinitializeLinkLocation
                } else {
                    // girder can move upwards
                    initializeNewLink(x,y,z,4,links[indx].getColor());				// add new link
                }
            } else {
                // randomly select new position to move to in same level
                findVacantSpot(indx);
            }

        } else if (update_type == 1) {

            /* if current level is right above temp_top_level, this means the top of is_occupied is looping back around
             * and about to catch this link. To fix this situation we'll force the current link upwards and then
             * clear that level of is_occupied to make room for the links in top_level that will soon be moving up
             * again
             */
            if (curr_level == (temp_top_level + 1) % num_levels) {
                curr_level = (curr_level + 1) % num_levels;    // reassign curr_level

                // force upwards; this deals with case when another tower has moved up first, and this link has
                // respond
                if (is_occupied[x][y][curr_level]) {
                    /* link can't move upwards - needs to reinitialize its location on temp_top_level
                     * Note that is_occupied is not updated to reflect the beam leaving this level - this will be
                     * handled when a beam from the top level moves into this level, i.e. a complete reset of values
                     */
                    reinitializeLinkLocation(indx);
                    findVacantSpot(indx);                   // try again; location of indx was redefined in reinitializeLinkLocation
                } else {
                    // girder can move upwards
                    initializeNewLink(x,y,z,4,links[indx].getColor());				// add new link
                }
            } else {
                // girder is free to update normally
                findVacantSpot(indx);
            }

        } // end update_type check
        if (temp_top_level != top_level) {
            // we've moved up; set flag so we can increase center for cam preset
            top_level_extend_frames = max_top_level_extend_frames;
        }
        top_level = temp_top_level; // finally update top_level


    }

    /************************************ FIND VACANT SPOT **********************************/
    public void findVacantSpot(int indx) {
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    this method randomly selects an available location for the next link to target

    Calls: initializeNewLink
           reinitializeLinkLocation
           findVacantSpot
    Called by: addLink
    */

        // argument is index into link_locs array
        // top_level (update_type = 0) or curr_level (update_type = 1)

        // fill out temp variables for ease of interpretation; x,y,z and curr_level represent the current node
        // in is_occupied; this method finds an available node to move to from this location
        x = link_locs[indx][0];
        y = link_locs[indx][1];
        z = link_locs[indx][2];
        curr_level = link_locs[indx][3];

        // initialize relevant variables; trans_probs entries should add to 4, and can be biased in particular
        // directions if not all the same, which might be fun to play with
        num_poss_pos = 4;
        trans_probs[0] = 1; // +x
        trans_probs[1] = 1; // -x
        trans_probs[2] = 1; // +y
        trans_probs[3] = 1; // -y

        // check for boundaries and already occupied nodes
        if ((x == num_links_x) || is_occupied[x+1][y][curr_level]){
            trans_probs[0] = 0;	// don't increase x value
            num_poss_pos--;		// decrement
        }
        if ((x == 0) || is_occupied[x-1][y][curr_level]){
            trans_probs[1] = 0; // don't decrease x value
            num_poss_pos--;		// decrement
        }
        if ((y == num_links_y) || is_occupied[x][y+1][curr_level]){
            trans_probs[2] = 0; // don't increase y value
            num_poss_pos--;		// decrement
        }
        if ((y == 0) || is_occupied[x][y-1][curr_level]){
            trans_probs[3] = 0; // don't decrease y value
            num_poss_pos--;		// decrement
        }

        // update transition probabilities and move
        if (num_poss_pos == 0){
            // link is forced in upwards direction

            curr_level = (curr_level+1) % num_levels;		// reassign level

            // see if link can move upwards
			/*
			 if the node above is blocked, and there could be 2 reasons for this.
			 1. the node is actually blocked, and we can't move there. in this case the link can't move upwards
			 and its location should be reinitialize (reinitializeLinkLocation)
			 2. the link is on the top level, and the level above is the old logic setup from several levels
			 BEHIND the top level; in this case we DO want to move into that space, and reset the is_occupied
			 array while we're at it. There is no possibility that an actual link might still occupy this location,
			 since these links will have been forced upwards during the previous iteration
			*/
            if (curr_level == (temp_top_level+1) % num_levels) {
                // link CAN move upwards because we're moving into an old logic layer
                initializeNewLink(x,y,z,4,links[indx].getColor());				// add new link

                // one level above temp_top_level, first link on this level
                temp_top_level = curr_level;
                temp_top_level_z++;
                // reset is_occupied at this level to clear out old logic
                for (int j = 0; j < num_links_x+1; j++) {
                    for (int k = 0; k < num_links_y+1; k++) {
                        is_occupied[j][k][curr_level] = false;
                    }
                }
                is_occupied[x][y][curr_level] = true;	// re-update collision array for newly added link

            } else if (is_occupied[x][y][curr_level]){
                // link CAN'T move upwards, and we're not on top layer - needs to reinitialize
                reinitializeLinkLocation(indx);
                // call function recursively; location of indx was redefined in reinitializeLinkLocation
                findVacantSpot(indx);
            } else {
                // [x][y][curr_level] is NOT occupied, and we're not moving into an old logic layer; move upwards
                initializeNewLink(x,y,z,4,links[indx].getColor());				// add new link
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
                initializeNewLink(x,y,z,0,links[indx].getColor());
            } else if (rand < trans_probs[0]+trans_probs[1]){
                initializeNewLink(x,y,z,1,links[indx].getColor());
            } else if (rand < trans_probs[0]+trans_probs[1]+trans_probs[2]){
                initializeNewLink(x,y,z,2,links[indx].getColor());
            } else {
                initializeNewLink(x,y,z,3,links[indx].getColor());
            }
        }
    }

    /************************************ INITIALIZE NEW LINK *******************************/
    public void initializeNewLink(int x, int y, int z, int orientation_, float[] color_) {
    /*
    orientation_ is the orientation of the link wrt is_occupied, not the tower holding the link

    AS A METHOD IN THE RecursiveTower CLASS:
    this method updates the current index into the link array, resets the link at that location
    if we are looping back through the array, and then calls setLinkProperties to update the
    properties of the link in that location with the new values. This is mainly just a helper
    function to keep the code looking cleaner in the addLink method.

    Calls: TowerLink.setLinkProperties
           TowerLink.reset
    Called by: addLink
               findVacantSpot
    */

        // add tower to end of tower array queue
        links_indx = (links_indx+1) % num_max_links;
        // update number of towers
        if (curr_num_links < num_max_links){
            curr_num_links++;
        } else {
            // we're moving back through the links array; need to reset link at that index
            links[links_indx].reset();
        }


        // set orientation and origin of new link in context of tower's orientation
        PVector orig = new PVector(0,0,0);
        orig.x = ((float) x)*(link_len_z + link_len_x);
        orig.y = ((float) y)*(link_len_z + link_len_y);
        orig.z = ((float) z)*(link_len_z + link_len_x) - link_len_x;
        /* add additional offsets as dictated by orientation; this takes into account the presence of junctions,
           which offsets the origin by a particular amount depending on the orientation of the link

           also going to update target position of new link and is_occupied here, which also depend on direction of update
        */
        switch (orientation_) {
            case 0:
                // link is moving in +x direction wrt is_occupied
                orig.x = orig.x + link_len_x;
                link_locs[links_indx][0] = x+1;
                link_locs[links_indx][1] = y;
                link_locs[links_indx][2] = z;
                link_locs[links_indx][3] = curr_level;
                is_occupied[x+1][y][curr_level] = true;	// update collision array
                break;
            case 1:
                // link is moving in -x direction wrt is_occupied
                link_locs[links_indx][0] = x-1;
                link_locs[links_indx][1] = y;
                link_locs[links_indx][2] = z;
                link_locs[links_indx][3] = curr_level;
                is_occupied[x-1][y][curr_level] = true;	// update collision array
                break;
            case 2:
                // link is moving in +y direction wrt is_occupied
                orig.y = orig.y + link_len_y;
                link_locs[links_indx][0] = x;
                link_locs[links_indx][1] = y+1;
                link_locs[links_indx][2] = z;
                link_locs[links_indx][3] = curr_level;
                is_occupied[x][y+1][curr_level] = true;	// update collision array
                break;
            case 3:
                // link is moving in -y direction wrt is_occupied
                link_locs[links_indx][0] = x;
                link_locs[links_indx][1] = y-1;
                link_locs[links_indx][2] = z;
                link_locs[links_indx][3] = curr_level;
                is_occupied[x][y-1][curr_level] = true;	// update collision array
                break;
            case 4:
                // link is moving in +z direction wrt is_occupied
                orig.z = orig.z + link_len_x;
                link_locs[links_indx][0] = x;
                link_locs[links_indx][1] = y;
                link_locs[links_indx][2] = z+1;
                link_locs[links_indx][3] = curr_level;
                is_occupied[x][y][curr_level] = true;	// update collision array
                break;
        }
        links[links_indx].setColor(color_);
        links[links_indx].setLinkProperties(orig,orientation_,orientation);

    }

    /************************************ REINITIALIZE LINK LOCATION ************************/
    public void reinitializeLinkLocation(int indx) {
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    this method is called whenever a link cannot move in any direction from its current position.
    It will instead be moved to an open node in the uppermost level (temp_top_level) and from there
    findVacantSpot will be called again.

    Calls: addJunction
    Called by: addLink
               findVacantSpot
    */

        boolean valid_indices = false;
        // loop through until valid initial indices are found
        while (!valid_indices) {
            x = (int) parent.random((float) num_links_x+1);
            y = (int) parent.random((float) num_links_y+1);
            z = temp_top_level;
            if (!is_occupied[x][y][z]) {
                valid_indices = true;
                // update logical array for valid indices
                is_occupied[x][y][z] = true;
            }
        }
        link_locs[indx][0] = x;                // keep track of tower target location in meta_is_occupied
        link_locs[indx][1] = y;                // keep track of tower target location in meta_is_occupied
        link_locs[indx][2] = temp_top_level_z; // keep track of tower target location in meta_is_occupied
        link_locs[indx][3] = temp_top_level;
        addJunction(indx);                     // add new junction for tower to build on

    }

    /************************************ REINITIALIZE LINK TYPE ****************************/
    public void reinitializeLinkType(String link_type, int num_links_x_, int num_links_y_, int num_links_z_,
                                       int num_init_links_, int num_max_links_, int update_type_) {
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    this method is called when a true RecursiveTower is desired; the constructor of
    RecursiveTower automatically selects the link type to be of the TowerBeam class to avoid
    catastrophic recursive looping. In order to set the links of a tower to RecursiveTower
    objects (or any other for that matter), this method can be called and will reinitialize
    all of the relevant properties of the RecursiveTower object. The inputs (num_links_x,...,num_init_links,etc.)
    all refer to the properties of the towers that act as links for this object

    Calls:
    Called by: Tubeworld constructor
    */

        // set properties from input arguments
        if (link_type.equals("RecursiveTower")) {

            links = new RecursiveTower[num_max_links];   // array of link objects

            for (int i = 0; i < num_max_links; i++) {
                links[i] = new RecursiveTower(parent,new PVector(0,0,0), 0,
                                    new CamParam(new PVector(0,0,-1),new PVector(0,0,0),new PVector(0,0,0),new PVector(0,-1,0)), 120);
                links[i].setTowerProperties(num_links_x_,num_links_y_,num_links_z_,num_init_links_,num_max_links_,update_type_);
                /* calling reset method at the end of this method will reinitialize all the relevant properties of the new links,
                 such as is_occupied, etc.
                 */
            }

            // change properties of higher level RecursiveTower to reflect the updated link identities
            // change link_len; these are now full towers instead of links
            float link_len_x_ = link_len_x;                                                     // take link_lens from initial tower
            float link_len_y_ = link_len_y;                                                     // take link_lens from initial tower
            float link_len_z_ = link_len_z;                                                     // take link_lens from initial tower
            link_len_x = ((float) num_links_x_)*(link_len_z_ + link_len_x_) + link_len_x_;      // side length of individual beams along x-direction
            link_len_y = ((float) num_links_y_)*(link_len_z_ + link_len_y_) + link_len_y_;      // side length of individual beams along y-direction
            link_len_z = ((float) num_links_z_)*(link_len_z_ + link_len_x_) - link_len_x_;      // side length of individual beams along z-direction; no junctions on top or bottom
                                                                                                // +1 because of how dynamics_stopped flag is updated in addLink
            // change side_len; these are now sides of a meta tower
            side_len_x = ((float) num_links_x)*(link_len_z + link_len_x) + link_len_x;      // total side length along x-direction
            side_len_y = ((float) num_links_y)*(link_len_z + link_len_y) + link_len_y;      // total side length along y-direction
            side_len_z = ((float) num_links_z)*(link_len_z + link_len_x) - link_len_x;      // total side length along z-direction

            // update junction array
            for (int i = 0; i < num_max_junctions; i++){
                junctions[i].x_dim = link_len_x;
                junctions[i].y_dim = link_len_y;
                junctions[i].z_dim = link_len_x;
                junctions[i].stroke_color[0] = 0;
                junctions[i].stroke_color[1] = 0;
                junctions[i].stroke_color[2] = 1;
                junctions[i].fill_color[0] = 0;
                junctions[i].fill_color[1] = 0;
                junctions[i].fill_color[2] = 0;
                junctions[i].stroke_color_inheritance_type = "initial";
                junctions[i].fill_color_inheritance_type = "initial";
            }

            // reset top level RecursiveTower
            // reset method will also reset all its links, which in turn will reset
            // is_occupied and camera properties that rely on side length of tower, etc.
            reset();


            // update camera properties with new side lengths
            // cam center will be at the center of the tower and follow the movement upwards
            cam_center = new PVector(origin.x+side_len_x/2,origin.y+side_len_x/2,origin.z);
            // preset2_cam_pos will be outside the tower and slightly above the current level, looking inwards
            preset2_cam_pos.loc = new PVector(origin.x+2*side_len_x,origin.y+side_len_y/2,origin.z+2000);
            preset2_cam_pos.sc = new PVector(origin.x+side_len_x/2,origin.y+side_len_y/2,origin.z);
            // reset init to be at the center of the tower, given the number of links; init will be looking down at the initial tower,
            // and this is where the camera will return upon resetting the camera position (key 0)
            init.loc = new PVector(origin.x+side_len_x/2,origin.y+side_len_y/2,origin.z+2000);
            init.sc  = new PVector(origin.x+side_len_x/2,origin.y+side_len_y/2,origin.z);
            init.dir = new PVector(0,0,-1);
            init.down = new PVector(0,-1,0);

            // initialize TowerLink objects and link_locs
            boolean valid_indices = false;                  // local variable
            curr_level = 1;                                 // for initializeNewLink
            // loop through all links to initialize
            for (int i = 0; i < num_max_links; i++) {
                // initialize all link objects, but only get starting coordinates for initial link objects
                if (i < num_init_links) {
                    // loop through until valid initial indices are found
                    while (!valid_indices) {
                        x = (int) parent.random((float) num_links_x + 1);
                        y = (int) parent.random((float) num_links_y + 1);
                        z = 0;
                        if (!is_occupied[x][y][z]) {
                            valid_indices = true;
                            // update logical array for valid indices; both the base location (at z = 0) and the destination
                            // location (at z = 1) should be marked as occupied
                            is_occupied[x][y][z] = true;
                            is_occupied[x][y][curr_level] = true;	// update collision array
                            initializeNewLink(x,y,z,4,links[i].getColor());
                        }
                    }
                    valid_indices = false;   // reset valid_indices for next initial link
                } else {
                    // update link_locs
                    link_locs[i][0] = 0;     // keep track of link location in is_occupied
                    link_locs[i][1] = 0;     // keep track of link location in is_occupied
                    link_locs[i][2] = 0;     // keep track of link location in is_occupied
                    link_locs[i][3] = 0;     // cyrrent level is initially same as z-value
                }
            }
        }
    }

    /************************************ DRAW BOUNDING BOX FOR TOWER ***********************/
    public void drawBoundingBoxTower(){
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    this method is called during each call to the drawSite method, and draws the open square around
    the bottom of the tower. Assumes that the current position is at the origin of the tower.

    Calls: N/A
    Called by: drawSite
    */

        rgb = RecursiveTower.hsvToRgb(init_color[0], init_color[1], init_color[2]);
        parent.stroke(rgb[0], rgb[1], rgb[2]);

        // draw bounding box for each link; take tower orientation into account
        switch (orientation) {
            case 0:
                // moving in +x direction
                // +x becomes +y, +y becomes +z, +z becomes +x
                parent.pushMatrix();
                parent.translate(0, side_len_x/2, side_len_y/2);
                parent.noFill();
                parent.box(0, side_len_x, side_len_y);
                parent.popMatrix();
                break;
            case 1:
                // moving in -x direction
                // +x becomes +y, +y becomes +z, +z becomes -x
                parent.pushMatrix();
                parent.translate(0, side_len_x/2, side_len_y/2);
                parent.noFill();
                parent.box(0, side_len_x, side_len_y);
                parent.popMatrix();
                break;
            case 2:
                // moving in +y direction
                // x stays the same, +y becomes +z, +z becomes +y
                parent.pushMatrix();
                parent.translate( side_len_x/2, 0, side_len_y/2 );
                parent.noFill();
                parent.box(side_len_x, 0, side_len_y);
                parent.popMatrix();
                break;
            case 3:
                // moving in -y direction
                // x stays the same, +y becomes +z, +z becomes -y
                parent.pushMatrix();
                parent.translate( side_len_x/2, 0, side_len_y/2 );
                parent.noFill();
                parent.box(side_len_x, 0, side_len_y);
                parent.popMatrix();
                break;
            case 4:
                // xyz coordinates stay the same
                parent.pushMatrix();
                parent.translate( side_len_x/2, side_len_y/2, 0 );
                parent.noFill();
                parent.box(side_len_x,side_len_y,0);
                parent.popMatrix();
                break;
        } // switch
    }

    /************************************ DRAW BOUNDING BOX FOR LINKS ***********************/
    public void drawBoundingBoxLinks() {
    /*
    AS A METHOD IN THE RecursiveTower CLASS:
    this method is called during each call to the drawSite method, and draws the open square around
    the bottom of the links in the tower. Assumes that the current position is at the origin of the tower.

    Calls: N/A
    Called by: drawSite
    */

        // draw bounding box for each link; take tower orientation into account
        xf = side_len_x;  // temp variable to store dim of bounding box
        yf = side_len_y;  // temp variable to store dim of bounding box
        zf = 0;           // temp variable to store dim of bounding box
        for (int i = 0; i < num_links_x + 1; i++) {
            for (int j = 0; j < num_links_y + 1; j++) {

                parent.pushMatrix();
                parent.stroke(255, 255, 255);
                parent.noFill();

                switch (orientation) {
                    case 0:
                        // moving in +x direction
                        // +x becomes +y, +y becomes +z, +z becomes +x
                        parent.translate(0,
                                ((float) i) * (side_len_z + side_len_x) + side_len_x / 2,
                                ((float) j) * (side_len_z + side_len_y) + side_len_y / 2);
                        parent.box(zf, xf, yf);
                        break;
                    case 1:
                        // moving in -x direction
                        // +x becomes +y, +y becomes +z, +z becomes -x
                        parent.translate(0,
                                ((float) i) * (side_len_z + side_len_x) + side_len_x / 2,
                                ((float) j) * (side_len_z + side_len_y) + side_len_y / 2);
                        parent.box(-zf, xf, yf);
                        break;
                    case 2:
                        // moving in +y direction
                        // x stays the same, +y becomes +z, +z becomes +y
                        parent.translate(((float) i) * (side_len_z + side_len_x) + side_len_x / 2,
                                0,
                                ((float) j) * (side_len_z + side_len_y) + side_len_y / 2);
                        parent.box(xf, zf, yf);
                        break;
                    case 3:
                        // moving in -y direction
                        // x stays the same, +y becomes +z, +z becomes -y
                        parent.translate(((float) i) * (side_len_z + side_len_x) + side_len_x / 2,
                                0,
                                ((float) j) * (side_len_z + side_len_y) + side_len_y / 2);
                        parent.box(xf, -zf, yf);
                        break;
                    case 4:
                        // xyz coordinates stay the same
                        parent.translate(((float) i) * (side_len_z + side_len_x) + side_len_x / 2,
                                ((float) j) * (side_len_z + side_len_y) + side_len_y / 2,
                                0);
                        parent.box(xf, yf, zf);
                        break;
                } // switch
                parent.popMatrix();
            } // end draw bounding box j
        } // end draw bounding box i
    }



    /************************************ ASSIGN INITIAL COLOR ******************************/
    public static float[] assignInitColor(PApplet parent, int i, String type) {
        float[] hsv = {0, 0, 0};
        if (type.equals("rgb")) {
            if (i%3 == 0) {
                hsv[0] = 0; hsv[1] = 1; hsv[2] = 1; return hsv;
            } else if (i%3 == 1){
                hsv[0] = 0.3333333f; hsv[1] = 1; hsv[2] = 1; return hsv;
            } else if (i%3 == 2){
                hsv[0] = 0.6666667f; hsv[1] = 1; hsv[2] = 1; return hsv;
            } else
                return hsv;
        } else if (type.equals("hsv")) {
            hsv[0] = parent.random(1); hsv[1] = 1; hsv[2] = 1; return hsv;
        } else {
            return hsv;
        }

    }

    /************************************ HSV TO RGB FUNCTION *******************************/
    public static int[] hsvToRgb(float hue, float saturation, float value) {

        int h = (int)(hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        int[] rgb = {0, 0, 0};
        switch (h) {
            case 0: rgb[0] = (int) (255 * value); rgb[1] = (int) (255 * t); rgb[2] = (int) (255 * p); break;
            case 1: rgb[0] = (int) (255 * q); rgb[1] = (int) (255 * value); rgb[2] = (int) (255 * p); break;
            case 2: rgb[0] = (int) (255 * p); rgb[1] = (int) (255 * value); rgb[2] = (int) (255 * t); break;
            case 3: rgb[0] = (int) (255 * p); rgb[1] = (int) (255 * q); rgb[2] = (int) (255 * value); break;
            case 4: rgb[0] = (int) (255 * t); rgb[1] = (int) (255 * p); rgb[2] = (int) (255 * value); break;
            case 5: rgb[0] = (int) (255 * value); rgb[1] = (int) (255 * p); rgb[2] = (int) (255 * q); break;
            default: throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
        return rgb;
    }

}
