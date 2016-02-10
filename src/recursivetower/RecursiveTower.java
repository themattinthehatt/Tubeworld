package recursivetower;

import core.Cam;
import core.CamParam;
import core.Site;

import processing.core.PApplet;
import processing.core.PVector;

public class RecursiveTower extends Site implements TowerLink {

    // Inherits from core.Site class
    // parent, Tubeworld PApplet
    // origin
    // render_radius
    // init
    // reset_frames

    /****************************************************************************************/
    /************************************ METHODS FOR SITE CLASS ****************************/
    /****************************************************************************************/

    /************************************ CONSTRUCTOR ***************************************/
    public RecursiveTower(PApplet parent_, PVector origin_, float render_radius_, CamParam init_, float reset_frames_) {
        // pass arguments to parent constructor; set Site properties
        super(parent_, origin_, render_radius_, init_, reset_frames_);

    }

    /************************************ UPDATE PHYSICS ************************************/
    public void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled){

    }

    /************************************ DRAW SITE *****************************************/
    public void drawSite(){

    }

    /************************************ UPDATE CAMERA *************************************/
    public int updateCam(Cam cam, int state, boolean[] keys_pressed, boolean[] keys_toggled){
        return 0;
    }



    /****************************************************************************************/
    /************************************ METHODS FOR LINK INTERFACE ************************/
    /****************************************************************************************/

    /************************************ UPDATE CAMERA *************************************/
    public void reset(){

    }

    /************************************ UPDATE CAMERA *************************************/
    public void setProperties(){

    }

    /************************************ UPDATE CAMERA *************************************/
    public void reinitializeLocation(){

    }


}
