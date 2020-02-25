package com.secuxtech.mysecuxpay.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;

import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;
import com.secuxtech.paymentkit.SecuXUserAccount;

public class RegistryActivity extends BaseActivity {

    SecuXAccountManager mAccountManager = new SecuXAccountManager();

    private EditText mEditTextEmail;
    private EditText mEditTextPhone;
    private EditText mEditTextPassword;
    private EditText mEditTextPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mShowBackButton = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);

        mEditTextEmail = findViewById(R.id.editText_register_email);
        mEditTextPhone = findViewById(R.id.editText_register_phone);
        mEditTextPassword = findViewById(R.id.editText_register_password);
        mEditTextPasswordConfirm = findViewById(R.id.editText_register_confirmpassword);

        mEditTextEmail.setOnFocusChangeListener(mViewFocusChangeListener);
        mEditTextPhone.setOnFocusChangeListener(mViewFocusChangeListener);
        mEditTextPassword.setOnFocusChangeListener(mViewFocusChangeListener);
        mEditTextPasswordConfirm.setOnFocusChangeListener(mViewFocusChangeListener);
    }

    public void onSigninButtonClick(View v){
        Intent newIntent = new Intent(mContext, LoginActivity.class);
        startActivity(newIntent);
    }

    private View.OnFocusChangeListener mViewFocusChangeListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (!hasFocus) {
                hideKeyboard(v);
            }
        }
    };

    public void onReigsterButtonClick(View v){
        CommonProgressDialog.showProgressDialog(mContext, "Register...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //SecuXUserAccount account = new SecuXUserAccount("maochuntest6@secuxtech.com", "0975123456", "12345678");
                String email = mEditTextEmail.getText().toString();
                String phone = mEditTextPhone.getText().toString();
                String password = mEditTextPassword.getText().toString();

                SecuXUserAccount account = new SecuXUserAccount(email, phone, password);
                Pair<Integer, String> ret = mAccountManager.registerUserAccount(account);
                CommonProgressDialog.dismiss();
                if (ret.first== SecuXServerRequestHandler.SecuXRequestOK) {
                    Intent newIntent = new Intent(mContext, MainActivity.class);
                    startActivity(newIntent);
                }else {
                    showMessageInMain("registration failed! Error: " + ret.second);
                }
            }
        }).start();
    }
}
