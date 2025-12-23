package burngame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;


/**
 *
 * 
 * @date December 27, 2024
 * @filename Enemy
 * @description Code for the enemies. All the info for an enemy including their weapon, their actions, how they see, attack, move, die, how hard they are to fight. Also the parent class for bosses.
 */

public class Enemy {
    private int health;
    private int x, y; // Enemy's position in the world
    public Image enemyImage;
    public Weapon weapon;
    private boolean canSee;
    private Line2D lineofsight;
    private long lastSeenTime = -1; // Tracks when the player was last seen
    private int reactionTime; // Reaction time in milliseconds
    private int level;
    private double accuracy;
    public double angle = Math.random() * 6.28;
    public BufferedImage strip;
    public int iter = 80;
    private boolean bloodDraw = false;
    private Image imgblood;
    private int speed;
    private Image bodyImage;
    private int speedChange;
private double distance;
public int extraDiff = 0;
    public Enemy(int x, int y, String type, int lvl) {
        this.x = x;
        this.y = y;
        this.health = 30; // Default health
        this.weapon = new Weapon(type, true);
        this.level = lvl;
        lineofsight = new Line2D.Double(960, 540, x-Main.worldX, y-Main.worldY);
        // Load the enemy image
        try {
         if (level == -1){ //I was paniking and dimitri was not working so I randomly thought of this weird way to solve it, it works so yeah. -1 is only passed if it is dimitri.
             level = 5;
             strip = ImageIO.read(new File("src/burngame/icons/dimitri.png"));
             enemyImage = ImageIO.read(new File("src/burngame/icons/dimitri.png"));
             bodyImage = ImageIO.read(new File("src/burngame/icons/dimitridead.png")).getScaledInstance(145, 169, Image.SCALE_DEFAULT); //sets the images properly if it is Dimitri, who is the only knife boss.
         }else{
             strip = ImageIO.read(new File("src/burngame/icons/"+type+getImageLevel()+".png")); //filenames are formatted to work with this
            imgblood = ImageIO.read(new File("src/burngame/icons/blood.png"));
            bodyImage = ImageIO.read(new File("src/burngame/icons/dead"+getImageLevel()+".png")).getScaledInstance(145, 169, Image.SCALE_DEFAULT);
         }
        } catch (IOException ex) {
            System.out.println("Failed to load enemy image.");
        }
        
          if (!type.equals("Knife")) { //starting image for knife, this is the idle pose
              enemyImage = strip.getScaledInstance(169, 169, Image.SCALE_DEFAULT);
          }else{
              Image frame = strip.getSubimage(0, iter, 40, 40);
               enemyImage = frame.getScaledInstance(169, 169, Image.SCALE_DEFAULT);
          }
       loadEnemyLevel(); //loads the stats for the enemy
    }
    public void aniframe(BufferedImage strip, String type) { //animations for enemy
        if (!type.equals("Knife")) return;
        
        Image frame = strip.getSubimage(0, iter, 40, 40);
        enemyImage = frame.getScaledInstance(169, 169, Image.SCALE_DEFAULT);
    }

