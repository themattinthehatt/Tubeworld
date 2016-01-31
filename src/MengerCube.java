import processing.core.PApplet;
import processing.core.PVector;

public class MengerCube {
	
	PApplet parent;
	float side_len;
	PVector loc;
	int cube_color;
	
	MengerCube(PApplet parent_,float side_len_,PVector loc_,int cube_color_){
		parent = parent_;
		side_len = side_len_;
		loc = loc_;
		cube_color  = cube_color_;
	}

}
