package edu.stevens.cs522.bookstore.util;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Author;

/**
 * Created by dduggan.
 */

public class BookAdapter extends ResourceCursorAdapter {

    protected final static int ROW_LAYOUT = R.layout.cart_row;

    public BookAdapter(Context context, Cursor cursor) {
        super(context, ROW_LAYOUT, cursor, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(ROW_LAYOUT,parent,false);
    }
    public Author[] stringArrayToAuthorArray(String[] authorsName) {
        String[] newArray=authorsName[0].split("\n");
        Author[] authors = new Author[newArray.length];
        int index = 0;
        for (String s : newArray) {
            Author a = null;
            String[] full_name = s.split("\\s+");
            if (full_name.length == 1) {
                a = new Author(full_name[0]);
            } else if (full_name.length == 2) {
                a = new Author(full_name[0], full_name[1]);
            } else if (full_name.length == 3) {
                a = new Author(full_name[0], full_name[1], full_name[2]);
            }
            authors[index] = a;
            index++;
        }
        return authors;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO
        TextView titleLine=(TextView) view.findViewById(R.id.cart_row_title);
        TextView authorLine=(TextView) view.findViewById(R.id.cart_row_author);
        titleLine.setText(BookContract.getTitle(cursor));
        //authorLine.setText(BookContract.getAuthors(cursor)[0]);
        Author[] authors =  stringArrayToAuthorArray(BookContract.getAuthors(cursor));
       // Log.i(BookAdapter.class.getCanonicalName(),BookContract.getIsbn(cursor)+" :lul: "+BookContract.getAuthors(cursor).length);
        //Log.i(BookAdapter.class.getCanonicalName(),"length:"+authors.length+" "+authors[0].lastName+" "+authors[0].firstName);
        if (authors == null || authors.length < 1) {
            authorLine.setText("No authors");
        } else {
            authorLine.setText(authors[0].firstName);
        }
    }
}