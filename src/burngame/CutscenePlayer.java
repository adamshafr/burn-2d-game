package burngame;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @date Jan 18, 2025
 * @filename CutscenePlayer
 * @description Plays cutscenes by loading numbered frame images at 5 FPS.
 * Frames are loaded from inside the JAR using classpath resources.
 * Frame naming format: "1 (x).png"
 */
public class CutscenePlayer implements Runnable {

    private BufferedImage[] frames;
    private int currentFrame = 0;
    private boolean running = false;
    private final Runnable onCutsceneEnd;

    /**
     * @param sceneName folder name under /burngame/cutscenes/
     * @param frameCount total number of frames in the cutscene
     * @param onCutsceneEnd callback executed when cutscene finishes
     */
    public CutscenePlayer(String sceneName, int frameCount, Runnable onCutsceneEnd) {
        this.onCutsceneEnd = onCutsceneEnd;
        loadFrames(sceneName, frameCount);
    }

    private void loadFrames(String sceneName, int frameCount) {
        frames = new BufferedImage[frameCount];

        try {
            for (int i = 0; i < frameCount; i++) {
                String path = String.format(
                    "/burngame/cutscenes/%s/1 (%d).png",
                    sceneName,
                    i + 1
                );

                InputStream is = getClass().getResourceAsStream(path);
                if (is == null) {
                    throw new RuntimeException("Missing cutscene frame: " + path);
                }

                frames[i] = ImageIO.read(is);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load cutscene frames", e);
        }
    }

    public void start() {
        running = true;
        new Thread(this, "CutscenePlayer").start();
    }

    public void stop() {
        running = false;
    }

    public BufferedImage getCurrentFrame() {
        if (frames != null && currentFrame < frames.length) {
            return frames[currentFrame];
        }
        return null;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        int frameDelay = 200; // 5 FPS

        while (running) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            int expectedFrame = (int) (elapsedTime / frameDelay);

            if (expectedFrame >= frames.length) {
                running = false;
                if (onCutsceneEnd != null) {
                    onCutsceneEnd.run();
                }
                return;
            }

            currentFrame = expectedFrame;

            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
    }
}
