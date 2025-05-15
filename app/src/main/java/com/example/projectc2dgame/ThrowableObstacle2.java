package com.example.projectc2dgame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ThrowableObstacle2 {
    private Bitmap bitmap;
    private int x, y;
    private int speed = 15;

    public ThrowableObstacle2(Context context, int startX, int startY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.throwable_obstacle2);
        bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

        this.x = startX;
        this.y = startY;
    }

    public void update() {
        x -= speed; // Sola doğru gider
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public Rect getRect() {
        return new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public boolean checkCollision(Obstacle obstacle) {
        return Rect.intersects(this.getRect(), obstacle.getRect());
    }

    public boolean checkCollision(Obstacle2 obstacle2) {
        return Rect.intersects(this.getRect(), obstacle2.getRect());
    }

    // Cat ile çarpışma kontrolü
    public boolean checkCollision(Cat cat) {
        return Rect.intersects(this.getRect(), cat.getRect());
    }
}
