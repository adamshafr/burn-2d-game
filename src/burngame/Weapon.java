package burngame;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;



/**
 *
 * 
 * @date December 25, 2024
 * @filename Weapon
 * @description Code for all weapons (knife, pistol, rifle). No projectile is ever fired. This is what manages attacking, reloading for both players and enemies, and makes them both take damage.
 */

public class Weapon {
    private String name;
    private int damage;
    private int fireRate; // Bullets per second
    private boolean automatic; // True for automatic weapons (AR), false for semi-automatic (Pistol)
    private Image sparkImage;
    private String sparkPath = "/burngame/icons/spark.png";
    private int clip = 24;
    public boolean reloading = false;    
    private long lastShotTime = 0;
    private int reloadTime = 3000;

    public Weapon(String name, boolean enemy) {
        this.name = name;
        switch (name) { //sets the stats of the weapon based on its name
            case "Pistol" -> {
                this.damage = 10;
                this.fireRate = 5;
                this.automatic = false;
            }
            case "Rifle" -> {
                this.damage = 10;
                this.fireRate = 10;
                this.automatic = true;
            }
            case "Knife" -> {
                this.damage = 100;
                this.fireRate = 10;
                this.automatic = false;
            }
            default -> {
            }
        }
        try { //we were originally going to make different spark images but never got to it
            sparkImage = Main.loadImage(sparkPath);
        } catch (IOException ex) {
            System.out.println("Failed");
        }
         if (enemy) fireRate /= 2;
         if (enemy) reloadTime = 1500;
    }

    public boolean canShoot(boolean enemy) throws UnsupportedAudioFileException{ //checks if the weapon is able to be shot
        if (reloading){ //if reloading its false
                return false;
            }
        if (clip == 0){ //if clip is empty reloads and returns false
            reload(enemy);
            return false;
        }
        long currentTime = System.currentTimeMillis(); //if shot too recently returns false
        if (fireRate == 0) return false; 
        if (currentTime - lastShotTime >= 1000 / fireRate) {
            lastShotTime = currentTime;
            return true;
        }
        return false;
        
    }
  
    public void reload(boolean enemy) throws UnsupportedAudioFileException{ //reloads the weapon (only triggers for AR)
        if (reloading) return;
            if (!enemy) Main.playSound("ReloadAR") ; 
            new Thread(() -> {
            try {
                // Set the label to visible
              reloading = true;

                // Wait for 3 seconds
                Thread.sleep(reloadTime);

                // Set the label to invisible
                clip = 24;
                reloading = false;
            } catch (InterruptedException e) {
            }
        }).start(); //this makes a smooth reloading experience
        }

 
    
    public String getName() {
        return name; //returns weapon name
    }
  
   
   public boolean isAutomatic() {
    return automatic; //returns if weapon is automatic
}
    public int getClip(){
        return clip; //returns weapon clip
    }
    public void setFirerate(int rate){
        this.fireRate = rate; //setter for fire rate
    }
   
   public void resetClip(){ //resets clips
                this.clip = 24;
   }
   
   private boolean isLoud(boolean enemy){
       return (name.equals("Rifle")|| ((name.equals("Pistol")) && enemy));
   }
   
public void shoot(int x, int y, int startX, int startY, boolean enemy) throws UnsupportedAudioFileException { //x and y is the x and y to shoot at.
//This is the main part of the weapon class, shooting. shooting works by making a line and checking for intersections on that line using point2d. It then finds the closest of the intersections and
//that is where it will draw the spark (or damage enemy/player if it hits one of them first)
    if (!canShoot(enemy)) return; //does not shoot if cannot shoot
    if (name.equals("Rifle")) clip--; //lowers clip if rifle
    if (name.equals("Pistol") && !enemy) {
        Main.playSound("pistolSuppressed");
    }
    else{
        Main.playSound(name);
    } //plays sound
     if (isLoud(enemy) && Main.level != null) {
        int worldShotX = startX + Main.worldX;
        int worldShotY = startY + Main.worldY;
        Main.level.alertEnemies(worldShotX, worldShotY);
    }
    Enemy damageEnemy = null;
    // Adjust position to world coordinates
    int adjustedX = x + Main.worldX;
    int adjustedY = y + Main.worldY;

    // Calculate direction vector
    double directionX = adjustedX - (startX + Main.worldX);
    double directionY = adjustedY - (startY + Main.worldY);

    // Get proper direction vector
    double length = Math.sqrt(directionX * directionX + directionY * directionY);
    if (length == 0) return;
    directionX /= length;
    directionY /= length;

    // Variables to track closest intersection
    double closestDistance;
    if(name.equals("Knife")){
        closestDistance = 100; //this is a range for the knife
    }else{
        closestDistance = Double.MAX_VALUE; //range for everything else is not limited
    }
    int sparkX = -1, sparkY = -1;
    boolean hitSomething = false;

    // Check ray against walls first
    for (Wall wall : Main.walls) {
        if (wall.isHardwall()) {
            Rectangle bounds = wall.getBounds(wall.x, wall.y);
            Point2D intersection = getRayWallIntersection(startX + Main.worldX, startY + Main.worldY, directionX, directionY, bounds);

            if (intersection != null) {
                double distance = intersection.distance(startX + Main.worldX, startY + Main.worldY);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    sparkX = (int) intersection.getX();
                    sparkY = (int) intersection.getY();
                    hitSomething = true;
                    
                }
            }
        }
       
    }

