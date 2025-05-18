package com.example.projectc2dgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background {

    private int bgXLeft;
    private int bgXRight;
    private Bitmap bgImage;
    private int bgScrollSpeed = 15;

    public Background(Bitmap bgImage,int bgXLeft,int bgXRight,int bgScrollSpeed) {
        this.bgImage = bgImage;
        this.bgXLeft=bgXLeft;
        this.bgXRight=bgXRight;
        this.bgScrollSpeed=bgScrollSpeed;
    }

    public void drawScrollingBackgroundDual(Canvas canvas) {
        int bgWidth = bgImage.getWidth();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int halfWidth = canvasWidth / 2;

        // SOL TARAF: Sola kayar
        bgXLeft -= bgScrollSpeed;
        if (bgXLeft <= -bgWidth) {
            bgXLeft += bgWidth;
        }
        canvas.save();
        canvas.clipRect(0, 0, halfWidth, canvasHeight);  // Sol yarıyı çiz
        canvas.drawBitmap(bgImage, bgXLeft, 0, null);
        canvas.drawBitmap(bgImage, bgXLeft + bgWidth, 0, null);
        canvas.restore();

        // SAĞ TARAF: Sağa kayar
        bgXRight += bgScrollSpeed;
        if (bgXRight >= bgWidth) {
            bgXRight -= bgWidth;
        }
        canvas.save();
        canvas.clipRect(halfWidth, 0, canvasWidth, canvasHeight); // Sağ yarıyı çiz
        canvas.drawBitmap(bgImage, bgXRight - bgWidth, 0, null);
        canvas.drawBitmap(bgImage, bgXRight, 0, null);
        canvas.restore();
    }
}