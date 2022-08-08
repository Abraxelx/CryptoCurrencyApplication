package com.abraxel.cryptocurrency;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abraxel.cryptocurrency.constants.Constants;
import com.abraxel.cryptocurrency.model.DbData;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class RemindMeActivity extends AppCompatActivity {

    private TextView coinName;
    private EditText valueSetter;
    private Button setReminderButton;
    private ElegantNumberButton elegantNumberButton;
    private ImageView imageViewReminder;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference db;
    String coinNameFromBundle;
    double coinValueFromBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_me);

        coinName = findViewById(R.id.txt_coin_name_reminder);
        valueSetter = findViewById(R.id.edttxt_value_setter);
        setReminderButton = findViewById(R.id.btn_remind_me);
        elegantNumberButton = findViewById(R.id.btn_elegant_number);
        imageViewReminder = findViewById(R.id.img_reminder);

        Bundle extras = getIntent().getExtras();
        coinNameFromBundle = extras.get(Constants.COIN_NAME).toString();
        coinValueFromBundle = (double) extras.get(Constants.COIN_VALUE);
        int valOfCoinInInt = (int) coinValueFromBundle;
        coinName.setText(coinNameFromBundle);
        valueSetter.setText(String.valueOf(valOfCoinInInt));
        elegantNumberButton.setNumber(String.valueOf(valOfCoinInInt));

        elegantNumberButton.setOnValueChangeListener((view, oldValue, newValue) -> {
            valueSetter.setText(String.valueOf(newValue));
        });


        setReminderButton.setOnClickListener(view -> {
            if (valueSetter.getText() != null) {
                DbData dbData = new DbData();
                firebaseDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_URL);
                db = firebaseDatabase.getReference(coinNameFromBundle.toLowerCase(Locale.ROOT) + "-reminder");
                dbData.setCoinName(coinNameFromBundle);
                dbData.setCoinVal(Integer.valueOf(valueSetter.getText().toString()));
                db.setValue(dbData, (error, ref) -> {
                    if (error != null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.data_could_not_be_saved) + error.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.reminder_set_successful), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        imageViewReminder.setImageResource((Integer) extras.get(Constants.COIN_LOGO));
    }
}