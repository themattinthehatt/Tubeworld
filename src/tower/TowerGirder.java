package tower;

import processing.core.PApplet;
import processing.core.PVector;

public class TowerGirder {

	// girder properties
	public PApplet parent; 		// parent PApplet for drawing methods
	public int curr_pos_x; 		// position of current beam object in int space
	public int curr_pos_y; 		// position of current beam object in int space
	public int curr_pos_z; 		// position of current beam object in int space
	public int curr_level;		// level in logic array (modded version of curr_pos_z)
	public int max_num_beams;	// maximum number of beams; preallocate
	public int num_beams;		// current number of beams
	public int beg_indx;		// beginning index for beam (treat as queue)
	public int end_indx;		// ending index for beam (treat as queue)
	public int init_color;		// initial color specification
	public TowerBeam[] beam;	// array of beam objects to create girder

	// drawing temp variables
	private float x;			// temp x-value for drawing girder
	private float y;			// temp y-value for drawing girder
	private float z;			// temp z-value for drawing girder


	/************************************ CONSTRUCTOR **************************************/
	public TowerGirder(PApplet parent_,int curr_pos_x_,int curr_pos_y_,int curr_pos_z_,int init_color_,float side_width_,float side_len_){

		// initialize PApplet for drawing methods
		parent = parent_;

		// integer index into logical collision detection array
		curr_pos_x = curr_pos_x_;	
		curr_pos_y = curr_pos_y_;
		curr_pos_z = curr_pos_z_;
		curr_level = 0;				// start at level 1 of [0 1 2];

		// initialize other properties
		num_beams = 0;
		beg_indx = 0;
		end_indx = -1; // adding first beam will bring this to 0
		init_color = init_color_;

		// initialize tower.TowerBeam objects
		max_num_beams = 128; // maybe make this dependent on tower.Tower size?
		beam = new TowerBeam[max_num_beams];	
		for (int i = 0; i < max_num_beams; i++) {
			beam[i] = new TowerBeam(side_width_,side_len_,0,new PVector(0,0,0),init_color_);
		}
	}

