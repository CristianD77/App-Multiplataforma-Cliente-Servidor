package com.example.app_dibujo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DrawMenuActivity extends AppCompatActivity {

    private TextView modeIndicator;
    private Button btnDraw, btnAnimate, btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_menu);

        modeIndicator = findViewById(R.id.modeIndicator);
        btnDraw = findViewById(R.id.btnDraw);
        btnAnimate = findViewById(R.id.btnAnimate);
        btnClose = findViewById(R.id.btnClose);

        if (MainActivity.connectionMode) {
            modeIndicator.setText("Modo Conexión activo");
            modeIndicator.setTextColor(Color.parseColor("#1565C0"));
        } else {
            modeIndicator.setText("Modo Sin Conexión");
            modeIndicator.setTextColor(Color.parseColor("#757575"));
        }

        btnDraw.setOnClickListener(v ->
                startActivity(new Intent(this, DrawingActivity.class)));

        btnAnimate.setOnClickListener(v -> {
            if (MainActivity.connectionMode) {
                WebSocketManager.getInstance().sendMessage("{\"type\":\"start_animation\"}");
            }
            startActivity(new Intent(this, AnimationActivity.class));
        });

        btnClose.setOnClickListener(v -> {
            WebSocketManager.getInstance().disconnect();
            finishAffinity();
        });
    }
}