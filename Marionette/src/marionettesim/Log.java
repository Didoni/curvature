/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim;

import marionettesim.gui.controls.ScriptEditFrame;

/**
 *
 * @author Asier
 */
public class Log {
    public static ScriptEditFrame scriptFrame;
    
    public static void log(String s){
        System.out.println("Log: " + s); //TOHACK
        
    }
    
    public static void logGLSLError(String s){
        if(scriptFrame != null){
            scriptFrame.setError(s);
        }
    }
}
