/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.renderer;

import marionettesim.gui.MainForm;
import marionettesim.math.FastMath;
import marionettesim.math.Matrix4f;
import marionettesim.scene.MeshEntity;
import marionettesim.scene.Resources;
import marionettesim.scene.Scene;
import marionettesim.simulation.Simulation;
import java.nio.FloatBuffer;
import javax.media.opengl.GL2;

/**
 *
 * @author Asier
 */
public class SliceAmpHeightRT extends Shader{ 
    int colouring;
    int minPosColor, maxPosColor;
    int heightGain;
    int alphaValue;
    int heightDiv;
    
    private float lastAlpha = 1.0f;
   
    public SliceAmpHeightRT(String vProgram, String fProgram) {
        super(vProgram, fProgram, ORDER_OPAQUE);
    }

    @Override
    public int getRenderingOrder() {
        if (lastAlpha < 1.0f){
            return ORDER_TRANSLUCENT;
        }else{
            return ORDER_OPAQUE;
        }
    }
    

    @Override
    void getUniforms(GL2 gl) {
        super.getUniforms(gl);
 
        heightDiv = gl.glGetUniformLocation(shaderProgramID, "heightDiv");
        
        heightGain = gl.glGetUniformLocation(shaderProgramID, "heightGain");
        alphaValue = gl.glGetUniformLocation(shaderProgramID, "alphaValue");
      
        minPosColor = gl.glGetUniformLocation(shaderProgramID, "minPosColor");
        maxPosColor = gl.glGetUniformLocation(shaderProgramID, "maxPosColor");
        colouring = gl.glGetUniformLocation(shaderProgramID, "colouring");
    }
    
    @Override
    void changeGLStatus(GL2 gl, Renderer renderer, Simulation s, MeshEntity e) {
        renderer.enableBlend(gl, lastAlpha < 1.0f);
        String mesh = e.getMesh();
                
        if (mesh.equals( Resources.MESH_QUAD ) || 
                mesh.equals( Resources.MESH_CUSTOM ) || 
                mesh.equals(Resources.MESH_GRID)){
            renderer.enableCullFace(gl, false);
        }else{
            renderer.enableCullFace(gl, true);
        }
        
        renderer.enableDepthTest(gl, true);
        renderer.enableTexture2D(gl, false);
    }

    @Override
    void bindUniforms(GL2 gl, Scene scene, Renderer renderer,Simulation s, MeshEntity me, Matrix4f projectionViewModel, Matrix4f viewModel, Matrix4f model, FloatBuffer fb) {
       super.bindUniforms(gl, scene, renderer, s, me, projectionViewModel, viewModel, model, fb);
       
        MainForm f = renderer.getForm();
        //TODO
       //gl.glUniform1f(heightDiv, (float) renderer.getForm().rtSlicePanel.getHeightDivs() );
     
      // lastAlpha = renderer.getForm().rtSlicePanel.getRTSliceAlpha();
       
       gl.glUniform1f(alphaValue, lastAlpha );
       //gl.glUniform1f(heightGain, renderer.getForm().rtSlicePanel.getHeightGain() );
       
       //gl.glUniform1f(minPosColor, renderer.getForm().rtSlicePanel.getAmpColorMin());
       //gl.glUniform1f(maxPosColor, renderer.getForm().rtSlicePanel.getAmpColorMax());
       //gl.glUniform1i(colouring, renderer.getForm().rtSlicePanel.getColouringCombo());
    }

    @Override
    protected String preProcessFragment(String sourceCode) {
        return sourceCode;
    }

    @Override
    protected String preProcessVertex(String sourceCode) {
        return super.preProcessFragment(sourceCode);
    }
    
    
}
