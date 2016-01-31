/* Site is an abstract class that sets up the fundamental properties and methods that every
Site class should implement*/

import processing.core.PApplet;
import processing.core.PVector;

public class Site {
	
	PApplet parent;			 // PApplet parent
	PVector center;          // center of site
	float rad_site;          // approximate radius of site
	float rad_inf;           // radius of influence of site
	CamParam init;           // initial camera dir,loc,sc and down for camera presets
	int reset_frames;        // number of frames for resetting camera loc when inside radius of influence
	
	Site(PApplet parent_, PVector center_, float rad_site_, float rad_inf_, CamParam init_, int reset_frames_){
		parent = parent_;
		center = center_;
		rad_site = rad_site_;
		rad_inf = rad_inf_;
		init = init_;
		reset_frames = reset_frames_;
	}
  
	/************************************ UPDATE PHYSICS ************************************/
	void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled){
	}
  
	/************************************ DRAW SITE *****************************************/
	void drawSite(){
	}
  
	/************************************ UPDATE CAMERA *************************************/
	int updateCam(Cam cam, int state, boolean[] keys_pressed){
		return state;
	}
  
}
