/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.simulation.functions;

import marionettesim.math.FastMath;
import marionettesim.simulation.Function2D;

/**
 *
 * @author Asier
 */
public class CurvatureFunction extends Function2D{
    final float curvature;
    final float side;
    
    final boolean negative;
    final float radious;
    final float r2;
    final float y0;

    public CurvatureFunction(float curv, float side) {
        if (curv == 0.0f){
            throw new IllegalArgumentException("Curvature cannot be equal to 0");
        }
        
        this.curvature = curv;
        this.side = side;
        
        if (curvature > 0.0f){
            negative = false;
        }else{
            negative = true;
            curv = -curv;
        }
        
        radious = 1.0f / curv;
        r2 = radious*radious;
        y0 = -FastMath.sqrt( r2 - side*side/2.0f);
    }
    
         
    @Override
    public float eval(float x, float y) {
        final float v =  FastMath.sqrt(r2 - x*x - y*y) + y0;
        return negative ? -v : v;
    }
    
}
