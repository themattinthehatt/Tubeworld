package recursivetower;

import processing.core.PApplet;
import processing.core.PVector;

public class TowerBeam implements TowerLink {

    public PApplet parent;                  // parent Tubeworld object; for access Processing methods
    public float side_width;	            // "circumference" of beam
    public float side_len;		            // length of beam; this is the distance between edges of the junctions
    public float temp_side_len;             // this is the changing length of the beam while it is updating
    public int orientation; 	            // 0 for updating along +x-axis, 1 for -x-axis, 2 for +y-axis, 3 for -y-axis, 4 for +z-axis
    public PVector origin; 		            // location of origin (corner) of beam; to draw, must add side_len/2 to 2 coordinates that beam is not moving along
    public float[] color;		            // color of beam

    public float beam_extend_frames;        // number of frames needed for extension of beam
    public float beam_pause_frames;         // number of frames paused during beam extensions
    public boolean dynamics_stopped;        // indicates whether the TowerBeam object is still extending or not
    public float updates_count;             // counter on number of updates; flags change in dynamics stopped
    public int tower_orientation;           // orientation of tower which beam is a part of; influences drawSite

    public TowerBeam(PApplet parent_, float side_width_, float side_len_, int orientation_, PVector origin_, float[] color_, int tower_orientation_){
        parent = parent_;
        side_width = side_width_;
        side_len = side_len_; // beam will overlap nodes by half a side_width on each end
        orientation = orientation_;
        origin = origin_;
        color = color_;
        tower_orientation = tower_orientation_;

        temp_side_len = 0;
        beam_extend_frames = 10;         // arbitrary, fun to play around with
        beam_pause_frames = 1;           // arbitrary, fun to play around with
        dynamics_stopped = false;        // start under assumption of extension
        updates_count = 0;
    }

