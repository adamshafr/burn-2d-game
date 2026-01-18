package burngame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;



/**
 *
 * 
 * @date December 20, 2024
 * @filename Player
 * @description Code for the player 
 */
public class Player {
    private int x = 960;  
    private int y = 540;  
    private int speed;
    private double angle; // Angle of rotation in radians
    private int maxHealth;
    private int health;
    private BufferedImage strip;
    private Weapon currentWeapon;
    private int weaponIndex = 0;
    private final Weapon[] weapons;
    private int iter = 80;
    private Image img;
    private Image imgblood;
    private Image bodyImage;
    private boolean bloodDraw = false;
    public boolean playable = true;
    private boolean dead;
    private void aniframe(BufferedImage strip) {
        try {
            // Load the player's image
            strip = Main.loadImage("/burngame/icons/carter" + weaponIndex + ".png");
        } catch (IOException ex) {
            System.out.println("Image Failed to Load");
        }
        Image frame = strip.getSubimage(0, iter, 40, 40);
        img = frame.getScaledInstance(169, 169, Image.SCALE_DEFAULT);
       // img =  frame; 
    }

    public Player() {
        
        try {
            // Load the player's image
            strip =  Main.loadImage("/burngame/icons/carter" + weaponIndex + ".png");
            imgblood = Main.loadImage("/burngame/icons/blood.png");
            bodyImage = Main.loadImage("/burngame/icons/carterdead.png").getScaledInstance(145, 169, Image.SCALE_DEFAULT);
            aniframe(strip);
        } catch (IOException ex) {
            System.out.println("Image Failed to Load");
        }
        weapons = new Weapon[] { //list of player weapons to scroll through
            new Weapon("Knife", false),
            new Weapon("Pistol", false),
            new Weapon("Rifle", false)
        };
        currentWeapon = weapons[weaponIndex];
         x -= img.getWidth(null)/2; //makes sure that the player is at the center of the screen
        y -= img.getHeight(null)/2;
    }
    
    public void switchWeapon(int direction) { //used to switch weapon
        weaponIndex = (weaponIndex + direction + weapons.length) % weapons.length;
        currentWeapon = weapons[weaponIndex];
        iter = 0;
        if(currentWeapon.getName().equals("Knife")){
            iter = 80;
        }
        aniframe(strip);
    }
    
    public void setWeapon(int index){ //used to directly swap the current weapon to a specific index
        weaponIndex = index;
        currentWeapon = weapons[weaponIndex];
        iter = 0;
        if(currentWeapon.getName().equals("Knife")){
            iter = 80;
        }
        aniframe(strip);
    }
    

