/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.gui.tabs;

import marionettesim.gui.MainForm;
import marionettesim.utils.Parse;
import marionettesim.utils.StringUtils;

/**
 *
 * @author Asier
 */
public class InputPanel extends javax.swing.JPanel {
    MainForm mf;
    private float lastX, lastZ, lastRY;
    
    public InputPanel(MainForm mf) {
        this.mf = mf;
        initComponents();
        
        lastX = 0.0f;
        lastZ = 0.0f;
        lastRY = 0.0f;
    }

    public float getLastX() {
        return lastX;
    }

    public float getLastZ() {
        return lastZ;
    }

    public float getLastRY() {
        return lastRY;
    }

    public boolean isNone(){
        return noneCheck.isSelected();
    }
    public boolean isMouse(){
        return mouseCheck.isSelected();
    }
    public boolean isOptiTrack(){
        return optiCheck.isSelected();
    }
    
    public void inputEventXZRY(float x, float z, float ry){
        inputEventXZ(x,z);
        inputEventRY( ry );
    }
    
    public void inputEventXZ(float x, float z){
        xText.setText( StringUtils.get().fourDecs( x ) );
        zText.setText( StringUtils.get().fourDecs( z ) );
        lastX = x * Parse.stringToFloat( xGainText.getText() ) + Parse.stringToFloat( xOffsetText.getText() );
        lastZ = z * Parse.stringToFloat( zGainText.getText() ) + Parse.stringToFloat( zOffsetText.getText() );
    }
    
    public void inputEventRY(float ry){
        ryText.setText( StringUtils.get().fourDecs( ry ) );
        lastRY = ry * Parse.stringToFloat( ryGainText.getText() ) + Parse.stringToFloat( ryOffsetText.getText() );
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inputGroup = new javax.swing.ButtonGroup();
        noneCheck = new javax.swing.JRadioButton();
        mouseCheck = new javax.swing.JRadioButton();
        optiCheck = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        xText = new javax.swing.JTextField();
        zText = new javax.swing.JTextField();
        ryText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        xGainText = new javax.swing.JTextField();
        zGainText = new javax.swing.JTextField();
        ryGainText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        xOffsetText = new javax.swing.JTextField();
        zOffsetText = new javax.swing.JTextField();
        ryOffsetText = new javax.swing.JTextField();
        resetButton = new javax.swing.JButton();

        inputGroup.add(noneCheck);
        noneCheck.setSelected(true);
        noneCheck.setText("none");

        inputGroup.add(mouseCheck);
        mouseCheck.setText("mouse");

        inputGroup.add(optiCheck);
        optiCheck.setText("OptiTrack");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("X");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Z");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("RY");

        xText.setEditable(false);
        xText.setText("X");

        zText.setEditable(false);
        zText.setText("Z");

        ryText.setEditable(false);
        ryText.setText("RY");

        jLabel4.setText("Gain:");

        xGainText.setText("1.0");

        zGainText.setText("1.0");

        ryGainText.setText("1.0");

        jLabel5.setText("Offset:");

        xOffsetText.setText("0");

        zOffsetText.setText("0");

        ryOffsetText.setText("0");

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(xText)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(noneCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(mouseCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(zText))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(optiCheck)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(ryText)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(xGainText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zGainText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ryGainText))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(xOffsetText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zOffsetText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ryOffsetText))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(resetButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {mouseCheck, noneCheck, optiCheck});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(noneCheck)
                    .addComponent(mouseCheck)
                    .addComponent(optiCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xGainText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zGainText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ryGainText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xOffsetText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zOffsetText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ryOffsetText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        xOffsetText.setText( xText.getText() );
        zOffsetText.setText( zText.getText() );
        ryOffsetText.setText( ryText.getText() );
    }//GEN-LAST:event_resetButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup inputGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JRadioButton mouseCheck;
    private javax.swing.JRadioButton noneCheck;
    private javax.swing.JRadioButton optiCheck;
    private javax.swing.JButton resetButton;
    private javax.swing.JTextField ryGainText;
    private javax.swing.JTextField ryOffsetText;
    private javax.swing.JTextField ryText;
    private javax.swing.JTextField xGainText;
    private javax.swing.JTextField xOffsetText;
    private javax.swing.JTextField xText;
    private javax.swing.JTextField zGainText;
    private javax.swing.JTextField zOffsetText;
    private javax.swing.JTextField zText;
    // End of variables declaration//GEN-END:variables
}