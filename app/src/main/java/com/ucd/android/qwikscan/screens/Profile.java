package com.ucd.android.qwikscan.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ucd.android.qwikscan.R;

/**
 * This class takes care of operation and tasks to do with the user profile view
 */
public class Profile extends AppCompatActivity implements View.OnClickListener{

    private String name;
    private String email;

    /**
     * This method is called on object/class creation, it initializes
     * the user name and email from the intent
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent callingIntent = getIntent();
        Bundle data = callingIntent.getExtras();
        name = data.getString("name");
        email = data.getString("email");
        setContentView(R.layout.activity_profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method is responsible checking and navigating to the selected item
     * @param item each available option
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * onClick method that redirects to the Scan class to
     * begin scanning
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scan:
                Intent intent = new Intent(this,Scan.class);
                startActivity(intent);
                break;

        }
    }

    /**
     * A method which returns the user name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * A method returns the user email
     * @return email
     */
    public String getEmail() {
        return email;
    }
}