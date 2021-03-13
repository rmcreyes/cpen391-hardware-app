package com.cpen391.hardwareapp;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/** Implements basic Bluetooth communication based on
 * Cpen 391 - Lecture 6c - Bluetooth Communications.pptx, and
 * https://developer.android.com/guide/topics/connectivity/bluetooth
 **/

public class BluetoothThread extends Thread {
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream
    private Handler mHandler;
    private int count;

    public BluetoothThread(BluetoothSocket socket, InputStream instm, OutputStream outsm, Handler handler) {
        mmSocket = socket;
        mmInStream = instm;
        mmOutStream = outsm;
        mHandler = handler;
        count = 0;
    }

    public void run(){
        // Keep listening to the InputStream while connected
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()
        while (true) {
            try {
                //read the data from socket stream
                numBytes = mmInStream.read(mmBuffer);
                // Send the obtained bytes to the UI Activity
                mHandler.obtainMessage(Constants.MESSAGE_READ, numBytes, -1, mmBuffer)
                        .sendToTarget();
            } catch (IOException e) {
                //an exception here marks connection loss
                //send message to UI Activity
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void WriteToBTDevice(String message) {
        String s = new String("\r\n");

        byte[] msgBuffer = message.getBytes();
        byte[] newline = s.getBytes();
        try {
            mmOutStream.write(msgBuffer);
            mmOutStream.write(newline);
        } catch (IOException e) {
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}
