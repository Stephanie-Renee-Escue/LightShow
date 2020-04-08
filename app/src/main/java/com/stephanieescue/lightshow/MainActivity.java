package com.stephanieescue.lightshow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.EventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, EventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    @Override
    public void onClick(View view) {

    }
}
