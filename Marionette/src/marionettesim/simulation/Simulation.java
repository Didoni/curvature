/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.simulation;

import marionettesim.gui.MainForm;
import marionettesim.math.FastMath;
import marionettesim.math.Quaternion;
import marionettesim.math.Transform;
import marionettesim.math.Vector3f;
import marionettesim.scene.Entity;
import marionettesim.scene.MeshEntity;
import marionettesim.scene.Resources;
import marionettesim.scene.Scene;
import marionettesim.shapes.Quad;
import marionettesim.simulation.functions.ZeroFunction;
import marionettesim.utils.Color;

/**
 *
 * @author Asier
 */
public class Simulation {
    public final static int N_FINGERS = 4;
    public final static float SURFACE_SIZE = 0.2f; //20cm
    
    public final static float MIN_SIZE = 0.001f; //use for znear and thin lines
    
    Vector3f boundaryMin, boundaryMax; //simulation boundaries
    
    MeshEntity surface;
    Function2D currentFunction;
    
    MeshEntity[] fingers;
    Vector3f[] fingersOffsets; //x ry z
    
    MeshEntity handCenter;
    
    MeshEntity kinectSlice;
    
    MeshEntity[] boundaries;
    
    
    
    public Simulation() {
        final float halfSize = SURFACE_SIZE / 2.0f;
        boundaryMin = new Vector3f(-halfSize, 0, -halfSize);
        boundaryMax = new Vector3f(halfSize, halfSize, halfSize);
                
        currentFunction = new ZeroFunction();
        
        fingersOffsets = new Vector3f[Simulation.N_FINGERS];
        //x, ry, z
        fingersOffsets[0] = new Vector3f(-0.058f, 20, -0.09f);
        fingersOffsets[1] = new Vector3f(-0.022f, 5, -0.112f);
        fingersOffsets[2] = new Vector3f( 0.012f, 0, -0.108f);
        fingersOffsets[3] = new Vector3f( 0.042f, -8, -0.082f);
    }


    //<editor-fold defaultstate="collapsed" desc="Getters and setters">
    public Vector3f getBoundaryMin() {
        return boundaryMin;
    }

    public void setBoundaryMin(Vector3f boundaryMin) {
        this.boundaryMin = boundaryMin;
    }

    public Vector3f getBoundaryMax() {
        return boundaryMax;
    }

    public void setBoundaryMax(Vector3f boundaryMax) {
        this.boundaryMax = boundaryMax;
    }

    public MeshEntity getKinectSlice() {
        return kinectSlice;
    }

    public Vector3f[] getFingersOffsets() {
        return fingersOffsets;
    }

    public MeshEntity getSurface() {
        return surface;
    }
    
    
//</editor-fold>
    
    public float getSurfaceWidth(){
        return surface.getTransform().getScale().x;
    }
    public float getSurfaceHeight(){
        return surface.getTransform().getScale().y;
    }
    
    public float maxDistanceBoundary(){
        Vector3f distances = boundaryMax.subtract(boundaryMin);
        return distances.maxComponent(); 
    }
    
    public float minDistanceBoundary(){
        Vector3f distances = boundaryMax.subtract(boundaryMin);
        return distances.minComponent(); 
    }
    
    public Vector3f getSimulationCenter(){
        return new Vector3f(boundaryMax).addLocal(boundaryMin).divideLocal(2.0f);
    }

    public Vector3f getSimulationSize() {
        return new Vector3f(boundaryMax).subtractLocal(boundaryMin);
    }
    
    public void updateBoundaryBoxes(){
        final float boxWidth = MIN_SIZE;
        Vector3f min = getBoundaryMin();
        Vector3f max = getBoundaryMax();
        final float simWidth = max.y - min.y;
        final float midY = (max.y + min.y) / 2.0f;
        
        boundaries[0].getTransform().getTranslation().set(min.x, midY, min.z);
        boundaries[0].getTransform().getScale().set(boxWidth,simWidth,boxWidth);

        boundaries[1].getTransform().getTranslation().set(min.x, midY, max.z);
        boundaries[1].getTransform().getScale().set(boxWidth,simWidth,boxWidth);

        boundaries[2].getTransform().getTranslation().set(max.x, midY, min.z);
        boundaries[2].getTransform().getScale().set(boxWidth,simWidth,boxWidth);

        boundaries[3].getTransform().getTranslation().set(max.x, midY, max.z);
        boundaries[3].getTransform().getScale().set(boxWidth,simWidth,boxWidth);
    }


