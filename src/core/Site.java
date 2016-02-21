/* core.Site is an abstract class that sets up the fundamental properties and methods that every
core.Site class should implement*/

package core;

import processing.core.PApplet;
import processing.core.PVector;

public abstract class Site {
	
	public PApplet parent;			// PApplet parent
	public PVector origin;          // origin of site
	public float render_radius;     // site will be rendered when camera is within this distance from origin
	public CamParam init;           // initial camera dir,loc,sc and down for camera presets
	public float reset_frames;      // number of frames for resetting camera loc when inside render_radius
	
	public Site(PApplet parent_, PVector origin_, float render_radius_, CamParam init_, float reset_frames_){
		parent = parent_;
		origin = origin_;
		render_radius = render_radius_;
		init = init_;
		reset_frames = reset_frames_;
	}
  
	/************************************ UPDATE PHYSICS ************************************/
	public abstract void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled);
  
	/************************************ DRAW SITE *****************************************/
	public abstract void drawSite();

	/************************************ UPDATE CAMERA *************************************/
	public abstract int updateCam(Cam cam, int state, boolean[] keys_pressed, boolean[] keys_toggled);
  
}
