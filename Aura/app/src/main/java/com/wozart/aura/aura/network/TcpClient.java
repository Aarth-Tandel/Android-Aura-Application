package com.wozart.aura.aura.network;

import android.util.Log;

import com.wozart.aura.aura.utilities.Constant;
import com.wozart.aura.aura.utilities.Encryption;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/***************************************************************************
 * File Name : TcpClient
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : TCP client to exchange JSON dat with Aura device
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class TcpClient {
    public static final int SERVER_PORT = 2345;
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) {
        if (mBufferOut != null && !mBufferOut.checkError()) {
            mBufferOut.println(message);
            mBufferOut.flush();
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        // send message that we are closing the connection
        sendMessage(Constant.CLOSED_CONNECTION);

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run(String data, String ip) {

        mRun = true;
        String encryptedData = Encryption.enryptMessage(data);
        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(ip);

            //create a socket to make the connection with the server
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(serverAddr, SERVER_PORT), 5000);
            } catch (Exception e){
                Log.e("TCP Client", "Error: " + e);
            }
            try {

                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // send login name
                sendMessage(encryptedData);
                Log.i("TCP Cleint","TCP TX Data : " + data);
                //in this while the client listens for the messages sent by the server
                int count = 0;
                boolean messageFlag = false;
                while (mRun && count < 1000 && !messageFlag)  {

                    mServerMessage = mBufferIn.readLine();
                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(mServerMessage);
                        messageFlag = true;
                    }
                    count++;
                }
                if(!messageFlag)
                    mMessageListener.messageReceived("Server Not Reachable");

            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
                stopClient();
            }

        } catch (Exception e) {
            mMessageListener.messageReceived(Constant.SERVER_NOT_REACHABLE);
            Log.e("TCP", "C: Error", e);
        }
    }
    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(String message);
    }
}
