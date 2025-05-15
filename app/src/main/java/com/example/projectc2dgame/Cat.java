package com.example.projectc2dgame;

import android.graphics.Bitmap;

public class Cat {
    public int x, y;
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



    public Cat(int x, int y,
               Bitmap runSpriteSheet, Bitmap jumpSpriteSheet,
               int runFrameCount, int jumpFrameCount,int jumpSpeed,int jumpHeight ,int frameDelay) {

        this.x = x;
        this.y = y;


        this.runSpriteSheet = runSpriteSheet;
        this.jumpSpriteSheet = jumpSpriteSheet;

        this.runFrameWidth = runSpriteSheet.getWidth() / runFrameCount;
        this.runFrameHeight = runSpriteSheet.getHeight();

        this.jumpFrameWidth = jumpSpriteSheet.getWidth() / jumpFrameCount;
        this.jumpFrameHeight = jumpSpriteSheet.getHeight();

        this.jumpSpeed=jumpSpeed;
        this.jumpHeight=jumpHeight;
        this.frameDelay=frameDelay;
    }
}