package com.example.projectc2dgame;

import android.content.Context;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObstacleManager {

    private Context context;
    private int screenHeight;
    private int screenWidth;  // Eklendi

    private List<Obstacle> obstacles;
    private List<Obstacle2> obstacles2;
    private List<ThrowableObstacle> throwableObstacles;
    private List<ThrowableObstacle2> throwableObstacles2;

    private long lastObstacleSpawnTime1;
    private long lastObstacleSpawnTime2;
    private long lastThrowableSpawnTime1;
    private long lastThrowableSpawnTime2;

    private long obstacleSpawnDelay1 = 1000;         // Normal engel 1 -> 2 saniye
    private long obstacleSpawnDelay2 = 1000;         // Normal engel 2 -> 3 saniye
    private long throwableSpawnDelay1 = 4000;        // Fırlatılabilir engel 1 -> 4 saniye (daha uzun)
    private long throwableSpawnDelay2 = 5000;        // Fırlatılabilir engel 2 -> 5 saniye (daha uzun)

    public ObstacleManager(Context context, int screenWidth, int screenHeight) {
        this.context = context;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

        obstacles = new ArrayList<>();
        obstacles2 = new ArrayList<>();
        throwableObstacles = new ArrayList<>();
        throwableObstacles2 = new ArrayList<>();

        lastObstacleSpawnTime1 = System.currentTimeMillis();
        lastObstacleSpawnTime2 = System.currentTimeMillis();
        lastThrowableSpawnTime1 = System.currentTimeMillis();
        lastThrowableSpawnTime2 = System.currentTimeMillis();
    }

    public void update() {
        long currentTime = System.currentTimeMillis();

        // Normal Obstacle ekle
        if (currentTime - lastObstacleSpawnTime1 > obstacleSpawnDelay1) {
            obstacles.add(new Obstacle(context, screenHeight));
            lastObstacleSpawnTime1 = currentTime;
        }

        if (currentTime - lastObstacleSpawnTime2 > obstacleSpawnDelay2) {
            obstacles2.add(new Obstacle2(context, screenHeight));
            lastObstacleSpawnTime2 = currentTime;
        }

        // ThrowableObstacle ekle (daha seyrek)
        if (currentTime - lastThrowableSpawnTime1 > throwableSpawnDelay1) {
            int startX = screenWidth;
            int startY = (int)(Math.random() * screenHeight);
            throwableObstacles.add(new ThrowableObstacle(context, startX, startY));
            lastThrowableSpawnTime1 = currentTime;
        }

        if (currentTime - lastThrowableSpawnTime2 > throwableSpawnDelay2) {
            int startX2 = screenWidth;
            int startY2 = (int)(Math.random() * screenHeight);
            throwableObstacles2.add(new ThrowableObstacle2(context, startX2, startY2));
            lastThrowableSpawnTime2 = currentTime;
        }





    // Normal Obstacle güncelle ve ekrandan çıkarsa kaldır
        Iterator<Obstacle> it1 = obstacles.iterator();
        while (it1.hasNext()) {
            Obstacle obstacle = it1.next();
            obstacle.update();
            if (obstacle.getX() + obstacle.getWidth() < 0) {
                it1.remove();
            }
        }

        Iterator<Obstacle2> it2 = obstacles2.iterator();
        while (it2.hasNext()) {
            Obstacle2 obstacle2 = it2.next();
            obstacle2.update();
            if (obstacle2.getX() > screenWidth) {  // sağa doğru hareket eden için sınır kontrolü
                it2.remove();
            }
        }

        // ThrowableObstacle güncelle ve ekrandan çıkarsa kaldır
        Iterator<ThrowableObstacle> it3 = throwableObstacles.iterator();
        while (it3.hasNext()) {
            ThrowableObstacle throwable = it3.next();
            throwable.update();
            if (throwable.getX() + throwable.getWidth() < 0) {
                it3.remove();
            }
        }

        Iterator<ThrowableObstacle2> it4 = throwableObstacles2.iterator();
        while (it4.hasNext()) {
            ThrowableObstacle2 throwable2 = it4.next();
            throwable2.update();
            if (throwable2.getX() > screenWidth) {  // sola hareket eden için sınır kontrolü
                it4.remove();
            }
        }
    }

    public void draw(Canvas canvas) {
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(canvas);
        }
        for (Obstacle2 obstacle2 : obstacles2) {
            obstacle2.draw(canvas);
        }
        for (ThrowableObstacle throwable : throwableObstacles) {
            throwable.draw(canvas);
        }
        for (ThrowableObstacle2 throwable2 : throwableObstacles2) {
            throwable2.draw(canvas);
        }
    }

    // Çarpışma kontrolü (tüm engeller)
    public boolean checkCollision(Cat cat) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.checkCollision(cat)) return true;
        }
        for (Obstacle2 obstacle2 : obstacles2) {
            if (obstacle2.checkCollision(cat)) return true;
        }
        for (ThrowableObstacle throwable : throwableObstacles) {
            if (throwable.checkCollision(cat)) return true;
        }
        for (ThrowableObstacle2 throwable2 : throwableObstacles2) {
            if (throwable2.checkCollision(cat)) return true;
        }
        return false;
    }
}
