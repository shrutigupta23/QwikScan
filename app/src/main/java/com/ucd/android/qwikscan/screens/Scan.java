package com.ucd.android.qwikscan.screens;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ucd.android.qwikscan.R;
import com.ucd.android.qwikscan.database.DatabaseHelper;
import com.ucd.android.qwikscan.model.Barcode;
import com.ucd.android.qwikscan.model.Order;

import java.util.ArrayList;

/**
 * The class handles barcode scanning and populates the list in a list view
 * Total price is aggregated with each scan
 */
public class Scan extends AppCompatActivity implements View.OnClickListener {

    // Local variables for buttons and text fields
    private Button scanButton;
    private Button proceedButton;
    private TextView totalPrice;
    private Barcode barcode;
    private int row, sum;

    private TableRow[] tr = new TableRow[100];

    /**
     * The method retrieves the buttons and text fields, and creates an event listener for buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        TextView productName = (TextView) findViewById(R.id.product_name);
        TextView productPrice = (TextView) findViewById(R.id.product_price);

        scanButton = (Button)findViewById(R.id.scan_button);
        proceedButton = (Button)findViewById(R.id.proceed_button);

        totalPrice = (TextView)findViewById(R.id.total);

        // Creating a click event listener for Scan and Proceed buttons
        scanButton.setOnClickListener(this);
        proceedButton.setOnClickListener(this);

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
     * The method handles click events for Buttons
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.scan_button:
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                // initiates the barcode scanner
                scanIntegrator.initiateScan();
                break;
            case R.id.proceed_button:
                Intent intent = new Intent(this,Payment.class);
                // passes total price and products list to next activity
                intent.putExtra(getString(R.string.total),totalPrice.getText());
                intent.putExtra(getString(R.string.data),readTable());
                startActivity(intent);
        }
    }

    /**
     * The method reads products list's data and adds them to an Order list
     * which is later used to show the receipt for the customer's purchase
     * @return an instance with all the scanned products' data
     */
    private Order readTable(){
        ArrayList<Order> orders = new ArrayList<Order>();
        String id = String.valueOf(System.currentTimeMillis());
        TableLayout
                table = (TableLayout) findViewById(R.id.table);
        int no_rows = table.getHeight();

        // iterate through all scanned products in the table
        for(int i = 1; i < no_rows;i++){
            TableRow tableRow = (TableRow)table.getChildAt(i);
            if(tableRow != null){
                int no_cols = table.getWidth();
                // creating an oder list of all products scanned
                // used for receipt and payment
                for(int j = 0;j < no_cols - 1;j++){
                    TextView productName = (TextView)tableRow.getChildAt(j);
                    TextView productPrice = (TextView)tableRow.getChildAt(j + 1);
                    if(productName !=null && productPrice !=null){
                        // get product name and price from table row
                        String sproductName = productName.getText().toString();
                        String sproductPrice = productPrice.getText().toString();
                        Order order = new Order();
                        order.setOrderId(id);
                        order.setProductName(sproductName);
                        order.setQuantity("1");
                        order.setProductAmount(sproductPrice);

                        // orders class populated
                        orders.add(order);

                    }
                }
            }
        }
        Order list_order = new Order();
        list_order.setOrderList(orders);
        return list_order;
    }

    /**
     * After every product scanning, the method hits the database and get
     * the product name and price using the barcode ID
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        String scanContent = null;
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanningResult != null){
            scanContent = scanningResult.getContents();
        }
        if (scanContent != null) {

            String scanFormat = scanningResult.getFormatName();

            // create Database helper to retrieve products list based on barcode ID
            DatabaseHelper dbHelper = new DatabaseHelper(this);

            //pass scanned barcode ID to query in DB and receive the corresponding Product's Name and Price
            barcode = dbHelper.getBarcodeDetails(scanContent);
            if(barcode != null) {
                row = row + 1;
                final TableLayout
                        table = (TableLayout) findViewById(R.id.table);

                tr[row] = new TableRow(Scan.this);
                tr[row].setId(row);

                final TextView[] product = new TextView[100];
                product[row] = new TextView(Scan.this);
                product[row].setPadding(17,0,0,0);

                final TextView[] price = new TextView[100];
                price[row] = new TextView(Scan.this);
                price[row].setGravity(Gravity.RIGHT);
                price[row].setPadding(0,0,17,0);

                product[row].setTextSize(18);
                price[row].setTextSize(18);

                // create dynamic rows based on barcode result
                // populate with product name and price
                product[row].setText(barcode.getProduct_name());
                price[row].setText(barcode.getProduct_price());
                tr[row].addView(product[row]);
                tr[row].addView(price[row]);
                tr[row].setBackground(getResources().getDrawable(R.drawable.scan_decor));

                table.addView(tr[row]);

                sum += Integer.parseInt(price[row].getText().toString());
                totalPrice.setText(String.valueOf(sum));

                // delete products from list on long press
                tr[row].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int id = v.getId();
                        TableLayout t = (TableLayout) findViewById(R.id.table);

                        sum -= Integer.parseInt(price[id].getText().toString());

                        totalPrice.setText(String.valueOf(sum));
                        table.removeView(tr[id]);
                        return true;
                    }
                });
            }
            // alert user if a non TESCO product is scanned
            else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Sorry, this is not a "+getString(R.string.store_name)+" registered product!!", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
        // alert user when there is no scan result
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
