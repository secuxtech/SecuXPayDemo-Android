package com.secuxtech.mysecuxpay.Fragment;


import android.app.Activity;
import android.content.Intent;
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

import com.secuxtech.mysecuxpay.Activity.CoinAccountListActivity;
import com.secuxtech.mysecuxpay.Activity.MainActivity;
import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;
import com.secuxtech.paymentkit.SecuXUserAccount;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private SecuXAccountManager mAccountManager = new SecuXAccountManager();
    private EditText mEdittextEmail;
    private EditText mEdittextPwd;
    private TextView mTextViewInvalidEmail;
    private TextView mTextViewInvalidPwd;

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

        Button loginBtn = view.findViewById(R.id.button_login);
        loginBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClick(v);
            }
        });

        return view;
    }

    private View.OnFocusChangeListener mViewFocusChangeListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (!hasFocus) {
                ((MainActivity)getActivity()).hideKeyboard(v);
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
                    checkInput(v);
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.
        }
    };

    private void checkInput(View v){
        if (v == mEdittextPwd){
            String pwd = mEdittextPwd.getText().toString();
            if (pwd.length()==0){
                mTextViewInvalidPwd.setVisibility(View.VISIBLE);
            }else{
                mTextViewInvalidPwd.setVisibility(View.INVISIBLE);
            }
        }else if (v==mEdittextEmail){
            String email = mEdittextEmail.getText().toString();
            if (email.length()==0 || !email.contains("@") || !email.contains(".") ||
                    email.indexOf('@')==email.length()-1 || email.indexOf('.')==email.length()-1 ||
                    email.indexOf('@')==0 || email.indexOf('.')==0){
                mTextViewInvalidEmail.setVisibility(View.VISIBLE);
            }else{
                mTextViewInvalidEmail.setVisibility(View.INVISIBLE);
            }
        }

    }



    public void onLoginButtonClick(View v)
    {
        //Intent newIntent = new Intent(mContext, MainActivity.class);
        //startActivity(newIntent);

        ((MainActivity)getActivity()).hideKeyboard(v);

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
        final SecuXUserAccount account = new SecuXUserAccount(email, "", pwd);

        CommonProgressDialog.showProgressDialog(getActivity(), "Login...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Pair<Integer, String> ret = mAccountManager.loginUserAccount(account);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonProgressDialog.dismiss();
                        if (ret.first== SecuXServerRequestHandler.SecuXRequestOK) {
                            Setting.getInstance().mAccount = account;
                            Intent newIntent = new Intent(getActivity(), CoinAccountListActivity.class);
                            startActivity(newIntent);
                        }else{
                            Toast toast = Toast.makeText(getActivity(), "Login failed! Invalid email account or password", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    }
                });

            }
        }).start();


    }

}
