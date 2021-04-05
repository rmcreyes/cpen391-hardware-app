package com.cpen391.hardwareapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    /* Implements basic Bluetooth communication based on
    * Cpen 391 - Lecture 6c - Bluetooth Communications.pptx, and
    * https://developer.android.com/guide/topics/connectivity/bluetooth
    **/

    /* bluetooth setup:
     * Checks if bluetooth is available and enabled
     * Establishes an connection with first paired device
     * NOTE: Assumes tablet only paired with one device.
     *       Modify logic if tablet will be paired with more than one device,
     *       and we need to specify which one to connect with
     * */
    public static SharedPreferences sp;
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private Context context;
    private Handler mHandler;
    private static BluetoothThread btThread;
    private FragmentManager fragmentManager;

    // a bluetooth “socket” to a bluetooth device
    private static BluetoothSocket mmSocket = null;

    // input/output “streams” with which we can read and write to device
    // Use of “static” important, it means variables can be accessed
    // without an object, this is useful as other activities can use
    // these streams to communicate after they have been opened.
    public static InputStream mmInStream = null;
    public static OutputStream mmOutStream = null;

    // indicates if we are connected to a device
    private boolean Connected = false;
    // we want to display all paired devices to the user in a ListView so they can choose a device
    private ArrayList<BluetoothDevice> Paireddevices = new ArrayList<BluetoothDevice>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("meter",MODE_PRIVATE);
        sp.edit().putInt(Constants.unitPriceStr, Constants.unitPrice).apply();

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        context = getApplicationContext();
        fragmentManager = getSupportFragmentManager();

        /* bluetooth setup:
        * Checks if bluetooth is available and enabled
        * Establishes an connection with first paired device
        * NOTE: Assumes tablet only paired with one device.
        *       Modify logic if tablet will be paired with more than one device,
        *       and we need to specify which one to connect with
        * */

        /* Handler when bluetooth thread receives a message */
        mHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case Constants.MESSAGE_READ:
                         String readMessage = (String) msg.obj;

                        /* Get current displaying fragment to process message accordingly */
                        Fragment navHostFragment = fragmentManager.findFragmentById(R.id.navHostFragment);
                        btFragment fragment = (btFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                        fragment.readBtData(readMessage);
                }
            }
        };
        // This call returns a handle to the one bluetooth device within your Android device
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // check to see if your android device even has a bluetooth device !!!!,
        if (mBluetoothAdapter == null) {
            Toast toast = Toast.makeText(context, "No Bluetooth !!", Toast.LENGTH_LONG);
            toast.show();
            finish();       // if no bluetooth device on this tablet don’t go any further.
            return;
        }

        // If the bluetooth device is not enabled, let’s turn it on

        if (!mBluetoothAdapter.isEnabled()) {

            // create a new intent that will ask the bluetooth adaptor to “enable” itself.
            // A dialog box will appear asking if you want turn on the bluetooth device

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            // REQUEST_ENABLE_BT below is a constant (defined as '1 - but could be anything)
            // When the “activity” is run and finishes, Android will run your onActivityResult()
            // function (see next page) where you can determine if it was successful or not

            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> thePairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are devices that have already been paired
        // get an iterator for the set of devices and iterate 1 device at a time
        if (thePairedDevices.size() > 0) {
            Iterator<BluetoothDevice> iter = thePairedDevices.iterator();
            BluetoothDevice aNewdevice;
            while (iter.hasNext()) {        // while at least one more device
                aNewdevice = iter.next();             // get next element in set

                //add the new device details to the array
                Paireddevices.add(aNewdevice);
            }

            BluetoothDevice HCDevice = null;
            if(Paireddevices.size() == 0) {
                Toast.makeText(context, "No paired Devices. Will fail.", Toast.LENGTH_LONG).show();
            }

            for (BluetoothDevice elem : Paireddevices) {
                if (elem.getName().equals(Constants.BLUETOOTH_DEVICE_NAME_RFS)) {
                    HCDevice = elem;
                    break;
                }
            }

            if (HCDevice == null) {
                Toast.makeText(context, "Could not find RFS Bluetooth pairing, using first paired device.", Toast.LENGTH_LONG).show();
                HCDevice = Paireddevices.get(0);
            }

            /* connect to first paired device */
            CreateSerialBluetoothDeviceSocket(HCDevice);
            ConnectToSerialBlueToothDevice();

            /* Start bluetooth thread */
            btThread = new BluetoothThread(mmSocket, mmInStream, mmOutStream, mHandler);
            btThread.start();
        }
    }

    /**
     * Create a socket with the bluetooth device
     * @param device
     */
    private void CreateSerialBluetoothDeviceSocket(BluetoothDevice device) {
        mmSocket = null;

        // universal UUID for a serial profile RFCOMM bluetooth device
        // this is just one of those “things” that you have to do and just works
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        // Get a Bluetooth Socket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Toast.makeText(context, "Socket Creation Failed", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * connect with the device and set up input and output stream
     */
    private void ConnectToSerialBlueToothDevice() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Attempt connection to the device through the socket.
            mmSocket.connect();
            Toast.makeText(context, "Connection Made", Toast.LENGTH_LONG).show();
        } catch (IOException connectException) {
            Toast.makeText(context, "Connection Failed", Toast.LENGTH_LONG).show();
            return;
        }

        //create the input/output stream and record fact we have made a connection
        GetInputOutputStreamsForSocket();
        Connected = true;
    }

    // gets the input/output stream associated with the current socket
    private void GetInputOutputStreamsForSocket() {
        try {
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
        } catch (IOException e) {
        }
    }

    public static void btWrite(String msg){
        if (btThread != null) {
            btThread.WriteToBTDevice(msg);
        }
    }

    /* disable using back button on this app */
    @Override
    public void onBackPressed() {

    }
}

