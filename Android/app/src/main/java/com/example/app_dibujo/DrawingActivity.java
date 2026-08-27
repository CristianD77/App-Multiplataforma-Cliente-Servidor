package com.example.app_dibujo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DrawingActivity extends AppCompatActivity
        implements WebSocketManager.WebSocketListener {

    private DrawingView drawingView;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawing);

        drawingView = findViewById(R.id.drawingView);
        statusText = findViewById(R.id.statusText);
        Button clearBtn = findViewById(R.id.clearBtn);

        updateStatus();

        clearBtn.setOnClickListener(v -> {
            drawingView.clearCanvas();
            if (MainActivity.connectionMode) {
                WebSocketManager.getInstance().sendClear();
            }
        });

        drawingView.setStrokeListener((x1, y1, x2, y2, color, size) -> {
            if (MainActivity.connectionMode) {
                WebSocketManager.getInstance().sendStroke(x1, y1, x2, y2, color, size);
            }
        });

        if (MainActivity.connectionMode) {
            WebSocketManager.getInstance().setListener(this);
        }
    }

    private void updateStatus() {
        if (!MainActivity.connectionMode) {
            statusText.setText("Sin conexión");
            statusText.setTextColor(Color.parseColor("#757575"));
        } else if (WebSocketManager.getInstance().isConnected()) {
            statusText.setText("Conectado");
            statusText.setTextColor(Color.parseColor("#388E3C"));
        } else {
            statusText.setText("Desconectado");
            statusText.setTextColor(Color.parseColor("#C62828"));
        }
        statusText.setTextSize(12f);
    }

    @Override
    public void onConnected() {
        runOnUiThread(this::updateStatus);
    }

    @Override
    public void onDisconnected() {
        runOnUiThread(this::updateStatus);
    }

    @Override
    public void onStrokeReceived(float x1, float y1, float x2, float y2, String color, float size) {
        runOnUiThread(() -> drawingView.drawRemoteLine(x1, y1, x2, y2, color, size));
    }

    @Override
    public void onClearReceived() {
        runOnUiThread(() -> drawingView.clearCanvas());
    }

    @Override
    public void onStartAnimation() {
        runOnUiThread(() ->
                startActivity(new Intent(this, AnimationActivity.class)));
    }

    @Override
    public void onStopAnimation() {
        // no hace nada desde DrawingActivity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketManager.getInstance().setListener(null);
    }
}