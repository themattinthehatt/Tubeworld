import processing.core.*;

import core.CamCtrl;
import core.CamParam;
import core.KeyHandler;
import core.Site;

import nightworld.NightWorld;
import rgbhallway.RGBHallway;
import glasscube.GlassCube;
import mengersponge.MengerSponge;
import tower.Tower;
import metatower.MetaTower;
import recursivetower.RecursiveTower;
import colorcascade.ColorCascade;

import java.awt.event.KeyEvent;

/* TODO
 * put site presets and skybox options in own class
 */

public class Tubeworld extends PApplet {

	// camera properties
	public KeyHandler key_handler;
	public CamParam cam_init;
	public CamCtrl cam_ctrl;

	// site properties
	public Site[] sites;
	public int num_sites;
	public float[] dist_to_site;
	public float min_dist;
	public int active_site_indx;
	
	public static void main(String[] args) {
//		PApplet.main(new String[] { "--present", "Tubeworld" });
		PApplet.main(new String[] { "Tubeworld" });

	}
	
	public void settings() {
		  size(1000, 800, "processing.opengl.PGraphics3D");
//		size(800,600,"processing.opengl.OPENGL");
	}
	
	public void setup(){
		
		frameRate(40);
//		frame.setBackground(new java.awt.Color(0, 0, 0));
		key_handler = new KeyHandler(this);

		// define which sites to render
		boolean rgbhallway = true;
		boolean glasscube = true;
		boolean mengersponge = false;
		boolean tower = false;
		boolean metatower = false;
		boolean recursivetower = true;
		boolean colorcascade = true;
		String start_site = "colorcascade";

		// common input to constructors if desired
		float render_radius = 10000; 	// distance beyond which site is not rendered
		float reset_frames = 60;		// number of frames needed for reset (0 key press)

		// initialize other variables
		num_sites = 8;
		sites = new Site[num_sites];
		dist_to_site = new float[num_sites];
		active_site_indx = 0;
		cam_init = new CamParam(new PVector(0,0,0),new PVector(0,0,0),new PVector(0,0,0),new PVector(0,0,0));

		// define which sites to build
		/* All sites need the following variables for their constructors:
		PVector origin;          	// origin of site
		float render_radius; 		// site will be rendered when camera is within this distance from origin
		core.CamParam init;         // initial camera dir,loc,sc and down for camera presets
		  PVector dir;             	// xyz coordinates of direction vector
		  PVector loc;             	// xyz coordinates of camera
		  PVector sc;              	// xyz coordinates of scene center
		  PVector down;            	// xyz coordinates of downward direction of camera
		int reset_frames;          	// number of frames for resetting camera loc when inside render radius
		 */

		int site_indx = 1;
		// NightWorld should always be rendered
		sites[0] = new NightWorld(this,new PVector(0,0,0), 100000,
					new CamParam(new PVector(-1,0,0),new PVector(600,0,0),new PVector(0,0,0),new PVector(0,0,-1)), reset_frames);
		// loop through remaining sites
		if (rgbhallway) {
			sites[site_indx] = new RGBHallway(this, new PVector(-1000, -1000, 0), render_radius,
					new CamParam(new PVector(-1, 0, 0), new PVector(-1000, -1000, 0), new PVector(-1200, -1000, 0), new PVector(0, 0, -1)), reset_frames);
			site_indx++;
			if (start_site.equals("rgbhallway")){
				cam_init.dir = new PVector(-1,0,0);
				cam_init.loc = new PVector(-1000,-1000,0);
				cam_init.sc = new PVector(-1200,-1000,0);
				cam_init.down = new PVector(0,0,-1);
			}
		}
		if (glasscube){
			sites[site_indx] = new GlassCube(this,new PVector(1000,1000,0), render_radius,
					new CamParam(new PVector(-1,0,0),new PVector(1600,1000,0),new PVector(1000,1000,0),new PVector(0,0,-1)), reset_frames);
			site_indx++;
			if (start_site.equals("glasscube")){
				cam_init.dir = new PVector(-1,0,0);
				cam_init.loc = new PVector(1600,1000,0);
				cam_init.sc = new PVector(1000,1000,0);
				cam_init.down = new PVector(0,0,-1);
			}
		}
		if (mengersponge){
			sites[site_indx] = new MengerSponge(this,new PVector(-2000,2000,0), render_radius,
					new CamParam(new PVector(0,1,0),new PVector(-1730,1500,270),new PVector(-1730,2000,270),new PVector(0,0,-1)), reset_frames);
			site_indx++;
			if (start_site.equals("mengersponge")){
				cam_init.dir = new PVector(0,1,0);
				cam_init.loc = new PVector(-1730,1500,270);
				cam_init.sc = new PVector(-1730,2000,270);
				cam_init.down = new PVector(0,0,-1);
			}
		}
		if (tower){
			sites[site_indx] = new Tower(this,new PVector(2500,-2500,0), render_radius,
					new CamParam(new PVector(0,0,-1),new PVector(2650,-2350,400),new PVector(2650,-2350,0),new PVector(0,-1,0)), reset_frames);
			site_indx++;
			if (start_site.equals("tower")){
				cam_init.dir = new PVector(0,0,-1);
				cam_init.loc = new PVector(2650,-2350,400);
				cam_init.sc = new PVector(2650,-2350,0);
				cam_init.down = new PVector(0,-1,0);
			}
		}
		if (metatower){
			sites[site_indx] = new MetaTower(this,new PVector(5000,5000,0), render_radius,
					new CamParam(new PVector(0,0,-1),new PVector(6125,6125,1000),new PVector(6125,6125,0),new PVector(0,-1,0)), reset_frames);
			site_indx++;
			if (start_site.equals("metatower")){
				cam_init.dir = new PVector(0,1,0);
				cam_init.loc = new PVector(6125,6125,300);
				cam_init.sc  = new PVector(6125,6125,300);
				cam_init.down = new PVector(0,0,-1);
			}
		}
		if (recursivetower){
			sites[site_indx] = new RecursiveTower(this,new PVector(5000,5000,0), render_radius,
					new CamParam(new PVector(0,0,-1),new PVector(6125,6125,1000),new PVector(6125,6125,0),new PVector(0,-1,0)), reset_frames);
					// set up recursion
					((RecursiveTower) sites[site_indx]).reinitializeLinkType("RecursiveTower",2,2,5,1,96,0);
			site_indx++;
			if (start_site.equals("recursivetower")){
				cam_init.dir = new PVector(0,0,-1);
				cam_init.loc = new PVector(6125,6125,1000);
				cam_init.sc  = new PVector(6125,6125,0);
				cam_init.down = new PVector(0,-1,0);
			}
		}
		if (colorcascade){
			sites[site_indx] = new ColorCascade(this,new PVector(-5000,3000,0), render_radius,
					new CamParam(new PVector(-1,0,0),new PVector(-4000,3000,1000),new PVector(-5000,3000,1000),new PVector(0,0,-1)), reset_frames);
			site_indx++;
			if (start_site.equals("colorcascade")){
				cam_init.dir = new PVector(-1,0,0);
				cam_init.loc = new PVector(-4000,3000,1000);
				cam_init.sc  = new PVector(-5000,3000,1000);
				cam_init.down = new PVector(0,0,-1);
			}
		}

		num_sites = site_indx; 					// redefine num_sites to accurately reflect number of sites rendered
		cam_ctrl = new CamCtrl(this,cam_init); 	// define initial camera site

	}

