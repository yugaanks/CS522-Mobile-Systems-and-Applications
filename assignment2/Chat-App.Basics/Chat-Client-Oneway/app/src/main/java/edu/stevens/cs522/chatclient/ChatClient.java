/*********************************************************************
 * Client for sending chat messages to the server..
 * <p>
 * Copyright (c) 2012 Stevens Institute of Technology
 **********************************************************************/
package edu.stevens.cs522.chatclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static edu.stevens.cs522.chatclient.R.id.send_button;

/*
 * @author dduggan
 * 
 */
public class ChatClient extends Activity implements OnClickListener {

    final static private String TAG = ChatClient.class.getCanonicalName();

    /*
     * Client name (should be entered in a parent activity, provided as an extra in the intent).
     */
    public static final String CLIENT_NAME_KEY = "client_name";

    public static final String DEFAULT_CLIENT_NAME = "client";

    private String clientName;

    /*
     * Client UDP port (should be entered in a parent activity, provided as an extra in the intent).
     */
    public static final String CLIENT_PORT_KEY = "client_port";

    public static final int DEFAULT_CLIENT_PORT = 6666;

    /*
     * Socket used for sending
     */
    private DatagramSocket clientSocket;

    private int clientPort;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText destinationHost;

    private EditText destinationPort;

    private EditText messageText;

    private Button sendButton;

    /*
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent callingIntent = getIntent();
        if (callingIntent != null && callingIntent.getExtras() != null) {

            clientName = callingIntent.getExtras().getString(CLIENT_NAME_KEY, DEFAULT_CLIENT_NAME);
            clientPort = callingIntent.getExtras().getInt(CLIENT_PORT_KEY, DEFAULT_CLIENT_PORT);

        } else {

            clientName = DEFAULT_CLIENT_NAME;
            clientPort = DEFAULT_CLIENT_PORT;

        }

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the main thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // TODO initialize the UI. done
        destinationHost = (EditText) findViewById(R.id.destination_host);
        destinationPort = (EditText) findViewById(R.id.destination_port);
        messageText = (EditText) findViewById(R.id.message_text);
        sendButton = (Button) findViewById(send_button);

        try {

            clientSocket = new DatagramSocket(clientPort);

        } catch (Exception e) {
            Log.e(TAG, "Cannot open socket: " + e.getMessage(), e);
            return;
        }
        sendButton.setOnClickListener(this);
    }

    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {
        if (v.getId() == R.id.send_button) {
            try {
            /*
			 * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */
                InetAddress destAddr;
                int destPort;
                byte[] sendData;  // Combine sender and message text; default encoding is UTF-8
                String message = "";
                // TODO get data from UI done
                destAddr = InetAddress.getByName(destinationHost.getText().toString());
                destPort = Integer.parseInt(destinationPort.getText().toString());
                message = clientName + ": " + messageText.getText().toString();
                sendData = message.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData,
                        sendData.length, destAddr, destPort);
                clientSocket.send(sendPacket);

                Log.i(TAG, "Sent packet: " + messageText);


            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown host exception: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IO exception: " + e.getMessage());
            } catch(NullPointerException e) {
                Log.e(TAG,"Null pointer exception "+e.getMessage());
            }
            messageText.setText("");
        }
    }
}