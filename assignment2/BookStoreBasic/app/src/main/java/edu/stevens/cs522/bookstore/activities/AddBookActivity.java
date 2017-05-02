package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;

public class AddBookActivity extends Activity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_RESULT_KEY = "book_result";
    private EditText title, author, isbn;
    static int bookId = 1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_book);
        title = (EditText)findViewById(R.id.search_title);
        author = (EditText)findViewById(R.id.search_author);
        isbn = (EditText)findViewById(R.id.search_isbn);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO provide SEARCH and CANCEL options   done;
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.addbookmenu,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // TODO done
        switch (item.getItemId()) {
            // SEARCH: return the book details to the BookStore activity done
            case R.id.search:
                Intent intent = new Intent();
                Book book = searchBook();
                intent.putExtra(BOOK_RESULT_KEY, book);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            // CANCEL: cancel the search request done
            case R.id.cancel:
                finish();
                return true;
        }

            return false;
    }

	public Book searchBook(){
		/*
		 * Search for the specified book.
		 */
		// TODO Just build a Book object with the search criteria and return that. done
        String [] str = author.getText().toString().split(",");
        Author[] authors = new Author[str.length];
        int index=0;
        for(String s : str){
            Author a = null;
            String[] full_name = s.split("\\s+");
            if(full_name.length == 1){
                a = new Author(full_name[0]);
            }else if(full_name.length == 2){
                a = new Author(full_name[0], full_name[1]);
            }else if(full_name.length == 3){
                a = new Author(full_name[0], full_name[1], full_name[2]);
            }
            authors[index] = a;
            index++;
        }
        Book book=new Book(bookId++,
                           title.getText().toString(),
                           authors,
                           isbn.getText().toString(),
                           "$55");

		return book;
	}

}