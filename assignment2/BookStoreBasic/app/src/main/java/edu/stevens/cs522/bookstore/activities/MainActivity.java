package edu.stevens.cs522.bookstore.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;

public class MainActivity extends ListActivity {

    // Use this when logging errors and warnings.
    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getCanonicalName();

    // These are request codes for sub activity request calls
    static final private int ADD_REQUEST = 1;

    @SuppressWarnings("unused")
    static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    // There is a reason this must be an ArrayList instead of a List.
    @SuppressWarnings("unused")
    private ArrayList<Book> shoppingCart;
    BooksAdapter booksAdapter;
    private ListView bstoreListV;
    static final private String cart = "list";
    private static int selectedItem=-1;
    private String itemSelected="";
    private static final String BOOK_DETAILS = "Book_Details";
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO check if there is saved UI state, and if so, restore it (i.e. the cart contents) done
        if (savedInstanceState != null)
            shoppingCart = savedInstanceState.getParcelableArrayList("list");

        // TODO Set the layout (use cart.xml layout) done
        setContentView(R.layout.cart);

        shoppingCart = new ArrayList<Book>();
        // TODO use an array adapter to display the cart contents. may be done
        booksAdapter = new BooksAdapter(this, shoppingCart);
        bstoreListV = (ListView) findViewById(android.R.id.list);
        bstoreListV.setAdapter(booksAdapter);
        bstoreListV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        bstoreListV.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(bstoreListV.getCheckedItemCount() + " Selected Items");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId()==R.id.menu_delete) {
                    delete_book();
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) { }
        });

        bstoreListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent=new Intent(getApplicationContext(),BookDetails.class);
                Book bookSelected=shoppingCart.get(position);
                intent.putExtra(BOOK_DETAILS,bookSelected);
                startActivity(intent);
            }
        });

    }

    private void delete_book(){

        SparseBooleanArray checked = bstoreListV.getCheckedItemPositions();
        Book [] books = new Book[checked.size()];
        for(int i =0; i < checked.size(); i++){
            if(checked.valueAt(i)){
                Book theSelectedBook = (Book) bstoreListV.getItemAtPosition(checked.keyAt(i));
                books[i] = theSelectedBook;
                booksAdapter.remove(theSelectedBook);
                booksAdapter.notifyDataSetChanged();
            }
        }
        for(Book b : books){
            shoppingCart.remove(b);
        }
        booksAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO provide ADD, DELETE and CHECKOUT options done
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookstore_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // TODO almost done
        // ADD provide the UI for adding a book
        Intent addIntent;
        if (item.getItemId() == R.id.add) {
            addIntent = new Intent(this, AddBookActivity.class);
            startActivityForResult(addIntent, ADD_REQUEST);
            return true;
        }
        // DELETE delete the currently selected book

        // CHECKOUT provide the UI for checking out
        if (item.getItemId() == R.id.checkout) {
            if (shoppingCart.size() < 0)
                return true;
            else {
                addIntent = new Intent(this, CheckoutActivity.class);
                startActivityForResult(addIntent, CHECKOUT_REQUEST);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // TODO Handle results from the Search and Checkout activities. done
        // SEARCH: add the book that is returned to the shopping cart.
        if (requestCode == ADD_REQUEST) {
            if (resultCode == RESULT_OK) {
                Book b = intent.getExtras().getParcelable(AddBookActivity.BOOK_RESULT_KEY);
                Toast.makeText(this, "book store function", Toast.LENGTH_LONG).show();
                shoppingCart.add(b);
                booksAdapter.notifyDataSetChanged();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Request Cancelled", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CHECKOUT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "checking out", Toast.LENGTH_LONG).show();
                shoppingCart.clear();
                booksAdapter.notifyDataSetChanged();


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Checkout Cancelled", Toast.LENGTH_LONG).show();
            }
        }
        // Use SEARCH_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.

        // SEARCH: add the book that is returned to the shopping cart.

        // CHECKOUT: empty the shopping cart.

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // TODO save the shopping cart contents (which should be a list of parcelables). may be done
        savedInstanceState.putParcelableArrayList(cart, shoppingCart);
    }

}

class BooksAdapter extends ArrayAdapter<Book> {
    public BooksAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //	Get	the	data	item	for	this	position
        Book book = getItem(position);
        //	Check	if	an	existing	view	is	being	reused,	otherwise	inflate	the	view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.cart_row, parent, false);
        }
        //	Lookup	view	for	data	population
        TextView title = (TextView) convertView.findViewById(R.id.cart_row_title);
        TextView author = (TextView) convertView.findViewById(R.id.cart_row_author);
        //	Populate	the	data	into	the	template	view	using	the	data	object
        title.setText(book.title);
        Author[] authors = book.authors;
        String authorsName = "";
        int count = 0;
        for (Author a : authors) {
            authorsName += a.firstName + " ";
            if (a.middleInitial != null) {
                authorsName += a.middleInitial + " ";
            }
            authorsName += a.lastName;

            count++;
            if (count < authors.length) {
                authorsName += ", ";
            }
        }
        author.setText(authorsName);
        //	Return	the	completed	view	to	render	on	screen
        return convertView;
    }
}

