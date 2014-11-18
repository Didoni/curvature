/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.shapes;

import marionettesim.math.BoundingBox;
import marionettesim.math.BoundingSphere;
import marionettesim.utils.BufferUtils;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

/**
 *
 * @author Asier
 */
public class Mesh {
    private BoundingSphere bSphere;
    private BoundingBox bBox;
    
    private FloatBuffer position;
    private FloatBuffer texture;
    private FloatBuffer normal;
    private int vertCount;
    
    private ShortBuffer indices;
    private int trianCount;

    public Mesh() {
        bSphere = new BoundingSphere();
        bBox = new BoundingBox();
    }

    public Mesh(int vertices, int tris){
        vertCount = vertices;
        position = BufferUtils.createFloatBuffer( vertices * 3);
        texture = BufferUtils.createFloatBuffer( vertices * 2);
        normal = BufferUtils.createFloatBuffer( vertices * 3);
        
        trianCount = tris;
        indices = BufferUtils.createShortBuffer(tris * 3);
    }
    
    public void setVerticesAndTris(int vertices, int tris){
        vertCount = vertices;
        trianCount = tris;
    }
    
    public FloatBuffer getPosition() {
        return position;
    }

    public void setPosition(FloatBuffer position) {
        this.position = position;
    }

    public FloatBuffer getTexture() {
        return texture;
    }

    public void setTexture(FloatBuffer texture) {
        this.texture = texture;
    }

    public FloatBuffer getNormal() {
        return normal;
    }

    public void setNormal(FloatBuffer normal) {
        this.normal = normal;
    }

    public int getVertCount() {
        return vertCount;
    }

    public void setVertCount(int vertCount) {
        this.vertCount = vertCount;
    }

    public ShortBuffer getIndices() {
        return indices;
    }

    public void setIndices(ShortBuffer indices) {
        this.indices = indices;
    }

    public int getTrianCount() {
        return trianCount;
    }

    public void setTrianCount(int trianCount) {
        this.trianCount = trianCount;
    }

    public BoundingSphere getbSphere() {
        return bSphere;
    }

    public void setbSphere(BoundingSphere bSphere) {
        this.bSphere = bSphere;
    }

    public BoundingBox getbBox() {
        return bBox;
    }

    public void setbBox(BoundingBox bBox) {
        this.bBox = bBox;
    }
    
    
    
    void updateBound(){
        rewindBuffers();
        bSphere.computeFromPoints(position);
        rewindBuffers();
        bBox.computeFromPoints(position);
        rewindBuffers();
    }
    
    public void updateCounts(){
        vertCount = position.limit() / 3;
        trianCount = indices.limit() / 3;
    }
    
    public void rewindBuffers(){
        if (position != null ) { position.rewind(); }
        if (texture != null ) { texture.rewind(); }
        if (normal != null ) { normal.rewind(); }
        if (indices != null ) { indices.rewind(); }
    }
    
    
    public static void writeMeshesToObj(List<Mesh> meshes, DataOutputStream os) throws IOException{
        int[] nVertices = new int[ meshes.size() ];
        
        int meshIndex = 0;        
        //write vertices information
        for(Mesh m : meshes){
            final int nv = m.getVertCount();
            
            FloatBuffer positions = m.position;
            for(int i = 0; i < nv; ++i){
                final int ti = i*3;
                os.writeBytes("v " + positions.get(ti+0) + " " + positions.get(ti+1) + " " + positions.get(ti+2) + "\n");
            }
            os.writeUTF("\n");
            FloatBuffer normals = m.normal;
            for(int i = 0; i < nv; ++i){
                final int ti = i*3;
                os.writeBytes("vn " + normals.get(ti+0) + " " + normals.get(ti+1) + " " + normals.get(ti+2) + "\n");
            }
            os.writeUTF("\n");
            FloatBuffer texture = m.texture;
            for(int i = 0; i < nv; ++i){
                final int ti = i*2;
                os.writeBytes("vt " + texture.get(ti+0) + " " + texture.get(ti+1) +  "\n");
            }
            os.writeUTF("\n");
            
            nVertices[meshIndex] = nv;
            meshIndex++;
        }
        
        
        //write indices
        int indexOffset = 1;
        meshIndex = 0;
        for(Mesh m : meshes){
            final int nt = m.getTrianCount();
            ShortBuffer indices = m.indices;
            for(int i = 0; i < nt; ++i){
                final int ti = i*3;
                int i0 = indices.get(ti + 0) + indexOffset;
                int i1 = indices.get(ti + 1) + indexOffset;
                int i2 = indices.get(ti + 2) + indexOffset;
                os.writeBytes("f " + 
                        i0+"/"+i0+"/"+i0 + " " +
                        i1+"/"+i1+"/"+i1 + " " +
                        i2+"/"+i2+"/"+i2 + "\n");
            }

            indexOffset += nVertices[meshIndex];
            meshIndex++;
        }
    }
}
