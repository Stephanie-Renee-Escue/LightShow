package com.stephanieescue.lightshow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.EventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playClassic = findViewById(R.id.playClassicButton);
        playClassic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameSelectionListener(view.getId());
            }
        });

    }

    LightShow game = new LightShow();
    private void gameSelectionListener(int buttonID){
        switch (buttonID){
            case R.id.playClassicButton:
                playClassic();
                break;
        }

    }

    private void playClassic(){
        setContentView(R.layout.game_board);
        game.setButtons((Button)findViewById(R.id.topLeftButton), (Button)findViewById(R.id.topRightButton),
                (Button)findViewById(R.id.bottomLeftButton), (Button)findViewById(R.id.bottomRightButton));
        setClassicListeners();
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

    private void setClassicListeners(){
        Button tempButton;
        tempButton = findViewById(R.id.topLeftButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextStep(1);
            }
        });
        tempButton = findViewById(R.id.topRightButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextStep(2);
            }
        });
        tempButton = findViewById(R.id.bottomLeftButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextStep(3);
            }
        });
        tempButton = findViewById(R.id.bottomRightButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextStep(4);
            }
        });

    }

    public void nextStep(int step){
        if (game.validate_next_step(step))
            game.showSequence();
        else {
            //game over sound can be played here
            setContentView(R.layout.activity_main);
        }
    }
}
