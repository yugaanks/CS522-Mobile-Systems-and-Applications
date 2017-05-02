package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.MessagesDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    /*
     * TODO See ChatServer for example of what to do, query peers database instead of messages database.
     */

    public SimpleCursorAdapter sca;
    private MessagesDbAdapter mdba;
    private Button next;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mdba!=null)
            cursor = mdba.fetchAllPeers();
        else{
            mdba = new MessagesDbAdapter(this);
            mdba.open();
            cursor = mdba.fetchAllPeers();
        }
        setContentView(R.layout.view_peers);
        ListView lv=(ListView) findViewById(R.id.peerList);
        String [] from = {"name"};
        int [] to ={R.id.sender};
        sca=new SimpleCursorAdapter(this,R.layout.message,cursor,from,to);
        lv.setAdapter(sca);
        sca.changeCursor(cursor);
        sca.notifyDataSetChanged();
        lv.setOnItemClickListener(this);
        registerForContextMenu(lv);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Log.i(ViewPeersActivity.class.getCanonicalName(), "id at view peers activity "+id);
        Intent intent = new Intent(this, ViewPeerActivity.class);
        cursor.moveToPosition(position);
        Peer peer=new Peer(cursor);
        id=peer.getId();
        Log.i(ViewPeersActivity.class.getCanonicalName(), "id at view peers activity "+id);
        intent.putExtra(ViewPeerActivity.PEER_ID_KEY, id);
        startActivity(intent);
    }
}