    public void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled) {
    /* updatePhysics method updates the growth of the link; this will no longer
       be called once the link is fully formed
       each call to this function will update the length of the beam and its location
       in proportion to the number of frames used for updating
       the orientation of the beam is always with respect to a tower oriented in the +z
       direction; changes in the tower orientation are taken care of in drawSite method
     */

        // update length of beam while expanding
        temp_side_len = temp_side_len + side_len/beam_extend_frames;

        switch (orientation) {
            case 0:
                // moving in +x-direction
                origin.x = origin.x + side_len / beam_extend_frames / 2; break;
            case 1:
                // moving in -x-direction
                origin.x = origin.x - side_len / beam_extend_frames / 2; break;
            case 2:
                // moving in +y-direction
                origin.y = origin.y + side_len / beam_extend_frames / 2; break;
            case 3:
                // moving in -y-direction
                origin.y = origin.y - side_len / beam_extend_frames / 2; break;
            case 4:
                // moving in +z-direction
                origin.z = origin.z + side_len / beam_extend_frames / 2; break;
            case 5:
                // moving in -z-direction
                origin.z = origin.z - side_len / beam_extend_frames / 2; break;
        }

        // update count on number of updates
        updates_count++;
        // if max number of updates is reached, switch on dynamics_stopped flag
        if (updates_count == beam_extend_frames){
            dynamics_stopped = true;
        }
    }

    public void drawSite() {
    /* draws the link - named so that it coincides with a method in the Site abstract
    class. This way, recursive tower can satisfy both extended the Site abstract
    class and implementing the TowerLink interface with a single draw function. Same
    is true of the naming for updatePhysics.
     */

        int[] rgb = RecursiveTower.hsvToRgb(color[0], color[1], color[2]);
        parent.stroke(rgb[0], rgb[1], rgb[2]);
        parent.fill(rgb[0], rgb[1], rgb[2]);

        // link orientation defines transform needed to go from xyz coordinates used in logic array to 3D xyz coordinates
        // we also want to shift the drawing location over, since 'box' will use the center of the beam instead of the
        // corner, which is what the 'origin' property defines; whichever directions the beam is not moving along should
        // have a +side_width/2 to center them
        switch (tower_orientation) {
            case 0:
                // moving in +x direction
                // +x becomes +y, +y becomes +z, +z becomes +x
                parent.pushMatrix();

                // draw box along different axes depending on orientation value
                if (orientation == 0 || orientation == 1) {
                    parent.translate(origin.z+side_width/2, origin.x, origin.y+side_width/2);
                    parent.box(side_width, temp_side_len, side_width);    // long axis in y-dir
                } else if (orientation == 2 || orientation == 3) {
                    parent.translate(origin.z+side_width/2, origin.x+side_width/2, origin.y);
                    parent.box(side_width, side_width, temp_side_len);    // long axis in z-dir
                } else if (orientation == 4) {
                    parent.translate(origin.z, origin.x+side_width/2, origin.y+side_width/2);
                    parent.box(temp_side_len, side_width, side_width);    // long axis in x-dir
                }
                parent.popMatrix();
                break;
            case 1:
                // moving in -x direction
                // +x becomes +y, +y becomes +z, +z becomes -x
                parent.pushMatrix();
                // draw box along different axes depending on orientation value
                if (orientation == 0 || orientation == 1) {
                    parent.translate(-origin.z-side_width/2, origin.x, origin.y+side_width/2);
                    parent.box(side_width, temp_side_len, side_width);    // long axis in y-dir
                } else if (orientation == 2 || orientation == 3) {
                    parent.translate(-origin.z-side_width/2, origin.x+side_width/2, origin.y);
                    parent.box(side_width, side_width, temp_side_len);    // long axis in z-dir
                } else if (orientation == 4) {
                    parent.translate(-origin.z, origin.x+side_width/2, origin.y+side_width/2);
                    parent.box(temp_side_len, side_width, side_width);    // long axis in x-dir
                }
                parent.popMatrix();
                break;
            case 2:
                // moving in +y direction
                // x stays the same, +y becomes +z, +z becomes +y
                parent.pushMatrix();
                // draw box along different axes depending on orientation value
                if (orientation == 0 || orientation == 1) {
                    parent.translate(origin.x, origin.z+side_width/2, origin.y+side_width/2);
                    parent.box(temp_side_len, side_width, side_width);    // long axis in x-dir
                } else if (orientation == 2 || orientation == 3) {
                    parent.translate(origin.x+side_width/2, origin.z+side_width/2, origin.y);
                    parent.box(side_width, side_width, temp_side_len);    // long axis in z-dir
                } else if (orientation == 4) {
                    parent.translate(origin.x+side_width/2, origin.z, origin.y+side_width/2);
                    parent.box(side_width, temp_side_len, side_width);    // long axis in y-dir
                }
                parent.popMatrix();
                break;
            case 3:
                // moving in -y direction
                // x stays the same, +y becomes +z, +z becomes -y
                parent.pushMatrix();
                // draw box along different axes depending on orientation value
                if (orientation == 0 || orientation == 1) {
                    parent.translate(origin.x, -origin.z-side_width/2, origin.y+side_width/2);
                    parent.box(temp_side_len, side_width, side_width);    // long axis in x-dir
                } else if (orientation == 2 || orientation == 3) {
                    parent.translate(origin.x+side_width/2, -origin.z-side_width/2, origin.y);
                    parent.box(side_width, side_width, temp_side_len);    // long axis in z-dir
                } else if (orientation == 4) {
                    parent.translate(origin.x+side_width/2, -origin.z, origin.y+side_width/2);
                    parent.box(side_width, temp_side_len, side_width);    // long axis in y-dir
                }
                parent.popMatrix();
                break;
            case 4:
                // moving in +z direction
                // xyz coordinates stay the same
                parent.pushMatrix();
                // draw box along different axes depending on orientation value
                if (orientation == 0 || orientation == 1) {
                    parent.translate(origin.x, origin.y+side_width/2, origin.z+side_width/2);
                    parent.box(temp_side_len, side_width, side_width);    // long axis in x-dir
                } else if (orientation == 2 || orientation == 3) {
                    parent.translate(origin.x+side_width/2, origin.y, origin.z+side_width/2);
                    parent.box(side_width, temp_side_len, side_width);    // long axis in y-dir
                } else if (orientation == 4) {
                    parent.translate(origin.x+side_width/2, origin.y+side_width/2, origin.z);
                    parent.box(side_width, side_width, temp_side_len);    // long axis in z-dir
                }
                parent.popMatrix();
                break;
        }
        
    }

    public void setLinkProperties(PVector origin_, int orientation_, int tower_orientation_) {
    /* this method is called when adding a new link to the RecursiveTower structure. The
    method sets all the necessary properties of the link object, such as the origin,
    color and orientation.
     */

        // set the necessary properties in the current link;
        origin = origin_;
        orientation = orientation_;
        tower_orientation = tower_orientation_;

        }

    public void setTowerProperties(int num_links_x_, int num_links_y_, int num_links_z_, int num_init_links_, int num_max_links_, int update_type_) {
    /* this method is called when updating a link that is a RecursiveTower object. The
    method sets all the necessary properties of the tower object that we might want to
    modify, which is a hacky way of changing variables in the 'link' array
     */
    }

    public boolean getDynamicsStopped() {
    /* this method returns the dynamics_stopped variable, that specifies whether or not
       a link is still updating
     */
        return dynamics_stopped;
    }

    public float[] getColor() {
    /* this method returns the color property of the link
     */
        return color;
    }

    public void setColor(float hue, float saturation, float value) {
    /* this method sets the color property of the link object
     */
        color[0] = hue;
        color[1] = saturation;
        color[2] = value;
    }

    public void decrementColor(float decrement_amount) {
    /* this method decrements the color of the link object; used when is_fading_color is set to true
     */
        color[2] -= decrement_amount; // index 2 is value in hsv
    }

    public void reset() {
    /* resets all properties to starting values; this method is called when the entire
    tower needs to be reset, and each object implementing the TowerLink interface will
    be reset through this method
     */
        temp_side_len = 0;
        dynamics_stopped = false;
        updates_count = 0;
    }

}
