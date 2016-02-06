/* core.CamParam class is used to store camera direction, location, scene center and down axis
for use with Processing's camera command. core.CamParam objects are used by the CamMove and
CamCtrl classes to facilitate the manipulation of these quantities during each frame by 
methods in the CamMove class
*/

package core;
import processing.core.PVector;

public class CamParam {
  
	// properties
	public PVector dir;             // xyz coordinates of direction vector
	public PVector loc;             // xyz coordinates of camera
	public PVector sc;              // xyz coordinates of scene center
	public PVector down;            // xyz coordinates of downward direction of camera
  
	// Constructor method
	public CamParam(PVector dir_, PVector loc_, PVector sc_, PVector down_) {
		dir = dir_;
		loc = loc_;
		sc = sc_;
		down = down_; 
	}
  
	// setEqual
	public void setEqual(CamParam new_cam_param){
		dir.set(new_cam_param.dir);
		loc.set(new_cam_param.loc);
		sc.set(new_cam_param.sc);
		down.set(new_cam_param.down);
	}
}
