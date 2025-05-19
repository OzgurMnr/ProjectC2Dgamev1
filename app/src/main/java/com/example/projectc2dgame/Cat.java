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
    public Bitmap attackSpriteSheet;
    public Bitmap hurtSpriteSheet;
    public Bitmap deadSpriteSheet;

    public int runFrameWidth, runFrameHeight;
    public int jumpFrameWidth, jumpFrameHeight;

    public int frameDelay;
    public int currentRunFrame = 0;
    public int runFrameCount;
    public int jumpFrameCount;
    public int startingFrameCount;
    public int idleFrameCount;
    public int attackFrameCount;
    public int hurtFrameCount;
    public int deadFrameCount;

    public long lastFrameChangeTime = 0;
    private float touchStartY = 0;
    public int location = 0;
    public boolean isReversed;
    public boolean isGameStart;
    public boolean isScare;
    public boolean isAttack = false;
    public boolean isDead = false;
    public boolean isHurt = false;

    public int scareCount;
    public int attackCount = 0;

    public int catLives;
    public int score;

    public Cat(int screenWidth, int screenHeight,
               Bitmap runSpriteSheet, Bitmap jumpSpriteSheet, Bitmap sleepSpriteSheet,
               Bitmap scareSpriteSheet, Bitmap attackSpriteSheet, Bitmap hurtSpriteSheet, Bitmap deadSpriteSheet,
               int catLives, int score, int runFrameCount, int jumpFrameCount,
               int startingFrameCount, int idleFrameCount, int attackFrameCount, int hurtFrameCount, int deadFrameCount,
               int jumpSpeed, int jumpHeight, int frameDelay, boolean isReversed,
               boolean isGameStart, boolean isScare, boolean isRightSide) {

        this.runSpriteSheet = runSpriteSheet;
        this.jumpSpriteSheet = jumpSpriteSheet;
        this.sleepSpriteSheet = sleepSpriteSheet;
        this.scareSpriteSheet = scareSpriteSheet;
        this.attackSpriteSheet = attackSpriteSheet;
        this.hurtSpriteSheet = hurtSpriteSheet;
        this.deadSpriteSheet = deadSpriteSheet;

        this.catLives = catLives;
        this.runFrameCount = runFrameCount;
        this.jumpFrameCount = jumpFrameCount;
        this.startingFrameCount = startingFrameCount;
        this.idleFrameCount = idleFrameCount;
        this.attackFrameCount = attackFrameCount;
        this.hurtFrameCount = hurtFrameCount;
        this.deadFrameCount = deadFrameCount;

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
        this.score = score;

        int catWidth = runFrameWidth;
        int catHeight = runFrameHeight;

        this.CatY = (screenHeight - catHeight) /2;
        this.CatX = isRightSide ? (screenWidth - catWidth) : 0;
    }

    public void update(long currentTime) {
        // Animasyon yönetimi
        if (isGameStart) {
            updateFrame(currentTime, idleFrameCount);
        } else if (isScare) {
            updateFrame(currentTime, attackFrameCount);
        } else if (isHurt) {
            updateFrame(currentTime, hurtFrameCount);
        } else if (isDead) {
            updateFrame(currentTime, deadFrameCount);
        } else if (isAttack) {
            updateFrame(currentTime, attackFrameCount);
        } else if (isJumping || isFalling || isJumpingThrough) {
            updateFrame(currentTime, jumpFrameCount);
        } else {
            updateFrame(currentTime, runFrameCount);
        }

        // Jump-through hareketi
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
                jumpProgress = 0;
                location--;
            }
        }
    }

    private void updateFrame(long currentTime, int frameCount) {
        int delay = frameDelay;
        if (isScare || isAttack || isHurt || isDead) {
            delay = 80;
        } else if (isJumping || isFalling || isJumpingThrough) {
            delay = 100;
        } else if (isGameStart) {
            delay = 20;
        }

        if (currentTime > lastFrameChangeTime + delay) {
            currentRunFrame = isReversed
                    ? (currentRunFrame + 1) % frameCount
                    : (currentRunFrame - 1 + frameCount) % frameCount;
            lastFrameChangeTime = currentTime;
        }
    }

    public void draw(Canvas canvas) {
        Rect src = new Rect(currentRunFrame * runFrameWidth, 0, (currentRunFrame + 1) * runFrameWidth, runFrameHeight);
        Rect dst = new Rect(CatX, CatY, CatX + runFrameWidth, CatY + runFrameHeight);

        if (isGameStart) {
            canvas.drawBitmap(sleepSpriteSheet, src, dst, null);
        } else if (isScare) {
            canvas.drawBitmap(scareSpriteSheet, src, dst, null);
            if (++scareCount == 8) isScare = false;
        } else if (isHurt) {
            canvas.drawBitmap(hurtSpriteSheet, src, dst, null);
        } else if (isDead) {
            canvas.drawBitmap(deadSpriteSheet, src, dst, null);
        } else if (isAttack) {
            canvas.drawBitmap(attackSpriteSheet, src, dst, null);
            if (++attackCount == 10) isAttack = false;
        } else if (isJumping || isFalling || isJumpingThrough) {
            canvas.drawBitmap(jumpSpriteSheet, src, dst, null);
        } else {
            canvas.drawBitmap(runSpriteSheet, src, dst, null);
        }

        // Debug çerçeve
        Paint red = new Paint();
        red.setColor(Color.RED);
        red.setStyle(Paint.Style.STROKE);
        red.setStrokeWidth(5);
        canvas.drawRect(getRect(), red);
    }

    public boolean handleTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStartY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                float endY = event.getY();
                if (touchStartY - endY > 100 && !isJumping && (location == 0 || location == -1) && !isGameStart) {
                    isJumping = true;
                    return true;
                } else if (endY - touchStartY > 100 && !isFalling && (location == 0 || location == 1) && !isGameStart) {
                    isFalling = true;
                    return true;
                }
                return false;
        }
        return false;
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
//Hitbox çizimi
    public Rect getRect() {
        int paddingX = runFrameWidth / 4;  // sprite boyutuna göre oransal kesme
        int paddingY = (runFrameHeight / 5)+15;

        return new Rect(
                CatX + paddingX,
                CatY + paddingY,
                CatX + runFrameWidth - paddingX,
                CatY + runFrameHeight - paddingY
        );
    }


    public int getHeight() {
        return runFrameHeight;
    }

    public int getWidth() {
        return runFrameWidth;
    }


    public int getX() {
        return CatX;
    }

    public int getY() {
        return CatY;
    }
}
