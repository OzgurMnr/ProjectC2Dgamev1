package com.example.projectc2dgame;


//en günceli
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.GestureDetector;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread; // Oyun döngüsünü yönetecek thread







    //CatSprite
    private Bitmap cat_runspriteSheet; // Karakterin tüm animasyon karelerini içeren sprite sheet
    private Bitmap  jump_spriteSheet;
    private Bitmap cat_runspriteSheet2;
    private int catframeWidth, catframeHeight; // Her bir animasyon karesinin genişliği ve yüksekliği
    private int jumpframeWidth, jumpframeHeight;

    private int catframeCount = 8; // Toplam animasyon karesi sayısı
    private int jumpframeCount = 8;



    private GestureDetector gestureDetector;
    private int currentFrameCat1 = 0;  // cat1 için animasyon frame indeksi
    private int currentFrameCat2 = catframeCount - 1; // cat2 için ters sırada, başta 7

    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Obstacle2> obstacles2 = new ArrayList<>();
    // Engel türleri için ayrı zamanlayıcılar
    private long lastObstacleSpawnTime3 = 0;
    private long lastObstacleSpawnTime4 = 0;

    private int obstacleSpawnDelay3 = 2000; // Obstacle için zamanlayıcı
    private int obstacleSpawnDelay4 = 3000; // Obstacle2 için zamanlayıcı
    private Cat cat1, cat2;

    private long currentTime;
    public boolean location1check=false;


    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        gestureDetector = new GestureDetector(context, new GestureListener());
        cat_runspriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.cat_sprite);
        jump_spriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.jump_sprite);
        cat_runspriteSheet2= BitmapFactory.decodeResource(getResources(), R.drawable.catrunmirror);
        catframeWidth = cat_runspriteSheet.getWidth()/ catframeCount ;
        catframeHeight = cat_runspriteSheet.getHeight();
        jumpframeWidth = jump_spriteSheet.getWidth()/8;
        jumpframeHeight = jump_spriteSheet.getHeight();
        thread = new GameThread(getHolder(), this);// Thread başlatılıyor
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int initialCat1X = (getWidth() - catframeWidth) - 400;
        int initialCat1Y = (getHeight() - catframeHeight) - 580;

        int initialCat2X = ((getWidth() - catframeWidth) / 2) - 1125;
        int initialCat2Y = ((getHeight() - catframeHeight) / 2) - 180;

        cat1 = new Cat(initialCat1X, initialCat1Y, cat_runspriteSheet, jump_spriteSheet, catframeCount, jumpframeCount,40,280,40);
        cat2 = new Cat(initialCat2X, initialCat2Y, cat_runspriteSheet, jump_spriteSheet, catframeCount, jumpframeCount,40,280,40);
        thread.setRunning(true);
        thread.start();


    }

    public void update() {
        currentTime = System.currentTimeMillis();
        cat1.update(currentTime);
        cat2.update(currentTime);



        // Engel 1 (Obstacle) ekleme
        if (currentTime > lastObstacleSpawnTime3 + obstacleSpawnDelay3) {
            obstacles.add(new Obstacle(getContext(), getHeight())); // Yeni Obstacle ekle
            lastObstacleSpawnTime3 = currentTime; // Zamanı güncelle
        }

        // Engel 2 (Obstacle2) ekleme
        if (currentTime > lastObstacleSpawnTime4 + obstacleSpawnDelay4) {
            obstacles2.add(new Obstacle2(getContext(), getHeight())); // Yeni Obstacle2 ekle
            lastObstacleSpawnTime4 = currentTime; // Zamanı güncelle
        }

        // Mevcut engelleri güncelle
        Iterator<Obstacle> iterator = obstacles.iterator();

        while (iterator.hasNext()) {
            Obstacle obstacle = iterator.next();
            obstacle.update();
            if (obstacle.getX() + obstacle.getWidth() < 0) {
                iterator.remove(); // Ekrandan çıkan engelleri listeden kaldır
            }
        }

        // Obstacle2 güncelleme ve temizleme
        Iterator<Obstacle2> iterator2 = obstacles2.iterator();
        while (iterator2.hasNext()) {
            Obstacle2 obstacle2 = iterator2.next();
            obstacle2.update();
            if (obstacle2.getX() + obstacle2.getWidth() < 0) {
                iterator2.remove(); // Ekrandan çıkan engelleri listeden kaldır
            }
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            canvas.drawColor(Color.WHITE);

            cat1.draw(canvas);
            cat2.draw(canvas);


        }

        // Tüm engelleri çiz
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(canvas);
        }
        for (Obstacle2 obstacle2 : obstacles2) {
            obstacle2.draw(canvas);
        }


    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean gestureHandled = gestureDetector.onTouchEvent(event);
        boolean catHandled = false; // dokunma işlenme durumu için değişken

        float x = event.getX();
        int width = getWidth();

        if (x > width / 2) {
            // Sol yarı - cat1 için dokunma işle
            catHandled = cat1.handleTouch(event);


        } else {
            // Sağ yarı - cat2 için dokunma işle
            catHandled = cat2.handleTouch(event);
        }

        // Eğer gestureDetector veya herhangi bir cat touch event'i işlediyse true döndür
        if (gestureHandled || catHandled) {
            return true;
        }

        return super.onTouchEvent(event);
    }





    //uygulumanın düzgün kapatılmasini saglar
    @Override public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRunning(false);
        try { thread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
    }
    @Override public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {}
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            int width = getWidth();

            if (x > width / 2) {
                // Sol yarı - cat1 için çift dokunma
                cat1.handleDoubleTap();
            } else {
                // Sağ yarı - cat2 için çift dokunma
                cat2.handleDoubleTap();
            }
            return true;
        }
    }
}