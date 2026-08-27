package com.example.app_dibujo;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AnimationActivity extends AppCompatActivity {

    private ParabolaView parabolaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        parabolaView = findViewById(R.id.parabolaView);

        Button btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> {
            if (MainActivity.connectionMode) {
                WebSocketManager.getInstance().sendMessage("{\"type\":\"stop_animation\"}");
            }
            finish();
        });

        // Avisar al PC que inicie la animación
        if (MainActivity.connectionMode) {
            WebSocketManager.getInstance().sendMessage("{\"type\":\"start_animation\"}");
        }
    }
}
