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
    private Background bg_1;

    private Bitmap bg_Image;

    private Bitmap heart_1;
    private Bitmap heart_2;
    private Bitmap heart_3;
    // Kalp + yazının toplam genişliğini hesapla (tahmini yazı genişliği: 80px)
    private int heartX = getWidth() - 180; // Sağdan boşluk
    private int heartY = 30;

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
        bg_Image = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.bg_1),
                screenWidth,
                screenHeight,
                true
        );
        heart_1 = BitmapFactory.decodeResource(getResources(), R.drawable.heart_1);
        heart_1 = Bitmap.createScaledBitmap(heart_1, 96, 64, false);

        heart_2 = BitmapFactory.decodeResource(getResources(), R.drawable.heart_2);
        heart_2 = Bitmap.createScaledBitmap(heart_2, 192, 64, false);

        heart_3 = BitmapFactory.decodeResource(getResources(), R.drawable.heart_3);
        heart_3 = Bitmap.createScaledBitmap(heart_3, 288, 64, false);


        getHolder().addCallback(this);
        gestureDetector = new GestureDetector(context, new GestureListener());

        thread = new GameThread(getHolder(), this); // Thread başlatılıyor
        setFocusable(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        cat1 = new Cat(getWidth(), getHeight(),
                BitmapFactory.decodeResource(getResources(), R.drawable.samurai_run_mirror_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.samurai_jump_mirror_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.samurai_idle_mirror_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.samurai_starting_mirror_sprite), 3, 0,
                8, 12, 6, 6, 40, 280, 40,
                false, true, false, true);

        cat2 = new Cat(getWidth(), getHeight(),
                BitmapFactory.decodeResource(getResources(), R.drawable.samurai_run_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.samurai_jump_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.samurai_idle_sprite),
                BitmapFactory.decodeResource(getResources(), R.drawable.samurai_starting_sprite), 3, 0,
                8, 12, 6, 6, 40, 280, 30,
                true, true, false, false);
        bg_1 = new Background(bg_Image, 0, 0, 15);

        obstacleManager = new ObstacleManager(getContext(), getHeight(), getWidth());

        thread.setRunning(true);
        thread.start();
    }


    public void update() {
        currentTime = System.currentTimeMillis();
        cat1.update(currentTime);
        cat2.update(currentTime);
        if (!cat1.isGameStart) {
            cat1.score += 10;
            cat2.score += 10;

            if (obstacleManager != null) {
                obstacleManager.update();

                // Çarpışmalar
                boolean cat1Hit = obstacleManager.checkCollision(cat1);
                boolean cat2Hit = obstacleManager.checkCollision(cat2);

                if (cat1Hit && currentTime - cat1LastHitTime > INVULNERABILITY_TIME) {
                    cat1.catLives++;
                    cat1LastHitTime = currentTime;
                }

                if (cat2Hit && currentTime - cat2LastHitTime > INVULNERABILITY_TIME) {
                    cat2.catLives++;
                    cat2LastHitTime = currentTime;
                }
            }
        }


    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        super.draw(canvas);

        if (canvas != null) {
            //  Arka plan resmi en başta çizilmeli
            if (cat1.isGameStart) {
                canvas.drawBitmap(bg_Image, 0, 0, null);
            } else {
                bg_1.drawScrollingBackgroundDual(canvas);
            }


            // 2 Sonra diğer şeyleri çiz
            cat1.draw(canvas);
            cat2.draw(canvas);

            if (obstacleManager != null) {
                obstacleManager.draw(canvas);
            }
            int heartX = canvas.getWidth() - 350; // Sağdan boşluk
            int heartY = 30;
            // 3️⃣ Can sayısını ekrana yaz
            if (cat1.catLives == 3) {
                canvas.drawBitmap(heart_3, 50, 50, null);
                canvas.drawText("x " + cat1.catLives, 110, 90, paint);
            } else if (cat1.catLives == 2) {
                canvas.drawBitmap(heart_2, 50, 50, null);
                canvas.drawText("x " + cat1.catLives, 110, 90, paint);
            } else if (cat1.catLives == 1) {
                canvas.drawBitmap(heart_1, 50, 50, null);
                canvas.drawText("x " + cat1.catLives, 110, 90, paint);
            }

            if (cat2.catLives == 3) {
                canvas.drawBitmap(heart_3, heartX, heartY, null);
                canvas.drawText("x " + cat2.catLives, heartX + 50, heartY + 45, paint);
            } else if (cat2.catLives == 2) {
                canvas.drawBitmap(heart_2, heartX, heartY, null);
                canvas.drawText("x " + cat2.catLives, heartX + 50, heartY + 45, paint);
            } else if (cat2.catLives == 1) {
                canvas.drawBitmap(heart_1, heartX, heartY, null);
                canvas.drawText("x " + cat2.catLives, heartX + 50, heartY + 45, paint);
            }
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            canvas.drawText("Score: " + cat1.score, heartX, heartY + 110, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            canvas.drawText("Score: " + cat2.score, 70, heartY + 120, paint);

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
            if (cat1.isGameStart) {
                cat1.handleDoubleTap();
                cat2.handleDoubleTap();
            } else {
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