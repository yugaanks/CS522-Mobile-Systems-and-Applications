package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.MessagesDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_ID_KEY = "peer_id";
    private MessagesDbAdapter mdba;
    SimpleCursorAdapter sca;
    Cursor cursor;
    //public static ListView messages;
    public static TextView peerName,peerAddress,peerPort, peerLastTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);
        peerName=(TextView) findViewById(R.id.view_user_name);
        peerAddress=(TextView) findViewById(R.id.view_address);
        peerPort=(TextView) findViewById(R.id.view_port);
        peerLastTimestamp=(TextView) findViewById(R.id.view_timestamp);
        long peerId=getIntent().getExtras().getLong(PEER_ID_KEY);
        Log.i(ViewPeerActivity.class.getCanonicalName(), "id at view peer activity "+peerId);
        mdba = new MessagesDbAdapter(this);
        mdba.open();
        Log.i(ViewPeerActivity.class.getCanonicalName(), "db open");
        Peer peer = mdba.fetchPeer(peerId);
        Log.i(ViewPeerActivity.class.getCanonicalName(), "peer fetched");
        String name = peer.name;
        String address = peer.address.toString().substring(1);
        String port = ""+peer.port;
        String timestamp=""+peer.timestamp;
        peerName.setText(name);
        peerAddress.setText(address);
        peerLastTimestamp.setText(timestamp);
        peerPort.setText(port);
        mdba.close();
        //cursor=mdba.fetchMessagesFromPeer(peer);
        //messages=(ListView) findViewById(R.id.)
    }

}
