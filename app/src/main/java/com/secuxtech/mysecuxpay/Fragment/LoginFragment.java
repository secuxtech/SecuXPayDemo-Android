package com.secuxtech.mysecuxpay.Fragment;


import android.annotation.TargetApi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.an.biometric.BiometricCallback;
import com.an.biometric.BiometricManager;
import com.secuxtech.mysecuxpay.Activity.CoinAccountListActivity;

import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;
import com.secuxtech.paymentkit.SecuXUserAccount;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment {

    private SecuXAccountManager mAccountManager = new SecuXAccountManager();
    private EditText mEdittextEmail;
    private EditText mEdittextPwd;
    private TextView mTextViewInvalidEmail;
    private TextView mTextViewInvalidPwd;
    private Button mButtonLogin;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mEdittextEmail = view.findViewById(R.id.editText_lgoin_email);
        mEdittextEmail.setOnFocusChangeListener(mViewFocusChangeListener);
        mEdittextEmail.setOnEditorActionListener(mTextviewEditorListener);
        //mEdittextEmail.addTextChangedListener(mTextWatcher);

        mEdittextPwd = view.findViewById(R.id.editText_lgoin_password);
        mEdittextPwd.setOnFocusChangeListener(mViewFocusChangeListener);
        mEdittextPwd.setOnEditorActionListener(mTextviewEditorListener);
        //mEdittextPwd.addTextChangedListener(mTextWatcher);

        mTextViewInvalidEmail = view.findViewById(R.id.textView_login_invalid_email);
        mTextViewInvalidPwd = view.findViewById(R.id.textView_login_invalid_password);
        mButtonLogin = view.findViewById(R.id.button_login);

        Button loginBtn = view.findViewById(R.id.button_login);
        loginBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClick(v);
            }
        });

        if (Setting.getInstance().mAccount!=null){
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new BiometricManager.BiometricBuilder(getActivity())
                                .setTitle("Login")
                                .setSubtitle("MySecuXPay")
                                .setDescription("Auto login with your biometric ID")
                                .setNegativeButtonText("Cancel")
                                .build()
                                .authenticate(mBiometricCallback);
                    }
                });
            }catch (Exception e){

            }
        }

        return view;
    }

    private View.OnFocusChangeListener mViewFocusChangeListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (!hasFocus) {
                hideKeyboard(v);
                checkInput(v);
            }
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


                    if (checkInput(v) && v==mEdittextPwd){
                        onLoginButtonClick(v);
                    }
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.
        }
    };

    private boolean checkInput(View v){
        if (v == mEdittextPwd){
            String pwd = mEdittextPwd.getText().toString();
            if (pwd.length()==0){
                mTextViewInvalidPwd.setVisibility(View.VISIBLE);
            }else{
                mTextViewInvalidPwd.setVisibility(View.INVISIBLE);
                return true;
            }
        }else if (v==mEdittextEmail){
            String email = mEdittextEmail.getText().toString();
            if (email.length()==0 || !email.contains("@") || !email.contains(".") ||
                    email.indexOf('@')==email.length()-1 || email.indexOf('.')==email.length()-1 ||
                    email.indexOf('@')==0 || email.indexOf('.')==0){
                mTextViewInvalidEmail.setVisibility(View.VISIBLE);
            }else{
                mTextViewInvalidEmail.setVisibility(View.INVISIBLE);
                return true;
            }
        }
        return false;
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
            mEdittextEmail.setText(Setting.getInstance().mAccount.mAccountName);
            mEdittextPwd.setText(Setting.getInstance().mAccount.mPassword);
            onLoginButtonClick(mButtonLogin);
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

        hideKeyboard(v);

        if (!checkWifi()){
            return;
        }

        checkInput(mEdittextEmail);
        checkInput(mEdittextPwd);

        if (mTextViewInvalidEmail.getVisibility()==View.VISIBLE || mTextViewInvalidPwd.getVisibility()==View.VISIBLE){
            Toast toast = Toast.makeText(getActivity(), "Invalid email account or password!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }

        String email = mEdittextEmail.getText().toString();
        String pwd = mEdittextPwd.getText().toString();

        //Setting.getInstance().mAccount = new SecuXUserAccount("maochuntest6@secuxtech.com", "0975123456", "12345678");
        final SecuXUserAccount account = new SecuXUserAccount(email, pwd);

        CommonProgressDialog.showProgressDialog(getActivity(), "Login...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Pair<Integer, String> ret = mAccountManager.loginUserAccount(account);
                if (ret.first!= SecuXServerRequestHandler.SecuXRequestOK){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonProgressDialog.dismiss();
                            Toast toast = Toast.makeText(getActivity(), "Login failed! Invalid email account or password", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    });

                }

                ret = mAccountManager.getCoinAccountList(account);
                if (ret.first!= SecuXServerRequestHandler.SecuXRequestOK){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonProgressDialog.dismiss();
                            Toast toast = Toast.makeText(getActivity(), "Login failed! Get coin token account list failed!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    });
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonProgressDialog.dismiss();

                        Setting.getInstance().mAccount = account;
                        Intent newIntent = new Intent(getActivity(), CoinAccountListActivity.class);
                        startActivity(newIntent);

                    }
                });

            }
        }).start();


    }



}
