
package burngame;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

/**
 *
 * 
 * @date December 20, 2024
 * @filename Main
 * @description Revenge story game, character is a former hitman trying to get revenge over those who killed his family.
 */
public class Main extends javax.swing.JFrame {
    //IF YOU WANT TO MAKE IT EASIER/HARDER CHANGE THIS VARIABLE (1 is easy, 2 is medium, 3 is hard, 4 is impossible)
//    public static int difficulty = Title.choice;
    //VARIABLE CREATION
    private static final Player player = new Player();
    public static ArrayList<Spark> sparks = new ArrayList<>();
    public Set<Integer> pressedKeys = new HashSet<>(); // Track currently pressed keys to make movement smoother than just a key listener
    private Timer gameLoop;
    public static int counter = 0;
    private boolean isShooting = false;
    public static int mouseX = 0;
    public static int mouseY = 0;
    public static int worldX;
    public static int worldY;
    private Image crosshair;
    private Image pistolImg;
    private Image arImg;
    private Image knifeImg;
    private static Image weaponImg;
    public static ArrayList<Wall> walls = new ArrayList<>();
    public static ArrayList<Body> bodies = new ArrayList<>();
    public static ArrayList<Enemy> enemies = new ArrayList<>();
    public static Image scaled;
    private Image scaledPistol;
    private Image scaledAR;
    private static Image scaledKnife;
    public static Lvl level = new Lvl();
    public static boolean missionFailed;
    private static boolean canRestart = false;
    private static boolean failScreen = false;

    
    //DEVELOPER VARIABLES
    private boolean showWalls = false; // Toggle visibility of walls for testing
    private boolean editMode = false; //Toggle edit mode for world building
    private boolean softwall = true; // toggle making softwalls or not
    //Wall building variables
    private int k = 0;
    private int xs[] = new int[2];
    private int ys[] = new int[2];
    
    
    
