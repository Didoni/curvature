/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.workers;

import java.util.logging.Level;
import java.util.logging.Logger;
import marionettesim.gui.tabs.SurfacePanel;

/**
 *
 * @author Asier
 */
public class PlayerThread extends Thread{
    private final SurfacePanel form;
    float currentTime;
    boolean goingDown;
    
    public boolean shouldIgnorePlayerSlider = false;
    public PlayerThread(SurfacePanel form) {
        this.form = form;
        currentTime = 0.0f;
    }

    public synchronized void playOrPause(){
        notify();
    }
    
    public synchronized  void stopReproduction(){
        notify();
        currentTime = 0.0f;
        goingDown = false;
    }
    
 
        
    @Override
    public void run() {
        while(!interrupted()){
            if (form.isPlaying()){
                form.mf.needUpdate();

                try {
                    float sleepTime = 1000.0f / form.getFPS();
                    Thread.sleep(  (long)sleepTime );
                } catch (InterruptedException ex) {
                    Logger.getLogger(PlayerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                synchronized(this){
                    try {
                        wait();
                    } catch (InterruptedException ex) {}
                }
            }
        }
    }

}
