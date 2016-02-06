/* core.Site is an abstract class that sets up the fundamental properties and methods that every
core.Site class should implement*/

package core;
import processing.core.PApplet;
import processing.core.PVector;

public class Site {
	
	public PApplet parent;			 // PApplet parent
	public PVector center;          // center of site
	public float rad_site;          // approximate radius of site
	public float rad_inf;           // radius of influence of site
	public CamParam init;           // initial camera dir,loc,sc and down for camera presets
	public int reset_frames;        // number of frames for resetting camera loc when inside radius of influence
	
	public Site(PApplet parent_, PVector center_, float rad_site_, float rad_inf_, CamParam init_, int reset_frames_){
		parent = parent_;
		center = center_;
		rad_site = rad_site_;
		rad_inf = rad_inf_;
		init = init_;
		reset_frames = reset_frames_;
	}
  
	/************************************ UPDATE PHYSICS ************************************/
	public void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled){
	}
  
	/************************************ DRAW SITE *****************************************/
	public void drawSite(){
	}
  
	/************************************ UPDATE CAMERA *************************************/
	public int updateCam(Cam cam, int state, boolean[] keys_pressed){
		return state;
	}
  
}