    public Main() {
        
        initComponents();
        initGameLoop();
        rejectLevel.setVisible(false);
        restartMsg.setVisible(false);
        failedMsg.setVisible(false);
        pauselbl.setVisible(false);
        missionFailed = false;
        level.reset(); //Starts the level, which is defaulted to 1 to start with.
        try { //loads all basic images
            crosshair = ImageIO.read(new File("src/burngame/icons/crosshair.png"));
            pistolImg = ImageIO.read(new File("src/burngame/icons/pistol.png"));
            arImg = ImageIO.read(new File("src/burngame/icons/rifle.png"));
            knifeImg = ImageIO.read(new File("src/burngame/icons/knife.png"));
            scaledAR = arImg.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
            scaledPistol = pistolImg.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
            scaledKnife = knifeImg.getScaledInstance(200, 200, Image.SCALE_DEFAULT);
            weaponImg = scaledKnife;
        } catch (IOException ex) {
            System.out.println("an image Failed");
        }

        // Set the custom cursor to hide the default cursor
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor blankCursor = toolkit.createCustomCursor(toolkit.getImage(""), new java.awt.Point(0, 0), "blank cursor");
        this.setCursor(blankCursor); 
        if (editMode) this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)); //this is for easier wall building, do not use for game
    }
        @SuppressWarnings("unchecked")
        
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panDraw = new javax.swing.JPanel(){
            @Override
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                draw(g);
            }

        };
        ammocount = new javax.swing.JLabel();
        failedMsg = new javax.swing.JLabel();
        restartMsg = new javax.swing.JLabel();
        rejectLevel = new javax.swing.JLabel();
        lblObjective = new javax.swing.JLabel();
        objectiveLb = new javax.swing.JLabel();
        pauselbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panDraw.setBackground(new java.awt.Color(0, 0, 0));
        panDraw.setMaximumSize(new java.awt.Dimension(1920, 1080));
        panDraw.setMinimumSize(new java.awt.Dimension(1920, 1080));
        panDraw.setPreferredSize(new java.awt.Dimension(1920, 1080));
        panDraw.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panDrawMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                panDrawMouseMoved(evt);
            }
        });
        panDraw.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                panDrawMouseWheelMoved(evt);
            }
        });
        panDraw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panDrawMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panDrawMouseReleased(evt);
            }
        });
        panDraw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                panDrawKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                panDrawKeyReleased(evt);
            }
        });
        panDraw.setFocusable(true);
        panDraw.requestFocusInWindow();

        ammocount.setFont(new java.awt.Font("Liberation Serif", 3, 48)); // NOI18N
        ammocount.setForeground(new java.awt.Color(255, 0, 0));
        ammocount.setText("jLabel1");
        ammocount.setFocusable(false);

        failedMsg.setFont(new java.awt.Font("Liberation Serif", 1, 120)); // NOI18N
        failedMsg.setForeground(new java.awt.Color(255, 0, 0));
        failedMsg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        failedMsg.setText("MISSION FAILED!");
        failedMsg.setFocusable(false);

        restartMsg.setFont(new java.awt.Font("Liberation Serif", 3, 48)); // NOI18N
        restartMsg.setForeground(new java.awt.Color(255, 0, 0));
        restartMsg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        restartMsg.setText("PRESS  R  TO RESTART");
        restartMsg.setFocusable(false);

        rejectLevel.setFont(new java.awt.Font("Segoe UI Symbol", 1, 36)); // NOI18N
        rejectLevel.setForeground(new java.awt.Color(255, 255, 255));
        rejectLevel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rejectLevel.setText("Eliminate all enemies before proceeding!");
        rejectLevel.setFocusable(false);

        lblObjective.setFont(new java.awt.Font("DialogInput", 1, 36)); // NOI18N
        lblObjective.setForeground(new java.awt.Color(204, 0, 0));
        lblObjective.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblObjective.setText("Objective:");
        lblObjective.setFocusable(false);

        objectiveLb.setFont(new java.awt.Font("DialogInput", 3, 48)); // NOI18N
        objectiveLb.setForeground(new java.awt.Color(255, 102, 102));
        objectiveLb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        objectiveLb.setText("Objective:");
        objectiveLb.setFocusable(false);

        pauselbl.setFont(new java.awt.Font("Liberation Serif", 3, 48)); // NOI18N
        pauselbl.setForeground(new java.awt.Color(255, 0, 0));
        pauselbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pauselbl.setText("Game Paused");
        pauselbl.setFocusable(false);

        javax.swing.GroupLayout panDrawLayout = new javax.swing.GroupLayout(panDraw);
        panDraw.setLayout(panDrawLayout);
        panDrawLayout.setHorizontalGroup(
            panDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panDrawLayout.createSequentialGroup()
                .addContainerGap(413, Short.MAX_VALUE)
                .addGroup(panDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panDrawLayout.createSequentialGroup()
                        .addGroup(panDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panDrawLayout.createSequentialGroup()
                                .addComponent(rejectLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(99, 99, 99)
                                .addComponent(objectiveLb, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ammocount, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(167, 167, 167))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panDrawLayout.createSequentialGroup()
                        .addComponent(failedMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 1106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(401, 401, 401))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panDrawLayout.createSequentialGroup()
                        .addComponent(lblObjective, javax.swing.GroupLayout.PREFERRED_SIZE, 1233, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(178, 178, 178))))
            .addGroup(panDrawLayout.createSequentialGroup()
                .addGroup(panDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panDrawLayout.createSequentialGroup()
                        .addGap(608, 608, 608)
                        .addComponent(restartMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 658, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panDrawLayout.createSequentialGroup()
                        .addGap(764, 764, 764)
                        .addComponent(pauselbl, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panDrawLayout.setVerticalGroup(
            panDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panDrawLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(ammocount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(pauselbl)
                .addGap(18, 18, 18)
                .addComponent(restartMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(failedMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(panDrawLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panDrawLayout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(rejectLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panDrawLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(objectiveLb, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(lblObjective, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(95, 95, 95))
        );

        failedMsg.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panDraw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addComponent(panDraw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void panDrawKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_panDrawKeyReleased
        pressedKeys.remove(evt.getKeyCode()); //removes a key if not pressed
    }//GEN-LAST:event_panDrawKeyReleased

    private void panDrawKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_panDrawKeyPressed
        // System.out.println(evt.getKeyCode());
        pressedKeys.add(evt.getKeyCode()); //this is for normal movement, makes it smoother and instant, aswell as being able to press multiple at once.
        if ((evt.getKeyCode()== 27) && level.canPause() && !failScreen){ //this is pause logic, but level cannot be paused if already "paused" like if dead or in cutscene
            player.playable = !player.playable;
            pauselbl.setVisible(!player.playable);
        }
        if (editMode){ //this is the code for wall building, only for edit mode
            if (evt.getKeyCode() == 75){
                xs[k]=mouseX + worldX;
                ys[k]=mouseY + worldY;
                k++;
                if (k>1){
                    printMethod(xs[0],xs[1],ys[0],ys[1]);
                    k=0;
                }
            }
          }
        if (evt.getKeyCode()==KeyEvent.VK_B){
        //	for testing purposes THIS ALSO CAUSES LEVELS TO BUG ONLY USE IT FOR BUILDING AND TESTING, THERE ARE NO FOUND BUGS DURING REGULAR GAMEPLAY ONLY WHEN USING THIS
        //    level.nextLevel(); //uncomment this if you want to go through levels when pressing B
            
        }
        if (evt.getKeyCode()==KeyEvent.VK_R){
            if(missionFailed && canRestart){ //first checks if failed mission to restart it
                restartLevel();
            }
            else if(player.getWeaponName().equals("Rifle")){ //else it will reload rifle if it is held
                try {
                    player.getCurrentWeapon().reload(false);
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (evt.getKeyCode()==KeyEvent.VK_G && level.getCurrentLevel() == 8){ //bomb planting for the one level it is used for
            level.bombPlanted = true;
            try {
                playSound("bomb");
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_panDrawKeyPressed

    private void panDrawMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panDrawMouseReleased
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
            isShooting = false; //stops shooting rifle
        }
    }//GEN-LAST:event_panDrawMouseReleased

    private void panDrawMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panDrawMousePressed
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1){
            if (player.getWeaponName().equals("Rifle")) {
                // Continuous shooting for Assault Rifle
                isShooting = true;
            }else{
                // Single shot for Pistol or Knife on mouse press
                player.shoot(); // Call shoot once for Pistol
            }
        }
    }//GEN-LAST:event_panDrawMousePressed

    private void panDrawMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_panDrawMouseWheelMoved
        if (player.playable){
            int direction = evt.getWheelRotation() > 0 ? 1 : -1;
            player.switchWeapon(direction); //scrolls through weapons.
            switch (player.getWeaponName()) {
                case "Pistol" -> weaponImg = scaledPistol;
                case "Rifle" -> weaponImg = scaledAR;
                case "Knife" -> weaponImg = scaledKnife;
                default -> {
                }
            }
        }
    }//GEN-LAST:event_panDrawMouseWheelMoved

    private void panDrawMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panDrawMouseMoved
        mouseX = evt.getX();
        mouseY = evt.getY();
       //updayes mouse x and y

    }//GEN-LAST:event_panDrawMouseMoved

    private void panDrawMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panDrawMouseDragged
        panDrawMouseMoved(evt); //works if mouse is dragged too
    }//GEN-LAST:event_panDrawMouseDragged

   
     private void initGameLoop() { //this is the game loop
        // Game loop updates player movement every 16 ms (~60 FPS)
        gameLoop = new Timer(16, e -> {
            counter++;
            if(counter == 7){
                counter = 0;
            }
            player.updatePlayerPosition(worldX, worldY, pressedKeys, counter); 
            int count = player.getCurrentWeapon().getClip();
            switch (player.getWeaponName()) {
                case "Rifle" -> ammocount.setText(String.valueOf(count));
                case "Pistol" -> ammocount.setText("∞");
                default -> ammocount.setText(""); //updates ammo text
            }
            if (player.getCurrentWeapon().reloading){
                ammocount.setText(""); //does not write ansything if player is reloading
            }
            panDraw.repaint();
            if (isShooting && player.getCurrentWeapon().isAutomatic()) {
                player.shoot();
            }
           checkTransition(); //checks if player is at the current levels transition
        });
        gameLoop.start();
    }
    
    public static void setWorld(int x, int y){ //setter for worldx and y
        worldX = x;
        worldY = y;
    }
    
    public static void setBackground(double scale, String image){ //setter for background
        try{
        Image unscaled = ImageIO.read(new File("src/burngame/backgrounds/"+image+".png"));
        scaled = unscaled.getScaledInstance((int)(unscaled.getWidth(null)*scale),(int)(unscaled.getHeight(null)*scale), Image.SCALE_DEFAULT);
        }catch (IOException ex) {
            System.out.println("failed");
        }
    }
    
    public static void failLevel(){ //method for failing current level, setting lables to false and true and making the restart message
        failScreen = true;
        failedMsg.setVisible(true);
        objectiveLb.setVisible(false);
        lblObjective.setVisible(false);
        missionFailed = true;
        player.playable = false;
         new Thread(() -> {
        try {
            Thread.sleep(3000); 
            restartMsg.setVisible(true);
            canRestart = true;
        } catch (InterruptedException e) {
        }
    }).start();
    }
    
    public static void restartLevel(){ //resets everything when a level is reset
        canRestart = false;
        failScreen = false;
         bodies.clear();
        worldX = 0;
        worldY = 0;
        missionFailed = false;
        failedMsg.setVisible(false);
        restartMsg.setVisible(false);
        objectiveLb.setVisible(true);
        lblObjective.setVisible(true);
        player.playable = true;
        player.resetPlayer();
        level.reset();
        weaponImg = scaledKnife;
        player.setWeapon(0);
    }
    
    public static void setLabels(boolean visible){ //setter for lables (cutscenes)
        objectiveLb.setVisible(visible);
        lblObjective.setVisible(visible);
        ammocount.setVisible(visible);
    }
    
    private static void updateObjective(){ 
        lblObjective.setText(level.getObjective()); //updates current objective
    }
    
  
   private void printMethod(int x1, int x2, int y1, int y2) { //THIS IS FOR WALL BUILDING NOT FOR THE ACTUAL GAME
    // Calculate the differences between the coordinates
    int xdiff = Math.abs(x1 - x2);
    int ydiff = Math.abs(y1 - y2);

    // Ensure x1, y1 is the leftmost/topmost point
    if (x2 < x1) {
        x1 = x2;
    }

    if (y2 < y1) {
        y1 = y2;
    }
        if (softwall){
            System.out.println("addWall(" + x1 + "," + y1 + "," + xdiff + ","+ydiff+",false);");
        }else{
        System.out.println("addWall(" + x1 + "," + y1 + "," + xdiff + ","+ydiff+",true);");
        }
}
        public static Rectangle getPlayerBounds(){ //returns the current players bounds
           Rectangle2D rect = player.getRotatedHitbox();
           return rect.getBounds();
       }

       public static Player getPlayer(){ //returns current player
           return player;
       }
   
      public static void playSound(String sound) throws UnsupportedAudioFileException { //method for playing sound. this is used everywhere to play sound.
    try {
        File f = new File("src/burngame/sounds/" + sound + ".wav");
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
        
        // Ensure the clip is closed after it finishes playing
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                clip.close(); // Close the clip when it stops
            }
        });
    } catch (LineUnavailableException | IOException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
}
    
    
    private void checkTransition(){ //checks if player can transition to next level
         if (getPlayerBounds().intersects(level.getTransition())){
              if (enemies.isEmpty()){
                  if (level.getCurrentLevel() != 8 || level.bombPlanted){
                  level.nextLevel();
                  }else{
                      bombReminder();
                  }
              }else if (level.getCurrentLevel() == 3 && level.cutsceneHappened){
               level.nextLevel();
              
              }else{
                  rejectLevel();
              }
     }
    }
    private void rejectLevel(){ //lets player know they cannot transition
         new Thread(() -> {
            try {
                rejectLevel.setVisible(true);
                Thread.sleep(2000);
                rejectLevel.setVisible(false);
            } catch (InterruptedException e) {
            }
        }).start();
    }
    
    private void bombReminder(){ //reminds player to plant the very important bomb for the one level it is used for
        new Thread(() -> {
            try {
                rejectLevel.setText("Press 'G' to plant a bomb!");
                rejectLevel.setVisible(true);
                Thread.sleep(2000);
                rejectLevel.setVisible(false);
                rejectLevel.setText("Eliminate all enemies before proceeding!");
            } catch (InterruptedException e) {
            }
        }).start();
    }
    
    
    private void draw(Graphics g) { //draw method
    ArrayList<Body> bodiesCopy; //THIS IS TO STOP A CERTAIN ERROR FROM OCCURING WHEN MODIFYING AN ARRAY WHILE DISPLAYING IT, i forgot the name of error
    ArrayList<Enemy> enemiesCopy;
    ArrayList<Spark> sparksCopy;

    synchronized (bodies) {
        bodiesCopy = new ArrayList<>(bodies);
    }
    synchronized (enemies) {
        enemiesCopy = new ArrayList<>(enemies);
    }
    synchronized (sparks) {
        sparksCopy = new ArrayList<>(sparks);
    }
    
    //starts with background and draws everything
     g.drawImage(scaled, 0-worldX, 0-worldY, null);
      
     

     for (Body body: bodiesCopy){
        body.draw(g);
    }
     
     

        for (Enemy enemy : enemiesCopy){
        enemy.draw(g);
    }

    player.draw(g);
   
  
    // Draw the walls (considering world movement)
    if (showWalls) {
        for (Wall wall : walls) {
            wall.draw(g);
        }
    }
    
 
    
    //Draw the bullet hitting animations
    sparks.removeIf(Spark::isExpired);
    for (Spark spark : sparksCopy) {
        spark.draw(g);
    }
    
    
    g.drawImage(weaponImg, 1600, 10, null);
    //cutscenes at the end if there are any, because those should be above the rest
    level.drawCutscene(g);
    if (!editMode) g.drawImage(crosshair, mouseX - crosshair.getWidth(null) / 2, mouseY - crosshair.getHeight(null) / 2, null);
    
    updateObjective();
}
    
    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        java.awt.EventQueue.invokeLater(() -> {
            new Title().setVisible(true);
        });
        
    }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JLabel ammocount;
    private static javax.swing.JLabel failedMsg;
    private static javax.swing.JLabel lblObjective;
    private static javax.swing.JLabel objectiveLb;
    private static javax.swing.JPanel panDraw;
    private static javax.swing.JLabel pauselbl;
    private static javax.swing.JLabel rejectLevel;
    private static javax.swing.JLabel restartMsg;
    // End of variables declaration//GEN-END:variables
}
