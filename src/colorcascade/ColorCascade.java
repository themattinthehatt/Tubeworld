package colorcascade;

import core.Cam;
import core.CamParam;
import core.Site;
import processing.core.PApplet;
import processing.core.PVector;
import java.awt.event.KeyEvent;

/* TODO
-construct graph
-render graph with circle vertices and line edges
-random walk on vertex position
-get diffusion process running
-camera preset 2

Tricky bits
  how can i ensure that color leaves the network fast enough? maybe make a sink node that has
  a small time constant to pull color out quickly

Future options
  wrap it all up into a cylinder so you can be inside of it!
  make beam come from skyworld, refract through a prism
  play with diffusion constants - random distribution, changing in time, etc.

Notes:
F1-F3 keys toggle color channels, but for some reason my keyboard's F keys send the Java KeyEvent values corresponding
to the NUMPAD values...
 */

public class ColorCascade extends Site {

    // Inherits from core.Site class
    // PApplet parent (Tubeworld object)
    // PVector origin                       // origin of site
    // float render_radius                  // site will be rendered when camera is within this distance from origin
    // CamParam init                        // initial camera dir,loc,sc and down for camera presets
    // float reset_frames                   // number of frames for resetting camera loc when inside render_radius

    // graph construction properties
    public int num_cols;                    // number of columns in graph-initializing grid
    public int num_rows;                    // number of rows in graph-initializing grid
    public float vertex_size;               // size of vertices (radius for cylinder, edge length for cube)
    public float vertex_range;              // side length of cube in which vertex is allowed to move (in pixels)

    // graph properties
    public int num_vertices;                // number of vertices
    public int num_edges;                   // number of edges
    public CCVertex[] vertices;             // array of ColorCascade vertices
    public CCEdge[] edges;                  // array of ColorCascade edges

    // diffusion properties
    public float[] taus;                    // diffusion time constants for RGB channels

    // other properties
    public String source_beam;              // type of beam that enters color cascade; "white", "hue_drift", "hue_rand"
    int color_channel_id;                   // identifies which color channels will be displayed

    /************************************ CONSTRUCTOR ***************************************/
    public ColorCascade(PApplet parent_, PVector origin_, float render_radius_, CamParam init_, float reset_frames_) {

        // pass arguments to parent constructor (Site constructor); sets Site properties
        super(parent_, origin_, render_radius_, init_, reset_frames_);

        // set graph construction properties
        num_cols = 7;
        num_rows = 10;
        vertex_size = 25;
        vertex_range = 100;

        // set diffusion properties
        taus = new float[3];                    // value for each of rgb channels
        taus[0] = 1; taus[1] = 1; taus[2] = 1;  // arbitrary for now

        // construct graph
        num_vertices = num_rows*num_cols;
        vertices = new VertexCylinder[num_vertices];
        num_edges = num_vertices;
        edges = new EdgeRect[num_edges];

        int counter = 0;
        PVector vertex_origin;
        PVector vertex_loc;
        for (int i = 0; i < num_rows; i++) {
            for (int j = 0; j < num_cols; j++) {
                // set all edges for now
                int[] parent_indxs = new int[1];
                int[] child_indxs = new int[1];
                if (counter != 0) {
                    child_indxs[0] = counter - 1;
                } else {
                    child_indxs[0] = 0;
                }
                if (counter != num_vertices-1) {
                    parent_indxs[0] = counter + 1;
                } else {
                    parent_indxs[0] = num_vertices-1;
                }

                vertex_origin = new PVector(origin.x, origin.y + ((float) j) * vertex_range,
                                            origin.z + ((float) i) * vertex_range );
                vertex_loc = new PVector(0, 0, 0);
                vertices[counter] = new VertexCylinder(parent, parent_indxs, child_indxs, vertex_origin, vertex_loc,
                                            taus, vertex_size);
                edges[counter] = new EdgeRect(parent, parent_indxs[0], child_indxs[0]);

                counter++;
            }
        }

        // reset init to be at the center of the graph, given the number of rows and cols;
        // and this is where the camera will return upon resetting the camera position (key 0)
        init.loc = new PVector(origin.x + 1000, origin.y + ((float) num_cols -1 ) * vertex_range / 2,
                                                origin.z + ((float) num_rows -1 ) * vertex_range / 2);
        init.sc  = new PVector(origin.x, origin.y + ((float) num_cols - 1) * vertex_range / 2,
                                         origin.z + ((float) num_rows - 1) * vertex_range / 2);
        init.dir = new PVector(-1,0,0);
        init.down = new PVector(0,0,-1);

    }

    public void updatePhysics(boolean[] keys_pressed, boolean[] keys_toggled) {
//        update color of top vertex
//        then run a diffusion step to update colors of vertices
//        then update edges
//        rgb values kept separately for each vertex, can go past 255, just run a modulo operator
//        toggle option for viewing different channels
//        reset - sets all vertex colors to some dark gray color

        // to see if updates should be paused
        if (!keys_toggled[KeyEvent.VK_SPACE]){

            // update vertices
            for (int i = 0; i < num_vertices; i++) {
                vertices[i].updatePosition();
                vertices[i].updateColor();
            }

            // update edges
            for (int i = 0; i < num_edges; i++) {
                edges[i].setColor(vertices[edges[i].parent_indxs[0]].color,vertices[edges[i].parent_indxs[1]].color);
            }

        } // end paused check
        if (keys_pressed[KeyEvent.VK_BACK_SPACE]){
            //reset();

        } // reset

        /* determine which channels will be rendered by updating color_channel_id
        1 - red
        2 - green
        3 = 1+2 -> red and green
        4 - blue
        5 = 4+1 -> blue and red
        6 = 4+2 -> blue and green
        7 = 1+2+4 -> all channels
         */
        color_channel_id = 0;       // start with nothing
        if (!keys_toggled[KeyEvent.VK_NUMPAD1]) { color_channel_id += 1; }
        if (!keys_toggled[KeyEvent.VK_NUMPAD2]) { color_channel_id += 2; }
        if (!keys_toggled[KeyEvent.VK_NUMPAD3]) { color_channel_id += 4; }
    }

    public void drawSite() {

        for (int i = 0; i < num_vertices; i++) {
            vertices[i].drawVertex(color_channel_id);
        }

//        for (int i = 0; i < num_edges; i++) {
//            edges[i].drawEdge(PVector.add(vertices[edges[i].parent_indxs[0]].origin, vertices[edges[i].parent_indxs[0]].loc),
//                              PVector.add(vertices[edges[i].parent_indxs[1]].origin, vertices[edges[i].parent_indxs[1]].loc),
//                              color_channel_id);
//        }

    }

    public int updateCam(Cam cam, int state, boolean[] keys_pressed, boolean[] keys_toggled){
    /*
    Calls: various methods in Cam class
    Called by: CamCtrl.update
     */

        if (state == 0) { // reset mode
            state = cam.smoothLinPursuit(init,reset_frames,0,1); // calling state, return state
        } else if (state == 2) {
            // random walk in the plane of the graph, looking slightly upwards
            state = 1;
        }
        return state;
    }
}
