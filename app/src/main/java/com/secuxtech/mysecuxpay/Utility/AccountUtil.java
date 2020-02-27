package com.secuxtech.mysecuxpay.Utility;

import androidx.annotation.DrawableRes;

import com.secuxtech.mysecuxpay.Model.CoinTokenAccount;
import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.paymentkit.SecuXCoinAccount;
import com.secuxtech.paymentkit.SecuXCoinTokenBalance;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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

    static public ArrayList<CoinTokenAccount> getCoinTokenAccounts(){
        ArrayList<CoinTokenAccount> accountArr = new ArrayList<>();
        for(int i = 0; i< Setting.getInstance().mAccount.mCoinAccountArr.size(); i++){
            SecuXCoinAccount coinAccount = Setting.getInstance().mAccount.mCoinAccountArr.get(i);

            Set<Map.Entry<String, SecuXCoinTokenBalance>> entrySet = coinAccount.mTokenBalanceMap.entrySet();
            for (Map.Entry<String, SecuXCoinTokenBalance> entry: entrySet){
                String token = entry.getKey();
                CoinTokenAccount tokenAccount = new CoinTokenAccount(coinAccount, token);
                accountArr.add(tokenAccount);
            }
        }
        return accountArr;
    }
}
