package com.example.app_dibujo;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    public interface StrokeListener {
        void onStroke(float x1, float y1, float x2, float y2, String color, float size);
    }

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paint;

    private float lastX, lastY;
    private boolean isDrawing = false;

    private StrokeListener strokeListener;

    // Constructor cuando se crea desde código
    public DrawingView(Context context) {
        super(context);
        initPaint();
    }

    // Constructor cuando se crea desde XML
    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    // Constructor adicional recomendado
    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {

        paint = new Paint();
        paint.setAntiAlias(true);

        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        paint.setColor(Color.parseColor("#222222"));

        paint.setStrokeWidth(8f);
    }

    // Se ejecuta cuando la vista conoce su tamaño
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        bitmapCanvas = new Canvas(bitmap);

        bitmapCanvas.drawColor(Color.WHITE);
    }

    // Dibuja el contenido en pantalla
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

    // Detecta el toque del usuario
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                lastX = x;
                lastY = y;

                isDrawing = true;

                break;

            case MotionEvent.ACTION_MOVE:

                if (isDrawing) {

                    drawLine(lastX, lastY, x, y, paint);

                    if (strokeListener != null) {

                        String hex = String.format("#%06X", (0xFFFFFF & paint.getColor()));

                        strokeListener.onStroke(
                                lastX,
                                lastY,
                                x,
                                y,
                                hex,
                                paint.getStrokeWidth()
                        );
                    }

                    lastX = x;
                    lastY = y;
                }

                break;

            case MotionEvent.ACTION_UP:

                isDrawing = false;

                break;
        }

        return true;
    }

    private void drawLine(float x1, float y1, float x2, float y2, Paint p) {

        if (bitmapCanvas == null) return;

        bitmapCanvas.drawLine(x1, y1, x2, y2, p);

        invalidate();
    }

    // Dibujo recibido por red
    public void drawRemoteLine(float x1, float y1, float x2, float y2, String color, float size) {

        Paint remotePaint = new Paint(paint);

        remotePaint.setColor(Color.parseColor(color));

        remotePaint.setStrokeWidth(size);

        drawLine(x1, y1, x2, y2, remotePaint);
    }

    // Limpiar canvas
    public void clearCanvas() {

        if (bitmapCanvas != null) {

            bitmapCanvas.drawColor(Color.WHITE);

            invalidate();
        }
    }

    public void setStrokeListener(StrokeListener listener) {
        this.strokeListener = listener;
    }

    public void setBrushColor(int color) {
        paint.setColor(color);
    }

    public void setBrushSize(float size) {
        paint.setStrokeWidth(size);
    }
}
