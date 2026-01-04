
package burngame;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;



/**
 *
 * 
 * @date January 10, 2024
 * @filename Lvl
 * @description Code for the levels. This seems like a lot of lines but most of them are just setting the walls and enemy spawn locations, which I got from the wall method builder in main for editmode, 
 * which was designed to make it super easy to build walls for levels, automatically saying the method needed for each wall in the console so we can just copy and paste it into the right level. This also 
 * manages the creation of cutscenes, bosses, and other values needed for each level (scale, background, starting x and y). Overall this class just manages everything that has to do with the level, making it
 * simpler for everyone, imagine if all this had to go in the main...
 * 
 */

public class Lvl {
    private int level = 1; //starts at 1
    private CutscenePlayer cutscenePlayer;
    public boolean inCutscene = false;
    public boolean cutsceneHappened = false;
    private Image transition;
    private boolean nextLevelAfterScene = false;
    private boolean instantNext = false;
    public boolean bombPlanted;
    private boolean anton1scene = false;
    private Boss currentBoss;
    private boolean inBombScene = false;
    
    
    private int navTileSize = -1;
    public boolean[][] navWalkable = null;
    private PathFinder pathFinder = null;   
    private int gridOriginX = 0;
    private int gridOriginY = 0;
    public Lvl(){
    }
    
    
    
    private void loadLevel(int level){
        bombPlanted = false;
        cutsceneHappened = false;
        this.level = level;
        initLevel();
    }
    public void reset(){
        loadLevel(level);
    }
private void initLevel(){
   Main.setWorld(0, 0);
   
   Main.bodies.clear();
   Main.getPlayer().resetPlayer();
    initWalls();
    if (level == 14 && !anton1scene){
       playScene(7);
    }
    initEnemies();
    initValues();
    playTransition(level);
    
    // Build navigation grid after walls are loaded
    buildNavGrid();
}
    private void initWalls(){ //clears and builds the new levels walls
        Main.walls.clear();
        buildWalls();

    }
    private void initEnemies(){ //clears and builds the new levels enemies
        Main.enemies.clear();
        buildEnemies();
    }
    
    
 
