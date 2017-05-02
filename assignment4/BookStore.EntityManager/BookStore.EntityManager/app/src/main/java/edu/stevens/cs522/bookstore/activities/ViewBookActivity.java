package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;


public class ViewBookActivity extends Activity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_KEY = "book";

	private ArrayAdapter<String> authorsAdapter;
	private TextView titleValue,ISBNValue;
	private ListView authorValue;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_book);
        authorsAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        titleValue=(TextView)findViewById(R.id.view_title);
        authorValue=(ListView) findViewById(R.id.view_authors);
        ISBNValue=(TextView)findViewById(R.id.view_isbn);
		Book book = getIntent().getParcelableExtra(BOOK_KEY);
        titleValue.setText(book.getTitle());
        Author authors[]=book.getAuthors();
        String temp[]=authors[0].getName().split(",");
        int index=0;
        Author[] authorsNew=new Author[temp.length];
        for(String s:temp){
            authorsNew[index]=new Author(s);
            index++;
        }
        Log.i(ViewBookActivity.class.getCanonicalName(),"authors length: "+authors.length);
        for (Author author : authorsNew) {
            authorsAdapter.add(author.name);
        }
        authorValue.setAdapter(authorsAdapter);
    ISBNValue.setText(book.getIsbn());

	}

}