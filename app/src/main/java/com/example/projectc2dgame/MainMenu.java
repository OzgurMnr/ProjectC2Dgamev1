package com.example.projectc2dgame;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainMenu extends AppCompatActivity {
    int x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void goOptions(View view) {
        Intent intent = new Intent(this, Options.class);
        startActivity(intent);
    }
    public void Play(View view) {
        Intent intent = new Intent(this, Sections.class);
        startActivity(intent);
    }
    public void goLeader(View view) {
        Intent intent = new Intent(this, LeaderBoard.class);
        startActivity(intent);
    }
    public void quit(View view) {
        finishAffinity(); // Tüm activity'leri sonlandırır
        System.exit(0);   // JVM'i durdurur
    }
}