    // Method to reduce health when hit by a weapon
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            die(); // Trigger death logic
        }
        
         bloodDraw = true;

        // Create a new thread to reset bloodDraw after 0.5 seconds
        new Thread(() -> {
        try {
            Thread.sleep(230); // 500 milliseconds = 0.5 seconds
        } catch (InterruptedException e) {
        }
        bloodDraw = false; // Reset bloodDraw
    }).start();
    }

    // What happens when the enemy dies
    private void die() { //regular enemies nothing interesting happens, they just die
        Main.bodies.add(new Body(x,y,angle,bodyImage));
        Main.enemies.remove(this);
        lineofsight = null;
        bloodDraw = true;
    }
    
    private void loadEnemyLevel(){ //loads the reaction time, accuracy, and speed for enemies.
        switch (level){
            case 1-> {
            reactionTime = 800;
            accuracy = 0.1;
            speedChange = -2;
            break;
            }case 2-> {
            reactionTime = 350;
            accuracy = 0.3;
            speedChange = 2;
            break;
            }case 3->{
             reactionTime = 400;
            accuracy = 0.4;  
            speedChange = 2;
            break;
            }case 4->{
             reactionTime = 270;
            accuracy = 0.5; 
            speedChange = 3;
            break;
            }case 5->{
             reactionTime = 260;
            accuracy = 0.7;
            speedChange = 4;
            break;
            }case 6->{
             reactionTime = 250;
            accuracy = 0.8;
            speedChange = 5; //these knife enemies are super fast
            break;
            }case 7->{
             reactionTime = 200;
            accuracy = 0.9;
            break;
            }
            
        }
    }
    
    private int getImageLevel(){ //this gets the imagelevel for file names, since in some levels enemies use the same image as others (vladimir and dimitris guards, ronans guards)
        return switch (level) {
            case 4, 5 -> 1;
            case 6 -> 3;
            default -> level;
        };
    }
    // Draw the enemy on the screen
    public void draw(Graphics g) { //draws the enemy
        if (health <=0)return;
        updateRotation();
        // Convert the enemy's world position to screen position
        int screenX = x - Main.worldX;
        int screenY = y - Main.worldY + extraDiff;
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        // Rotate around the center of the player
        g2d.rotate(angle, screenX, screenY);
        // Draw the enemy image
        g2d.drawImage(enemyImage, screenX - enemyImage.getWidth(null) / 2, screenY - enemyImage.getHeight(null) / 2, null);
        g2d.setTransform(oldTransform);
        checkSight();
        if (canSee){
         long currentTime = System.currentTimeMillis();
         moveToPlayer();
        if (lastSeenTime != -1 && currentTime - lastSeenTime >= reactionTime) {
            shoot(); // Shoot at the player
        }
        if ((currentTime - lastSeenTime)<= 1000){
            moveToPlayer();
        }
        }
        
        if (bloodDraw){
            g2d.drawImage(imgblood,screenX-(imgblood.getWidth(null)/2),screenY-(imgblood.getHeight(null)/2),null);
        }
        updateSpeed();
        resolveCollisions();
    }
    
   
    // Get the enemy's bounding box for collision detection
    public Rectangle getBounds() { 
        int width = 100;
        int height = 33;
        AffineTransform transform = AffineTransform.getRotateInstance(angle, x, y);
        Rectangle2D originalRect = new Rectangle2D.Double(x - width / 2.0, y - height / 2.0, width,height);
        Shape rotatedRect = transform.createTransformedShape(originalRect);
        return rotatedRect.getBounds();
    }


    private void updateSpeed(){ //if
        if (this.weapon.getName().equals("Knife")){
            speed = 7 + speedChange;
        }
    }
    
    private void updateRotation(){
        if (Main.getPlayer().playable){
        if (canSee){
             int enemyCenterX = x + enemyImage.getWidth(null) / 2;
               int enemyCenterY = y + enemyImage.getHeight(null) / 2;

        // Player is always at (960, 540) in screen coordinates
        angle = Math.atan2(540 - (enemyCenterY - Main.worldY), 960 - (enemyCenterX - Main.worldX))+1.57;
        }
        }
    }
    
    // Getter for health (optional)
    public int getHealth() {
        return health;
    }
    
    public void shoot() {
        if (Main.level.cutsceneHappened) return;
        if (Main.getPlayer().playable){
        if (this.weapon.getName().equals("Knife")&&distance>100){
            return;
        }
        if(this.weapon.getName().equals("Knife")){
            iter = 40;
        }
        // Base target is the player at the center of the screen
        int targetX = 960; 
        int targetY = 540; 

        // Apply accuracy offset
        double maxOffset = (2.0 - accuracy) * 200; // Maximum offset based on accuracy
        double rand = Math.random();
        if (rand >= accuracy){
        targetX += (int) ((Math.random() * 2) * maxOffset); // Random offset between -maxOffset and +maxOffset
        targetY += (int) ((Math.random() * 2) * maxOffset);
        }
        try {
            // Call weapon's shoot method with adjusted target
            weapon.shoot(targetX, targetY, x - Main.worldX, y - Main.worldY, true);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Enemy.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }

    private void checkSight() { //Line of sight check
        lineofsight = new Line2D.Double(960, 540, x - Main.worldX, y - Main.worldY);
        canSee = true; // Assume clear sight unless proven otherwise
        Rectangle enemyBounds = this.getBounds();
        enemyBounds.x -= Main.worldX;
        enemyBounds.y -= Main.worldY;

        // Check if enemy is within the visible screen bounds
        if (!enemyBounds.intersects(new Rectangle(0, 0, 1920, 1080))) {
            canSee = false;
        }

        // Check if any walls block the line of sight
        for (Wall wall : Main.walls) {
            if (wall.isHardwall()){
            Rectangle bounds = wall.getBounds(wall.x - Main.worldX, wall.y - Main.worldY);

            if (lineofsight.intersects(bounds)) {
                canSee = false; // Line of sight blocked
                break;
            }
        }
        }
        // Update hostility and reaction time
        if (canSee) {
            if (lastSeenTime == -1) {
                lastSeenTime = System.currentTimeMillis(); // Record first sighting time
            }
        } else {
            lastSeenTime = -1; // Reset if the player is not visible
        }
        
    }

    public void moveToPlayer() { //moves to player (not proper pathfinding for now, just kinda stupidly walk towards player when they see them
        if (Main.level.cutsceneHappened) return;
        if (Main.getPlayer().playable){
        // Calculate the direction vector from the enemy to the player
        double deltaX = (960 + Main.worldX) - x; // Player's global X position
        double deltaY = (540 + Main.worldY) - y; // Player's global Y position

        // Calculate the distance to normalize the direction
        distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Avoid division by zero
        if (distance == 0) return;

        // Normalize the direction and scale by speed
        double moveX = (deltaX / distance) * speed;
        double moveY = (deltaY / distance) * speed;

        // Update the enemy's position
        x += moveX;
        y += moveY;
        aniframe(strip, this.weapon.getName());
        if(this.weapon.getName().equals("Knife")){
            if(iter == 360){
                iter = 80;
            }
            if(Main.counter == 6){
                iter += 40;
            }
        }
        resolveCollisions();
        }
    }
 public void resolveCollisions() { //same exact resolve collisions method as the player has, and the same pushout method
     Rectangle hitbox = this.getBounds();
     hitbox.x -= Main.worldX;
     hitbox.y -= Main.worldY;
     Rectangle playerHitbox = Main.getPlayerBounds();
     pushOut(hitbox,playerHitbox);
        
        for (Enemy e : Main.enemies){ //pushout for other enemies, the player, and the walls
            if (e != this){
                
            Rectangle enemyBounds = e.getBounds();
            enemyBounds.x -= Main.worldX;
            enemyBounds.y -= Main.worldY;
            pushOut(hitbox,enemyBounds);
        }
        }
        
        for (Wall wall : Main.walls) {
            Rectangle2D wallBounds = wall.getBounds(wall.x - Main.worldX, wall.y - Main.worldY);
            pushOut(hitbox, wallBounds);
        }
    }

 
 private void pushOut(Rectangle hitbox, Rectangle2D wallBounds){
     if (hitbox.intersects(wallBounds)) {
                double disX, disY;

                // Horizontal collision
                if (hitbox.getCenterX() < wallBounds.getCenterX()) {
                    disX = wallBounds.getMinX() - hitbox.getMaxX();
                } else {
                    disX = wallBounds.getMaxX() - hitbox.getMinX();
                }

                // Vertical collision
                if (hitbox.getCenterY() < wallBounds.getCenterY()) {
                    disY = wallBounds.getMinY() - hitbox.getMaxY();
                } else {
                    disY = wallBounds.getMaxY() - hitbox.getMinY();
                }

                // Apply the smaller push-out value
                if (Math.abs(disX) < Math.abs(disY)) {
                    x += disX;
                } else {
                    y += disY;
                }
            }
    }
 
 
        public int get(String xy){ //get the x or y.
            if (xy.equals("x")) return x;
            if (xy.equals("y")) return y;
            return 1;
        }
}

