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

public class Wall {
   public int x, y, width, height;
   private final boolean hardwall;

    public Wall(int x, int y, int width, int height, boolean hard) {
        this.x = x +Main.worldX;
        this.y = y+ Main.worldY;
        this.width = width;
        this.height = height;
        this.hardwall = hard;
    }

    // Method to get the wall's bounds
   public void draw(Graphics g) {
    g.setColor(Color.BLACK); // Wall color
    g.fillRect(x - Main.worldX, y - Main.worldY, width, height); // Adjusted position for world movement
}

// Update getBounds method for collision detection
public Rectangle getBounds(int x, int y) {
    return new Rectangle(x, y, width, height); // Adjusted position for world movement
}

public boolean isHardwall(){
    return hardwall;
}
}

