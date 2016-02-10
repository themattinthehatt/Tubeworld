/* core.Cam is a class used for updating camera dir, loc, sc and down using primitive camera movements.
The core.Cam object can then be used by the CamCtrl object or various core.Site objects
to facilitate quick development of custom preprogrammed camera behaviors.
*/

package core;
import processing.core.PVector;
import processing.core.PApplet;

public class Cam {

	public CamParam curr;    // current values for camera dir, loc, sc, and down
	public 	CamParam update;  // update values for camera dir, loc, sc, and down
	public float r;
	public float theta;
	public float phi;
	public float dr;   // update value for radius
	public float dtheta; // update value for theta
	public float dphi; // update value for phi
	public int fr_count;     // frame counter for more complicated movements
  
	// Constructor Method
	public Cam(CamParam curr_) {
		curr = curr_;
		update = new CamParam(new PVector(0,0,0),new PVector(0,0,0),new PVector(0,0,0),new PVector(0,0,0));
		fr_count = 0;
	}
  
	/* methods for camera manipulation
  	NOTE: core.CamParam object update should ONLY be used AFTER assigning it a value; never use values
  	fed into it.
	 */
  
	/******************************** ORIGIN: CURRENT LOCATION ********************************/
  
	/**************** DISPLACEMENTS **********************/
	public void moveForward(float dist){
		curr.dir.mult(dist);                                       // update distance moved
		curr.loc.add(curr.dir);                                    // move forward in dir direction
		curr.sc.add(curr.dir);                                     // update scene center
		curr.dir.normalize();                                      // return dir to unit vector
	}
	public void moveBackward(float dist){
		curr.dir.mult(dist);                                       // update distance moved
		curr.loc.sub(curr.dir);                                    // move backward in dir direction
		curr.sc.sub(curr.dir);                                     // update scene center
		curr.dir.normalize();                                      // return dir to unit vector
	}
	public void moveUpward(float dist){
		update.loc = PVector.mult(curr.down,dist);                 // update distance moved
		curr.loc.sub(update.loc);                                  // move upward in negative down direction
		curr.sc.sub(update.loc);                                   // update scene center
	}
	public void moveDownward(float dist){
		update.loc = PVector.mult(curr.down,dist);                 // update distance moved
		curr.loc.add(update.loc);                                  // move upward in negative down direction
		curr.sc.add(update.loc);                                   // update scene center
	}
	public void panLeft(float dist){
		update.dir = PVector.cross(curr.down,curr.dir,update.dir); // get vector pointing left
		update.dir.mult(dist);                                     // update distance moved
		curr.loc.add(update.dir);                                  // move sideways in update.dir direction
		curr.sc.add(update.dir);                                   // update scene center
	}
	public void panRight(float dist){
		update.dir = PVector.cross(curr.down,curr.dir,update.dir); // get vector pointing left
		update.dir.mult(dist);                                     // update distance moved
		curr.loc.sub(update.dir);                                  // move sideways opposite to update.dir direction
		curr.sc.sub(update.dir);                                   // update scene center
	}
	/***************** ROTATIONS ***********************/
	public void rotLeft(float rads){
		// update direction vector
		update.dir = PVector.cross(curr.down,curr.dir,update.dir); // get vector pointing left
		update.dir.mult(PApplet.tan(rads));                        // update rotation by rads
		curr.dir.add(update.dir);                                  // update direction vector
		curr.dir.normalize();                                      // return dir to unit vector
		
		// update scene center
		update.sc = PVector.sub(curr.sc,curr.loc,update.sc);       // get vector pointing from loc to sc for mag
		update.sc = PVector.mult(curr.dir,update.sc.mag(),update.sc); // get new vector pointing in curr.dir w proper mag
		curr.sc = PVector.add(curr.loc,update.sc,curr.sc);         // update scene center
		
		// no need to update down vector
	}
	public void rotRight(float rads){
		// update direction vector
		update.dir = PVector.cross(curr.down,curr.dir,update.dir); // get vector pointing left
		update.dir.mult(-PApplet.tan(rads));                       // update rotation by rads, make vector point right
		curr.dir.add(update.dir);                                  // update direction vector
		curr.dir.normalize();                                      // return dir to unit vector
		
		// update scene center
		update.sc = PVector.sub(curr.sc,curr.loc,update.sc);       // get vector pointing from loc to sc for mag
		update.sc = PVector.mult(curr.dir,update.sc.mag(),update.sc); // get new vector pointing in curr.dir w proper mag
		curr.sc = PVector.add(curr.loc,update.sc,curr.sc);         // update scene center
		
		// no need to update down vector
	}
	public void rotUp(float rads){
		// update direction vector
		update.dir = PVector.mult(curr.down,-1,update.dir);        // get vector pointing up 
		update.dir.mult(PApplet.tan(rads));                        // update rotation by rads
		update.down = PVector.mult(curr.dir,1,update.down);        // get vector pointing forward (need to do this bf curr.dir changes)
		curr.dir.add(update.dir);                                  // update direction vector
		curr.dir.normalize();                                      // return dir to unit vector
    
		// update scene center
		update.sc = PVector.sub(curr.sc,curr.loc,update.sc);       // get vector pointing from loc to sc for mag
		update.sc = PVector.mult(curr.dir,update.sc.mag(),update.sc); // get new vector pointing in curr.dir w proper mag
		curr.sc = PVector.add(curr.loc,update.sc,curr.sc);         // update scene center
		
		// update down vector
		update.down.mult(PApplet.tan(rads));                       // update rotation by rads
		curr.down.add(update.down);                                // update down vector 
		curr.down.normalize();                                     // return down to unit vector
	}
	public void rotDown(float rads){
		// update direction vector
		update.dir = PVector.mult(curr.down,1,update.dir);         // get vector pointing down 
		update.dir.mult(PApplet.tan(rads));                        // update rotation by rads
		update.down = PVector.mult(curr.dir,-1,update.down);       // get vector pointing backward (need to do this bf curr.dir changes)
		curr.dir.add(update.dir);                                  // update direction vector
		curr.dir.normalize();                                      // return dir to unit vector
    
		// update scene center
		update.sc = PVector.sub(curr.sc,curr.loc,update.sc);       // get vector pointing from loc to sc for mag
		update.sc = PVector.mult(curr.dir,update.sc.mag(),update.sc); // get new vector pointing in curr.dir w proper mag
		curr.sc = PVector.add(curr.loc,update.sc,curr.sc);         // update scene center
    
		// update down vector
		update.down.mult(PApplet.tan(rads));                       // update rotation by rads
		curr.down.add(update.down);                                // update down vector 
		curr.down.normalize();                                     // return down to unit vector
	}
	public void rotCCW(float rads){
		// no need to update direction vector
		// no need to update scene center
		// update down vector
		update.down = PVector.cross(curr.down,curr.dir,update.down); // leftward pointing vector
		update.down.mult(PApplet.tan(-rads));                        // updates rotation by rads, make vector point right
		curr.down.add(update.down);                                  // update direction vector
		curr.down.normalize();                                       // return down to unit vector
	}
	public void rotCW(float rads){
		// no need to update direction vector
		// no need to update scene center
		// update down vector
		update.down = PVector.cross(curr.down,curr.dir,update.down); // leftward pointing vector
		update.down.mult(PApplet.tan(rads));                         // updates rotation by rads
		curr.down.add(update.down);                                  // update direction vector
		curr.down.normalize();                                       // return down to unit vector
	}
  
  
  
  
	/******************************** ORIGIN: SITE CENTER *************************************/
	public void sphMoveRadius(PVector center, float dr_, String sc_update){
		/* sc_update: 
          	center - scene center is same as center
          	ahead - scene center is straight ahead, changes with dir vector
          	none - no change; scene center stays the same
		 */
		// update loc vector
		update.loc = PVector.sub(curr.loc,center,update.loc);  // vector away center
		update.loc.normalize();                                // make unit vector
		curr.loc.add(update.loc.mult(dr_));                    // add dr to curr loc to update curr loc
		switch(sc_update){
			case "center":
				// update sc vector
				curr.sc.set(center);
				// update dir vector
				curr.dir = PVector.sub(curr.sc,curr.loc,curr.dir);      // vector pointing to scene center
				curr.dir.normalize();                                    // return dir to unit vector  
				// no need to update down vector
			case "ahead":
				// update sc vector
				curr.sc.add(update.loc.mult(dr_));
				// update direction vector
				curr.dir = PVector.sub(curr.sc,curr.loc,curr.dir); // vector from loc to sc
				curr.dir.normalize();                              // return dir to unit vector
				// no need to update down vector
			case "none":
		}
    
	}
	public void sphSetRadius(PVector center, float r_, String sc_update){
		r = getRadius(center,curr.loc);
		sphMoveRadius(center,r_-r,sc_update);
	}
	public void sphMoveTheta(PVector center, float dtheta_, String sc_update){
		/* sc_update: 
          	center - scene center is same as center
          	ahead - scene center is straight ahead, changes with dir vector
          	none - no change; scene center stays the same
		 */
		// update location vector
		curr.loc = rotateTheta(center,curr.loc,dtheta_);        // rotate current location
		switch(sc_update){
			case "center":
				// update sc vector
				curr.sc.set(center);
				// update dir vector
				curr.dir = PVector.sub(curr.sc,curr.loc,curr.dir);      // vector pointing to scene center
				curr.dir.normalize();                                    // return dir to unit vector         
				// update down vector
				curr.down = rotateTheta(new PVector(0,0,0),curr.down,dtheta_); // rotate current down vec
				curr.down.normalize(); 
			case "ahead":
				// update sc vector
				curr.sc = rotateTheta(center,curr.sc,dtheta_);
				// update dir vector
				curr.dir = PVector.sub(curr.sc,curr.loc,curr.dir);      // vector pointing to scene center
				curr.dir.normalize();                                    // return dir to unit vector 
				// update down vector
				curr.down = rotateTheta(new PVector(0,0,0),curr.down,dtheta_); // rotate current down vec
				curr.down.normalize(); 
			case "none":
		}   
	}
	public void sphSetTheta(PVector center, float theta_, String sc_update){
		theta = getTheta(center,curr.loc);
		sphMoveTheta(center,theta_-theta,sc_update);
	}
	public void sphMovePhi(PVector center, float dphi_, String sc_update){
		/* sc_update: 
          	center - scene center is same as center
          	ahead - scene center is straight ahead, changes with dir vector
          	none - no change; scene center stays the same
		 */
		r = getRadius(center,curr.loc);                          // get radius                
		theta = getTheta(center,curr.loc);                       // get theta
		phi = getPhi(center,curr.loc);                           // get phi
		if (phi+dphi_ < 0 || phi+dphi_ > PApplet.PI){ // over the top or under the bottom
			phi = (phi + dphi_) % PApplet.PI;
			theta = (PApplet.PI + theta) % PApplet.TWO_PI;
		} else {
			phi = phi + dphi_;
		}      
		// update loc vector
		update.loc.set(r*PApplet.cos(theta)*PApplet.sin(phi),r*PApplet.sin(theta)*PApplet.sin(phi),r*PApplet.cos(phi));
		PVector change = new PVector(0,0,0);
		change = PVector.sub(PVector.add(center,update.loc),curr.loc,change);
		curr.loc = PVector.add(center,update.loc);
		switch(sc_update){
			case "center":
				// update sc vector
				curr.sc.set(center);
				// update dir vector
				curr.dir = PVector.sub(curr.sc,curr.loc,curr.dir);      // vector pointing to scene center
				curr.dir.normalize();                                    // return dir to unit vector    
				// update down vector
				curr.down = rotatePhi(new PVector(0,0,0),curr.down,dphi_);
				curr.down.normalize(); 
			case "ahead":
				// update sc vector
				curr.sc.add(change);
				// update dir vector
				curr.dir = PVector.sub(curr.sc,curr.loc,curr.dir);      // vector pointing to scene center
				curr.dir.normalize();                                    // return dir to unit vector    
				// update down vector
				curr.down = rotatePhi(new PVector(0,0,0),curr.down,dphi_);
				curr.down.normalize(); 
			case "none":
		}
	}
	public void sphSetPhi(PVector center, float phi_, String sc_update){
		phi = getPhi(center,curr.loc);
		sphMovePhi(center,phi_-phi,sc_update);
	} 

