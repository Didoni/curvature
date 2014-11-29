/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.renderer;

import java.nio.FloatBuffer;
import javax.media.opengl.GL2;
import marionettesim.gui.MainForm;
import marionettesim.math.Matrix4f;
import marionettesim.scene.MeshEntity;
import marionettesim.scene.Scene;
import marionettesim.simulation.Simulation;

/**
 *
 * @author Asier
 */
public class DinamicSurfaceShader extends Shader{
    int colouring;
    int minColor, maxColor;
    int gain;
    int time;
   
    private MainForm mf;
    
    public DinamicSurfaceShader(String vProgram, String fProgram, MainForm mf) {
        super(vProgram, fProgram, ORDER_OPAQUE);
        this.mf = mf;
    }

    @Override
    void getUniforms(GL2 gl) {
        super.getUniforms(gl);
 
        time = gl.glGetUniformLocation(shaderProgramID, "t");
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
        
       gl.glUniform1f(time, renderer.getForm().surfacePanel.getTime());
       gl.glUniform1f(gain, renderer.getForm().surfacePanel.getGain());
       gl.glUniform1f(minColor, renderer.getForm().surfacePanel.getMinColor());
       gl.glUniform1f(maxColor, renderer.getForm().surfacePanel.getMaxColor());
       gl.glUniform1i(colouring, renderer.getForm().surfacePanel.getColorGradient());
    }

    @Override
    protected String preProcessVertex(String sourceCode) {
        sourceCode = super.preProcessVertex(sourceCode);
        sourceCode = sourceCode.replaceAll("//TEMPLATE MYFUNC", mf.surfacePanel.getGlslScript());
        return sourceCode;
    }

    
}
