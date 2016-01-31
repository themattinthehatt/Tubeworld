/* KeyHandler is a static class that allows updates from multiple keys simultaneously, even
though Processing only registers a single keyPressed event per frame

IMPORTANT: must include the following functions outside of the setup and draw functions:
// for updating key events
void keyPressed(){
  key_handler.keys_pressed();
}
void keyReleased(){
  key_handler.key_released();
}

  keyboard controls:
  camera controls: user will modify direction of motion with respect to a direction vector.
  LOCATION (ASCII value in parentheses)
  (000) left arrow:     pan left
  (001) right arrow:    pan right
  (002) up arrow:       move forward (+shift - move up)
  (003) down arrow:     move back (+shift - move down)
  (097) a:              rotate left
  (115) s:              rotate right
  (100) d:              rotate up
  (102) f:              rotate down
  (122) z:              rotate ccw
  (120) x:              rotate cw
  SPEED
  (113) q               increase forward speed
  (119) w               decrease forward speed
  (101) e               increase angular speed
  (114) r               decrease angular speed
  STATE
  (032) space			pause updating (but not rendering)
  (008) backspace		reset
  PRESETS
  (048) 0               reset
  (049) 1               free viewing (default)
  (050) 2               preset 1 (defined in classes derived from Site class)
  (051) 3               preset 2 (defined in classes derived from Site class)
*/

/* TODO
 * would be nice to use KeyEvent values to index keys_pressed and keys_toggled arrays; how to mix
 * this with parent.key?
 */
import processing.core.PApplet;
import java.awt.event.KeyEvent;

public class KeyHandler {
  
	PApplet parent; 		// parent PApplet, for 
	boolean[] keys_pressed;	// keep track of key presses
	boolean[] keys_toggled;	// keep track of key toggles
  
	/*************************************** CONSTRUCTOR ************************************/
	KeyHandler(PApplet parent_){
		// parent PApplet
		parent = parent_;
		// boolean defaults to false, but explicitly initialize anyway
		keys_pressed = new boolean[128];
		keys_toggled = new boolean[128];
		for (int i = 0; i < 128; i++) {
			keys_pressed[i] = false;
			keys_toggled[i] = false;
		}
	}

