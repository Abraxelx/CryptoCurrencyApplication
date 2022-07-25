package com.abraxel.cryptocurrency.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.abraxel.cryptocurrency.ChartDataActivity;
import com.abraxel.cryptocurrency.R;
import com.abraxel.cryptocurrency.constants.Constants;
import com.abraxel.cryptocurrency.model.CryptoCurrencies;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> implements Filterable {

    List<CryptoCurrencies> cryptoCurrencies;
    List<CryptoCurrencies> allCurrencies;
    Context context;

    public CurrencyAdapter(List<CryptoCurrencies> cryptoCurrencies, Context context) {
        this.cryptoCurrencies = cryptoCurrencies;
        this.context = context;
        this.allCurrencies = new ArrayList<>(cryptoCurrencies);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.crypto_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CryptoCurrencies currencies = cryptoCurrencies.get(position);
        holder.currencyImageView.setImageResource(currencies.getLogoResourceId());
        holder.coinName.setText(currencies.getCoinName());
        holder.pair.setText("Birim : " + currencies.getPair());
        holder.last.setText("Anlık Değer: " + currencies.getLast() + " ₺");

        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString string1 = new SpannableString("Değişim Yüzdesi : ");
        builder.append(string1);
        SpannableString string2 = new SpannableString("%" + currencies.getDailyPercent());

        if (!currencies.getDailyPercent().contains("-")) {
            string2.setSpan(new ForegroundColorSpan(Color.parseColor("#006600")), 0, string2.length(), 0);
        } else {
            string2.setSpan(new ForegroundColorSpan(Color.RED), 0, string2.length(), 0);
        }
        builder.append(string2);
        holder.percent.setText(builder, TextView.BufferType.SPANNABLE);
        holder.high.setText("En yüksek (24s) : " + currencies.getHigh() + " ₺");
        holder.low.setText("En düşük (24s) : " + currencies.getLow() + " ₺");


        holder.itemView.setOnClickListener(view -> {

            Intent reminderActivity = new Intent(view.getContext(), CoinReminderActivity.class);
            reminderActivity.putExtra(Constants.COIN_NAME, currencies.getCoinName().toLowerCase());
            view.getContext().startActivity(reminderActivity);

        });

    }

    @Override
    public int getItemCount() {
        return cryptoCurrencies == null ? 0 : cryptoCurrencies.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<CryptoCurrencies> filteredList = new ArrayList<>();
            final FilterResults filterResults = new FilterResults();


            if (constraint.toString().isEmpty()) {
                filteredList.addAll(cryptoCurrencies);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (CryptoCurrencies currency : cryptoCurrencies) {
                    if (currency.getCoinName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(currency);
                    }
                }
            }

            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            cryptoCurrencies = (List<CryptoCurrencies>) results.values;
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {

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
