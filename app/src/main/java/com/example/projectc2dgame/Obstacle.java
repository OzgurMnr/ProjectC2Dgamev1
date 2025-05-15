
package com.example.projectc2dgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Obstacle {
    private Bitmap sharedBitmap; // Engel resmi
    private int x, y;      // Engel pozisyonu
    private int speed = 10; // Engel sola doğru ne kadar hızlı gidecek


    public Obstacle(Context context, int screenHeight) {
        // Engel görseli, drawable klasöründeki bir resim (obstacle.png gibi)
        sharedBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacle);
        sharedBitmap = Bitmap.createScaledBitmap(sharedBitmap, 200, 200, true);
        // Engel ekranın sağından başlar
        x = (Resources.getSystem().getDisplayMetrics().widthPixels)/2;

        // Engel ekranın alt kısmında yer alır
        y = screenHeight - sharedBitmap.getHeight();
        int lane2 = (int) (Math.random() * 3); // 0-3 arası rastgele seç
        // Hat (lane2) 0 = üst, 1 = orta, 2 = alt, default= alt
        double margin =(int) (screenHeight/30.0);
        switch (lane2) {
            case 0:
                y = (int) margin;
                break;
            case 1:
                y = (int) ((screenHeight - sharedBitmap.getHeight()) / 2.0);
                break;
            case 2:
            default:
                y = (int) (screenHeight - sharedBitmap.getHeight() - margin);
                break;
        }
    }

    // Engel konumunu güncelle (her frame'de sola doğru hareket ettir)
    public void update() {
        x += speed;
    }

    // Ekrana engeli çizer
    public void draw(Canvas canvas) {
        canvas.drawBitmap(sharedBitmap, x, y, null);
    }

    // Getter metodları
    public int getX() {
        return x;
    }

    public int getWidth() {
        return sharedBitmap.getWidth();
    }
}

