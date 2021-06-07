package com.abraxel.cryptocurrency.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abraxel.cryptocurrency.MainActivity;
import com.abraxel.cryptocurrency.R;
import com.abraxel.cryptocurrency.model.CryptoCurrencies;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder>{

    List<CryptoCurrencies> cryptoCurrencies;
    Context context;
    public CurrencyAdapter(List<CryptoCurrencies> cryptoCurrencies, Context context) {
        this.cryptoCurrencies = cryptoCurrencies;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.crypto_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CryptoCurrencies currencies = cryptoCurrencies.get(position);
        holder.currencyImageView.setImageResource(currencies.getLogoResourceId());
        holder.coinName.setText(currencies.getCoinName());
        holder.pair.setText("Birim : " + currencies.getPair());
        holder.last.setText("Anlık Değer: " + currencies.getLast());
        holder.percent.setText("Değişim Yüzdesi : %" + currencies.getDailyPercent());
        holder.high.setText("En yüksek (24s) : " + currencies.getHigh());
        holder.low.setText("En düşük (24s) : " + currencies.getLow());


      /*  holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, currencies.getPair(), Toast.LENGTH_LONG).show();
            }
        });
       */


    }

    @Override
    public int getItemCount() {
        return cryptoCurrencies == null ? 0 : cryptoCurrencies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView currencyImageView;
        TextView coinName;
        TextView pair;
        TextView last;
        TextView percent;
        TextView high;
        TextView low;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyImageView = itemView.findViewById(R.id.currencyLogo);
            coinName = itemView.findViewById(R.id.coinName);
            pair = itemView.findViewById(R.id.pair);
            last = itemView.findViewById(R.id.last);
            percent = itemView.findViewById(R.id.percent);
            high = itemView.findViewById(R.id.high);
            low = itemView.findViewById(R.id.low);
        }
    }
}
