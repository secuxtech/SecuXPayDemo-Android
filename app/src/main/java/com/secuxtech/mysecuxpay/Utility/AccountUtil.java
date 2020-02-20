package com.secuxtech.mysecuxpay.Utility;

import androidx.annotation.DrawableRes;

import com.secuxtech.mysecuxpay.R;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-20
 */
public class AccountUtil {

    static public @DrawableRes
    int getCoinLogo(String coinType){
        switch (coinType){
            case "DCT":
                return R.drawable.dct;
            case "LBR":
                return R.drawable.lbr;

            default:
                return R.drawable.dct;
        }
    }
}
