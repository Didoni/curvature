/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.workers;

import marionettesim.gui.UDPControlForm;
import marionettesim.simulation.Simulation;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asier
 */
public class UdpControlWorker extends Thread{
    UDPControlForm form;
    Simulation simulation;
    
    DatagramSocket serverSocket;
    public UdpControlWorker(UDPControlForm form, Simulation simulation) {
        this.form = form;
        this.simulation = simulation;
    }
    
    @Override
    public void run() {
        try {
            serverSocket = new DatagramSocket( form.getPort() );
            byte[] recData = new byte[1024];
            DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
            while(!interrupted()){
                try {
                    serverSocket.receive(recPacket); 
                    String msg = new String(recPacket.getData(), 0, recPacket.getLength());
                    int command = Integer.parseInt(msg);
                   //TODO
                    if (form.isRepaint()){
                        form.getMf().needUpdate();
                    }
                } catch (IOException ex) {
                     Logger.getLogger(UdpControlWorker.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 yield();
            }
            
        } catch (SocketException ex) {
            Logger.getLogger(UdpControlWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
