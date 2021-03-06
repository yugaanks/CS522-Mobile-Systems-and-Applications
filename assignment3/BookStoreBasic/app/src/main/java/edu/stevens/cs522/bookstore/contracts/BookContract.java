package edu.stevens.cs522.bookstore.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * Created by dduggan.
 */

public class BookContract implements BaseColumns {

    public static final String TITLE = "title";

    public static final String AUTHORS = "authors";

    public static final String ISBN = "isbn";

    public static final String PRICE = "price";

    public static final String ID = "_id";

    /*
     * TITLE column
     */

    private static int titleColumn = -1;

    public static String getTitle(Cursor cursor) {
        if (titleColumn < 0) {
            titleColumn = cursor.getColumnIndexOrThrow(TITLE);
            ;
        }
        return cursor.getString(titleColumn);
    }

    public static void putTitle(ContentValues values, String title) {
        values.put(TITLE, title);
    }

    /*
     * Synthetic authors column
     */
    public static final char SEPARATOR_CHAR = '|';

    private static final Pattern SEPARATOR =
            Pattern.compile(Character.toString(SEPARATOR_CHAR), Pattern.LITERAL);

    public static String[] readStringArray(String in) {
        System.out.println(in);
        return SEPARATOR.split(in);
    }

    private static int authorColumn = -1;

    public static String[] getAuthors(Cursor cursor) {
        if (authorColumn < 0) {
            authorColumn = cursor.getColumnIndexOrThrow(AUTHORS);
        }
        Log.e(TAG, "get authors problem"+cursor.getString(authorColumn));
        System.out.println(cursor.getString(authorColumn));
        return readStringArray(cursor.getString(authorColumn));
    }

    public static void putAuthors(ContentValues values, String authors) {
        values.put(AUTHORS, authors);
    }

    public static String getId(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(_ID));
    }

    public static void putId(ContentValues contentValues, String id) {
        contentValues.put(_ID, id);
    }

    public static int isbnColumn = -1;

    public static String getIsbn(Cursor cursor) {
        if (isbnColumn < 0) {
            isbnColumn = cursor.getColumnIndexOrThrow(ISBN);
        }
        return cursor.getString(isbnColumn);
    }

    public static void putIsbn(ContentValues values, String isbn) {
        values.put(ISBN, isbn);
    }
    // TODO complete definitions of other getter and setter operations

    public static int priceColumn=-1;

    public static String getPrice(Cursor cursor) {
        if(priceColumn<0)
            priceColumn=cursor.getColumnIndexOrThrow(PRICE);
        return cursor.getString(priceColumn);
    }
    public static void putPrice(ContentValues values, String price ) {
        values.put(PRICE,price);
    }

}
