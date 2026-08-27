package com.example.app_dibujo;

import okhttp3.*;
import org.json.JSONObject;

public class WebSocketManager {

    public interface WebSocketListener {
        void onConnected();
        void onDisconnected();
        void onStrokeReceived(float x1, float y1, float x2, float y2, String color, float size);
        void onClearReceived();
        void onStartAnimation();
        void onStopAnimation();
    }

    private static WebSocketManager instance;
    private OkHttpClient client;
    private WebSocket webSocket;
    private WebSocketListener listener;
    private boolean connected = false;

    private WebSocketManager() {
        client = new OkHttpClient();
    }

    public static WebSocketManager getInstance() {
        if (instance == null) instance = new WebSocketManager();
        return instance;
    }

    public void setListener(WebSocketListener listener) {
        this.listener = listener;
    }

    public void connect(String url) {
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new okhttp3.WebSocketListener() {

            @Override
            public void onOpen(WebSocket ws, Response response) {
                connected = true;
                if (listener != null) listener.onConnected();
            }

            @Override
            public void onMessage(WebSocket ws, String text) {
                try {
                    JSONObject data = new JSONObject(text);
                    String type = data.getString("type");
                    if (type.equals("stroke") && listener != null) {
                        listener.onStrokeReceived(
                                (float) data.getDouble("x1"),
                                (float) data.getDouble("y1"),
                                (float) data.getDouble("x2"),
                                (float) data.getDouble("y2"),
                                data.getString("color"),
                                (float) data.getDouble("size")
                        );
                    } else if (type.equals("clear") && listener != null) {
                        listener.onClearReceived();
                    } else if (type.equals("start_animation") && listener != null) {
                        listener.onStartAnimation();
                    } else if (type.equals("stop_animation") && listener != null) {
                        listener.onStopAnimation();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(WebSocket ws, Throwable t, Response response) {
                connected = false;
                if (listener != null) listener.onDisconnected();
            }

            @Override
            public void onClosed(WebSocket ws, int code, String reason) {
                connected = false;
                if (listener != null) listener.onDisconnected();
            }
        });
    }

    public void sendStroke(float x1, float y1, float x2, float y2, String color, float size) {
        if (!connected || webSocket == null) return;
        try {
            JSONObject msg = new JSONObject();
            msg.put("type", "stroke");
            msg.put("x1", x1); msg.put("y1", y1);
            msg.put("x2", x2); msg.put("y2", y2);
            msg.put("color", color);
            msg.put("size", size);
            webSocket.send(msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendClear() {
        if (!connected || webSocket == null) return;
        try {
            JSONObject msg = new JSONObject();
            msg.put("type", "clear");
            webSocket.send(msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        if (!connected || webSocket == null) return;
        webSocket.send(msg);
    }

    public void disconnect() {
        if (webSocket != null) webSocket.close(1000, "App cerrada");
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }
}