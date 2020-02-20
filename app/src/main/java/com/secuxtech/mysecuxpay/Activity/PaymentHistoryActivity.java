package com.secuxtech.mysecuxpay.Activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.secuxtech.mysecuxpay.Adapter.HistoryListAdapter;

import com.secuxtech.mysecuxpay.Interface.OnListScrollListener;
import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.paymentkit.SecuXPaymentHistory;
import com.secuxtech.paymentkit.SecuXPaymentManager;

import java.util.ArrayList;

public class PaymentHistoryActivity extends BaseActivity {

    HistoryListAdapter mAdapter;
    SecuXPaymentManager mPaymentManager = new SecuXPaymentManager();
    ArrayList<SecuXPaymentHistory> payHisArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_history_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));


        new Thread(new Runnable() {
            @Override
            public void run() {

                Pair<Boolean, String> ret = mPaymentManager.getPaymentHistory(Setting.getInstance().mAccount, "SPC", 1, 20, payHisArr);
                if (!ret.first){
                    showMessageInMain("Get payment history failed! Error: " + ret.second);

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (payHisArr.size()>0){
                            mAdapter = new HistoryListAdapter(mContext, payHisArr);
                            recyclerView.setAdapter(mAdapter);

                            mAdapter.setOnListScrollListener(new OnListScrollListener(){
                                @Override
                                public void onBottomReached(int position){
                                    Log.i(TAG, "onBottomReached");
                                }

                                @Override
                                public void onTopReached(int position){
                                    Log.i(TAG, "onTopReached");
                                }
                            });

                        }else{
                            TextView textviewNoHistory = findViewById(R.id.textView_nohistory);
                            textviewNoHistory.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        }).start();







    }


}
