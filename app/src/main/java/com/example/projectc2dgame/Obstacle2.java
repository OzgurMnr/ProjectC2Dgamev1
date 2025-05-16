package com.example.projectc2dgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Obstacle2 {
    private Bitmap bitmap;   // Engel resmi
    private int x, y;        // Engel pozisyonu
    private int speed = -15;  // Engelin hareket hızı (pozitif, sağa doğru hareket için)

    public Obstacle2(Context context, int startY) {
        // Engel görselini yükle ve ölçeklendir
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle2);
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);

        // Engel ekranın ortasından başlar (isteğe göre değiştirilebilir)
        x = (Resources.getSystem().getDisplayMetrics().widthPixels) / 2;

        // Y konumu dışarıdan alınır
        y = startY;
    }

    // Engel sağa doğru hareket eder
    public void update() {
        x -= speed;  // sağa doğru hareket
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
        int padding = 20; // Dilersen 40 yapabilirsin, kediyle aynı olsun diye
        return new Rect(
                x + padding,
                y + padding,
                x + bitmap.getWidth() - padding,
                y + bitmap.getHeight() - padding
        );
    }

    // Kedi ile çarpışma kontrolü yap
    public boolean checkCollision(Cat cat) {
        return Rect.intersects(this.getRect(), cat.getRect());
    }
}
