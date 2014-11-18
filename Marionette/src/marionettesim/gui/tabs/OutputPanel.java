/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.gui.tabs;

import javax.swing.JTextField;
import marionettesim.gui.MainForm;
import marionettesim.math.Vector2f;
import marionettesim.simulation.Simulation;
import marionettesim.utils.StringUtils;

/**
 *
 * @author Asier
 */
public class OutputPanel extends javax.swing.JPanel {
    MainForm mf;
           
    Vector2f[] angles;
    
    public OutputPanel(MainForm mf) {
        this.mf = mf;
        angles = new Vector2f[Simulation.N_FINGERS];
        for(int i = 0; i < Simulation.N_FINGERS; ++i){
            angles[i] = new Vector2f();
        }
        initComponents();
    }

    public void updateFingerRots(int i, float rx, float rz) {
        angles[i].x = rx;
        angles[i].y = rz;
    }

    private static void updateFinger(Vector2f finger, JTextField x, JTextField y){
        x.setText( StringUtils.get().twoDecs(finger.x));
        y.setText( StringUtils.get().twoDecs(finger.y));
    }
    
    public void updateFingers() {
        //apply to gui
        updateFinger(angles[0], f1RxText, f1RzText);
        updateFinger(angles[1], f2RxText, f2RzText);
        updateFinger(angles[2], f3RxText, f3RzText);
        updateFinger(angles[3], f4RxText, f4RzText);
                
        //TODO send serial
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        f1RxText = new javax.swing.JTextField();
        f1RzText = new javax.swing.JTextField();
        f2RzText = new javax.swing.JTextField();
        f2RxText = new javax.swing.JTextField();
        f3RzText = new javax.swing.JTextField();
        f3RxText = new javax.swing.JTextField();
        f4RzText = new javax.swing.JTextField();
        f4RxText = new javax.swing.JTextField();
        fingerCombo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        loadButton = new javax.swing.JButton();
        sendCheck = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        gainText = new javax.swing.JTextField();

        f1RxText.setText("F1 RX");

        f1RzText.setText("F1 RZ");

        f2RzText.setText("F2 RZ");

        f2RxText.setText("F2 RX");

        f3RzText.setText("F3 RZ");

        f3RxText.setText("F3 RX");

        f4RzText.setText("F4 RZ");

        f4RxText.setText("F4 RX");
        f4RxText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                f4RxTextActionPerformed(evt);
            }
        });

        fingerCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Finger 1", "Finger 2", "Finger 3", "Finger 4" }));

        jLabel1.setText("Calib File:");

        loadButton.setText("Load");

        sendCheck.setText("Send trhough serial");

        jLabel2.setText("Gain:");

        gainText.setText("1.0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fingerCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(gainText))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(sendCheck)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(f3RxText, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(f3RzText, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(f4RxText, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(f4RzText, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(f2RxText, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(f1RxText, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(f1RzText, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(f2RzText, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(loadButton)))
                            .addGap(0, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {f1RxText, f1RzText, f2RxText, f2RzText, f3RxText, f3RzText, f4RxText, f4RzText});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(f1RxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(f1RzText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(f2RxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(f2RzText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(f3RxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(f3RzText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(f4RxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(f4RzText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(gainText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(fingerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(loadButton))
                .addGap(18, 18, 18)
                .addComponent(sendCheck)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void f4RxTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_f4RxTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_f4RxTextActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField f1RxText;
    private javax.swing.JTextField f1RzText;
    private javax.swing.JTextField f2RxText;
    private javax.swing.JTextField f2RzText;
    private javax.swing.JTextField f3RxText;
    private javax.swing.JTextField f3RzText;
    private javax.swing.JTextField f4RxText;
    private javax.swing.JTextField f4RzText;
    private javax.swing.JComboBox fingerCombo;
    private javax.swing.JTextField gainText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton loadButton;
    private javax.swing.JCheckBox sendCheck;
    // End of variables declaration//GEN-END:variables


}
