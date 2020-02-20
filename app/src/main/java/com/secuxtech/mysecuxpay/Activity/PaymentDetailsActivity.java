package com.secuxtech.mysecuxpay.Activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
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

import java.security.Signature;
import java.text.SimpleDateFormat;
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
    public static final String PAYMENT_STORENAME = "com.secux.MySecuXPay.STORENAME";
    public static final String PAYMENT_DATE = "com.secux.MySecuXPay.DATE";

    private Context mContext = this;
    private ProgressBar mProgressBar;

    private SecuXPaymentManager mPaymentManager = new SecuXPaymentManager();

    private String mPaymentInfo = ""; //"{\"amount\":\"11\", \"coinType\":\"DCT\", \"deviceID\":\"4ab10000726b\"}";
    private String mStoreName = "";
    private String mAmount = "";
    private String mType = "";
    private String mToken = "";

    private SecuXCoinAccount mCoinAccount = null;
    private SecuXCoinTokenBalance mTokenBalance = null;

    private Timer mMonitorPaymentTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        Intent intent = getIntent();
        mPaymentInfo = intent.getStringExtra(MainActivity.PAYMENT_INFO);
        mAmount = intent.getStringExtra(PAYMENT_AMOUNT);
        mType = intent.getStringExtra(PAYMENT_COINTYPE);
        mToken = intent.getStringExtra(PAYMENT_TOKEN);

        mCoinAccount = Setting.getInstance().mAccount.getCoinAccount(mType);
        mTokenBalance = mCoinAccount.getBalance(mToken);

        ImageView imageviewLogo = findViewById(R.id.imageView_account_coinlogo);
        imageviewLogo.setImageResource(AccountUtil.getCoinLogo(mType));

        TextView textviewName = findViewById(R.id.textView_account_name);
        textviewName.setText(Setting.getInstance().mAccount.getCoinAccount(mType).mAccountName);

        TextView textviewAmount = findViewById(R.id.editText_paymentinput_amount);
        textviewAmount.setText(mAmount);

        ImageView payinputLogo = findViewById(R.id.imageView_paymentinput_coinlogo);
        payinputLogo.setImageResource(AccountUtil.getCoinLogo(mType));

        TextView textviewPaymentType = findViewById(R.id.textView_paymentinput_coinname);
        textviewPaymentType.setText(mToken);

        Button buttonPay = findViewById(R.id.button_pay);
        buttonPay.setEnabled(false);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_load_storeinfo);
        mProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SecuXAccountManager accMgr = new SecuXAccountManager();
                accMgr.getAccountBalance(Setting.getInstance().mAccount, mType, mToken);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textviewName = findViewById(R.id.textView_account_name);
                        textviewName.setText(Setting.getInstance().mAccount.getCoinAccount(mType).mAccountName);

                        TextView textviewBalance = findViewById(R.id.textView_account_balance);
                        textviewBalance.setText(String.format("%.2f", mTokenBalance.mFormattedBalance) + " " + mToken);

                        TextView textviewUsdbalance = findViewById(R.id.textView_account_usdbalance);
                        textviewUsdbalance.setText(String.format("$ %.2f", mTokenBalance.mUSDBalance));
                    }
                });

                //Must set the callback for the SecuXPaymentManager
                mPaymentManager.setSecuXPaymentManagerCallback(mPaymentMgrCallback);

                //Use SecuXPaymentManager to get store info.
                mPaymentManager.getStoreInfo(getBaseContext(), mPaymentInfo);

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
        if (payAmount<=0 || payAmount > mTokenBalance.mFormattedBalance){
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

        CommonProgressDialog.showProgressDialog(mContext);

        //Use SecuXManager to do payment, must call in main thread
        mPaymentManager.doPayment(mContext, Setting.getInstance().mAccount, mStoreName, mPaymentInfo);
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

                    String amountStr = mAmount.toString() + " " + mType;

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

                        Button buttonPay = findViewById(R.id.button_pay);
                        buttonPay.setEnabled(true);
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
