/*********************************************************************
 * Chat server: accept chat messages from clients.
 * <p>
 * Sender name and GPS coordinates are encoded
 * in the messages, and stripped off upon receipt.
 * <p>
 * Copyright (c) 2017 Stevens Institute of Technology
 **********************************************************************/
package edu.stevens.cs522.chatserver;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class ChatServer extends Activity implements OnClickListener {

    final static public String TAG = ChatServer.class.getCanonicalName();

    /*
     * Socket used both for sending and receiving
     */
    private DatagramSocket serverSocket;

    /*
     * True as long as we don't get socket errors
     */
    private boolean socketOK = true;

    /*
     * TODO: Declare a listview for messages, and an adapter for displaying messages. done
     */
    ArrayList<String> messages;
    private static ListView messageView;
    private ArrayAdapter<String> messageAdapter;
    private Button button;

    /*
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the main thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            /*
			 * Get port information from the resources.
			 */
            messages = new ArrayList<String>();
            messageView = (ListView) findViewById(R.id.msgList);
            messageAdapter = new ArrayAdapter<String>(this, R.layout.message, messages);
            messageView.setAdapter(messageAdapter);
            int port = Integer.parseInt(this.getString(R.string.app_port));
            serverSocket = new DatagramSocket(port);
            System.out.println("socket initialized");
        } catch (Exception e) {
            Log.e(TAG, "Cannot open socket" + e.getMessage());
            return;
        }

		/*
		 * TODO: Initialize the UI. done
		 */
        button =(Button)findViewById(R.id.next);
        button.setOnClickListener(this);

        registerForContextMenu(messageView);
    }

    public void onClick(View v) {

        System.out.println("button clicked");
        byte[] receiveData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        System.out.println("receive packet initialised");
        try {

            serverSocket.receive(receivePacket);
            System.out.println("Received a packet");
            Log.i(TAG, "Received a packet");

            InetAddress sourceIPAddress = receivePacket.getAddress();
            Log.i(TAG, "Source IP Address: " + sourceIPAddress);
            System.out.println("Source IP Address: " + sourceIPAddress);
			/*
			 * TODO: Extract sender and receiver from message and display. done
			 */
            receiveData = receivePacket.getData();
            System.out.println("receive data: "+receiveData.toString());
            String origMessage[] = new String(receiveData).split(":");
            System.out.println("sender: "+origMessage[0]+" \n message: "+origMessage[1]);
            messages.add(origMessage[0]+":"+origMessage[1]);
            System.out.println("message added");
            messageAdapter.notifyDataSetChanged();
            System.out.println("change notified");

        } catch (Exception e) {

            Log.e(TAG, "Problems receiving packet: " + e.getMessage());
            socketOK = false;
        }

    }

    /*
     * Close the socket before exiting application
     */
    public void closeSocket() {
        serverSocket.close();
    }

    /*
     * If the socket is OK, then it's running
     */
    boolean socketIsOK() {
        return socketOK;
    }

}