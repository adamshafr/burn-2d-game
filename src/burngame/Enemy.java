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
import java.awt.Point;
import java.util.List;


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
    private boolean stat;
    
    
    // ====== AI STATE SYSTEM ======
private enum AIState { IDLE, ALERTED, CHASING, SHOOTING}
private AIState aiState = AIState.IDLE;

// Spawn position (home)
private final int spawnX;
private final int spawnY;

// Returning-to-home behavior
private boolean returningHome = false;
private long arrivedAtLastKnownTime = -1;

// Timings (tweak if needed)
private static final long SEARCH_TIME_MS = 3000; // wait 3s at last known pos
private static final int REPLAN_DELAY = 15;

public List<Point> path = null;
private int pathIndex = 0;
private int replanCooldown = 0;

private Point lastKnownPlayerPos = null;

private boolean isGunEnemy;
private final int HEARING_RADIUS = 2000;

private final int GUN_MIN_DIST = 140;
private final int GUN_MAX_DIST = 420;




public int extraDiff = 0;
    public Enemy(int x, int y, String type, int lvl, boolean stat) {
        this.x = x;
        this.y = y;
        this.spawnX = x;
        this.spawnY = y;
        this.health = 30; // Default health
        this.weapon = new Weapon(type, true);
        this.level = lvl;
        this.stat = stat;
        isGunEnemy = !weapon.getName().equals("Knife");
        lineofsight = new Line2D.Double(960, 540, x-Main.worldX, y-Main.worldY);
        // Load the enemy image
        try {
         if (level == -1){ //I was paniking and dimitri was not working so I randomly thought of this weird way to solve it, it works so yeah. -1 is only passed if it is dimitri.
             level = 5;
             strip = Main.loadImage("/burngame/icons/dimitri.png");
             enemyImage = strip;
             bodyImage = Main.loadImage("/burngame/icons/dimitridead.png").getScaledInstance(145, 169, Image.SCALE_DEFAULT); //sets the images properly if it is Dimitri, who is the only knife boss.
         }else{
             strip = Main.loadImage("/burngame/icons/"+type+getImageLevel()+".png"); //filenames are formatted to work with this
            imgblood = Main.loadImage("/burngame/icons/blood.png");
            bodyImage = Main.loadImage("/burngame/icons/dead"+getImageLevel()+".png").getScaledInstance(145, 169, Image.SCALE_DEFAULT);
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
        if (stat){
            speed = 0;
            speedChange = 0;
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
        checkSight();
        updateAI();
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
        

        if (canSee) {
            long currentTime = System.currentTimeMillis();
            if (lastSeenTime != -1 && currentTime - lastSeenTime >= reactionTime) {
                shoot();
            }
        }

        
        if (bloodDraw){
            g2d.drawImage(imgblood,screenX-(imgblood.getWidth(null)/2),screenY-(imgblood.getHeight(null)/2),null);
        }
        updateSpeed();
   //     resolveCollisions();
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


    private void updateSpeed(){ 
    if (stat) {
        // Stationary enemy - never moves
        speed = 0;
    } else {
        // Normal enemy - use speed calculation
        speed = 7 + speedChange;
    }
}
    
private void updateRotation(){
    if (Main.getPlayer().playable) {
        // ALWAYS face player when they can see them
        if (canSee) {
            int enemyCenterX = x + enemyImage.getWidth(null) / 2;
            int enemyCenterY = y + enemyImage.getHeight(null) / 2;

            angle = Math.atan2(
                (540 + Main.worldY) - enemyCenterY,
                (960 + Main.worldX) - enemyCenterX
            ) + 1.57;
        } else {
            // When pathfinding, face the direction we're actually moving
            // Calculate movement direction from current frame
            double dx = 0;
            double dy = 0;
            
            if (path != null && pathIndex < path.size()) {
                Point target = path.get(pathIndex);
                dx = target.x - x;
                dy = target.y - y;
            } else if (aiState == AIState.CHASING && lastKnownPlayerPos != null) {
                dx = lastKnownPlayerPos.x - x;
                dy = lastKnownPlayerPos.y - y;
            }
            
            // Only update angle if we're actually moving somewhere
            if (dx != 0 || dy != 0) {
                angle = Math.atan2(dy, dx) + 1.57;
            }
            // If not moving, keep current angle
        }
    }
}
    
    // Getter for health 
    public int getHealth() {
        return health;
    }
    
    public void setSpeed(int x){
        speed = x;
    }
    
public void shoot() {
    if (Main.level.cutsceneHappened) return;
    if (Main.getPlayer().playable){
        // FOR KNIFE ENEMIES: Only attack when very close
        if (this.weapon.getName().equals("Knife")){
            if (distance > 60) { // Attack range for knife
                return;
            }
            iter = 40;
        }
        
        // Base target is the player at the center of the screen
        int targetX = 960; 
        int targetY = 540; 

        // Apply accuracy offset
        double maxOffset = (2.0 - accuracy) * 200;
        double rand = Math.random();
        if (rand >= accuracy){
            targetX += (int) ((Math.random() * 2) * maxOffset);
            targetY += (int) ((Math.random() * 2) * maxOffset);
        }
        try {
            weapon.shoot(targetX, targetY, x - Main.worldX, y - Main.worldY, true);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Enemy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

private void checkSight() {
    // Calculate distance to player (add this at the beginning)
    double dxToPlayer = (960 + Main.worldX) - x;
    double dyToPlayer = (540 + Main.worldY) - y;
    distance = Math.sqrt(dxToPlayer * dxToPlayer + dyToPlayer * dyToPlayer);
    
    // First, check if enemy is on screen (player can see enemy)
    Rectangle enemyBounds = this.getBounds();
    enemyBounds.x -= Main.worldX;
    enemyBounds.y -= Main.worldY;
    
    // Enemy must be on screen to see player
    if (!enemyBounds.intersects(new Rectangle(0, 0, 1920, 1080))) {
        canSee = false;
        lastSeenTime = -1;
        return;
    }
    
    // Player's world position
    int playerWorldX = 960 + Main.worldX;
    int playerWorldY = 540 + Main.worldY;
    
    // Enemy's world position
    int enemyWorldX = x;
    int enemyWorldY = y;
    
    // Create line from enemy to player IN WORLD COORDINATES
    lineofsight = new Line2D.Double(enemyWorldX, enemyWorldY, 
                                   playerWorldX, playerWorldY);
    
    canSee = true; // Assume clear sight
    
    // Check if any hardwalls block the line of sight
    for (Wall wall : Main.walls) {
        if (wall.isHardwall()) {
            Rectangle bounds = new Rectangle(wall.x, wall.y, wall.width, wall.height);
            
            if (lineofsight.intersects(bounds)) {
                canSee = false; // Line of sight blocked
                break;
            }
        }
    }
    
    // Update last seen time
    if (canSee) {
        if (lastSeenTime == -1) {
            lastSeenTime = System.currentTimeMillis();
        }
    } else {
        lastSeenTime = -1;
    }
}
/*
    public void moveToPlayer() { //moves to player (not proper pathfinding for now, just kinda stupidly walk towards player when they see them
        if (path != null) return;
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
        }
    }
*/
 public void resolveCollisions() {
    // Use axis-aligned bounding box for collision resolution
    Rectangle hitbox = new Rectangle(x - 50, y - 17, 100, 33);
    hitbox.x -= Main.worldX;
    hitbox.y -= Main.worldY;
    
    // Save original position
    int originalX = x;
    int originalY = y;
    
    /* Check collision with player
    Rectangle playerHitbox = Main.getPlayerBounds();
    pushOut(hitbox, playerHitbox);
    
    // Check collision with other enemies
    for (Enemy e : Main.enemies) {
        if (e != this) {
            Rectangle enemyBounds = e.getBounds();
            enemyBounds.x -= Main.worldX;
            enemyBounds.y -= Main.worldY;
            pushOut(hitbox, enemyBounds);
        }
    }
    */
    // Check collision with walls
    for (Wall wall : Main.walls) {
        Rectangle2D wallBounds = wall.getBounds(wall.x - Main.worldX, wall.y - Main.worldY);
        pushOut(hitbox, wallBounds);
    }
    
    // If we got pushed back significantly, we might be at a corner
    // Try to slide along the wall instead
    double moveX = x - originalX;
    double moveY = y - originalY;
    double moveDistance = Math.sqrt(moveX * moveX + moveY * moveY);
    
    // If we tried to move but got pushed back a lot, we're at a corner
    if (moveDistance < speed * 0.3 && (Math.abs(moveX) > 0.1 || Math.abs(moveY) > 0.1)) {
        // Try sliding perpendicular to the wall
        trySlideAroundCorner(originalX, originalY);
    }
}

private void trySlideAroundCorner(int originalX, int originalY) {
    // Try moving in 8 directions to get unstuck from corner
    int[][] directions = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1},  // Cardinal
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // Diagonal
    };
    
    for (int[] dir : directions) {
        int testX = originalX + dir[0] * 5;
        int testY = originalY + dir[1] * 5;
        
        Rectangle testHitbox = new Rectangle(testX - 50, testY - 17, 100, 33);
        testHitbox.x -= Main.worldX;
        testHitbox.y -= Main.worldY;
        
        boolean collision = false;
        
        // Check walls
        for (Wall wall : Main.walls) {
            Rectangle2D wallBounds = wall.getBounds(wall.x - Main.worldX, wall.y - Main.worldY);
            if (testHitbox.intersects(wallBounds)) {
                collision = true;
                break;
            }
        }
        
        // Check other enemies
        if (!collision) {
            for (Enemy e : Main.enemies) {
                if (e != this) {
                    Rectangle enemyBounds = e.getBounds();
                    enemyBounds.x -= Main.worldX;
                    enemyBounds.y -= Main.worldY;
                    if (testHitbox.intersects(enemyBounds)) {
                        collision = true;
                        break;
                    }
                }
            }
        }
        
        if (!collision) {
            // This direction is clear, move there
            x = testX;
            y = testY;
            return;
        }
    }
}

 
 private void pushOut(Rectangle hitbox, Rectangle2D wallBounds) {
    if (hitbox.intersects(wallBounds)) {
        double disX, disY;
        
        // Calculate overlap in both directions
        double overlapLeft = hitbox.getMaxX() - wallBounds.getMinX();
        double overlapRight = wallBounds.getMaxX() - hitbox.getMinX();
        double overlapTop = hitbox.getMaxY() - wallBounds.getMinY();
        double overlapBottom = wallBounds.getMaxY() - hitbox.getMinY();
        
        // Find the smallest overlap
        disX = (overlapLeft < overlapRight) ? -overlapLeft : overlapRight;
        disY = (overlapTop < overlapBottom) ? -overlapTop : overlapBottom;
        
        // Apply the smaller push-out value
        if (Math.abs(disX) < Math.abs(disY)) {
            x += disX;
        } else {
            y += disY;
        }
        
        // Update hitbox position for subsequent checks
        hitbox.x = x - 50 - Main.worldX;
        hitbox.y = y - 17 - Main.worldY;
    }
}
 
 
        public int get(String xy){ //get the x or y.
            if (xy.equals("x")) return x;
            if (xy.equals("y")) return y;
            return 1;
        }
        
public void hearGunshot(int gx, int gy) {
    double dx = gx - x;
    double dy = gy - y;
    double distanceSquared = dx*dx + dy*dy;
    
    // Only alert if within hearing radius
    if (distanceSquared <= HEARING_RADIUS * HEARING_RADIUS) {
        lastKnownPlayerPos = new Point(gx, gy);
        if (!canSee) { // Only use pathfinding if we can't see player
            aiState = AIState.ALERTED;
            replanCooldown = 0;
            path = null; // Clear old path
        }
    }
}

public void updateAI() {
    if (!Main.getPlayer().playable || Main.level.cutsceneHappened || stat) return;
    
    // Calculate distance to player
    double dxToPlayer = (960 + Main.worldX) - x;
    double dyToPlayer = (540 + Main.worldY) - y;
    distance = Math.sqrt(dxToPlayer * dxToPlayer + dyToPlayer * dyToPlayer);
    
    // DEBUG
    if (Main.debugMode && Main.counter == 0 && !Main.enemies.isEmpty() && this == Main.enemies.get(0)) {
        System.out.println("Enemy AI - State: " + aiState + 
                         " CanSee: " + canSee + 
                         " Distance: " + (int)distance +
                         " Path: " + (path != null ? path.size() : 0) +
                         " ReturningHome: " + returningHome);
    }

    /* ===================== RETURN HOME OVERRIDES ===================== */

    // If we see player or hear something, cancel return-home immediately
    if (canSee) {
        returningHome = false;
        arrivedAtLastKnownTime = -1;
    }

    /* ===================== NORMAL STATE UPDATES ===================== */

    // Update AI state based on sight
    if (canSee) {
        lastKnownPlayerPos = new Point(960 + Main.worldX, 540 + Main.worldY);
        
        if (isGunEnemy) {
            aiState = AIState.CHASING;
        } else {
            aiState = AIState.SHOOTING;
        }
        replanCooldown = 0;
    } 
    else if (lastKnownPlayerPos != null && !returningHome) {
        aiState = AIState.ALERTED;
        
        double distToLastKnown = Math.sqrt(
            Math.pow(lastKnownPlayerPos.x - x, 2) + 
            Math.pow(lastKnownPlayerPos.y - y, 2)
        );
        
        if (distToLastKnown < 50) {
            if (arrivedAtLastKnownTime == -1) {
                arrivedAtLastKnownTime = System.currentTimeMillis();
            }

            // Wait at last known position, then go home
            if (System.currentTimeMillis() - arrivedAtLastKnownTime > SEARCH_TIME_MS) {
                returningHome = true;
                lastKnownPlayerPos = null;
                path = null;
                replanCooldown = 0;
            }
        }
    } 
    else if (!returningHome) {
        aiState = AIState.IDLE;
    }

    // Decrease replan cooldown
    if (replanCooldown > 0) replanCooldown--;

    /* ===================== RETURNING HOME MODE ===================== */

    if (returningHome) {
        double distToHome = Math.sqrt(
            Math.pow(spawnX - x, 2) + 
            Math.pow(spawnY - y, 2)
        );

        // Reached home
        if (distToHome < 40) {
            returningHome = false;
            aiState = AIState.IDLE;
            path = null;
            return;
        }

        // Pathfind home
        if (path == null && replanCooldown == 0) {
            path = Main.level.getPathFinder().findPath(x, y, spawnX, spawnY, this);
            pathIndex = 0;
            replanCooldown = REPLAN_DELAY;
        }

        followPath();

        // IMPORTANT: skip avoidance while returning home
        resolveCollisions();
        return;
    }

    /* ===================== NORMAL PATHFINDING ===================== */

    if (aiState == AIState.ALERTED && lastKnownPlayerPos != null && replanCooldown == 0) {
        boolean needNewPath = (path == null || path.isEmpty());
        if (!needNewPath && pathIndex < path.size()) {
            Point currentTarget = path.get(pathIndex);
            double distToTarget = Math.sqrt(
                Math.pow(currentTarget.x - x, 2) + 
                Math.pow(currentTarget.y - y, 2)
            );
            needNewPath = (distToTarget > 100);
        }
        
        if (needNewPath) {
            path = Main.level.getPathFinder().findPath(x, y, lastKnownPlayerPos.x, lastKnownPlayerPos.y, this);
            pathIndex = 0;
            replanCooldown = REPLAN_DELAY;
        }
    }

    /* ===================== MOVEMENT LOGIC (UNCHANGED) ===================== */

    if (isGunEnemy) {
        if (canSee) {
            if (distance < GUN_MIN_DIST) {
                aiState = AIState.CHASING;
                double angleToPlayer = Math.atan2(dyToPlayer, dxToPlayer);
                lastKnownPlayerPos = new Point(
                    (int)(x - Math.cos(angleToPlayer) * GUN_MIN_DIST * 1.5),
                    (int)(y - Math.sin(angleToPlayer) * GUN_MIN_DIST * 1.5)
                );
                replanCooldown = 0;
            } 
            else if (distance > GUN_MAX_DIST) {
                aiState = AIState.CHASING;
                lastKnownPlayerPos = new Point(960 + Main.worldX, 540 + Main.worldY);
                replanCooldown = 0;
            } 
            else {
                aiState = AIState.SHOOTING;
                path = null;
            }
        }
        
        if ((aiState == AIState.CHASING || aiState == AIState.ALERTED) && lastKnownPlayerPos != null) {
            followPath();
        }
    } 
    else {
        if (canSee) {
            if (distance > 50) {
                double moveX = (dxToPlayer / distance) * speed;
                double moveY = (dyToPlayer / distance) * speed;
                
                for (Enemy e : Main.enemies) {
                    if (e == this || e == null || e.health <= 0) continue;
                    
                    double dxToEnemy = e.x - x;
                    double dyToEnemy = e.y - y;
                    double distToEnemy = Math.sqrt(dxToEnemy*dxToEnemy + dyToEnemy*dyToEnemy);
                    
                    if (distToEnemy < 80 && distToEnemy > 0) {
                        double pushStrength = 1.5 * (1.0 - (distToEnemy / 80.0));
                        moveX -= (dxToEnemy / distToEnemy) * pushStrength;
                        moveY -= (dyToEnemy / distToEnemy) * pushStrength;
                    }
                }
                
                double moveDist = Math.sqrt(moveX*moveX + moveY*moveY);
                if (moveDist > 0) {
                    moveX = (moveX / moveDist) * speed;
                    moveY = (moveY / moveDist) * speed;
                }
                
                x += moveX;
                y += moveY;
                
                aniframe(strip, "Knife");
                if (iter == 360) iter = 80;
                if (Main.counter == 6) iter += 40;
            }
        } 
        else if (aiState == AIState.ALERTED && lastKnownPlayerPos != null) {
            followPath();
        }
    }

    // Normal avoidance only when NOT returning home
    avoidOtherEnemies();
    resolveCollisions();
}



private void followPath() {
    if (path != null && pathIndex < path.size()) {
        Point target = path.get(pathIndex);
        
        double dx = target.x - x;
        double dy = target.y - y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        
        if (dist < 15) {
            pathIndex++;
            if (pathIndex >= path.size()) {
                path = null;
            }
        } else {
            // Move toward waypoint
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
            
            // Animation for knife enemies
            if (weapon.getName().equals("Knife")) {
                aniframe(strip, "Knife");
                if (iter == 360) iter = 80;
                if (Main.counter == 6) iter += 40;
            }
        }
    }
}

private void avoidOtherEnemies() {
    if (Main.enemies == null || Main.enemies.size() <= 1) return;
    
    // Save original position
    int originalX = x;
    int originalY = y;
    
    double avoidX = 0;
    double avoidY = 0;
    int avoidCount = 0;
    
    for (Enemy e : Main.enemies) {
        if (e == this || e == null || e.getHealth() <= 0) continue;
        
        double dx = e.x - x;
        double dy = e.y - y;
        double distance = Math.sqrt(dx*dx + dy*dy);
        
        // Avoid enemies that are too close (within 80 units)
        if (distance > 0 && distance < 80) {
            // Push away from the other enemy
            avoidX -= (dx / distance) * (1.0 - distance/80.0);
            avoidY -= (dy / distance) * (1.0 - distance/80.0);
            avoidCount++;
        }
    }
    
    if (avoidCount > 0) {
        // Apply avoidance movement
        avoidX /= avoidCount;
        avoidY /= avoidCount;
        
        // Normalize and scale by a reasonable amount
        double avoidDist = Math.sqrt(avoidX*avoidX + avoidY*avoidY);
        if (avoidDist > 0) {
            avoidX = (avoidX / avoidDist) * 3.0; // Small, safe amount
            avoidY = (avoidY / avoidDist) * 3.0;
            
            // TEST the new position first
            int testX = x + (int)avoidX;
            int testY = y + (int)avoidY;
            
            // Create a test hitbox
            Rectangle testHitbox = new Rectangle(testX - 50, testY - 17, 100, 33);
            testHitbox.x -= Main.worldX;
            testHitbox.y -= Main.worldY;
            
            boolean collision = false;
            
            // Check walls
            for (Wall wall : Main.walls) {
                if (!wall.isStair()){
                Rectangle2D wallBounds = wall.getBounds(wall.x - Main.worldX, wall.y - Main.worldY);
                if (testHitbox.intersects(wallBounds)) {
                    collision = true;
                    break;
                }
                }
            }
            
            // Only apply avoidance if it won't cause wall collision
            if (!collision) {
                x = testX;
                y = testY;
            }
            // If it would cause collision, don't move (stay in original position)
        }
    }
}

}

