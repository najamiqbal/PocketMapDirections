package com.example.pocketmapdirections;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.pocketmapdirections.fragments.GoSpeedo;

public class SpeedoMeter extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speedo_meter_activity);
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerspeedo,new GoSpeedo()).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
