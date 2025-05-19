package com.example.projectc2dgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ThrowableObstacle2 {
    private Bitmap bitmap;
    private int x, y;
    private int speed = -15;
    private boolean isRedirected = false;
    private int startX, startY;
    private int targetX, targetY;
    private int t1 = 0, t2 = 30; // 30 frame'lik parabol
    private boolean shouldBeRemoved = false;
    public ThrowableObstacle2(Context context, int startX, int startY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.throwable_obstacle2);
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);

        this.x = startX;
        this.y = startY;
    }

        public void update() {
            if (isRedirected) {
                t1++;
                if (t1 > t2) t1 = t2;

                int timeLeft = t2 - t1;

                int controlX = (startX + targetX) / 2;
                int controlY = startY - 300; // Parabol tepe noktası

                x = (timeLeft * timeLeft * startX +
                        2 * timeLeft * t1 * controlX +
                        t1 * t1 * targetX) / (t2 * t2);

                y = (timeLeft * timeLeft * startY +
                        2 * timeLeft * t1 * controlY +
                        t1 * t1 * targetY) / (t2 * t2);

                // Hedefe ulaştıysa yok olmalı
                if (t1 >= t2) {
                    shouldBeRemoved = true;
                }

            } else {
                x += speed; // normal sağa hareket
            }
        }

    public void redirectTo(int targetX, int targetY) {
        this.isRedirected = true;
        this.startX = this.x;
        this.startY = this.y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.t1 = 0;
    }
    public boolean shouldBeRemoved() {
        return isRedirected && t1 >= t2;
    }
    // Yeni metod: throwTo (redirectTo'nun alias'ı)
    public void throwTo(int targetX, int targetY) {
        redirectTo(targetX, targetY);
    }
    public boolean isRedirected() {
        return isRedirected;
    }


    // Yeni metod: getY()
    public int getY() {
        return y;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return bitmap.getWidth();
    }
    public int getHeight() {
        return bitmap.getHeight();
    }


    public Rect getRect() {
        int padding = 10;
        return new Rect(
                x + padding,
                y + padding,
                x + bitmap.getWidth() - padding,
                y + bitmap.getHeight() - padding);
    }

    public boolean checkCollision(Cat cat) {
        return Rect.intersects(this.getRect(), cat.getRect());
    }
}
