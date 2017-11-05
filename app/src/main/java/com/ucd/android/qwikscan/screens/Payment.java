package com.ucd.android.qwikscan.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ucd.android.qwikscan.R;
import com.ucd.android.qwikscan.paymentMethods.AndroidPay;
import com.ucd.android.qwikscan.paymentMethods.CashQRCode;
import com.ucd.android.qwikscan.paymentMethods.PayPal;

import java.io.Serializable;

/**
 * The method enables a user to pay using either of the three options -
 * Android Pay, PayPal, or cash/card payment at the counter
 * The method handles these three scenarios
 */
public class Payment extends AppCompatActivity implements View.OnClickListener {

    // local variables for buttons and text fields
    TextView textViewTotal;
    Button androidPayButton;
    Button paypalButton;
    Button cashCardButton;
    Serializable product_data;

    int androidPayRequestCode, payPalRequestCode, qrRequestCode;

    /**
     * The method sets event listeners for all payment buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        textViewTotal = (TextView) findViewById(R.id.total_textview);

        // set click event listener for Android Pay
        androidPayButton = (Button) findViewById(R.id.android_pay);
        androidPayButton.setOnClickListener(this);

        // set click event listener for PayPal payment
        paypalButton = (Button) findViewById(R.id.paypal);
        paypalButton.setOnClickListener(this);

        // set click event listener to generate QR code for cash payment
        cashCardButton = (Button) findViewById(R.id.cash_card);
        cashCardButton.setOnClickListener(this);

        androidPayRequestCode = getApplicationContext().getResources().getInteger(R.integer.androidpay_request_code);
        payPalRequestCode = getApplicationContext().getResources().getInteger(R.integer.paypal_request_code);
        qrRequestCode = getApplicationContext().getResources().getInteger(R.integer.qr_request_code);

        Intent received = getIntent();
        // validate if all required data is received
        if(received!=null && received.hasExtra(getString(R.string.total)) && received.hasExtra(getString(R.string.data))){
            textViewTotal.setText(received.getStringExtra(getString(R.string.total)));
            product_data = received.getSerializableExtra(getString(R.string.data));
        }
        else
        {
            textViewTotal.setText(getString(R.string.error_message));
        }


    }

    /**
     * The method is used to create an options menu for the activity
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * The method is called whenever an item in the options menu is selected
     * @param item
     * @return true if already at home screen, or loops the method until it reaches Home screen
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * The method handles click events for payment to traverse to corresponding activity -
     * Android Pay, PayPal or cash_card
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.android_pay:
            {
                Intent androidPay = new Intent(this, AndroidPay.class);
                int game = R.integer.androidpay_request_code;
                Log.d("VALUE",String.valueOf(game));
                startActivityForResult(androidPay,androidPayRequestCode);
                break;
            }
            case R.id.paypal:
            {
                Intent payPal = new Intent(this, PayPal.class);
                startActivityForResult(payPal,payPalRequestCode);
                break;
            }
            case R.id.cash_card:
            {
                Intent cashCard = new Intent(this, CashQRCode.class);
                cashCard.putExtra(getString(R.string.total), textViewTotal.getText());
                startActivityForResult(cashCard,qrRequestCode);
            }
        }
    }

    /**
     * The method collects information on payment mode
     * and passes the values by calling the confirmation activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent(this,Confirmation.class);

        if(requestCode == androidPayRequestCode){
            intent.putExtra(getString(R.string.payment_method),getString(R.string.android_pay_button));
        }
        else if(requestCode == payPalRequestCode){
            intent.putExtra(getString(R.string.payment_method),getString(R.string.paypal_button));
        }
        else if(requestCode == qrRequestCode){
            intent.putExtra(getString(R.string.payment_method),getString(R.string.cash_card_button));
        }
        else{
            Log.d("ERROR","Oops! Something went wrong");
        }

        intent.putExtra(getString(R.string.data),product_data);
        startActivity(intent);

        super.onActivityResult(requestCode,resultCode,data);
    }
}
