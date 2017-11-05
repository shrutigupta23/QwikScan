package com.ucd.android.qwikscan.paymentMethods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ucd.android.qwikscan.R;

/**
 * As described in the report PayPal functionality was not
 * fully integrated. Look at report for more detail
 */
public class PayPal extends AppCompatActivity {

    Button confirmOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);
        confirmOrder = (Button) findViewById(R.id.pay_pal_leave);
        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK);
                finish();
            }
        });

    }

}
