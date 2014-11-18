/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.simulation;

import java.nio.FloatBuffer;
import marionettesim.math.FastMath;
import marionettesim.math.Vector2f;
import marionettesim.math.Vector3f;
import marionettesim.shapes.Mesh;

/**
 *
 * @author Asier
 */
public abstract class Function2D {
    
    public abstract float eval(float x, float y);
    
    public float getRecommendedH(float size){
        return 0.001f;
    }
    
    public float calcRotationX(float x, float y, float rotation, float gain, float h){
        Vector2f p2 = new Vector2f( h*2.0f, 0);
        Vector2f p1 = new Vector2f( h, 0);
        Vector2f n1 = new Vector2f(-h, 0);
        Vector2f n2 = new Vector2f(-h*2.0f, 0);

        p2.rotateAroundOrigin(rotation);
        p1.rotateAroundOrigin(rotation);
        n1.rotateAroundOrigin(rotation);
        n2.rotateAroundOrigin(rotation);
        
        p2.addLocal(x, y);
        p1.addLocal(x, y);
        n1.addLocal(x, y);
        n2.addLocal(x, y);
        
        //Stencil five-points
        float dX = (
                eval(p2.x, p2.y) * -1.0f +
                eval(p1.x, p1.y) * 8.0f +
                eval(n1.x, n1.y) * -8.0f +
                eval(n2.x, n2.y) * 1.0f
                ) / 12.0f / h * gain;
        
        return FastMath.atan(dX);
    }
    
    public float calcRotationY(float x, float y, float rotation, float gain, float h){
        Vector2f p2 = new Vector2f(0,  h*2.0f);
        Vector2f p1 = new Vector2f(0,  h);
        Vector2f n1 = new Vector2f(0, -h);
        Vector2f n2 = new Vector2f(0, -h*2.0f);

        p2.rotateAroundOrigin(rotation);
        p1.rotateAroundOrigin(rotation);
        n1.rotateAroundOrigin(rotation);
        n2.rotateAroundOrigin(rotation);
        
        p2.addLocal(x, y);
        p1.addLocal(x, y);
        n1.addLocal(x, y);
        n2.addLocal(x, y);
        
        float dY = (
                eval(p2.x, p2.y) * -1.0f +
                eval(p1.x, p1.y) * 8.0f +
                eval(n1.x, n1.y) * -8.0f +
                eval(n2.x, n2.y) * 1.0f
                ) / 12.0f / h * gain;
        
        return FastMath.atan(dY);
    }
    
    public void applyToGrid(float sizeX, float sizeY, float gain, Mesh grid){
        final int nVert = grid.getVertCount();
        final int D = (int) FastMath.sqrt( nVert );
        
        final float stepX = sizeX / D;
        final float stepY = sizeY / D;
        final float startX = - sizeX / 2.0f;
        final float startY = - sizeY / 2.0f;
        final float h = getRecommendedH((sizeX+sizeY) / 2.0f);
        
        FloatBuffer pos = grid.getPosition();
        FloatBuffer nor = grid.getNormal();
        
        Vector3f normal = new Vector3f();
        Vector3f aux = new Vector3f();
        
        int index = 0;
        for(int iy = 0; iy < D; ++iy){
            final float y = startY + stepY * iy;
            for(int ix = 0; ix < D; ++ix){
                final float x = startX + stepX * ix;
                
                int iii = index*3;
                int i0 = iii + 0;
                int i1 = iii + 1;
                int i2 = iii + 2;
                
                //update position (only y)
                //pos.put(i0, x);
                //pos.put(i1, y);
                pos.put(i2, gain * eval(x, -y));
                        
                //update the normal
                calcNormal(x,y, h, gain, normal, aux);
                nor.put(i0, normal.x);
                nor.put(i1, normal.y);
                nor.put(i2, normal.z);
                
                index++;
            }
        }
    }
    
    public void calcNormal(float x, float y, float h, float gain, Vector3f target, Vector3f aux){
        float nx = (eval(x+h,y) - eval(x-h,y)) * gain;
        float ny = (eval(x,y+h) - eval(x,y-h)) * gain;

        target.set( 2.0f*h , 0.0f , nx).normalize();
        aux.set( 0.0f, 2.0f*h, ny ).normalize();
        
        target.crossLocal( aux );
    }
    

}
