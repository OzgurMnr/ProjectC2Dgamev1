package com.example.projectc2dgame;

import com.example.projectc2dgame.R;;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread; // Oyun döngüsünü yönetecek thread




    private float touchStartY = 0;
    private boolean isJumping = false;

    private int jumpProgress = 0;
    private boolean isFalling=false;
    private int location=0;


    //CatSprite
    private Bitmap cat_runspriteSheet; // Karakterin tüm animasyon karelerini içeren sprite sheet
    private Bitmap  jump_spriteSheet;
    private int catframeWidth, catframeHeight; // Her bir animasyon karesinin genişliği ve yüksekliği
    private int jumpframeWidth, jumpframeHeight;
    private int currentFrame = 0; // O anda görüntülenen kare indeksi
    private int catframeCount = 8; // Toplam animasyon karesi sayısı
    private int jumpframeCount = 8;
    private long lastFrameChangeTime = 0; // Son kare değişim zamanı (ms cinsinden)
    //jumnptroughline
    private boolean jumptroughline = false;
    private boolean isJumpingThrough = false;
    private int jumpThroughHeight = 240;
    private int jumpThroughProgress = 0;
    private int jumpThroughSpeed = 30;


    private GestureDetector gestureDetector;

    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Obstacle2> obstacles2 = new ArrayList<>();
    // Engel türleri için ayrı zamanlayıcılar
    private long lastObstacleSpawnTime3 = 0;
    private long lastObstacleSpawnTime4 = 0;

    private int obstacleSpawnDelay3 = 2000; // Obstacle için zamanlayıcı
    private int obstacleSpawnDelay4 = 3000; // Obstacle2 için zamanlayıcı
    private Cat cat1, cat2;


    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        gestureDetector = new GestureDetector(context, new GestureListener());
        cat_runspriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.cat_sprite);
        jump_spriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.jump_sprite);
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

        cat1 = new Cat(initialCat1X, initialCat1Y, cat_runspriteSheet, jump_spriteSheet, catframeCount, jumpframeCount,40,320,40);
        cat2 = new Cat(initialCat2X, initialCat2Y, cat_runspriteSheet, jump_spriteSheet, catframeCount, jumpframeCount,40,320,40);
        thread.setRunning(true);
        thread.start();
    }

    public void update() {
        long currentTime = System.currentTimeMillis();

        // animasyon karesini değiştirr

        if(isJumping||isFalling){
            if (currentTime > lastFrameChangeTime + cat1.frameDelay) {
                currentFrame = (currentFrame + 1) % catframeCount;
                lastFrameChangeTime = currentTime;
            }
        }
        else{
            if (currentTime > lastFrameChangeTime + cat1.frameDelay) {
                currentFrame = (currentFrame + 1) % catframeCount;
                lastFrameChangeTime = currentTime;
            }
        }



        // zıplama hareketi
        if (isJumpingThrough) {
            if (cat1.jumpThroughProgress < jumpThroughHeight) {
                cat1.y -= jumpThroughSpeed;
                cat2.y -= jumpThroughSpeed;
                cat1.jumpThroughProgress += jumpThroughSpeed;
                cat2.jumpThroughProgress += jumpThroughSpeed;
            } else if (cat1.jumpThroughProgress < jumpThroughHeight * 2) {
                cat1.y += jumpThroughSpeed;
                cat2.y += jumpThroughSpeed;
                cat1.jumpThroughProgress += jumpThroughSpeed;
                cat2.jumpThroughProgress += jumpThroughSpeed;
            } else {
                isJumpingThrough = false;
                jumptroughline = false;
                cat1.jumpThroughProgress = 0;
                cat2.jumpThroughProgress = 0;
            }
        }
        // Zıplama veya düşme hareketi
        if (isJumping) {
            if (jumpProgress < cat1.jumpHeight) {
                cat1.y -= cat1.jumpSpeed;
                cat2.y -= cat1.jumpSpeed;
                jumpProgress += cat1.jumpSpeed;
            } else {
                isJumping = false;
                jumpProgress = 0;
                location++;
            }
        } else if (isFalling) {
            if (jumpProgress < cat1.jumpHeight) {
                cat1.y += cat1.jumpSpeed;
                cat2.y += cat1.jumpSpeed;
                jumpProgress += cat1.jumpSpeed;
            } else {
                isFalling = false;
                jumpProgress = 0;
                location--;
            }
        }



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
        canvas.drawColor(Color.WHITE);
        if (isJumping || isFalling) {
            currentFrame=0;
            Rect src1 = new Rect(currentFrame * cat1.jumpFrameWidth, 0,
                    (currentFrame + 1) * cat1.jumpFrameWidth, cat1.jumpFrameHeight);
            Rect dst1 = new Rect(cat1.x, cat1.y,
                    cat1.x + cat1.jumpFrameWidth * 3, cat1.y + cat1.jumpFrameHeight * 3);
            canvas.drawBitmap(cat1.jumpSpriteSheet, src1, dst1, null);
        } else {
            Rect src1 = new Rect(currentFrame * cat1.runFrameWidth, 0,
                    (currentFrame + 1) * cat1.runFrameWidth, cat1.runFrameHeight);
            Rect dst1 = new Rect(cat1.x, cat1.y,
                    cat1.x + cat1.runFrameWidth * 3, cat1.y + cat1.runFrameHeight * 3);
            canvas.drawBitmap(cat1.runSpriteSheet, src1, dst1, null);
        }
        if (isJumping || isFalling) {
            currentFrame = 0;
            Rect src2 = new Rect(currentFrame * cat2.jumpFrameWidth, 0,
                    (currentFrame + 1) * cat2.jumpFrameWidth, cat2.jumpFrameHeight);
            Rect dst2 = new Rect(cat2.x, cat2.y,
                    cat2.x + cat2.jumpFrameWidth * 3, cat2.y + cat2.jumpFrameHeight * 3);
            canvas.drawBitmap(cat2.jumpSpriteSheet, src2, dst2, null);
        } else {
            Rect src2 = new Rect(currentFrame * cat2.runFrameWidth, 0,
                    (currentFrame + 1) * cat2.runFrameWidth, cat2.runFrameHeight);
            Rect dst2 = new Rect(cat2.x, cat2.y,
                    cat2.x + cat2.runFrameWidth * 3, cat2.y + cat2.runFrameHeight * 3);
            canvas.drawBitmap(cat2.runSpriteSheet, src2, dst2, null);
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
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStartY = event.getY(); // dokunmanın başladığı y
                return true;

            case MotionEvent.ACTION_UP:
                float endY = event.getY();
                if (touchStartY - endY > 100 && !isJumping && (location==0 || location == -1)  ) { // yukarı doğru kaydırıldıysa ve zıplamıyorsa
                    isJumping = true;


                }
                else if (endY-touchStartY > 100 && !isFalling && (location==0 || location ==1)) {
                    isFalling = true;
                    
                }

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


            isJumpingThrough=true;
            jumptroughline=true;

            return true;
        }
    }
}



