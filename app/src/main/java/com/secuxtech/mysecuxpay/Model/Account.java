package com.secuxtech.mysecuxpay.Model;

import androidx.annotation.DrawableRes;

import com.secuxtech.mysecuxpay.R;
import com.secuxtech.paymentkit.SecuXAccount;
import com.secuxtech.paymentkit.SecuXCoinType;

public class Account extends SecuXAccount {

    public Double mBalance;
    public Double mUsdBanance;

    public Account(String name, @SecuXCoinType.CoinType String type, String path,
                   String address, String key, Double balance, Double usdBalance){
        super(name, type, path, address, key);

        this.mName = name;
        this.mCoinType = type;
        this.mPath = path;
        this.mAddress = address;
        this.mKey = key;
        this.mBalance = balance;
        this.mUsdBanance = usdBalance;
    }

    public @DrawableRes int GetCoinLogo(){
        switch (mCoinType){
            case SecuXCoinType.DCT:
                return R.drawable.dct;
            case SecuXCoinType.LBR:
                return R.drawable.lbr;

            default:
                return R.drawable.dct;
        }
    }
}
