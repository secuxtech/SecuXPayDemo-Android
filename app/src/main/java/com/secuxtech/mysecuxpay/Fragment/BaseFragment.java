package com.secuxtech.mysecuxpay.Fragment;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


/**
 * Created by maochuns.sun@gmail.com on 2020/3/24
 */
public class BaseFragment  extends Fragment {

    protected boolean checkWifi(){
        /*
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Activity.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //SupplicantState supState = wifiInfo.getSupplicantState();
        if (wifiInfo.getNetworkId() == -1){
            this.showMessageInMain("No internet! Please check the Wifi");
            return false;
        }
        */
        return true;
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
}
