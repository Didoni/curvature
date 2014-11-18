/**
 * @file SerialComms.java
 */
/**
 * @package com.ultrahaptics.api
 * @brief The package for the UltraHaptics API
 */
package marionettesim.utils.uartComm;

import marionettesim.utils.DialogUtils;
import java.io.*;
//import gnu.io.*;
import java.util.Vector;

/**
 * @brief Provides methods for sending data to the driver boards via the UART
 * link.
 * @author Tom Carter
 * @date 2012
 * @version 0.1
 * @pre Requires RXTX (http://rxtx.qbang.org).
 */
public class SerialComms implements NetworkInterface {

    /**
     * @brief Speed of the serial port.
     */
    private static int speed = 115200;
    /**
     * @brief The {@link Network}.
     */
    private Network network;

    /**
     * @brief Constructor for a new instance of SerialComms.
     */
    public SerialComms() {
        network = new Network(0, this, 255);

        // Get list of available serial ports.
        Vector<String> ports = network.getPortList();
       
        if (ports.isEmpty()) {
            DialogUtils.showError(null, "No Serial Ports", "No serial ports where found");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ports.size(); ++i) {
            sb.append( (i+1) + " -> " + ports.elementAt(i) + " || ");
        }

        int inpNum = Integer.parseInt( DialogUtils.getStringDialog(null, "Select Port: " + sb.toString(), "1") );


        // Connect to the port.
        if (network.connect(ports.elementAt(inpNum - 1), speed)) {
            System.out.println("Connected.");
        } else {
            DialogUtils.showError(null, "Error", "There was an error connecting");
        }
    }
    
    public void disconnect(){
        network.disconnect();
    }

    public void write(int length, int message[]) {
        network.writeSerial(length, message);
    }

    /**
     * @brief Implementing {@link comms.NetworkIface::networkDisconnected}.
     * @details Called when the connection has been closed.
     * @see {@link comms.NetworkIface::networkDisconnected}
     */
    @Override
    public void networkDisconnected(int id) {
    }

    /**
     * @brief Implementing {@link comms.NetworkIface::parseInput}.
     * @details Handles messages received over the serial port. Currently just
     * prints it to the terminal.
     * @see {@link comms.NetworkIface::parseInput}
     */
    @Override
    public void parseInput(int id, int numBytes, int[] message) {
        System.out.print("Received the following message: ");
        System.out.print(message[0]);
        for (int i = 1; i < numBytes; i++) {
            System.out.print(", ");
            System.out.print(message[i]);
        }
        System.out.println();
    }

    /**
     * @brief Implements {@link comms.NetworkIface::writeLog}.
     * @details Used to write information concerning the connection. Currently,
     * all information is written to the command line.
     * @see {@link comms.NetworkIface::writeLog}
     */
    @Override
    public void writeLog(int id, String text) {
        //System.out.println("   log:  |" + text + "|");
    }
    
    
}
