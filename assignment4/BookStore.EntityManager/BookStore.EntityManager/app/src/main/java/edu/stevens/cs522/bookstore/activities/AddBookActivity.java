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

    private EditText title, author, isbn;
    static int bookId = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book);
        title = (EditText) findViewById(R.id.search_title);
        author = (EditText) findViewById(R.id.search_author);
        isbn = (EditText) findViewById(R.id.search_isbn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addbookmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent();
                Book book = addBook();
                intent.putExtra(ViewBookActivity.BOOK_KEY, book);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            case R.id.cancel:
                finish();
                return true;
        }
        return false;
    }

    public Book addBook() {

        String str1 = author.getText().toString();
        String[] str=str1.split(",");
        Author[] authors = new Author[str.length];
        int index = 0;
        for (String s : str) {
            authors[index] = new Author(s);
            index++;
        }
        Book book = new Book(bookId++,
                title.getText().toString(),
                authors,
                isbn.getText().toString(),
                "$55");
        return book;
    }

}