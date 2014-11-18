/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.scene.behaviours;

import marionettesim.scene.Behaviour;
import marionettesim.scene.Entity;

/**
 *
 * @author Asier
 */
public class Follow extends Behaviour{
    public Entity target;

    public Follow(Entity target) {
        this.target = target;
    }
  
    
    
    
    @Override
    public boolean tick(float dt, Entity e) {
        if (target != null){
            e.getTransform().getTranslation().set( target.getTransform().getTranslation() );
        }
        return false;
    }
    
}
