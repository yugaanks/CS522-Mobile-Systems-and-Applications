package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;

/**
 * Created by Yugaank on 1/31/2017.
 */

public class BookDetails extends Activity {
    private static final String BOOK_DETAILS = "Book_Details";
    private TextView titleValue,authorValue,ISBNValue,priceValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_details);
        titleValue=(TextView)findViewById(R.id.titleValue);
        authorValue=(TextView)findViewById(R.id.authorValue);
        ISBNValue=(TextView)findViewById(R.id.ISBNValue);
        priceValue=(TextView)findViewById(R.id.PriceValue);
        Book book=getIntent().getExtras().getParcelable(BOOK_DETAILS);
        titleValue.setText(book.getTitle());
        Author authors[]=book.getAuthors();
        String authorsName="";
        for(int i=0;i<authors.length;i++) {
            if(authors[i].middleInitial!=null){
                authorsName+=authors[i].firstName+" "+authors[i].middleInitial+" "+authors[i].lastName;
            }
            else {
                authorsName+=authors[i].firstName+" "+authors[i].lastName;
            }
            authorsName+="\n";
        }
        authorValue.setText(authorsName);
        ISBNValue.setText(book.getIsbn());
        priceValue.setText(book.getPrice());
    }
}
