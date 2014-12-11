/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.gui;

import marionettesim.gui.controls.SliderPanel;
import marionettesim.utils.DialogUtils;
import marionettesim.utils.FileUtils;
import marionettesim.Log;
import marionettesim.gui.tabs.MiscPanel;
import marionettesim.gui.tabs.SimPanel;
import marionettesim.math.FastMath;
import marionettesim.math.Quaternion;
import marionettesim.math.Transform;
import marionettesim.math.Vector3f;
import marionettesim.renderer.Renderer;
import marionettesim.scene.BehavioursThread;
import marionettesim.scene.Entity;
import marionettesim.scene.MeshEntity;
import marionettesim.scene.Scene;
import marionettesim.simulation.Simulation;
import marionettesim.utils.Parse;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import marionettesim.gui.tabs.HandPanel;
import marionettesim.gui.tabs.InputPanel;
import marionettesim.gui.tabs.OutputPanel;
import marionettesim.gui.tabs.SurfacePanel;
import marionettesim.simulation.EntityTag;
import marionettesim.utils.StringUtils;
import marionettesim.workers.UdpControlWorker;


/**
 *
 * @author Asier
 */
public class MainForm extends javax.swing.JFrame {
    SliderPanel sliderPanel;
     
    JFrame fullFrame;
    KinectControlForm kinectForm;
   
    public final GLJPanel gljpanel;
    public final Renderer renderer;
    public final Scene scene;
    public Simulation simulation;
    
    BehavioursThread animationThread;
    
    public final ArrayList<Entity> selection;
    boolean cameraLooked;
    boolean hasDragged;
    int firstDragX, firstDragY;
    
    public final InputPanel inputPanel;
    public final OutputPanel outputPanel;
    public final HandPanel handPanel;
    public final SurfacePanel surfacePanel;
    public final SimPanel simPanel;
    public final MiscPanel miscPanel;
    
