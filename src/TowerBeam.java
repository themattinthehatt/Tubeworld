import processing.core.PVector;

public class TowerBeam {
	
	float side_width;	// distance between node centers in physical space + overlap of nodes; for drawing
	float side_len;		// "circumference" of beam
	int orientation; 	// 1 for long axis along x-axis, 2 for y-axis, 3 for z-axis
	int update_dir;		// helps with updating expansion of newly created beams
	PVector loc;
	int color;
	
	TowerBeam(float side_width_,float side_len_,int orientation_,PVector loc_,int color_){
		side_width = side_width_;
		side_len = side_len_+side_width; // beam will overlap nodes by half a side_width on each end
		orientation = orientation_;
		loc = loc_;
		color = color_;
	}
}
