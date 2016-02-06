package tower;

import processing.core.PVector;

public class TowerBeam {
	
	public float side_width;	// distance between node centers in physical space + overlap of nodes; for drawing
	public float side_len;		// "circumference" of beam
	public int orientation; 	// 1 for long axis along x-axis, 2 for y-axis, 3 for z-axis
	public int update_dir;		// helps with updating expansion of newly created beams
	public PVector loc;
	public int color;
	
	public TowerBeam(float side_width_,float side_len_,int orientation_,PVector loc_,int color_){
		side_width = side_width_;
		side_len = side_len_+side_width; // beam will overlap nodes by half a side_width on each end
		orientation = orientation_;
		loc = loc_;
		color = color_;
	}
}
