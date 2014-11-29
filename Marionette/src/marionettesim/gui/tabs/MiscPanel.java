/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.gui.tabs;

import marionettesim.gui.MainForm;
import marionettesim.renderer.Material;
import marionettesim.scene.Entity;
import marionettesim.scene.MeshEntity;
import marionettesim.scene.Resources;
import marionettesim.scene.Scene;
import marionettesim.simulation.EntityTag;
import marionettesim.utils.Color;
import marionettesim.utils.Parse;

/**
 *
 * @author Asier
 */
public class MiscPanel extends javax.swing.JPanel {
    public MainForm mf;
    
    public MiscPanel(MainForm mf) {
        this.mf = mf;
        initComponents();
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        maskGroup = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        maskMaskCheck = new javax.swing.JRadioButton();
        maskDisableCheck = new javax.swing.JRadioButton();
        maskVisibleCheck = new javax.swing.JRadioButton();
        maskAddCubeButton = new javax.swing.JButton();
        maskDelButton = new javax.swing.JButton();
        maskAddSphereButton = new javax.swing.JButton();
        reloadShadersButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ambientText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        diffuseText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        specText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        shiText = new javax.swing.JTextField();

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("mask objects"));

        maskGroup.add(maskMaskCheck);
        maskMaskCheck.setText("mask");
        maskMaskCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskMaskCheckActionPerformed(evt);
            }
        });

        maskGroup.add(maskDisableCheck);
        maskDisableCheck.setSelected(true);
        maskDisableCheck.setText("disable");
        maskDisableCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskDisableCheckActionPerformed(evt);
            }
        });

        maskGroup.add(maskVisibleCheck);
        maskVisibleCheck.setText("visible");
        maskVisibleCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskVisibleCheckActionPerformed(evt);
            }
        });

        maskAddCubeButton.setText("Cube");
        maskAddCubeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAddCubeButtonActionPerformed(evt);
            }
        });

        maskDelButton.setText("Del");
        maskDelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskDelButtonActionPerformed(evt);
            }
        });

        maskAddSphereButton.setText("Sphe");
        maskAddSphereButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAddSphereButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maskDisableCheck)
                    .addComponent(maskAddCubeButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(maskVisibleCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maskMaskCheck))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(maskAddSphereButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maskDelButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maskDisableCheck)
                    .addComponent(maskVisibleCheck)
                    .addComponent(maskMaskCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maskAddCubeButton)
                    .addComponent(maskAddSphereButton)
                    .addComponent(maskDelButton))
                .addContainerGap())
        );

        reloadShadersButton.setText("ReloadShaders");
        reloadShadersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadShadersButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Ambient:");

        ambientText.setText("0.7");
        ambientText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ambientTextActionPerformed(evt);
            }
        });

        jLabel2.setText("Diff:");

        diffuseText.setText("0.7");
        diffuseText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diffuseTextActionPerformed(evt);
            }
        });

        jLabel3.setText("Spec:");

        specText.setText("0.7");
        specText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specTextActionPerformed(evt);
            }
        });

        jLabel4.setText("Shi");

        shiText.setText("0.7");
        shiText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shiTextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ambientText))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(reloadShadersButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(shiText)
                            .addComponent(diffuseText)
                            .addComponent(specText))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ambientText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(diffuseText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(specText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(shiText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(reloadShadersButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void maskMaskCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskMaskCheckActionPerformed
        Scene.setVisible(mf.scene.getEntities(), EntityTag.MASK, true);
        Scene.setShader(mf.scene.getEntities(), EntityTag.MASK, Resources.SHADER_MASK);
        mf.needUpdate();
    }//GEN-LAST:event_maskMaskCheckActionPerformed

    private void maskDisableCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskDisableCheckActionPerformed
        Scene.setVisible(mf.scene.getEntities(), EntityTag.MASK, false);
        mf.needUpdate();
    }//GEN-LAST:event_maskDisableCheckActionPerformed

    private void maskVisibleCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskVisibleCheckActionPerformed
        Scene.setVisible(mf.scene.getEntities(), EntityTag.MASK, true);
        Scene.setShader(mf.scene.getEntities(), EntityTag.MASK, Resources.SHADER_SOLID_SPEC);
        mf.needUpdate();
    }//GEN-LAST:event_maskVisibleCheckActionPerformed

    private void maskAddCubeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskAddCubeButtonActionPerformed
        MeshEntity me = new MeshEntity(Resources.MESH_BOX, null, Resources.SHADER_SOLID_SPEC);
        me.setTag( EntityTag.MASK ); me.setColor( Color.WHITE );
        mf.addMeshEntityToSceneCenter(me);
        mf.needUpdate();
    }//GEN-LAST:event_maskAddCubeButtonActionPerformed

    private void maskDelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskDelButtonActionPerformed
        //Remove from simulation and scene
        for (Entity e : mf.selection){
            if (e.getTag() == EntityTag.MASK){
                mf.scene.getEntities().remove( e );
            }
        }
        mf.clearSelection();
        mf.needUpdate();
    }//GEN-LAST:event_maskDelButtonActionPerformed

    private void maskAddSphereButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskAddSphereButtonActionPerformed
        MeshEntity me = new MeshEntity(Resources.MESH_SPHERE, null, Resources.SHADER_SOLID_SPEC);
        me.setTag( EntityTag.MASK ); me.setColor( Color.WHITE );
        mf.addMeshEntityToSceneCenter(me);
        mf.needUpdate();
    }//GEN-LAST:event_maskAddSphereButtonActionPerformed

    private void reloadShadersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadShadersButtonActionPerformed
        mf.renderer.reloadShaders();
    }//GEN-LAST:event_reloadShadersButtonActionPerformed

    private void ambientTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ambientTextActionPerformed
        guiToSurface();
    }//GEN-LAST:event_ambientTextActionPerformed

    private void diffuseTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diffuseTextActionPerformed
        guiToSurface();
    }//GEN-LAST:event_diffuseTextActionPerformed

    private void specTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specTextActionPerformed
        guiToSurface();
    }//GEN-LAST:event_specTextActionPerformed

    private void shiTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shiTextActionPerformed
        guiToSurface();
    }//GEN-LAST:event_shiTextActionPerformed

    public void surfaceToGUI(){
        Material m = mf.simulation.getSurface().getMaterial();
        ambientText.setText( m.getAmbient() + "");
        diffuseText.setText( m.getDiffuse()+ "");
        specText.setText( m.getSpecular()+ "");
        shiText.setText( m.getShininess()+ "");
    }
    
    private void guiToSurface(){
        Material m = mf.simulation.getSurface().getMaterial();
        m.setAmbient( Parse.stringToFloat( ambientText.getText()) );
        m.setDiffuse(Parse.stringToFloat( diffuseText.getText()) );
        m.setSpecular(Parse.stringToFloat( specText.getText()) );
        m.setShininess(Parse.stringToFloat( shiText.getText()) );
        mf.needUpdate();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ambientText;
    private javax.swing.JTextField diffuseText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton maskAddCubeButton;
    private javax.swing.JButton maskAddSphereButton;
    private javax.swing.JButton maskDelButton;
    private javax.swing.JRadioButton maskDisableCheck;
    private javax.swing.ButtonGroup maskGroup;
    private javax.swing.JRadioButton maskMaskCheck;
    private javax.swing.JRadioButton maskVisibleCheck;
    private javax.swing.JButton reloadShadersButton;
    private javax.swing.JTextField shiText;
    private javax.swing.JTextField specText;
    // End of variables declaration//GEN-END:variables
}
