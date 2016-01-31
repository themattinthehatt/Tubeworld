// CubeComponents class; used as objects in Cube class

import processing.core.PApplet;
import processing.core.PVector;

public class ShimmerCubeComponent {
	
	PApplet parent;
	
	PVector pos;
	PVector r;     // side length of red cube
	PVector rmax;  // max side length of red cube
	PVector g;     // side length of red cube
	PVector gmax;  // max side length of red cube
	PVector b;     // side length of red cube
	PVector bmax;  // max side length of red cube
    
	float half_side_len;
    
	// random number that defines dynamics of shimmer
	PVector r_rand; 
	PVector g_rand; 
	PVector b_rand; 
	float temp;
	float rand_max;
	float rand_min;
	float amp;
    
	// collision flags
	float coll_cnt; // collision detected
	float coll_inc; // number of frames for oscillation to occur upon collision
	
	ShimmerCubeComponent(PApplet parent_, PVector pos_, float half_side_len_) {
		parent = parent_;
		pos = pos_;
		r = new PVector(0,0,0);
		rmax = new PVector(0,0,0);
		g = new PVector(0,0,0);
		gmax = new PVector(0,0,0);
		b = new PVector(0,0,0);
		bmax = new PVector(0,0,0);
		half_side_len = half_side_len_;
		rand_min = 2*half_side_len*((float) .06);  // min displacement 20% smaller than white cube
		rand_max = 2*half_side_len*((float) .08);  // max displacement 20% larger than white cube
		// collision flags
		coll_cnt = 0;
		coll_inc = 180;
	}
    
	void updateShifts(){
		// update collision counter 
		coll_cnt = coll_inc;
		// randomly assign directions of oscillation
		rmax.set(getRand()*parent.random(rand_min,rand_max),getRand()*parent.random(rand_min,rand_max),getRand()*parent.random(rand_min,rand_max));
		gmax.set(getRand()*parent.random(rand_min,rand_max),getRand()*parent.random(rand_min,rand_max),getRand()*parent.random(rand_min,rand_max));
		bmax.set(getRand()*parent.random(rand_min,rand_max),getRand()*parent.random(rand_min,rand_max),getRand()*parent.random(rand_min,rand_max));
	}
    
	void shimmer(){
		if (coll_cnt > 0){
			// modulate max side lengths of rgb cubes
			amp = PApplet.cos(PApplet.PI/1*(coll_inc-coll_cnt))*PApplet.exp(((float) -0.0005)*(coll_inc-coll_cnt));
			r.set(rmax.mult(amp));
			g.set(gmax.mult(amp));
			b.set(bmax.mult(amp));
			coll_cnt--;
		} else {
			r.set(0,0,0);
			g.set(0,0,0);
			b.set(0,0,0);
		}
	}
  
	void drawCubes(){
		
		// draw red cube
		parent.pushMatrix();
		parent.translate(r.x,r.y,r.z);
		parent.noStroke();
		parent.fill(255,0,0);
		parent.box(2*half_side_len);
		parent.popMatrix();
       
		// draw green cube
		parent.pushMatrix();
		parent.translate(g.x,g.y,g.z);
		parent.noStroke();
		parent.fill(0,255,0);
		parent.box(2*half_side_len);
		parent.popMatrix();
		
		//	draw blue cube
		parent.pushMatrix();
		parent.translate(b.x,b.y,b.z);
		parent.noStroke();
		parent.fill(0,0,255);
		parent.box(2*half_side_len);
		parent.popMatrix();
       
		// draw white cube
		parent.stroke(200,200,200);
		// noStroke();
		parent.fill(255,255,255,255);
		parent.box(2*half_side_len);
  
	}
  
	float getRand(){
		temp = parent.random(0,1);
		if (temp < 0.5){
			return -1;
		} else {
			return 1;
		}
	}
    
}
