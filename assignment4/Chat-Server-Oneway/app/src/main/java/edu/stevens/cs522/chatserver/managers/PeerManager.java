package edu.stevens.cs522.chatserver.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

import edu.stevens.cs522.chatserver.async.AsyncContentResolver;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.IEntityCreator;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chatserver.async.SimpleQueryBuilder;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;


/**
 * Created by dduggan.
 */

public class PeerManager extends Manager<Peer> {

    public static final int LOADER_ID = 2;

    private static final IEntityCreator<Peer> creator = new IEntityCreator<Peer>() {
        @Override
        public Peer create(Cursor cursor) {
            return new Peer(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public PeerManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public void getAllPeersAsync(IQueryListener<Peer> listener) {
        QueryBuilder.executeQuery(tag, (Activity) context, PeerContract.CONTENT_URI, loaderID,creator,listener);
    }

    public void getPeerAsync(long id, final IContinue<Peer> callback) {
        SimpleQueryBuilder.executeQuery((Activity) context,PeerContract.CONTENT_URI(id),creator,new SimpleQueryBuilder.ISimpleQueryListener<Peer>(){
            public void handleResults(List<Peer> peers) {
                if(callback!=null || peers.size()>0)
                    callback.kontinue(peers.get(0));
                else
                    throw new IllegalStateException("no peer found");
            }
        });

    }
    public void persistAsync(final Peer peer, final IContinue<Long> callback) {
        ContentValues values = new ContentValues();
        peer.writeToProvider(values);
        contentResolver.insertAsync(PeerContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri value) {
                //peer.setId(PeerContract.getId(value));
                getSyncResolver().notifyChange(value, null);
                if (callback != null)
                    callback.kontinue(PeerContract.getId(value));
            }
        });
    }
}
