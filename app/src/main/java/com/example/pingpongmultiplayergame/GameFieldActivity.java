package com.example.pingpongmultiplayergame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pingpongmultiplayergame.GameTools.DoublePoint;
import com.example.pingpongmultiplayergame.GameTools.PlayMode;

import java.util.Objects;

public class GameFieldActivity extends AppCompatActivity implements View.OnTouchListener, Runnable {
    public static final int AFK_TIME_THRESHOLD = 8000;
    public static final int DELAY_BEFORE_START_ROUND = 400;
    public static final int DELAY_AFTER_LOSE_ROUND = 500;
    public static final double MIN_BALL_SPEED = 1.4;

    TextView myScore;
    TextView enemyScore;
    TextView AFKModeCaption;
    TextView maxTimeCaption;
    ImageView myPlatform;
    ImageView enemyPlatform;
    ImageView ball;
    View gameView;
    int myScoreCounter = 0;
    int enemyScoreCounter = 0;
    boolean inAFKMode = false;
    PlayMode mode;

    long lastTouchTime = System.currentTimeMillis();
    long maxGameTime = 0;


    @Override
    public void run() {
        while (gameView.getHeight() <= 0) {
            delayMs(100);
        }
        DoublePoint centerPoint = new DoublePoint(gameView.getWidth() / 2, gameView.getHeight() / 2 - (ball.getHeight() / 2));

        prepareField(centerPoint);

        mode = (PlayMode) getIntent().getSerializableExtra("mode");
        switch (mode) {
            case SINGLEPLAYER:
                doSingleplayerGame(centerPoint,
                        getIntent().getIntExtra("ball_speed", 6),
                        getIntent().getIntExtra("enemy_speed", 3),
                        getIntent().getBooleanExtra("enable_afk", true),
                        getIntent().getIntExtra("score_limit", 20));
                break;
        }

        showResults();
        finish();
    }

    private void showResults() {
        Intent resultIntent = new Intent(GameFieldActivity.this, GameResultsActivity.class);
        resultIntent.putExtra("enemy_score", enemyScoreCounter);
        resultIntent.putExtra("my_score", myScoreCounter);
        resultIntent.putExtra("best_time_formatted", formatTime(maxGameTime));
        startActivity(resultIntent);
    }

    private void prepareField(DoublePoint centerPoint) {
        setYOnView(myPlatform, gameView.getHeight() - gameView.getHeight() / 8);
        setYOnView(enemyPlatform, gameView.getHeight() / 8);
        setXOnView(myPlatform, (float) centerPoint.x - myPlatform.getWidth() / 2);
        setXOnView(enemyPlatform, myPlatform.getX());

        setYOnView(myScore, (float) (centerPoint.y + 10.));
        setYOnView(enemyScore, (float) (centerPoint.y - enemyScore.getHeight() - 10.));
        setXOnView(myScore, gameView.getWidth() - myScore.getWidth() - 2);
        setXOnView(enemyScore, gameView.getWidth() - enemyScore.getWidth() - 2);

        setXOnView(AFKModeCaption, (float) centerPoint.x - AFKModeCaption.getWidth() / 2);
        setXOnView(maxTimeCaption, gameView.getWidth() - maxTimeCaption.getWidth() - 3);
        setYOnView(AFKModeCaption, gameView.getHeight() - AFKModeCaption.getHeight());
        setYOnView(maxTimeCaption, 0);
        setViewVisibility(gameView, View.VISIBLE);
    }

    private void doSingleplayerGame(DoublePoint centerPoint, int maxBallSpeed, int enemyAIspeed, boolean allowAFK, int scoreLimit) {
        DoublePoint ballPosition = new DoublePoint(centerPoint.x - ball.getWidth() / 2, centerPoint.y);
        DoublePoint ballSpeed = new DoublePoint(0, 0);
        startNewRound(centerPoint, maxBallSpeed, ballPosition, ballSpeed, false);
        long gameStartedTime = System.currentTimeMillis();

        while (myScoreCounter < scoreLimit && enemyScoreCounter < scoreLimit) {
            accelerateBall(ballPosition, ballSpeed);
            checkBallCollisionWithTheFieldEdges(ballPosition, ballSpeed);
            processAFKMode(allowAFK, ballPosition, enemyAIspeed);
            applyAIEnemyAction(ballPosition, enemyAIspeed);
            if (System.currentTimeMillis() - gameStartedTime > maxGameTime) {
                maxGameTime = System.currentTimeMillis() - gameStartedTime;
            }
            updateTimeCaption(gameStartedTime);
            if (checkBallLoseFactor(centerPoint, maxBallSpeed, ballPosition, ballSpeed)) {
                gameStartedTime = System.currentTimeMillis();
            }
            checkCollisionWithMyPlatform(ballPosition, ballSpeed, maxBallSpeed);
            checkCollisionWithEnenemyPlatform(ballPosition, ballSpeed, maxBallSpeed);
            delayMs(10);
            refreshBallPosition(ballPosition);
        }
    }

