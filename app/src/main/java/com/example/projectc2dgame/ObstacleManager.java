    package com.example.projectc2dgame;

    import android.content.Context;
    import android.graphics.Canvas;
    import android.util.Log;

    import java.util.ArrayList;
    import java.util.Iterator;
    import java.util.List;

    public class ObstacleManager {

        private Context context;
        private int screenHeight;
        private int screenWidth;

        private List<Obstacle> obstacles;
        private List<Obstacle2> obstacles2;
        private List<ThrowableObstacle> throwableObstacles;
        private List<ThrowableObstacle2> throwableObstacles2;

        private long lastObstacleSpawnTime1;
        private long lastObstacleSpawnTime2;
        private long lastThrowableSpawnTime1;
        private long lastThrowableSpawnTime2;

        private final long obstacleSpawnDelay1 = 15000;         // Normal engel 1 -> 2 saniye
        private final long obstacleSpawnDelay2 = 15000;         // Normal engel 2 -> 3 saniye
        private final long throwableSpawnDelay1 = 2000;        // Fırlatılabilir engel 1 -> 4 saniye (daha uzun)
        private final long throwableSpawnDelay2 = 2000;        // Fırlatılabilir engel 2 -> 5 saniye (daha uzun)

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

        private int getLaneY(int screenHeight, int bitmapHeight) {
            int lane = (int) (Math.random() * 3);  // 0: alt, 1: orta, 2: üst
            int partHeight = screenHeight / 12;
            int partIndexFromBottom;

            if (lane == 0) {
                partIndexFromBottom = 1;
            } else if (lane == 1) {
                partIndexFromBottom = 4;
            } else {
                partIndexFromBottom = 7;
            }

            int bottomY = screenHeight - (partIndexFromBottom * partHeight);


            return bottomY - bitmapHeight;
        }


        public void update() {
            long currentTime = System.currentTimeMillis();

            // Obstacle 1
            if (currentTime - lastObstacleSpawnTime1 > obstacleSpawnDelay1) {
                int bitmapHeight = 120;
                int startY = getLaneY(screenHeight, bitmapHeight);
                obstacles.add(new Obstacle(context, startY));
                lastObstacleSpawnTime1 = currentTime;
            }

            // Obstacle 2
            if (currentTime - lastObstacleSpawnTime2 > obstacleSpawnDelay2) {
                int bitmapHeight = 120;
                int startY = getLaneY(screenHeight, bitmapHeight);
                obstacles2.add(new Obstacle2(context, startY));
                lastObstacleSpawnTime2 = currentTime;
            }

            // ThrowableObstacle
            if (currentTime - lastThrowableSpawnTime1 > throwableSpawnDelay1) {
                int startX = screenWidth;
                int startY = getLaneY(screenHeight, 120);
                throwableObstacles.add(new ThrowableObstacle(context, startX, startY));
                lastThrowableSpawnTime1 = currentTime;
            }

            if (currentTime - lastThrowableSpawnTime2 > throwableSpawnDelay2) {
                int startX2 = screenWidth;
                int startY2 = getLaneY(screenHeight, 120);
                throwableObstacles2.add(new ThrowableObstacle2(context, startX2, startY2));
                lastThrowableSpawnTime2 = currentTime;
            }

            // Obstacle update
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
                if (obstacle2.getX() + obstacle2.getWidth() < 0) {
                    it2.remove();
                }
            }


            Iterator<ThrowableObstacle> it3 = throwableObstacles.iterator();
            while (it3.hasNext()) {
                ThrowableObstacle throwable = it3.next();
                throwable.update();
                if (throwable.getX() + throwable.getWidth() < 0 || throwable.shouldBeRemoved()) {
                    it3.remove();
                }
            }


            Iterator<ThrowableObstacle2> it4 = throwableObstacles2.iterator();
            while (it4.hasNext()) {
                ThrowableObstacle2 throwable2 = it4.next();
                throwable2.update();
                if (throwable2.getX() + throwable2.getWidth() < 0 || throwable2.shouldBeRemoved()) {
                    it4.remove();
                }
            }
        }


        public void draw(Canvas canvas) {
            this.screenHeight = canvas.getHeight();  // Gerçek canvas yüksekliğini güncelle

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
            boolean collided = false;

            // Normal obstacle1
            Iterator<Obstacle> it1 = obstacles.iterator();
            while (it1.hasNext()) {
                Obstacle obstacle = it1.next();
                if (obstacle.checkCollision(cat)) {
                    it1.remove(); // Çarpışan kutuyu sil
                    collided = true;
                    break;
                }
            }

            // Normal obstacle2
            Iterator<Obstacle2> it2 = obstacles2.iterator();
            while (it2.hasNext()) {
                Obstacle2 obstacle2 = it2.next();
                if (obstacle2.checkCollision(cat)) {
                    it2.remove(); // Çarpışan kutuyu sil
                    collided = true;
                    break;
                }
            }

            // ThrowableObstacle
            Iterator<ThrowableObstacle> it3 = throwableObstacles.iterator();
            while (it3.hasNext()) {
                ThrowableObstacle throwable = it3.next();
                if (throwable.checkCollision(cat)) {
                    it3.remove(); // Çarpışan kutuyu sil
                    collided = true;
                    break;
                }
            }

            // ThrowableObstacle2
            Iterator<ThrowableObstacle2> it4 = throwableObstacles2.iterator();
            while (it4.hasNext()) {
                ThrowableObstacle2 throwable2 = it4.next();
                if (throwable2.checkCollision(cat)) {
                    it4.remove(); // Çarpışan kutuyu sil
                    collided = true;
                    break;
                }
            }

            return collided;
        }

        //Fırlatma
        public void throwFrontObstacle(Cat fromCat, Cat toCat) {
            int fromX = fromCat.getX();
            int fromY = fromCat.getY();
            int toX = toCat.getX();

            boolean isFacingLeft = fromCat.isReversed;
            int maxDistance = 800; // Test için daha geniş bir alan

            int catCenterX = fromX + fromCat.getWidth() / 2;
            int catCenterY = fromY + fromCat.getHeight() / 2;

            ThrowableObstacle closestObstacle = null;
            int closestDistance = Integer.MAX_VALUE;

            for (ThrowableObstacle obstacle : throwableObstacles) {
                if (obstacle.isRedirected()) continue;

                int obstacleCenterX = obstacle.getX() + obstacle.getWidth() / 2;
                int obstacleCenterY = obstacle.getY() + obstacle.getHeight() / 2;
                int deltaX = obstacleCenterX - catCenterX;

                int distance = Math.abs(deltaX);
                Log.d("THROW_DEBUG", "Obstacle1 - CatX: " + catCenterX + " | ObstacleX: " + obstacleCenterX +
                        " | DeltaX: " + deltaX + " | Distance: " + distance);

                if (distance < maxDistance && distance < closestDistance) {
                    closestObstacle = obstacle;
                    closestDistance = distance;
                }
            }

            if (closestObstacle != null) {
                closestObstacle.throwTo(toX, closestObstacle.getY());
                return;
            }

            ThrowableObstacle2 closestObstacle2 = null;
            closestDistance = Integer.MAX_VALUE;

            for (ThrowableObstacle2 obstacle : throwableObstacles2) {
                if (obstacle.isRedirected()) continue;

                int obstacleCenterX = obstacle.getX() + obstacle.getWidth() / 2;
                int obstacleCenterY = obstacle.getY() + obstacle.getHeight() / 2;
                int deltaX = obstacleCenterX - catCenterX;

                int distance = Math.abs(deltaX);
                Log.d("THROW_DEBUG", "Obstacle2 - CatX: " + catCenterX + " | ObstacleX: " + obstacleCenterX +
                        " | DeltaX: " + deltaX + " | Distance: " + distance);

                if (distance < maxDistance && distance < closestDistance) {
                    closestObstacle2 = obstacle;
                    closestDistance = distance;
                }
            }

            if (closestObstacle2 != null) {
                closestObstacle2.throwTo(toX, closestObstacle2.getY());
            }
        }

    }