package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.async.IContinue;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.managers.BookManager;
import edu.stevens.cs522.bookstore.managers.TypedCursor;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements OnItemClickListener, AbsListView.MultiChoiceModeListener, IQueryListener {

    // Use this when logging errors and warnings.
    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getCanonicalName();

    // These are request codes for subactivity request calls
    static final private int ADD_REQUEST = 1;

    static final private int LOADER_ID = 1;

    static final private String cart = "list";

    ArrayList<Book> shoppingCart;

    @SuppressWarnings("unused")
    static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    ListView lv;
    private BookManager bookManager;

    private BookAdapter bookAdapter;

    final Activity mainA=this;
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingCart = new ArrayList<>();
        getContentResolver();
        setContentView(R.layout.cart);
        // Use a custom cursor adapter to display an empty (null) cursor.
        bookAdapter = new BookAdapter(this, null);
        lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(bookAdapter);

        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.books_cab, menu);
                selected = new HashSet<Long>();
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
                        delete_book();
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                //destroying action jackson mode
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    selected.add(id);
                } else {
                    selected.remove(id);
                }
                mode.setTitle("Book(s) selected");
            }
        });
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ViewBookActivity.class);
                Book book = new Book((Cursor) lv.getAdapter().getItem(position));
                intent.putExtra(ViewBookActivity.BOOK_KEY, book);
                startActivity(intent);
                /*Cursor cursor = bookAdapter.getCursor();
                cursor.moveToPosition(position);
                Book book = new Book(cursor);

                bookManager.getBookAsync(book.id, new IContinue<Book>() {
                    @Override
                    public void kontinue(Book value) {
                        Intent intent = new Intent(mainA, ViewBookActivity.class);
                        intent.putExtra(ViewBookActivity.BOOK_KEY,value);
                        startActivityForResult(intent, 3);//request code- checkout+1
                    }
                });*/


            }
        });
        registerForContextMenu(lv);
        // Initialize the book manager and query for all books
        bookManager = new BookManager(this);
        bookManager.getAllBooksAsync(this);
    }

    public void delete_book() {
        Cursor c = null;
        BookManager manager = new BookManager(this);
        manager.deleteBooksAsync(selected);
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
                Intent addIntent = new Intent(this, AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                return true;

            case R.id.checkout:
                Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
                startActivityForResult(checkoutIntent, CHECKOUT_REQUEST);
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
//        StringBuffer sb=new StringBuffer();
        // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        switch (requestCode) {
            case ADD_REQUEST:
                // ADD: add the book that is returned to the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                if (resultCode == RESULT_OK) {
                    book = intent.getExtras().getParcelable(ViewBookActivity.BOOK_KEY);
                    bookManager.persistAsync(book);
                    bookManager.getAllBooksAsync(this);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
                }
                break;
            case CHECKOUT_REQUEST:
                // CHECKOUT: empty the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                if (resultCode == RESULT_OK) {
                    bookManager.deleteBooksAsync(new IContinue<Integer>() {
                        @Override
                        public void kontinue(Integer value) {
                            Toast.makeText(getApplicationContext(), "checking out",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    //getContentResolver().delete(BookContract.CONTENT_URI, null, null);
                    break;
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Checkout Cancelled", Toast.LENGTH_SHORT).show();
                }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(cart, shoppingCart);
        super.onSaveInstanceState(savedInstanceState);
    }



    @Override
    public void handleResults(TypedCursor results) {
        bookAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        bookAdapter.swapCursor(null);
    }


    /*
     * Selection of a book from the list view
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // ok to do on main thread for BookStoreWithContentProvider
        Intent intent = new Intent(getApplicationContext(), ViewBookActivity.class);
        Book book = new Book((Cursor) lv.getAdapter().getItem(position));
        intent.putExtra(ViewBookActivity.BOOK_KEY, book);
        startActivity(intent);
        /*Cursor cursor = bookAdapter.getCursor();
        cursor.moveToPosition(position);
        Book book = new Book(cursor);

        bookManager.getBookAsync(book.id, new IContinue<Book>() {
            @Override
            public void kontinue(Book value) {
                Intent intent = new Intent(mainA, ViewBookActivity.class);
                intent.putExtra(ViewBookActivity.BOOK_KEY,value);
                startActivityForResult(intent, 3);//request code- checkout+1
            }
        });*/
    }


    /*
     * Handle multi-choice action mode for deletion of several books at once
     */

    Set<Long> selected;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.books_cab, menu);
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
        mode.setTitle("Book(s) selected");
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                delete_book();
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
        selected.clear();
    }

}