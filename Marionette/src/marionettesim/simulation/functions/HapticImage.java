/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.simulation.functions;

import com.jogamp.opengl.util.awt.ImageUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import marionettesim.math.FastMath;
import marionettesim.simulation.Function2D;
import marionettesim.utils.ImagesUtils;

/**
 *
 * @author Asier
 */
public class HapticImage extends Function2D{
    int width, height;
    float halfWidth, halfHeight;
    float[][] values;
    
    private final String path;
    private final float w,h;
    private float sw, sh;
    
    
    public HapticImage(String path, float w, float h){
        this.path = path;
        this.w = w;
        this.h = h;
    }
    
    public void init(int size, int filter, float fixedGain) throws IOException{
        //get the image
        BufferedImage bi = ImageIO.read(new File(path));
        width = bi.getWidth();
        height = bi.getHeight();
        
        //resize
        if (width != size){
            bi = ImagesUtils.resizeImage(bi, size, size);
            width = bi.getWidth();
            height = bi.getHeight();
        }
        
        //scale world->image
        sw = width / w;
        sh = height / h;
        halfWidth = width / 2.0f;
        halfHeight = height / 2.0f;
        
        //extract argb
        int nPixels = width * height;
        int[] data = new int[nPixels];
        bi.getRGB(0, 0, width, height, data, 0, width);
        
        //extract greyscale
        values = new float[width][height];
        int index = 0;
        for(int iy = 0; iy < height; ++iy){
            for(int ix = 0; ix < width; ++ix){
                int sourceColor = data[index];
                values[ix][iy] = (((sourceColor >> 16) & 0x000000FF ) +
                        ((sourceColor >> 8) & 0x000000FF) +
                        ((sourceColor) & 0x000000FF)) / 3.0f / 256.0f * fixedGain;
            
                index++;
            }
        }
    }

    @Override
    public float getRecommendedH(float size) {
        return ((size / width) + (size / height)) / 2.0f;
    }
    
    @Override
    public float eval(float x, float y) {
        int px = (int)(x * sw + halfWidth);
        int py = (int)(y * sh + halfHeight);
        
        px = FastMath.iclamp(px, 0, width-1);
        py = FastMath.iclamp(py, 0, height-1);
        
        return values[px][py];
    }
    
}
