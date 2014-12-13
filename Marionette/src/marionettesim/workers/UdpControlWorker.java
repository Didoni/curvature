/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import marionettesim.gui.MainForm;
import marionettesim.utils.Parse;

/**
 *
 * @author Asier
 */
public class UdpControlWorker extends Thread{
    MainForm mf;
    
    DatagramSocket serverSocket;
    public UdpControlWorker(MainForm mf) {
        this.mf = mf;
    }

    @Override
    public void run() {
        try {
            final int port = mf.inputPanel.getPort();
            final String addr = mf.inputPanel.getAddress();
            
            serverSocket = new DatagramSocket();
            
            byte[] recData = new byte[1024];
            byte[] sendData = new byte[1024];
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length);
            sendPacket.setAddress(InetAddress.getByName(addr));
            sendPacket.setPort(port);
            
            DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
            while(!interrupted()){
                try {
                    //send output angles (8 float angles)
                    String angles = mf.outputPanel.getAnglesString();
                    sendData = angles.getBytes();
                    sendPacket.setData(sendData);
                    serverSocket.send(sendPacket);
                    
                    //Read input from OptiTrack	(3 floats)
                    serverSocket.receive(recPacket); 
                    String msg = new String(recPacket.getData(), 0, recPacket.getLength());
                    String[] split = msg.split(" ");
                    if(split.length > 2){
                        float x = Parse.stringToFloat(split[0]);
                        float y = Parse.stringToFloat(split[1]);
                        float rz = Parse.stringToFloat(split[2]);

                        mf.inputPanel.inputEventXZRY(-y, -x, rz);

                        mf.needUpdate();
                    }
                } catch (IOException ex) {
                     Logger.getLogger(UdpControlWorker.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 yield();
            }
            
        } catch (SocketException ex) {
            Logger.getLogger(UdpControlWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(UdpControlWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
