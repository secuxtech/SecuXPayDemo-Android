package com.secuxtech.mysecuxpay.Model;

import com.secuxtech.paymentkit.SecuXCoinAccount;
import com.secuxtech.paymentkit.SecuXCoinTokenBalance;
import com.secuxtech.paymentkit.SecuXPaymentHistory;
import com.secuxtech.paymentkit.SecuXUserAccount;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-20
 */
public class Setting {
    private static final Setting ourInstance = new Setting();

    public static Setting getInstance() {
        return ourInstance;
    }

    public SecuXUserAccount mAccount = null;
    public SecuXPaymentHistory mLastPaymentHis = null;

    public boolean mTestModel = false;

    private Setting() {
    }


}
