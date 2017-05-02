package edu.stevens.cs522.bookstore.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;

public class BookProvider extends ContentProvider {
    public BookProvider() {
    }

    private static final String AUTHORITY = BookContract.AUTHORITY;

    private static final String CONTENT_PATH = BookContract.CONTENT_PATH;

    private static final String CONTENT_PATH_ITEM = BookContract.CONTENT_PATH_ITEM;

    private static final String DATABASE_NAME = "books.db";

    private static final int DATABASE_VERSION = 2;

    private static final String BOOKS_TABLE = "books";

    private static final String AUTHORS_TABLE = "authors";

    private static final String DATABASE_CREATE1 ="create table "+BookContract.BOOKS_TABLE+" ("+BookContract.ID +" integer primary key autoincrement,"+
            BookContract.TITLE+" text,"+BookContract.AUTHORS +" text,"+BookContract.ISBN+" text,"+BookContract.PRICE+" text);";

    private static final String DATABASE_CREATE2 ="create table "+ AuthorContract.AUTHORS_TABLE+" ("+AuthorContract.ID +" integer primary key autoincrement,"+
            AuthorContract.FIRST_NAME+" text,"+AuthorContract.MIDDLE_INITIAL +" text,"+AuthorContract.LAST_NAME+" text,"+AuthorContract.BOOK_FK+
            " integer not null,foreign key ("+AuthorContract.BOOK_FK+") references "+BookContract.BOOKS_TABLE+"("+ BookContract.ID +")on delete cascade );";


    public static String CREATE_INDEX="Create index AuthorsBookIndex on "+AuthorContract.AUTHORS_TABLE+"("+ AuthorContract.BOOK_FK+");";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int ALL_ROWS = 1;
    private static final int SINGLE_ROW = 2;
    private static HashMap<String, String> booksHashMap=new HashMap<>();
    public static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE1);
            db.execSQL(DATABASE_CREATE2);
            db.execSQL(CREATE_INDEX);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+BookContract.BOOKS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+AuthorContract.AUTHORS_TABLE);
            onCreate(db);
        }
    }

    private DbHelper dbHelper;
    private SQLiteDatabase sqldb;


    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        sqldb = dbHelper.getWritableDatabase();
        if(sqldb!=null)
            return true;
        else
            return false;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH_ITEM, SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)){
            case ALL_ROWS:
                return BookContract.CONTENT_TYPE;
            case SINGLE_ROW:
                return BookContract.CONTENT_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri _temp = null;
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                long rowId= sqldb.insert(BookContract.BOOKS_TABLE, null, values);
                if (rowId > 0){
                    _temp = ContentUris.withAppendedId(BookContract.CONTENT_URI,rowId);
                    getContext().getContentResolver().notifyChange(_temp , null);
                    return _temp;
                }
            case SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

//    @Override
//    public Cursor query(Uri uri, String[] projection, String selection,
//                        String[] selectionArgs, String sortOrder) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
//        queryBuilder.setTables(BookContract.BOOKS_TABLE + "," + AuthorContract.AUTHORS_TABLE);
//        booksHashMap.put(BookContract.AUTHORS, "GROUP_CONCAT((first || initial || last),'|') as " + BookContract.AUTHORS);
//        HashMap<String,String> map=booksHashMap;
//        String xd = "Books._id, title, price, isbn";
//        for (String field :
//                projection) {
//            if (!map.containsKey(field)) map.put(field, field);
//        }
//        queryBuilder.setProjectionMap(map);
//        switch (uriMatcher.match(uri)) {
//            case ALL_ROWS:
////                queryBuilder.appendWhere(BookContract.BOOKS_TABLE + "." + BookContract.ID + "="+AuthorContract.AUTHORS_TABLE + "." + AuthorContract.BOOK_FK);
////                queryBuilder.setProjectionMap(BOOKSMAP);
////                return db.query(BOOKS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
//                break;
//            case SINGLE_ROW:
//                queryBuilder.appendWhere(BookContract.ID + "=" + BookContract.getId(uri)); //uri.getPathSegments().get(1));
//                break;
//            // throw new UnsupportedOperationException("Not yet implemented");
//            default:
//                throw new IllegalStateException("insert: bad case");
//        }
//        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, xd, null, null);
//        cursor.moveToFirst();
//        cursor.setNotificationUri(getContext().getContentResolver(), uri);
//        return cursor;
//    }
@Override
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

    SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
    String joinStat = BookContract.BOOKS_TABLE
            + " LEFT OUTER JOIN " + AuthorContract.AUTHORS_TABLE
            + " ON (" + BookContract.BOOKS_TABLE + "." + BookContract.ID
            + " = " + AuthorContract.AUTHORS_TABLE + "." + AuthorContract.BOOK_FK + ")";
    String groupby = "Books._id, title, price, isbn";
    builder.setTables(joinStat);
    HashMap<String, String> map = booksHashMap;
    for (String field :
            projection) {
        if (!map.containsKey(field)) map.put(field, field);
    }
    builder.setProjectionMap(map);

    switch (uriMatcher.match(uri)) {
        case ALL_ROWS:
//                builder.setProjectionMap(new HashMap<String, String>());
            break;
        case SINGLE_ROW:
            builder.appendWhere(BookContract.ID + " = " + BookContract.getId(uri));
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
    }
    Log.d("query", builder.buildQuery(projection, selection, groupby, null, null, null));
    Cursor cursor = builder.query(sqldb, projection, selection, selectionArgs, groupby, null, null);
    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return cursor;
}

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new IllegalStateException("Update of books not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowid=0;
        switch (uriMatcher.match(uri)){
            case ALL_ROWS:
                rowid=sqldb.delete(BookContract.BOOKS_TABLE, selection, selectionArgs);
                sqldb.delete(AuthorContract.AUTHORS_TABLE, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                break;
            case SINGLE_ROW:
                String id = uri.getPathSegments().get(1);
                rowid=sqldb.delete(BookContract.BOOKS_TABLE, BookContract.BOOKS_TABLE+"."+BookContract.ID +" = "+Integer.parseInt(id)+(!TextUtils.isEmpty(selection) ? " AND (" + selection+ ')':""),selectionArgs);
                sqldb.delete(AuthorContract.AUTHORS_TABLE, AuthorContract.AUTHORS_TABLE + "." + AuthorContract.BOOK_FK + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return rowid;
    }

}
