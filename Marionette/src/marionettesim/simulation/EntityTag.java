/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.simulation;

/**
 *
 * @author Asier
 */
public class EntityTag {
    public static final int NONE = 1<<0;
    public static final int SURFACE = 1<<1;
    public static final int HAND = 1<<2;
    public static final int FINGER = 1<<3;
    public static final int SIMULATION_BOUNDINGS = 1<<4;
    public static final int KINECT_MESH = 1<<8;
    public static final int MASK = 1<<9;
}
