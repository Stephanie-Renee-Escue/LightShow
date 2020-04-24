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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
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
    public TextView message, playerScore, highScore, credits, viewCredits;
    LightShow game;
    int count;
    final private int MAX_STEPS = 24; // counting [0]
    String gameType;
    List<String> highScoreNames = new ArrayList<String>();
    List<Integer> highScoreValues = new ArrayList<Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        game = new LightShow(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assigning all TextViews
        message = findViewById(R.id.messageTV);
        playerScore = findViewById(R.id.playerScoreTV);
        highScore = findViewById(R.id.topScoreTV);
        credits = findViewById(R.id.creditsTV);
        viewCredits = findViewById(R.id.viewCreditsTV);

        soundsLoaded = new HashSet<Integer>();
        soundSetup();
        setMainActivityListeners();

    }

    @Override
    protected void onPause(){
        super.onPause();
        game.stopThread();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_main);
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
                PauseForIntro playIntroRewind = new PauseForIntro("Rewind");
                playIntroRewind.execute();
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
                }else if (gameType.equals("Rewind")) {
                    PauseForIntro rePlayIntroRewind = new PauseForIntro("Rewind");
                    rePlayIntroRewind.execute();
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

             // Clickable text view
            case R.id.viewCreditsTV:
                setContentView(R.layout.credits);
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

    private void playRewind(){
        gameType = "Rewind";
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
        if (gameType == "Rewind") {
            int step_count = game.get_current_step();
            if (count < step_count) { //partial step
                if (game.validate_previous_step((step_count - count), step))
                    count++;
                else
                    endGame();
            } else { //final step of current sequence
                if (step_count == MAX_STEPS) {//user reached max number of steps
                    if (game.validate_previous_step((step_count - count), step))
                        winGame();
                } else { //max number of current sequence
                    if (game.validate_previous_step((step_count - count), step)) {
                        count = 0;
                        game.showSequence();
                        TextView tv = findViewById(R.id.playerScoreTV);
                        tv.setText("Your Score: " + (step_count + 1));

                    } else
                        endGame();
                }
            }

        } else { //for Classic and Surprise
            int step_count = game.get_current_step();
            if (count < step_count) { //partial step
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
                        TextView tv = findViewById(R.id.playerScoreTV);
                        tv.setText("Your Score: " + (step_count + 1));
                    } else
                        endGame();
                }
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
            loadHighScore();
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
                case "Rewind":
                    playRewind();
                    break;
            }
        }
    }

    private void loadHighScore(){
        if (readHighScores()){
            TextView tv = findViewById(R.id.topScoreTV);
            tv.setText("Top Score: " + highScoreValues.get(0));
            tv = findViewById(R.id.playerScoreTV);
            tv.setText("Your Score:");
            highScoreValues.clear();
            highScoreNames.clear();
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
                if ((score >= highScoreValues.get(highScoreValues.size() -1 )) || (highScoreValues.size() < 10)){ //player scored higher than records
                    addScore(score);
                }
            } else {
                addScore(score);
            }
            game.disableButtons();
        }
    }


    private void saveScores(){
        //First sort the array
        while (highScoreValues.size() > 10){ //limit top scores to 10
            highScoreValues.remove(highScoreValues.size() - 1);
            highScoreNames.remove(highScoreNames.size() - 1);
        }
        if (highScoreValues.size() >= 2) { //only sort if more than 2 scores
            for (int i = 0; i < highScoreValues.size()-1; i++)
                for (int j = i + 1 ; j < highScoreValues.size(); j++)
                    if (highScoreValues.get(j) > highScoreValues.get(i)){
                        Log.i("Tracking", highScoreValues.get(j) + ">" + highScoreValues.get(i));
                        int tempInt = highScoreValues.get(i);
                        highScoreValues.set(i, highScoreValues.get(j));
                        highScoreValues.set(j, tempInt);
                        String tempString = highScoreNames.get(i);
                        highScoreNames.set(i, highScoreNames.get(j));
                        highScoreNames.set(j, tempString);
                    }
        }
        try {
            FileOutputStream fos;
            fos = openFileOutput("highScores.txt", Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(bw);
            for (int i = 0; i < highScoreNames.size(); i++){
                pw.println(highScoreNames.get(i));
                pw.println(highScoreValues.get(i));
            }
            pw.close();
            highScoreNames.clear();
            highScoreValues.clear();
        } catch (FileNotFoundException e) {
            Log.e("Exception", "File not read " + e.getMessage());
        }
    }


    private void addScore(int newScore){
        highScoreValues.add(newScore);
        getNewName();
    }

    //inflate dialog to get player's name
    private void getNewName(){
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
                                highScoreNames.add(userInput.getText().toString());
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
            while (scanner.hasNext()){
                highScoreNames.add(scanner.nextLine());
                highScoreValues.add(Integer.parseInt(scanner.nextLine()));
            }
            scanner.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private void showHighScores(){
        setContentView(R.layout.high_score);
        if (readHighScores()) {
            String[] highScoresList = new String[highScoreValues.size()];
            for (int i = 0; i < highScoreValues.size(); i++) {
                highScoresList[i] = highScoreValues.get(i) + " - " + highScoreNames.get(i);
                Log.i("Tracking", "Read " + highScoreValues.size() + "records");
            }
            ListView listView = findViewById(R.id.highScoreListView);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,highScoresList);
            listView.setAdapter(adapter);
            //((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            setHighScoreListener();
            highScoreValues.clear();
            highScoreNames.clear();

        } else {
            String[] highScoresList = new String[1];
            highScoresList[0] = "No high scores yet";
            ListView listView = findViewById(R.id.highScoreListView);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,highScoresList);
            listView.setAdapter(adapter);
            setHighScoreListener();
        }
    }
}