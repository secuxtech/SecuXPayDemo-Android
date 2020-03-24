package com.secuxtech.mysecuxpay.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.secuxtech.mysecuxpay.R;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "MySecuXPay";
    protected boolean mShowBackButton = true;
    protected boolean mShowLogo = true;
    protected Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_base);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorTitle)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorTitle));

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.layout_secux_logo_imageview, null);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorTitle)));
            actionBar.setDisplayHomeAsUpEnabled(mShowBackButton);
            if (mShowLogo) {

                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setTitle("");
                actionBar.setCustomView(v);
            } else {
                actionBar.setDisplayShowCustomEnabled(false);
                actionBar.setTitle("");
                actionBar.hide();
            }
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    protected void onResume(){
        super.onResume();

        this.checkWifi();
    }

    protected boolean checkWifi(){
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //SupplicantState supState = wifiInfo.getSupplicantState();
        if (wifiInfo.getNetworkId() == -1){
            this.showMessageInMain("No internet! Please check the Wifi");
            return false;
        }

        return true;
    }

    protected void showMessageInMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
