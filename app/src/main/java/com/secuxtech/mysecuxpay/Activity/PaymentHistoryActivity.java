package com.secuxtech.mysecuxpay.Activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.secuxtech.mysecuxpay.Adapter.HistoryListAdapter;
import com.secuxtech.mysecuxpay.Model.Wallet;
import com.secuxtech.mysecuxpay.R;

public class PaymentHistoryActivity extends BaseActivity {

    HistoryListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_history_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        if (Wallet.getInstance().getPaymentHistory().size()>0){
            mAdapter = new HistoryListAdapter(this, Wallet.getInstance().getPaymentHistory());
            recyclerView.setAdapter(mAdapter);
        }else{
            TextView textviewNoHistory = findViewById(R.id.textView_nohistory);
            textviewNoHistory.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }



    }


}
