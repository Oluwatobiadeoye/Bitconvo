package com.intermediate.android.bitconvo;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NetworkProblem extends AppCompatActivity {

    private TextView retry, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_error);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        retry = (TextView) findViewById(R.id.retry);
        settings = (TextView) findViewById(R.id.settings);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NetworkProblem.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(Settings.ACTION_SETTINGS));
            }
        });

    }
}