	public void draw(){

		// note: lights have to be called before drawing takes place

		cam_ctrl.update(key_handler.keys_pressed,key_handler.keys_toggled,sites[active_site_indx]);
		// push back far end of viewing plane
//		perspective(PApplet.PI/3,1.77777777778f,1,10000);
		perspective(PApplet.PI/3,1.01f,1,10000);

		// find closest site
		min_dist = 100000;		// initialize to large value
		for (int i = 0; i < num_sites; i++){
			dist_to_site[i] = cam_ctrl.cam.curr.loc.dist(sites[i].origin);
			// update minimum distance
			if (dist_to_site[i] < min_dist){
				min_dist = dist_to_site[i];
				active_site_indx = i;
			}
		} 

		// render sites only if camera is within their radius of influence
		for (int i = 0; i < num_sites; i++){
			if (dist_to_site[i] < sites[i].render_radius){
				sites[i].updatePhysics(key_handler.keys_pressed,key_handler.keys_toggled);
				sites[i].drawSite();
			}
		}

		// return to site origins if shift+(0-9) key combo is pressed
		if (key_handler.keys_pressed[KeyEvent.VK_SHIFT] && key_handler.keys_pressed[KeyEvent.VK_0]){
			if (num_sites >= 1) {
				cam_ctrl.state = 0;		// set state so that we do a smooth linear movement to init
				active_site_indx = 0;	// set which site to reset to
			}
		} else if (key_handler.keys_pressed[KeyEvent.VK_SHIFT] && key_handler.keys_pressed[KeyEvent.VK_1]){
			if (num_sites >= 2) {
				cam_ctrl.state = 0;		// set state so that we do a smooth linear movement to init
				active_site_indx = 1;	// set which site to reset to
			}
		} else if (key_handler.keys_pressed[KeyEvent.VK_SHIFT] && key_handler.keys_pressed[KeyEvent.VK_2]){
			if (num_sites >= 3) {
				cam_ctrl.state = 0;		// set state so that we do a smooth linear movement to init
				active_site_indx = 2;	// set which site to reset to
			}
		} else if (key_handler.keys_pressed[KeyEvent.VK_SHIFT] && key_handler.keys_pressed[KeyEvent.VK_3]){
			if (num_sites >= 4) {
				cam_ctrl.state = 0;		// set state so that we do a smooth linear movement to init
				active_site_indx = 3;	// set which site to reset to
			}
		} else if (key_handler.keys_pressed[KeyEvent.VK_SHIFT] && key_handler.keys_pressed[KeyEvent.VK_4]){
			if (num_sites >= 5) {
				cam_ctrl.state = 0;		// set state so that we do a smooth linear movement to init
				active_site_indx = 4;	// set which site to reset to
			}
		} else if (key_handler.keys_pressed[KeyEvent.VK_SHIFT] && key_handler.keys_pressed[KeyEvent.VK_5]){
			if (num_sites >= 6) {
				cam_ctrl.state = 0;		// set state so that we do a smooth linear movement to init
				active_site_indx = 5;	// set which site to reset to
			}
		} else if (key_handler.keys_pressed[KeyEvent.VK_SHIFT] && key_handler.keys_pressed[KeyEvent.VK_6]){
			if (num_sites >= 7) {
				cam_ctrl.state = 0;		// set state so that we do a smooth linear movement to init
				active_site_indx = 6;	// set which site to reset to
			}
		} else if (key_handler.keys_pressed[KeyEvent.VK_SHIFT] && key_handler.keys_pressed[KeyEvent.VK_7]){
			if (num_sites >= 8) {
				cam_ctrl.state = 0;		// set state so that we do a smooth linear movement to init
				active_site_indx = 7;	// set which site to reset to
			}
		} else if (key_handler.keys_pressed[KeyEvent.VK_SHIFT] && key_handler.keys_pressed[KeyEvent.VK_8]){
			if (num_sites >= 9) {
				cam_ctrl.state = 0;		// set state so that we do a smooth linear movement to init
				active_site_indx = 8;	// set which site to reset to
			}
		} else if (key_handler.keys_pressed[KeyEvent.VK_SHIFT] && key_handler.keys_pressed[KeyEvent.VK_9]){
			if (num_sites >= 10) {
				cam_ctrl.state = 0;		// set state so that we do a smooth linear movement to init
				active_site_indx = 9;	// set which site to reset to
			}
		}
	}

	// for updating key events
	public void keyPressed(){
		key_handler.key_pressed();
	}
	
	public void keyReleased(){
		key_handler.key_released();
	}

} // end Tubeworld class
