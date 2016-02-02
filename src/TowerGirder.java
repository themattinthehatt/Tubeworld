import processing.core.PApplet;
import processing.core.PVector;

public class TowerGirder {
	
	PApplet parent; 	// parent
	int curr_pos_x; 	// position of current beam object in int space
	int curr_pos_y; 	// position of current beam object in int space
	int curr_pos_z; 	// position of current beam object in int space
	int curr_level;		// level in logic array (modded version of curr_pos_z)
	int max_num_beams;	// maximum number of beams; preallocate
	int num_beams;		// current number of beams
	int beg_indx;		// beginning index for beam (treat as queue)
	int end_indx;		// ending index for beam (treat as queue)
	int init_color;		// initial color specification
	TowerBeam[] beam;	// array of beam objects to create girder

	/************************************ CONSTRUCTOR **************************************/
	TowerGirder(PApplet parent_,int curr_pos_x_,int curr_pos_y_,int curr_pos_z_,int init_color_,float side_width_,float side_len_){
		// initialize from inputs
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
		// initialize TowerBeam objects
		max_num_beams = 128; // maybe make this dependent on Tower size?
		beam = new TowerBeam[max_num_beams];	
		for (int i = 0; i < max_num_beams; i++) {
			beam[i] = new TowerBeam(side_width_,side_len_,0,new PVector(0,0,0),init_color_);
		}
	}
	
	void drawGirder(){
		// iterate through beams
		for (int i = 0; i < num_beams; i++){
			parent.pushMatrix();
			parent.translate(beam[i].loc.x,beam[i].loc.y,beam[i].loc.z);
			parent.stroke(beam[i].color);
			parent.fill(beam[i].color);
			// draw box along different axes depending on orientation value
			if (beam[i].orientation == 0){
				parent.box(beam[i].side_len,beam[i].side_width,beam[i].side_width);
			} else if (beam[i].orientation == 1){
				parent.box(beam[i].side_width,beam[i].side_len,beam[i].side_width);
			} else if (beam[i].orientation == 2){
				parent.box(beam[i].side_width,beam[i].side_width,beam[i].side_len);
			}
			parent.popMatrix();
		}
	}
	
	void addBeam(int update_dir){
		
		// add beam to end of beam array queue
		end_indx = (end_indx+1) % max_num_beams;
		// update number of beams
		if (num_beams < max_num_beams){
			num_beams++;
		}
		
		// translate from update_dir to orientation
		if (update_dir == 0 || update_dir == 1) {
			beam[end_indx].orientation = 0;		// long axis of beam in x-direction
		} else if (update_dir == 2 || update_dir == 3) {
			beam[end_indx].orientation = 1;		// long axis of beam in y-direction
		} else if (update_dir == 4) {
			beam[end_indx].orientation = 2;		// long axis of beam in z-direction
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
		
		// store update direction
		beam[end_indx].update_dir = update_dir;		
		
		// update colors
		beam[end_indx].color = init_color;

	}
	
	void updateBeam(float beam_extend_frames, float desired_length){
		// update length of beam while expanding; direction depends on orientation
		
		/* add proportion of desired length that depends on number of frames used to extend beam;
		 * note that initial beam side_len is set to side_width, so this extra length needed for 
		 * drawing is already taken care of
		 */
		beam[end_indx].side_len = beam[end_indx].side_len + desired_length/beam_extend_frames;
		
		// location will be halfway between nodes
		if (beam[end_indx].update_dir == 0){
			// moving in +x-direction
			beam[end_indx].loc.x = beam[end_indx].loc.x + desired_length/beam_extend_frames/2;
		} else if (beam[end_indx].update_dir == 1){
			// moving in -x-direction
			beam[end_indx].loc.x = beam[end_indx].loc.x - desired_length/beam_extend_frames/2;
		} else if (beam[end_indx].update_dir == 2){
			// moving in +y-direction
			beam[end_indx].loc.y = beam[end_indx].loc.y + desired_length/beam_extend_frames/2;
		} else if (beam[end_indx].update_dir == 3){
			// moving in -y-direction
			beam[end_indx].loc.y = beam[end_indx].loc.y - desired_length/beam_extend_frames/2;
		} else if (beam[end_indx].update_dir == 4){
			// moving in +z-direction
			beam[end_indx].loc.z = beam[end_indx].loc.z + desired_length/beam_extend_frames/2;
		}
	}

	void resetGirder(boolean[][][] is_occupied, int level, int z_val, int num_beams_x, int num_beams_y,
					 float beam_side_len, float beam_side_width) {
		
		// level is where we'll look for open spots in is_occupied
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
		valid_indices = false;	// reset
		
		// don't need to fully reinitialize new TowerGirder object, just reset key variables
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
