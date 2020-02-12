package com.secuxtech.mysecuxpay.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.secuxtech.mysecuxpay.Model.Account;
import com.secuxtech.mysecuxpay.Model.Wallet;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccount;
import com.secuxtech.paymentkit.SecuXCoinType;
import com.secuxtech.paymentkit.SecuXPaymentManager;
import com.secuxtech.paymentkit.SecuXPaymentManagerCallback;

public class PaymentDetailsActivity extends BaseActivity {

    public static final String PAYMENT_RESULT = "com.secux.MySecuXPay.PAYMENTRESULT";
    public static final String PAYMENT_AMOUNT = "com.secux.MySecuXPay.AMOUNT";
    public static final String PAYMENT_COINTYPE = "com.secux.MySecuXPay.COINTYPE";
    public static final String PAYMENT_STORENAME = "com.secux.MySecuXPay.STORENAME";

    private Context mContext = this;
    private ProgressBar mProgressBar;

    private SecuXPaymentManager mPaymentManager = new SecuXPaymentManager();

    private String mPaymentInfo = ""; //"{\"amount\":\"11\", \"coinType\":\"DCT\", \"deviceID\":\"4ab10000726b\"}";
    private String mStoreName = "";
    private String mAmount = "";
    private @SecuXCoinType.CoinType String mType;

    private Account mAccount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);


        Intent intent = getIntent();
        mPaymentInfo = intent.getStringExtra(MainActivity.PAYMENT_INFO);
        mAmount = intent.getStringExtra(PAYMENT_AMOUNT);
        mType = intent.getStringExtra(PAYMENT_COINTYPE);
        mAccount = Wallet.getInstance().getAccount(mType);

        ImageView imageviewLogo = findViewById(R.id.imageView_account_coinlogo);
        imageviewLogo.setImageResource(mAccount.GetCoinLogo());

        TextView textviewName = findViewById(R.id.textView_account_name);
        textviewName.setText(mAccount.mName);

        TextView textviewAmount = findViewById(R.id.editText_payment_amount);
        textviewAmount.setText(mAmount);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_load_storeinfo);
        mProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {

                Wallet.getInstance().getCoinToUsdRate();
                Wallet.getInstance().getAccountBalance(mAccount);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textviewBalance = findViewById(R.id.textView_account_balance);
                        textviewBalance.setText(String.format("%.2f", mAccount.mBalance) + " " + mType);

                        TextView textviewUsdbalance = findViewById(R.id.textView_account_usdbalance);
                        textviewUsdbalance.setText(String.format("$ %.2f", mAccount.mUsdBanance));
                    }
                });


                //Must set the callback for the SecuXPaymentManager
                mPaymentManager.setSecuXPaymentManagerCallback(mPaymentMgrCallback);

                //Use SecuXPaymentManager to get store info.
                mPaymentManager.getStoreInfo(getBaseContext(), mPaymentInfo);


            }
        }).start();

        EditText edittext = findViewById(R.id.editText_payment_amount);
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void onPayButtonClick(View v){
        CommonProgressDialog.showProgressDialog(mContext);

        //Create SecuX account
        SecuXAccount account = new SecuXAccount("ifun-886-936105934-6", SecuXCoinType.DCT, "", "", "");

        //Use SecuXManager to do payment, must call in main thread
        mPaymentManager.doPayment(mContext, account, mStoreName, mPaymentInfo);

    }


    //Callback for SecuXPaymentManager
    private SecuXPaymentManagerCallback mPaymentMgrCallback = new SecuXPaymentManagerCallback() {

        //Called when payment is completed. Returns payment result and error message.
        @Override
        public void paymentDone(final boolean ret, final String errorMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonProgressDialog.dismiss();
                    if (ret){
                        //Toast toast = Toast.makeText(mContext, "Payment successful!", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER,0,0);
                        //toast.show();



                    }else{
                        //Toast toast = Toast.makeText(mContext, "Payment failed! Error: " + errorMsg, Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER,0,0);
                        //toast.show();
                    }

                    String amountStr = mAmount.toString() + " " + mType;

                    Intent newIntent = new Intent(mContext, PaymentResultActivity.class);
                    newIntent.putExtra(PAYMENT_RESULT, ret);
                    newIntent.putExtra(PAYMENT_STORENAME, mStoreName);
                    newIntent.putExtra(PAYMENT_AMOUNT, amountStr);
                    startActivity(newIntent);
                }
            });

        }

        //Called when payment status is changed. Payment status are: "Device connecting...", "DCT transferring..." and "Device verifying..."
        @Override
        public void updatePaymentStatus(final String status){
            Log.i("secux-paymentkit-exp", "Update payment status: " + status);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonProgressDialog.setProgressTip(status);
                }
            });
        }

        //Called when get store information is completed. Returns store name and store logo.
        @Override
        public void getStoreInfoDone(final boolean ret, final String storeName, final Bitmap storeLogo){
            Log.i("secux-paymentkit-exp", "Get store info. done ret=" + String.valueOf(ret) + ",name=" + storeName);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    mProgressBar.setVisibility(View.INVISIBLE);
                    if (ret){
                        mStoreName = storeName;
                        TextView textviewStoreName = findViewById(R.id.textView_storename);
                        textviewStoreName.setText(mStoreName);

                        ImageView imgviewStoreLogo = findViewById(R.id.imageView_storelogo);
                        imgviewStoreLogo.setVisibility(View.VISIBLE);

                        imgviewStoreLogo.setImageBitmap(storeLogo);
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
