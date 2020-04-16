package com.secuxtech.mysecuxpay.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.secuxtech.mysecuxpay.Activity.CoinAccountListActivity;
import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;
import com.secuxtech.paymentkit.SecuXUserAccount;


/**
 * Created by maochuns.sun@gmail.com on 2020/3/24
 */
public class BaseFragment  extends Fragment {

    protected SecuXAccountManager mAccountManager = new SecuXAccountManager();

    protected boolean checkWifi(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void showMessageInMain(final String msg){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void login(final SecuXUserAccount account){
        CommonProgressDialog.showProgressDialog(getActivity(), "Login...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Pair<Integer, String> ret = mAccountManager.loginUserAccount(account);
                if (ret.first!= SecuXServerRequestHandler.SecuXRequestOK){
                    showMessageInMain("Login failed! Invalid email account or password");
                }

                ret = mAccountManager.getCoinAccountList(account);
                if (ret.first!= SecuXServerRequestHandler.SecuXRequestOK){
                    showMessageInMain("Login failed! Get coin token account list failed!");
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonProgressDialog.dismiss();

                        Setting.getInstance().mUserLogout = false;
                        Setting.getInstance().mAccount = account;
                        Setting.getInstance().mUserAccountName = account.mAccountName;
                        Setting.getInstance().mUserAccountPwd = account.mPassword;
                        Setting.getInstance().saveSettings(getActivity());
                        Intent newIntent = new Intent(getActivity(), CoinAccountListActivity.class);
                        startActivity(newIntent);

                    }
                });

            }
        }).start();
    }
}