    private void initValues(){ //this is the background scale, enemy position, and boss if there is any.
        String background;
        int x,y;
        double scale;
        switch (level) {
            case 1 -> {
                scale = 4.5;
                x = 350;
                y = 1600;
                background = "1a";
                currentBoss = new Boss(670,1101,"Pistol",1, level, true);
              Main.enemies.add(currentBoss);
            }
            case 2 -> {
                scale = 4.6;
                x = 1250;
                y = -350;
                background = "1b";
                currentBoss =new Boss(60,419,"Pistol",1, level, false);
                Main.enemies.add(currentBoss);
            }
            case 3 -> {
                scale = 4.3;
                x = 55;
                y = 1500;
                background = "2";
                currentBoss = new Boss(118,219,"Rifle",2, level, false);
                Main.enemies.add(currentBoss);
            }
            case 4 -> {
                scale = 4.3;
                x = 0;
                y = 875;
                background = "3a";
            }
            case 5 -> {
                scale = 3;
                x = -120;
                y = -130;
                background = "3b";
                currentBoss = new Boss(200,374,"Pistol",3, level, false);
                Main.enemies.add(currentBoss);
            }
            case 6 -> {
                scale = 4.5;
                x = -50;
                y = 700;
                background = "4a";
            }
            case 7 -> {
                scale = 4.5;
                x = 800;
                y = -450;
                background = "4b";
            }
            case 8 -> {
                scale = 4;
                x = -850;
                y = 400;
                background = "4c";
            }
            case 9 -> {
                scale = 4;
                x = -250;
                y = 310;
                background = "5a";
            }
            case 10 -> {
                scale = 4.3;
                x = -900;
                y = 100;
                background = "5b";
            }
            case 11 -> {
                scale = 3.4;
                x = -275;
                y = 230;
                background = "5c";
                currentBoss = new Boss(1170,80,"Knife",-1, level, false);
                Main.enemies.add(currentBoss);
            }
            case 12 -> {
                scale = 3;
                x = -900;
                y = -375;
                background = "6b";
            }
            case 13 -> {
                scale = 3.5;
                x = 1050;
                y = 550;
                background = "6c";
                currentBoss = new Boss(258,180,"Pistol",6, level, false);
                Main.enemies.add(currentBoss);
            }
            case 14 -> {
                scale = 4.3;
                x = -900;
                y = 325;
                background = "7a";
            }
            case 15 -> {
                scale = 4;
                x = -900;
                y = 50;
                background = "7b";
            }
            case 16 -> {
                scale = 4;
                x = -900;
                y = -350;
                background = "7c";
                currentBoss = new Boss(1150,188,"Pistol",7, level, false);
                Main.enemies.add(currentBoss);
            }
            default -> {
                return;
            }
        }
        Main.setWorld(x,y); //sets the world to the right value (starting position)
        Main.setBackground(scale, background); //sets the backround to the right scale and image
    }
     private void buildWalls(){ //builds the walls for every level, all these were generated by the wall building method in main. This is long because each wall has to be made 1 by 1 somewhere
        switch (level) {
            case 1 -> {
                addWall(-822,1,822,2568,false);
                addWall(1126,275,808,265,true);
                addWall(1048,2235,503,15,true);
                addWall(1552,1827,698,422,true);
                addWall(-1,1805,1048,445,true);
                addWall(-1,1538,14,267,true);
                addWall(-1,1265,1048,275,true);
                addWall(0,1,14,1262,true);
                addWall(14,1,2236,14,true);
                addWall(2237,15,13,1426,true);
                addWall(1895,1441,356,119,true);
                addWall(2245,1560,4,268,true);
                addWall(2025,941,211,221,true);
                addWall(2052,631,182,191,true);
                addWall(489,873,15,391,true);
                addWall(489,1,15,740,true);
                addWall(85,91,305,191,true);
                addWall(-2,1238,15,32,true);
                addWall(1611,153,260,120,false);
                addWall(544,1144,261,121,false);
                addWall(2029,1562,223,266,false);
                addWall(503,1145,39,118,false);
            }
            case 2 -> {
                addWall(2285,1,14,400,true);
                addWall(1481,401,820,64,true);
                addWall(1481,465,68,455,true);
                addWall(1,1,2298,12,true);
                addWall(1,13,13,906,true);
                addWall(14,906,1469,13,true);
                addWall(765,14,390,137,true);
                addWall(227,265,327,17,false);
                addWall(226,565,325,15,false);
                addWall(194,390,17,82,false);
                addWall(1342,476,63,428,false);
                addWall(841,313,230,460,false);
                addWall(220,144,340,108,false);
                addWall(220,295,341,255,false);
                addWall(220,593,341,109,false);
                addWall(93,384,90,87,false);
            }
            case 3 -> {
                addWall(667,2137,622,12,true);
                addWall(667,435,89,1714,true);
                addWall(2,435,754,90,true);
                addWall(2,0,12,525,true);
                addWall(14,0,1275,14,true);
                addWall(1278,14,11,2135,true);
                addWall(757,1566,160,185,true);
                addWall(1158,1067,120,261,true);
                addWall(757,663,162,335,true);
                addWall(1128,193,151,337,true);
            }
            case 4 -> {//why was this level so annoying
                addWall(1,1493,1720,12,true);
                addWall(-1,1,13,1505,true);
                addWall(12,1,1708,11,true);
                addWall(1706,12,14,1479,true);
                addWall(1346,833,367,14,true);
                addWall(12,835,1182,12,true);
                addWall(826,309,12,870,true);
                addWall(826,7,13,172,true);
                addWall(35,34,480,168,true);
                addWall(506,713,289,90,true);
                addWall(869,879,287,87,true);
                addWall(1386,882,285,90,true);
                addWall(1539,47,143,141,true);
                addWall(825,1302,14,191,true);
                addWall(837,469,77,132,false);
                addWall(206,1363,538,111,false);
                addWall(24,1376,91,57,false);
                addWall(25,1204,92,55,false);
                addWall(24,1031,92,57,false);
                addWall(171,861,56,93,false);
                addWall(345,864,54,92,false);
                addWall(516,864,54,92,false);
                addWall(688,865,56,89,false);
                addWall(918,66,80,78,false);
                addWall(1178,69,81,80,false);
                addWall(1398,74,79,78,false);
                addWall(1570,280,80,80,false);
                addWall(1570,496,82,80,false);
                addWall(1568,698,81,79,false);
                addWall(743,1364,61,110,false);
                addWall(11,12,503,188,false);
                addWall(857,67,685,10,false);
                addWall(1679,4,41,188,false);
                addWall(1651,279,37,535,false);
                addWall(830,841,39,125,false);
                addWall(853,839,304,40,false);
                addWall(1384,837,307,46,false);
                addWall(1669,837,22,132,false);
                addWall(1570,776,87,38,false);
                addWall(1639,188,9,94,false);
                addWall(855,7,839,60,false);
                addWall(1477,80,66,73,false);
                addWall(855,78,61,65,false);
                addWall(791,716,39,111,false);
                addWall(506,798,325,33,false);
                addWall(13,839,817,27,false);
                addWall(18,1379,188,111,false);
                addWall(743,863,79,90,false);
            }
            case 5 -> {
                addWall(258,207,141,327,false);
                addWall(490,157,91,91,false);
                addWall(-1,1,111,750,true);
                addWall(110,735,789,16,false);
                addWall(886,265,13,470,false);
                addWall(824,2,75,262,true);
                addWall(614,2,285,71,true);
                addWall(114,1,786,14,true);
            }
            case 6 -> {
                addWall(1007,354,316,995,true);
                addWall(63,1134,736,184,true);
                addWall(33,524,176,793,true);
                addWall(652,518,147,620,true);
                addWall(1531,527,138,768,true);
                addWall(1669,527,540,147,true);
                addWall(2064,674,145,621,true);
                addWall(2220,0,30,1349,true);
                addWall(0,0,2220,30,true);
                addWall(0,30,29,1319,true);
                addWall(29,1318,2189,31,true);
                addWall(1533,1296,145,23,true);
            }
            case 7 -> {
                addWall(197,181,1602,89,true);
                addWall(1787,0,14,450,true);
                addWall(0,0,1787,14,true);
                addWall(0,14,13,437,true);
                addWall(13,435,1785,16,true);

            }
            case 8 -> {
                addWall(2375,0,25,1600,true);
                addWall(0,1573,2375,27,true);
                addWall(0,2,29,1571,true);
                addWall(29,2,2369,25,true);
                addWall(1741,25,27,795,true);
                addWall(1741,1036,28,535,true);
                addWall(1529,1359,237,213,true);
                addWall(684,1324,483,165,true);
                addWall(80,1299,435,220,true);
                addWall(425,680,1066,548,true);
                addWall(1304,140,234,304,true);
                addWall(1011,241,293,131,true);
                addWall(604,142,407,302,true);
                addWall(481,241,124,134,true);
                addWall(340,240,144,52,true);
                addWall(29,133,310,311,true);
                addWall(27,1299,55,36,false);
                addWall(465,1518,51,56,false);
                addWall(2135,509,236,586,false);
            }
            case 9 ->{
                addWall(512,879,1486,20,true);
                addWall(1980,0,18,899,true);
                addWall(512,0,1468,19,true);
                addWall(512,19,18,401,true);
                addWall(512,582,20,318,true);
                addWall(1,723,511,22,true);
                addWall(1,257,20,488,true);
                addWall(21,257,494,19,true);
                addWall(885,760,19,119,true);
                addWall(884,349,22,249,true);
                addWall(906,349,478,19,true);
                addWall(1543,348,439,20,true);
                addWall(136,370,229,62,false);
                addWall(136,572,227,62,false);
                addWall(1149,516,607,213,false);
                addWall(1746,585,98,78,false);
                addWall(1806,583,84,84,false);
            }
            case 10 ->{
                addWall(-1,0,88,585,true);
                addWall(87,0,1203,29,true);
                addWall(1201,29,89,539,true);
                addWall(1266,566,23,295,true);
                addWall(0,836,1266,25,true);
                addWall(0,582,21,254,true);
                addWall(216,134,30,450,true);
                addWall(246,134,213,29,true);
                addWall(431,163,28,63,true);
                addWall(431,201,428,25,true);
                addWall(829,134,30,67,true);
                addWall(829,134,245,30,true);
                addWall(1047,164,27,402,true);
            }
            case 11 ->{
                addWall(0,850,1432,50,true);
                addWall(1363,-70,69,970,true);
                addWall(-104,-70,1467,66,true);
                addWall(-104,-4,101,857,true);
                addWall(136,529,173,175,true);
                addWall(136,191,172,172,true);
                addWall(570,116,218,106,true);
                addWall(1260,-1,102,242,true);
                addWall(1343,242,17,609,true);
                addWall(-1,832,1361,19,true);
                addWall(-1,0,18,832,true);
                addWall(17,0,1243,16,true);
                addWall(994,418,232,105,false);
                addWall(993,673,234,105,false);
            }
            case 12 ->{
                addWall(0,0,899,16,true);
                addWall(886,16,13,734,true);
                addWall(-1,735,887,15,true);
                addWall(-1,1,18,734,true);
                addWall(12,297,660,16,true);
                addWall(831,109,54,92,false);
            }
            case 13 ->{
                addWall(2083,-1,17,1750,true);
                addWall(0,1731,2083,18,true);
                addWall(0,0,18,1731,true);
                addWall(18,0,2081,24,true);
                addWall(1905,1,194,194,true);
                addWall(969,3,179,194,true);
                addWall(655,18,17,94,true);
                addWall(653,259,18,107,true);
                addWall(14,349,639,17,true);
                addWall(1574,491,179,176,true);
                addWall(1575,1015,178,178,true);
                addWall(1049,1016,180,178,true);
                addWall(525,1015,178,179,true);
                addWall(-1,1015,178,177,true);
                addWall(179,1081,347,39,false);
                addWall(703,1083,348,38,false);
                addWall(1229,1079,347,36,false);
                addWall(1645,667,39,348,false);
            }
            case 14 ->{
                addWall(1,0,2578,22,true);
                addWall(2558,23,21,1697,true);
                addWall(-1,1695,2559,25,true);
                addWall(-1,1,23,1694,true);
                addWall(18,543,140,157,true);
                addWall(21,1020,139,157,true);
                addWall(710,1235,1186,246,true);
                addWall(1652,1481,244,217,true);
                addWall(2421,1471,135,160,true);
                addWall(2420,994,139,157,true);
                addWall(1505,740,244,245,true);
                addWall(1995,616,244,244,true);
                addWall(2241,246,243,243,true);
                addWall(718,147,244,492,true);
                addWall(963,344,1045,149,true);
                addWall(1850,14,158,330,true);
                addWall(2423,1639,131,55,false);
                addWall(962,334,889,10,true);
            }
            case 15 ->{
                addWall(-1,0,802,205,true);
                addWall(799,0,1400,22,true);
                addWall(2181,22,18,1179,true);
                addWall(0,1174,2181,27,true);
                addWall(0,997,800,202,true);
                addWall(0,202,20,997,true);
                addWall(-1,21,801,183,true);
                addWall(772,205,28,272,true);
                addWall(445,447,355,30,true);
                addWall(18,445,226,30,true);
                addWall(20,687,224,30,true);
                addWall(444,688,955,28,true);
                addWall(1021,400,28,495,true);
                addWall(1021,18,28,181,true);
                addWall(1320,19,258,242,true);
                addWall(1601,688,581,28,true);
                addWall(1839,68,100,300,false);
                addWall(1800,509,178,178,false);
                addWall(1841,21,101,45,false);
            }
            case 16 ->{
                addWall(0,379,1599,22,true);
                addWall(1580,0,19,401,true);
                addWall(0,0,1580,18,true);
                addWall(0,18,18,361,true);
                addWall(257,18,42,53,true);
                addWall(854,19,47,53,true);
                addWall(555,328,45,55,true);
                addWall(1156,329,45,52,true);
                addWall(500,20,155,36,false);
                addWall(199,345,156,35,false);
                addWall(799,344,155,37,false);
                addWall(1100,19,155,38,false);
            }
            default -> {
            }
        }
     }
     private void buildEnemies(){ //now have to do the same with enemies. methods split to make it a little simpler and be clear what does what.
                  switch (level){
             case 1 ->{
               addEnemy(588,1675,"Knife");
                addEnemy(1905,1719,"Knife");
                addEnemy(2175,880,"Knife");
                addEnemy(2131,82,"Knife");
                addEnemy(1548,212,"Knife");
                addEnemy(77,992,"Knife");
                addEnemy(391,1193,"Knife");
                addEnemy(1803,1505,"Pistol");
                addEnemy(2182,1289,"Pistol");
                addEnemy(1057,426,"Pistol");
                addEnemy(562,86,"Pistol");
                addEnemy(235,353,"Pistol");

             }
             case 2 ->{
                 addEnemy(617,201,"Knife");
                 addEnemy(617,657,"Knife");
                 addEnemy(1372,418,"Knife");
                 addEnemy(138,306,"Pistol");
                addEnemy(141,552,"Pistol");
             }
             case 3 ->{
                 addEnemy(1210,1412,"Knife");
                addEnemy(1084,1108,"Knife");
                addEnemy(1078,1286,"Knife");
                addEnemy(836,1447,"Pistol");
                addEnemy(847,1086,"Pistol");
                addEnemy(856,580,"Pistol");
                addEnemy(1192,98,"Pistol");
                addEnemy(1216,981,"Pistol");

             }
             case 4 ->{
                 addEnemy(198,1005,"Knife");
                    addEnemy(1490,243,"Knife");
                    addEnemy(155,1237,"Pistol");
                    addEnemy(1505,751,"Pistol");
                    addEnemy(71,273,"Pistol");
                    addEnemy(62,541,"Pistol");
                    addEnemy(951,212,"Rifle");
                    addEnemy(674,111,"Rifle");
             }
             case 6 ->{
                 addEnemy(717,429,"Knife");
                addEnemy(591,896,"Knife");
                addEnemy(273,892,"Knife");
                addEnemy(1400,819,"Pistol");
                addEnemy(1423,546,"Pistol");
                addEnemy(1388,179,"Rifle");
                addEnemy(433,1028,"Rifle");
             }
             case 7 ->{
                addEnemy(98,243,"Knife");
               addEnemy(297,358,"Knife");
               addEnemy(665,355,"Knife");
               addEnemy(897,359,"Knife");
               addEnemy(1155,365,"Knife");
               addEnemy(1469,357,"Knife");
               addEnemy(1730,355,"Pistol");
               addEnemy(483,358,"Rifle");

             }
             case 8 ->{
                 addEnemy(381,513,"Knife");
                addEnemy(1606,940,"Knife");
                addEnemy(1414,1438,"Pistol");
                addEnemy(1657,348,"Pistol");
                addEnemy(2016,265,"Rifle");
                addEnemy(1944,1409,"Rifle");
             }
             case 9 ->{
                addEnemy(75,494,"Knife");
                addEnemy(184,494,"Knife");
                addEnemy(338,501,"Knife");
                addEnemy(443,490,"Knife");
                addEnemy(1849,717,"Knife");
                addEnemy(1677,780,"Pistol");
                addEnemy(1775,463,"Pistol");
                addEnemy(1340,797,"Pistol");
                addEnemy(1462,791,"Pistol");
                addEnemy(1176,805,"Pistol");
                addEnemy(986,826,"Pistol");
                addEnemy(1885,810,"Rifle");
                addEnemy(1892,453,"Rifle");


             }
             case 10 ->{
                addEnemy(327,281,"Rifle");
                addEnemy(991,277,"Rifle");
                addEnemy(654,97,"Rifle");

             }
             case 13 ->{
                addEnemy(1230,1315,"Knife");
                    addEnemy(1213,1528,"Knife");
                    addEnemy(1868,363,"Knife");
                    addEnemy(1067,372,"Knife"); 
                    addEnemy(742,70,"Pistol");
                    addEnemy(732,357,"Pistol");
                    addEnemy(162,1266,"Rifle");
                    addEnemy(1373,922,"Rifle");
                    addEnemy(872,929,"Rifle");
                    addEnemy(242,939,"Rifle");
                    addEnemy(592,924,"Rifle");

             }
             case 14 ->{
                 addEnemy(1231,660,"Pistol");
                    addEnemy(1247,867,"Pistol");
                    addEnemy(1226,1148,"Pistol");
                    addEnemy(1049,248,"Pistol");
                    addEnemy(916,1600,"Pistol");
                    addEnemy(2349,180,"Pistol");
                    addEnemy(2152,1613,"Pistol");
                    addEnemy(1724,237,"Rifle");
                    addEnemy(1416,262,"Rifle");
                    addEnemy(1459,1592,"Rifle");
                    addEnemy(2132,1326,"Rifle");
             }
             case 15 ->{
              addEnemy(118,333,"Pistol");
                    addEnemy(109,869,"Pistol");   
                    addEnemy(913,829,"Rifle");
                    addEnemy(729,408,"Rifle");
                    addEnemy(1149,811,"Rifle");
                    addEnemy(1763,181,"Rifle");
                    addEnemy(1194,116,"Rifle");
                    addEnemy(2082,606,"Rifle");
                    addEnemy(2066,145,"Rifle");
             }//level 16 is only anton
         }
     }
      private void addWall(int x, int y, int width, int height, boolean bool){
          if (bool == true) Main.walls.add(new Wall(x,y,width,height,0));
          if (bool == false) Main.walls.add(new Wall(x,y,width,height,1));
     }//Methods to add a wall or enemy to the arrays in main
     private void addEnemy(int x, int y, String weapon){
         int enemyLevel = 1;
         switch (level){
             case 1,2 -> enemyLevel = 1;
             case 3 -> enemyLevel = 2;
             case 4,5 -> enemyLevel = 3;
             case 6,7,8 -> enemyLevel = 4;
             case 9,10,11 -> enemyLevel = 5;
             case 12,13 -> enemyLevel = 6;
             case 14,15,16 -> enemyLevel = 7;
             //This is important to adjust enemy levels, since technically there are only 7 levels, some of which having more stages. 
             //Because of this, even if they may be different levels the enemy level should be the same for all stages of the same level
         }
         Main.enemies.add(new Enemy(x,y,weapon, enemyLevel, false));
     }
     public Rectangle getTransition(){ //returns the right transition rectangle for each level
        switch (level) {
            case 1 -> { return new Rectangle(-20 - Main.worldX, 698 - Main.worldY, 48, 135); }
            case 3 -> { return new Rectangle(932 - Main.worldX, 2120 - Main.worldY, 189, 68); }
            case 4 -> { return new Rectangle(-30 - Main.worldX, 285 - Main.worldY, 84, 186); }
            case 6 -> { return new Rectangle(0 - Main.worldX, 355 - Main.worldY, 46, 139); }
            case 7 -> { return new Rectangle(1761 - Main.worldX, 270 - Main.worldY, 54, 166); }
            case 8 -> { return new Rectangle(-40 - Main.worldX, 845 - Main.worldY, 71, 153); } 
            case 9 -> { return new Rectangle(1964 - Main.worldX, 88 - Main.worldY, 46, 191); }
            case 10 -> { return new Rectangle(490 - Main.worldX, 0 - Main.worldY, 217, 59); }
            case 12 -> { return new Rectangle(14 - Main.worldX, 456 - Main.worldY, 57, 157); }
            case 14 -> { return new Rectangle(2531 - Main.worldX, 1151 - Main.worldY, 26, 321); }
            case 15 -> { return new Rectangle(2157 - Main.worldX, 837 - Main.worldY, 23, 223); }
            default -> { return new Rectangle(2000000000, 2000000000, 0, 0); } //unreachable rectangle
            //This gives the transition rectanges for the player to move through levels. Some levels move automatically after a cutscene, others the player has to go to a specific place.
        }
     }
     public void nextLevel(){ //goes to the next level
       
         if (level == 8){ //level 8 is the only level that goes back a level, as the player goes into a hallway. Loadfakelevel loads that level with a different spawn and no enemies, and the loading of it
             //is technically a cutscene in a way.
             level--;
             loadFakeLevel();
             Main.enemies.clear();
             
         }else{
         level++;
         loadLevel(level);//go to next level otherwise
         }
         
     }
     
     
     private void loadFakeLevel(){ //code for after planting a bomb and escaping "blowing up" the lab offscreen.
        Main.setWorld(0, 0);
        Main.bodies.clear();
       Main.getPlayer().resetPlayer();
        initWalls();
        initValues();
        Main.setWorld(600, -200);
        Main.getPlayer().playable = false;
        inBombScene = true;
        new Thread(() -> {
            try {   
                Main.setLabels(false);
                Thread.sleep(2000);
                try {
                    Main.playSound("explosion");
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Lvl.class.getName()).log(Level.SEVERE, null, ex);
                }
                Thread.sleep(7100); //waits a very long and annoying time (basically a cutscene) before sending the player to level 9
                level += 2;
                inBombScene = false;
                loadLevel(level);
            }catch (InterruptedException e) {
            }
                }).start();
     }
     public String getObjective(){ //returns the objective for each level.
          switch (level){
             case 1 ->{
                 return "Find Vladimir Petrovitch";
             }
             case 2 ->{
                 return "Eliminate Vladimir Petrovitch";
             }
             case 3 ->{
                 if (cutsceneHappened) return "Leave the area the same way you came";
                 return "Eliminate the Street Gang";
             }
             case 4 ->{
                 return "Get to Ronan's office";
             }
             case 5 ->{
                 return "Get answers from Ronan";
             }
             case 6 ->{
                 return "Find the Meth Lab";
             }
             case 7 ->{
                 return "Find the Meth Lab";
             }
             case 8 ->{
                 return "Plant a bomb then escape";
             }
             case 9 ->{
                 return "Find Dimitri Petrovitch";
             }
             case 10 ->{
                 return "Get to Dimitri's Office";
             }
             case 11 ->{
                 return "Eliminate Demitri Petrovitch (with a knife)";
             }
             case 12 ->{
                 return "Find Ronan";
             }
             case 13 ->{
                 return "Eliminate Ronan and his men";
             }
             case 14 ->{
                 if (Main.enemies.isEmpty()) return "Find the leader";
                 return "Defend Yourself";
             }
             case 15 ->{
                 return "Find the leader";
             }
             case 16 ->{
                 if (cutsceneHappened) return "Eliminate Anton";
                 return "Eliminate the Masked Leader";
             }
             default -> {
                 return "";
             }
         }
     }
     
    public void playScene(int scene){ //public method for playing cutscenes
        switch (scene){
            case 1 -> scene1();
            case 2 -> scene2();
            case 3 -> scene3();
            case 4 -> scene4();
            case 5 -> scene5();
            case 6 -> scene6();
            case 7 -> scene7();
            case 8 -> scene8();
        }
        Main.setLabels(false);
        Main.getPlayer().playable = false;
        inCutscene = true;
        cutsceneHappened = true;
    } 
     //logic for all cutscenes. 
    private void scene1(){
         cutscenePlayer = new CutscenePlayer("Vladimir1",this::onCutsceneEnd);
        cutscenePlayer.start();
        try {
            Main.playSound("scene1");
        } catch (UnsupportedAudioFileException ex) {
        }
    }
     
    private void scene2(){
    //  playTransition(0);
      cutscenePlayer = new CutscenePlayer("Driving",this::onCutsceneEnd);
        cutscenePlayer.start();
        try {
            Main.playSound("scene2");
        } catch (UnsupportedAudioFileException ex) {
        }
        instantNext = true;
    }
    
    private void scene3(){
         cutscenePlayer = new CutscenePlayer("Treyvon1",this::onCutsceneEnd);
        cutscenePlayer.start();
         try {
            Main.playSound("scene3");
        } catch (UnsupportedAudioFileException ex) {
        }
         
    }
    
    private void scene4(){
         cutscenePlayer = new CutscenePlayer("Ronan1",this::onCutsceneEnd);
        cutscenePlayer.start();
         try {
            Main.playSound("scene4");
        } catch (UnsupportedAudioFileException ex) {
        }
         nextLevelAfterScene = true;
    }
    
    private void scene5(){
         cutscenePlayer = new CutscenePlayer("Dimitri1",this::onCutsceneEnd);
        cutscenePlayer.start();
        nextLevelAfterScene = true;
         try {
            Main.playSound("scene5");
        } catch (UnsupportedAudioFileException ex) {
        }
         new Thread(() -> {
        try {
            Thread.sleep(39000); 
            currentBoss.takeDamage(100);
        } catch (InterruptedException e) {
        }
    }).start();
         
    }
    
    private void scene6(){
        cutscenePlayer = new CutscenePlayer("Ronan2",this::onCutsceneEnd); 
        cutscenePlayer.start();
        nextLevelAfterScene = true;
         try {
            Main.playSound("scene6");
        } catch (UnsupportedAudioFileException ex) {
        }
         new Thread(() -> {
        try {
            Thread.sleep(21800); 
            currentBoss.takeDamage(100);
        } catch (InterruptedException e) {
        }
    }).start();
    }
    
    private void scene7(){
        cutscenePlayer = new CutscenePlayer("Anton1",this::onCutsceneEnd);
        cutscenePlayer.start();
        anton1scene = true;
         try {
            Main.playSound("scene7");
        } catch (UnsupportedAudioFileException ex) {
        }
    }
    
    private void scene8(){
        cutscenePlayer = new CutscenePlayer("Anton2",this::onCutsceneEnd); 
        cutscenePlayer.start();
         try {
            Main.playSound("scene8");
        } catch (UnsupportedAudioFileException ex) {
        }
          new Thread(() -> {
        try {
            Thread.sleep(5000); 
            currentBoss.changeSkinToAnton();
        } catch (InterruptedException e) {
        }
    }).start();
        
    }
     
    public void endCutscene(){ //we wanted to make a key to skip cutscenes but we couldnt figure out how to stop audio in time so this is here for now
        onCutsceneEnd();
    }
      private void onCutsceneEnd() { // callback method for ending a cutscene
        inCutscene = false; // Mark that the cutscene is finished
        Main.setLabels(true);
        Main.getPlayer().playable = true;
        cutsceneHappened = true;
        if (nextLevelAfterScene){
            nextLevelAfterScene = false; //some levels go to the next level after a cutscene, this makes that happen after 3 seconds
             new Thread(() -> {
        try {
            Thread.sleep(3000); 
            nextLevel();
        } catch (InterruptedException e) {
        }
    }).start();
        }else if (instantNext){ //some cutscenes instantly go to the next level
            instantNext = false;
            nextLevel();
        }
        if (level == 14) cutsceneHappened = false;
    }
      
    public void drawCutscene(Graphics g){ //draws the current cutscene frame
        if (inCutscene) {
            Main.getPlayer().playable = false;
            Main.setLabels(false);
            BufferedImage frame = cutscenePlayer.getCurrentFrame();
            if (frame != null) {
                g.drawImage(frame, 0, 0, 1920, 1080, null);
            }
              }
                if (transition != null){
            g.drawImage(transition, 0, 0, 1920, 1080, null);
        }
}
    
    private void playTransition(int i){ //plays transition screens
        try {
            transition = (ImageIO.read(new File("transitions/"+i+".png")));
            Main.getPlayer().playable = false;
            Main.setLabels(false);
        } catch (IOException ex) {
            transition = null;
            Main.getPlayer().playable = true;
            Main.setLabels(true);
        }
        if (transition != null){
            new Thread(() -> {
        try {
            Thread.sleep(3000); // Wait for 3 seconds (3000 milliseconds)
            transition = null; // Set transition to null after the delay
            Main.getPlayer().playable = true;
            Main.setLabels(true);
        } catch (InterruptedException e) {
        }
    }).start();
        }
        
        
        
        }
    
    
    public boolean canPause(){ //returns if player can pause
        return !(inCutscene || inBombScene);
    }
    
    public int getCurrentLevel(){ //getter for level
        return level;
    }
    
   

    //PATHFINDING

