package edu.stevens.cs522.chatserver.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class MessagesDbAdapter {

    private static final String DATABASE_NAME = "messages.db";

    private static final String MESSAGE_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";

    private static final int DATABASE_VERSION = 1;

    private static final String PEER_ID = PeerContract.ID;

    private static final String NAME = PeerContract.NAME;

    private static final String ADDRESS = PeerContract.ADDRESS;

    private static final String PORT = PeerContract.PORT;

    private static final String TIMESTAMP = MessageContract.TIMESTAMP;

    private static final String MESSAGE_ID = MessageContract.ID;

    private static final String MESSAGE_TEXT = MessageContract.MESSAGE_TEXT;

    private static final String SENDER = MessageContract.SENDER;

    private static final String PEER_FK = "peer_fk";


    public static String INDEX1 = "CREATE INDEX MessagesPeerIndex on " + MESSAGE_TABLE + "(" + PEER_FK + ");";
    public static String INDEX2 = "CREATE INDEX PeerNameIndex on " + PEERS_TABLE + "(" + NAME + ");";

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        // TODO

        private static final String DATABASE_CREATE1 = "CREATE TABLE " + PEERS_TABLE + " (" + PEER_ID + " integer primary key autoincrement," +
                NAME + " text not null," + ADDRESS + " text not null," + PORT + " integer," + TIMESTAMP + " text not null );";
        private static final String DATABASE_CREATE2 = "CREATE TABLE " + MESSAGE_TABLE + " (" + MESSAGE_ID + " integer primary key autoincrement," +
                MESSAGE_TEXT + " text," + TIMESTAMP + " text," + SENDER + " text," + PEER_FK + " integer, foreign key (" + PEER_FK + ") references " + PEERS_TABLE +
                "(" + PEER_ID + ") on delete cascade);";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO
            db.execSQL(DATABASE_CREATE1);
            db.execSQL(DATABASE_CREATE2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO
            db.execSQL("DROP TABLE IF EXISTS " + PEERS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
            onCreate(db);
        }
    }


    public MessagesDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        // TODO
        db = dbHelper.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public Cursor fetchAllMessages() {
        // TODO
        String view = "(select " + PEERS_TABLE + "." + NAME + "," + MESSAGE_TEXT + "," + PEERS_TABLE + "." + PEER_ID + "," + MESSAGE_TABLE + "." + TIMESTAMP + " from " + PEERS_TABLE +
                " join " + MESSAGE_TABLE + " on " + PEERS_TABLE + "." + PEER_ID + "=" + MESSAGE_TABLE + "." + PEER_FK + ") as foo;";
        return db.query(view, new String[]{NAME, MESSAGE_TEXT, PEER_ID, TIMESTAMP}, null, null, null, null, null);
    }

    public Cursor fetchAllPeers() {
        // TODO
        return db.query(PEERS_TABLE, new String[]{PEER_ID, NAME, ADDRESS, PORT, TIMESTAMP}, null, null, null, null, null);
    }

    public Peer fetchPeer(long peerId) {
        // TODO
        Cursor cursor = db.rawQuery("select * from " + PEERS_TABLE + " where " + PEER_ID + " =" + peerId, null);
        cursor.moveToFirst();
        for (String i : cursor.getColumnNames())
            Log.i(MessagesDbAdapter.class.getCanonicalName(), i);
        return new Peer(cursor);
    }

    public Cursor fetchMessagesFromPeer(Peer peer) {
        // TODO
        Cursor cursor = db.rawQuery("SELECT " + MESSAGE_TABLE + "." + MESSAGE_ID + " ," + MESSAGE_TEXT + " ," + TIMESTAMP + " ," + SENDER + " ," + PEER_FK + " ," + PEER_FK + " as _id FROM " + MESSAGE_TABLE + " WHERE " + PEER_FK + " = " + peer.id + " AND " + SENDER + " = '" + peer.name + "' ", null);
        return cursor;
    }

    public void persist(Message message) throws SQLException {
        // TODO
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        ContentValues values = new ContentValues();
        values.put(MESSAGE_TEXT, message.getMessageText());
        values.put(TIMESTAMP, df.format(message.getTimestamp()));
        values.put(SENDER, message.getSender());
        values.put(PEER_FK, message.getSenderId());
        db.insert(MESSAGE_TABLE, null, values);
    }

    /**
     * Add a peer record if it does not already exist; update information if it is already defined.
     *
     * @param peer
     * @return The database key of the (inserted or updated) peer record
     * @throws SQLException
     */
    public long persist(Peer peer) throws SQLException {
        // TODO
        ContentValues values = new ContentValues();
        long id = peer.getId();
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        if (id <= 0) {
            values.put(NAME, peer.getName());
            values.put(ADDRESS, peer.getAddress().toString());
            values.put(PORT, peer.getPort());
            values.put(TIMESTAMP, df.format(peer.getTimestamp()));
            db.insert(PEERS_TABLE, null, values);
            Cursor c = db.rawQuery("select last_insert_rowid() from " + PEERS_TABLE, null);
            c.moveToFirst();
            id = c.getInt(0);
            return id;
        }
        throw new SQLException("Failed to add peer " + peer.name);
    }

    public void close() {
        // TODO
        db.close();
    }
}