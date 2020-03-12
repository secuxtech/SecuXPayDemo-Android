package com.secuxtech.mysecuxpay.Activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.an.biometric.BiometricCallback;
import com.an.biometric.BiometricManager;
import com.secuxtech.mysecuxpay.Adapter.CoinAccountListAdapter;
import com.secuxtech.mysecuxpay.Interface.AdapterItemClickListener;
import com.secuxtech.mysecuxpay.Model.CoinTokenAccount;
import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.AccountUtil;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;

import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXCoinAccount;
import com.secuxtech.paymentkit.SecuXCoinTokenBalance;
import com.secuxtech.paymentkit.SecuXPaymentManager;
import com.secuxtech.paymentkit.SecuXPaymentManagerCallback;
import com.secuxtech.paymentkit.SecuXUserAccount;

import org.json.JSONObject;

import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.arch.core.executor.ArchTaskExecutor.getMainThreadExecutor;

public class PaymentDetailsActivity extends BaseActivity {

    public static final String PAYMENT_RESULT = "com.secux.MySecuXPay.PAYMENTRESULT";
    public static final String PAYMENT_AMOUNT = "com.secux.MySecuXPay.AMOUNT";
    public static final String PAYMENT_COINTYPE = "com.secux.MySecuXPay.COINTYPE";
    public static final String PAYMENT_TOKEN = "com.secux.MySecuXPay.TOKEN";
    public static final String PAYMENT_DEVID = "com.secux.MySecuXPay.DEVID";
    public static final String PAYMENT_DEVIDHASH = "com.secux.MySecuXPay.DEVIDHASH";
    public static final String PAYMENT_STORENAME = "com.secux.MySecuXPay.STORENAME";
    public static final String PAYMENT_DATE = "com.secux.MySecuXPay.DATE";
    public static final String PAYMENT_SHOWACCOUNTSEL = "com.secux.MySecuXPay.SHOWACCOUNTSEL";

    private Context mContext = this;
    private ProgressBar mProgressBar;

    private SecuXPaymentManager mPaymentManager = new SecuXPaymentManager();

    private String mPaymentInfo = ""; //"{\"amount\":\"11\", \"coinType\":\"DCT\", \"deviceID\":\"4ab10000726b\"}";
    private String mStoreInfo = "";
    private String mStoreName = "";
    private String mAmount = "";
    private String mType = "";
    private String mToken = "";
    private String mDevID = "";
    private String mDevIDhash = "";

    private SecuXCoinAccount mCoinAccount = null;
    private SecuXCoinTokenBalance mTokenBalance = null;

    private Timer mMonitorPaymentTimer = new Timer();

    private Dialog mAccountSelDialog;
    private boolean mShowAccountSel = false;

    private Button mButtonPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        Intent intent = getIntent();
        mPaymentInfo = intent.getStringExtra(PaymentMainActivity.PAYMENT_INFO);
        mAmount = intent.getStringExtra(PAYMENT_AMOUNT);
        mType = intent.getStringExtra(PAYMENT_COINTYPE);
        mToken = intent.getStringExtra(PAYMENT_TOKEN);
        mDevID = intent.getStringExtra(PAYMENT_DEVID);
        mDevIDhash = intent.getStringExtra(PAYMENT_DEVIDHASH);
        mShowAccountSel = intent.getBooleanExtra(PAYMENT_SHOWACCOUNTSEL, false);

        mCoinAccount = Setting.getInstance().mAccount.getCoinAccount(mType);
        mTokenBalance = mCoinAccount.getBalance(mToken);

        ImageView imageviewLogo = findViewById(R.id.imageView_account_coinlogo);
        imageviewLogo.setImageResource(AccountUtil.getCoinLogo(mType));

        TextView textviewName = findViewById(R.id.textView_account_name);
        textviewName.setText(Setting.getInstance().mAccount.getCoinAccount(mType).mAccountName);

