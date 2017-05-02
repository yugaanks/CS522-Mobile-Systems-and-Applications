package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.Date;

/**
 * Created by dduggan.
 */

public class MessageContract implements BaseColumns {

    public static final String MESSAGE_TEXT = "message_text";

    public static final String TIMESTAMP = "timestamp";

    public static final String SENDER = "sender";

    public static final String ID = "_id";

//    private int messageTextColumn = -1;

    public static String getMessageText(Cursor cursor) {
     //   if (messageTextColumn < 0) {
            //messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
       // }
        return cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_TEXT));
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }

    public static String getId(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(ID));
    }

    public static void putId(ContentValues values, String id) {
        values.put(ID, id);
    }

    public static String getSender(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(SENDER));
    }

    public static void putSender(ContentValues values, String sender) {
        values.put(SENDER, sender);
    }

    public static Date getTimestamp(Cursor cursor) {
        return new Date(cursor.getLong(cursor.getColumnIndexOrThrow(TIMESTAMP)));
    }

    public static void putTimestamp(ContentValues values, Date timeStamp) {
        values.put(TIMESTAMP, timeStamp.toString());
    }
}
