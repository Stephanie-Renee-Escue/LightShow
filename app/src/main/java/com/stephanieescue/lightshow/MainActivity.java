package com.stephanieescue.lightshow;

// Team Members: Joshua Foster, Lionel Sosa Estrada, and Stephanie Escue

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SoundPool soundPool;
    private Set<Integer> soundsLoaded;
    int soundId, topLeftId, topRightId, bottomLeftId, bottomRightId, errorId, gameStartId, gameOverId;
    Button topLeftButton, topRightButton, bottomLeftButton, bottomRightButton;
    public TextView message, playerScore, highScore;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = findViewById(R.id.messageTV);
        playerScore = findViewById(R.id.playerScoreTV);
        highScore = findViewById(R.id.topScoreTV);

        soundsLoaded = new HashSet<Integer>();
        soundSetup();

    }

    LightShow game = new LightShow();

    public void onClick(View view) {
        setContentView(R.layout.game_board);
        ImageView logo = findViewById(R.id.logo);

        switch (view.getId()) {

            case R.id.playClassicButton:
                logo.setImageResource(R.drawable.classic_logo);
                playGame();
                //howToPlayButton.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                break;
            case R.id.playRewindButton:
                logo.setImageResource(R.drawable.rewind_logo);
                playGame();
                break;
            case R.id.playSurpriseButton:
                logo.setImageResource(R.drawable.surprise_logo);
                playGame();
        }
    }

    private void playGame(){
        Handler handler = new Handler();

        // playerScore.setText("Your Score: " + game.getScore);
        // Add top score here

        playSound(gameStartId);
        game.setButtons((Button)findViewById(R.id.topLeftButton), (Button)findViewById(R.id.topRightButton),
                (Button)findViewById(R.id.bottomLeftButton), (Button)findViewById(R.id.bottomRightButton));
        setButtonListeners();
        game.setColors(ResourcesCompat.getColor(getResources(), R.color.darkGreen, null),
                ResourcesCompat.getColor(getResources(), R.color.lightGreen, null),
                ResourcesCompat.getColor(getResources(), R.color.darkRed, null),
                ResourcesCompat.getColor(getResources(), R.color.lightRed, null),
                ResourcesCompat.getColor(getResources(), R.color.darkYellow, null),
                ResourcesCompat.getColor(getResources(), R.color.lightYellow, null),
                ResourcesCompat.getColor(getResources(), R.color.darkBlue, null),
                ResourcesCompat.getColor(getResources(), R.color.lightBlue, null));
        /*for (int i=0; i < 4; i++) {
            topLeftButton.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGreen));
            handler.postDelayed((Runnable) this, 1000);
            topRightButton.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGreen));
            handler.postDelayed((Runnable) this, 1000);
            bottomLeftButton.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGreen));
            handler.postDelayed((Runnable) this, 1000);
            bottomRightButton.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGreen));
            handler.postDelayed((Runnable) this, 1000);
        } */
        game.showSequence();
    }

    private void setButtonListeners(){
        Button tempButton;

        tempButton = findViewById(R.id.topLeftButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundId=topLeftId;
                playSound(soundId);
                nextStep(1);
            }
        });
        tempButton = findViewById(R.id.topRightButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundId=topRightId;
                playSound(soundId);
                nextStep(2);
            }
        });
        tempButton = findViewById(R.id.bottomLeftButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundId=bottomLeftId;
                playSound(soundId);
                nextStep(3);
            }
        });
        tempButton = findViewById(R.id.bottomRightButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundId=bottomRightId;
                playSound(soundId);
                nextStep(4);
            }
        });

        findViewById(R.id.mainMenuButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_main);
            }
        });

        findViewById(R.id.highScoreButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.high_score);

            }
        });

        findViewById(R.id.playButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //message.setVisibility(View.INVISIBLE);

            }
        });

    }

    private void soundSetup() {

        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setUsage(AudioAttributes.USAGE_GAME);

        SoundPool.Builder spBuilder = new SoundPool.Builder();
        spBuilder.setAudioAttributes(attrBuilder.build());
        spBuilder.setMaxStreams(1);
        soundPool = spBuilder.build();

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) { // success
                    soundsLoaded.add(sampleId);
                    Log.i("SOUND", "Sound loaded " + sampleId);
                } else {
                    Log.i("SOUND", "Error cannot load sound status = " + status);
                }
            }
        });

        // Assigning sounds to integer Id
        topLeftId = soundPool.load(this, R.raw.sound1, 1);
        topRightId = soundPool.load(this, R.raw.sound2, 1);
        bottomLeftId = soundPool.load(this, R.raw.sound3, 1);
        bottomRightId = soundPool.load(this, R.raw.sound4, 1);
        errorId = soundPool.load(this, R.raw.error, 1);
        gameStartId = soundPool.load(this, R.raw.startgame, 1);
        gameOverId = soundPool.load(this, R.raw.gameover, 1);

    }

    private void playSound(int soundId) {
        if (soundsLoaded.contains(soundId)) {
            soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    public void nextStep(int step){
        /* TextView gameMessage;

        public nextStep(TextView message) {
            gameMessage = message;
        } */

        if (game.validate_next_step(step))
            game.showSequence();
        else {
            playSound(errorId);
            //message.setVisibility(View.VISIBLE);
            // message.setText(getResources().getString(R.string.game_over));
            playSound(gameOverId);
            //setContentView(R.layout.activity_main);
        }
    }

    // Used for all click events for main menu button
    public void mainMenu(View view) {
        setContentView(R.layout.activity_main);
    }
}