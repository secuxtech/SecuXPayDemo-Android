package com.secuxtech.mysecuxpay.Activity;



import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;

import android.widget.Toast;
import android.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.secuxtech.mysecuxpay.Model.PaymentHistoryModel;
import com.secuxtech.mysecuxpay.Model.Wallet;
import com.secuxtech.mysecuxpay.R;

import com.google.zxing.integration.android.IntentResult;
import com.secuxtech.paymentkit.SecuXCoinType;

import org.json.JSONObject;

public class MainActivity extends BaseActivity {

    private final Context mContext = this;
    private IntentIntegrator scanIntegrator;

    public static final String PAYMENT_INFO = "com.secux.MySecuXPay.PAYMENTINFO";

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

    }



    public void onScanQRCodeButtonClick(View v)
    {
        View button1 = (View) findViewById(R.id.scan_qrcode_button);

        scanIntegrator = new IntentIntegrator(MainActivity.this);
        scanIntegrator.setPrompt("Start scan ...");
        scanIntegrator.setTimeout(300000);
        scanIntegrator.initiateScan();
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

                for (int i=0; i<20; i++){
                    PaymentHistoryModel payment = new PaymentHistoryModel(Wallet.getInstance().getAccount(SecuXCoinType.IFC), "Store ttt", "2020-12-12 11:12:11", String.format("%.2f", 5.68), "234");
                    Wallet.getInstance().addPaymentHistoryItem(payment);
                }


                Intent ttIntent = new Intent(mContext, PaymentHistoryActivity.class);
                startActivity(ttIntent);
                return;

/*
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

        }

        super.onActivityResult(requestCode, resultCode, intent);
        Toast.makeText(getApplicationContext(),"Scan failed!!",Toast.LENGTH_LONG).show();

    }


}
