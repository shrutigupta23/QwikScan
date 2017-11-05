package com.ucd.android.qwikscan.paymentMethods;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ucd.android.qwikscan.R;

/**
 * This class takes care of all transaction dealing the QR payment option
 */
public class CashQRCode extends AppCompatActivity {

    TextView textView;
    ImageView barCode;
    Button confirmOrder;


    /**
     * onCreate method which generates and populates the QR code
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_qrcode);

        Intent intent = this.getIntent();
        String total = intent.getStringExtra(getString(R.string.total));

        textView = (TextView) findViewById(R.id.total);
        textView.setText(total);
        barCode = (ImageView) findViewById(R.id.QRCode);
        confirmOrder = (Button)findViewById(R.id.qr_leave);
        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK);
                finish();


            }
        });
        MultiFormatWriter mfw = new MultiFormatWriter();

        try{
            //puts total sum of shopping into QR code
            BitMatrix bm = mfw.encode(total, BarcodeFormat.QR_CODE, 2000, 2000);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bm);

            barCode.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
