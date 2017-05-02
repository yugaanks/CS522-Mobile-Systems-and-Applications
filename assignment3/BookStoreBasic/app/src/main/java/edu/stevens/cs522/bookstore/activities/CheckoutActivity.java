package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import edu.stevens.cs522.bookstore.R;

public class CheckoutActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO display ORDER and CANCEL options. done
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checkoutmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // TODO done
        if (item.getItemId() == R.id.order) {
            setResult(Activity.RESULT_OK);
            finish();
            return true;
        }
        if (item.getItemId() == R.id.cancel) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return true;
        }
        return false;
        // ORDER: display a toast message of how many books have been ordered and return

        // CANCEL: just return with REQUEST_CANCELED as the result code

    }

}