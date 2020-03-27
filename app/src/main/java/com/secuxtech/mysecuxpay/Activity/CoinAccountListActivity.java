package com.secuxtech.mysecuxpay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.secuxtech.mysecuxpay.Adapter.CoinAccountListAdapter;
import com.secuxtech.mysecuxpay.Adapter.HistoryListAdapter;
import com.secuxtech.mysecuxpay.Adapter.TokenTransHistoryAdapter;
import com.secuxtech.mysecuxpay.Interface.AdapterItemClickListener;
import com.secuxtech.mysecuxpay.Model.CoinTokenAccount;
import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.AccountUtil;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXCoinAccount;
import com.secuxtech.paymentkit.SecuXCoinTokenBalance;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class CoinAccountListActivity extends BaseActivity {

    SecuXAccountManager mAccountManager = new SecuXAccountManager();
    CoinAccountListAdapter mAdapter;
    ArrayList<CoinTokenAccount> mTokenAccountArray = new ArrayList<>();

    private AdapterItemClickListener mItemClickListener = new AdapterItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            CoinTokenAccount account = mTokenAccountArray.get(position);
            Log.i(TAG, account.mAccountName + " " + account.mCoinType + " " + account.mToken);

            /*
            Intent newIntent = new Intent(mContext, TokenTransHistoryActivity.class);
            newIntent.putExtra(TokenTransHistoryActivity.TRANSACTION_HISTORY_COINTYPE, account.mCoinType);
            newIntent.putExtra(TokenTransHistoryActivity.TRANSACTION_HISTORY_TOKEN, account.mToken);
            startActivity(newIntent);

             */
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mShowBackButton = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_account_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_acclist_accounts);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        CommonProgressDialog.showProgressDialog(mContext, "Loading...");
        loadAccounts();

        BottomNavigationView navigationView = findViewById(R.id.navigation_main);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void loadAccounts(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAccountManager.getAccountBalance(Setting.getInstance().mAccount, "DCT", "SPC");

                mTokenAccountArray = AccountUtil.getCoinTokenAccounts();

                CommonProgressDialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_acclist_accounts);
                        mAdapter = new CoinAccountListAdapter(mContext, mTokenAccountArray, mItemClickListener);
                        recyclerView.setAdapter(mAdapter);
                    }
                });

            }
        }).start();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_main_accounts:
                    return true;

                case R.id.navigation_main_payment:
                    Intent newIntent = new Intent(mContext, PaymentMainActivity.class);
                    startActivity(newIntent);
                    return true;

                case R.id.navigation_main_userinfo:
                    Intent userInfoIntent = new Intent(mContext, UserInfoActivity.class);
                    startActivity(userInfoIntent);
                    return true;
            }
            return false;
        }

    };
}