public void buildNavGrid() {
    ArrayList<Wall> walls = Main.walls;
    
    if (walls == null || walls.isEmpty()) {
        // Default grid if no walls
        navTileSize = 32;
        navWalkable = new boolean[64][48]; // 2048x1536 world
        for (int tx = 0; tx < 64; tx++) {
            for (int ty = 0; ty < 48; ty++) {
                navWalkable[tx][ty] = true;
            }
        }
        pathFinder = new PathFinder(this, navTileSize);
        return;
    }

    // Determine world bounds from ALL walls (including softwalls for navigation)
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;
    
    for (Wall w : walls) {
        if (w == null || w.isStair()) continue;
        // Use actual wall coordinates (they're stored with world offsets removed)
        minX = Math.min(minX, w.x);
        minY = Math.min(minY, w.y);
        maxX = Math.max(maxX, w.x + w.width);
        maxY = Math.max(maxY, w.y + w.height);
    }
    
    // Add padding around the map
    int padding = 100;
    minX -= padding;
    minY -= padding;
    maxX += padding;
    maxY += padding;
    
    // Ensure positive coordinates
    if (minX < 0) minX = 0;
    if (minY < 0) minY = 0;
    
    // Use a reasonable tile size - 32 works well for your game scale
    navTileSize = 32;
    
    // Calculate grid dimensions
    int cols = (int) Math.ceil((double)(maxX - minX) / navTileSize);
    int rows = (int) Math.ceil((double)(maxY - minY) / navTileSize);
    
    // Create walkable grid
    navWalkable = new boolean[cols][rows];
    
    // Initialize all tiles as walkable
    for (int tx = 0; tx < cols; tx++) {
        for (int ty = 0; ty < rows; ty++) {
            navWalkable[tx][ty] = true;
        }
    }
    
    // Mark tiles that are too close to walls as unwalkable (using AABB for enemies)
    for (int tx = 0; tx < cols; tx++) {
        for (int ty = 0; ty < rows; ty++) {
            double cx = minX + tx * navTileSize + navTileSize/2.0;
            double cy = minY + ty * navTileSize + navTileSize/2.0;
            
            // A tile is walkable if an enemy's AABB (100x33) centered here doesn't collide with walls
            boolean walk = true;
            if (walls != null) {
                // Enemy AABB dimensions (axis-aligned)
                int halfWidth = 50;   // 100/2
                int halfHeight = 17;  // 33/2
                int buffer = 5;       // Small safety margin
                
                for (Wall w : walls) {
                    if (w == null || w.isStair()) continue;
                    
                    // Check if enemy's AABB would intersect wall
                    if (cx + halfWidth + buffer >= w.x && 
                        cx - halfWidth - buffer <= w.x + w.width &&
                        cy + halfHeight + buffer >= w.y && 
                        cy - halfHeight - buffer <= w.y + w.height) {
                        walk = false;
                        break;
                    }
                }
            }
            navWalkable[tx][ty] = walk;
        }
    }
    
    // Create PathFinder
    pathFinder = new PathFinder(this, navTileSize);
    
    // Store the grid origin for coordinate conversion
    this.gridOriginX = minX;
    this.gridOriginY = minY;
}


