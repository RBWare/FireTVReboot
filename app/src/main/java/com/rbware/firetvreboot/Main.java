package com.rbware.firetvreboot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;


public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot now"});
        } catch (IOException e){
            Toast.makeText(this, "Could not reboot your device", Toast.LENGTH_SHORT).show();
        }
    }
}
