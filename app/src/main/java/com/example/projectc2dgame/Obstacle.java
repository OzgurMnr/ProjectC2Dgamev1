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
    private int speed = -10;  // Engelin hareket hızı (sola doğru hareket için negatif olmalı)

    public Obstacle(Context context, int screenHeight) {
        // Engel görselini yükle ve ölçeklendir
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle);
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);

        // Engel ekranın sağından başlar
        x = (Resources.getSystem().getDisplayMetrics().widthPixels)/2;

        // Engel için rastgele hat seçimi ve y konumu belirle
        int lane = (int) (Math.random() * 3);
        double margin = screenHeight / 30.0;

        switch (lane) {
            case 0:
                y = (int) margin; // üst hat
                break;
            case 1:
                y = (int) ((screenHeight - bitmap.getHeight()) / 2.0); // orta hat
                break;
            case 2:
            default:
                y = (int) (screenHeight - bitmap.getHeight() - margin); // alt hat
                break;
        }
    }

    // Engel sola doğru hareket eder
    public void update() {
        x += speed;  // sola hareket için hız negatif olmalı
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

    // Çarpışma için engelin dikdörtgen alanı
    public Rect getRect() {
        return new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
    }

    // Kedi ile çarpışma kontrolü yap
    public boolean checkCollision(Cat cat) {
        return Rect.intersects(this.getRect(), cat.getRect());
    }
}
