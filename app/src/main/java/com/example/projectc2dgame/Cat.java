package com.example.projectc2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    public int startingFrameCount;
    public int idleFrameCount;
    public long lastFrameChangeTime = 0;
    private float touchStartY = 0;
    public int location = 0;
    public boolean isReversed;
    public boolean isGameStart;
    public boolean isScare;
    public int scareCount;

    public int catLives;
    public int score;


    public Cat(int screenWidth, int screenHeight,
               Bitmap runSpriteSheet, Bitmap jumpSpriteSheet, Bitmap sleepSpriteSheet, Bitmap scareSpriteSheet,
               int catLives,int score,int runFrameCount, int jumpFrameCount, int startingFrameCount,int idleFrameCount,
               int jumpSpeed, int jumpHeight, int frameDelay, boolean isReversed,
               boolean isGameStart, boolean isScare, boolean isRightSide) {

        this.runSpriteSheet = runSpriteSheet;
        this.jumpSpriteSheet = jumpSpriteSheet;
        this.sleepSpriteSheet = sleepSpriteSheet;
        this.scareSpriteSheet = scareSpriteSheet;

        this.catLives=catLives;

        this.runFrameCount = runFrameCount;
        this.jumpFrameCount = jumpFrameCount;
        this.startingFrameCount = startingFrameCount;
        this.idleFrameCount=idleFrameCount;

        this.runFrameWidth = (runSpriteSheet.getWidth() / 8);
        this.runFrameHeight = runSpriteSheet.getHeight();

        this.jumpFrameWidth = jumpSpriteSheet.getWidth() / jumpFrameCount;
        this.jumpFrameHeight = jumpSpriteSheet.getHeight();

        this.jumpSpeed = jumpSpeed;
        this.jumpHeight = jumpHeight;
        this.frameDelay = frameDelay;

        this.isReversed = isReversed;
        this.isGameStart = isGameStart;
        this.isScare = isScare;
        this.score=score;

        int catWidth = runFrameWidth ;
        int catHeight = runFrameHeight ;

        this.CatY = (screenHeight  - catHeight)/2;

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
                    currentRunFrame = (currentRunFrame + 1) % idleFrameCount;
                    lastFrameChangeTime = currentTime;
                }
            } else if (!isReversed) {
                if (currentTime > lastFrameChangeTime + frameDelay) {
                    currentRunFrame = (currentRunFrame - 1 + idleFrameCount) % idleFrameCount;
                    lastFrameChangeTime = currentTime;
                }
            }
        } else if (isScare) {
            if (isReversed) {
                if (currentTime > lastFrameChangeTime + 80) {
                    currentRunFrame = (currentRunFrame + 1) % startingFrameCount;
                    lastFrameChangeTime = currentTime;
                }
            } else if (!isReversed) {
                if (currentTime > lastFrameChangeTime + 80) {
                    currentRunFrame = (currentRunFrame - 1 + startingFrameCount) % startingFrameCount;
                    lastFrameChangeTime = currentTime;
                }
            }
        } else if (isJumping || isFalling || isJumpingThrough) {

            if (isReversed) {
                if (currentTime > lastFrameChangeTime + 100) {
                    currentRunFrame = (currentRunFrame + 1) % jumpFrameCount;
                    lastFrameChangeTime = currentTime;
                }
            } else if (!isReversed) {
                if (currentTime > lastFrameChangeTime + 100) {
                    currentRunFrame = (currentRunFrame - 1 + jumpHeight) % jumpFrameCount;
                    lastFrameChangeTime = currentTime;
                }
            }
        } else {
            if (isReversed) {
                if (currentTime > lastFrameChangeTime + 20) {
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
        // Düşmeee
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

        Rect src = new Rect(currentRunFrame * runFrameWidth, 0, (currentRunFrame + 1) * runFrameWidth, runFrameHeight);
        Rect dst = new Rect(CatX, CatY, CatX + runFrameWidth/2, CatY + runFrameHeight/2);


        if (isGameStart) {

            canvas.drawBitmap(sleepSpriteSheet, src, dst, null);
        } else if (isScare) {
            canvas.drawBitmap(scareSpriteSheet, src, dst, null);
            scareCount++;
            if (scareCount == 8) {
                isScare = false;
            }

        } else if (isJumping || isFalling || isJumpingThrough) {
            // Zıplama (tek kare)
            canvas.drawBitmap(jumpSpriteSheet, src, dst, null);
        } else if (catLives == 6) {

            canvas.drawBitmap(jumpSpriteSheet, src, dst, null);

        } else {
            // Koşma animasyonu

            canvas.drawBitmap(runSpriteSheet, src, dst, null);
        }
        Paint red = new Paint();
        red.setColor(Color.RED);
        red.setStyle(Paint.Style.STROKE);
        red.setStrokeWidth(5);
        canvas.drawRect(getRect(), red);

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

    public Rect getRect() {
        int drawWidth = runFrameWidth ;     // 128 * 3 = 384
        int drawHeight = runFrameHeight ;   // 128 * 3 = 384

        int paddingX = 60;  // yatayda içeri çek
        int paddingY = 80;  // dikeyde içeri çek

        return new Rect(
                CatX + paddingX,
                CatY + paddingY,
                CatX + runFrameWidth - paddingX,
                CatY + runFrameHeight - paddingY
        );
    }



    }