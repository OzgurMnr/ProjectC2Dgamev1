package com.example.projectc2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

public class Cat {

    public int initialCatX;
    public int initialCatY;

    public int jumpHeight;
    public int jumpSpeed;
    public int jumpProgress = 0;
    public boolean isJumping = false;
    public boolean isFalling = false;
    public boolean isJumpingThrough = false;
    public int jumpThroughProgress = 0;

    public Bitmap runSpriteSheet;
    public Bitmap jumpSpriteSheet;

    public int runFrameWidth, runFrameHeight;
    public int jumpFrameWidth, jumpFrameHeight;

    public int frameDelay;
    public int currentRunFrame = 0;
    public int runFrameCount;
    public int jumpFrameCount; // Zıplamaaaaaa akare sayısı (şu an sabit 1 olabilir ama geliştirmeye açık)
    public long lastFrameChangeTime = 0;
    private float touchStartY = 0;
    public int location=0;
    public boolean isReversed;

    public Cat(int initialCatX, int initialCatY,
               Bitmap runSpriteSheet, Bitmap jumpSpriteSheet,
               int runFrameCount, int jumpFrameCount,
               int jumpSpeed, int jumpHeight, int frameDelay,boolean isReversed) {



        this.runSpriteSheet = runSpriteSheet;
        this.jumpSpriteSheet = jumpSpriteSheet;

        this.runFrameCount = runFrameCount;
        this.jumpFrameCount = jumpFrameCount;

        this.runFrameWidth = runSpriteSheet.getWidth() / runFrameCount;
        this.runFrameHeight = runSpriteSheet.getHeight();

        this.jumpFrameWidth = jumpSpriteSheet.getWidth() / jumpFrameCount;
        this.jumpFrameHeight = jumpSpriteSheet.getHeight();

        this.jumpSpeed = jumpSpeed;
        this.jumpHeight = jumpHeight;
        this.frameDelay = frameDelay;

        this.isReversed=isReversed;
        this.initialCatX=initialCatX;
        this.initialCatY=initialCatY;

    }

    public void update(long currentTime) {
        // Koşma animasyonu zamanlaması
        if(isReversed){
            if (currentTime > lastFrameChangeTime + frameDelay) {
                currentRunFrame = (currentRunFrame + 1) % runFrameCount;
                lastFrameChangeTime = currentTime;
            }
        } else if (!isReversed) {
            if (currentTime > lastFrameChangeTime + frameDelay) {
                currentRunFrame = (currentRunFrame - 1 + runFrameCount) % runFrameCount;
                lastFrameChangeTime = currentTime;
            }
        }


        // Jump-through hareketi (örnek: çift hat zıplama)
        if (isJumpingThrough) {
            if (jumpThroughProgress < jumpHeight) {
                initialCatY -= jumpSpeed;
                jumpThroughProgress += jumpSpeed;
            } else if (jumpThroughProgress < jumpHeight * 2) {
                initialCatY += jumpSpeed;
                jumpThroughProgress += jumpSpeed;
            } else {
                isJumpingThrough = false;
                jumpThroughProgress = 0;
            }
        }


        // Normal zıplama
        if (isJumping) {
            if (jumpProgress < jumpHeight) {
                initialCatY -= jumpSpeed;
                jumpProgress += jumpSpeed;
            } else {
                isJumping = false;
                jumpProgress = 0;
                location++;
            }
        }
        // Düşme
        else if (isFalling) {
            if (jumpProgress < jumpHeight) {
                initialCatY += jumpSpeed;
                jumpProgress += jumpSpeed;
            } else {
                isFalling = false;
                location--;
                jumpProgress = 0;
            }
        }
    }


    public void draw(Canvas canvas) {
        if (isJumping || isFalling || isJumpingThrough) {
            // Zıplama (tek kare)
            Rect src = new Rect(0, 0, jumpFrameWidth, jumpFrameHeight);
            Rect dst = new Rect(initialCatX, initialCatY, initialCatX + jumpFrameWidth * 3, initialCatY + jumpFrameHeight * 3);
            canvas.drawBitmap(jumpSpriteSheet, src, dst, null);
        } else {
            // Koşma animasyonu
            Rect src = new Rect(currentRunFrame * runFrameWidth, 0,
                    (currentRunFrame + 1) * runFrameWidth, runFrameHeight);
            Rect dst = new Rect(initialCatX, initialCatY, initialCatX + runFrameWidth * 3, initialCatY + runFrameHeight * 3);
            canvas.drawBitmap(runSpriteSheet, src, dst, null);
        }
    }



    public boolean handleTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStartY = event.getY(); // dokunmanın başladığı y
                return true;

            case MotionEvent.ACTION_UP:
                float endY = event.getY();
                if (touchStartY - endY > 100 && !isJumping && (location == 0 || location == -1)) { // yukarı kaydırma
                    isJumping = true;
                    return true;
                } else if (endY - touchStartY > 100 && !isFalling && (location == 0 || location == 1)) { // aşağı kaydırma
                    isFalling = true;
                    return true;
                }
                return false; // Kaydırma kriterlerine uymayan durumlar için
        }
        return false; // Diğer tüm durumlar için
    }
    public void handleDoubleTap() {
        if (!isJumpingThrough && !isJumping && !isFalling) {
            isJumpingThrough = true;
            jumpThroughProgress = 0;
        }
    }
// Çarpışma için
Rect dst = new Rect(initialCatX, initialCatY, initialCatX + runFrameWidth * 3, initialCatY + runFrameHeight * 3);

    public Rect getRect() {
        return new Rect(initialCatX, initialCatY, initialCatX + runFrameWidth * 3, initialCatY + runFrameHeight * 3);
    }

}