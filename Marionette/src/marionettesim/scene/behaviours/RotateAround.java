/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.scene.behaviours;

import marionettesim.math.FastMath;
import marionettesim.math.Vector3f;
import marionettesim.scene.Behaviour;
import marionettesim.scene.Entity;

/**
 *
 * @author Asier
 */
public class RotateAround extends Behaviour{
    public Vector3f center;
    public float radious;
    public float angularSpeed;
    
    private float t;

    public RotateAround(Vector3f center, float radious, float angularSpeed) {
        this.center = center;
        this.radious = radious;
        this.angularSpeed = angularSpeed;
    }
    
    
    
    @Override
    public boolean tick(float dt, Entity e) {
        Vector3f trans = e.getTransform().getTranslation();
        trans.x = center.x + FastMath.cos(angularSpeed * t) * radious;
        trans.y = center.y ;
        trans.z = center.z + FastMath.sin(angularSpeed * t) * radious;
        
        t += dt;
        
        return false;
    }
    
    
}
