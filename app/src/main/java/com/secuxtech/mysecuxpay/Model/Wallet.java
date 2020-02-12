package com.secuxtech.mysecuxpay.Model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.secuxtech.paymentkit.SecuXAccount;
import com.secuxtech.paymentkit.SecuXAccountBalance;
import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXCoinType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class Wallet {
    private static final Wallet ourInstance = new Wallet();

    public static Wallet getInstance() {
        return ourInstance;
    }

    private Wallet() {
        loadAccounts();
    }

    private ArrayList<Account> mAccountList = new ArrayList<>();
    private SecuXAccountManager mAccountManager = new SecuXAccountManager();
    Map<String, Double> mCoinRate = null;

    public void loadAccounts(){
        mAccountList.clear();
        Account accountDCT = new Account("ifun-886-936105934-6", SecuXCoinType.DCT, "", "", "", 0.0, 0.0);
        //Account accountIFC = new Account("ifun-886-900-112233-44", SecuXCoinType.IFC, "", "", "", 0.0, 0.0);

        mAccountList.add(accountDCT);

    }

    public ArrayList<Account> getAccounts(){
        return mAccountList;
    }

    public Account getAccount(@SecuXCoinType.CoinType String type){
        for (Account account:
             mAccountList) {

            if (account.mCoinType.compareTo(type)==0){
                return account;
            }
        }
        return null;
    }

    public void getCoinToUsdRate(){
        mCoinRate = mAccountManager.getCoinUSDRate();
    }

    public void getAccountBalance(Account account){

        SecuXAccountBalance balance = new SecuXAccountBalance();
        if (mAccountManager.getAccountBalance(account, balance)){
            Log.i("secux-paymentkit-exp",
                    "getAccountBalance done. balance= " + String.valueOf(balance.mFormatedBalance) + ", usdBalance=" + String.valueOf(balance.mUSDBalance));

            account.mBalance = balance.mFormatedBalance;
            account.mUsdBanance = balance.mUSDBalance;

            if (balance.mUSDBalance==0 && mCoinRate!=null && mCoinRate.containsKey(account.mCoinType)){
                account.mUsdBanance = balance.mFormatedBalance * mCoinRate.get(account.mCoinType);
            }

        }else{
            Log.i("secux-paymentkit-exp", "get account balance failed!");
        }

    }
}
