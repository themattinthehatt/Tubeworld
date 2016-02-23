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
  LOCATION
  left arrow:   pan left
  right arrow:  pan right
  up arrow:     move forward (+shift -> move up)
  down arrow:   move back (+shift -> move down)
  a:            rotate left
  d:            rotate right
  w:            rotate up
  s:            rotate down
  z:            rotate ccw
  x:            rotate cw
  SPEED
  e             increase forward speed
  r             decrease forward speed
  t             increase angular speed
  y             decrease angular speed
  STATE
  space			pause updating (but not rendering)
  backspace		reset
  shift + tab	cycle through background colors
  PRESETS
  0             reset
  1             free viewing (default)
  2             preset 1 (defined in classes derived from core.Site class)
  3             preset 2 (defined in classes derived from core.Site class)
  etc.
  shift + 0 	reset to site 0
  shift + 1 	reset to site 1
  etc.
*/

package core;
import processing.core.PApplet;

public class KeyHandler {
  
	public PApplet parent; 		// parent PApplet, for
	public boolean[] keys_pressed;	// keep track of key presses
	public boolean[] keys_toggled;	// keep track of key toggles
  
	/*************************************** CONSTRUCTOR ************************************/
	public KeyHandler(PApplet parent_){
		// parent PApplet
		parent = parent_;
		// boolean defaults to false, but explicitly initialize anyway
		keys_pressed = new boolean[256];
		keys_toggled = new boolean[256];
		for (int i = 0; i < 256; i++) {
			keys_pressed[i] = false;
			keys_toggled[i] = false;
		}
	}

	/**************************** UPDATE NEWLY PRESSSED KEY *********************************/
	public void key_pressed() {
		keys_pressed[parent.keyCode] = true;
	} // keys_pressed()
  

	/**************************** UPDATE NEWLY RELEASED KEY *********************************/
	public void key_released() {
		// key will be toggled upon release
		keys_pressed[parent.keyCode] = false;
		keys_toggled[parent.keyCode] = !keys_toggled[parent.keyCode];
  	} // key_released()
}