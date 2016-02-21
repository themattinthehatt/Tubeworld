/* core.KeyHandler is a static class that allows updates from multiple keys simultaneously, even
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
  (115) d:              rotate right
  (100) w:              rotate up
  (102) s:              rotate down
  (122) z:              rotate ccw
  (120) x:              rotate cw
  SPEED
  (113) e               increase forward speed
  (119) r               decrease forward speed
  (101) t               increase angular speed
  (114) y               decrease angular speed
  STATE
  (032) space			pause updating (but not rendering)
  (008) backspace		reset
  PRESETS
  (048) 0               reset
  (049) 1               free viewing (default)
  (050) 2               preset 1 (defined in classes derived from core.Site class)
  (051) 3               preset 2 (defined in classes derived from core.Site class)
*/

/* TODO
 * would be nice to use KeyEvent values to index keys_pressed and keys_toggled arrays; how to mix
 * this with parent.key?
 */

package core;
import processing.core.PApplet;
import java.awt.event.KeyEvent;

public class KeyHandler {
  
	public PApplet parent; 		// parent PApplet, for
	public boolean[] keys_pressed;	// keep track of key presses
	public boolean[] keys_toggled;	// keep track of key toggles
  
	/*************************************** CONSTRUCTOR ************************************/
	public KeyHandler(PApplet parent_){
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
	public void key_pressed() {

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
		} else if (parent.keyCode == KeyEvent.VK_TAB) {
			keys_pressed[9] = true;
		}
		// speed multipliers
		if (parent.key == 'e') {
			keys_pressed[101] = true;
		} else if (parent.key == 'r') {
			keys_pressed[114] = true;
		} else if (parent.key == 't') {
			keys_pressed[116] = true;
		} else if (parent.key == 'y') {
			keys_pressed[121] = true;
		}
		// movement keys
		else if (parent.key == 'a') {
			keys_pressed[97] = true;  //rotate left
		} else if (parent.key == 'd') {
			keys_pressed[100] = true; // rotate right
		} else if (parent.key == 'w') { 
			keys_pressed[119] = true; // rotate up
		} else if (parent.key == 's') { 
			keys_pressed[115] = true; // rotate down
		} else if (parent.key == 'z') { 
			keys_pressed[122] = true; // rotate ccw
		} else if (parent.key == 'x') { 
			keys_pressed[120] = true; // rotate cw
		}
		// camera keys
		else if (parent.key == '0') {
			keys_pressed[48] = true;  // reset camera position
		} else if (parent.key == '1') {
			keys_pressed[49] = true;  // free viewing mode
		} else if (parent.key == '2') {
			keys_pressed[50] = true;  // preset 1
		} else if (parent.key == '3') {
			keys_pressed[51] = true;  // preset 2
		} else if (parent.key == '4') {
			keys_pressed[52] = true;  // preset 3
		} else if (parent.key == '5') {
			keys_pressed[53] = true;  // preset 4
		} else if (parent.key == '6') {
			keys_pressed[54] = true;  // preset 5
		} else if (parent.key == '7') {
			keys_pressed[55] = true;  // preset 6
		} else if (parent.key == '8') {
			keys_pressed[56] = true;  // preset 7
		} else if (parent.key == '9') {
			keys_pressed[57] = true;  // preset 8
		} else if (parent.key == '~') {
			keys_pressed[126] = true; // reset to site 0
		} else if (parent.key == ')') {
			keys_pressed[41] = true;  // reset to site 0
		} else if (parent.key == '!') {
			keys_pressed[33] = true;  // reset to site 1
		} else if (parent.key == '@') {
			keys_pressed[64] = true;  // reset to site 2
		} else if (parent.key == '#') {
			keys_pressed[35] = true;  // reset to site 3
		} else if (parent.key == '$') {
			keys_pressed[36] = true;  // reset to site 4
		} else if (parent.key == '%') {
			keys_pressed[37] = true;  // reset to site 5
		} else if (parent.key == '^') {
			keys_pressed[94] = true;  // reset to site 6
		} else if (parent.key == '&') {
			keys_pressed[38] = true;  // reset to site 7
		} else if (parent.key == '*') {
			keys_pressed[42] = true;  // reset to site 8
		} else if (parent.key == '(') {
			keys_pressed[40] = true;  // reset to site 9
		}

	} // keys_pressed()
  












