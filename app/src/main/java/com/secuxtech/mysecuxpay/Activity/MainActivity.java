package com.secuxtech.mysecuxpay.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;

import android.view.View;

import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.secuxtech.mysecuxpay.R;

import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private final Context mContext = this;
    private IntentIntegrator scanIntegrator;

    public static final String PAYMENT_INFO = "com.secux.MySecuXPay.PAYMENTINFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check

            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }



    public void onScanQRCodeButtonClick(View v)
    {
        View button1 = (View) findViewById(R.id.scan_qrcode_button);

        scanIntegrator = new IntentIntegrator(MainActivity.this);
        scanIntegrator.setPrompt("Start scan ...");
        scanIntegrator.setTimeout(300000);
        scanIntegrator.initiateScan();
    }




    //Callback when scan done
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null && scanningResult.getContents() != null)
        {
            final String scanContent = scanningResult.getContents();
            if (scanContent.length() > 0)
            {
                //Toast.makeText(getApplicationContext(),"Scan result: "+scanContent, Toast.LENGTH_LONG).show();
                Intent newIntent = new Intent(this, PaymentDetailsActivity.class);
                newIntent.putExtra(PAYMENT_INFO, scanContent);
                startActivity(newIntent);
                return;
            }

        }

        super.onActivityResult(requestCode, resultCode, intent);
        Toast.makeText(getApplicationContext(),"Scan failed!!",Toast.LENGTH_LONG).show();

    }


}
