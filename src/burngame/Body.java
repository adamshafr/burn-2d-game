package burngame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
/**
 *
 *
 * @date Jan 6, 2025
 * @filename Body
 * @description Code bodies, since all bodies have different images and properties (angle, x, y, image)
 */
public class Body {
    private final int worldX, worldY; 
    private final Image img;
    private final double angle;

    public Body(int worldX, int worldY, double angle, Image img) {
        this.worldX = worldX; // Save the exact world coordinates at creation
        this.worldY = worldY;
        this.img = img; 
        this.angle = angle + 3.14;
    }


    public void draw(Graphics g) {
        // Convert the spark's world position to screen position
        int screenX = worldX - Main.worldX;
        int screenY = worldY - Main.worldY;
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();

        // Rotate the body image
        g2d.rotate(angle, screenX, screenY);

        // Draw the image, adjusted to keep it centered

        g2d.drawImage(img, screenX - img.getWidth(null) / 2, screenY - img.getHeight(null) / 2, null);
        // Restore the original transform
        g2d.setTransform(oldTransform);
    }
    
    
}