/**
 * PathFinder
     * @return 
 */
public PathFinder getPathFinder() {
    if (pathFinder == null) buildNavGrid();
    return pathFinder;
}

/**
 * Return the nav tile size chosen.
     * @return 
 */
public int getNavTileSize() {
    if (navTileSize <= 0) buildNavGrid();
    return navTileSize;
}

/**
 * Helpers used by PathFinder / smoothing:
     * @param tx
     * @param ty
     * @return 
 */
public boolean isInBoundsTile(int tx, int ty) {
    if (navWalkable == null) buildNavGrid();
    return tx >= 0 && ty >= 0 && tx < navWalkable.length && ty < navWalkable[0].length;
}


public Point worldToGrid(double worldX, double worldY) {
    if (navTileSize <= 0) return new Point(0, 0);
    int tx = (int)((worldX - gridOriginX) / navTileSize);
    int ty = (int)((worldY - gridOriginY) / navTileSize);
    return new Point(tx, ty);
}

public Point gridToWorld(int tx, int ty) {
    if (navTileSize <= 0) return new Point(0, 0);
    int wx = tx * navTileSize + navTileSize / 2 + gridOriginX;
    int wy = ty * navTileSize + navTileSize / 2 + gridOriginY;
    return new Point(wx, wy);
}

