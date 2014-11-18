/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.workers;

//import SimpleOpenNI.SimpleOpenNI;
import marionettesim.utils.BufferedImageView;
import marionettesim.Log;
import marionettesim.gui.KinectControlForm;
import marionettesim.scene.Scene;
import marionettesim.shapes.Mesh;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import marionettesim.gui.MainForm;

/**
 *
 * @author Asier
 */
public class KinectWorker extends Thread{
    MainForm mf;
    KinectControlForm form;
    
    Mesh meshA, meshB;
    
    public boolean renderTestFrame = false;

    public KinectWorker(MainForm mf, KinectControlForm form) {
        this.mf = mf;
        this.form = form;
    }

    public synchronized void wakeUp(){
        notifyAll();
    }
    
    /*
    private SimpleOpenNI context = null;
    private void initKinect() {
        context = new SimpleOpenNI(SimpleOpenNI.RUN_MODE_MULTI_THREADED);
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
    */
    
    private void initKinect(){}
    private void shutdownKinect(){}
    private void updateKinect(){}
    private int getKinectWidth(){return 0;}
    private int getKinectHeight(){return 0;}
    private int[] getKinectDepthMap(){return null;}
    private float[] getKinectDepthToWorld(){return null;}
    
    
    
    @Override
    public void run() {
        initKinect();
        initMeshes();
        while (!interrupted()){
            if (form.isKinectEnabled()){
                updateMesh();
                
                if (form.isRepaint()){
                    form.getForm().needUpdate();
                }
                try {
                    Thread.sleep( (long) form.getLatency() );
                } catch (InterruptedException ex) {
                }
            }
            
            if (! form.isKinectEnabled() ){
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

    private int[] currentInd, prevInd;
    
    private void updateMesh() {
        final int step = form.getSteps();
        final float minDepth = form.getMinDepth();
        final float maxDepth = form.getMaxDepth();
        final float sz = form.getSustractZ();
        
        final int w = getKinectWidth();
        final int h = getKinectHeight();
        
        if (w == 0 || h == 0) { return; }
         
        updateKinect();
        final int[] depthMap = getKinectDepthMap();
        final float[] dTw = getKinectDepthToWorld();
        
        Mesh m = meshA;
        FloatBuffer mPos = m.getPosition();
        FloatBuffer mNor = m.getNormal();
        FloatBuffer mTex = m.getTexture();
        ShortBuffer mInd = m.getIndices();
        int maxVert = mPos.capacity() / 3;
        int maxInd = mInd.capacity();
        short numVert = 0; int numInd = 0;
        
        if (currentInd == null || currentInd.length < w) { currentInd = new int[w]; }
        if (prevInd == null || prevInd.length < w) { prevInd = new int[w]; }
        Arrays.fill(currentInd, -1);
        Arrays.fill(prevInd, -1);
        final int wLessStep = w-step;
        
        BufferedImage bi = null;
        Graphics gr = null;
        if(renderTestFrame){
            bi = new BufferedImage( getKinectWidth(), getKinectHeight(), BufferedImage.TYPE_INT_ARGB);
            gr = bi.getGraphics();
        }
        
        outerloop:
        for(int y = step; y < h; y+=step){
            for(int x = 0; x < w; x+=step){
                if (numVert >= maxVert - 1 || numInd >= maxInd - 2*3){ break outerloop; } //mesh is full
                if(renderTestFrame){
                gr.setColor(Color.red);
                gr.drawOval(x, y, 1, 1);
                }
                int p = y*w + x;
                int pLeft = p-1;
                int pUp = p - w;
                int pUpRigh = pUp + 1;
                
                currentInd[x] = -1;
                
                //left triangle
                if (x > 0){
                    if (depthMap[p] <= maxDepth && depthMap[p] >= minDepth &&
                            depthMap[pUp] <= maxDepth && depthMap[pUp] >= minDepth &&
                            depthMap[pLeft] <= maxDepth && depthMap[pLeft] >= minDepth){
                        numInd+=3;
                        if(renderTestFrame){
                        gr.setColor(Color.BLUE);
                        gr.drawOval(x, y, 3, 3);
                        }
                        currentInd[x] = numVert++; //currentPoint
                        mPos.put( dTw[p*3 + 0] ); mPos.put( dTw[p*3 + 1] );  mPos.put( dTw[p*3 + 2] - sz);
                        mNor.put( 0.0f ); mNor.put( 0.0f );  mNor.put( 0.0f );  mTex.put( 0.0f ); mTex.put( 0.0f );
                        mInd.put( (short) currentInd[x] );
                        
                        if (prevInd[x] == -1){ //up
                            prevInd[x] = numVert++;
                            mPos.put( dTw[pUp*3 + 0] ); mPos.put( dTw[pUp*3 + 1] );  mPos.put( dTw[pUp*3 + 2]  - sz);
                            mNor.put( 0.0f ); mNor.put( 0.0f );  mNor.put( 0.0f );  mTex.put( 0.0f ); mTex.put( 0.0f );
                        }
                        mInd.put( (short) prevInd[x] );
                        
                        if (currentInd[x-step] == -1){ //left
                            currentInd[x-step] = numVert++;
                            mPos.put( dTw[pLeft*3 + 0] ); mPos.put( dTw[pLeft*3 + 1] );  mPos.put( dTw[pLeft*3 + 2]  - sz);
                            mNor.put( 0.0f ); mNor.put( 0.0f );  mNor.put( 0.0f );  mTex.put( 0.0f ); mTex.put( 0.0f );
                        }
                        mInd.put( (short) currentInd[x-step] );
                    }
                            
                }
                
                //right triangle
                if (x < wLessStep){
                    if (depthMap[p] <= maxDepth && depthMap[p] >= minDepth &&
                            depthMap[pUp] <= maxDepth && depthMap[pUp] >= minDepth &&
                            depthMap[pUpRigh] <= maxDepth && depthMap[pUpRigh] >= minDepth){
                        numInd+=3;
                        if(renderTestFrame){
                        gr.setColor(Color.BLUE);
                        gr.drawOval(x, y, 3, 3);
                        }
                        if (currentInd[x] == -1){ //currentPoint
                            currentInd[x] = numVert++; 
                            mPos.put( dTw[p*3 + 0] ); mPos.put( dTw[p*3 + 1] );  mPos.put( dTw[p*3 + 2]  - sz);
                            mNor.put( 0.0f ); mNor.put( 0.0f );  mNor.put( 0.0f );  mTex.put( 0.0f ); mTex.put( 0.0f );
                        }
                        mInd.put( (short) currentInd[x] );
                        
                        if (prevInd[x] == -1){ //up
                            prevInd[x] = numVert++;
                            mPos.put( dTw[pUp*3 + 0] ); mPos.put( dTw[pUp*3 + 1] );  mPos.put( dTw[pUp*3 + 2]  - sz);
                            mNor.put( 0.0f ); mNor.put( 0.0f );  mNor.put( 0.0f );  mTex.put( 0.0f ); mTex.put( 0.0f );
                        }
                        mInd.put( (short) prevInd[x] );
                        
                        if (prevInd[x+step] == -1){ //up right
                            prevInd[x+step] = numVert++;
                            mPos.put( dTw[pUpRigh*3 + 0] ); mPos.put( dTw[pUpRigh*3 + 1] );  mPos.put( dTw[pUpRigh*3 + 2]  - sz);
                            mNor.put( 0.0f ); mNor.put( 0.0f );  mNor.put( 0.0f );  mTex.put( 0.0f ); mTex.put( 0.0f );
                        }
                        mInd.put( (short) prevInd[x+step] );
                    }
                        
                }
            }
            int[] tmp = prevInd;
            prevInd = currentInd;
            currentInd = tmp;
        }
        
        m.setVerticesAndTris(numVert, numInd/3);
        m.rewindBuffers();
        switchMesh();
        
        if (renderTestFrame){
            BufferedImageView.showImage("Image", bi, form);
        }
        renderTestFrame = false;
    }

    private void switchMesh() {
        //set mesh A into the MeshEntity
        mf.simulation.getKinectSlice().customMesh = meshA;
        
        //swap A with B
        Mesh tmp = meshA;
        meshA = meshB;
        meshB = tmp;
        
        meshA.rewindBuffers();
    }

    private void initMeshes() {
        final int maxShort = (255 * 255 / 2) - 2;
        meshA = new Mesh( maxShort, maxShort);
        meshB = new Mesh( maxShort, maxShort);
    }
    
    
}
