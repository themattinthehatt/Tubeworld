// Cube class; used as objects in the glasscube.GlassCube class

package glasscube;
import processing.core.PApplet;
import processing.core.PVector;

public class ShimmerCube {
  
	// PApplet parent
	public PApplet parent;
	
	// size of cube
	public PVector bounds;
	public float half_side_len;
	public float mass;
  
	// location and velocity of cube, shift displacements for shimmer
	public PVector loc;
	public PVector vel;
	public ShimmerCubeComponent rgb;
  
	// for collison detection
	public PVector temp;
	public float temp1;
	public float temp2;
	public float mass_fac;
	public float r;
	public float theta;
	public float phi;
 
	/************************************ CONSTRUCTOR ************************************/
	public ShimmerCube(PApplet parent_, float bound_side_len, float side_len_){
    
		// parent PApplet
		parent = parent_;
		
		// size of boundary cube
		bounds = new PVector(bound_side_len/2,bound_side_len/2,bound_side_len/2);
    
		// size of cube
		half_side_len = side_len_/2;
		mass = PApplet.pow(side_len_,3);
    
		// set random initial location
		loc = new PVector(parent.random(-bounds.x+half_side_len,bounds.x-half_side_len),
						  parent.random(-bounds.x+half_side_len,bounds.x-half_side_len),
						  parent.random(-bounds.x+half_side_len,bounds.x-half_side_len)); // random initial position within boundary cube
		// set random initial velocity
		vel = new PVector(parent.random((float)-0.5,(float)0.5),parent.random((float)-0.5,(float)0.5),parent.random((float)-0.5,(float)0.5));
		//vel = new PVector(PApplet.random(-2,2),PApplet.random(-2,2),PApplet.random(-2,2));

		// set rgb components
		rgb = new ShimmerCubeComponent(parent,loc,half_side_len);
    
	}
  
	/************************************ UPDATE  *******************************************/
	public void updatePos(){
		// just move white square; colored squares will update in relation to this in shimmer
		loc.add(vel);
	}
  
	/************************************ BOUNDARY DETECTION ********************************/
	public void detectBoundaries(){
   
		// check sides after updating
		if (loc.x+half_side_len > bounds.x){
			loc.x = bounds.x-half_side_len; // reset position
			vel.x = -vel.x;              // update direction
			rgb.updateShifts();           // update shimmer info
		} else if (loc.x-half_side_len < -bounds.x){
			loc.x = -bounds.x+half_side_len;   // reset position
			vel.x = -vel.x;         // update direction
			rgb.updateShifts();        // update shimmer info
		}
   
		if (loc.y+half_side_len > bounds.y){
			loc.y = bounds.y-half_side_len; // reset position
			vel.y = -vel.y;              // update direction
			rgb.updateShifts();        // update shimmer info
		} else if (loc.y-half_side_len < -bounds.y){
			loc.y = -bounds.y+half_side_len;   // reset position
			vel.y = -vel.y;         // update direction
			rgb.updateShifts();        // update shimmer info
		}
		
		if (loc.z+half_side_len > bounds.z){
			loc.z = bounds.z-half_side_len; // reset position
			vel.z = -vel.z;              // update direction
			rgb.updateShifts();        // update shimmer info
		} else if (loc.z-half_side_len < -bounds.z){
			loc.z = -bounds.z+half_side_len;   // reset position
			vel.z = -vel.z;         // update direction
			rgb.updateShifts();        // update shimmer info
		}
	}

  
	/************************************ SHIMMER *******************************************/
	public void shimmer(){
		rgb.shimmer();
	}
  
	/************************************ DRAW CUBE *****************************************/
	public void drawCube(){
		parent.pushMatrix();
		parent.translate(loc.x,loc.y,loc.z);
		rgb.drawCubes();
		parent.popMatrix();
	}
  
