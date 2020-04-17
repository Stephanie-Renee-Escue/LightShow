package com.stephanieescue.lightshow;

// Team Members: Joshua Foster, Lionel Sosa Estrada, and Stephanie Escue

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
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
    private int[] sounds = new int[4];
    private Button[] buttons = new Button[4];
    int errorId, gameStartId, gameOverId;
    public TextView message, playerScore, highScore;;
    LightShow game;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        game = new LightShow(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = findViewById(R.id.messageTV);
        playerScore = findViewById(R.id.playerScoreTV);
        highScore = findViewById(R.id.topScoreTV);

        soundsLoaded = new HashSet<Integer>();
        soundSetup();
        setMainActivityListeners();

    }

    private void setMainActivityListeners(){
        //set listeners for game selection
        Button temp;
        temp = findViewById(R.id.playClassicButton);
        temp.setOnClickListener(this);
        temp = findViewById(R.id.playRewindButton);
        temp.setOnClickListener(this);
        temp = findViewById(R.id.playSurpriseButton);
        temp.setOnClickListener(this);

    }

    private void setGameboardListeners(){
        Button temp;
        temp = findViewById(R.id.mainMenuButton);
        temp.setOnClickListener(this);

        Button hs;
        hs = findViewById(R.id.highScoreButton);
        hs.setOnClickListener(this);

        // setup game board

        for (int i = 0; i < 4; i++)
            buttons[i].setOnClickListener(this);
    }

    public void onClick(View view) {


        switch (view.getId()) {

            //These 3 cases are for game selection
            case R.id.playClassicButton:
                playClassic();
                //howToPlayButton.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                break;
            case R.id.playRewindButton:
                //logo.setImageResource(R.drawable.rewind_logo);
                playClassic();
                break;
            case R.id.playSurpriseButton:
                //logo.setImageResource(R.drawable.surprise_logo);
                playSurprise();
                break;
            //These 3 are for buttons in the game board
            case R.id.mainMenuButton:
                setContentView(R.layout.activity_main);
                setMainActivityListeners();
                break;
            case R.id.highScoreButton:
                setContentView(R.layout.high_score);
                break;
            case R.id.playButton:
                //new game
                break;
            //The default is for the 4 game buttons
            default:
                for (int i = 0; i < 4; i++){
                    if (view == buttons[i]){
                        playSound(sounds[i]);
                        nextStep(i);
                    }
                }
        }
    }

    private void playClassic(){
        Log.i("Tracking", "playClassic()");
        setContentView(R.layout.game_board);
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.classic_logo);
        buttons[0] = findViewById(R.id.topLeftButton);
        buttons[1] = findViewById(R.id.topRightButton);
        buttons[2] = findViewById(R.id.bottomLeftButton);
        buttons[3] = findViewById(R.id.bottomRightButton);
        Log.i("Tracking:","Button 0: " + buttons[0].getId());
        Log.i("Tracking:","Button 1: " + buttons[1].getId());
        Log.i("Tracking:","Button 2: " + buttons[2].getId());
        Log.i("Tracking:","Button 3: " + buttons[3].getId());
        count = 0;
        game.new_game();
        game.setButtons(buttons);

        setGameboardListeners();

        // playerScore.setText("Your Score: " + game.getScore);
        // Add top score here

        playSound(gameStartId);


        game.setColors(ResourcesCompat.getColor(getResources(), R.color.darkGreen, null),
                ResourcesCompat.getColor(getResources(), R.color.lightGreen, null),
                ResourcesCompat.getColor(getResources(), R.color.darkRed, null),
                ResourcesCompat.getColor(getResources(), R.color.lightRed, null),
                ResourcesCompat.getColor(getResources(), R.color.darkYellow, null),
                ResourcesCompat.getColor(getResources(), R.color.lightYellow, null),
                ResourcesCompat.getColor(getResources(), R.color.darkBlue, null),
                ResourcesCompat.getColor(getResources(), R.color.lightBlue, null));
        game.showSequence();
    }
    private void playSurprise(){
        Log.i("Tracking", "playClassic()");
        setContentView(R.layout.game_board);
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.classic_logo);
        buttons[0] = findViewById(R.id.topLeftButton);
        buttons[0].setBackgroundColor(getResources().getColor(R.color.darkRed));
        buttons[1] = findViewById(R.id.topRightButton);
        buttons[1].setBackgroundColor(getResources().getColor(R.color.darkRed));
        buttons[2] = findViewById(R.id.bottomLeftButton);
        buttons[2].setBackgroundColor(getResources().getColor(R.color.darkRed));
        buttons[3] = findViewById(R.id.bottomRightButton);
        buttons[3].setBackgroundColor(getResources().getColor(R.color.darkRed));
        Log.i("Tracking:","Button 0: " + buttons[0].getId());
        Log.i("Tracking:","Button 1: " + buttons[1].getId());
        Log.i("Tracking:","Button 2: " + buttons[2].getId());
        Log.i("Tracking:","Button 3: " + buttons[3].getId());
        count = 0;
        game.new_game();
        game.setButtons(buttons);

        setGameboardListeners();

        // playerScore.setText("Your Score: " + game.getScore);
        // Add top score here

        playSound(gameStartId);


        game.setColors(
                ResourcesCompat.getColor(getResources(), R.color.darkRed, null),
                ResourcesCompat.getColor(getResources(), R.color.lightRed, null),
                ResourcesCompat.getColor(getResources(), R.color.darkRed, null),
                ResourcesCompat.getColor(getResources(), R.color.lightRed, null),
                ResourcesCompat.getColor(getResources(), R.color.darkRed, null),
                ResourcesCompat.getColor(getResources(), R.color.lightRed, null),
                ResourcesCompat.getColor(getResources(), R.color.darkRed, null),
                ResourcesCompat.getColor(getResources(), R.color.lightRed, null));
        game.showSequence();
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
        sounds[0] = soundPool.load(this, R.raw.sound1, 1);
        sounds[1] = soundPool.load(this, R.raw.sound2, 1);
        sounds[2] = soundPool.load(this, R.raw.sound3, 1);
        sounds[3] = soundPool.load(this, R.raw.sound4, 1);
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
        if (count < game.get_current_step()){ //partial step
            if (game.validate_next_step(step))
                count++;
            else
                endGame();
        } else if (count == game.get_current_step()) { //final step of current sequence
            if (game.validate_next_step(step)) {
                count = 0;
                game.showSequence();
            } else
                endGame();
        }
    }

    public void endGame(){
        playSound(errorId);
        //message.setVisibility(View.VISIBLE);
        // message.setText(getResources().getString(R.string.game_over));
        playSound(gameOverId);
        //setContentView(R.layout.activity_main);

    }

    // Used for all click events for main menu button
    public void mainMenu(View view) {
        setContentView(R.layout.activity_main);
    }

}