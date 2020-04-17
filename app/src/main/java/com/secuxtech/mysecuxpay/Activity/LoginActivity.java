package com.secuxtech.mysecuxpay.Activity;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


//import com.an.biometric.BiometricCallback;
//import com.an.biometric.BiometricManager;
import com.secuxtech.mysecuxpay.Fragment.LoginFragment;
import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccountManager;

import com.secuxtech.paymentkit.SecuXServerRequestHandler;
import com.secuxtech.paymentkit.SecuXUserAccount;

public class LoginActivity extends BaseActivity {

    //private SecuXAccountManager mAccountManager = new SecuXAccountManager();
    //private EditText mEdittextEmail;
    //private EditText mEdittextPwd;

    private Fragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mShowBackButton = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = this.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        /*
        mEdittextEmail = findViewById(R.id.editText_lgoin_email);
        mEdittextEmail.setOnFocusChangeListener(mViewFocusChangeListener);
        mEdittextEmail.setOnEditorActionListener(mTextviewEditorListener);
        mEdittextEmail.addTextChangedListener(mTextWatcher);

        mEdittextPwd = findViewById(R.id.editText_lgoin_password);
        mEdittextPwd.setOnFocusChangeListener(mViewFocusChangeListener);
        mEdittextPwd.setOnEditorActionListener(mTextviewEditorListener);
        mEdittextPwd.addTextChangedListener(mTextWatcher);

         */

        FragmentManager fm = getSupportFragmentManager();
        mLoginFragment = fm.findFragmentByTag("fragment_login");
        if (mLoginFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            mLoginFragment =new LoginFragment();
            //ft.add(android.R.id.content,mLoginFragment,"fragment_login");
            ft.add(R.id.llayout_login,mLoginFragment,"fragment_login");
            ft.commit();
        }
    }

    /*
    @Override
    protected void onResume(){
        super.onResume();

        if (Setting.getInstance().mAccount!=null){
            new BiometricManager.BiometricBuilder(this)
                    .setTitle("Login")
                    .setSubtitle("MySecuXPay")
                    .setDescription("Auto login with your biometric ID")
                    .setNegativeButtonText("Cancel")
                    .build()
                    .authenticate(mBiometricCallback);
        }
    }

    private View.OnFocusChangeListener mViewFocusChangeListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (!hasFocus) {
                hideKeyboard(v);
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.i(TAG, "beforeTextChanged");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.i(TAG, "afterTextChanged");


        }
    };

    private TextView.OnEditorActionListener mTextviewEditorListener = new EditText.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.i(TAG, "onEditorAction " + String.valueOf(actionId));
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i(TAG, "Edit done");
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.
        }
    };


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
            mEdittextEmail.setText(Setting.getInstance().mAccount.mAccountName);
            mEdittextPwd.setText(Setting.getInstance().mAccount.mPassword);
            onLoginButtonClick(null);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {

        }
    };

    public void onLoginButtonClick(View v)
    {
        //Intent newIntent = new Intent(mContext, MainActivity.class);
        //startActivity(newIntent);


        String email = mEdittextEmail.getText().toString();
        String pwd = mEdittextPwd.getText().toString();
        Setting.getInstance().mAccount = new SecuXUserAccount("maochuntest6@secuxtech.com", "0975123456", "12345678");
        //Setting.getInstance().mAccount = new SecuXUserAccount(email, "", pwd);

        CommonProgressDialog.showProgressDialog(mContext, "Login...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Pair<Integer, String> ret = mAccountManager.loginUserAccount(Setting.getInstance().mAccount);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonProgressDialog.dismiss();
                        if (ret.first== SecuXServerRequestHandler.SecuXRequestOK) {
                            Intent newIntent = new Intent(mContext, CoinAccountListActivity.class);
                            startActivity(newIntent);

                        }else{
                            showMessageInMain("Login failed! Error: " + ret.second);
                        }
                    }
                });

            }
        }).start();


    }

     */
}
