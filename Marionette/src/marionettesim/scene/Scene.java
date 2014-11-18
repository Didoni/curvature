/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.scene;

import marionettesim.math.Matrix4f;
import marionettesim.math.Ray;
import marionettesim.math.Vector3f;
import marionettesim.scene.behaviours.Follow;
import marionettesim.simulation.Simulation;
import marionettesim.utils.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Asier
 */
public class Scene {
    
    Camera camera;
    Light light;
    ArrayList<MeshEntity> entities;
    
    public Scene() {
        camera = new Camera();
        light = new Light();
        entities = new ArrayList<>();
        initScene();
    }
    
    
    public void adjustCameraNearAndFar(Simulation s){
        getCamera().setNear( Simulation.MIN_SIZE );
        getCamera().setFar( s.maxDistanceBoundary() * 10.0f);
    }
    
    private void adjustCameraToPoint(Simulation s, float aspect, Vector3f position){
        Vector3f simCenter = s.getSimulationCenter();
        
        adjustCameraNearAndFar(s);
        getCamera().setOrtho(false);
        getCamera().updateProjection( aspect );
        getCamera().getTransform().getTranslation().set(
                position.x, 
                position.y,
                position.z);
        getCamera().activateObservation(true, simCenter);
    }
    
    public void adjustCameraToSimulation(Simulation s, float aspect){
        Vector3f simCenter = s.getSimulationCenter();
        Vector3f simMax = s.getBoundaryMax();
        
        adjustCameraToPoint( s, aspect,
                new Vector3f( simCenter.x, 
                simMax.y * 3.0f + getCamera().getNear() * 8.0f,
                simMax.z * 3.0f + getCamera().getNear() * 8.0f ));
    }
    
    public void adjustCameraToTop(Simulation s, float aspect){
        Vector3f simCenter = s.getSimulationCenter();
        Vector3f simMax = s.getBoundaryMax();
        
        adjustCameraToPoint( s, aspect,
                new Vector3f( simCenter.x, 
                simMax.y + getCamera().getNear() * 8.0f,
                simCenter.z ));
    }
    
    public void adjustCameraToFront(Simulation s, float aspect){
        Vector3f simCenter = s.getSimulationCenter();
        Vector3f simMax = s.getBoundaryMax();
        
        adjustCameraToPoint( s, aspect,
                new Vector3f( simCenter.x, 
                simCenter.y,
                simMax.z + getCamera().getNear() * 8.0f ));
    }
    

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }

    public ArrayList<MeshEntity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<MeshEntity> entities) {
        this.entities = entities;
    }

    private void initScene() {
        camera.getTransform().setTranslation(0, 0, 20);
        camera.setFov(45); camera.setOrtho(false);
        camera.updateProjection(1.5f);
        camera.setObservationMode(true);
        
        
        light.getTransform().getTranslation().set(10,10,-10);
        //light.getBehaviours().add( new RotateAround(new Vector3f(0, 10, 0), 10, 1f));
        
        MeshEntity me;
        me = new MeshEntity(Resources.MESH_SPHERE, null, Resources.SHADER_SOLID);
        me.setColor(Color.WHITE);
        me.getTransform().setScale(0.0001f);
        me.getTransform().getTranslation().set(light.getTransform().getTranslation());
        me.getBehaviours().add(new Follow(light));
        entities.add(me);
        
        
    }
    
    //x and y should be in the range -1, 1
    public Vector3f screenPointToVector(float x, float y){
        x = x * 2.0f - 1.0f;
        y = y * 2.0f - 1.0f;
        
        Vector3f toReturn =  new Vector3f(x, y, -camera.getNear());
        
        Matrix4f invProjection = getCamera().getProjection().invert();
        
        invProjection.multiplyPoint(toReturn, toReturn);
        
        getCamera().getTransform().transformPoint(toReturn, toReturn);
        
        return toReturn;
    }

    public Ray pointToRay(float x, float y) {
        Ray r = new Ray();
        r.fromTwoPoints(camera.getTransform().getTranslation(), screenPointToVector(x, y));
        return r;
    }
    
    public Vector3f clickToObject(float x, float y, MeshEntity entity){
        Ray r = pointToRay(x, y);
        float currentDistance = entity.rayToBox(r);
        return r.pointAtDistance( currentDistance );
    }
    
    public MeshEntity pickObject(float x, float y, int tagBits){
        Ray r = pointToRay(x, y);
        
        
        float minDistance = Float.MAX_VALUE;
        MeshEntity pick = null;
        for( MeshEntity me : entities){
            if((me.tag & tagBits) != 0 ){
                float currentDistance = me.rayToBox(r);
                if (currentDistance >= 0.0f && currentDistance < minDistance){
                    minDistance = currentDistance;
                    pick = me;
                }
            }
        }
        return pick;
    }
    
    public static void setVisible(List<MeshEntity> list, int tagBit, int frame, int number, boolean visible){
        Iterator<MeshEntity> i = list.iterator();
        while (i.hasNext()){
            MeshEntity e = i.next();
            if ((e.tag & tagBit) != 0){
                if ( (frame == -1 || e.frame == frame)&& (number == -1 || e.number == number)){
                    e.setVisible(visible);
                }else{
                    e.setVisible(! visible);
                }
            }
        }
    }
    
    
    public static void setVisible(List<MeshEntity> list, int tagBit, boolean visible){
        Iterator<MeshEntity> i = list.iterator();
        while (i.hasNext()){
            MeshEntity e = i.next();
            if ((e.tag & tagBit) != 0){
                e.setVisible(visible);
            }
        }
    }
    
    public static void setShader(List<MeshEntity> list, int tagBit, int shader){
        Iterator<MeshEntity> i = list.iterator();
        while (i.hasNext()){
            MeshEntity e = i.next();
            if ((e.tag & tagBit) != 0){
                e.setShader( shader );
            }
        }
    }
    
    public static void removeWithTag(List<MeshEntity> list, int tagBit){
        Iterator<MeshEntity> i = list.iterator();
        while (i.hasNext()){
            Entity e = i.next();
            if ((e.tag & tagBit) != 0){
                i.remove();
            }
        }
    }
    
    public void gatherMeshEntitiesWithTag(ArrayList<MeshEntity> a, int tag){
        for(MeshEntity e : entities){
            if( (e.getTag() & tag) != 0){
                a.add(e);
            }
        }
    }
   

    public void removeWithTag(int tag) {
        Iterator<MeshEntity> iter = entities.iterator();
        while(iter.hasNext()){
            MeshEntity me = iter.next();
            if( (me.getTag() & tag) != 0 ){
                iter.remove();
            }
        }
    }
    
    
}
