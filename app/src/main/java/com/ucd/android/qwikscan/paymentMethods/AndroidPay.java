package com.ucd.android.qwikscan.paymentMethods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ucd.android.qwikscan.R;

/**
 * As described in the report Android Pay functionality was not
 * fully integrated. Look at report for more detail
 */
public class AndroidPay extends AppCompatActivity {

    Button confirmOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_pay);
        confirmOrder = (Button) findViewById(R.id.android_pay_leave);
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
