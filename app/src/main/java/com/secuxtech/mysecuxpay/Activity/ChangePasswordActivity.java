package com.secuxtech.mysecuxpay.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;

public class ChangePasswordActivity extends BaseActivity {

    private EditText mEdittextOldPwd;
    private EditText mEdittextPwd;
    private EditText mEdittextConfirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mEdittextOldPwd = findViewById(R.id.editText_changepwd_old);
        mEdittextPwd = findViewById(R.id.editText_changepwd_new);
        mEdittextConfirmPwd = findViewById(R.id.editText_changepwd_confirmnew);
    }

    public void onChangeButtonClick(View v){
        if (mEdittextOldPwd.getText().length()<6){
            Toast toast = Toast.makeText(this, "Invalid old password!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }

        if (mEdittextPwd.getText().length()<6){
            Toast toast = Toast.makeText(this, "Invalid new password length! Password must have 6~18 characteristics.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }

        if (mEdittextPwd.getText().toString().compareTo(mEdittextConfirmPwd.getText().toString())!=0){
            Toast toast = Toast.makeText(this, "New password DOES NOT match!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }

        CommonProgressDialog.showProgressDialog(this, "In Progress ...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SecuXAccountManager accMgr = new SecuXAccountManager();
                final Pair<Integer, String> ret = accMgr.changePassword(mEdittextOldPwd.getText().toString(), mEdittextPwd.getText().toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonProgressDialog.dismiss();

                        if (ret.first == SecuXServerRequestHandler.SecuXRequestOK){
                            Toast toast = Toast.makeText(mContext, "Password changed!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            Setting.getInstance().mAccount.mPassword = mEdittextPwd.getText().toString();
                            finish();
                        }else{
                            Toast toast = Toast.makeText(mContext, "Change password failed!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    }
                });


            }
        }).start();
    }


}
