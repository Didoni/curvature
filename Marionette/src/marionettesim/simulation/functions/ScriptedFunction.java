/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.simulation.functions;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import marionettesim.gui.controls.ScriptEditFrame;
import marionettesim.simulation.Function2D;
import marionettesim.simulation.Simulation;

/**
 *
 * @author Asier
 */
public class ScriptedFunction extends Function2D{
    private ScriptEditFrame scriptFrame;
    private Simulation simulation;

    public ScriptedFunction(ScriptEditFrame scriptFrame, Simulation simulation) {
        this.scriptFrame = scriptFrame;
        this.simulation = simulation;
    }
    
    
    @Override
    public float eval(float x, float y) {
        if (scriptFrame != null && simulation != null){
            final float time = simulation.getTime();
            try {
                return scriptFrame.evalScript(x, -y, time);
            } catch (ScriptException ex) {
                Logger.getLogger(ScriptedFunction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0.0f;
    }
    
}
