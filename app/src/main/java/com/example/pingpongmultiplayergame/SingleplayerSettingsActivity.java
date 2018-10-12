package com.example.pingpongmultiplayergame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.pingpongmultiplayergame.GameTools.PlayMode;

public class SingleplayerSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer_settings);
        findViewById(R.id.start_game_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleplayerSettingsActivity.this, GameFieldActivity.class);
                CheckBox allowAFKModeCheckBox = findViewById(R.id.allow_afk_cb);
                SeekBar ballSpeedBar = findViewById(R.id.max_ball_speed_bar);
                SeekBar enemySpeedBar = findViewById(R.id.enemy_speed_bar);
                TextView scoreLimitTV = findViewById(R.id.score_limit_input);
                intent.putExtra("mode", PlayMode.SINGLEPLAYER);
                intent.putExtra("enable_afk", allowAFKModeCheckBox.isChecked());
                intent.putExtra("ball_speed", ballSpeedBar.getProgress());
                intent.putExtra("enemy_speed", enemySpeedBar.getProgress() + 1);
                intent.putExtra("score_limit", Integer.valueOf("" + scoreLimitTV.getText()));

                startActivity(intent);
            }
        });
    }
}
