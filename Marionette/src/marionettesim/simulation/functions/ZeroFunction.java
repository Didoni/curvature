/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.simulation.functions;

import marionettesim.simulation.Function2D;

/**
 *
 * @author Asier
 */
public class ZeroFunction extends Function2D{

    @Override
    public float eval(float x, float y) {
        return 0.0f;
    }
    
}
