package com.example.projectc2dgame;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;



public class Obstacle {
    private Bitmap bitmap;   // Engel resmi
    private int x, y;        // Engel pozisyonu
    private int speed = 15;  // Sağ doğru hareket için pozitif hız

    public Obstacle(Context context, int startY) {
        // Engel görselini yükle ve ölçeklendir
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle);
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);

        // Engel ekranın ortasından başlar
        x = (Resources.getSystem().getDisplayMetrics().widthPixels) / 2;

        // Y konumu dışarıdan parametre olarak atanır
        y = startY;
    }

    // Engel sağa doğru hareket eder
    public void update() {
        x += speed;  // sağa doğru
    }

    // Engeli ekrana çiz
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);

    }

    // Engel pozisyonu için getter
    public int getX() {
        return x;
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    // Çarpışma için dikdörtgen alan
    public Rect getRect() {
        int padding = 10;
        return new Rect(
                x + padding,
                y + padding,
                x + bitmap.getWidth() - padding,
                y + bitmap.getHeight() - padding
        );
    }

    // Kedi ile çarpışma kontrolü
    public boolean checkCollision(Cat cat) {
        return Rect.intersects(this.getRect(), cat.getRect());
    }
}
