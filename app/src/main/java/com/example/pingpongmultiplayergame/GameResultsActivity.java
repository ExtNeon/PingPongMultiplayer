package com.example.pingpongmultiplayergame;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameResultsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);
        int myScore = getIntent().getIntExtra("my_score", 0);
        int enemyScore = getIntent().getIntExtra("enemy_score", 0);
        String formattedBestTime = getIntent().getStringExtra("best_time_formatted");
        ((TextView) findViewById(R.id.result_score_caption)).setText(myScore + ":" + enemyScore);
        ((TextView) findViewById(R.id.conclusion_caption)).setText(myScore > enemyScore ? getString(R.string.you_won) : getString(R.string.you_lose));
        ((TextView) findViewById(R.id.match_time_stats)).setText(getString(R.string.best_time) + " " + formattedBestTime);
        ((Button) findViewById(R.id.return_button)).setText(enemyScore > myScore ? getString(R.string.revenge) : getString(R.string.replay));
        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