    public void shoot() { //code for player to shoot 
        if (playable){
        if (currentWeapon != null) {
            try {
                currentWeapon.shoot(Main.mouseX, Main.mouseY, 960, 540, false); //shoots the current weapon towards the mouse
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (currentWeapon.getName().equals("Knife")){
            iter = 0; //plays knife animation
        }
        }
    }
   
    public String getWeaponName() {
        return currentWeapon != null ? currentWeapon.getName() : "None"; //getters for weapin name and weapon itself
    }
    public Weapon getCurrentWeapon(){
        return currentWeapon;
    }

    
    public void resetPlayer(){ //sets the maxhealth based on selected difficulty, this is the only thing difficulty changes
        switch (Title.getDifficulty()){
            case 1-> maxHealth = 210;
            case 2-> maxHealth = 60;
            case 3-> maxHealth = 40;
            case 4-> maxHealth = 1;
        }
        for (Weapon w : weapons){ //resets weapon 
            w.reloading = false;
            w.resetClip();
        }
        health = maxHealth; //resets health
        dead = false;
    }
   
    public void updateDirection(int mouseX, int mouseY){ // makes player face the mouse
        angle = Math.atan2(mouseY - (y + img.getHeight(null) / 2), mouseX - (x + img.getWidth(null) / 2)) +1.57;
        
    }
    
    public void updatePlayerPosition(int worldX, int worldY, Set<Integer> pressedKeys, int counter){ //code for updating player based on what is being pressed
    // Set speed based on weapon
    int speedChange = 0;
    for (Wall wall : Main.walls){     
        if (wall.isStair()){
        Rectangle2D wallBounds = wall.getBounds(wall.x - Main.worldX, wall.y - Main.worldY);
        if (this.getRotatedHitbox().intersects(wallBounds)) {
            speedChange = -5;
        }else{
            speedChange = 0;
        }
        }
    }

    if (playable){
            
        
    if (currentWeapon.getName().equals("Knife")) {
        speed = 14 + speedChange; //speed is higher holding a knife
    } else {
        speed = 10 + speedChange;
    }
    // Movement vector components
    double moveX = 0;
    double moveY = 0;
    // Check which keys are pressed
    if (pressedKeys.contains(KeyEvent.VK_W)) moveY -= speed;
    if (pressedKeys.contains(KeyEvent.VK_S)) moveY += speed;
    if (pressedKeys.contains(KeyEvent.VK_A)) moveX -= speed;
    if (pressedKeys.contains(KeyEvent.VK_D)) moveX += speed;
    // Get a direction vector to maintain proper speed. this is to make sure the player isnt faster while moving diagonally and keeps movement smooth
    if (moveX != 0 && moveY != 0) {
        double length = Math.sqrt(moveX * moveX + moveY * moveY);  // Calculate the length of the vector
        moveX /= length;  
        moveY /= length;  
        moveX *= speed;   // Scale back up to the desired speed
        moveY *= speed;
    }
    // Apply the movement
    Main.worldX += moveX;
    Main.worldY += moveY;
    // Handle other game logic (iter, weapon updates)
    if (iter >= 360 && weaponIndex == 0) {
        iter = 80;
    }
    if (iter >= 120 && weaponIndex == 1) {
        iter = 0;
        if (counter == 6) {
            iter += 40;
        }
    }
    if (iter >= 120 && weaponIndex == 2) {
        iter = 0;
        if (counter == 6) {
            iter += 40;
        }
    }
    if (counter == 3 || counter == 6) {
        if (weaponIndex == 0) {
            iter += 40;
        }
        if (!(weaponIndex == 0) && counter == 6) {
            iter += 40;
        }
    }
    // Only update animation if moving
    if (moveX != 0 || moveY != 0) {
        aniframe(strip); // Update animation only if the player is moving
    }
    // Resolve any collisions
    resolveCollisions();
    }
}
        //This is for resolving collisions. This way even if the player rotates it will still feel smooth and have them be "pushed out" of a wall making it feel more realistic.
    //this works by checking the shortest distance the player needs to be pushed out by so they are not in the wall anymore, then applying it
    public void resolveCollisions() {
        Rectangle2D playerHitbox = this.getRotatedHitbox();
        /*
         for (Enemy e : Main.enemies){
            Rectangle enemyBounds = e.getBounds();
            enemyBounds.x -= Main.worldX;
            enemyBounds.y -= Main.worldY;
            pushOut(playerHitbox,enemyBounds);
        }
        */
        
        for (Wall wall : Main.walls) {
            if (!wall.isStair()){
            Rectangle2D wallBounds = wall.getBounds(wall.x - Main.worldX, wall.y - Main.worldY);
            pushOut(playerHitbox, wallBounds);
        }
        }
        
        
    }
    private void pushOut(Rectangle2D hitbox, Rectangle2D wallBounds){
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
                    Main.worldX += disX;
                } else {
                    Main.worldY += disY;
                }
            }
 }
    public void draw(Graphics g) { //draws the player
        if (!dead){
        updateDirection(Main.mouseX,Main.mouseY);
        Graphics2D g2d = (Graphics2D) g;
        
        // Calculate the center of the image for rotation
        int centerX = x + img.getWidth(null) / 2;
        int centerY = y + img.getHeight(null) / 2;

        // Save the current transform
        AffineTransform oldTransform = g2d.getTransform();

        // Rotate around the center of the player
        g2d.rotate(angle, centerX, centerY);

        // Draw the image, adjusted to keep it centered
        x = 960 - (img.getWidth(null)/2);
        y = 540 - (img.getHeight(null)/2);
        g2d.drawImage(img, x, y, null);

        // Restore the original transform
        g2d.setTransform(oldTransform);
        //g2d.draw(hitboxdraw);
        
        if (bloodDraw){
            g2d.drawImage(imgblood,960-(imgblood.getWidth(null)/2),540-(imgblood.getHeight(null)/2),null);
        }
        }
    }
    
   public Rectangle2D getRotatedHitbox() {
        
        // Create the unrotated rectangle centered at (960, 540)
        Rectangle2D rect = new Rectangle2D.Double(960 - 100 / 2, 540 - 33 / 2, 100, 33);
        
        // Rotate the rectangle around its center
        AffineTransform transform = AffineTransform.getRotateInstance(angle, 960, 540);
        transform.createTransformedShape(rect).getBounds2D();
        return transform.createTransformedShape(rect).getBounds2D();
    }
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0 && Title.getDifficulty() != 0) { //this is so peaceful mode is fully invulmerable
           die(); // Trigger death logic
        }

    // Set bloodDraw to true
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
    
    public void die(){ //death logic, fails level, plays the annoying sound, sets player to dead and makes them unable to do anything, aswell as adds a player body.
        Main.failLevel();
        Main.playSound("fail");
        playable = false;
        dead = true;
        Main.bodies.add(new Body(960+Main.worldX,540+Main.worldY,angle,bodyImage));
    }
}   
 
    