    private boolean checkBallLoseFactor(DoublePoint centerPoint, int maxBallSpeed, DoublePoint ballPosition, DoublePoint ballSpeed) {
        boolean enemyLoseFlag = ballPosition.y < -(ball.getHeight() + 10);
        if (ballPosition.y > gameView.getHeight() + 10 || enemyLoseFlag) {
            if (enemyLoseFlag) {
                myScoreCounter++;
                setImageResourceOnView(enemyPlatform, R.color.losedPlatformColor);
            } else {
                enemyScoreCounter++;
                setImageResourceOnView(myPlatform, R.color.losedPlatformColor);
            }
            setTextOnView(myScore, "" + myScoreCounter);
            setTextOnView(enemyScore, "" + enemyScoreCounter);
            delayMs(DELAY_AFTER_LOSE_ROUND);
            startNewRound(centerPoint, maxBallSpeed, ballPosition, ballSpeed, enemyLoseFlag);

            return true;
        } else {
            return false;
        }
    }

    private void startNewRound(DoublePoint centerPoint, int maxBallSpeed, DoublePoint ballPosition, DoublePoint ballSpeed, boolean enemyLoseFlag) {
        setImageResourceOnView(enemyPlatform, R.color.normalPlatformColor);
        setImageResourceOnView(myPlatform, R.color.normalPlatformColor);
        ballPosition.set(centerPoint.x - ball.getWidth() / 2, centerPoint.y);
        ballSpeed.set(generateNewBallSpeed(maxBallSpeed, enemyLoseFlag));
        refreshBallPosition(ballPosition);
        delayMs(DELAY_BEFORE_START_ROUND);
    }

    private DoublePoint generateNewBallSpeed(int maxBallSpeed, boolean enemyLoseFlag) {
        double newYSpeed = Math.random() * maxBallSpeed / 1.5 + MIN_BALL_SPEED;
        double newXSpeed = Math.random() * maxBallSpeed / 1.5 + MIN_BALL_SPEED;
        if (Math.random() > 0.5) { //С вероятностью в 50% мы выбираем, в какую сторону запускать шарик
            newXSpeed *= -1;
        }
        if (enemyLoseFlag) {
            newYSpeed *= -1;
        }
        return new DoublePoint(newXSpeed, newYSpeed);
    }

    private void updateTimeCaption(long gameStartedTime) {
        setTextOnView(maxTimeCaption, getString(R.string.current_round_time) + ' ' + formatTime(System.currentTimeMillis() - gameStartedTime) + '\n' + getString(R.string.best_time) + ' ' + formatTime(maxGameTime));
        setXOnView(maxTimeCaption, gameView.getWidth() - maxTimeCaption.getWidth() - 3);
    }

    private void processAFKMode(boolean allowAFK, DoublePoint ballPosition, int platformFollowingStep) {
        if (allowAFK && System.currentTimeMillis() - lastTouchTime > AFK_TIME_THRESHOLD) {
            applyAFK_AI_Action(ballPosition, platformFollowingStep);
            setViewVisibility(AFKModeCaption, View.VISIBLE);
            inAFKMode = true;
        } else {
            setViewVisibility(AFKModeCaption, View.INVISIBLE);
            inAFKMode = false;
        }
    }

    private void checkBallCollisionWithTheFieldEdges(DoublePoint ballPosition, DoublePoint ballSpeed) {
        if (ballPosition.x + ball.getWidth() > gameView.getWidth()) {
            ballSpeed.x *= -1;
            ballPosition.x = gameView.getWidth() - ball.getWidth();
        } else if (ballPosition.x < 0) {
            ballPosition.x = 0;
            ballSpeed.x *= -1;
        }
    }

