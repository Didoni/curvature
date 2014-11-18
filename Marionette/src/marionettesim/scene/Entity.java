/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.scene;

import marionettesim.math.Transform;
import marionettesim.math.Vector3f;
import marionettesim.renderer.Material;
import marionettesim.utils.Color;
import java.util.ArrayList;

/**
 *
 * @author Asier
 */
public class Entity {
    Material material;
    int color;
    Transform transform;
    ArrayList<Behaviour> behaviours;
    int tag;
    int frame;
    int number;
    public boolean selected;
    
    
    public Entity() {
        tag = 0;
        color = Color.WHITE;
        material = new Material();
        transform = new Transform();
        behaviours = new ArrayList<>();
        selected = false;
    }

    public int getColor() {
        if(!selected){
            return color;
        }else{
            return Color.GREEN;
        }
    }
    
    public int getRealColor(){
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
    
    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    
    
    
    public ArrayList<Behaviour> getBehaviours() {
        return behaviours;
    }

    public void setBehaviours(ArrayList<Behaviour> behaviours) {
        this.behaviours = behaviours;
    }
    
    

    public void moveLocalSpace(float x, float y, float z){
        Vector3f v = new Vector3f(x,y,z);
        getTransform().getRotation().multLocal(v);
        getTransform().getTranslation().addLocal(v);
    }

    
    public void rotate(float rx, float ry, float rz){
        getTransform().getRotation().rotate(rx, ry, rz);
    }
    
    public void rotateLocal( float rx, float ry, float rz ){
        getTransform().getRotation().rotateLocalSpace(rx, ry, rz);
    }
    
    public void lookAt(Entity other){
        Vector3f dir = other.getTransform().getTranslation().subtract( getTransform().getTranslation() );
        dir.negateLocal();
        getTransform().getRotation().lookAt(dir, Vector3f.UNIT_Y);
    }
    
    public void lookAt(Vector3f observationPoint) {
        Vector3f dir = observationPoint.subtract( getTransform().getTranslation() );
        dir.negateLocal();
        getTransform().getRotation().lookAt(dir, Vector3f.UNIT_Y);
    }
}
