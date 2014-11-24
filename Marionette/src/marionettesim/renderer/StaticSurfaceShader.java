/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.renderer;

import java.nio.FloatBuffer;
import javax.media.opengl.GL2;
import marionettesim.math.Matrix4f;
import marionettesim.scene.MeshEntity;
import marionettesim.scene.Scene;
import marionettesim.simulation.Simulation;

/**
 *
 * @author Asier
 */
public class StaticSurfaceShader extends Shader{
    int colouring;
    int minColor, maxColor;
    int gain;
   
    public StaticSurfaceShader(String vProgram, String fProgram) {
        super(vProgram, fProgram, ORDER_OPAQUE);
    }

    

    @Override
    void getUniforms(GL2 gl) {
        super.getUniforms(gl);
 
        gain = gl.glGetUniformLocation(shaderProgramID, "heightGain");
        minColor = gl.glGetUniformLocation(shaderProgramID, "minColor");
        maxColor = gl.glGetUniformLocation(shaderProgramID, "maxColor");
        colouring = gl.glGetUniformLocation(shaderProgramID, "colouring");
    }
    
    @Override
    void changeGLStatus(GL2 gl, Renderer renderer, Simulation s, MeshEntity e) {
        renderer.enableCullFace(gl, false);
        renderer.enableDepthTest(gl, true);
        renderer.enableTexture2D(gl, false);
    }

    @Override
    void bindUniforms(GL2 gl, Scene scene, Renderer renderer,Simulation s, MeshEntity me, Matrix4f projectionViewModel, Matrix4f viewModel, Matrix4f model, FloatBuffer fb) {
       super.bindUniforms(gl, scene, renderer, s, me, projectionViewModel, viewModel, model, fb);
        
       gl.glUniform1f(gain, renderer.getForm().surfacePanel.getGain());
       gl.glUniform1f(minColor, renderer.getForm().surfacePanel.getMinColor());
       gl.glUniform1f(maxColor, renderer.getForm().surfacePanel.getMaxColor());
       gl.glUniform1i(colouring, renderer.getForm().surfacePanel.getColorGradient());
    }

    
}
