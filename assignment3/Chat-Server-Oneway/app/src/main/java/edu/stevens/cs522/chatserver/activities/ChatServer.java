/*********************************************************************
 * Chat server: accept chat messages from clients.
 * <p>
 * Sender name and GPS coordinates are encoded
 * in the messages, and stripped off upon receipt.
 * <p>
 * Copyright (c) 2017 Stevens Institute of Technology
 **********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.databases.MessagesDbAdapter;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

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
     * UI for displayed received messages
     */
    private SimpleCursorAdapter messages;

    private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessagesDbAdapter messagesDbAdapter;

    private Button next;

    Cursor cursor;

    /*
     * Use to configure the app (user name and port)
     */
    private SharedPreferences settings;

    /*
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the messages thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /**
         * Initialize settings to default values.
         */
        if (savedInstanceState == null) {
            PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        }

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        int port = Integer.valueOf(settings.getString(SettingsActivity.APP_PORT_KEY, getResources().getString(R.string.target_port_default)));

        try {
            serverSocket = new DatagramSocket(port);
        } catch (Exception e) {
            Log.e(TAG, "Cannot open socket", e);
            return;
        }

        setContentView(R.layout.messages);

        // TODO open the database using the database adapter done
        // TODO query the database using the database adapter, and manage the cursor on the messages thread done

        try {
            if (messagesDbAdapter != null)
                cursor = messagesDbAdapter.fetchAllMessages();
            else {
                messagesDbAdapter = new MessagesDbAdapter(this);
                messagesDbAdapter.open();
                cursor = messagesDbAdapter.fetchAllMessages();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ListView lv = (ListView) findViewById(R.id.message_list);
        String[] from = {PeerContract.NAME, MessageContract.MESSAGE_TEXT};
        // int [] to ={R.string.target_host_default,R.id.message_list};
        int[] to = {R.id.sender, R.id.message};

        // TODO use SimpleCursorAdapter to display the messages received. done

        messagesAdapter = new SimpleCursorAdapter(this, R.layout.message, cursor, from, to);
        lv.setAdapter(messagesAdapter);


        // TODO bind the button for "next" to this activity as listener done
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        // registerForContextMenu(lv);
    }


    public void onDestroy() {
        super.onDestroy();
        messagesDbAdapter.close();
        closeSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS and SETTINGS options done
        getMenuInflater().inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            // TODO PEERS provide the UI for viewing list of peers done
            case R.id.peers:
                Intent peerIntent = new Intent(getApplicationContext(), ViewPeersActivity.class);
                startActivity(peerIntent);
                break;

            // TODO SETTINGS provide the UI for settings done
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return false;
    }


    public void onClick(View v) {

        byte[] receiveData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {

            serverSocket.receive(receivePacket);
            Log.i(TAG, "Received a packet");

            InetAddress sourceIPAddress = receivePacket.getAddress();
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
            String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");
            //String msgContents[] = new String("xd: Hello bots:"+df.format(new Date())).split(":");
            Log.i(TAG, "message split " + msgContents[0] + " " + msgContents[1] + " " + msgContents[2]);
            Message message = new Message();
            message.sender = msgContents[0];        //"xd";//
            //message.timestamp = new Date();
            message.messageText = msgContents[1];
            message.timestamp = df.parse(msgContents[2]);

            //message.messageText = msgContents[1];//     "Hello people, testing app";//
            Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);

            //Peer sender = new Peer(msgContents[0],InetAddress.getLocalHost(),6666,df.parse(msgContents[2]));
            Peer sender = new Peer();
            sender.name = message.sender;
            sender.timestamp = message.timestamp;
            sender.address = sourceIPAddress;
            sender.port = receivePacket.getPort();
            message.senderId = messagesDbAdapter.persist(sender);
            messagesDbAdapter.persist(message);
            cursor.requery();
            messagesAdapter.changeCursor(cursor);
            messagesAdapter.notifyDataSetChanged();

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