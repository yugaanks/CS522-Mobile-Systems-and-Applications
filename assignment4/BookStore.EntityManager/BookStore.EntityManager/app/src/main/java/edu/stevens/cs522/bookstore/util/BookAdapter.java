package edu.stevens.cs522.bookstore.util;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;

/**
 * Created by dduggan.
 */

public class BookAdapter extends ResourceCursorAdapter {

    protected final static int ROW_LAYOUT = R.layout.cart_row;

    public BookAdapter(Context context, Cursor cursor) {
        super(context, ROW_LAYOUT, cursor, 0);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(ROW_LAYOUT,parent,false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleLine=(TextView) view.findViewById(R.id.cart_row_title);
        TextView authorLine=(TextView) view.findViewById(R.id.cart_row_author);
        Book book=new Book(cursor);
        Author[] authors =  book.getAuthors();
        String temp[]=authors[0].getName().split(",");
        int index=0;
        Author[] authorsNew=new Author[temp.length];
        for(String s:temp){
            authorsNew[index]=new Author(s);
            index++;
        }
        titleLine.setText(book.getTitle());
        authorLine.setText(authorsNew[0].getName());
    }
}
