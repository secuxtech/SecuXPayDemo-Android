package com.secuxtech.mysecuxpay.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.secuxtech.mysecuxpay.Adapter.HistoryListAdapter;
import com.secuxtech.mysecuxpay.Adapter.TokenTransHistoryAdapter;
import com.secuxtech.mysecuxpay.Interface.AdapterItemClickListener;
import com.secuxtech.mysecuxpay.Interface.OnListScrollListener;
import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;
import com.secuxtech.paymentkit.SecuXTransferHistory;

import java.util.ArrayList;

public class TokenTransHistoryActivity extends BaseActivity {

    public final static String TRANSACTION_HISTORY_COINTYPE = "com.secuxtech.MySecuXPay.TRANSHISCOINTYPE";
    public final static String TRANSACTION_HISTORY_TOKEN = "com.secuxtech.MySecuXPay.TRANSHISTOKEN";


    private SecuXAccountManager mAccountManager = new SecuXAccountManager();
    private ArrayList<SecuXTransferHistory> mTransHistoryArray = new ArrayList<>();
    private TokenTransHistoryAdapter mAdapter;

    private String mCoinType;
    private String mToken;

    AdapterItemClickListener mItemClickListener = new AdapterItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            SecuXTransferHistory history = mTransHistoryArray.get(position);
            Log.i(TAG, history.mDetailslUrl);

            Intent newIntent = new Intent(mContext, TokenTransferDetailsActivity.class);
            newIntent.putExtra(TokenTransferDetailsActivity.TRANSACTION_HISTORY_DETAIL_URL, history.mDetailslUrl);
            startActivity(newIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_trans_history);

        Intent intent = getIntent();
        mCoinType = intent.getStringExtra(TRANSACTION_HISTORY_COINTYPE);
        mToken = intent.getStringExtra(TRANSACTION_HISTORY_TOKEN);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView_transhistory_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        CommonProgressDialog.showProgressDialog(mContext, "Loading");
        new Thread(new Runnable() {
            @Override
            public void run() {

                Pair<Integer, String> ret = mAccountManager.getTransferHistory(mCoinType, mToken, 1, 20, mTransHistoryArray);
                if (ret.first!= SecuXServerRequestHandler.SecuXRequestOK){
                    showMessageInMain("Get payment history failed! Error: " + ret.second);

                    if (ret.second.contains("No token") || ret.first == SecuXServerRequestHandler.SecuXRequestUnauthorized){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent newIntent = new Intent(mContext, LoginActivity.class);
                                startActivity(newIntent);
                            }
                        });
                        return;
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonProgressDialog.dismiss();
                        if (mTransHistoryArray.size()>0){
                            mAdapter = new TokenTransHistoryAdapter(mContext, mTransHistoryArray, mItemClickListener);
                            recyclerView.setAdapter(mAdapter);

                        }else{
                            TextView textviewNoHistory = findViewById(R.id.textView_transhistory_nohistory);
                            textviewNoHistory.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        }).start();
    }

}
