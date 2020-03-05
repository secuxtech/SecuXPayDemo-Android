package com.secuxtech.mysecuxpay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.paymentkit.SecuXCoinAccount;
import com.secuxtech.paymentkit.SecuXPaymentManager;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;

import org.json.JSONObject;

public class PaymentMainActivity extends BaseActivity {

    private final Context mContext = this;
    private IntentIntegrator mScanIntegrator;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent = null;

    SecuXPaymentManager mPaymentManager = new SecuXPaymentManager();

    public static final String PAYMENT_INFO = "com.secux.MySecuXPay.PAYMENTINFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mShowBackButton = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check

            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (null == mNfcAdapter) {
            Toast toast = Toast.makeText(mContext, "No NFC support!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            //finish();
            //return;
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast toast = Toast.makeText(mContext, "Please turn on NFC!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            //finish();
            //return;
        }

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

        } else {
            // Bluetooth is enabled
        }

        BottomNavigationView navigationView = findViewById(R.id.navigation_main);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        MenuItem menuItem = navigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPendingIntent == null) {
            mPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        }

        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            processIntent(intent);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_main_accounts:

                    Intent newIntent = new Intent(mContext, CoinAccountListActivity.class);
                    startActivity(newIntent);
                    return true;
                case R.id.navigation_main_payment:

                    return true;

            }
            return false;
        }

    };

    private void processIntent(final Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        try {
            ndef.close();
            ndef.connect();
            NdefMessage messages = ndef.getNdefMessage();

            String amount="", devid="", cointype="", token="";
            for (final NdefRecord record : messages.getRecords()) {
                byte[] payload = record.getPayload();
                String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                int languageCodeLength = payload[0] & 0077;
                String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                String text = new String(payload, languageCodeLength + 1,
                        payload.length - languageCodeLength - 1, textEncoding);

                //{"amount":"11.5", "coinType":"DCT", "token":"SPC","deviceIDhash":"04793D374185C2167A420D250FFF93F05156350C"}

                Log.i(TAG, text);

                if (text.contains("{") && text.contains("}")) {
                    handlePaymentInfo(text);
                    return;
                }
            }

        } catch (Exception e) {
            //Log.e(TAG, e.getLocalizedMessage());
        } finally {
            try {
                ndef.close();
            } catch (Exception e) {
                Log.e(TAG, "close ndef failed! " + e.getLocalizedMessage());
            }
        }

    }

    public void onScanQRCodeButtonClick(View v)
    {
        mScanIntegrator = new IntentIntegrator(PaymentMainActivity.this);
        mScanIntegrator.setPrompt("Start scan ...");
        mScanIntegrator.setTimeout(30000);
        mScanIntegrator.setCaptureActivity(ScanQRCodeActivity.class);
        mScanIntegrator.initiateScan();
    }

    public void onHistoryButtonClick(View v){
        Intent newIntent = new Intent(mContext, PaymentHistoryActivity.class);
        startActivity(newIntent);
    }

    public void handlePaymentInfo(final String payinfo){

        new Thread(new Runnable() {
            @Override
            public void run() {

                Pair<Integer, String> ret = mPaymentManager.getDeviceInfo(payinfo);
                if (ret.first== SecuXServerRequestHandler.SecuXRequestUnauthorized){
                    showMessageInMain("Login timeout! Please login again!");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent newIntent = new Intent(mContext, LoginActivity.class);
                            startActivity(newIntent);
                        }
                    });

                    return;

                }else if (ret.first==SecuXServerRequestHandler.SecuXRequestFailed){
                    showMessageInMain("Invalid payment information!");

                    if (ret.second.contains("no token")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent newIntent = new Intent(mContext, LoginActivity.class);
                                startActivity(newIntent);
                            }
                        });
                    }

                    return;
                }

                try{
                    String amount = "0", coinType = "DCT", token = "SPC";
                    final String payInfoReply = ret.second;
                    final boolean showAccountSelection;

                    JSONObject payinfoJson = new JSONObject(payInfoReply);
                    if (payinfoJson.has("amount")) {
                        amount = payinfoJson.getString("amount");
                    }

                    if (payinfoJson.has("coinType")) {
                        coinType = payinfoJson.getString("coinType");
                        showAccountSelection = false;
                    }else{
                        showAccountSelection = true;
                    }

                    if (payinfoJson.has("token")) {
                        token = payinfoJson.getString("token");
                    }

                    final String devID = payinfoJson.getString("deviceID");
                    final String devIDHash = payinfoJson.getString("deviceIDhash");

                    SecuXCoinAccount coinAcc = Setting.getInstance().mAccount.getCoinAccount(coinType);

                    if (coinAcc == null){
                        showMessageInMain("Unsupported Coin Type!");
                        return;
                    }else if (coinAcc.getBalance(token)==null){
                        showMessageInMain("Unsupported Token Type!");
                        return;
                    }

                    final String payAmount = amount;
                    final String payCoinType = coinType;
                    final String payToken = token;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getApplicationContext(),"Scan result: "+scanContent, Toast.LENGTH_LONG).show();
                            Intent newIntent = new Intent(mContext, PaymentDetailsActivity.class);
                            newIntent.putExtra(PAYMENT_INFO, payInfoReply);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_AMOUNT, payAmount);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_COINTYPE, payCoinType);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_TOKEN, payToken);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_DEVID, devID);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_DEVIDHASH, devIDHash);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_SHOWACCOUNTSEL, showAccountSelection);
                            startActivity(newIntent);
                        }
                    });
                }catch (Exception e){
                    showMessageInMain("Invalid payment information!");
                    return;
                }


            }
        }).start();


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
                handlePaymentInfo(scanContent);
                return;
            }


                /*
                String amount;
                @SecuXCoinType.CoinType String coinType;
                try{
                    JSONObject payinfoJson = new JSONObject(scanContent);
                    amount = payinfoJson.getString("amount");
                    coinType = payinfoJson.getString("coinType");
                    String devid = payinfoJson.getString("deviceID");

                    if (Wallet.getInstance().getAccount(coinType) == null){
                        Toast toast = Toast.makeText(mContext, "Unsupported Coin Type!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        return;
                    }

                }catch (Exception e){
                    Toast toast = Toast.makeText(mContext, "Invalid QRCode!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }


                //Toast.makeText(getApplicationContext(),"Scan result: "+scanContent, Toast.LENGTH_LONG).show();
                Intent newIntent = new Intent(this, PaymentDetailsActivity.class);
                newIntent.putExtra(PAYMENT_INFO, scanContent);
                newIntent.putExtra(PaymentDetailsActivity.PAYMENT_AMOUNT, amount);
                newIntent.putExtra(PaymentDetailsActivity.PAYMENT_COINTYPE, coinType);
                startActivity(newIntent);
                return;

                 */

                /*
                String amountStr = "10 IFC";

                Intent newIntent = new Intent(mContext, PaymentResultActivity.class);
                newIntent.putExtra(PaymentDetailsActivity.PAYMENT_RESULT, false);
                newIntent.putExtra(PaymentDetailsActivity.PAYMENT_STORENAME, "My test Store");
                newIntent.putExtra(PaymentDetailsActivity.PAYMENT_AMOUNT, amountStr);
                startActivity(newIntent);

                 */


        }

        super.onActivityResult(requestCode, resultCode, intent);
        Toast.makeText(getApplicationContext(),"Scan failed!!",Toast.LENGTH_LONG).show();

    }
}
