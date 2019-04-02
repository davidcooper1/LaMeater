package com.example.lameater;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.TextView;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;


public class TemperatureFetcher {

    // This is the default UUID for a bluetooth serial device.
    private static final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // These are the IDs for callback functions.
    public static final int CALLBACK_DISCONNECT    = 0x00000001;
    public static final int CALLBACK_CONNECT       = 0x00000002;
    public static final int CALLBACK_CONNECTING    = 0x00000004;
    public static final int CALLBACK_DATA_RECEIVED = 0x00000008;

    // Instance of ConnectThread so that a connection can be closed at any time if need be.
    private ConnectThread c;

    // Used for synchronization of the callbacksEnabled property.
    private ReentrantLock callbackLock;

    // Used to check if it is safe to execute callbacks at the current time.
    private boolean callbacksEnabled;

    // Reference to whatever callback may be executing.
    private Thread callback;

    // Runnables that will be executed for callbacks.
    private Runnable disconnectRunnable;
    private Runnable dataReceivedRunnable;
    private Runnable connectRunnable;
    private Runnable connectingRunnable;

    // Used for synchronization of the data propery.
    private ReentrantLock dataLock;

    // Data string from bluetooth device.
    private String data;

    // Captures events related to bluetooth device discovery.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("ACTION", action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) { // A bluetooth device's broadcast was received.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                Log.d("DISCOVERY", device.getName() + " : " + device.getAddress());
                if (deviceName != null && deviceName.equals("LaMeater")) {
                    int state = device.getBondState();
                    if (state == BluetoothDevice.BOND_NONE) { // LaMeater was found but not previously connected.
                        // Create a pairing request.
                        device.createBond();
                    } else if (state == BluetoothDevice.BOND_BONDED){ // LaMeater was found and already connected.
                        // Stop discovery mode to ensure a fast connection.
                        if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
                            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        }
                        // Create the connection.
                        connectToDevice(device);
                    }
                }
            } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) { // A pairing request was initiated.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                if (deviceName != null && deviceName.equals("LaMeater")) {
                    // Sets the pin of the LaMeater pairing request.
                    try {
                        device.setPin("1234".getBytes("UTF-8"));
                    } catch (Exception e) {

                    }
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) { // Bluetooth device unpaired, started pairing, or is paired.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();

                if (deviceName != null && deviceName.equals("LaMeater")) {
                    // If LaMeater was just paired then start receiving data.
                    int bond = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    if (bond == BluetoothDevice.BOND_BONDED) {
                        if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
                            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        }
                        Log.d("CREATE", "Bond Created!");
                        connectToDevice(device);
                    }
                }
            }
        }
    };

    private final IntentFilter filter = new IntentFilter();

    public TemperatureFetcher() {
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        callbackLock = new ReentrantLock();
        dataLock = new ReentrantLock();
    }

    // Checks if LaMeater was already paired if not try to find and connect to it.
    public void connect() {
        // Check if device is already paired, if not pair it then create the connection.
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        BluetoothDevice device = findDeviceFromPaired();
        if (device != null) {
            Log.d("EVENT", "Device found!");
            connectToDevice(device);
        } else {
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
        }
    }

    // Disconnects from LaMeater
    public void disconnect() {
        if (c != null) {
            c.cancel();
            try {
                c.join();
            } catch (InterruptedException e) {
                Log.e("ERROR", "Unable to stop ConnectThread.", e);
            }
        }
    }

    // Checks if LaMeater was already paired.
    public BluetoothDevice findDeviceFromPaired() {
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("LaMeater")) {
                    return device;
                }
            }
        }
        return null;
    }

    // Attempts to connect to LaMeater.
    public void connectToDevice(BluetoothDevice device) {
        attemptCallback(CALLBACK_CONNECTING);
        disconnect();
        c = new ConnectThread(device);
        c.start();
    }

    public BroadcastReceiver getReceiver() {
        return this.receiver;
    }

    public IntentFilter getFilter() {
        return this.filter;
    }

    public void setCallbacksEnabled(boolean state) {
        callbackLock.lock();

        try {
            if (callback != null)
                callback.join();
            callbacksEnabled = state;
        } catch (InterruptedException e) {
            Log.e("ERROR", "Error while waiting for callback thread.", e);
        } finally {
            callbackLock.unlock();
        }
    }

    public void setCallback(int callbackId, Runnable callback) {
        setCallbacksEnabled(false);
        switch(callbackId) {
            case CALLBACK_DATA_RECEIVED :
                dataReceivedRunnable = callback;
                break;

            case CALLBACK_DISCONNECT :
                disconnectRunnable = callback;
                break;
        }
    }

    private void attemptCallback(int callbackId) {
        boolean hasLock = callbackLock.tryLock();

        if (hasLock) {
            try {
                if (callbacksEnabled) {
                    Runnable callbackRunnable = null;

                    switch (callbackId) {
                        case CALLBACK_DATA_RECEIVED:
                            callbackRunnable = dataReceivedRunnable;
                            break;
                        case CALLBACK_DISCONNECT:
                            callbackRunnable = disconnectRunnable;
                            break;
                        case CALLBACK_CONNECT:
                            callbackRunnable = connectRunnable;
                        case CALLBACK_CONNECTING:
                            callbackRunnable = connectingRunnable;
                    }
                    callback = new Thread(callbackRunnable);
                    callback.start();
                }
            } finally {
                callbackLock.unlock();
            }
        }
    }

    public String getData() {
        dataLock.lock();
        String result;
        try {
            result = new String(data);
        } finally {
            dataLock.unlock();
        }

        return result;
    }

    public void setData(String value) {
        dataLock.lock();
        try {
            data = value;
        } finally {
            dataLock.unlock();
        }
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket socket;
        private ConnectedThread connected;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
            } catch (IOException e) {
                Log.e("ERROR", "Failed to create socket.", e);
            }

            socket = tmp;
        }

        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {

            }

            try {
                Log.d("CONNECT", "Trying to connect to socket.");
                Log.d("CONNECT", "Bluetooth discovery is " + ((BluetoothAdapter.getDefaultAdapter().isDiscovering())));
                socket.connect();
                Log.d("CONNECT", "Socket connected.");
            } catch (IOException err) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("ERROR", "Coud not close client socket.", e);
                }
                return;
            }

            attemptCallback(CALLBACK_CONNECT);
            connected = new ConnectedThread(socket);
            connected.start();
            try {
                connected.join();
            } catch (InterruptedException e) {
                Log.e("ERROR", "Error while closing ConnectedThread.", e);
            }

            attemptCallback(CALLBACK_DISCONNECT);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e("ERROR", "Could not close client socket.", e);
            }
        }

    }

    // This class is responsible for managing the current bluetooth connection.
    private class ConnectedThread extends Thread {

        BluetoothSocket socket;
        InputStream in;

        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
            InputStream tmpIn = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e("ERROR", "Error occured when creating input stream.", e);
            }

            in = tmpIn;
        }

        public void run() {
            Scanner input = new Scanner(in);
            input.useDelimiter("\r\n");

            while(true) {
                try {
                    final String line = input.next();
                    setData(line);
                    Log.d("DATA", (new Date()).toString());
                    attemptCallback(CALLBACK_DATA_RECEIVED);
                } catch (Exception e) {
                    Log.e("ERROR", "Input stream was disconnected.", e);
                    break;
                }
            }
        }


    }

}
