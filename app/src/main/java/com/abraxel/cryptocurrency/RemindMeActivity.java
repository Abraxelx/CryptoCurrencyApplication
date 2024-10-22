package com.abraxel.cryptocurrency;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abraxel.cryptocurrency.model.CryptoCurrencies;

import nl.dionsegijn.steppertouch.StepperTouch;


public class RemindMeActivity extends AppCompatActivity {


    String coinNameFromBundle;
    double coinValueFromBundle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StepperTouch stepperTouch;
        Button setReminderButton;
        EditText valueSetter;
        TextView coinName;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_me);

        coinName = findViewById(R.id.txt_coin_name_reminder);
        valueSetter = findViewById(R.id.edttxt_value_setter);
        setReminderButton = findViewById(R.id.btn_remind_me);
        stepperTouch = findViewById(R.id.stepperTouch);
        ImageView imageViewReminder = findViewById(R.id.img_reminder);

        Bundle receivedBundle = getIntent().getExtras();
        CryptoCurrencies currencies = null;
        if (receivedBundle != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                currencies = receivedBundle.getSerializable("reminderData", CryptoCurrencies.class);
            } else {
                currencies = (CryptoCurrencies) receivedBundle.getSerializable("reminderData");
            }
        }
        assert currencies != null;
        coinNameFromBundle = currencies.getCoinName();
        coinValueFromBundle = Double.parseDouble(currencies.getLast());
        int valOfCoinInInt = (int) coinValueFromBundle;
        coinName.setText(coinNameFromBundle);
        valueSetter.setText(String.valueOf(valOfCoinInInt));
        stepperTouch.setMaxValue(valOfCoinInInt * 10);
        stepperTouch.setMinValue(0);
        stepperTouch.setCount(valOfCoinInInt);
        stepperTouch.setSideTapEnabled(true);
        stepperTouch.addStepCallback((i, b) -> valueSetter.setText(String.valueOf(i)));


        setReminderButton.setOnClickListener(view -> {
            Toast.makeText(this, "Reminder set for " + coinNameFromBundle, Toast.LENGTH_SHORT).show();
        });

        imageViewReminder.setImageResource((currencies.getLogoResourceId()));
    }
}