package com.example.pingpongmultiplayergame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        findViewById(R.id.select_game_mode_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton singleplayerRadioButton = findViewById(R.id.singleplayer_rb);
                if (singleplayerRadioButton.isChecked()) {
                    Intent intent = new Intent(LaunchActivity.this, SingleplayerSettingsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
