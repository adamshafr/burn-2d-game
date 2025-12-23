package burngame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


/**
 *
 * 
 * @date Jan 18, 2025
 * @filename CutscenePlayer
 * @description Code for playing cutscenes. I had the cutscenes as videos but could not find a way to play them so converted them to frame by frame images, this goes through the images at 5fps
 * and plays the video basically.
 */
public class CutscenePlayer implements Runnable {
    private BufferedImage[] frames;
    private int currentFrame = 0;
    private boolean running = false;
    private final Runnable onCutsceneEnd; // Callback for when the cutscene ends

    public CutscenePlayer(String folderPath, Runnable onCutsceneEnd) {
        this.onCutsceneEnd = onCutsceneEnd;
        loadFrames(folderPath);
    }

    private void loadFrames(String folderpath) {
        try {
            File folder = new File("cutscenes/"+folderpath);
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".png"));

            if (files == null || files.length == 0) {
                throw new RuntimeException("No PNG files found in folder: " + folderpath);
            }

            // Sort files by frame number (assumes filenames like "1 (1).png", "1 (2).png", etc.)
            java.util.Arrays.sort(files, (f1, f2) -> {
                int n1 = extractFrameNumber(f1.getName());
                int n2 = extractFrameNumber(f2.getName());
                return Integer.compare(n1, n2);
            });

            frames = new BufferedImage[files.length];
            for (int i = 0; i < files.length; i++) {
                frames[i] = ImageIO.read(files[i]);
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Failed to load frames: " + e.getMessage());
        }
    }

    private int extractFrameNumber(String filename) {
        try {
            int start = filename.indexOf("(") + 1;
            int end = filename.indexOf(")");
            return Integer.parseInt(filename.substring(start, end));
        } catch (NumberFormatException e) {
            return 0; // Default to 0 if parsing fails
        }
    }

    public void start() {
        running = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    public BufferedImage getCurrentFrame() {
        if (frames != null && currentFrame < frames.length) {
            return frames[currentFrame];
        }
        return null; // No frame to display
    }

   @Override
public void run() {
    long startTime = System.currentTimeMillis(); // Record the start time
    int frameDelay = 200; // Delay in milliseconds (5 FPS = 1000ms / 5 = 200ms)

    while (running) {
        // Calculate the expected current frame based on elapsed time
        long elapsedTime = System.currentTimeMillis() - startTime;
        int expectedFrame = (int) (elapsedTime / frameDelay);

        if (expectedFrame >= frames.length) {
            running = false; // Stop the loop when the cutscene finishes
            if (onCutsceneEnd != null) {
                onCutsceneEnd.run(); // Trigger callback
            }
            return;
        }

        // Only update the current frame if it has changed
        if (expectedFrame != currentFrame) {
            currentFrame = expectedFrame;
        }

        try {
            // Sleep for a short time to reduce CPU usage
            Thread.sleep(10); // Sleep briefly to avoid tight looping
        } catch (InterruptedException e) {
        }
    }
}
}