    private void addVizObjects(Scene scene) {     
        kinectSlice = new MeshEntity(Resources.MESH_CUSTOM, null, Resources.SHADER_SOLID_SPEC);
        kinectSlice.setTag( EntityTag.KINECT_MESH );
        kinectSlice.setVisible( false );
        kinectSlice.getTransform().getScale().set(0.001f);
        scene.getEntities().add( kinectSlice );
        
        boundaries = new MeshEntity[4];
        for(int i = 0; i < 4; ++i){
            boundaries[i] = new MeshEntity(Resources.MESH_BOX, null, Resources.SHADER_SOLID);
            boundaries[i].setColor(Color.WHITE);
            boundaries[i].setTag( EntityTag.SIMULATION_BOUNDINGS );
            scene.getEntities().add( boundaries[i] );
        }
        
        updateBoundaryBoxes();
    }
    
    
    public void init(Scene scene){
        //Simulation boundaries and others
        addVizObjects(scene);
        
        //fingers
        fingers = new MeshEntity[N_FINGERS];
        for(int i = 0; i < N_FINGERS; ++i){
            final MeshEntity finger = new MeshEntity(Resources.MESH_BOX, null, Resources.SHADER_SOLID_SPEC);
            finger.setColor( Color.GREEN );
            finger.setTag( EntityTag.FINGER );
            finger.getTransform().getScale().set( 0.02f, 0.003f, 0.03f ); //20x3x30mm
            fingers[i] = finger;
            scene.getEntities().add( finger );
        }
        
        //hand
        handCenter = new MeshEntity(Resources.MESH_BOX, null, Resources.SHADER_SOLID_SPEC);
        handCenter.setColor( Color.BLUE );
        handCenter.setTag( EntityTag.HAND );
        handCenter.getTransform().getScale().set( 0.005f );
        scene.getEntities().add( handCenter );
        
        //initial surface
        surface = new MeshEntity(Resources.MESH_CUSTOM, null, Resources.SHADER_STATIC_SURFACE);
        surface.customMesh = new Quad(1.0f, 1.0f, 128);
        surface.setColor( Color.RED );
        surface.setTag( EntityTag.SURFACE );
        surface.getTransform().getScale().set( 0.2f, 0.2f, 1.0f );
        surface.getTransform().getRotation().rotate(FastMath.degToRad( -90 ), 0, 0);
        scene.getEntities().add( surface );
    }
    
    

    
    public void tick(MainForm mf){
        final float gain = mf.surfacePanel.getGain();
        
        //update surface if needed
        
        //get input position X,Z,RY
        final float inputX = mf.inputPanel.getLastX();
        final float inputZ = mf.inputPanel.getLastZ();
        final float inputRy = mf.inputPanel.getLastRY();
        
        final float minAngle = mf.outputPanel.getMinAngle() * FastMath.DEG_TO_RAD;
        final float maxAngle = mf.outputPanel.getMaxAngle() * FastMath.DEG_TO_RAD;
        
        //update hand
        assignXZRYtoEntity(handCenter, inputX, inputZ, inputRy);
        
        final float h = currentFunction.getRecommendedH( (getSurfaceWidth() + getSurfaceHeight()) / 2.0f);
        
        //calc fingers  Y
        float[] euler = new float[3];
        for(int i = 0; i < N_FINGERS; ++i){
            final MeshEntity finger = fingers[i];
            //X,Z,RY
            assignXZRYtoEntity(finger, fingersOffsets[i]);
            final Transform fTrans = finger.getTransform();
            fTrans.combineWithParentNoScale( handCenter.getTransform() );
            
            //Y
            final Vector3f fPos = fTrans.getTranslation();
            fPos.y = currentFunction.eval(fPos.x, -fPos.z) * gain;
            
            //RX,RZ
            final Quaternion fRot = fTrans.getRotation();
            fRot.toAngles(euler);
            euler[2] = currentFunction.calcRotationX(fPos.x, -fPos.z, euler[1], gain, h);
            euler[0] = currentFunction.calcRotationY(fPos.x, -fPos.z, euler[1], gain, h);
            
            euler[2] = FastMath.clamp(euler[2], minAngle, maxAngle);
            euler[0] = FastMath.clamp(euler[0], minAngle, maxAngle);
            
            fRot.fromAngles(euler);
            
            //update fingers
            float rx = FastMath.radToDeg( euler[2] );
            float rz = FastMath.radToDeg( euler[0] );
            
            mf.outputPanel.updateFingerRots(i, rx, rz);
        }
        mf.outputPanel.updateFingers();
        
        //calc hand Y, do this in the last place, otherwise will affect finger.y while doing combineFather
        handCenter.getTransform().getTranslation().y = currentFunction.eval(inputX, -inputZ) * gain;
          
        //logic
        logic();
        
        //send data
    }
    
    public void logic(){
        
    }
    
    public void applyNewFunction(Function2D f, float gain){
        f.applyToGrid(getSurfaceWidth(), getSurfaceHeight(), gain, surface.customMesh);
        currentFunction = f;
    }
    
    
    private static void assignXZRYtoEntity(Entity e, float x, float z, float ry){
        e.getTransform().getTranslation().set(x, 0.0f, z);
        e.getTransform().getRotation().set( Quaternion.IDENTITY );
        e.getTransform().getRotation().rotate(0.0f, FastMath.degToRad(ry), 0.0f);
    }
    
    private static void assignXZRYtoEntity(Entity e, Vector3f v){
        assignXZRYtoEntity(e, v.x, v.z, v.y);
    }
    
    
    
}