	/********************************* SMOOTH PURSUITS ****************************************/
	public int smoothLinPursuit(CamParam dest, float reset_frames, int call_state, int return_state){
		/* move from curr dir/loc/sc/down vector to dest dir/loc/sc/down vector in a linear manner.*/
  
		if (fr_count == 0){
			/* Calculate update vectors here, then move along them at regular increments for the 
       		duration of move, which will take reset_frames.
			*/
			update.loc = PVector.sub(dest.loc,curr.loc,update.loc); // vector pointing from curr to dest location
			update.loc.div(reset_frames);                   // update vector for location
       
			update.sc = PVector.sub(dest.sc,curr.sc,update.sc);     // vector pointing from curr to dest sc
			update.sc.div(reset_frames);                    // update vector for sc
       
			update.down = PVector.sub(dest.down,curr.down,update.down); // vector pointing from curr to dest down
			update.down.div(reset_frames);                  // update vector for down
		}
		if (fr_count < reset_frames-1){
			curr.loc.add(update.loc);                               // update location
			curr.sc.add(update.sc);                                 // update scene center
			curr.down.add(update.down);                             // update down vector
			fr_count++;                                             // increment frame counter
			return call_state;                                      // continue to return to call state
		} else if (fr_count == reset_frames-1){
			curr.setEqual(dest);                                    // set curr dir/loc/sc/down to desired destination values
			fr_count = 0;                                           // reset frame count
			return return_state;                                    // return state
		} else {
			return return_state;
		}
	}
	public int smoothSphPursuit(CamParam dest, PVector center, float reset_frames, int call_state, int return_state){
		/* move from curr dir/loc/sc/down vector to dest dir/loc/sc/down vector, scaled to be on the
    	surface of a sphere at a distance defined by dest, following motion in the positive theta direction
		 */
		if (fr_count == 0){
			/* Calculate update vectors here, then move along them at regular increments for the 
      		duration of move, which will take reset_frames.
			*/
			float r_init, r_fin;
			float theta_init, theta_fin;
			float phi_init, phi_fin;
			
			// get initial and final r, theta and phi values
			r_init = getRadius(center,curr.loc);
			r_fin = getRadius(center,dest.loc);
			theta_init = getTheta(center,curr.loc);
			theta_fin = getTheta(center,dest.loc);
			phi_init = getPhi(center,curr.loc);
			phi_fin = getPhi(center,dest.loc);
			// get increments to apply during update
			dr = (r_fin-r_init)/reset_frames;
			dtheta = (PApplet.TWO_PI-(theta_init-theta_fin))/reset_frames; 	// TWO_PI minus ensures it moves in positive theta dir
			dphi = (phi_fin-phi_init)/reset_frames;
			// get update vectors for sc and down
			update.sc = PVector.sub(dest.sc,curr.sc,update.sc);      		 // vector pointing from cur sc to center
			update.sc.div(reset_frames);                				     // resize for proper updating
			update.down = PVector.sub(dest.down,curr.down,update.down);      // vector pointing from cur down to dest down
			update.down.div(reset_frames);				                     // resize for proper updating
		}
		if (fr_count < reset_frames-1){
			sphMoveRadius(center,dr,"none");
			sphMoveTheta(center,dtheta,"none");
			sphMovePhi(center,dphi,"none");
			curr.sc.add(update.sc);                                 // update scene center
			curr.down.add(update.down);                             // update down vector
			fr_count++;                                             // increment frame counter
			return call_state;                                      // continue to return to call state
		} else if (fr_count == reset_frames-1) {
			curr.setEqual(dest);                                    // set curr dir/loc/sc/down to desired destination values
			fr_count = 0;                                           // reset frame count
			return return_state;                                    // return state
		} else {
			return return_state;
		}
	}
  
  
	/********************************* HELPER FUNCTIONS ****************************************/
	public float getRadius(PVector center, PVector vec){
		update.loc = PVector.sub(vec,center,update.loc);          // vector away center
		return update.loc.mag();
	}
	public float getTheta(PVector center, PVector vec){
		update.loc = PVector.sub(vec,center,update.loc);          // vector away center
		if (update.loc.x != 0){
			theta = PApplet.atan(update.loc.y/update.loc.x);    
			// atan goes between -PI/2 and PI/2
			if (update.loc.x < 0 && update.loc.y > 0) {
				theta = PApplet.PI + theta;     // theta negative here
			} else if (update.loc.x < 0 && update.loc.y < 0) {
				theta = PApplet.PI + theta;     // theta positive here
			} else if (update.loc.x > 0 && update.loc.y < 0) {
				theta = PApplet.TWO_PI + theta; // theta negative here
			}
		} else if (update.loc.y > 0) {
			theta = PApplet.PI/2;
		} else if (update.loc.y < 0) {
			theta = 3*PApplet.PI/2;
		} else {
			theta = 0;
		}
		return theta;
	}
	public float getPhi(PVector center, PVector vec){
		update.loc = PVector.sub(vec,center,update.loc);          // vector away center
		r = update.loc.mag();                                     // get radius
		if (r != 0){
			// acos goes between 0 to PI
			return PApplet.acos(update.loc.z/r);                            
		} else {
			return 0;
		}
	}
	public PVector rotateTheta(PVector center, PVector vec, float theta_){
		PVector temp = new PVector(0,0,0);
		temp = PVector.sub(vec,center,temp);                      // vector pointing away from center
		temp.set(temp.x*PApplet.cos(theta_)-temp.y*PApplet.sin(theta_),
				 temp.x*PApplet.sin(theta_)+temp.y*PApplet.cos(theta_),
				 temp.z);                                         // multiply loc update vec by rotation matrix
		vec = PVector.add(center,temp);                           // add rotated vector back to center
		return vec;
	}
	public PVector rotatePhi(PVector center, PVector vec, float phi_){
		PVector temp = new PVector(0,0,0);
		temp = PVector.sub(vec,center,temp);                      // vector pointing away from center
		float theta_ = getTheta(center, vec);
		// rotate by negative theta to get vector in xz plane, rotate by phi in xz plane, then rotate by theta
		temp = rotateTheta(new PVector(0,0,0),temp,-theta_);
		temp.set(temp.x*PApplet.cos(phi_)-temp.z*PApplet.sin(phi_),
				 temp.y,
				 temp.x*PApplet.sin(phi_)+temp.z*PApplet.cos(phi_));  
		vec = rotateTheta(new PVector(0,0,0),temp,theta_);
		return vec;
	}
  
}  

