package com.secuxtech.mysecuxpay.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.secuxtech.mysecuxpay.R;

public class BaseActivity extends AppCompatActivity {

    protected boolean mShowBackButton = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorTitle)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorTitle));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorTitle)));

        getSupportActionBar().setDisplayHomeAsUpEnabled(mShowBackButton);
        //getSupportActionBar().setTitle("Home");
        //getSupportActionBar().setSubtitle("sairam");
    }
}
