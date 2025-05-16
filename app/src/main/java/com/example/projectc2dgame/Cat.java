package com.example.projectc2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

public class Cat {

    public int CatX;
    public int CatY;

    public int jumpHeight;
    public int jumpSpeed;
    public int jumpProgress = 0;
    public boolean isJumping = false;
    public boolean isFalling = false;
    public boolean isJumpingThrough = false;
    public int jumpThroughProgress = 0;

    public Bitmap runSpriteSheet;
    public Bitmap jumpSpriteSheet;
    public Bitmap sleepSpriteSheet;

    public Bitmap scareSpriteSheet;
    public Bitmap hurtSpriteSheet;
    public Bitmap deathSpriteSheet;

    public int runFrameWidth, runFrameHeight;
    public int jumpFrameWidth, jumpFrameHeight;

    public int frameDelay;
    public int currentRunFrame = 0;
    public int runFrameCount;
    public int jumpFrameCount; // Zıplamaaaaaa akare sayısı (şu an sabit 1 olabilir ama geliştirmeye açık)
    public int scareFrameCount;
    public long lastFrameChangeTime = 0;
    private float touchStartY = 0;
    public int location = 0;
    public boolean isReversed;
    public boolean isGameStart;
    public boolean isScare;
    public int scareCount;

    public int catLives;


    public Cat(int screenWidth, int screenHeight,
               Bitmap runSpriteSheet, Bitmap jumpSpriteSheet, Bitmap sleepSpriteSheet, Bitmap scareSpriteSheet,
               int runFrameCount, int jumpFrameCount, int scareFrameCount,
               int jumpSpeed, int jumpHeight, int frameDelay, boolean isReversed,
               boolean isGameStart, boolean isScare, boolean isRightSide) {

        this.runSpriteSheet = runSpriteSheet;
        this.jumpSpriteSheet = jumpSpriteSheet;
        this.sleepSpriteSheet = sleepSpriteSheet;
        this.scareSpriteSheet = scareSpriteSheet;

        this.runFrameCount = runFrameCount;
        this.jumpFrameCount = jumpFrameCount;
        this.scareFrameCount = scareFrameCount;

        this.runFrameWidth = runSpriteSheet.getWidth() / runFrameCount;
        this.runFrameHeight = runSpriteSheet.getHeight();

        this.jumpFrameWidth = jumpSpriteSheet.getWidth() / jumpFrameCount;
        this.jumpFrameHeight = jumpSpriteSheet.getHeight();

        this.jumpSpeed = jumpSpeed;
        this.jumpHeight = jumpHeight;
        this.frameDelay = frameDelay;

        this.isReversed = isReversed;
        this.isGameStart = isGameStart;
        this.isScare = isScare;

        int catWidth = runFrameWidth * 3;
        int catHeight = runFrameHeight * 3;

        this.CatY = (screenHeight / 2) - (catHeight / 2);

        if (isRightSide) {
            this.CatX = screenWidth - catWidth;  // sağda konumlandır
        } else {
            this.CatX = 0;  // solda konumlandır
        }
    }

