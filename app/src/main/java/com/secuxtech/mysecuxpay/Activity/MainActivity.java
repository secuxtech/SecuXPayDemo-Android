package com.secuxtech.mysecuxpay.Activity;



import android.Manifest;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;

import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.secuxtech.mysecuxpay.Model.PaymentHistoryModel;
import com.secuxtech.mysecuxpay.Model.Wallet;
import com.secuxtech.mysecuxpay.R;

import com.google.zxing.integration.android.IntentResult;
import com.secuxtech.paymentkit.SecuXCoinType;

import org.json.JSONObject;


public class MainActivity extends BaseActivity {

    private final Context       mContext = this;
    private IntentIntegrator    mScanIntegrator;

    private NfcAdapter          mNfcAdapter;
    private PendingIntent       mPendingIntent = null;

    public static final String PAYMENT_INFO = "com.secux.MySecuXPay.PAYMENTINFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mShowBackButton = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

            String amount="", devid="", cointype="";
            for (final NdefRecord record : messages.getRecords()) {
                byte[] payload = record.getPayload();
                String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                int languageCodeLength = payload[0] & 0077;
                String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                String text = new String(payload, languageCodeLength + 1,
                        payload.length - languageCodeLength - 1, textEncoding);

                if (text.contains("amount:")){
                    amount = text.substring(text.indexOf(':')+1);
                }else if (text.contains("DevID:")){
                    devid = text.substring(text.indexOf(':')+1);
                }else if (text.contains("CoinType:")){
                    cointype = text.substring(text.indexOf(':')+1);
                }

                Log.i(TAG, text);
            }

            if (amount.length()>0 && devid.length()>0 && cointype.length()>0){
                JSONObject payinfoJson = new JSONObject();
                payinfoJson.put("amount", amount);
                payinfoJson.put("deviceID", devid);
                payinfoJson.put("coinType", cointype);

                handlePaymentInfoJson(payinfoJson);
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
        mScanIntegrator = new IntentIntegrator(MainActivity.this);
        mScanIntegrator.setPrompt("Start scan ...");
        mScanIntegrator.setTimeout(30000);
        mScanIntegrator.initiateScan();
    }

    public void onHistoryButtonClick(View v){
        Intent newIntent = new Intent(mContext, PaymentHistoryActivity.class);
        startActivity(newIntent);
    }

    public void handlePaymentInfoJson(JSONObject payinfoJson){
        String amount;
        @SecuXCoinType.CoinType String coinType;
        try{
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
        newIntent.putExtra(PAYMENT_INFO, payinfoJson.toString());
        newIntent.putExtra(PaymentDetailsActivity.PAYMENT_AMOUNT, amount);
        newIntent.putExtra(PaymentDetailsActivity.PAYMENT_COINTYPE, coinType);
        startActivity(newIntent);
        return;

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
                try{
                    JSONObject payinfoJson = new JSONObject(scanContent);
                    handlePaymentInfoJson(payinfoJson);

                    return;
                }catch (Exception e) {
                }
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
