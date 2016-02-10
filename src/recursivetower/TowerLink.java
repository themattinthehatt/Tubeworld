/* The TowerLink interface sets methods that should be implemented by any
class that is being used as a link in the RecursiveTower class. Notably, the
RecursiveTower class itself implements the TowerLink interface, so that a
RecursiveTower object can be used as a link in another RecursiveTower object.
 */

package recursivetower;


public interface TowerLink {

    void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled);
    void drawSite();
    void reset();
    void setProperties();
    void reinitializeLocation();

}
