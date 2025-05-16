package com.example.projectc2dgame;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.GestureDetector;
import android.app.Activity;
import android.util.DisplayMetrics;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread; // Oyun döngüsünü yönetecek thread





    private GestureDetector gestureDetector;


    private ObstacleManager obstacleManager;

    private Cat cat1, cat2;

    private long currentTime;


    private Bitmap bgImage;


    // Çarpışma sonrası kısa dokunulmazlık süresi için zaman tutucu
    private long cat1LastHitTime = 0;
    private long cat2LastHitTime = 0;
    private static final long INVULNERABILITY_TIME = 1500; // 1.5 saniye dokunulmazlık

    public GameView(Context context) {
        super(context);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

// bg_1 resmini ekran boyutuna göre ölçekle ve yükle
        bgImage = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.bg_1),
                screenWidth,
                screenHeight,
                true
        );
        getHolder().addCallback(this);
        gestureDetector = new GestureDetector(context, new GestureListener());

        thread = new GameThread(getHolder(), this); // Thread başlatılıyor
        setFocusable(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        cat1 = new Cat(getWidth(), getHeight(),
                BitmapFactory.decodeResource(getResources(), R.drawable.cat_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.jump_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.sleep_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.scare_sprite),
                8, 8, 11, 40, 280, 40,
                true, true, false, true);

        cat2 = new Cat(getWidth(), getHeight(),
                BitmapFactory.decodeResource(getResources(), R.drawable.cat_mirror_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.jump_mirror_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.sleep_mirror_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.scare_mirror_sprite),
                8, 8, 11, 40, 280, 40,
                false, true, false, false);
        obstacleManager = new ObstacleManager(getContext(), getHeight(),getWidth());

        thread.setRunning(true);
        thread.start();
    }


    public void update() {
        currentTime = System.currentTimeMillis();
        cat1.update(currentTime);
        cat2.update(currentTime);
    if(!cat1.isGameStart){
        if (obstacleManager != null) {
            obstacleManager.update();


            // Cat1 çarpışma kontrolü ve can azaltma
            if (obstacleManager.checkCollision(cat1)) {
                if (currentTime - cat1LastHitTime > INVULNERABILITY_TIME) {
                    cat1.catLives++;
                    cat1LastHitTime = currentTime;
                    if (cat1.catLives <= 0) {
                        thread.setRunning(false);
                        // İstersen game over ekranına geçiş veya başka işlemler ekleyebilirsin
                    }
                }
            }
        }

            // Cat2 çarpışma kontrolü ve can azaltma
            if (obstacleManager.checkCollision(cat2)) {
                if (currentTime - cat2LastHitTime > INVULNERABILITY_TIME) {
                    cat2.catLives++;
                    cat2LastHitTime = currentTime;
                    if (cat2.catLives <= 0) {
                        thread.setRunning(false);
                        // İstersen game over ekranına geçiş veya başka işlemler ekleyebilirsin
                    }
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            // 1️⃣ Arka plan resmi en başta çizilmeli
            canvas.drawBitmap(bgImage, 0, 0, null);

            // 2️⃣ Sonra diğer şeyleri çiz
            cat1.draw(canvas);
            cat2.draw(canvas);

            if (obstacleManager != null) {
                obstacleManager.draw(canvas);
            }

            // 3️⃣ Can sayısını ekrana yaz
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setTextSize(50);
            paint.setAntiAlias(true);

            canvas.drawText("Cat1 Lives: " + cat1.catLives, 50, 100, paint);
            canvas.drawText("Cat2 Lives: " + cat2.catLives, 50, 170, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean gestureHandled = gestureDetector.onTouchEvent(event);
        boolean catHandled = false;

        float x = event.getX();
        int width = getWidth();

        if (x > width / 2) {
            // Sağ yarı - cat1 için dokunma işlemi
            catHandled = cat1.handleTouch(event);
        } else {
            // Sol yarı - cat2 için dokunma işlemi
            catHandled = cat2.handleTouch(event);
        }

        return gestureHandled || catHandled || super.onTouchEvent(event);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            int width = getWidth();
            if(cat1.isGameStart){
                cat1.handleDoubleTap();
                cat2.handleDoubleTap();
            }else {
                if (x > width / 2) {
                    // Sağ yarı - cat1 için çift dokunma
                    cat1.handleDoubleTap();
                } else {
                    // Sol yarı - cat2 için çift dokunma
                    cat2.handleDoubleTap();
                }
            }

            return true;
        }
    }
}