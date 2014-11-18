/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.gui.tabs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import marionettesim.gui.MainForm;
import marionettesim.scene.MeshEntity;
import marionettesim.scene.Resources;
import marionettesim.shapes.Quad;
import marionettesim.simulation.Function2D;
import marionettesim.simulation.functions.CurvatureFunction;
import marionettesim.simulation.functions.HapticImage;
import marionettesim.simulation.functions.ZeroFunction;
import marionettesim.utils.FileUtils;
import marionettesim.utils.Parse;

/**
 *
 * @author Asier
 */
public class SurfacePanel extends javax.swing.JPanel {
    MainForm mf;
    
    public SurfacePanel(MainForm mf) {
        this.mf = mf;
        
        initComponents();
    }

    public float getGain(){
        return Parse.stringToFloat( gainText.getText() );
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        sizeXText = new javax.swing.JTextField();
        sizeYText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        gridDivsText = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        gainText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        curvatureText = new javax.swing.JTextField();
        applyCurvatureButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        pathText = new javax.swing.JTextField();
        selectFileText = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        resizeText = new javax.swing.JTextField();
        filterCombo = new javax.swing.JComboBox();
        imageApplyButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        colouringCombo = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        colAmpMinText = new javax.swing.JTextField();
        colAmpMaxText = new javax.swing.JTextField();

        jButton1.setText("jButton1");

        jLabel1.setText("Size");

        sizeXText.setText("0.2");
        sizeXText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sizeXTextActionPerformed(evt);
            }
        });

        sizeYText.setText("0.2");
        sizeYText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sizeYTextActionPerformed(evt);
            }
        });

        jLabel2.setText("Grid divs:");

        gridDivsText.setText("128");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Gain:");

        gainText.setText("1.0");

        jLabel4.setText("Color:");

        jLabel5.setText("curvature:");

        curvatureText.setText("2");

        applyCurvatureButton.setText("Apply");
        applyCurvatureButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyCurvatureButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(curvatureText, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(applyCurvatureButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(curvatureText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(applyCurvatureButton)
                .addContainerGap(129, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Curvature", jPanel1);

        jLabel6.setText("path:");

        selectFileText.setText("Select");
        selectFileText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFileTextActionPerformed(evt);
            }
        });

        jLabel7.setText("resize to:");

        resizeText.setText("128");

        filterCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No filtering" }));

        imageApplyButton.setText("Apply");
        imageApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageApplyButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filterCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pathText, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectFileText))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resizeText))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(imageApplyButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(pathText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectFileText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(resizeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imageApplyButton)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Image", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 247, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 189, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("JS+GLSL", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 247, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 189, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Kinect", jPanel4);

        colouringCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "fire&ice", "hue", "brown to blue", "test1" }));
        colouringCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colouringComboActionPerformed(evt);
            }
        });

        jLabel11.setText("AmpCol:");

        colAmpMinText.setText("0");
        colAmpMinText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colAmpMinTextActionPerformed(evt);
            }
        });

        colAmpMaxText.setText("1500");
        colAmpMaxText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colAmpMaxTextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sizeXText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sizeYText))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gridDivsText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gainText))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colouringCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colAmpMinText, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colAmpMaxText, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {colAmpMaxText, colAmpMinText});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(sizeXText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sizeYText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(gridDivsText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(gainText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(colouringCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(colAmpMinText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colAmpMaxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void colouringComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colouringComboActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_colouringComboActionPerformed

    private void colAmpMinTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colAmpMinTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_colAmpMinTextActionPerformed

    private void colAmpMaxTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colAmpMaxTextActionPerformed
        mf.needUpdate();
    }//GEN-LAST:event_colAmpMaxTextActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        final int divs = Parse.stringToInt( gridDivsText.getText() );
        final MeshEntity surface = mf.simulation.getSurface();
        
        surface.customMesh = new Quad(1.0f, 1.0f, divs);
        surface.setMesh( Resources.MESH_CUSTOM );
        
        mf.needUpdate();
    }//GEN-LAST:event_okButtonActionPerformed

    private void updateSurfaceSize(){
        final float sx = Parse.stringToFloat( sizeXText.getText() );
        final float sy = Parse.stringToFloat( sizeYText.getText() );
        
        final MeshEntity surface = mf.simulation.getSurface();
        surface.getTransform().getScale().set(sx, sy, 1.0f);
        
        mf.needUpdate();
    }
    
    private void sizeXTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sizeXTextActionPerformed
        updateSurfaceSize();
    }//GEN-LAST:event_sizeXTextActionPerformed

    private void sizeYTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sizeYTextActionPerformed
       updateSurfaceSize();
    }//GEN-LAST:event_sizeYTextActionPerformed

    private void applyCurvatureButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyCurvatureButtonActionPerformed
        final float gain = getGain();
        final float curvature = Parse.stringToFloat( curvatureText.getText() );
        final float width = mf.simulation.getSurfaceWidth();
        
        Function2D f = new CurvatureFunction(curvature, width);
        mf.simulation.applyNewFunction(f, gain);
        
        mf.needUpdate();
    }//GEN-LAST:event_applyCurvatureButtonActionPerformed

    private void selectFileTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFileTextActionPerformed
        String path = FileUtils.selectFile(this, "open", "", null);
        if(path != null){
            pathText.setText(path);
        }
    }//GEN-LAST:event_selectFileTextActionPerformed

    private void imageApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageApplyButtonActionPerformed
        final String path = pathText.getText();
        final int size = Parse.stringToInt( resizeText.getText() );
        final int filter = filterCombo.getSelectedIndex();
        final float w = mf.simulation.getSurfaceWidth();
        final float h = mf.simulation.getSurfaceHeight();
        final float gain = getGain();
        
        HapticImage image = new HapticImage(path, w, h);
        try {
            final float topHeight = mf.simulation.getBoundaryMax().y;
            image.init(size, filter, topHeight);
            mf.simulation.applyNewFunction(image, gain);
            mf.needUpdate();
        } catch (IOException ex) {
            Logger.getLogger(SurfacePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }//GEN-LAST:event_imageApplyButtonActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyCurvatureButton;
    private javax.swing.JTextField colAmpMaxText;
    private javax.swing.JTextField colAmpMinText;
    private javax.swing.JComboBox colouringCombo;
    private javax.swing.JTextField curvatureText;
    private javax.swing.JComboBox filterCombo;
    private javax.swing.JTextField gainText;
    private javax.swing.JTextField gridDivsText;
    private javax.swing.JButton imageApplyButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField pathText;
    private javax.swing.JTextField resizeText;
    private javax.swing.JButton selectFileText;
    private javax.swing.JTextField sizeXText;
    private javax.swing.JTextField sizeYText;
    // End of variables declaration//GEN-END:variables
}