    public void update(long currentTime) {
        // Koşma animasyonu zamanlaması
        if (isGameStart) {
            if (isReversed) {
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
        } else if (isScare) {
            if (isReversed) {
                if (currentTime > lastFrameChangeTime + 80) {
                    currentRunFrame = (currentRunFrame + 1) % 11;
                    lastFrameChangeTime = currentTime;
                }
            } else if (!isReversed) {
                if (currentTime > lastFrameChangeTime + frameDelay) {
                    currentRunFrame = (currentRunFrame - 1 + 11) % 11;
                    lastFrameChangeTime = currentTime;
                }
            }
        } else if (isJumping || isFalling || isJumpingThrough) {

            if (isReversed) {
                if (currentTime > lastFrameChangeTime + 100) {
                    currentRunFrame = (currentRunFrame + 1) % 3;
                    lastFrameChangeTime = currentTime;
                }
            } else if (!isReversed) {
                if (currentTime > lastFrameChangeTime + frameDelay) {
                    currentRunFrame = (currentRunFrame - 1 + runFrameCount) % 3;
                    lastFrameChangeTime = currentTime;
                }
            }
        } else {
            if (isReversed) {
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
        }


        // Jump-through hareketi (örnek: çift hat zıplama)
        if (isJumpingThrough) {
            if (jumpThroughProgress < jumpHeight) {
                CatY -= jumpSpeed;
                jumpThroughProgress += jumpSpeed;
            } else if (jumpThroughProgress < jumpHeight * 2) {
                CatY += jumpSpeed;
                jumpThroughProgress += jumpSpeed;
            } else {
                isJumpingThrough = false;
                jumpThroughProgress = 0;
            }
        }


        // Normal zıplama
        if (isJumping) {
            if (jumpProgress < jumpHeight) {
                CatY -= jumpSpeed;
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
                CatY += jumpSpeed;
                jumpProgress += jumpSpeed;
            } else {
                isFalling = false;
                location--;
                jumpProgress = 0;
            }
        }
    }


    public void draw(Canvas canvas) {
        if (isGameStart) {
            Rect src = new Rect(currentRunFrame * runFrameWidth, 0, (currentRunFrame + 1) * runFrameWidth, runFrameHeight);
            Rect dst = new Rect(CatX, CatY, CatX + runFrameWidth * 3, CatY + runFrameHeight * 3);
            canvas.drawBitmap(sleepSpriteSheet, src, dst, null);
        } else if (isScare) {
            Rect src = new Rect(currentRunFrame * runFrameWidth, 0, (currentRunFrame + 1) * runFrameWidth, runFrameHeight);
            Rect dst = new Rect(CatX, CatY, CatX + runFrameWidth * 3, CatY + runFrameHeight * 3);
            canvas.drawBitmap(scareSpriteSheet, src, dst, null);
            scareCount++;
            if (scareCount == 8) {
                isScare = false;
            }

        } else if (isJumping || isFalling || isJumpingThrough) {
            // Zıplama (tek kare)
            Rect src = new Rect(currentRunFrame * runFrameWidth, 0, (currentRunFrame + 1) * runFrameWidth, runFrameHeight);
            Rect dst = new Rect(CatX, CatY, CatX + runFrameWidth * 3, CatY + runFrameHeight * 3);
            canvas.drawBitmap(jumpSpriteSheet, src, dst, null);
        } else if (catLives == 6) {
            Rect src = new Rect(currentRunFrame * runFrameWidth, 0, (currentRunFrame + 1) * runFrameWidth, runFrameHeight);
            Rect dst = new Rect(CatX, CatY, CatX + runFrameWidth * 3, CatY + runFrameHeight * 3);
            canvas.drawBitmap(deathSpriteSheet, src, dst, null);

        } else {
            // Koşma animasyonu
            Rect src = new Rect(currentRunFrame * runFrameWidth, 0, (currentRunFrame + 1) * runFrameWidth, runFrameHeight);
            Rect dst = new Rect(CatX, CatY, CatX + runFrameWidth * 3, CatY + runFrameHeight * 3);
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
                if (touchStartY - endY > 100 && !isJumping && (location == 0 || location == -1) && !isGameStart) { // yukarı kaydırma
                    isJumping = true;
                    return true;
                } else if (endY - touchStartY > 100 && !isFalling && (location == 0 || location == 1) && !isGameStart) { // aşağı kaydırma
                    isFalling = true;
                    return true;
                }
                return false; // Kaydırma kriterlerine uymayan durumlar için
        }
        return false; // Diğer tüm durumlar için
    }

    public void handleDoubleTap() {
        if (isGameStart) {
            isGameStart = false;
            isScare = true;
            scareCount = 0;
        } else {
            if (!isJumpingThrough && !isJumping && !isFalling) {
                isJumpingThrough = true;
                jumpThroughProgress = 0;
            }
        }

    }

    // Çarpışma için
    // Draw için dst Rect — 3 kat büyütülmüş hali
    // Çizim alanı, 3x büyütülmüş
    Rect dst = new Rect(CatX, CatY, CatX + runFrameWidth * 3, CatY + runFrameHeight * 3);

    // Çarpışma kutusu, frame’den biraz daha küçük
    public Rect getRect() {
        int padding = 150;  // daha fazla padding ile hitbox küçültülüyor
        return new Rect(
                CatX + padding,
                CatY + padding,
                CatX + runFrameWidth * 3 - padding,
                CatY + runFrameHeight * 3 - padding
        );
    }


}