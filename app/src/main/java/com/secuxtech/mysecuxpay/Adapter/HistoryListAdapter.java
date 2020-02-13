package com.secuxtech.mysecuxpay.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.secuxtech.mysecuxpay.Model.PaymentHistoryModel;
import com.secuxtech.mysecuxpay.R;

import java.util.List;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private Context mContext;
    private List<PaymentHistoryModel> mHistoryList;

    public HistoryListAdapter(Context context, List<PaymentHistoryModel> histryList) {
        this.mContext = context;
        this.mHistoryList = histryList;
    }

    @Override
    public HistoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater ll = LayoutInflater.from(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_payment_history_layout, parent, false);

        return new HistoryListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HistoryListAdapter.ViewHolder holder, int position) {
        final PaymentHistoryModel historyItem = mHistoryList.get(position);

        holder.textviewStoreName.setText(historyItem.mStoreName);
        holder.textviewAccount.setText(historyItem.mAccount.mName);
        holder.textviewDate.setText(historyItem.mDate);
        holder.textviewUsdbalance.setText("$ " + historyItem.mUsbBalance);
        holder.textviewBalance.setText(historyItem.mBalance + " " + historyItem.mAccount.mCoinType);
        holder.imageviewCoinLogo.setImageResource(historyItem.mAccount.GetCoinLogo());
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textviewStoreName, textviewAccount, textviewDate, textviewUsdbalance, textviewBalance;
        ImageView imageviewCoinLogo;
        ViewHolder(View itemView) {
            super(itemView);

            textviewStoreName = itemView.findViewById(R.id.textView_history_storename);
            textviewAccount = itemView.findViewById(R.id.textView_history_account);
            textviewDate = itemView.findViewById(R.id.textView_history_date);
            textviewUsdbalance = itemView.findViewById(R.id.textView_history_usdbalance);
            textviewBalance = itemView.findViewById(R.id.textView_history_balance);
            imageviewCoinLogo = itemView.findViewById(R.id.imageView_history_coinlogo);

        }

    }
}