        TextView textviewAmount = findViewById(R.id.editText_paymentinput_amount);
        if (Double.valueOf(mAmount) > 0.0){
            textviewAmount.setText(mAmount);
        }else{
            textviewName.requestFocus();
        }

        ImageView payinputLogo = findViewById(R.id.imageView_paymentinput_coinlogo);
        payinputLogo.setImageResource(AccountUtil.getCoinLogo(mType));

        TextView textviewPaymentType = findViewById(R.id.textView_paymentinput_coinname);
        textviewPaymentType.setText(mToken);

        mButtonPay = findViewById(R.id.button_pay);
        mButtonPay.setEnabled(false);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_load_storeinfo);
        mProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SecuXAccountManager accMgr = new SecuXAccountManager();

                if (mShowAccountSel){
                    accMgr.getAccountBalance(Setting.getInstance().mAccount);
                }else{
                    accMgr.getAccountBalance(Setting.getInstance().mAccount, mType, mToken);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textviewName = findViewById(R.id.textView_account_name);
                        textviewName.setText(Setting.getInstance().mAccount.getCoinAccount(mType).mAccountName);

                        TextView textviewBalance = findViewById(R.id.textView_account_balance);
                        textviewBalance.setText(String.format("%.2f", mTokenBalance.mFormattedBalance) + " " + mToken);

                        TextView textviewUsdbalance = findViewById(R.id.textView_account_usdbalance);
                        textviewUsdbalance.setText(String.format("$ %.2f", mTokenBalance.mUSDBalance));


                        CardView cardViewAccount = findViewById(R.id.cardView_account);
                        cardViewAccount.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickAccount(v);
                            }
                        });
                    }
                });

                //Must set the callback for the SecuXPaymentManager
                mPaymentManager.setSecuXPaymentManagerCallback(mPaymentMgrCallback);

                //Use SecuXPaymentManager to get store info.
                mPaymentManager.getStoreInfo(mDevIDhash);

            }
        }).start();

        EditText edittext = findViewById(R.id.editText_paymentinput_amount);
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void onPayButtonClick(View v){

        EditText edittextAmount = findViewById(R.id.editText_paymentinput_amount);
        String strAmount = edittextAmount.getText().toString();
        if (strAmount.length() == 0){
            Toast toast = Toast.makeText(mContext, "Invalid payment amount!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        Double payAmount = Double.valueOf(strAmount);
        if (payAmount<=0 || payAmount > mTokenBalance.mFormattedBalance.doubleValue()){
            Toast toast = Toast.makeText(mContext, "Invalid payment amount!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            Toast toast = Toast.makeText(mContext, "Please turn on Bluetooth! Payment abort!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM,0,200);
            toast.show();
            return;
        }

        /*
        mMonitorPaymentTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (CommonProgressDialog.isProgressVisible()){
                    mPaymentManager.cancelPayment();
                    CommonProgressDialog.dismiss();

                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(500);
                    }

                    MediaPlayer mediaPlayer = new MediaPlayer();
                    AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.payfailed);
                    try {
                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        afd.close();
                        mediaPlayer.prepare();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();


                    String amountStr = mAmount + " " + mType;

                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                    Date date = Calendar.getInstance().getTime();
                    String dateStr = simpleDateFormat.format(date);

                    Intent newIntent = new Intent(mContext, PaymentResultActivity.class);
                    newIntent.putExtra(PAYMENT_RESULT, false);
                    newIntent.putExtra(PAYMENT_STORENAME, mStoreName);
                    newIntent.putExtra(PAYMENT_AMOUNT, amountStr);
                    newIntent.putExtra(PAYMENT_DATE, dateStr);
                    startActivity(newIntent);
                }
            }
        }, 10000);
        */

        mAmount = strAmount;

        try{
            new BiometricManager.BiometricBuilder(this)
                    .setTitle("Pay to " + mStoreName)
                    .setSubtitle("MySecuXPay")
                    .setDescription("Allow payment with your biometric ID")
                    .setNegativeButtonText("Cancel")
                    .build()
                    .authenticate(mBiometricCallback);
        }catch (Exception e){
            Log.i(TAG, e.getMessage());
            doPayment();
        }

    }

    private void doPayment(){
        CommonProgressDialog.showProgressDialog(mContext);

        try {
            JSONObject payInfoJson = new JSONObject();
            payInfoJson.put("amount", mAmount);
            payInfoJson.put("coinType", mType);
            payInfoJson.put("token", mToken);
            payInfoJson.put("deviceID", mDevID);
            mPaymentInfo = payInfoJson.toString();
            mPaymentManager.doPayment(mContext, Setting.getInstance().mAccount, mStoreInfo, mPaymentInfo);
        }catch (Exception e){

            Toast toast = Toast.makeText(mContext, "Generate payment data failed!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM,0,200);
            toast.show();
            return;
        }
    }

    private BiometricCallback mBiometricCallback = new BiometricCallback() {
        @Override
        public void onSdkVersionNotSupported() {

        }

        @Override
        public void onBiometricAuthenticationNotSupported() {

        }

        @Override
        public void onBiometricAuthenticationNotAvailable() {

        }

        @Override
        public void onBiometricAuthenticationPermissionNotGranted() {

        }

        @Override
        public void onBiometricAuthenticationInternalError(String error) {

        }

        @Override
        public void onAuthenticationFailed() {

        }

        @Override
        public void onAuthenticationCancelled() {

        }

        @Override
        public void onAuthenticationSuccessful() {
            doPayment();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {

        }
    };

    public void onClickAccount(View v){
        if (!mShowAccountSel){
            return;
        }
        Log.i(TAG, "click the account");
        showDialog(this);
    }

    public void showDialog(Activity activity){

        mAccountSelDialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAccountSelDialog.setCancelable(true);
        mAccountSelDialog.setContentView(R.layout.dialog_account_list_selection_layout);

        AdapterItemClickListener mItemClickListener = new AdapterItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CoinTokenAccount account = AccountUtil.getCoinTokenAccounts().get(position);
                Log.i(TAG, account.mAccountName);

                mType = account.mCoinType;
                mToken = account.mToken;

                ImageView imageviewLogo = findViewById(R.id.imageView_account_coinlogo);
                imageviewLogo.setImageResource(AccountUtil.getCoinLogo(account.mCoinType));

                TextView textviewName = findViewById(R.id.textView_account_name);
                textviewName.setText(account.mAccountName);

                TextView textviewBalance = findViewById(R.id.textView_account_balance);
                textviewBalance.setText(String.format("%.2f", mTokenBalance.mFormattedBalance) + " " + mToken);

                TextView textviewUsdbalance = findViewById(R.id.textView_account_usdbalance);
                textviewUsdbalance.setText(String.format("$ %.2f", mTokenBalance.mUSDBalance));

                ImageView payinputLogo = findViewById(R.id.imageView_paymentinput_coinlogo);
                payinputLogo.setImageResource(AccountUtil.getCoinLogo(account.mCoinType));

                TextView textviewPaymentType = findViewById(R.id.textView_paymentinput_coinname);
                textviewPaymentType.setText(account.mCoinType);

                mAccountSelDialog.dismiss();

            }
        };

        RecyclerView recyclerView = mAccountSelDialog.findViewById(R.id.recyclerView_accountsel_dialog);
        CoinAccountListAdapter adapterRe = new CoinAccountListAdapter(PaymentDetailsActivity.this, AccountUtil.getCoinTokenAccounts(), mItemClickListener);
        recyclerView.setAdapter(adapterRe);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        mAccountSelDialog.show();

    }

    //Callback for SecuXPaymentManager
    private SecuXPaymentManagerCallback mPaymentMgrCallback = new SecuXPaymentManagerCallback() {

        //Called when payment is completed. Returns payment result and error message.
        @Override
        public void paymentDone(final boolean ret, final String errorMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMonitorPaymentTimer.cancel();
                    CommonProgressDialog.dismiss();

                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(500);
                    }

                    MediaPlayer mediaPlayer = new MediaPlayer();
                    AssetFileDescriptor afd;

                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                    Date date = Calendar.getInstance().getTime();
                    String dateStr = simpleDateFormat.format(date);
                    if (ret){
                        //Toast toast = Toast.makeText(mContext, "Payment successful!", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER,0,0);
                        //toast.show();

                        //Double usdAmount = Wallet.getInstance().getUSDValue(Double.valueOf(mAmount), mAccount.mCoinType);

                        //PaymentHistoryModel payment = new PaymentHistoryModel(mAccount, mStoreName, dateStr, String.format("%.2f", usdAmount), mAmount);
                        //Wallet.getInstance().addPaymentHistoryItem(payment);


                        afd = getResources().openRawResourceFd(R.raw.paysuccess);


                    }else{
                        String message = errorMsg;
                        if (message.contains("Scan timeout")){
                            message = "No payment device!";
                        }

                        Toast toast = Toast.makeText(mContext, "Payment failed! Error: " + message, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();


                        afd = getResources().openRawResourceFd(R.raw.payfailed);
                    }

                    try {
                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        afd.close();
                        mediaPlayer.prepare();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();

                    String amountStr = mAmount.toString() + " " + mToken;

                    Intent newIntent = new Intent(mContext, PaymentResultActivity.class);
                    newIntent.putExtra(PAYMENT_RESULT, ret);
                    newIntent.putExtra(PAYMENT_STORENAME, mStoreName);
                    newIntent.putExtra(PAYMENT_AMOUNT, amountStr);
                    newIntent.putExtra(PAYMENT_DATE, dateStr);
                    startActivity(newIntent);
                }
            });

        }

        //Called when payment status is changed. Payment status are: "Device connecting...", "DCT transferring..." and "Device verifying..."
        @Override
        public void updatePaymentStatus(final String status){
            Log.i("secux-paymentkit-exp", "Update payment status:" + SystemClock.uptimeMillis() + " " + status);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonProgressDialog.setProgressTip(status);
                }
            });
        }

        //Called when get store information is completed. Returns store name and store logo.
        @Override
        public void getStoreInfoDone(final boolean ret, final String storeInfo, final Bitmap storeLogo){
            Log.i("secux-paymentkit-exp", "Get store info. done ret=" + String.valueOf(ret) + ",name=" + storeInfo);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    mProgressBar.setVisibility(View.INVISIBLE);
                    if (ret){
                        mStoreInfo = storeInfo;

                        mStoreName = "";
                        try{
                            JSONObject storeInfoJson = new JSONObject(mStoreInfo);
                            mStoreName = storeInfoJson.getString("name");
                        }catch (Exception e){
                        }

                        TextView textviewStoreName = findViewById(R.id.textView_storename);
                        textviewStoreName.setText(mStoreName);

                        ImageView imgviewStoreLogo = findViewById(R.id.imageView_storelogo);
                        imgviewStoreLogo.setVisibility(View.VISIBLE);

                        imgviewStoreLogo.setImageBitmap(storeLogo);

                        Button buttonPay = findViewById(R.id.button_pay);
                        buttonPay.setEnabled(true);
                    }else{
                        ImageView imgviewStoreLogo = findViewById(R.id.imageView_storelogo);
                        imgviewStoreLogo.setVisibility(View.VISIBLE);

                        imgviewStoreLogo.setImageResource(R.drawable.storename_unavailable);

                        Toast toast = Toast.makeText(mContext, "Get store info. failed!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();

                        mButtonPay.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorButtonDisabled));

                    }
                }
            });

        }

        @Override
        public void userAccountUnauthorized(){
            showMessageInMain("User account authorization timeout! Please login again");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent newIntent = new Intent(mContext, LoginActivity.class);
                    startActivity(newIntent);
                }
            });
        }

    };

}
