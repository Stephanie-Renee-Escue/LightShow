package com.stephanieescue.lightshow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class LightShow {
    //declarations
    private List<Integer> steps = new ArrayList<Integer>();
    private List<Integer> userSteps = new ArrayList<Integer>();
    public int score=0;
    private Button[] buttons = new Button[4];
    private int[] darkColors = new int[4];
    private int[] lightColors = new int[4];
    private Context context;
    private SoundPool soundPool;
    private Set<Integer> soundsLoaded;
    private int[] sounds = new int[4];
    private int[] soundsIds = new int[4];

    //default constructor
    public LightShow(){
        soundsLoaded = new HashSet<Integer>();
        soundSetupClass();
    }

    public LightShow(Context context){
        this.context = context;
    }

    //constructor with views for the colors
    public void setButtons(Button[] incoming_buttons){
        for (int i = 0; i < 4; i++)
            buttons[i] = incoming_buttons[i];
    }

    public int get_current_step(){
        return (steps.size() - 1);
    }

    private int generate_step() {
        Random random = new Random();
        return random.nextInt(4);
    }

    public void new_game(){
        steps.clear();
        userSteps.clear();
    }

    public boolean validate_next_step(int current_step){
        if (steps.get(steps.size()-1) == current_step) {
            userSteps.add(current_step);
            return true;
        }
        return false;
    }

    public boolean validate_previous_step(int position, int current_step){
        if (steps.get(position) == current_step) {
            userSteps.add(current_step);
            return true;
        }
        return false;
    }

    PauseThread pause;

    public void showSequence() {
        steps.add(generate_step());

        //Changing color in a new thread
        pause = new PauseThread();
        pause.execute();
    }

    public void stopThread(){
        pause.cancel(true);
    }

    public void disableButtons(){
        for (int i = 0; i < 4; i++){
            buttons[i].setClickable(false);
        }
    }

    public void enableButtons(){
        for (int i = 0; i < 4; i++){
            buttons[i].setClickable(true);
        }
    }


    public void setColors(int oneDark, int oneLight, int twoDark, int twoLight,
                          int threeDark, int threeLight, int fourDark, int fourLight){
        darkColors[0] = oneDark;
        lightColors[0] = oneLight;
        darkColors[1] = twoDark;
        lightColors[1] = twoLight;
        darkColors[2] = threeDark;
        lightColors[2] = threeLight;
        darkColors[3] = fourDark;
        lightColors[3] = fourLight;

        //set background back to the drawable resource
        buttons[0].setBackgroundColor(darkColors[0]);
        buttons[1].setBackgroundColor(darkColors[1]);
        buttons[2].setBackgroundColor(darkColors[2]);
        buttons[3].setBackgroundColor(darkColors[3]);
    }

    public void setSounds (int sound1, int sound2, int sound3, int sound4) {
        soundsIds[0] = sound1;
        soundsIds[1] = sound2;
        soundsIds[2] = sound3;
        soundsIds[3] = sound4;

        soundsLoaded = new HashSet<Integer>();
        soundSetupClass();
        soundsLoaded = new HashSet<Integer>();
        soundSetupClass();
    }


    class PauseThread extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            disableButtons();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                for (int i = 0; i < steps.size(); i++) {
                    final int ind = i;
                    //short pause after user clicks color
                    if (i == 0) {
                        Thread.sleep(400);
                    }
                    playSound(sounds[steps.get(ind)]);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[steps.get(ind)].setBackgroundColor(lightColors[steps.get(ind)]);
                        }
                    });

                    Thread.sleep(850);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[steps.get(ind)].setBackgroundColor(darkColors[steps.get(ind)]);
                        }
                    });

                    Thread.sleep(250); //short pause to see if a button is pressed twice in a row
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            enableButtons();
            if (soundsIds[0] == soundsIds[1]){ //playing surprise
                for (int i = 0; i < 4; i++)
                    buttons[i].setBackgroundResource(R.drawable.toprightbutton);
            } else {
                //set background back to the drawable resource
                buttons[0].setBackgroundResource(R.drawable.topleftbutton);
                buttons[1].setBackgroundResource(R.drawable.toprightbutton);
                buttons[2].setBackgroundResource(R.drawable.bottomleftbutton);
                buttons[3].setBackgroundResource(R.drawable.bottomrightbutton);
            }
        }
    }

    private void soundSetupClass() {

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
                    Log.i("SOUND", "Sound loaded in class " + sampleId);
                } else {
                    Log.i("SOUND", "Error cannot load sound status = " + status);
                }
            }
        });

        // Assigning sounds
        sounds[0] = soundPool.load((Activity) context, soundsIds[0], 1);
        sounds[1] = soundPool.load((Activity) context, soundsIds[1], 1);
        sounds[2] = soundPool.load((Activity) context, soundsIds[2], 1);
        sounds[3] = soundPool.load((Activity) context, soundsIds[3], 1);
    }

    private void playSound(int soundId) {
        if (soundsLoaded.contains(soundId)) {
            soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

}