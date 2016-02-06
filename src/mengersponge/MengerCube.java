package mengersponge;

import processing.core.PApplet;
import processing.core.PVector;

public class MengerCube {

	public PApplet parent;
	public float side_len;
	public PVector loc;
	public int cube_color;

	public MengerCube(PApplet parent_,float side_len_,PVector loc_,int cube_color_){
		parent = parent_;
		side_len = side_len_;
		loc = loc_;
		cube_color  = cube_color_;
	}

}
