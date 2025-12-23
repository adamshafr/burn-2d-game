package burngame;

import java.awt.Graphics;
import java.awt.Image;

/**
 *
 * 
 * @date December 26, 2024
 * @filename Body
 * @description Code for sparks that appear when a gun shoots a wall
 */


public class Spark {
    private final int worldX, worldY; // Spark's static world position
    private final Image img;
    private final long createTime;
    private final int lifetime = 300; // Spark lasts 300 ms

    public Spark(int worldX, int worldY, Image img) {
        this.worldX = worldX; // Save the exact world coordinates at creation
        this.worldY = worldY;
        this.img = img;
        this.createTime = System.currentTimeMillis();
    }

    public boolean isExpired() {//returns if spark is expired
        return System.currentTimeMillis() - createTime > lifetime;
    }

    public void draw(Graphics g) { //draws the spark
        // Convert the spark's world position to screen position
        int screenX = worldX - Main.worldX;
        int screenY = worldY - Main.worldY;
        // Center the spark image and draw it
        g.drawImage(img, screenX - img.getWidth(null) / 2, screenY - img.getHeight(null) / 2, null);
    }
}
