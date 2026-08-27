package com.example.app_dibujo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER_URL = "ws://192.168.20.8:8765";
    public static boolean connectionMode = false;

    private Button Bt1, Bt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); // Cargar el XML

        Bt1 = findViewById(R.id.button);
        Bt2 = findViewById(R.id.button2);

        /* boton modo conexion */
        Bt1.setOnClickListener(v -> {
            connectionMode = true;
            WebSocketManager.getInstance().connect(SERVER_URL);

            new android.os.Handler().postDelayed(() ->
                    startActivity(new Intent(this, DrawMenuActivity.class)), 800);
        });

        /* boton modo sin conexion */
        Bt2.setOnClickListener(v -> {
            connectionMode = false;
            startActivity(new Intent(this, DrawMenuActivity.class));
        });
    }
}