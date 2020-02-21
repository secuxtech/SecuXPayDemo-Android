package com.secuxtech.mysecuxpay.Model;

import com.secuxtech.paymentkit.SecuXUserAccount;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-20
 */
public class Setting {
    private static final Setting ourInstance = new Setting();

    public static Setting getInstance() {
        return ourInstance;
    }

    public SecuXUserAccount mAccount = null;

    private Setting() {
    }
}