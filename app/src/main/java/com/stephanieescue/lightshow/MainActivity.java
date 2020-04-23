package com.stephanieescue.lightshow;

// Team Members: Joshua Foster, Lionel Sosa Estrada, and Stephanie Escue

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
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
    final private int MAX_STEPS = 25;
    String[] highScoreNames = new String[11];
    int[] highScoreValues = new int[11];
    String gameType;

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

    @Override
    protected void onPause(){
        super.onPause();

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
        temp = findViewById(R.id.playButton);
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
                PauseForIntro playIntroClassic = new PauseForIntro("Classic");
                playIntroClassic.execute();
                break;
            case R.id.playRewindButton:
                playClassic();
                break;
            case R.id.playSurpriseButton:
                PauseForIntro playIntroSurprise = new PauseForIntro("Surprise");
                playIntroSurprise.execute();
                break;

            //These 3 are for buttons in the game board
            case R.id.mainMenuButton:
            case R.id.mainMenuHighScores:
                setContentView(R.layout.activity_main);
                setMainActivityListeners();
                break;
            case R.id.highScoreButton:
                showHighScores();
                break;
            case R.id.playButton:
                if (gameType.equals("Classic")) {
                    PauseForIntro rePlayIntroClassic = new PauseForIntro("Classic");
                    rePlayIntroClassic.execute();
                } else if (gameType.equals("Surprise")) {
                    PauseForIntro rePlayIntroSurprise = new PauseForIntro("Surprise");
                    rePlayIntroSurprise.execute();
                }
                break;

             // These 3 buttons show the game instructions
            case R.id.classicInstructionsButton:
                setContentView(R.layout.classic_instructions);
                break;
            case R.id.rewindInstructionsButton:
                setContentView(R.layout.rewind_instructions);
                break;
            case R.id.surpriseInstructionsButton:
                setContentView(R.layout.surprise_instructions);
                break;

            //this is the button in high score
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

    private void backToGame() {
        return;
    }

    private void playClassic(){
        gameType = "Classic";
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.classic_logo);
        buttons[0] = findViewById(R.id.topLeftButton);
        buttons[1] = findViewById(R.id.topRightButton);
        buttons[2] = findViewById(R.id.bottomLeftButton);
        buttons[3] = findViewById(R.id.bottomRightButton);
        sounds[0] = soundPool.load(this, R.raw.sound1, 1);
        sounds[1] = soundPool.load(this, R.raw.sound2, 1);
        sounds[2] = soundPool.load(this, R.raw.sound3, 1);
        sounds[3] = soundPool.load(this, R.raw.sound4, 1);

        count = 0;
        game.new_game();
        game.setButtons(buttons);

        setGameboardListeners();

        // playerScore.setText("Your Score: " + game.getScore);
        // Add top score here

        game.setColors(ResourcesCompat.getColor(getResources(), R.color.darkGreen, null),
                ResourcesCompat.getColor(getResources(), R.color.lightGreen, null),
                ResourcesCompat.getColor(getResources(), R.color.darkRed, null),
                ResourcesCompat.getColor(getResources(), R.color.lightRed, null),
                ResourcesCompat.getColor(getResources(), R.color.darkYellow, null),
                ResourcesCompat.getColor(getResources(), R.color.lightYellow, null),
                ResourcesCompat.getColor(getResources(), R.color.darkBlue, null),
                ResourcesCompat.getColor(getResources(), R.color.lightBlue, null));
        game.setSounds(R.raw.sound1, R.raw.sound2, R.raw.sound3, R.raw.sound4);
        game.showSequence();
    }

    private void playSurprise(){
        //setContentView(R.layout.game_board);
        gameType="Surprise";
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.surprise_logo);

        buttons[0] = findViewById(R.id.topLeftButton);
        buttons[1] = findViewById(R.id.topRightButton);
        buttons[2] = findViewById(R.id.bottomLeftButton);
        buttons[3] = findViewById(R.id.bottomRightButton);
        sounds[0] = soundPool.load(this, R.raw.sound2, 1);
        sounds[1] = soundPool.load(this, R.raw.sound2, 1);
        sounds[2] = soundPool.load(this, R.raw.sound2, 1);
        sounds[3] = soundPool.load(this, R.raw.sound2, 1);

        count = 0;
        game.new_game();
        game.setButtons(buttons);

        setGameboardListeners();

        // playerScore.setText("Your Score: " + game.getScore);
        // Add top score here

        //playSound(gameStartId);


        game.setColors(
                ResourcesCompat.getColor(getResources(), R.color.darkRed, null),
                ResourcesCompat.getColor(getResources(), R.color.lightRed, null),
                ResourcesCompat.getColor(getResources(), R.color.darkRed, null),
                ResourcesCompat.getColor(getResources(), R.color.lightRed, null),
                ResourcesCompat.getColor(getResources(), R.color.darkRed, null),
                ResourcesCompat.getColor(getResources(), R.color.lightRed, null),
                ResourcesCompat.getColor(getResources(), R.color.darkRed, null),
                ResourcesCompat.getColor(getResources(), R.color.lightRed, null));
        game.setSounds(R.raw.sound2, R.raw.sound2, R.raw.sound2, R.raw.sound2);
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
        int step_count = game.get_current_step();
        if (count < step_count){ //partial step
            if (game.validate_previous_step(count, step))
                count++;
            else
                endGame();
        } else { //final step of current sequence
            if (step_count == MAX_STEPS) {//user reached max number of steps
                if (game.validate_next_step(step))
                    winGame();
            } else { //max number of current sequence
                if (game.validate_next_step(step)) {
                    count = 0;
                    game.showSequence();
                } else
                    endGame();
            }
        }
    }

    private void winGame(){
        PauseGameOver gameOver = new PauseGameOver("win");
        gameOver.execute();
    }

    public void endGame(){
        PauseGameOver gameOver = new PauseGameOver("lose");
        gameOver.execute();
    }

    // Used for all click events for main menu button
    public void mainMenu(View view) {
        setContentView(R.layout.activity_main);
    }

    //Thread to pause game while the intro song plays
    class PauseForIntro extends AsyncTask<Void, Void, Void> {
        private String gameType;

        //Constructor that receives the game type to start
        public PauseForIntro(String game){
            gameType = game;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setContentView(R.layout.game_board);
            playSound(gameStartId);
         }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            switch (gameType) {
                case "Classic":
                    playClassic();
                    break;
                case "Surprise":
                    playSurprise();
                    break;
            }
        }
    }

    //thread to pause game while gave over sounds play
    class PauseGameOver extends AsyncTask<Void, Void, Void> {
        private String gameResult;

        public PauseGameOver(String result){
            gameResult = result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (gameResult.equals("lose"))
                playSound(errorId);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // play sound depending on result
            if (gameResult.equals("lose")){
                playSound(gameOverId);
            } else if (gameResult.equals("win")) {
                playSound(gameStartId);
            }

            int score = game.get_current_step();
            // read high scores or write new file
            if (readHighScores()){
                if (score >= highScoreValues[9]){ //player scored higher than records
                    addScore(game.get_current_step());
                }
            } else {
                createScores();
                Boolean temp = readHighScores();
                addScore(game.get_current_step());
            }
            game.disableButtons();
        }
    }

    private void createScores(){
        FileOutputStream fos;
        try {
            fos = openFileOutput("highScores.txt", Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(bw);
            for (int i = 0; i < 10; i++){
                pw.println("null");
                pw.println("0");
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void saveScores(){
        FileOutputStream fos;
        try {
            fos = openFileOutput("highScores.txt", Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(bw);
            for (int i = 0; i < 10; i++){
                pw.println(highScoreNames[i]);
                pw.println(highScoreValues[i]);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    private void addScore(int newScore){
        int newPosition = 0;
        for (int i = 0; i < 10; i++){
            if (newScore >= highScoreValues[newPosition])
                break;
        }
        /*while ((newScore >= highScoreValues[newPosition - 1]) && (newPosition > 0)) {
            Log.i("Tracking", newPosition + ": " + newScore + ">=" + highScoreValues[newPosition]);
            newPosition--;
        }*/
        for (int i = newPosition; i < 10; i++){
            highScoreValues[i+1] = highScoreValues[i];
            highScoreNames[i+1] = highScoreNames[i];
        }
        highScoreValues[newPosition] = newScore;
        getNewName(newPosition);

    }

    //inflate dialog to get player's name
    private void getNewName(int position){

        final int pos = position;
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.high_score_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                highScoreNames[pos] = userInput.getText().toString();
                                saveScores();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    private void setHighScoreListener(){
        Button temp = findViewById(R.id.mainMenuHighScores);
        temp.setOnClickListener(this);
    }

    private boolean readHighScores(){
        try { //tries to open file with high scores
            FileInputStream fis = openFileInput("highScores.txt");
            Scanner scanner = new Scanner(fis);
            for (int i = 0; i < 10; i++){
                highScoreNames[i] = scanner.nextLine();
                if (highScoreNames[i] == "null"){
                    highScoreNames[i] = "";
                }
                highScoreValues[i] = Integer.parseInt(scanner.nextLine());
            }
            scanner.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private void showHighScores(){
        setContentView(R.layout.high_score);
        String[] highScoresList = new String[10];
        for (int i = 0; i < 10; i++)
            highScoresList[i] = highScoreValues[i] + " - " + highScoreNames[i];
        ListView listView = findViewById(R.id.highScoreListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,highScoresList);
        listView.setAdapter(adapter);
        setHighScoreListener();
    }
}