// Fix the isTileWalkable method:
public boolean isTileWalkable(int tx, int ty) {
    if (navWalkable == null) return false;
    return tx >= 0 && ty >= 0 && tx < navWalkable.length && ty < navWalkable[0].length && navWalkable[tx][ty];
}

/**
 * Check line of sight using your existing LOS logic.We keep your existing LOS semantics (hardwalls block sight; softwalls do not).I invoke your existing method if present (please keep/adjust the call name if different).
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return 
 */
public boolean lineOfSightWorldPoints(int x1, int y1, int x2, int y2) {
    ArrayList<Wall> walls = Main.walls;
    // If you already have a function for LOS (raycast) keep using it here:
    // e.g. return this.isLineOfSightClear(x1, y1, x2, y2);
    // If you don't have that exact method name, replace with your existing LOS call.
    // The fallback below performs a conservative hardwall-only check by iterating along the line.
    double dx = x2 - x1;
    double dy = y2 - y1;
    double dist = Math.hypot(dx, dy);
    int steps = Math.max(8, (int)(dist / (getNavTileSize() / 4.0)));
    for (int i = 0; i <= steps; i++) {
        double t = (double)i / steps;
        int sx = (int)Math.round(x1 + t * dx);
        int sy = (int)Math.round(y1 + t * dy);
        // test if any hardwall covers this point
        if (walls != null) {
            for (Wall w : walls) {
                if (w == null) continue;
                if (!w.isHardwall()) continue; // softwalls don't block LOS
                if (sx >= w.x && sx <= w.x + w.width && sy >= w.y && sy <= w.y + w.height) {
                    return false;
                }
            }
        }
    }
    return true;
}

/**
 * Alert enemies to a loud sound (gunshot).Call from Weapon.fire() when weapon.isLoud() is true.
     * @param worldX
     * @param worldY
 */
public void alertEnemies(int worldX, int worldY) {
    ArrayList<Enemy> enemies = Main.enemies;
    if (enemies == null) return;
    for (Enemy e : enemies) {
        if (e == null) continue;
        e.hearGunshot(worldX, worldY);
    }
}


public boolean isTileWalkableWithEnemyCheck(int tx, int ty, Enemy checkingEnemy) {
    if (!isTileWalkable(tx, ty)) return false;
    
    // Check if any other enemy is on or near this tile
    if (Main.enemies != null && checkingEnemy != null) {
        
        for (Enemy e : Main.enemies) {
            if (e == checkingEnemy || e == null || e.getHealth() <= 0) continue;
            
            // Get enemy's grid position
            Point enemyGrid = worldToGrid(e.get("x"), e.get("y"));
            
            // If enemy is on this tile or adjacent (1 tile away), it's not walkable
            if (Math.abs(enemyGrid.x - tx) <= 1 && Math.abs(enemyGrid.y - ty) <= 1) {
                return false;
            }
        }
    }
    
    return true;
}
}