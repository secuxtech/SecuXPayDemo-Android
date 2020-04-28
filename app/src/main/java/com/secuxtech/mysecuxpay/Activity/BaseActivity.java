package com.secuxtech.mysecuxpay.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "SecuXEvPay";
    protected boolean mShowBackButton = true;
    protected boolean mShowLogo = true;
    protected Context mContext = this;

    //@SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_base);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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



    }

    @Override
    protected void onResume(){
        super.onResume();

        this.checkWifi();
    }

    protected boolean checkWifi(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    public void showProgressInMain(String info){
        final String msgInfo = info;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonProgressDialog.showProgressDialog(mContext, msgInfo);
            }
        });
    }

    public void hideProgressInMain(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonProgressDialog.dismiss();
            }
        });
    }

    public void showLoginWndInMain(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mContext, "Login timeout! Please login again!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

                Intent newIntent = new Intent(mContext, LoginActivity.class);
                startActivity(newIntent);
                return;
            }
        });
    }

    public void checkBluetoothSetting(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast toast = Toast.makeText(mContext, "The phone DOES NOT support bluetooth! APP will terminate!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            finish();
            return;
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            Toast toast = Toast.makeText(mContext, "Please turn on Bluetooth!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
}
