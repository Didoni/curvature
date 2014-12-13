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
    
     final String path;
     final float w,h;
     float sw, sh;
    
    
    public HapticImage(String path, float w, float h){
        this.path = path;
        this.w = w;
        this.h = h;
    }
    
    public void calcScaleWorldImage(){
        //scale world->image
        sw = width / w;
        sh = height / h;
        halfWidth = width / 2.0f;
        halfHeight = height / 2.0f;
    }
    
    public void loadImage(int size, float fixedGain) throws IOException{
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
        
        calcScaleWorldImage();
        
        //extract argb
        int nPixels = width * height;
        int[] data = new int[nPixels];
        bi.getRGB(0, 0, width, height, data, 0, width);
        
        //extract greyscale
        values = new float[width][height];
        int index = 0;
        for(int iy = height-1; iy >= 0; --iy){
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
        float px = x * sw + halfWidth;
        float py = y * sh + halfHeight;
        
        px = FastMath.clamp(px, 1, width-2);
        py = FastMath.clamp(py, 1, height-2);
        
        return bilinearFetch(px, py);
    }
    
     public float bilinearFetch(float x, float y) {
        int xx = (int) Math.floor(x);
        int yy = (int) Math.floor(y);
        float dx = x - xx;
        float dy = y - yy;
       
        float a = values[xx][yy] + dx * (values[xx][yy + 1] - values[xx][yy]);
        float b = values[xx + 1][yy] + dx * (values[xx + 1][yy + 1] - values[xx + 1][yy]);
        
        return a + dy * (b - a);
    }
    
}
