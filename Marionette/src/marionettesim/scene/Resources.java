/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.scene;

import marionettesim.renderer.Shader;
import marionettesim.shapes.Box;
import marionettesim.shapes.Cylinder;
import marionettesim.shapes.Mesh;
import marionettesim.shapes.Quad;
import marionettesim.shapes.Sphere;
import marionettesim.shapes.Torus;
import java.util.HashMap;
import javax.media.opengl.GL2;
import marionettesim.gui.MainForm;
import marionettesim.renderer.DinamicSurfaceShader;
import marionettesim.renderer.StaticSurfaceShader;

/**
 *
 * @author Asier
 */
public class Resources {
    public static final int SHADER_SOLID = 1;
    public static final int SHADER_SOLID_SPEC = 2;
    public static final int SHADER_SOLID_DIFF = 3;
    public static final int SHADER_MASK = 9;
    public static final int SHADER_STATIC_SURFACE = 10;
    public static final int SHADER_DINAMIC_SURFACE = 11;
    
    public static final String MESH_CUSTOM = "custom";
    public static final String MESH_QUAD = "quad";
    public static final String MESH_BOX = "box";
    public static final String MESH_BOX_UP = "boxUp";
    public static final String MESH_SPHERE = "sphere";
    public static final String MESH_DONUT = "donut";
    public static final String MESH_CYLINDER = "cylinder";
    public static final String MESH_GRID = "grid";
    public static final int MESH_GRID_DIVS = 128;
    
    private static Resources _instance;
    public static Resources get(){
        return _instance;
    }
    
    public static void init(GL2 gl, MainForm mf){
        if (_instance != null){
            _instance.releaseResources(gl);
            _instance = null;
        }
        _instance = new Resources(gl, mf);
        
    }
     
    private final HashMap<Integer, Shader> shaders;
    private final HashMap<String, Mesh> meshes;
    
    private Resources(GL2 gl, MainForm mf){
        shaders = new HashMap<>();
        meshes = new HashMap<>();
        initResources(gl, mf);
    }

    private void initResources(GL2 gl, MainForm mf) {
        //load shaders
        shaders.put(SHADER_SOLID, new Shader("ColorPlain.vsh", "ColorPlain.fsh", Shader.ORDER_OPAQUE).init(gl));
        shaders.put(SHADER_SOLID_SPEC, new Shader("ColorSpec.vsh", "ColorSpec.fsh", Shader.ORDER_OPAQUE).init(gl));
        shaders.put(SHADER_SOLID_DIFF, new Shader("ColorDiff.vsh", "ColorDiff.fsh", Shader.ORDER_OPAQUE).init(gl));
        shaders.put(SHADER_MASK, new Shader("MatteMask.vsh", "MatteMask.fsh", Shader.ORDER_MASK).init(gl));
        shaders.put(SHADER_STATIC_SURFACE, new StaticSurfaceShader("StaticSlice_V.glsl", "StaticSlice_F.glsl").init(gl));
        shaders.put(SHADER_DINAMIC_SURFACE, new DinamicSurfaceShader("DinamicSlice_V.glsl", "StaticSlice_F.glsl",mf).init(gl));
        
        //load meshes
        meshes.put(MESH_BOX, new Box(0.5f, 0.5f, 0.5f) );
        meshes.put(MESH_BOX_UP, new Box(0.5f, 0.5f, 0.5f, 0.0f,0.5f, 0.0f) );
        meshes.put(MESH_SPHERE, new Sphere(8, 8, 0.5f) );
        meshes.put(MESH_DONUT, new Torus(10, 10, 0.2f, 0.5f) );
        meshes.put(MESH_CYLINDER, new Cylinder(4, 16, 0.5f, 1, true, false) );
        meshes.put(MESH_QUAD, new Quad(1, 1, 1) );
        meshes.put(MESH_GRID, new Quad(1, 1, MESH_GRID_DIVS) );
        
        //load textures
    }
    
    
    public void reloadShaders(GL2 gl){
       for(int i : shaders.keySet()){
            Shader s = shaders.get(i);
            s.reload(gl);
        } 
    }
    
    public Shader getShader(int s){
        return shaders.get(s);
    }
    
    public Mesh getMesh(String m){
        return meshes.get(m);
    }
    
    public void releaseResources(GL2 gl){
        //delete shaders
        for(int s : shaders.keySet()){
            shaders.get(s).unloadShader(gl);
        }
        
        //delete textures
    }
}
