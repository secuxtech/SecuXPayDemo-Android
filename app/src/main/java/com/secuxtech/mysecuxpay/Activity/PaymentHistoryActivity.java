package com.secuxtech.mysecuxpay.Activity;

import android.os.Bundle;

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

        mAdapter = new HistoryListAdapter(this, Wallet.getInstance().getPaymentHistory());
        recyclerView.setAdapter(mAdapter);

    }
}
