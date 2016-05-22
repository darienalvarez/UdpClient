package com.flomio.udpclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by darien
 * on 5/16/16.
 *
 * Start and UDP Client task
 * The background process receive the port by parameter.
 * If the port is not provided the process start in the DEFAULT PORT
 * The DEFAULT PORT is 10001
 */
public class UdpClient extends AsyncTask<Integer, Void, Void> {

    private static final String TAG = "UDPClient";
    private static final int DEFAULT_PORT = 10001;

    private boolean mProcessDatagram;

    public UdpClient() {
        this.mProcessDatagram = true;
    }

    protected Void doInBackground(Integer... params) {

        DatagramSocket serverSocket = null;
        try {
            int port = DEFAULT_PORT;
            if (params != null && params.length == 1) {
                port = params[0];
            }

            serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[32];

            Log.i(TAG, String.format("Listening on udp:%s:%d",
                    InetAddress.getLocalHost().getHostAddress(), port));

            String sendString = "tyco";
            byte[] sendData = sendString.getBytes("UTF-8");

            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            while (mProcessDatagram) {
                serverSocket.receive(receivePacket);
                String tag = new String(receivePacket.getData(), 0,
                        receivePacket.getLength());
                Log.i(TAG, "RECEIVED: " + tag);

                Intent i = new Intent();
                i.setAction(MainActivity.MESSAGE_RECEIVED);
                i.putExtra(MainActivity.MESSAGE_STRING, tag);
                // now send acknowledgement packet back to sender
                InetAddress IPAddress = receivePacket.getAddress();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                        IPAddress, receivePacket.getPort());
                serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
            Log.e(TAG, "error", e);
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        stopUdpClient();
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i(TAG, "Process done");
    }

    public void stopUdpClient() {
        mProcessDatagram = false;
    }
}
