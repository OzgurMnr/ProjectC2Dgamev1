package com.example.projectc2dgame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class ThrowableObstacle {
    private Bitmap bitmap;
    private int x, y;
    private int speed = +15;

    public ThrowableObstacle(Context context, int startX, int startY) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.throwable_obstacle);
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);

        this.x = startX;
        this.y = startY;
    }

    public void update() {
        x += speed; // Sağ doğru gider
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

    // Çarpışma dikdörtgeni
    public Rect getRect() {
        int padding = 20;
        return new Rect(
                x + padding,
                y + padding,
                x + bitmap.getWidth() - padding,
                y + bitmap.getHeight() - padding);
    }

    // Sadece Cat ile çarpışma kontrolü
    public boolean checkCollision(Cat cat) {
        return Rect.intersects(this.getRect(), cat.getRect());
    }
}