	public void drawGirder(int tower_orientation){
		// tower orientation defines transform needed to go from xyz coordinates used in logic array to 3D xyz coordinates
		// Here we need to take into account not only the orientation of the tower, but also the orientation of the individual
		// beams within that girder. Poo. Some matrix transforms on a whole bunch of vertices would be efficient...
		switch (tower_orientation) {
			case 0:
				// moving in +x direction
				// +x becomes +y, +y becomes +z, +z becomes +x
				// iterate through beams
				for (int i = 0; i < num_beams; i++) {
					parent.pushMatrix();
					parent.translate(beam[i].loc.z, beam[i].loc.x, beam[i].loc.y);
					parent.stroke(beam[i].color);
					parent.fill(beam[i].color);
					// draw box along different axes depending on orientation value
					if (beam[i].orientation == 0 || beam[i].orientation == 1) {
						parent.box(beam[i].side_width, beam[i].side_len, beam[i].side_width);    // long axis in y-dir
					} else if (beam[i].orientation == 2 || beam[i].orientation == 3) {
						parent.box(beam[i].side_width, beam[i].side_width, beam[i].side_len);    // long axis in z-dir
					} else if (beam[i].orientation == 4) {
						parent.box(beam[i].side_len, beam[i].side_width, beam[i].side_width);    // long axis in x-dir
					}
					parent.popMatrix();
				}
				break;
			case 1:
				// moving in -x direction
				// +x becomes +y, +y becomes +z, +z becomes -x
				for (int i = 0; i < num_beams; i++) {
					parent.pushMatrix();
					parent.translate(-beam[i].loc.z, beam[i].loc.x, beam[i].loc.y);
					parent.stroke(beam[i].color);
					parent.fill(beam[i].color);
					// draw box along different axes depending on orientation value
					if (beam[i].orientation == 0 || beam[i].orientation == 1) {
						parent.box(beam[i].side_width, beam[i].side_len, beam[i].side_width);    // long axis in y-dir
					} else if (beam[i].orientation == 2 || beam[i].orientation == 3) {
						parent.box(beam[i].side_width, beam[i].side_width, beam[i].side_len);    // long axis in z-dir
					} else if (beam[i].orientation == 4) {
						parent.box(beam[i].side_len, beam[i].side_width, beam[i].side_width);    // long axis in x-dir
					}
					parent.popMatrix();
				}
				break;
			case 2:
				// moving in +y direction
				// x stays the same, +y becomes +z, +z becomes +y
				for (int i = 0; i < num_beams; i++) {
					parent.pushMatrix();
					parent.translate(beam[i].loc.x, beam[i].loc.z, beam[i].loc.y);
					parent.stroke(beam[i].color);
					parent.fill(beam[i].color);
					// draw box along different axes depending on orientation value
					if (beam[i].orientation == 0 || beam[i].orientation == 1) {
						parent.box(beam[i].side_len, beam[i].side_width, beam[i].side_width);    // long axis in x-dir
					} else if (beam[i].orientation == 2 || beam[i].orientation == 3) {
						parent.box(beam[i].side_width, beam[i].side_width, beam[i].side_len);    // long axis in z-dir
					} else if (beam[i].orientation == 4) {
						parent.box(beam[i].side_width, beam[i].side_len, beam[i].side_width);    // long axis in y-dir
					}
					parent.popMatrix();
				}
				break;
			case 3:
				// moving in -y direction
				// x stays the same, +y becomes +z, +z becomes -y
				for (int i = 0; i < num_beams; i++) {
					parent.pushMatrix();
					parent.translate(beam[i].loc.x, -beam[i].loc.z, beam[i].loc.y);
					parent.stroke(beam[i].color);
					parent.fill(beam[i].color);
					// draw box along different axes depending on orientation value
					if (beam[i].orientation == 0 || beam[i].orientation == 1) {
						parent.box(beam[i].side_len, beam[i].side_width, beam[i].side_width);    // long axis in x-dir
					} else if (beam[i].orientation == 2 || beam[i].orientation == 3) {
						parent.box(beam[i].side_width, beam[i].side_width, beam[i].side_len);    // long axis in z-dir
					} else if (beam[i].orientation == 4) {
						parent.box(beam[i].side_width, beam[i].side_len, beam[i].side_width);    // long axis in y-dir
					}
					parent.popMatrix();
				}
				break;
			case 4:
				// moving in +z direction
				// xyz coordinates stay the same
				// iterate through beams
				for (int i = 0; i < num_beams; i++){
					parent.pushMatrix();
					parent.translate(beam[i].loc.x, beam[i].loc.y, beam[i].loc.z);
					parent.stroke(beam[i].color);
					parent.fill(beam[i].color);
					// draw box along different axes depending on orientation value
					if (beam[i].orientation == 0 || beam[i].orientation == 1){
						parent.box(beam[i].side_len, beam[i].side_width, beam[i].side_width);	// long axis in x-dir
					} else if (beam[i].orientation == 2 || beam[i].orientation == 3){
						parent.box(beam[i].side_width, beam[i].side_len, beam[i].side_width); 	// long axis in y-dir
					} else if (beam[i].orientation == 4){
						parent.box(beam[i].side_width, beam[i].side_width, beam[i].side_len); 	// long axis in z-dir
					}
					parent.popMatrix();
				}
				break;

		}

	}

