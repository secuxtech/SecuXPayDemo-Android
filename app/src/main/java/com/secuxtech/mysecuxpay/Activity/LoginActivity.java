package com.secuxtech.mysecuxpay.Activity;

import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccountManager;

import com.secuxtech.paymentkit.SecuXUserAccount;

public class LoginActivity extends BaseActivity {

    private SecuXAccountManager mAccountManager = new SecuXAccountManager();
    private EditText mEdittextEmail;
    private EditText mEdittextPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mShowBackButton = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEdittextEmail = findViewById(R.id.editText_lgoin_email);
        mEdittextEmail.setOnFocusChangeListener(mViewFocusChangeListener);
        mEdittextEmail.setOnEditorActionListener(mTextviewEditorListener);
        mEdittextEmail.addTextChangedListener(mTextWatcher);

        mEdittextPwd = findViewById(R.id.editText_lgoin_password);
        mEdittextPwd.setOnFocusChangeListener(mViewFocusChangeListener);
        mEdittextPwd.setOnEditorActionListener(mTextviewEditorListener);
        mEdittextPwd.addTextChangedListener(mTextWatcher);
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

                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.
        }
    };

    public void onSignupButtonClick(View v){
        Intent newIntent = new Intent(mContext, RegistryActivity.class);
        startActivity(newIntent);
    }

    public void onLoginButtonClick(View v)
    {
        String email = mEdittextEmail.getText().toString();
        String pwd = mEdittextPwd.getText().toString();
        Setting.getInstance().mAccount = new SecuXUserAccount("maochuntest6@secuxtech.com", "0975123456", "12345678");
        //Setting.getInstance().mAccount = new SecuXUserAccount(email, "", pwd);

        CommonProgressDialog.showProgressDialog(mContext, "Login...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Pair<Boolean, String> ret = mAccountManager.loginUserAccount(Setting.getInstance().mAccount);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonProgressDialog.dismiss();
                        if (ret.first) {
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
}
