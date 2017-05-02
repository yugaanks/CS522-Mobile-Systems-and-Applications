/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import java.util.Date;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.managers.MessageManager;
import edu.stevens.cs522.chatserver.managers.PeerManager;
import edu.stevens.cs522.chatserver.managers.TypedCursor;

public class ChatServer extends Activity implements OnClickListener, QueryBuilder.IQueryListener<Message> {

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

    private MessageManager messageManager;

    private PeerManager peerManager;

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

        int port = Integer.valueOf(settings.getString(SettingsActivity.APP_PORT_KEY, getResources().getString(R.string.default_app_port)));

		try {
			serverSocket = new DatagramSocket(port);
		} catch (Exception e) {
			Log.e(TAG, "Cannot open socket", e);
			return;
		}

        setContentView(R.layout.messages);
        messageList = (ListView) findViewById(R.id.message_list);
        String[] from = {MessageContract.SENDER, MessageContract.MESSAGE_TEXT};
         int [] to ={R.id.sender,R.id.message};
        Log.i(TAG,"line 117");
        messagesAdapter=new SimpleCursorAdapter(this, R.layout.message, null, from, to,0);
        next = (Button) findViewById(R.id.next);
        messageList.setAdapter(messagesAdapter);
        messageManager=new MessageManager(this);
        peerManager=new PeerManager(this);
        Log.e(TAG,"line 123");
        messageManager.getAllMessagesAsync(this);
        Log.e(TAG,"line 125");
        next.setOnClickListener(this);
        Log.e(TAG,"line 127");
	}

    public void onDestroy() {
        super.onDestroy();
        closeSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case R.id.peers:
                Intent peerIntent = new Intent(getApplicationContext(), ViewPeersActivity.class);
                startActivity(peerIntent);
                break;

            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return false;
    }



    public void onClick(View v) {
        Log.i(TAG, "Error at line 161");
		byte[] receiveData = new byte[1024];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        Log.i(TAG, "Error at line 165");
		try {
			

			serverSocket.receive(receivePacket);
			Log.i(TAG, "Received a packet");

			InetAddress sourceIPAddress = receivePacket.getAddress();
			Log.i(TAG, "Source IP Address: " + sourceIPAddress);
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
			String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");
            //Log.e(TAG, df.format(new Date()));
        //    String msgContents[]=new String("client:"+"Hello:"+new Date(System.currentTimeMillis())).split(":");
            final Message message = new Message();
            message.sender = msgContents[0];
            //Log.e(TAG, ""+new Date());
            message.messageText = msgContents[1];
            message.timestamp = new Date(msgContents[2]+":"+msgContents[3]+":"+msgContents[4]);//df.parse(msgContents[2]);

			Log.e(TAG, "Received from " + message.sender + ": " + message.messageText + " : " + message.timestamp.toString());
            Log.e(TAG, ""+message.timestamp);
            Peer sender = new Peer();
            sender.name = message.sender;
            sender.setTimestamp(message.timestamp);
            Log.e(TAG, "Hello SDASIJDIASDIJIAJSDIJISAMD "+msgContents[2]);
            sender.address = receivePacket.getAddress();
            //sender.address = InetAddress.getLocalHost();
            sender.port = receivePacket.getPort();
            //sender.port = 6666;
            Log.e(TAG, "Error at line 190");
            peerManager.persistAsync(sender, new IContinue<Long>() {
                @Override
                public void kontinue(Long value) {
                    message.setSenderId(value);
                    messageManager.persistAsync(message);
                }
            });
            Log.e(TAG, "Error at line 198");
           // messageManager.persistAsync(message);
            Log.i(TAG, "Error at line 200");
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

    @Override
    public void handleResults(TypedCursor<Message> results) {

        messagesAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        messagesAdapter.swapCursor(null);
    }
}