	public void addBeam(int orientation){
		
		// add beam to end of beam array queue
		end_indx = (end_indx+1) % max_num_beams;
		// update number of beams
		if (num_beams < max_num_beams){
			num_beams++;
		}

		/* initially use location of node; these values will change through draw iterations in updateBeam method
		 * to eventually be the location between the nodes that are spanned by the beam
		 * need to take into account the overlapping of beams at nodes
		 * side_len is distance between nodes PLUS side_width to deal with overhang; subtracting off side_width
		 * gives distance between nodes in physical space
		*/
		beam[end_indx].loc.x = ((float) curr_pos_x)*(beam[end_indx].side_len-beam[end_indx].side_width);
		beam[end_indx].loc.y = ((float) curr_pos_y)*(beam[end_indx].side_len-beam[end_indx].side_width);
		beam[end_indx].loc.z = ((float) curr_pos_z)*(beam[end_indx].side_len-beam[end_indx].side_width);
		 
		// initialize length to be same as width (length used above for calculating beam loc should be correct
		// whether using new beam or overtaking old)
		beam[end_indx].side_len = beam[end_indx].side_width;

		// update colors
		beam[end_indx].color = init_color;

		// update orientation of new beam object
		beam[end_indx].orientation = orientation;

	}

	public void updateBeam(float beam_extend_frames, float desired_length){
		// update length of beam while expanding; direction depends on orientation
		
		/* add proportion of desired length that depends on number of frames used to extend beam;
		 * note that initial beam side_len is set to side_width, so this extra length needed for 
		 * drawing is already taken care of
		 */
		beam[end_indx].side_len = beam[end_indx].side_len + desired_length/beam_extend_frames;
		
		// location will be halfway between nodes
		if (beam[end_indx].orientation == 0){
			// moving in +x-direction
			beam[end_indx].loc.x = beam[end_indx].loc.x + desired_length/beam_extend_frames/2;
		} else if (beam[end_indx].orientation == 1){
			// moving in -x-direction
			beam[end_indx].loc.x = beam[end_indx].loc.x - desired_length/beam_extend_frames/2;
		} else if (beam[end_indx].orientation == 2){
			// moving in +y-direction
			beam[end_indx].loc.y = beam[end_indx].loc.y + desired_length/beam_extend_frames/2;
		} else if (beam[end_indx].orientation == 3){
			// moving in -y-direction
			beam[end_indx].loc.y = beam[end_indx].loc.y - desired_length/beam_extend_frames/2;
		} else if (beam[end_indx].orientation == 4){
			// moving in +z-direction
			beam[end_indx].loc.z = beam[end_indx].loc.z + desired_length/beam_extend_frames/2;
		} else if (beam[end_indx].orientation == 5){
			// moving in -z-direction
			beam[end_indx].loc.z = beam[end_indx].loc.z - desired_length/beam_extend_frames/2;
		}
	}

	public void resetGirder(boolean[][][] is_occupied, int level, int z_val, int num_beams_x, int num_beams_y,
					 float beam_side_len, float beam_side_width) {

		// INPUTS:
		// 		is_occupied - current boolean array defining occupied nodes in tower
		// 		level - z-value of where we'll look for open spots in is_occupied
		// 		z_val - corresponding z-value of level in 3D space (since level is constrained to be a small number)
		// 		num_beams_x - for determining possible initial points
		// 		num_beams_y - for determining possible initial points
		// 		beam_side_len - for reinitializing beam objects
		// 		beam_side_width - for reinitializing beam objects

		// temp variables
		boolean valid_indices = false; 
		int x = 0;						
		int y = 0;
		int z = level;
		// loop through until valid initial indices are found
		while (!valid_indices){
			x = (int) parent.random((float) num_beams_x);
			y = (int) parent.random((float) num_beams_y);
			if (!is_occupied[x][y][z]){
				valid_indices = true;
			}
		}
		// update reference to is_occupied
		is_occupied[x][y][z] = true;

		// don't need to fully reinitialize new tower.TowerGirder object, just reset key variables
		for (int i = 0; i < num_beams; i++){
			beam[i].side_len = beam_side_len+beam_side_width;
			beam[i].color = init_color;
		}
		curr_pos_x = x;
		curr_pos_y = y;
		curr_pos_z = z_val; // not the z variable from above; need the unmodded version
		curr_level = level;
		num_beams = 0;
		beg_indx = 0;
		end_indx = -1;
	}
}
