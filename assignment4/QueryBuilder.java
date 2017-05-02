package edu.stevens.cs522.chatserver.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.managers.MessageManager;
import edu.stevens.cs522.chatserver.managers.PeerManager;
import edu.stevens.cs522.chatserver.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks {

    public static interface IQueryListener<T> {

        public void handleResults(TypedCursor<T> results);

        public void closeResults();

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        if (id == loaderId) {
            String[] projection = null;
            switch (id) {
                case PeerManager.LOADER_ID:
                    projection = new String[]{
                            PeerContract.ID,
                            PeerContract.NAME,
                            PeerContract.ADDRESS,
                            PeerContract.PORT,
                            PeerContract.TIMESTAMP
                    };
                    break;
                case MessageManager.LOADER_ID:
                    
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected loader id: " + id);
            }
            
        }
        throw new IllegalArgumentException("Unexpected loader id: " + id);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (loader.getId() == loaderId) {
            String s[]=data.getColumnNames();
            for(String d:s)
                Log.e(QueryBuilder.class.getCanonicalName(),d);
            listener.handleResults(new TypedCursor<T>(data, null));
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == loaderId) {
            listener.closeResults();
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }
}
