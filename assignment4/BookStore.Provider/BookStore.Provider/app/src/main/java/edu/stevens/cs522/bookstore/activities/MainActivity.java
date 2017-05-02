package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements OnItemClickListener, AbsListView.MultiChoiceModeListener, LoaderManager.LoaderCallbacks<Cursor> {

    // Use this when logging errors and warnings.
    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getCanonicalName();

    // These are request codes for subactivity request calls
    static final private int ADD_REQUEST = 1;

    @SuppressWarnings("unused")
    static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    static final private int LOADER_ID = 1;

    public static final String BOOK_DETAILS_KEY = "book_details";

    public static String list = "list";

    private ArrayList<Book> shoppingCart = new ArrayList<Book>();

    BookAdapter bookAdapter;

    ContentResolver resolver;

    //TextView emptyList;
    TextView emptyText;

    ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(savedInstanceState!=null)
//            savedInstanceState.putParcelableArrayList(list, shoppingCart);
        resolver = getContentResolver();

        setContentView(R.layout.cart);
        // Use a custom cursor adapter to display an empty (null) cursor.

        lv = (ListView) findViewById(R.id.list);
        getData();
        registerForContextMenu(lv);
//        emptyList = (TextView) findViewById(R.id.emptyList);
//        bookAdapter = new BookAdapter(this, null);
//        lv.setAdapter(bookAdapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ViewBookActivity.class);
                Book book = new Book((Cursor) lv.getAdapter().getItem(position));
                intent.putExtra(BOOK_DETAILS_KEY, book);
                startActivity(intent);
            }
        });

        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(lv.getCheckedItemCount() + " selected Items");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.books_cab, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        try {
                            delete_book();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.i(TAG, "destroying action mode");
            }
        });
        registerForContextMenu(lv);
        //getLoaderManager().initLoader(LOADER_ID, null, this);
    }
    public void getData(){

        getLoaderManager().initLoader(LOADER_ID, null, this);
        bookAdapter = new BookAdapter(this,null);
        lv.setAdapter(bookAdapter);
        emptyText = (TextView)findViewById(android.R.id.empty);
        lv.setEmptyView(emptyText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.bookstore_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.add:
                // Intent addIntent = new Intent(this, AddBookActivity.class);
                // startActivityForResult(addIntent, ADD_REQUEST);
                Intent addIntent = new Intent(getApplicationContext(), AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                return true;

            case R.id.checkout:
                Intent checkOutIntent = new Intent(getApplicationContext(), CheckoutActivity.class);
                startActivityForResult(checkOutIntent, CHECKOUT_REQUEST);
                return true;

            default:
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Book book;
        StringBuffer sb=new StringBuffer();
        // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        switch (requestCode) {
            case ADD_REQUEST:
                // ADD: add the book that is returned to the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                if (resultCode == RESULT_OK) {
                    book = intent.getExtras().getParcelable(BOOK_DETAILS_KEY);
                    //String authors_names=book.authorObjectToString(book.getAuthors());

                    ContentValues values = new ContentValues();
                     book.writeToProvider(values);
//                    values.put(BookContract.TITLE, book.title);
//                    values.put(BookContract.AUTHORS, book.authors[0].toString());
//                    values.put(BookContract.ISBN, book.isbn);
//                    values.put(BookContract.PRICE, book.price);
                     this.getContentResolver().insert(BookContract.CONTENT_URI, values);
//                    for(int i=0;i<book.authors.length;i++)
//                    {
//                        ContentValues authorValues = new ContentValues();
//                        authorValues.put(AuthorContract.FIRST_NAME,book.authors[i].firstName );
//                        authorValues.put(AuthorContract.MIDDLE_INITIAL,book.authors[i].middleInitial );
//                        authorValues.put(AuthorContract.LAST_NAME,book.authors[i].lastName );
//                        authorValues.put(AuthorContract.BOOK_FK,book.getId());
//                        this.getContentResolver().insert(AuthorContract.CONTENT_URI, authorValues);
//                    }
                } else if(resultCode == RESULT_CANCELED) {
                    Toast.makeText(this,"cancelled",Toast.LENGTH_SHORT).show();
                }
                break;
            case CHECKOUT_REQUEST:
                // CHECKOUT: empty the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                if (resultCode == RESULT_OK) {
                    getContentResolver().delete(BookContract.CONTENT_URI, null, null);
                    break;
                } else if(resultCode == RESULT_CANCELED){
                    Toast.makeText(this,"Checkout Cancelled",Toast.LENGTH_SHORT).show();
                }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(list, shoppingCart);
        super.onSaveInstanceState(savedInstanceState);
    }

    /*
     * Loader callbacks
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;
        switch (id){
            case LOADER_ID:
                String[] projection = {BookContract.BOOKS_TABLE+"."+BookContract.ID, BookContract.TITLE, BookContract.AUTHORS, BookContract.ISBN, BookContract.PRICE};
                cursorLoader = new CursorLoader(this, BookContract.CONTENT_URI, projection, null, null, null);
                break;
            default:
                cursorLoader = null;
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case LOADER_ID:
                this.bookAdapter.changeCursor(data);
                break;
            default:
                throw new IllegalArgumentException("Unexpected loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case LOADER_ID:
                this.bookAdapter.changeCursor(null);
                break;
            default:
                throw new IllegalArgumentException("Unexpected loader id: " + loader.getId());
        }
    }


    /*
     * Selection of a book from the list view
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // ok to do on main thread for BookStoreWithContentProvider
        Intent intent = new Intent(getApplicationContext(), ViewBookActivity.class);
        Book book = new Book((Cursor) lv.getAdapter().getItem(position));
        intent.putExtra(BOOK_DETAILS_KEY, book);
        startActivity(intent);
    }


    /*
     * Handle multi-choice action mode for deletion of several books at once
     */
    private void delete_book() throws SQLException {
        Cursor cursor = null;
        SparseBooleanArray itemsBooleanValues = lv.getCheckedItemPositions();
        for(int i = 0; i < itemsBooleanValues.size(); i++){
            if(itemsBooleanValues.valueAt(i)){
                if(bookAdapter.getCursor().moveToPosition(i)) {
                    cursor = bookAdapter.getCursor();
                    String _id = bookAdapter.getCursor().getString(cursor.getColumnIndex(BookContract.ID));
                    Uri uri = Uri.parse(BookContract.CONTENT_URI + "/" + _id);
                    getContentResolver().delete(uri, null, null);
                    bookAdapter.changeCursor(cursor);
                }
            }
        }
    }

    Set<Long> selected;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.books_cab, menu);
        selected = new HashSet<Long>();
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            selected.add(id);
        } else {
            selected.remove(id);
        }
        mode.setTitle(lv.getCheckedItemCount() + " selected Items");
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                try {
                    delete_book();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

}