    public MainForm() {
        sliderPanel = new SliderPanel(1, true);
        
        cameraLooked = true;
        
        selection = new ArrayList<>();
         
        scene = new Scene();
        simulation = new Simulation();
        renderer = new Renderer(scene, this);
        
        inputPanel = new InputPanel(this);
        outputPanel = new OutputPanel(this);
        handPanel = new HandPanel(this);
        surfacePanel = new SurfacePanel(this);
    
        miscPanel = new MiscPanel(this);
        simPanel = new SimPanel(this);
         
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        gljpanel = new GLJPanel(glcapabilities);
        gljpanel.addGLEventListener( new GLEventListener() {      
            @Override
            public void init( GLAutoDrawable glautodrawable ) {
                renderer.init(glautodrawable.getGL().getGL2(), glautodrawable.getWidth(), glautodrawable.getHeight());
            }          
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                renderer.reshape( glautodrawable.getGL().getGL2(), width, height );
            }      
            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {
                renderer.dispose( glautodrawable.getGL().getGL2() );
            }
            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                //TimerUtil.get().tack("Render");
                //TimerUtil.get().tick("Render");
                renderer.render( glautodrawable.getGL().getGL2(), glautodrawable.getWidth(), glautodrawable.getHeight() );
            }
        });
        
        initComponents();
        mainTabPanel.addTab("In", inputPanel); 
        mainTabPanel.addTab("Out", outputPanel); 
        mainTabPanel.addTab("Hand", handPanel); 
        mainTabPanel.addTab("Surface", surfacePanel); 
        mainTabPanel.addTab("Sim", simPanel);        
        mainTabPanel.addTab("Misc", miscPanel);
        
        initSimulation();
        miscPanel.surfaceToGUI();
         
        animationThread = new BehavioursThread(scene, this);
        //animationThread.start();
   
        kinectForm = new KinectControlForm(this, scene);
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public Renderer getRenderer() {
        return renderer;
    }
    
    public void initSimulation(){ 
        simulation.init( scene );
       
        simPanel.simulationBoundariesToGUI();
        
        //init camera
        scene.adjustCameraNearAndFar( simulation );
        scene.adjustCameraToSimulation(simulation, getGLAspect());
        needUpdate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        wrapPlayButtonGroup = new javax.swing.ButtonGroup();
        slicesSource = new javax.swing.ButtonGroup();
        preCubeSource = new javax.swing.ButtonGroup();
        maskObjectsGroup = new javax.swing.ButtonGroup();
        controlPointsColorGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        panelSlider = sliderPanel;
        mainTabPanel = new javax.swing.JTabbedPane();
        sliderFieldLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        rzText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        rxText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        syText = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        xText = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        szText = new javax.swing.JTextField();
        ryText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        sxText = new javax.swing.JTextField();
        yText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        zText = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        containerPanel = new javax.swing.JPanel();
        panel = gljpanel;
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        screenCaptureMenu = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        resetCamMenu = new javax.swing.JMenuItem();
        unlockCameraMenu = new javax.swing.JMenuItem();
        frontCamMenu = new javax.swing.JMenuItem();
        topCamMenu = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        camLookSelectionMenu = new javax.swing.JMenuItem();
        originCamMenu = new javax.swing.JMenuItem();
        centerCamMenu = new javax.swing.JMenuItem();
        otherCamMenu = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        kinectMenu = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        fullScreenMenu = new javax.swing.JMenuItem();
        camProjMenu = new javax.swing.JMenuItem();
        camViewMenu = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Marionette Sim - Bristol BIG");

        panelSlider.setBackground(new java.awt.Color(255, 255, 255));
        panelSlider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelSliderMouseDragged(evt);
            }
        });
        panelSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelSliderMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panelSliderMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout panelSliderLayout = new javax.swing.GroupLayout(panelSlider);
        panelSlider.setLayout(panelSliderLayout);
        panelSliderLayout.setHorizontalGroup(
            panelSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelSliderLayout.setVerticalGroup(
            panelSliderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
        );

        sliderFieldLabel.setText("MMM");

        rzText.setText("0");
        rzText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rzTextFocusGained(evt);
            }
        });
        rzText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rzTextActionPerformed(evt);
            }
        });

        jLabel1.setText("X");

        jLabel6.setText("RY");

        rxText.setText("0");
        rxText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rxTextFocusGained(evt);
            }
        });
        rxText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rxTextActionPerformed(evt);
            }
        });

        jLabel5.setText("RZ");

        jLabel2.setText("Y");

        syText.setText("0");
        syText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                syTextFocusGained(evt);
            }
        });
        syText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syTextActionPerformed(evt);
            }
        });

        jLabel21.setText("SZ:");

        xText.setText("0");
        xText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                xTextFocusGained(evt);
            }
        });
        xText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xTextActionPerformed(evt);
            }
        });

        jLabel15.setText("SX:");

        szText.setText("0");
        szText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                szTextFocusGained(evt);
            }
        });
        szText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                szTextActionPerformed(evt);
            }
        });

        ryText.setText("0");
        ryText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ryTextFocusGained(evt);
            }
        });
        ryText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ryTextActionPerformed(evt);
            }
        });

        jLabel4.setText("RX");

        sxText.setText("0");
        sxText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sxTextFocusGained(evt);
            }
        });
        sxText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sxTextActionPerformed(evt);
            }
        });

        yText.setText("0");
        yText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                yTextFocusGained(evt);
            }
        });
        yText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yTextActionPerformed(evt);
            }
        });

        jLabel3.setText("Z");

        zText.setText("0");
        zText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                zTextFocusGained(evt);
            }
        });
        zText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zTextActionPerformed(evt);
            }
        });

        jLabel16.setText("SY:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(szText, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sxText))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zText))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yText))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xText)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(syText, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rzText))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rxText)
                            .addComponent(ryText))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(xText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(rxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(yText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6)
                                .addComponent(ryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(zText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(rzText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(sxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(syText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(szText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(sliderFieldLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(mainTabPanel))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderFieldLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainTabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
        );

        containerPanel.setLayout(new java.awt.BorderLayout());

        panel.setBackground(new java.awt.Color(0, 0, 0));
        panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelMouseDragged(evt);
            }
        });
        panel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                panelMouseWheelMoved(evt);
            }
        });
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 502, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        containerPanel.add(panel, java.awt.BorderLayout.CENTER);

        jMenu1.setText("Simulation");

        screenCaptureMenu.setText("Screen Capture");
        screenCaptureMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                screenCaptureMenuActionPerformed(evt);
            }
        });
        jMenu1.add(screenCaptureMenu);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Camera");

        resetCamMenu.setText("reset");
        resetCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetCamMenuActionPerformed(evt);
            }
        });
        jMenu2.add(resetCamMenu);

        unlockCameraMenu.setText("Un/Lock cam");
        unlockCameraMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unlockCameraMenuActionPerformed(evt);
            }
        });
        jMenu2.add(unlockCameraMenu);

        frontCamMenu.setText("Front");
        frontCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frontCamMenuActionPerformed(evt);
            }
        });
        jMenu2.add(frontCamMenu);

        topCamMenu.setText("Top");
        topCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topCamMenuActionPerformed(evt);
            }
        });
        jMenu2.add(topCamMenu);

        jMenu3.setText("Look At");

        camLookSelectionMenu.setText("Selection");
        camLookSelectionMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camLookSelectionMenuActionPerformed(evt);
            }
        });
        jMenu3.add(camLookSelectionMenu);

        originCamMenu.setText("Origin 000");
        originCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                originCamMenuActionPerformed(evt);
            }
        });
        jMenu3.add(originCamMenu);

        centerCamMenu.setText("Center");
        centerCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                centerCamMenuActionPerformed(evt);
            }
        });
        jMenu3.add(centerCamMenu);

        otherCamMenu.setText("Other");
        otherCamMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                otherCamMenuActionPerformed(evt);
            }
        });
        jMenu3.add(otherCamMenu);

        jMenu2.add(jMenu3);

        jMenuBar1.add(jMenu2);

        jMenu5.setText("Kinect");

        kinectMenu.setText("Show");
        kinectMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kinectMenuActionPerformed(evt);
            }
        });
        jMenu5.add(kinectMenu);

        jMenuItem1.setText("capture frame");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem1);

        jMenuBar1.add(jMenu5);

        jMenu7.setText("View");
        jMenu7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu7ActionPerformed(evt);
            }
        });

        fullScreenMenu.setText("Full Screen");
        fullScreenMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullScreenMenuActionPerformed(evt);
            }
        });
        jMenu7.add(fullScreenMenu);

        camProjMenu.setText("Edit Projection");
        camProjMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camProjMenuActionPerformed(evt);
            }
        });
        jMenu7.add(camProjMenu);

        camViewMenu.setText("Edit View");
        camViewMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camViewMenuActionPerformed(evt);
            }
        });
        jMenu7.add(camViewMenu);

        jMenuBar1.add(jMenu7);

        jMenu9.setText("Utils");
        jMenuBar1.add(jMenu9);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(containerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(containerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private int lastButton, lastX, lastY;
    private void panelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMousePressed
        lastButton = evt.getButton();
        lastX = evt.getX();
        lastY = evt.getY();
       
        if (lastButton == 2) {
            if (cameraLooked) {
                scene.getCamera().activateObservation(true, scene.getCamera().getObservationPoint());
            }
        } else if (lastButton == 1) {
            if (! inputPanel.isMouse()){
                updateSelection(evt);
            }
        }

    }//GEN-LAST:event_panelMousePressed

    private void panelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMouseDragged
        int x = evt.getX();
        int y = evt.getY();
        final float rotGain = 0.01f;
        final float moveGain =  simPanel.getGUIGain()  * 0.5f;
        float diffX = (x - lastX);
        float diffY = (y - lastY);
        
        if (lastButton == 1) {
            if (inputPanel.isMouse()) {
               inputPanel.inputEventXZ(
                       inputPanel.getLastX() + diffX * simulation.getSurfaceWidth() / panel.getWidth(),
                       inputPanel.getLastZ() + diffY * simulation.getSurfaceHeight()/ panel.getHeight());
            }
        } else if (lastButton == 2) {
            if (cameraLooked) {
                scene.getCamera().moveAzimuthAndInclination(-diffX * rotGain, -diffY * rotGain);
                scene.getCamera().updateObservation();
            } else {
                scene.getCamera().rotateLocal(-diffY * rotGain, -diffX * rotGain, 0);
            }
        } else if (lastButton == 3) {
            if (inputPanel.isMouse()) {
                inputPanel.inputEventRY( 
                        inputPanel.getLastRY() + diffX * -90.0f / panel.getWidth());
            } else {
                scene.getCamera().moveLocalSpace(-diffX * moveGain, diffY * moveGain, 0);
            }
        }

        needUpdate();

        lastX = x;
        lastY = y;
    }//GEN-LAST:event_panelMouseDragged

    private void panelMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_panelMouseWheelMoved
         float wheel = (float)evt.getPreciseWheelRotation();

        final float wheelGain = simPanel.getGUIGain() * 6f;
        final float value = wheel * wheelGain;
        if (cameraLooked) {
            scene.getCamera().setDistance(scene.getCamera().getDistance() + value);
            scene.getCamera().updateObservation();
        } else {
            scene.getCamera().moveLocalSpace(0, 0, value);
        }
        needUpdate();
    }//GEN-LAST:event_panelMouseWheelMoved

    private void lookCamera(Vector3f v){
        scene.getCamera().setOrtho(false);
        scene.getCamera().updateProjection( getGLAspect());
        scene.getCamera().activateObservation(true, v);
    }
    
    private void originCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_originCamMenuActionPerformed
        lookCamera(Vector3f.ZERO);
    }//GEN-LAST:event_originCamMenuActionPerformed

    private void centerCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_centerCamMenuActionPerformed
        lookCamera( simulation.getSimulationCenter() );
    }//GEN-LAST:event_centerCamMenuActionPerformed

    private void otherCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otherCamMenuActionPerformed
        String v = DialogUtils.getStringDialog(this, "Vector", "0.00 0.00 0.00");
        if (v != null){
            lookCamera( new Vector3f().parse(v) );
        }
    }//GEN-LAST:event_otherCamMenuActionPerformed

    public float getGLAspect(){
        return panel.getWidth() / (float) panel.getHeight();
    }
    
    public void clearSelection(){
        for(Entity e : selection){
            e.selected = false;
        }
        selection.clear();
    }
        
    private void resetCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetCamMenuActionPerformed
        scene.adjustCameraToSimulation(simulation, getGLAspect());
        needUpdate();
    }//GEN-LAST:event_resetCamMenuActionPerformed

    private void xTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_xTextFocusGained
        changeSlider(FieldsToChange.xField, "X", Simulation.MIN_SIZE * 8.0f);
    }//GEN-LAST:event_xTextFocusGained

    private void yTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_yTextFocusGained
        changeSlider(FieldsToChange.yField, "Y", Simulation.MIN_SIZE * 8.0f);
    }//GEN-LAST:event_yTextFocusGained

    private void zTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_zTextFocusGained
        changeSlider(FieldsToChange.zField, "Z", Simulation.MIN_SIZE * 8.0f);
    }//GEN-LAST:event_zTextFocusGained

    private void rxTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rxTextFocusGained
        changeSlider(FieldsToChange.rxField, "RX", 360);
    }//GEN-LAST:event_rxTextFocusGained

    private void ryTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ryTextFocusGained
        changeSlider(FieldsToChange.ryField, "RY", 360);
    }//GEN-LAST:event_ryTextFocusGained

    private void rzTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rzTextFocusGained
        changeSlider(FieldsToChange.rzField, "RZ", 360);
    }//GEN-LAST:event_rzTextFocusGained

    private void panelSliderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelSliderMouseDragged
        float diff = sliderPanel.touchDrag(evt.getX(), evt.getY()); 
        changeSelectionField(sliderField, diff * sliderScale, false, true);
        needUpdate();
    }//GEN-LAST:event_panelSliderMouseDragged

    public void updateTransForField(FieldsToChange field, String text){
        if (text.length() < 1) {return;}
        boolean absolute;
        float value;
        if (text.charAt(0) == 'a'){
            absolute = false;
            value = Parse.stringToFloat( text.substring(1));
        }else{
            absolute = true;
            value = Parse.stringToFloat( text );
        }
        changeSelectionField(field, value, absolute, false);
        needUpdate();
    }
    
    private void xTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xTextActionPerformed
        updateTransForField(FieldsToChange.xField, xText.getText());
    }//GEN-LAST:event_xTextActionPerformed

    private void rxTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rxTextActionPerformed
        updateTransForField(FieldsToChange.rxField, rxText.getText());
    }//GEN-LAST:event_rxTextActionPerformed

    private void yTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yTextActionPerformed
        updateTransForField(FieldsToChange.yField, yText.getText());
    }//GEN-LAST:event_yTextActionPerformed

    private void ryTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ryTextActionPerformed
        updateTransForField(FieldsToChange.ryField, ryText.getText());
    }//GEN-LAST:event_ryTextActionPerformed

    private void zTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zTextActionPerformed
        updateTransForField(FieldsToChange.zField, zText.getText());
    }//GEN-LAST:event_zTextActionPerformed

    private void rzTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rzTextActionPerformed
        updateTransForField(FieldsToChange.rzField, rzText.getText());
    }//GEN-LAST:event_rzTextActionPerformed

    
    private void panelSliderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelSliderMousePressed
        sliderPanel.touchDown(evt.getX(), evt.getY(), 0);
    }//GEN-LAST:event_panelSliderMousePressed

    private void panelSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelSliderMouseReleased
        sliderPanel.setShow( false );
        sliderPanel.repaint();
    }//GEN-LAST:event_panelSliderMouseReleased

    private void frontCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frontCamMenuActionPerformed
        scene.adjustCameraToFront(simulation, getGLAspect());
        needUpdate();
    }//GEN-LAST:event_frontCamMenuActionPerformed

    private void topCamMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topCamMenuActionPerformed
        scene.adjustCameraToTop(simulation, getGLAspect());
        needUpdate();
    }//GEN-LAST:event_topCamMenuActionPerformed

    private void syTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syTextActionPerformed
        updateTransForField(FieldsToChange.syField, syText.getText());
    }//GEN-LAST:event_syTextActionPerformed

    private void syTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_syTextFocusGained
        changeSlider(FieldsToChange.syField, "SY", simulation.maxDistanceBoundary() / 8.0f);
    }//GEN-LAST:event_syTextFocusGained

    private void sxTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sxTextActionPerformed
        updateTransForField(FieldsToChange.sxField, sxText.getText());
    }//GEN-LAST:event_sxTextActionPerformed

    private void sxTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sxTextFocusGained
        changeSlider(FieldsToChange.sxField, "SX", simulation.maxDistanceBoundary() / 8.0f);
    }//GEN-LAST:event_sxTextFocusGained

    private void szTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_szTextFocusGained
        changeSlider(FieldsToChange.szField, "SZ", simulation.maxDistanceBoundary() / 8.0f);
    }//GEN-LAST:event_szTextFocusGained

    private void szTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_szTextActionPerformed
       updateTransForField(FieldsToChange.szField, szText.getText());
    }//GEN-LAST:event_szTextActionPerformed

    private void kinectMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kinectMenuActionPerformed
        kinectForm.setVisible( true );
    }//GEN-LAST:event_kinectMenuActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        kinectForm.getWorker().renderTestFrame = true;
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void closeFullScreen(){
        gljpanel.removeKeyListener( gljpanel.getKeyListeners()[0] );
        fullFrame.remove(gljpanel);
        this.containerPanel.add(gljpanel);
        fullFrame.dispose();
        fullFrame = null;
        this.containerPanel.revalidate();
        repaint();
    }
    private void fullScreenMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullScreenMenuActionPerformed
        fullFrame = new JFrame();
        gljpanel.addKeyListener(new KeyListener() {
            @Override public void keyReleased(KeyEvent e) {}
            @Override public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                Log.log("Exiting full screen");
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    closeFullScreen();
                }
            } 
        });
        fullFrame.setUndecorated(true);  // no decoration such as title and scroll bars
        this.remove( gljpanel );
        fullFrame.getContentPane().add(gljpanel);
        showOnScreen(1, fullFrame);
        fullFrame.setUndecorated(true);     // no decoration such as title bar
        fullFrame.setExtendedState(Frame.MAXIMIZED_BOTH);  // full screen mode
        fullFrame.setVisible(true);
        fullFrame.requestFocus();
    }//GEN-LAST:event_fullScreenMenuActionPerformed

    public static void showOnScreen(int screen, JFrame frame) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        if (screen > -1 && screen < gd.length) {
            frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x, frame.getY());
        } else if (gd.length > 0) {
            frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY());
        } else {
            throw new RuntimeException("No Screens Found");
        }
    }
    
    private void jMenu7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu7ActionPerformed

    
    public void addMeshEntityToSceneCenter( MeshEntity me){
        me.getTransform().getTranslation().set( simulation.getSimulationCenter() );
        me.getTransform().getScale().set( simulation.maxDistanceBoundary() );
        scene.getEntities().add( me );
    }
    
    private void camProjMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_camProjMenuActionPerformed
        ProjectionForm pf = new ProjectionForm(scene.getCamera().getProjection());
        pf.setLocationRelativeTo(this);
        pf.setVisible(true);
    }//GEN-LAST:event_camProjMenuActionPerformed

    private void camViewMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_camViewMenuActionPerformed
        TransformForm tf = new TransformForm(scene.getCamera().getTransform());
        tf.setLocationRelativeTo(this);
        tf.setVisible(true);
    }//GEN-LAST:event_camViewMenuActionPerformed

    private void unlockCameraMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unlockCameraMenuActionPerformed
       cameraLooked = !cameraLooked;
    }//GEN-LAST:event_unlockCameraMenuActionPerformed

    
    private void screenCaptureMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_screenCaptureMenuActionPerformed
        String path = FileUtils.selectNonExistingFile(this, ".png");
        if(path != null){
            BufferedImage bi = new BufferedImage(gljpanel.getWidth(), gljpanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            gljpanel.paint( bi.getGraphics() );
            try {
                ImageIO.write(bi, "png", new File(path));
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_screenCaptureMenuActionPerformed

    private void camLookSelectionMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_camLookSelectionMenuActionPerformed
        if(! selection.isEmpty() ){
            lookCamera( selection.get(0).getTransform().getTranslation() );
        }
    }//GEN-LAST:event_camLookSelectionMenuActionPerformed
 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem camLookSelectionMenu;
    private javax.swing.JMenuItem camProjMenu;
    private javax.swing.JMenuItem camViewMenu;
    private javax.swing.JMenuItem centerCamMenu;
    private javax.swing.JPanel containerPanel;
    private javax.swing.ButtonGroup controlPointsColorGroup;
    private javax.swing.JMenuItem frontCamMenu;
    private javax.swing.JMenuItem fullScreenMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JMenuItem kinectMenu;
    private javax.swing.JTabbedPane mainTabPanel;
    private javax.swing.ButtonGroup maskObjectsGroup;
    private javax.swing.JMenuItem originCamMenu;
    private javax.swing.JMenuItem otherCamMenu;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel panelSlider;
    private javax.swing.ButtonGroup preCubeSource;
    private javax.swing.JMenuItem resetCamMenu;
    private javax.swing.JTextField rxText;
    private javax.swing.JTextField ryText;
    private javax.swing.JTextField rzText;
    private javax.swing.JMenuItem screenCaptureMenu;
    private javax.swing.ButtonGroup slicesSource;
    private javax.swing.JLabel sliderFieldLabel;
    private javax.swing.JTextField sxText;
    private javax.swing.JTextField syText;
    private javax.swing.JTextField szText;
    private javax.swing.JMenuItem topCamMenu;
    private javax.swing.JMenuItem unlockCameraMenu;
    private javax.swing.ButtonGroup wrapPlayButtonGroup;
    private javax.swing.JTextField xText;
    private javax.swing.JTextField yText;
    private javax.swing.JTextField zText;
    // End of variables declaration//GEN-END:variables

    public void needUpdate() {
        panel.repaint();
    }
    

    private void updateSelection(MouseEvent evt) {
        int x = evt.getX(); int y = evt.getY();
        int tags = EntityTag.NONE;
       if (mainTabPanel.getSelectedComponent() == miscPanel){
            tags |= EntityTag.MASK;
       }
        
        Entity e = scene.pickObject(
                lastX / (float) panel.getWidth(),
                1.0f - lastY / (float) panel.getHeight(), tags);
        if ( e == null ){
            clearSelection();
            needUpdate();
            return;
        }

        
        if ((evt.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
           if(selection.contains(e)){
                selection.remove(e);
                e.selected = false;
            }else{
                e.selected = true;
                selection.add(e);
                entityToGUI(e); 
            }
        } else {
            clearSelection();
            e.selected = true;
            selection.add(e);
            entityToGUI(e);
        }

        needUpdate();
    }

    private void entityToGUI(Entity e) {
       xText.setText( StringUtils.get().fourDecs(e.getTransform().getTranslation().x ));
       yText.setText( StringUtils.get().fourDecs( e.getTransform().getTranslation().y ));
       zText.setText( StringUtils.get().fourDecs( e.getTransform().getTranslation().z ));
       
       float[] angles = new float[3];
       e.getTransform().getRotation().toAngles(angles);
       rxText.setText( StringUtils.get().fourDecs( angles[0] * FastMath.RAD_TO_DEG));
       ryText.setText( StringUtils.get().fourDecs( angles[1] * FastMath.RAD_TO_DEG ));
       rzText.setText( StringUtils.get().fourDecs( angles[2] * FastMath.RAD_TO_DEG ));
       
       
       sxText.setText( StringUtils.get().fourDecs( e.getTransform().getScale().x ));
       syText.setText( StringUtils.get().fourDecs( e.getTransform().getScale().y ));
       szText.setText( StringUtils.get().fourDecs( e.getTransform().getScale().z ));
    }

    public enum FieldsToChange{
        xField, yField, zField, rxField, ryField, rzField,
        wField, frField,
        ampField, phaseField,
        sxField, syField, szField
    };
    private FieldsToChange sliderField;
    private float sliderScale;
    public void changeSlider(FieldsToChange field, String name, float scale){
        sliderField = field;
        sliderFieldLabel.setText(name);
        sliderScale = scale;
    }
    
    private void changeSelectionField(FieldsToChange field, float value, boolean absolute, boolean updateTextField){
        float[] angles = new float[3];
        
        for(Entity e : selection){
            Transform tra = e.getTransform();
            
            if(field == FieldsToChange.xField){
                tra.getTranslation().x = absolute ? value : tra.getTranslation().x + value;
                if (updateTextField) { xText.setText( StringUtils.get().fourDecs(tra.getTranslation().x)); }
            }else if(field == FieldsToChange.yField){
                tra.getTranslation().y = absolute ? value : tra.getTranslation().y + value;
                if (updateTextField) { yText.setText( StringUtils.get().fourDecs(tra.getTranslation().y)); }
            }else if(field == FieldsToChange.zField){
                tra.getTranslation().z = absolute ? value : tra.getTranslation().z + value;
                if (updateTextField) { zText.setText( StringUtils.get().fourDecs(tra.getTranslation().z)); }
            }else if(field == FieldsToChange.sxField){
                tra.getScale().x = absolute ? value : tra.getScale().x + value;
                if (updateTextField) { sxText.setText( StringUtils.get().fourDecs( tra.getScale().x )); }
            }else if(field == FieldsToChange.syField){
                 tra.getScale().y = absolute ? value : tra.getScale().y + value;
                if (updateTextField) { syText.setText( StringUtils.get().fourDecs( tra.getScale().y )); }
            }else if(field == FieldsToChange.szField){
                 tra.getScale().z = absolute ? value : tra.getScale().z + value;
                if (updateTextField) { szText.setText( StringUtils.get().fourDecs( tra.getScale().z )); }
            }else if(field == FieldsToChange.rxField || 
                    field == FieldsToChange.ryField || 
                    field == FieldsToChange.rzField){
                float rads = value * FastMath.DEG_TO_RAD;
                Quaternion q = tra.getRotation();
                q.toAngles(angles);
                if(field == FieldsToChange.rxField) {
                    angles[0] = absolute ? rads : angles[0] + rads;
                    if (updateTextField) { rxText.setText( StringUtils.get().fourDecs(angles[0] * FastMath.RAD_TO_DEG)); }
                }else if (field == FieldsToChange.ryField) {
                    angles[1] = absolute ? rads : angles[1] + rads;
                    if (updateTextField) { ryText.setText( StringUtils.get().fourDecs(angles[1] * FastMath.RAD_TO_DEG)); }
                }else if (field == FieldsToChange.rzField) {
                    angles[2] = absolute ? rads : angles[2] + rads;
                    if (updateTextField) { rzText.setText( StringUtils.get().fourDecs(angles[2] * FastMath.RAD_TO_DEG)); }
                }
                q.fromAngles(angles);
            }
           
            updateTextField = false; //only use the first transducer, only one value can be displayed in the text field
        }
    }
    
    public Scene getScene() {
        return scene;
    }

    public ArrayList<Entity> getSelection() {
        return selection;
    }
   
}
