package burngame;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;


/**
 *
 * 
 * @date Jan 5, 2025
 * @filename Boss
 * @description Code for bosses, which are a subclass of enemies
 */

public class Boss extends Enemy {
    private int health;
    private Image bossImage;
    private int bossLevel;
    private Image body;
    private boolean cutScenePlayed = false;
    private boolean shotOnce = false;
    public Boss(int x, int y, String type, int enemyLevel, int level1){
       super(x,y,type,enemyLevel);
       this.bossLevel = level1;
        try { //loads the boss image for the level it is in
           switch (bossLevel) {
               case 1 -> bossImage = (ImageIO.read(new File("src/burngame/icons/Pistol1.png"))).getScaledInstance(169, 169, Image.SCALE_DEFAULT);
               case 2 -> {
                   bossImage = (ImageIO.read(new File("src/burngame/icons/vladimir.png"))).getScaledInstance(169, 169, Image.SCALE_DEFAULT);
                   body = ImageIO.read(new File("src/burngame/icons/vladimirdead.png")).getScaledInstance(145, 169, Image.SCALE_DEFAULT);
               }
               case 3 -> {
                   bossImage = (ImageIO.read(new File("src/burngame/icons/treyvon.png"))).getScaledInstance(169, 169, Image.SCALE_DEFAULT);
                   body = ImageIO.read(new File("src/burngame/icons/treyvondead.png")).getScaledInstance(145, 169, Image.SCALE_DEFAULT);
               }
               case 5, 13 -> {
                   bossImage = (ImageIO.read(new File("src/burngame/icons/ronan.png"))).getScaledInstance(169, 169, Image.SCALE_DEFAULT);
                   body = ImageIO.read(new File("src/burngame/icons/ronandead.png")).getScaledInstance(145, 169, Image.SCALE_DEFAULT);
               }
               case 11 -> {
                   super.strip = (ImageIO.read(new File("src/burngame/icons/dimitri.png")));
                   body = ImageIO.read(new File("src/burngame/icons/dimitridead.png")).getScaledInstance(145, 169, Image.SCALE_DEFAULT);
                   health = 200;
                  
               }
               case 16 -> {
                   bossImage = (ImageIO.read(new File("src/burngame/icons/anton.png"))).getScaledInstance(169, 169, Image.SCALE_DEFAULT);
                   body = ImageIO.read(new File("src/burngame/icons/antondead.png")).getScaledInstance(145, 169, Image.SCALE_DEFAULT);
               }
               default -> {
               }
           }
        } catch (IOException ex) {
           
        }
    }

    
    @Override //overrides the take damage method
    public void takeDamage(int damage){
        if (bossLevel == 11 && damage <50) return;
         health -= damage;
        if (health <= 0 && this.cutScenePlayed && this.bossLevel != 5) realDie();
        if (health <= 0 && levelCleared()&& !cutScenePlayed){
            injure(); //this is the death that causes a cutscene to play, or special death logic
        }
    }
    
    private void injure(){
        switch (bossLevel){
            case 1 ->{ //this boss goes in the fire than disappears (becoming anton in the future)
                this.weapon.setFirerate(0);
                if (!shotOnce){
                new Thread(() -> {
                    try {
                        shotOnce = true;
                        int target = 100;
                        int current = 0;
                        while (current < target) {
                            super.extraDiff += 5;
                            current += 5;
                            Thread.sleep(10);
                        }
                        Thread.sleep(3000);
                        Main.enemies.remove(this);
                    }catch (InterruptedException e) {
                    }
                }).start();
                }
            }
            case 2 ->{ //for the rest they play a cutscene on their first death (injury)
                health = 1;
                Main.level.playScene(1);
                cutScenePlayed = true;
                
            }
            case 3 ->{
                health = 1;
                Main.level.playScene(3);
                cutScenePlayed = true;
            } 
            case 5 ->{
                health = 1;
                Main.level.playScene(4);
                cutScenePlayed = true;
            }
            case 11 ->{
                health = 1;
                Main.level.playScene(5);
                cutScenePlayed = true;
            }
            case 13 ->{
                health = 1;
                Main.level.playScene(6);
                cutScenePlayed = true;
            }
            case 16 ->{
                health = 1;
                Main.level.playScene(8);
                cutScenePlayed = true;

            }
                            
        }
   
    }
    
    private void realDie(){ //method for the real death of the boss
        Main.bodies.add(new Body(super.get("x"),super.get("y"),angle,body));
        Main.enemies.remove(this);
        if (this.bossLevel == 2){
            new Thread(() -> {
        try {
            Thread.sleep(2000); 
            Main.level.playScene(2);
        } catch (InterruptedException e) {
        }
    }).start();
        }
    }
    
    @Override
    public void draw(Graphics g){ //overrides draw to make sure it always uses the right image
        if (bossLevel != 11) super.enemyImage = bossImage; // if it is dimitri it will already be the right image.
        super.draw(g);
    }
    
    private boolean levelCleared(){
        if (this.bossLevel == 1) return true;
        ArrayList<Enemy> enemies = Main.enemies;
        for (Enemy enemy : enemies) {
            if (enemy != this) {
                return false; 
            }
        }
        return true; // All other enemies are dead
    }
    
    @Override
     public void aniframe(BufferedImage strip, String type) { //overrides aniframe to animate using dimitris image instead of the enemy image (dimitri is the only boss that moves)
        if (!type.equals("Knife")) return;
        try {
            strip = ImageIO.read(new File("src/burngame/icons/dimitri.png"));
        } catch (IOException ex) {
        }
        Image frame = strip.getSubimage(0, iter, 40, 40);
        super.enemyImage = frame.getScaledInstance(169, 169, Image.SCALE_DEFAULT);
    }
     
     
    public void changeSkinToAnton(){ //method to change skin to anton mid cutscene
        try {
                bossImage = (ImageIO.read(new File("src/burngame/icons/antonidle.png"))).getScaledInstance(169, 169, Image.SCALE_DEFAULT);
            } catch (IOException ex) {
                System.out.println("error");
            }
    }
 
 }


