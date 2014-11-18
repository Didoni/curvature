/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.scene.behaviours;

import marionettesim.math.Vector3f;
import marionettesim.scene.Behaviour;
import marionettesim.scene.Entity;

/**
 *
 * @author Asier
 */
public class SpeedTransform extends Behaviour{
    Vector3f translation;
    Vector3f rotation;
    Vector3f scale;

    public SpeedTransform() {
        translation = new Vector3f();
        rotation = new Vector3f();
        scale = new Vector3f();
    }

    @Override
    public boolean tick(float dt, Entity e) {
        e.getTransform().getTranslation().addLocal( translation.x * dt, translation.y * dt, translation.z * dt);
        e.getTransform().getRotation().rotate(rotation.x * dt, rotation.y * dt, rotation.z * dt);
        e.getTransform().getScale().addLocal( scale.x * dt, scale.y * dt, scale.z * dt);
        return false;
    }

    
    public Vector3f getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
    
    
}
