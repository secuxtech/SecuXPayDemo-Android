package com.secuxtech.mysecuxpay.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;

public class TokenTransferDetailsActivity extends BaseActivity {

    public final static String TRANSACTION_HISTORY_DETAIL_URL = "com.secuxtech.MySecuXPay.TRANSHISDETAILURL";

    private String mDetailUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_transfer_details);

        Intent intent = getIntent();
        mDetailUrl = intent.getStringExtra(TRANSACTION_HISTORY_DETAIL_URL);

        CommonProgressDialog.showProgressDialog(mContext, "Loading...");
        final WebView detailWebView = findViewById(R.id.webView_transfer_details);
        detailWebView.getSettings().setBuiltInZoomControls(true);
        detailWebView.getSettings().setJavaScriptEnabled(true);
        detailWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                CommonProgressDialog.dismiss();
                Log.i(TAG, "onPageFinished");
            }
        });

        detailWebView.loadUrl(mDetailUrl);




    }
}