	/************************************ HELPER FUNCTIONS **********************************/
	public void getTheta(PVector vec){
		temp = PVector.sub(vec,loc,temp);          // vector away center
		if (temp.x != 0){
			theta = PApplet.atan(temp.y/temp.x);    
			// atan goes between -PApplet.PI/2 and PApplet.PI/2
			if (temp.x < 0 && temp.y > 0) {
				theta = PApplet.PI + theta;     // theta negative here
			} else if (temp.x < 0 && temp.y < 0) {
				theta = PApplet.PI + theta;     // theta positive here
			} else if (temp.x > 0 && temp.y < 0) {
				theta = PApplet.TWO_PI + theta; // theta negative here
			}
		} else if (temp.y > 0) {
			theta = PApplet.PI/2;
		} else if (temp.y < 0) {
			theta = 3*PApplet.PI/2;
		} else {
			theta = 0;
		}
	}
	public void getPhi(PVector vec){
		temp = PVector.sub(vec,loc,temp);          // vector away center
		r = temp.mag();                                     // get radius
		if (r != 0){
			// acos goes between 0 to PApplet.PI
			phi = PApplet.acos(temp.z/r);                            
		} else {
			phi = 0;
		}
	}
  
	/************************************ COLLISION DETECTION *******************************/
	public void detectCollision(ShimmerCube other){
		// have loc, vel, other.loc, other.vel
		// check for collision
		// check for collision
		if ((loc.x-half_side_len < other.loc.x+other.half_side_len) &&
				(other.loc.x-other.half_side_len < loc.x+half_side_len) &&
				(loc.y-half_side_len < other.loc.y+other.half_side_len) &&
				(other.loc.y-other.half_side_len < loc.y+half_side_len) &&
				(loc.z-half_side_len < other.loc.z+other.half_side_len) &&
				(other.loc.z-other.half_side_len < loc.z+half_side_len)){
  
			rgb.updateShifts();           // update shimmer info    
			other.rgb.updateShifts();
			mass_fac = 1/(mass+other.mass);
			
			// find direction of collision by getting angle between centers
			// in spherical coordinates
			getTheta(other.loc);
			getPhi(other.loc);
  
			// 6 possible face collisions, 12 possible edge collisions, 8 possible corner collisions
			if (phi < PApplet.PI/4){
				// collision in +z face
				// reset position of other cube
				other.loc.z = loc.z+half_side_len+other.half_side_len;
				// update velocities
				temp1 = vel.z;
				temp2 = other.vel.z;
				vel.z = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
			} else if (phi > 3*PApplet.PI/4){
				// collision in -z face
				// reset position of other cube
				other.loc.z = loc.z-half_side_len-other.half_side_len;
				// update velocities
				temp1 = vel.z;
				temp2 = other.vel.z;
				vel.z = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
			} else if (phi == PApplet.PI/4){
				if (theta > PApplet.PI/4 && theta < 3*PApplet.PI/4){
					// collision in +y+z edge
					// reset position of other cube
					other.loc.y = loc.y+half_side_len+other.half_side_len;
					other.loc.z = loc.z+half_side_len+other.half_side_len;
					// update velocities
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta > 3*PApplet.PI/4 && theta < 5*PApplet.PI/4){
					// collision in -x+z edge
					// reset position of other cube
					other.loc.x = loc.x-half_side_len-other.half_side_len;
					other.loc.z = loc.z+half_side_len+other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta > 5*PApplet.PI/4 && theta < 7*PApplet.PI/4){
					// collision in -y+z edge
					// reset position of other cube
					other.loc.y = loc.y-half_side_len-other.half_side_len;
					other.loc.z = loc.z+half_side_len+other.half_side_len;
					// update velocities
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
								2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
								2*mass*mass_fac*temp1;
		          	temp1 = vel.z;
		          	temp2 = other.vel.z;
		          	vel.z = (mass-other.mass)*mass_fac*temp1 + 
		          			2*other.mass*mass_fac*temp2;
		          	other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
		          					2*mass*mass_fac*temp1;
				} else if ((theta > 0 && theta < PApplet.PI/4) || 
						(theta > 7*PApplet.PI/4) && (phi > PApplet.PI/4 && phi < 3*PApplet.PI/4)){
					// collision in +x+z edge      
					// reset position of other cube
					other.loc.x = loc.x+half_side_len+other.half_side_len;
					other.loc.z = loc.z+half_side_len+other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta == PApplet.PI/4){
					// collision in +x+y+z corner
					// reset position of other cube
					other.loc.x = loc.x+half_side_len+other.half_side_len;
					other.loc.y = loc.y+half_side_len+other.half_side_len;
					other.loc.z = loc.z+half_side_len+other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta == 3*PApplet.PI/4){
					// collision in -x+y+z corner
					// reset position of other cube
					other.loc.x = loc.x-half_side_len-other.half_side_len;
					other.loc.y = loc.y+half_side_len+other.half_side_len;
					other.loc.z = loc.z+half_side_len+other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta == 5*PApplet.PI/4){
					// collision in -x-y+z corner
					// reset position of other cube
					other.loc.x = loc.x-half_side_len-other.half_side_len;
					other.loc.y = loc.y-half_side_len-other.half_side_len;
					other.loc.z = loc.z+half_side_len+other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta == 7*PApplet.PI/4){
					// collision in +x-y+z corner
					// reset position of other cube
					other.loc.x = loc.x+half_side_len+other.half_side_len;
					other.loc.y = loc.y-half_side_len-other.half_side_len;
					other.loc.z = loc.z+half_side_len+other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				}
			} else if (phi == 3*PApplet.PI/4){
				if (theta > PApplet.PI/4 && theta < 3*PApplet.PI/4){
					// collision in +y-z edge
					// reset position of other cube
					other.loc.y = loc.y+half_side_len+other.half_side_len;
					other.loc.z = loc.z-half_side_len-other.half_side_len;
					// update velocities
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta > 3*PApplet.PI/4 && theta < 5*PApplet.PI/4){
					// collision in -x-z edge
					// reset position of other cube
					other.loc.x = loc.x-half_side_len-other.half_side_len;
					other.loc.z = loc.z-half_side_len-other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta > 5*PApplet.PI/4 && theta < 7*PApplet.PI/4){
					// collision in -y-z edge
					// reset position of other cube
					other.loc.y = loc.y-half_side_len-other.half_side_len;
					other.loc.z = loc.z-half_side_len-other.half_side_len;
					// update velocities
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if ((theta > 0 && theta < PApplet.PI/4) || 
						(theta > 7*PApplet.PI/4) && (phi > PApplet.PI/4 && phi < 3*PApplet.PI/4)){
					// collision in +x-z edge      
					// reset position of other cube
					other.loc.x = loc.x+half_side_len+other.half_side_len;
					other.loc.z = loc.z-half_side_len-other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta == PApplet.PI/4){
					// collision in +x+y-z corner
					// reset position of other cube
					other.loc.x = loc.x+half_side_len+other.half_side_len;
					other.loc.y = loc.y+half_side_len+other.half_side_len;
					other.loc.z = loc.z-half_side_len-other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta == 3*PApplet.PI/4){
					// collision in -x+y-z corner
					// reset position of other cube
					other.loc.x = loc.x-half_side_len-other.half_side_len;
					other.loc.y = loc.y+half_side_len+other.half_side_len;
					other.loc.z = loc.z-half_side_len-other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta == 5*PApplet.PI/4){
					// collision in -x-y-z corner
					// reset position of other cube
					other.loc.x = loc.x-half_side_len-other.half_side_len;
					other.loc.y = loc.y-half_side_len-other.half_side_len;
					other.loc.z = loc.z-half_side_len-other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				} else if (theta == 7*PApplet.PI/4){
					// collision in +x-y-z corner
					// reset position of other cube
					other.loc.x = loc.x+half_side_len+other.half_side_len;
					other.loc.y = loc.y-half_side_len-other.half_side_len;
					other.loc.z = loc.z-half_side_len-other.half_side_len;
					// update velocities
					temp1 = vel.x;
					temp2 = other.vel.x;
					vel.x = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.y;
					temp2 = other.vel.y;
					vel.y = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
					temp1 = vel.z;
					temp2 = other.vel.z;
					vel.z = (mass-other.mass)*mass_fac*temp1 + 
							2*other.mass*mass_fac*temp2;
					other.vel.z = (other.mass-mass)*mass_fac*temp2 + 
							2*mass*mass_fac*temp1;
				}
				// no need to check phi angle anymore
			} else if (theta > PApplet.PI/4 && theta < 3*PApplet.PI/4){
				// collision in +y face
				// reset position of other cube
				other.loc.y = loc.y+half_side_len+other.half_side_len;
				// update velocities
				temp1 = vel.y;
				temp2 = other.vel.y;
				vel.y = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
			} else if (theta > 3*PApplet.PI/4 && theta < 5*PApplet.PI/4){
				// collision in -x face
				// reset position of other cube
				other.loc.x = loc.x-half_side_len-other.half_side_len;
				// update velocities
				temp1 = vel.x;
				temp2 = other.vel.x;
				vel.x = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
			} else if (theta > 5*PApplet.PI/4 && theta < 7*PApplet.PI/4){
				// collision in -y face
				// reset position of other cube
				other.loc.y = loc.y-half_side_len-other.half_side_len;
				// update velocities
				temp1 = vel.y;
				temp2 = other.vel.y;
				vel.y = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
			} else if ((theta > 0 && theta < PApplet.PI/4) || 
					(theta > 7*PApplet.PI/4) && (phi > PApplet.PI/4 && phi < 3*PApplet.PI/4)){
				// collision in +x face
				// reset position of other cube
				other.loc.x = loc.x+half_side_len+other.half_side_len;
				// update velocities
				temp1 = vel.x;
				temp2 = other.vel.x;
				vel.x = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
			} else if (theta == PApplet.PI/4){
				// collision in +x+y edge
				// reset position of other cube
				other.loc.x = loc.x+half_side_len+other.half_side_len;
				other.loc.y = loc.y+half_side_len+other.half_side_len;
				// update velocities
				temp1 = vel.x;
				temp2 = other.vel.x;
				vel.x = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
				temp1 = vel.y;
				temp2 = other.vel.y;
				vel.y = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
			} else if (theta == 3*PApplet.PI/4){
				// collision in -x+y edge
				// reset position of other cube
				other.loc.x = loc.x-half_side_len-other.half_side_len;
				other.loc.y = loc.y+half_side_len+other.half_side_len;
				// update velocities
				temp1 = vel.x;
				temp2 = other.vel.x;
				vel.x = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
				temp1 = vel.y;
				temp2 = other.vel.y;
				vel.y = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
			} else if (theta == 5*PApplet.PI/4){
				// collision in -x-y edge
				// reset position of other cube
				other.loc.x = loc.x-half_side_len-other.half_side_len;
				other.loc.y = loc.y-half_side_len-other.half_side_len;
				// update velocities
				temp1 = vel.x;
				temp2 = other.vel.x;
				vel.x = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
				temp1 = vel.y;
				temp2 = other.vel.y;
				vel.y = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
			} else if (theta == 7*PApplet.PI/4){
				// collision in +x-y edge
				// reset position of other cube
				other.loc.x = loc.x+half_side_len+other.half_side_len;
				other.loc.y = loc.y-half_side_len-other.half_side_len;      
				// update velocities
				temp1 = vel.x;
				temp2 = other.vel.x;
				vel.x = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.x = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
				temp1 = vel.y;
				temp2 = other.vel.y;
				vel.y = (mass-other.mass)*mass_fac*temp1 + 
						2*other.mass*mass_fac*temp2;
				other.vel.y = (other.mass-mass)*mass_fac*temp2 + 
						2*mass*mass_fac*temp1;
			} // collision location
		} // if collision
	} // detect collision method  
}
