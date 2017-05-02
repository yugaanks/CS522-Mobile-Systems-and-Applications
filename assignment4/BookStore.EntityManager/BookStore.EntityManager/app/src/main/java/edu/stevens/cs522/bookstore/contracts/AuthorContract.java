package edu.stevens.cs522.bookstore.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by dduggan.
 */

public class AuthorContract implements BaseColumns {

    public static final String ID = _ID;

    public static final String NAME = "name";

    public static final String AUTHORS_TABLE = "author";

    public static final String BOOK_FK = "foreign_key";
    /*
     * NAME column
     */

    private static int nameColumn = -1;

    public static String getName(Cursor cursor) {
        if (nameColumn < 0) {
            nameColumn =  cursor.getColumnIndexOrThrow(NAME);;
        }
        return cursor.getString(nameColumn);
    }

    public static void putName(ContentValues values, String firstName) {
        values.put(NAME, firstName);
    }


    public static int getId(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(ID));
    }

    public static void putId(ContentValues values, int id) {
        values.put(ID, id);
    }
}


