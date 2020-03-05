package com.secuxtech.mysecuxpay.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.secuxtech.mysecuxpay.Interface.AdapterItemClickListener;
import com.secuxtech.mysecuxpay.Interface.OnListScrollListener;
import com.secuxtech.mysecuxpay.Model.Setting;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.AccountUtil;
import com.secuxtech.paymentkit.SecuXCoinAccount;
import com.secuxtech.paymentkit.SecuXPaymentHistory;

import java.util.List;


public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private Context mContext;
    private List<SecuXPaymentHistory> mHistoryList;

    private AdapterItemClickListener mClickListener;

    OnListScrollListener mOnListScrollListener = null;

    public HistoryListAdapter(Context context, List<SecuXPaymentHistory> histryList, AdapterItemClickListener clickListener) {
        this.mContext = context;
        this.mHistoryList = histryList;
        this.mClickListener = clickListener;
    }

    @Override
    public HistoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater ll = LayoutInflater.from(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_payment_history_layout, parent, false);

        return new HistoryListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HistoryListAdapter.ViewHolder holder, int position) {
        final SecuXPaymentHistory historyItem = mHistoryList.get(position);

        holder.textviewStoreName.setText(historyItem.mStoreName);

        SecuXCoinAccount coinAcc = Setting.getInstance().mAccount.getCoinAccount(historyItem.mCoinType);
        if (coinAcc!=null){
            holder.textviewAccount.setText(coinAcc.mAccountName);
        }else{
            holder.textviewAccount.setText("N/A");
        }

        holder.textviewDate.setText(historyItem.mTransactionTime + " UTC");
        //holder.textviewUsdbalance.setText("$ " + 0);
        holder.textviewTransCode.setText(historyItem.mTransactionCode);
        holder.textviewBalance.setText(historyItem.mAmount.toString() + " " + historyItem.mToken);
        holder.imageviewCoinLogo.setImageResource(AccountUtil.getCoinLogo(historyItem.mCoinType));

        if (position == mHistoryList.size()-1 && mOnListScrollListener!=null){
            mOnListScrollListener.onBottomReached(position);
        }
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

    public void setOnListScrollListener(OnListScrollListener listener){
        mOnListScrollListener = listener;
    }

    public void updateHistoryList(List<SecuXPaymentHistory> histryList){
        mHistoryList = histryList;
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textviewStoreName, textviewAccount, textviewDate, textviewTransCode, textviewBalance;  //textviewUsdbalance,
        ImageView imageviewCoinLogo;
        ViewHolder(View itemView) {
            super(itemView);

            textviewStoreName = itemView.findViewById(R.id.textView_history_storename);
            textviewAccount = itemView.findViewById(R.id.textView_history_account);
            textviewDate = itemView.findViewById(R.id.textView_history_date);
            //textviewUsdbalance = itemView.findViewById(R.id.textView_history_usdbalance);
            textviewBalance = itemView.findViewById(R.id.textView_history_balance);
            imageviewCoinLogo = itemView.findViewById(R.id.imageView_history_coinlogo);
            textviewTransCode = itemView.findViewById(R.id.textView_history_code);

            CardView cardView = itemView.findViewById(R.id.cardView_payment_history);

            cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (mClickListener != null)
                        mClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }

    }
}
