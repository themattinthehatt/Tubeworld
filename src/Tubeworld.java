import processing.core.*;

/* TODO
 * fix presets on Menger Sponge
 * start building tower!!
 * make F_ presets to move to specified sites
 */

public class Tubeworld extends PApplet {

	KeyHandler key_handler;
	CamParam cam_init;
	CamCtrl cam_ctrl;
	Site[] sites;
	int num_sites;
	float[] dist_to_site;
	float min_dist;
	int active_site_indx;
	
	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "Tubeworld" });
//		PApplet.main(new String[] { "Tubeworld" });

	}
	
	public void settings() {
		  size(800, 600, "processing.opengl.PGraphics3D");
//		size(800,600,"processing.opengl.OPENGL");
	}
	
	public void setup(){
		
		frameRate(40);
//		frame.setBackground(new java.awt.Color(0, 0, 0));
		key_handler = new KeyHandler(this);
		num_sites = 5;
		sites = new Site[num_sites];
		dist_to_site = new float[num_sites];
		active_site_indx = 0;
		  
		 /* PVector center;          // center of site
		  float rad_site;            // approximate radius of site
		  float rad_inf;             // radius of influence of site
		  CamParam init;             // initial camera dir,loc,sc and down for camera presets
		    PVector dir;             // xyz coordinates of direction vector
		    PVector loc;             // xyz coordinates of camera
		    PVector sc;              // xyz coordinates of scene center
		    PVector down;            // xyz coordinates of downward direction of camera
		  int reset_frames;          // number of frames for resetting camera loc when inside radius of influence
		 */
		  sites[0] = new NightWorld(this,new PVector(0,0,0), 5000, 100000, 
		                             new CamParam(new PVector(-1,0,0),new PVector(600,0,0),new PVector(0,0,0),new PVector(0,0,-1)), 120);
		  sites[1] = new GlassCube(this,new PVector(1000,1000,0), 400, 10000, 
		                             new CamParam(new PVector(-1,0,0),new PVector(1600,1000,0),new PVector(1000,1000,0),new PVector(0,0,-1)), 120);
		  sites[2] = new RGBHallway(this,new PVector(-1000,-1000,0), 400, 10000, 
		                             new CamParam(new PVector(-1,0,0),new PVector(-1000,-1000,0),new PVector(-1200,-1000,0),new PVector(0,0,-1)), 120);
		  sites[3] = new MengerSponge(this,new PVector(-2000,2000,0), 500, 10000,
				  					 new CamParam(new PVector(0,1,0),new PVector(-1730,1500,270),new PVector(-1730,2000,270),new PVector(0,0,-1)), 120);
		  sites[4] = new Tower(this,new PVector(2500,-2500,0), 500, 10000,
					 				 new CamParam(new PVector(0,0,-1),new PVector(2650,-2350,400),new PVector(2650,-2350,0),new PVector(0,-1,0)), 120);
		  
		  // initialize camera object
		  cam_ctrl = new CamCtrl(this,new CamParam(new PVector(0,0,-1),new PVector(2650,-2350,400),new PVector(2650,-2350,0),new PVector(0,-1,0)));
		  
	}

	public void draw(){

		// note: lights have to be called before drawing takes place
		cam_ctrl.update(key_handler.keys_pressed,sites[active_site_indx]);
				
		// find closest site	
		dist_to_site[0] = 0;	// ensures we're always within radius of influence of tubeworld
		min_dist = 100000;		// initialize to large value
		for (int i = 1; i < num_sites; i++){
			dist_to_site[i] = cam_ctrl.cam.curr.loc.dist(sites[i].center);
			// update minimum distance
			if (dist_to_site[i] < min_dist){
				min_dist = dist_to_site[i];
				active_site_indx = i;
			}
		} 

		// render sites only if camera is within their radius of influence
		for (int i = 0; i < num_sites; i++){
			if (dist_to_site[i] < sites[i].rad_inf){
				sites[i].updatePhysics(key_handler.keys_pressed,key_handler.keys_toggled);
				sites[i].drawSite();
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