    if (enemy) {
        // Check if the ray intersects the player
        Rectangle bounds = Main.getPlayerBounds();
        bounds.x += Main.worldX;
        bounds.y += Main.worldY;
        Point2D intersection = getRayWallIntersection(startX + Main.worldX, startY + Main.worldY, directionX, directionY, bounds);

        if (intersection != null) {
            double distance = intersection.distance(startX + Main.worldX, startY + Main.worldY);
            if (distance < closestDistance) {
                Main.getPlayer().takeDamage(damage); //if it hit player before it hit a wall
                return;
            }
        }
    } else {
        // Check if the ray intersects any enemy
        for (Enemy e : Main.enemies) {
    Rectangle bounds = e.getBounds();
    Point2D intersection = getRayWallIntersection(startX + Main.worldX, startY + Main.worldY, directionX, directionY, bounds);

    if (intersection != null) {
        double distance = intersection.distance(startX + Main.worldX, startY + Main.worldY);
        if (distance < closestDistance) {
            closestDistance = distance; 
            damageEnemy = e;         
        }
    }
}
    }
    if (damageEnemy != null){ //if it hit an enemy then it damages the closest one
        damageEnemy.takeDamage(damage);
        return;
    }
    // If no enemies or player were hit, create a spark only for wall hits
    if (hitSomething && !name.equals("Knife")) {//no spark for knife
        Main.sparks.add(new Spark(sparkX, sparkY, sparkImage));
    }
}




private Point2D getRayWallIntersection(double startX, double startY, double dirX, double dirY, Rectangle wall) {
    // Wall edges (top, bottom, left, right)
    Line2D[] edges = {
        new Line2D.Double(wall.x, wall.y, wall.x + wall.width, wall.y),              // Top edge
        new Line2D.Double(wall.x, wall.y, wall.x, wall.y + wall.height),            // Left edge
        new Line2D.Double(wall.x + wall.width, wall.y, wall.x + wall.width, wall.y + wall.height), // Right edge
        new Line2D.Double(wall.x, wall.y + wall.height, wall.x + wall.width, wall.y + wall.height) // Bottom edge
    }; 

    // Track the closest intersection
    Point2D closestIntersection = null;
    double closestDistance = Double.MAX_VALUE;

    for (Line2D edge : edges) {
        Point2D intersection = getLineIntersection(startX, startY, startX + dirX * 10000, startY + dirY * 10000, edge.getX1(), edge.getY1(), edge.getX2(), edge.getY2()); //*10000 to make sure the ray is far enough
        if (intersection != null) {
            // Calculate distance to the intersection point
            double distance = intersection.distance(startX, startY);

            // Update the closest intersection
            if (distance < closestDistance) {
                closestDistance = distance;
                closestIntersection = intersection;
            }
        }
    }

    return closestIntersection; // Return the closest intersection
}


private Point2D getLineIntersection(double x1, double y1, double x2, double y2,
                                    double x3, double y3, double x4, double y4) {
    double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

    // Lines are parallel if denominator is zero
    if (denom == 0) return null;

    double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denom;
    double u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denom;

    // Check if intersection is within the ray and wall segment
    if (t >= 0 && u >= 0 && u <= 1) {
        double intersectX = x1 + t * (x2 - x1);
        double intersectY = y1 + t * (y2 - y1); 
        return new Point2D.Double(intersectX, intersectY); // Return the intersection point
    }

    return null; // No valid intersection
}

    
}