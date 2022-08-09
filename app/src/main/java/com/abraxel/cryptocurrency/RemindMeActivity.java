package com.abraxel.cryptocurrency;


import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class RemindMeActivity extends AppCompatActivity {

    private TextView coinName;
    private EditText valueSetter;
    private Button setReminderButton;
    private ElegantNumberButton elegantNumberButton;
    private ImageView imageViewReminder;
    String coinNameFromBundle;
    double coinValueFromBundle;

    private FirebaseFirestore firebaseDatabase;


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
                firebaseDatabase = FirebaseFirestore.getInstance();
                dbData.setCoinName(coinNameFromBundle);
                dbData.setCoinVal(Integer.valueOf(valueSetter.getText().toString()));
                firebaseDatabase.collection("currencies").document(coinNameFromBundle.toLowerCase(Locale.ROOT) + "-reminder").set(dbData)
                        .addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(), getString(R.string.reminder_set_successful), Toast.LENGTH_LONG).show()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception error) {
                                Toast.makeText(getApplicationContext(), getString(R.string.data_could_not_be_saved) + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        imageViewReminder.setImageResource((Integer) extras.get(Constants.COIN_LOGO));
    }
}