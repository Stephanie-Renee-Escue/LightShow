package com.stephanieescue.lightshow;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LightShow {
    //declarations
    private List<Integer> steps = new ArrayList<Integer>();
    private List<Integer> userSteps = new ArrayList<Integer>();
    private Button topLeftButton, topRightButton, bottomLeftButton, bottomRightButton;
    private int topLeftDark, topLeftLight, topRightDark, topRightLight;
    private int bottomLeftDark, bottomLeftLight, bottomRightDark, bottomRightLight;
    private Handler handler;
    public int score=0;

    //default constructor
    public LightShow(){
        generate_sequence(25);
    }

    //constructor with number of steps
    public LightShow(int steps){
        generate_sequence(steps);
    }

    //constructor with views for the colors
    public void setButtons(Button topLeftView, Button topRightView, Button bottomLeftView, Button bottomRightView){
        topLeftButton = topLeftView;
        topRightButton = topRightView;
        bottomLeftButton = bottomLeftView;
        bottomRightButton = bottomRightView;
    }

    public void generate_sequence(int limit) {
        Random random = new Random();
        for (int i = 0; i < limit; i++){
            steps.add(random.nextInt(4)+1);
            Log.d("Value", "Step " + i + ": " + steps.get(i).toString());
        }
        if (limit >= 3) // make sure that the same pattern isn't repeated 3 times.
            for (int i = 3; i < steps.size(); i++) {
                if ((steps.get(i) == steps.get(i-1)) && (steps.get(i) == steps.get(i-2)) && (steps.get(i) == steps.get(i-3)))
                    steps.set(i, random.nextInt(4));
            }
    }

    public void new_game(){
        steps.clear();
        userSteps.clear();
        generate_sequence(25);
    }

    public List<Integer> get_sequence(int count){
        return steps.subList(0,count);
    }

    public boolean validate_next_step(int current_step){
        boolean isCorrect = false;

        if (steps.get(userSteps.size()) == current_step) {
            userSteps.add(current_step);
            isCorrect = true;
        }
        return isCorrect;
    }

    public void showSequence(){
        Handler handler = new Handler();
        for (int i = 0; i <= userSteps.size(); i++ ){
            Log.d("Steps", "Number of steps: " + steps.size() + " Current step: " + i);
            switch (steps.get(i)){
                case 1:
                    topLeftButton.setPressed(true);
                    /*topLeft.setBackgroundColor(topLeftLight);
                    topLeft.animate().setDuration(1000).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // set color back to normal
                            topLeft.setBackgroundColor(topLeftDark);
                        }
                    }).start();*/
                    Runnable runnableTopLeft = new Runnable(){
                        @Override
                        public void run() {
                            // set color back to normal
                            topLeftButton.setBackgroundColor(topLeftDark);
                        }
                    };
                    //topLeft.animate().setDuration(1000).withEndAction(runnableTopLeft).start();
                    handler.postDelayed(runnableTopLeft, 1000);
                    break;
                case 2:
                    topRightButton.setPressed(true);
                    /*topRight.animate().setDuration(1000).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // set color back to normal
                            topRight.setBackgroundColor(topRightDark);
                        }
                    }).start();*/
                    Runnable runnableTopRight = new Runnable(){
                        @Override
                        public void run() {
                            // set color back to normal
                            topRightButton.setBackgroundColor(topRightDark);
                        }
                    };
                    //topRight.animate().setDuration(1000).withEndAction(runnableTopRight).start();
                    handler.postDelayed(runnableTopRight, 1000);
                    break;
                case 3:
                    bottomLeftButton.setPressed(true);
                    /*bottomLeft.animate().setDuration(1000).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // set color back to normal
                            bottomLeft.setBackgroundColor(bottomLeftDark);
                        }
                    }).start();*/
                    Runnable runnableBottomLeft = new Runnable(){
                        @Override
                        public void run() {
                            // set color back to normal
                            bottomLeftButton.setBackgroundColor(bottomLeftDark);
                        }
                    };
                    //bottomLeft.animate().setDuration(1000).withEndAction(runnableBottomLeft).start();
                    handler.postDelayed(runnableBottomLeft, 1000);
                    break;
                case 4:
                    bottomRightButton.setPressed(true);
                    /*bottomRight.animate().setDuration(1000).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // set color back to normal
                            bottomRight.setBackgroundColor(bottomRightDark);
                        }
                    }).start();*/
                    Runnable runnableBottomRight = new Runnable(){
                        @Override
                        public void run() {
                            // set color back to normal
                            bottomRightButton.setBackgroundColor(bottomRightDark);
                        }
                    };
                    //bottomRight.animate().setDuration(1000).withEndAction(runnableBottomRight).start();
                    handler.postDelayed(runnableBottomRight, 1000);

                    break;
            }
        }
    }


    public void setColors(int oneDark, int oneLight, int twoDark, int twoLight,
                          int threeDark, int threeLight, int fourDark, int fourLight){
        topLeftDark = oneDark;
        topLeftLight = oneLight;
        topRightDark = twoDark;
        topRightLight = twoLight;
        bottomLeftDark = threeDark;
        bottomLeftLight = threeLight;
        bottomRightDark = fourDark;
        bottomRightLight = fourLight;
    }
}