    private void accelerateBall(DoublePoint ballPosition, DoublePoint ballSpeed) {
        ballPosition.x += ballSpeed.x;
        ballPosition.y += ballSpeed.y;
    }

    /**
     * Метод, позволяющий изменять текст в TextView из другого потока
     *
     * @param view  TextView, в котором необходимо изменить текст
     * @param value Новое значение поля text в TextView
     */
    private void setTextOnView(final TextView view, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(value);
            }
        });
    }


    /**
     * Метод, позволяющий изменять параметр visibility в View из другого потока
     *
     * @param view       View, в котором необходимо изменить данный параметр
     * @param visibility Новое значение параметра visibility
     */
    private void setViewVisibility(final View view, final int visibility) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(visibility);
            }
        });
    }

    /**
     * Метод, позволяющий изменять координату x в определённом View из другого потока
     *
     * @param view View, в котором необходимо изменить данный параметр
     * @param x    Новое значение параметра координаты x
     */
    private void setXOnView(final View view, final float x) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setX(x);
            }
        });
    }

    /**
     * Метод, позволяющий изменять координату y в определённом View из другого потока
     *
     * @param view View, в котором необходимо изменить данный параметр
     * @param y    Новое значение параметра координаты y
     */
    private void setYOnView(final View view, final float y) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setY(y);
            }
        });
    }

    /**
     * Метод, позволяющий изменить значение параметра imageResource в определённом ImageView из другого потока
     *
     * @param view  ImageView, в котором необходимо изменить целевой параметр
     * @param resId Новое значение идентефикатора ресурса
     */
    private void setImageResourceOnView(final ImageView view, final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setImageResource(resId);
            }
        });
    }

    private void applyAIEnemyAction(DoublePoint ballPosition, int movementSpeed) {

        if (ballPosition.y < gameView.getHeight() / 2 && Math.abs(ballPosition.x - (enemyPlatform.getX() + enemyPlatform.getWidth() / 2)) > movementSpeed * 1.3) {
            float newX;
            double difference = Math.abs(ballPosition.x - (enemyPlatform.getX() + enemyPlatform.getWidth() / 2));
            double moveAmount = difference < movementSpeed ? difference : movementSpeed;

            if (ballPosition.x > enemyPlatform.getX() + enemyPlatform.getWidth() / 2) {
                newX = enemyPlatform.getX() + (float) moveAmount;
            } else {
                newX = enemyPlatform.getX() - (float) moveAmount;

            }
            if (enemyPlatform.getX() < 0 || newX < 0) {
                setXOnView(enemyPlatform, 0);
            } else if (enemyPlatform.getX() > gameView.getWidth() - enemyPlatform.getWidth() || newX > gameView.getWidth() - enemyPlatform.getWidth()) {
                setXOnView(enemyPlatform, gameView.getWidth() - enemyPlatform.getWidth());
            } else {
                setXOnView(enemyPlatform, newX);
            }
        }

    }

    private void applyAFK_AI_Action(DoublePoint ballPosition, int movementSpeed) {
        if (ballPosition.y > gameView.getHeight() / 2 && Math.abs(ballPosition.x - (myPlatform.getX() + myPlatform.getWidth() / 2)) > movementSpeed * 1.3) {
            float newX;
            double difference = Math.abs(ballPosition.x - (myPlatform.getX() + myPlatform.getWidth() / 2));
            double moveAmount = difference < movementSpeed ? difference : movementSpeed;
            if (ballPosition.x > myPlatform.getX() + myPlatform.getWidth() / 2) {
                newX = myPlatform.getX() + (float) moveAmount;
            } else {
                newX = myPlatform.getX() - (float) moveAmount;
            }
            if (myPlatform.getX() < 0 || newX < 0) {
                setXOnView(myPlatform, 0);
            } else if (myPlatform.getX() > gameView.getWidth() - myPlatform.getWidth() || newX > gameView.getWidth() - myPlatform.getWidth()) {
                setXOnView(myPlatform, gameView.getWidth() - myPlatform.getWidth());
            } else {
                setXOnView(myPlatform, newX);
            }
        }

    }

    private void checkCollisionWithMyPlatform(DoublePoint ballPosition, DoublePoint ballSpeed, double maxBallSpeed) {
        if ((ballPosition.y + ball.getHeight() >= myPlatform.getY() && ballPosition.y <= myPlatform.getHeight() + myPlatform.getY())) {
            if ((ballPosition.x < myPlatform.getX() + myPlatform.getWidth() && ballPosition.x + ball.getWidth() > myPlatform.getX())) {
                double accuracy = Math.abs(ballPosition.x - (myPlatform.getX() + (myPlatform.getWidth() / 2.))) / (myPlatform.getWidth() / 2.);
                if (ballSpeed.y < 0) {
                    accuracy *= -1;
                }
                ballSpeed.y = -(accuracy + ballSpeed.y);
                if (Math.abs(ballSpeed.y) > maxBallSpeed) {
                    ballSpeed.y -= ballSpeed.y / 8;
                    if (Math.abs(ballSpeed.x) < maxBallSpeed) {
                        ballSpeed.x += ballSpeed.x / 8;
                    }
                }

                ballPosition.y = myPlatform.getY() - (ball.getHeight());
            }
            if (Math.abs(ballPosition.x - (myPlatform.getX() + myPlatform.getWidth())) <= Math.abs(ballSpeed.x) || Math.abs(myPlatform.getX() - (ballPosition.x + ball.getWidth())) <= Math.abs(ballSpeed.x)) {
                ballSpeed.x *= -1;
            }
        }
    }

    private void checkCollisionWithEnenemyPlatform(DoublePoint ballPosition, DoublePoint ballSpeed, double maxBallSpeed) {
        if ((ballPosition.y <= enemyPlatform.getY() + enemyPlatform.getHeight() && ballPosition.y + ball.getHeight() >= enemyPlatform.getY())) {
            if ((ballPosition.x < enemyPlatform.getX() + enemyPlatform.getWidth() && ballPosition.x + ball.getWidth() > enemyPlatform.getX())) {
                double accuracy = Math.abs(ballPosition.x - (enemyPlatform.getX() + (enemyPlatform.getWidth() / 2.))) / (enemyPlatform.getWidth() / 2.);
                if (ballSpeed.y < 0) {
                    accuracy *= -1;
                }
                ballSpeed.y = -(accuracy + ballSpeed.y);
                if (Math.abs(ballSpeed.y) > maxBallSpeed) {
                    ballSpeed.y -= ballSpeed.y / 8;
                    if (Math.abs(ballSpeed.x) < maxBallSpeed) {
                        ballSpeed.x += ballSpeed.x / 8;
                    }
                }
                ballPosition.y = enemyPlatform.getY() + enemyPlatform.getHeight();
            }
            if (Math.abs(ballPosition.x - (enemyPlatform.getX() + enemyPlatform.getWidth())) <= Math.abs(ballSpeed.x) || Math.abs(enemyPlatform.getX() - (ballPosition.x + ball.getWidth())) <= Math.abs(ballSpeed.x)) {
                ballSpeed.x *= -1;
            }
        }
    }

    private void refreshBallPosition(DoublePoint newBallPosition) {
        setXOnView(ball, (float) newBallPosition.x);
        setYOnView(ball, (float) newBallPosition.y);
    }

    private void delayMs(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_field);
        // Убрать ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();
        // инициализируем нужные элементы
        myScore = findViewById(R.id.my_score);
        enemyScore = findViewById(R.id.enemy_score);
        gameView = findViewById(R.id.main_layout);
        myPlatform = findViewById(R.id.my_platform);
        enemyPlatform = findViewById(R.id.enemy_platform);
        ball = findViewById(R.id.ball);
        AFKModeCaption = findViewById(R.id.afk_mode_caption);
        maxTimeCaption = findViewById(R.id.max_time_caption);
        // назначаем слушателя касания для Layout-а
        gameView.setOnTouchListener(this);
        gameView.setVisibility(View.INVISIBLE);
        new Thread(this).start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // получаем координаты касания
        myPlatform.setX((int) event.getX() - (myPlatform.getX() / 2));
        lastTouchTime = System.currentTimeMillis();
        gameView.performClick();
        return true;
    }

    private String formatTime(long millis) {
        int seconds = (int) millis / 1000;
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        int hours = minutes / 60;
        minutes -= hours * 60;
        int days = hours / 24;
        hours -= days * 24;

        return (days > 0 ? days + " " + getString(R.string.days) + " " : "") + (hours > 0 ? hours + " " + getString(R.string.hours) + " " : "") + (minutes > 0 ? minutes + " " + getString(R.string.minutes) + " " : "") + seconds /*+ (false ? '.' + millis : "") */ + " " + getString(R.string.seconds);
    }
}
