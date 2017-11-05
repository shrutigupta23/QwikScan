package com.ucd.android.qwikscan.screens;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ucd.android.qwikscan.R;
import com.ucd.android.qwikscan.database.DatabaseHelper;
import com.ucd.android.qwikscan.model.Order;
import com.ucd.android.qwikscan.model.TransactionEntry;

import java.util.ArrayList;

public class Confirmation extends AppCompatActivity implements View.OnClickListener {

    private Order list_products;
    private String paymentMethod;
    private TextView textView;
    private TextView transactionId;
    private Button shareReceipt;
    private TableLayout receiptTable;
    private TextView message;

    /**
     * This method gets the list of products in the order from the calling intent and displays the user's payment method
     * It passes this list of products to an asnychronous task that inserts in the DB and generates a transaction no.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        textView = (TextView) findViewById(R.id.success);
        transactionId = (TextView) findViewById(R.id.transaction);
        shareReceipt = (Button) findViewById(R.id.share);
        shareReceipt.setOnClickListener(this);
        message = (TextView) findViewById(R.id.message);
        receiptTable = (TableLayout) findViewById(R.id.tableReceipt);

        Intent intent = getIntent();
        if (intent != null &&
                intent.hasExtra(getString(R.string.payment_method)) &&
                intent.hasExtra(getString(R.string.data))) {
            list_products = (Order) intent.getSerializableExtra(getString(R.string.data));

            paymentMethod = intent.getStringExtra(getString(R.string.payment_method));
            if(list_products.getOrderList().size() > 0) {
                new DBOperation().execute(list_products);
                textView.setText("Thank you for paying with " + paymentMethod);

            }
            else{
                textView.setText("No products were scanned");
            }
        }
        ;
    }

    /**
     * This is done so that user doesn't make multiple payments for same order
     */
    @Override
    public void onBackPressed() {
        Intent intent= new Intent(this,Scan.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

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
     * DB Task to insert the user's order
     */
    private class DBOperation extends AsyncTask<Order, Void, String> {
        @Override
        protected String doInBackground(Order... orders) {
            Order order = orders[0];
            String id = "";
            int sum = 0;
            ArrayList<Order> products = order.getOrderList();
            for (Order temp : products) {

                sum += Integer.parseInt(temp.getProductAmount());
            }
            TransactionEntry trans = new TransactionEntry();
            trans.setOrderId(products.get(0).getOrderId());
            trans.setTotalAmount(String.valueOf(sum));
            trans.setDate(String.valueOf(System.currentTimeMillis()));
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                dbHelper.addTransactionEntry(trans);


                for (Order temp : products) {

                    dbHelper = new DatabaseHelper(getApplicationContext());
                    dbHelper.addOrderEntry(temp);
                }
                return trans.getOrderId();
            }
            catch(android.database.sqlite.SQLiteConstraintException e){

                return trans.getOrderId();
            }

        }


        /**
         * On execution of the task, the receipt is displayed to the user in a tabular format
         * Since, the size of the receipt varies we need to generate the rows and text views dynamically in the code.
         * Beneath the receipt there is a button that allows user to share the receipt
         * @param id
         */

        @Override
        protected void onPostExecute(String id) {
            transactionId.setText("Your order is confirmed. Your transaction id is " + id);
            ArrayList<Order> products = list_products.getOrderList();

            TableRow row = new TableRow(getApplicationContext());
            TextView tv1 = new TextView(getApplicationContext());
            tv1.setText(getString(R.string.product_name));
            TextView tv2 = new TextView(getApplicationContext());
            tv2.setText(getString(R.string.product_price));

            tv1.setPadding(3,3,3,3);
            tv2.setPadding(3,3,3,3);
            tv1.setTextColor(getColor(R.color.tableBg));
            tv2.setTextColor(getColor(R.color.tableBg));

            row.addView(tv1);
            row.addView(tv2);
            receiptTable.addView(row,0);

            for(int i = 1; i <= products.size();i++){
                row = new TableRow(getApplicationContext());
                tv1 = new TextView(getApplicationContext());
                tv1.setText(products.get(i-1).getProductName());
                tv2 = new TextView(getApplicationContext());
                tv2.setText(products.get(i-1).getProductAmount());

                tv1.setPadding(3,3,3,3);
                tv2.setPadding(3,3,3,3);

                tv1.setTextColor(getColor(R.color.tableBg));
                tv2.setTextColor(getColor(R.color.tableBg));

                row.addView(tv1);
                row.addView(tv2);
                receiptTable.addView(row,i);


            }
            message.setVisibility(View.VISIBLE);
            receiptTable.setVisibility(View.VISIBLE);
            shareReceipt.setVisibility(View.VISIBLE);
        }
    }

    /**
     * On clicking of the 'Share Receipt' button , an Intent to send data is launched.
     * Any activity registered for this intent : e-mail, whatsapp etc can be opened.
     * @param view
     */

    @Override
    public void onClick(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String allData = "Product Name \t Product Price \t Quantity";
        ArrayList<Order> products = list_products.getOrderList();
        for (Order temp : products) {
            allData = allData + "\n" + temp.getProductName() + "\t" + temp.getProductAmount() + "\t" + temp.getQuantity();
        }
        sendIntent.putExtra(Intent.EXTRA_TEXT, allData);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
