package com.wozart.aura.aura.network;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/***************************************************************************
 * File Name : TcpServer
 * Author : Aarth Tandel
 * Date of Creation : 29/12/17
 * Description : TCP server to listen to messages from Aura device
 * Revision History :
 * ____________________________________________________________________________
 * 29/12/17  Aarth Tandel - Initial Commit
 * ____________________________________________________________________________
 * 29/12/17 Version 1.0
 * ____________________________________________________________________________
 *
 *****************************************************************************/

public class TcpServer extends Service {
    private static String TAG = "TcpServer";
    private ServerSocket serverSocket;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
        Log.d(TAG, "Server started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        Log.d(TAG, "Server stopped");
        super.onDestroy();
    }

    private class SocketServerThread extends Thread {

        public void run() {

            try {
                ServerSocket soServer = new ServerSocket(2345);
                Socket socClient = null;
                while(!Thread.currentThread().isInterrupted()){
                    socClient = soServer.accept();
                    ServerAsyncTask serverAsyncTask = new ServerAsyncTask();
                    serverAsyncTask.execute(new Socket[] { socClient });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class ServerAsyncTask extends AsyncTask<Socket, Void, String> {
        @Override
        protected String doInBackground(Socket... params) {
            String result = null;
            Socket mySocket = params[0];
            try {

                InputStream is = mySocket.getInputStream();
                PrintWriter out = new PrintWriter(mySocket.getOutputStream(),
                        true);

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));

                result = br.readLine();

                //mySocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendMessageToActivity(result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {


        }
    }

    private void sendMessageToActivity(String msg) {
        Intent intent = new Intent("intentKey");
        intent.putExtra("key", msg);
        LocalBroadcastManager.getInstance(TcpServer.this).sendBroadcast(intent);
    }
}
