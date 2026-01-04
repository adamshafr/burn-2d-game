package burngame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *
 * 
 * @date December 24, 2024
 * @filename Wall
 * @description Code for walls, allowing them to be drawn and get the bounds
 */

// In Wall.java - Store original coordinates:

public class Wall {
   public int x, y, width, height;
   private final int hardwall;
   private final int originalX, originalY; // Store original positions

    public Wall(int x, int y, int width, int height, int hard) {
        this.originalX = x; // Store original position
        this.originalY = y;
        this.x = x; 
        this.y = y;
        this.width = width;
        this.height = height;
        this.hardwall = hard;
    }

    
   public void draw(Graphics g) {
    g.setColor(Color.RED);
    g.fillRect(x - Main.worldX, y - Main.worldY, width, height);
}

// get wall bounds
public Rectangle getBounds(int x, int y) {
    return new Rectangle(x, y, width, height);
}

public boolean isHardwall(){
    return hardwall == 0;
}

public boolean isStair(){
    return hardwall == 2;
}

// Get original position for pathfinding
public int getOriginalX() {
    return originalX;
}

public int getOriginalY() {
    return originalY;
}
}

