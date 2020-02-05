package com.secuxtech.mysecuxpay;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.secuxtech.paymentkit.*;

public class MainActivity extends AppCompatActivity {

    private SecuXPaymentManager mPaymentManager = new SecuXPaymentManager();
    private String mPaymentInfo = "{\"amount\":\"11\", \"coinType\":\"DCT\", \"deviceID\":\"4ab10000726b\"}";
    private final Context mContext = this;

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

        new Thread(new Runnable() {
            @Override
            public void run() {

                //Must set the callback for the SecuXPaymentManager
                mPaymentManager.setSecuXPaymentManagerCallback(mPaymentMgrCallback);

                //Use SecuXPaymentManager to get store info.
                mPaymentManager.getStoreInfo(getBaseContext(), mPaymentInfo);
            }
        }).start();

    }

    //Callback for SecuXPaymentManager
    private SecuXPaymentManagerCallback mPaymentMgrCallback = new SecuXPaymentManagerCallback() {

        //Called when payment is completed. Returns payment result and error message.
        @Override
        public void paymentDone(final boolean ret, final String errorMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                if (ret){
                    Toast toast = Toast.makeText(mContext, "Payment successful!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(mContext, "Payment failed! Error: " + errorMsg, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                }
            });

        }

        //Called when payment status is changed. Payment status are: "Device connecting...", "DCT transferring..." and "Device verifying..."
        @Override
        public void updatePaymentStatus(final String status){
            Log.i("secux-paymentkit-example", "Update payment status: " + status);
        }

        //Called when get store information is completed. Returns store name and store logo.
        @Override
        public void getStoreInfoDone(final boolean ret, final String storeName, final Image storeLogo){
            Log.i("secux-paymentkit-example", "Get store info. done ret=" + String.valueOf(ret) + ",name=" + storeName);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                if (ret){
                    final String name = storeName;

                    //Create SecuX account
                    SecuXAccount account = new SecuXAccount("ifun-886-936105934-6", SecuXCoinType.DCT, "", "", "");

                    //Use SecuXManager to do payment, must call in main thread
                    mPaymentManager.doPayment(mContext, account, name, mPaymentInfo);

                }else{
                    Toast toast = Toast.makeText(mContext, "Get store info. failed!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                }
            });

        }

    };
}
