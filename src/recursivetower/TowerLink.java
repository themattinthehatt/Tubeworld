/* The TowerLink interface sets methods that should be implemented by any
class that is being used as a link in the RecursiveTower class. Notably, the
RecursiveTower class itself implements the TowerLink interface, so that a
RecursiveTower object can be used as a link in another RecursiveTower object.
 */

package recursivetower;

import processing.core.PVector;

public interface TowerLink {

    void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled);
    /* updatePhysics method updates the growth of the link; this will no longer
    be called once the link is fully formed
     */

    void drawSite();
    /* draws the link - named so that it coincides with a method in the Site abstract
    class. This way, recursive tower can satisfy both extended the Site abstract
    class and implementing the TowerLink interface with a single draw function. Same
    is true of the naming for updatePhysics.
     */

    void setLinkProperties(PVector origin, int orientation, int tower_orientation);
    /* this method is called when adding a new link to the RecursiveTower structure. The
    method sets all the necessary properties of the link object, such as the origin,
    color and orientation.
     */

    void setTowerProperties(int num_links_x, int num_links_y, int num_links_z, int num_init_links, int num_max_links, int update_type);
    /* this method is called when updating a link that is a RecursiveTower object. The
    method sets all the necessary properties of the tower object that we might want to
    modify, which is a hacky way of changing variables in the 'link' array
     */

    boolean getDynamicsStopped();
    /* this method returns the dynamics_stopped variable, that specifies whether or not
    a link is still updating
     */

    float[] getColor();
    /* this method returns the color property of the link
     */

    void setColor(float hue, float saturation, float value);
    /* this method sets the color property of the link object
     */

    void decrementColor(float decrement_amount);
    /* this method decrements the color of the link object; used when is_fading_color is set to true
     */

    void reset();
    /* this method resets all relevant properties of the link so that the object can be
    reused. In the case of a RecursiveTower object, this includes resetting is_occupied
    */
}
