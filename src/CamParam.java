/* CamParam class is used to store camera direction, location, scene center and down axis
for use with Processing's camera command. CamParam objects are used by the CamMove and 
CamCtrl classes to facilitate the manipulation of these quantities during each frame by 
methods in the CamMove class
*/
import processing.core.PVector;

public class CamParam {
  
	// properties
	PVector dir;             // xyz coordinates of direction vector
	PVector loc;             // xyz coordinates of camera
	PVector sc;              // xyz coordinates of scene center
	PVector down;            // xyz coordinates of downward direction of camera
  
	// Constructor method
	CamParam(PVector dir_, PVector loc_, PVector sc_, PVector down_) {
		dir = dir_;
		loc = loc_;
		sc = sc_;
		down = down_; 
	}
  
	// setEqual
	void setEqual(CamParam new_cam_param){
		dir.set(new_cam_param.dir);
		loc.set(new_cam_param.loc);
		sc.set(new_cam_param.sc);
		down.set(new_cam_param.down);
	}
}
