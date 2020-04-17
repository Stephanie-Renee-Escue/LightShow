package com.stephanieescue.lightshow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class LightShow {
    //declarations
    private List<Integer> steps = new ArrayList<Integer>();
    private List<Integer> userSteps = new ArrayList<Integer>();
    public int score=0;
    private Button[] buttons = new Button[4];
    private int[] darkColors = new int[4];
    private int[] lightColors = new int[4];
    private final int MAX_STEPS = 25;
    private Context context;


    //default constructor
    public LightShow(){
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

    public void showSequence() {
        /*Handler handler = new Handler();
        for (int i = 0; i <= userSteps.size(); i++ ){
            Log.d("Steps", "Number of steps: " + steps.size() + " Current step: " + i);
            switch (steps.get(i)){
                case 1:
                    topLeftButton.setPressed(true);
                    Runnable runnableTopLeft = new Runnable(){
                        @Override
                        public void run() {
                            // set color back to normal
                            topLeftButton.setBackgroundColor(topLeftDark);
                        }
                    };
                    handler.postDelayed(runnableTopLeft, 1000);
                    break;
                case 2:
                    topRightButton.setPressed(true);
                    Runnable runnableTopRight = new Runnable(){
                        @Override
                        public void run() {
                            // set color back to normal
                            topRightButton.setBackgroundColor(topRightDark);
                        }
                    };
                    handler.postDelayed(runnableTopRight, 1000);
                    break;
                case 3:
                    bottomLeftButton.setPressed(true);
                    Runnable runnableBottomLeft = new Runnable(){
                        @Override
                        public void run() {
                            // set color back to normal
                            bottomLeftButton.setBackgroundColor(bottomLeftDark);
                        }
                    };
                    handler.postDelayed(runnableBottomLeft, 1000);
                    break;
                case 4:
                    bottomRightButton.setPressed(true);
                    Runnable runnableBottomRight = new Runnable(){
                        @Override
                        public void run() {
                            // set color back to normal
                            bottomRightButton.setBackgroundColor(bottomRightDark);
                        }
                    };
                    handler.postDelayed(runnableBottomRight, 1000);
                    break;
            }
        }*/
        steps.add(generate_step());
        //Log.i("Tracking", "Added " + steps.get(steps.size()-1) + " Total Steps: " + steps.size());

        PauseThread pause = new PauseThread();
        pause.execute();

    }

    private void disableButtons(){
        for (int i = 0; i < 4; i++){
            buttons[i].setClickable(false);
        }
    }

    private void enableButtons(){
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
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[steps.get(ind)].setBackgroundColor(lightColors[steps.get(ind)]);
                        }
                    });

                    Log.i("Tracking", "showSequence to sleep");
                    Thread.sleep(1500);
                    Log.i("Tracking", "showSequence from sleep");

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[steps.get(ind)].setBackgroundColor(darkColors[steps.get(ind)]);
                        }
                    });
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
        }
    }
}