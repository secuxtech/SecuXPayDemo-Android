package com.secuxtech.mysecuxpay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.secuxtech.mysecuxpay.Interface.AdapterItemClickListener;
import com.secuxtech.mysecuxpay.Model.CoinTokenAccount;
import com.secuxtech.mysecuxpay.R;
import com.secuxtech.mysecuxpay.Utility.AccountUtil;


import java.math.BigDecimal;
import java.util.List;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-21
 */
public class CoinAccountListAdapter extends RecyclerView.Adapter<CoinAccountListAdapter.ViewHolder>{

    private Context mContext;
    private List<CoinTokenAccount> mCoinAccountList;
    private AdapterItemClickListener mItemClickListener;

    public CoinAccountListAdapter(Context context, List<CoinTokenAccount> accountList, AdapterItemClickListener clickListener) {
        this.mContext = context;
        this.mCoinAccountList = accountList;
        this.mItemClickListener = clickListener;
    }

    @Override
    public CoinAccountListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater ll = LayoutInflater.from(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_accountinfo_layout, parent, false);

        return new CoinAccountListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CoinAccountListAdapter.ViewHolder holder, int position) {
        final CoinTokenAccount accItem = mCoinAccountList.get(position);

        holder.textviewAccountName.setText(accItem.mAccountName);
        holder.textviewBalance.setText(accItem.mBalance.mFormattedBalance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " " + accItem.mToken);
        holder.textviewUsdbalance.setText("$" + accItem.mBalance.mUSDBalance.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        holder.imageviewCoinLogo.setImageResource(AccountUtil.getCoinLogo(accItem.mCoinType));

    }


    @Override
    public int getItemCount() {
        return mCoinAccountList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textviewAccountName, textviewUsdbalance, textviewBalance;
        ImageView imageviewCoinLogo;
        ViewHolder(View itemView) {
            super(itemView);

            textviewAccountName = itemView.findViewById(R.id.textView_account_name);
            textviewUsdbalance = itemView.findViewById(R.id.textView_account_usdbalance);
            textviewBalance = itemView.findViewById(R.id.textView_account_balance);
            imageviewCoinLogo = itemView.findViewById(R.id.imageView_account_coinlogo);

            /*
            CardView cardView = itemView.findViewById(R.id.cardView_account);

            cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (mItemClickListener != null){
                        mItemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });

             */

        }

    }
}
