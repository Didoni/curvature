/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package marionettesim.utils;

/**
 *
 * @author Asier
 */
public class Color {
   public static final int WHITE = create(255, 255, 255, 255);
   public static final int BLACK = create(0, 0, 0, 255);
   public static final int RED = create(255, 0, 0, 255);
   public static final int GREEN = create(0, 255, 0, 255);
   public static final int BLUE = create(0, 0, 255, 255);
   public static final int YELLOW = create(0, 255, 255, 255);

   public static int parse(String s){
       if(s.contains(",")){
           String[] splitted = s.split(",");
           return create(
                   Integer.parseInt(splitted[0]), 
                   Integer.parseInt(splitted[1]), 
                   Integer.parseInt(splitted[2]), 
                   Integer.parseInt(splitted[3]));
       }else{
           return Integer.parseInt(s);
       }
   }
   public static int argb2rgba(int argb){
       return (argb << 8) | red(argb);
   }
   
   public static int rgba2argb(int rgba){
       return (rgba >> 8) | (alpha(rgba) << 24);
   }
   
   public static int create(int r, int g, int b, int a){
          return ((r & 0xFF) << 24) |
                ((g & 0xFF) << 16) |
                ((b & 0xFF) << 8)  |
                ((a & 0xFF));
    }
    
    public static int red(int color){
        return (color >> 24) & 0xff;
    }

    public static int green(int color){
        return (color >> 16) & 0xff;
    }
    
    public static int blue(int color){
        return (color >> 8) & 0xff;
    }
    
    public static int alpha(int color){
        return color & 0xff;
    }
    
    public static int changeAlpha(int color, int newAlpha){
        return (color & 0xffffff00) | (newAlpha & 0xff);
    }
}
