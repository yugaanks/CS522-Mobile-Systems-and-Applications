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
	//private ListView authorValue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_book);
		titleValue=(TextView)findViewById(R.id.view_title);
		authorValue=(ListView) findViewById(R.id.view_authors);
		//authorValue=(ListView) findViewById(R.id.view_authors);
		ISBNValue=(TextView)findViewById(R.id.view_isbn);
		Book book=getIntent().getExtras().getParcelable(MainActivity.BOOK_DETAILS_KEY);
		titleValue.setText(book.getTitle());
		Author authors[]=book.getAuthors();
        Log.i(ViewBookActivity.class.getCanonicalName(),"authors length: "+authors.length);
        String temp[]=new String[authors.length];
        int index=0;
        for(Author a: authors){
            String j=new String();
            if(a.middleInitial != null){
                j = a.firstName+" "+a.middleInitial+" "+a.lastName;
            }else{
                j = a.firstName+" "+a.lastName;
            }
            temp[index]=j;
        }
        authorsAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,temp);
        authorValue.setAdapter(authorsAdapter);
        ISBNValue.setText(book.getIsbn());
		Log.i(ViewBookActivity.class.getCanonicalName(),""+authors.length);
//		String authorsName = "";
//		for(Author a : authors){
//			if(a.middleInitial != null){
//				authorsName += a.firstName+" "+a.middleInitial+" "+a.lastName;
//			}else{
//				authorsName += a.firstName+" "+a.lastName;
//			}
//			authorsName += "\n";
//		}
		//authorValue.setText(authorsName);

	}

}