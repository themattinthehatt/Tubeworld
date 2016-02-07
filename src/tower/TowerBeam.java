package tower;

import processing.core.PVector;

public class TowerBeam {

	public float side_width;	// "circumference" of beam
	public float side_len;		// distance between node centers in physical space + width of single node (overlap of nodes); for drawing
	public int orientation; 	// 0 for updating along +x-axis, 1 for -x-axis, 2 for +y-axis, 3 for -y-axis, 4 for +z-axis
	public PVector loc; 		// location of center of beam
	public int color;			// color of beam
	
	public TowerBeam(float side_width_,float side_len_,int orientation_,PVector loc_,int color_){
		side_width = side_width_;
		side_len = side_len_+side_width; // beam will overlap nodes by half a side_width on each end
		orientation = orientation_;
		loc = loc_;
		color = color_;
	}
}
