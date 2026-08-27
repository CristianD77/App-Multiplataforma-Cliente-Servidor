package com.example.app_dibujo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ParabolaView extends View {

        /*variables de la animacion*/
    private float t = 0f;
    private boolean forward = true;
    private Paint ballPaint;

        /*constructores de las vistas*/
    //Se usa cuando la vista se crea desde código Java.
    public ParabolaView(Context context) {
        super(context);
        init();
    }
    //Se usa cuando la vista se crea desde XML.
    public ParabolaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    //Permite aplicar estilos o temas.
    public ParabolaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //METODO PARA INICIARLIZAR VAIRALBES
    private void init() {

        ballPaint = new Paint();//objeto de dibujo crear pincel
        ballPaint.setColor(Color.BLUE); //color de la bola
        ballPaint.setAntiAlias(true);//quita los pixeles de los bordes

        startAnimation();//inicia la animacion
    }

    //metodo animacion control del movimiento
    private void startAnimation() {
        //ejecuta cada frame 60fps
        postOnAnimation(new Runnable() {
            @Override
            //ejecuta el bloque constantemente
            public void run() {

                float speed = 0.005f;//velocidad de la animacion
                //inicia el movimiento hacia adelante
                if (forward) {
                    t += speed;
                    if (t >= 1f) forward = false;//cambia la direccion cuando llega a 1
                } else { //mov hacia atras
                    t -= speed;
                    if (t <= 0f) forward = true;
                }

                invalidate();//redibujar pantalla
                postOnAnimation(this);//rejecuta el run
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) { //metodo para dibujar la todo en la pantalla
        super.onDraw(canvas);

        int w = getWidth();//tamaño de la pantalla
        int h = getHeight();

        float x = t * w;//pos x

        float y = h - (4 * h * t * (1 - t)); //pos y y forma de la parabola el h esta dado por la pantalla

        canvas.drawCircle(x, y, 25, ballPaint);//dibuja la bola
    }
}