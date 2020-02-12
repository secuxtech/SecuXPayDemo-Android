package com.secuxtech.mysecuxpay.Model;

import com.secuxtech.paymentkit.SecuXAccount;

public class PaymentHistoryModel {
    public String   mStoreName = "N/A";
    public Account  mAccount = null;
    public String   mDate = "";
    public String   mUsbBalance = "";
    public String   mBalance = "";

    public PaymentHistoryModel(Account account, String storename, String date, String usdBalance, String balance){
        mAccount = account;
        mStoreName = storename;
        mDate = date;
        mUsbBalance = usdBalance;
        mBalance = balance;
    }
}
