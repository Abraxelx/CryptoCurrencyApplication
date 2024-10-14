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
import com.abraxel.cryptocurrency.model.ReminderData;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import nl.dionsegijn.steppertouch.StepperTouch;


public class RemindMeActivity extends AppCompatActivity {


    String coinNameFromBundle;
    double coinValueFromBundle;

    private FirebaseFirestore fireStore;


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
            if (valueSetter.getText() != null) {
                ReminderData reminderData = new ReminderData();
                fireStore = FirebaseFirestore.getInstance();
                reminderData.setCoinName(coinNameFromBundle);
                reminderData.setCurrentValue((int) coinValueFromBundle);
                reminderData.setDesiredValue(Integer.parseInt(valueSetter.getText().toString()));
                reminderData.setDeviceKey(MainActivity.TOKEN);
                reminderData.setNotified(false);
                if (reminderData.getDesiredValue() != reminderData.getCurrentValue()) {
                    fireStore.collection(coinNameFromBundle.toLowerCase(Locale.ROOT))
                            .document(MainActivity.TOKEN)
                            .set(reminderData)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(getApplicationContext(), getString(R.string.reminder_set_successful), Toast.LENGTH_LONG).show()).addOnFailureListener(error ->
                                    Toast.makeText(getApplicationContext(), getString(R.string.data_could_not_be_saved) + error.getMessage(), Toast.LENGTH_LONG).show());
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.same_value_error), Toast.LENGTH_LONG).show();
                }
            }
        });

        imageViewReminder.setImageResource((currencies.getLogoResourceId()));
    }
}