/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.workers;

import marionettesim.Log;
import marionettesim.gui.MainForm;
import marionettesim.scene.Resources;
import marionettesim.simulation.functions.KinectFunction;

/**
 *
 * @author Asier
 */
public class KinectWorker extends Thread{
    MainForm mf;
    KinectFunction function;
   
    public KinectWorker(MainForm mf) {
        this.mf = mf;
        
    }

    public synchronized void wakeUp(){
        notifyAll();
    }

    public KinectFunction getFunction() {
        return function;
    }
    
    
    
    private SimpleOpenNI.SimpleOpenNI context = null;
    private void initKinect() {
        context = new SimpleOpenNI.SimpleOpenNI(SimpleOpenNI.SimpleOpenNI.RUN_MODE_MULTI_THREADED);
        if (context.isInit() == false) {
            Log.log("Can't init SimpleOpenNI, maybe the camera is not connected!");
            return;
        }
        context.enableDepth(320, 240, 30);
    }
    private void shutdownKinect(){
        if (context != null) { context.close(); }
    }
    private void updateKinect(){
        if (context != null) {  context.update(); }
    }
    private int getKinectWidth(){
        if (context != null) {  return context.depthWidth(); }
        return 0;
    }
    private int getKinectHeight(){
        if (context != null) {  return context.depthHeight(); }
        return 0;
    }
    private int[] getKinectDepthMap(){
        if (context != null) {  return context.depthMap(); }
        return null;
    }
    private float[] getKinectDepthToWorld(){
        if (context != null) {  return context.depthMapRealWorld(); }
        return null;
    }
   
    /*
    private void initKinect(){}
    private void shutdownKinect(){}
    private void updateKinect(){}
    private int getKinectWidth(){return 0;}
    private int getKinectHeight(){return 0;}
    private int[] getKinectDepthMap(){return null;}
    private float[] getKinectDepthToWorld(){return null;}
    */
    
    @Override
    public void run() {
        initKinect();
        while (!interrupted()){
            if (mf.surfacePanel.isKinectEnabled()){
                updateMesh();
                
                if (mf.surfacePanel.isKinectRepaint()){
                    mf.needUpdate();
                }
                
                try {
                    final float mSecs = 1000.0f / mf.surfacePanel.getKinectFrames();
                    Thread.sleep( (long) mSecs );
                } catch (InterruptedException ex) {
                }
            }
            
            if (! mf.surfacePanel.isKinectEnabled() ){
                synchronized(this){
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }
        
        //shutdown kinect
        shutdownKinect();
    }

    
    
    private void updateMesh() {
        final int w = getKinectWidth();
        final int h = getKinectHeight();
        
        if (w == 0 || h == 0) { return; }
         
        updateKinect();
        final int[] depthMap = getKinectDepthMap();
        final float[] dTW = getKinectDepthToWorld();
        
        //update KinectFunction
        if(function != null){
            function.updateDepth(depthMap);
        }
    }
    
    public void createFunction(){
        final float w = mf.simulation.getSurfaceWidth();
        final float h = mf.simulation.getSurfaceHeight();
        function = new KinectFunction(w,h, Resources.MESH_GRID_DIVS + 1, 
                getKinectWidth(), getKinectHeight(),
                mf.surfacePanel.getKinectX(), mf.surfacePanel.getKinectY(),
                mf.surfacePanel.getKinectMaxDiff(),mf.surfacePanel.getKinectFilter());
    }

    public void snapFunctionBackground() {
        if(function != null){
            function.snapBackground();
        }
    }
}