	/**************************** UPDATE NEWLY RELEASED KEY *********************************/
	public void key_released() {
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
		} else if (parent.keyCode == KeyEvent.VK_TAB) {
			keys_pressed[9] = false;
			keys_toggled[9] = !keys_toggled[9];
		} else if (parent.keyCode == KeyEvent.VK_RIGHT_PARENTHESIS) {
			keys_pressed[48] = false;  // reset camera position
		}
		// speed multipliers
		if (parent.key == 'e') {
			keys_pressed[101] = false;
			keys_toggled[101] = !keys_toggled[101];
		} else if (parent.key == 'r') {
			keys_pressed[114] = false;
			keys_toggled[114] = !keys_toggled[114];
		} else if (parent.key == 't') {
			keys_pressed[116] = false;
			keys_toggled[116] = !keys_toggled[116];
		} else if (parent.key == 'y') {
			keys_pressed[121] = false;
			keys_toggled[121] = !keys_toggled[121];
		}
		// movement keys
		else if (parent.key == 'a') {
			keys_pressed[97] = false;  //rotate left
			keys_toggled[97] = !keys_toggled[97];
		} else if (parent.key == 'd') {
			keys_pressed[100] = false; // rotate right
			keys_toggled[100] = !keys_toggled[100];
		} else if (parent.key == 'w') { 
			keys_pressed[119] = false; // rotate up
			keys_toggled[119] = !keys_toggled[119];
		} else if (parent.key == 's') { 
			keys_pressed[115] = false; // rotate down
			keys_toggled[115] = !keys_toggled[115];
		} else if (parent.key == 'z') { 
			keys_pressed[122] = false; // rotate ccw
			keys_toggled[122] = !keys_toggled[122];
		} else if (parent.key == 'x') { 
			keys_pressed[120] = false; // rotate cw
			keys_toggled[120] = !keys_toggled[120];
		}
		// camera keys
		else if (parent.key == '0') {
			keys_pressed[48] = false;  // reset camera position
			keys_toggled[48] = !keys_toggled[48];
		} else if (parent.key == '1') { 
			keys_pressed[49] = false;  // free viewing mode
			keys_toggled[49] = !keys_toggled[49];
		} else if (parent.key == '2') { 
			keys_pressed[50] = false;  // preset 1
			keys_toggled[50] = !keys_toggled[50];
		} else if (parent.key == '3') {
			keys_pressed[51] = false;  // preset 2
			keys_toggled[51] = !keys_toggled[51];
		} else if (parent.key == '4') {
			keys_pressed[52] = false;  // preset 3
			keys_toggled[52] = !keys_toggled[52];
		} else if (parent.key == '5') {
			keys_pressed[53] = false;  // preset 4
			keys_toggled[53] = !keys_toggled[53];
		} else if (parent.key == '6') {
			keys_pressed[54] = false;  // preset 5
			keys_toggled[54] = !keys_toggled[54];
		} else if (parent.key == '7') {
			keys_pressed[55] = false;  // preset 6
			keys_toggled[55] = !keys_toggled[55];
		} else if (parent.key == '8') {
			keys_pressed[56] = false;  // preset 7
			keys_toggled[56] = !keys_toggled[56];
		} else if (parent.key == '9') {
			keys_pressed[57] = false;  // preset 8
			keys_toggled[57] = !keys_toggled[57];
		} else if (parent.key == '~') {
			keys_pressed[126] = false; // reset to site 0
			keys_toggled[126] = !keys_toggled[126];
		} else if (parent.key == ')') {
			keys_pressed[41] = false;  // reset to site 0
			keys_toggled[41] = !keys_toggled[41];
		} else if (parent.key == '!') {
			keys_pressed[33] = false;  // reset to site 1
			keys_toggled[33] = !keys_toggled[33];
		} else if (parent.key == '@') {
			keys_pressed[64] = false;  // reset to site 2
			keys_toggled[64] = !keys_toggled[64];
		} else if (parent.key == '#') {
			keys_pressed[35] = false;  // reset to site 3
			keys_toggled[35] = !keys_toggled[35];
		} else if (parent.key == '$') {
			keys_pressed[36] = false;  // reset to site 4
			keys_toggled[36] = !keys_toggled[36];
		} else if (parent.key == '%') {
			keys_pressed[37] = false;  // reset to site 5
			keys_toggled[37] = !keys_toggled[37];
		} else if (parent.key == '^') {
			keys_pressed[94] = false;  // reset to site 6
			keys_toggled[94] = !keys_toggled[94];
		} else if (parent.key == '&') {
			keys_pressed[38] = false;  // reset to site 7
			keys_toggled[38] = !keys_toggled[38];
		} else if (parent.key == '*') {
			keys_pressed[42] = false;  // reset to site 8
			keys_toggled[42] = !keys_toggled[42];
		} else if (parent.key == '(') {
			keys_pressed[40] = false;  // reset to site 9
			keys_toggled[40] = !keys_toggled[40];
		}
  	} // key_released()
}