	/**************************** UPDATE NEWLY PRESSSED KEY *********************************/
	void key_pressed() {
		
		if (parent.keyCode == KeyEvent.VK_UP) {  
			keys_pressed[2] = true;   // move forward
		} else if (parent.keyCode == KeyEvent.VK_DOWN) {
			keys_pressed[3] = true;   // move backward
		} else if (parent.keyCode == KeyEvent.VK_LEFT) { 
			keys_pressed[0] = true;   // pan left
		} else if (parent.keyCode == KeyEvent.VK_RIGHT) { 
			keys_pressed[1] = true;   // pan right
		} else if (parent.keyCode == KeyEvent.VK_SHIFT) {
			keys_pressed[14] = true;  // change forward/backward to up/down
		} else if (parent.keyCode == KeyEvent.VK_SPACE) {
			keys_pressed[32] = true;  // pause
		} else if (parent.keyCode == KeyEvent.VK_BACK_SPACE) {
			keys_pressed[8] = true;   // reset
		}
		// speed multipliers
		if (parent.key == 'q') {
			keys_pressed[113] = true;
		} else if (parent.key == 'w') {
			keys_pressed[119] = true;
		} else if (parent.key == 'e') {
			keys_pressed[101] = true;
		} else if (parent.key == 'r') {
			keys_pressed[114] = true;
		}
		// movement keys
		else if (parent.key == 'a') {
			keys_pressed[97] = true;  //rotate left
		} else if (parent.key == 's') {
			keys_pressed[115] = true; // rotate right
		} else if (parent.key == 'd') { 
			keys_pressed[100] = true; // rotate up
		} else if (parent.key == 'f') { 
			keys_pressed[102] = true; // rotate down
		} else if (parent.key == 'z') { 
			keys_pressed[122] = true; // rotate ccw
		} else if (parent.key == 'x') { 
			keys_pressed[120] = true; // rotate cw
		} else if (parent.key == '0') { 
			keys_pressed[48] = true;  // reset camera position 
		} else if (parent.key == '1') {
			keys_pressed[49] = true;  // free viewing mode
		} else if (parent.key == '2') {
			keys_pressed[50] = true;  // preset 1
		}
	} // keys_pressed()
  
  
	/**************************** UPDATE NEWLY RELEASED KEY *********************************/
	void key_released() {
		// key will be toggled upon release
		if (parent.keyCode == KeyEvent.VK_UP) {  
			keys_pressed[2] = false;   // move forward
			keys_toggled[2] = !keys_toggled[2];
		} else if (parent.keyCode == KeyEvent.VK_DOWN) {
			keys_pressed[3] = false;   // move backward
			keys_toggled[3] = !keys_toggled[3];
		} else if (parent.keyCode == KeyEvent.VK_LEFT) { 
			keys_pressed[0] = false;   // pan left
			keys_toggled[0] = !keys_toggled[0];
		} else if (parent.keyCode == KeyEvent.VK_RIGHT) { 
			keys_pressed[1] = false;   // pan right
			keys_toggled[1] = !keys_toggled[1];
    	} else if (parent.keyCode == KeyEvent.VK_SHIFT) {
			keys_pressed[14] = false;  // change forward/backward to up/down
			keys_toggled[14] = !keys_toggled[14];
		} else if (parent.keyCode == KeyEvent.VK_SPACE) {
			keys_pressed[32] = false;  // pause
			keys_toggled[32] = !keys_toggled[32];
		} else if (parent.keyCode == KeyEvent.VK_BACK_SPACE) {
			keys_pressed[8] = false;   // reset
			keys_toggled[8] = !keys_toggled[8];
		}
		// speed multipliers
		if (parent.key == 'q') {
			keys_pressed[113] = false;
			keys_toggled[113] = !keys_toggled[113];
		} else if (parent.key == 'w') {
			keys_pressed[119] = false;
			keys_toggled[119] = !keys_toggled[119];
		} else if (parent.key == 'e') {
			keys_pressed[101] = false;
			keys_toggled[101] = !keys_toggled[101];
		} else if (parent.key == 'r') {
			keys_pressed[114] = false;
			keys_toggled[114] = !keys_toggled[114];
		}
		// movement keys
		else if (parent.key == 'a') {
			keys_pressed[97] = false;  //rotate left
			keys_toggled[97] = !keys_toggled[97];
		} else if (parent.key == 's') {
			keys_pressed[115] = false; // rotate right
			keys_toggled[115] = !keys_toggled[115];
		} else if (parent.key == 'd') { 
			keys_pressed[100] = false; // rotate up
			keys_toggled[100] = !keys_toggled[100];
		} else if (parent.key == 'f') { 
			keys_pressed[102] = false; // rotate down
			keys_toggled[102] = !keys_toggled[102];
		} else if (parent.key == 'z') { 
			keys_pressed[122] = false; // rotate ccw
			keys_toggled[122] = !keys_toggled[122];
		} else if (parent.key == 'x') { 
			keys_pressed[120] = false; // rotate cw
			keys_toggled[120] = !keys_toggled[120];
		} else if (parent.key == '0') { 
			keys_pressed[48] = false;  // reset camera position
			keys_toggled[48] = !keys_toggled[48];
		} else if (parent.key == '1') { 
			keys_pressed[49] = false;  // free viewing mode
			keys_toggled[49] = !keys_toggled[49];
		} else if (parent.key == '2') { 
			keys_pressed[50] = false;  // preset 1
			keys_toggled[50] = !keys_toggled[50];
		}
  	} // key_released()
}