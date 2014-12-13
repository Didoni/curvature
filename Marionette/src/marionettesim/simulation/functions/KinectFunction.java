/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.simulation.functions;

import java.nio.FloatBuffer;
import java.util.Arrays;
import marionettesim.math.FastMath;
import marionettesim.math.Vector3f;
import marionettesim.shapes.Mesh;

/**
 *
 * @author Asier
 */
public class KinectFunction extends HapticImage{
    public final static int GRID_SIZE = 128;
    
    private final int[] backgroundDepth;
    private int[] currentDepth;
    private final int size;
    private final int depthW, depthH;
    private final int startX, startY;
    private final float maxDiff;

    public KinectFunction(float w, float h, int divs, int depthW, int depthH, int startX, int startY, float maxDiff) {
        super("", w, h);
        this.startX = startX;
        this.startY = startY;
        this.depthW = depthW;
        this.depthH = depthH;
        this.maxDiff = maxDiff;
        this.size = divs;
        width = divs; height = divs;
        values = new float[divs][divs];
        calcScaleWorldImage();
        backgroundDepth = new int[depthW*depthH];
        currentDepth = new int[depthW*depthH];
    }
    
    public void updateDepth(int[] depth){
        currentDepth = depth;
    }
    
    @Override
    public boolean needsUpdate() {
        return true;
    }
    
    @Override
    public void applyToGrid(float sizeX, float sizeY, float gain, Mesh grid) {
        final int nPoints = size*size;
        final int nVert = grid.getVertCount();
        assert (nPoints == nVert );
        
        
        //update values
        int i = 0;
        for(int iy = 0; iy < size; ++iy){
            i = startX + (startY+iy)*depthW;
            for(int ix = 0; ix < size; ++ix){
                float diff = backgroundDepth[i] - currentDepth[i];
                if (FastMath.abs(diff) > maxDiff){
                    diff = 0;
                }
                values[ix][iy] = (diff) / 1000.0f;
                
                ++i;
            }
        }
        
        FloatBuffer pos = grid.getPosition();
        FloatBuffer nor = grid.getNormal();
        
       
        i = 0;
        for(int iy = 0; iy < size; ++iy){
            for(int ix = 0; ix < size; ++ix){
                int iii = i*3;
                int i0 = iii + 0;
                int i1 = iii + 1;
                int i2 = iii + 2;
                
                //update position (only y)
                //pos.put(i0, x);
                //pos.put(i1, y);
                pos.put(i2, values[ix][size-iy-1] * gain);
                        
                //update the normal
                /*
                nor.put(i0, normal.x);
                nor.put(i1, normal.y);
                nor.put(i2, normal.z);
                */
                
                i++;
            }
        }   
        
    }
    

    public void snapBackground() {
        for(int j = currentDepth.length - 1; j >= 0; --j){
            backgroundDepth[j] = currentDepth[j];
        }
    }
    
}
