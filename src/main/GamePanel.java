package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JPanel;

import tile.TileManager;
import tile_interactive.InteractiveTile;
import entity.Entity;

public class GamePanel extends JPanel implements Runnable {
    
    //Screen Settings
    final int originalTileSize = 16; //16x16 tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; //48x48 tile
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; //960 pixels
    public final int screenHeight = tileSize * maxScreenRow; //576 pixels

    //World Settings
    public final int maxWorldCol = 100;
    public final int maxWorldRow = 100;
    public int worldWidth = tileSize * maxWorldCol;
    public int worldHeight = tileSize * maxWorldRow;
    public final int maxMap = 10;
    public int currentMap = 0;

    //Full screen
    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    Graphics2D g2;
    public boolean fullScreenOn = false;

    //FPS
    int FPS = 60;

    //System
    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public entity.Player player = new entity.Player(this, keyH);
    public UI ui = new UI (this);
    public EventHandler eventH = new EventHandler(this);
    Config config = new Config(this);
    Thread gameThread;

    //Sound
    Sound soundEffect = new Sound();
    Sound music = new Sound();
    public void playMusic(int i) { music.setFile(i); music.play(); music.loop(); }
    public void stopMusic() { music.stop(); }
    public void playSoundEffect(int i) { soundEffect.setFile(i); soundEffect.play(); }

    //Entity and objects
    public AssetSetter aSetter = new AssetSetter(this);
    public Entity obj[][] = new Entity[maxMap][20];
    public Entity npc[][] = new Entity[maxMap][10];
    public Entity enemies[][] = new Entity[maxMap][20];
    public TileManager tileM;
    public InteractiveTile[][] iTile;
    ArrayList<Entity> entityList = new ArrayList<>();
    public ArrayList<Entity> projectileList = new ArrayList<>();
    public ArrayList<Entity> particleList = new ArrayList<>();
    
    public void updateWorldSize() {
        worldWidth = tileSize * tileM.mapCol[currentMap];
        worldHeight = tileSize * tileM.mapRow[currentMap];
    }   

    //Setup Game (defaults)
    public void setupGame() {
        aSetter.setObject();
        aSetter.setNPC();
        aSetter.setEnemies();
        gameState = titleState;

        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D)tempScreen.getGraphics();

        if (fullScreenOn == true) { setFullScreen(); }
        tileM.refreshInteractiveTiles();
        updateWorldSize();  
    }

    // Call this method whenever the map changes
    public void changeMap(int newMap) {
        currentMap = newMap;
        tileM.refreshInteractiveTiles();
        updateWorldSize();
    }

    //Game State
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int characterStatsState = 4;
    public final int settingsState = 5;
    public final int gameOverState = 6;
    public final int transitionState = 7;

    public GamePanel() {
    iTile = new InteractiveTile[maxMap][maxWorldCol * maxWorldRow];
    tileM = new TileManager(this);
    this.setPreferredSize(new java.awt.Dimension(screenWidth, screenHeight));
    this.setBackground(java.awt.Color.black);
    this.setDoubleBuffered(true);
    this.addKeyListener(keyH);
    this.setFocusable(true);
    playMusic(0);
    }

    public void retry() {
        player.setDefaultPositions();
        player.restoreLife();
    }

    public void restart() {
        player.setDefaultValues();
        player.setDefaultPositions();
        player.restoreLife();
        player.setItems();
        aSetter.setObject();
        aSetter.setNPC();
        aSetter.setEnemies();
    }

    public void setFullScreen() {
        //Get local device screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(Main.window);
        //Get screen width and height of full screen
        screenWidth2 = Main.window.getWidth();
        screenHeight2 = Main.window. getHeight();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double drawInterval = 1000000000 / FPS; //0.01666 seconds
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            drawToTempScreen(); //Draw everything to buffered image
            drawToScreen(); //Draw buffered image tp screen
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;
                if (remainingTime < 0) {
                    remainingTime = 0;
                }
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }   

    //Update
    public void update() {
        if (gameState == playState) {
            //Player
            player.update();
            eventH.checkEvent();
            //NPC
            for (int i = 0; i < npc[1].length; i++) {
                if (npc[currentMap][i] != null) { npc[currentMap][i].update(); }
            }
            //Enemeis
            for (int i = 0; i < enemies[1].length; i++) {
                if (enemies[currentMap][i] != null) {
                    if (enemies[currentMap][i].alive == true && enemies[currentMap][i].dying == false) { enemies[currentMap][i].update(); } 
                    if (enemies[currentMap][i].alive == false) { 
                        enemies[currentMap][i].checkDrop(); 
                        enemies[i] = null;
                    }
                }
            }
            //Projectiles
            for (int i = 0; i < projectileList.size(); i++) {
                if (projectileList.get(i) != null) {
                    if (projectileList.get(i).alive == true) { projectileList.get(i).update(); } 
                    if (projectileList.get(i).alive == false) { projectileList.remove(i); }
                }
            }
            //Interactive tiles
            for (int i = 0; i < iTile[1].length; i++) {
                if (iTile[currentMap][i] != null) { iTile[currentMap][i].update(); }
            }
            //Particles
            for (int i = 0; i < particleList.size(); i++) {
                if (particleList.get(i) != null) {
                    if (particleList.get(i).alive == true) { particleList.get(i).update(); } 
                    if (particleList.get(i).alive == false) { particleList.remove(i); }
                }
            }
        }
        if (gameState == pauseState) {
            //Nothing for now
        }
    }
    
    public void drawToTempScreen() {
        //DEBUG
        long drawStart = 0;
        if (keyH.checkDrawTime == true) { drawStart = System.nanoTime(); }
        //Title screen
        if (gameState == titleState) { ui.draw(g2); }
        else {
            //Tile
            tileM.draw(g2);
            //Interactive tile
            for (int i = 0; i < iTile[1].length; i++) {
                if (iTile[currentMap][i] != null) { iTile[currentMap][i].draw(g2); }
            }
            //Add entities to list
            //Player
            entityList.add(player);
            //NPC
             for (int i = 0; i < npc[1].length; i++) {
                if (npc[currentMap][i] != null) { entityList.add(npc[currentMap][i]); }
             }
             //Objects
             for (int i = 0; i < obj[1].length; i++) {
                if (obj[currentMap][i] != null) { entityList.add(obj[currentMap][i]); }
             }
             //Enemies
             for (int i = 0; i < enemies[1].length; i++) {
                if (enemies[currentMap][i] != null) { entityList.add(enemies[currentMap][i]); }
             }
             //Projectiles
             for (int i = 0; i < projectileList.size(); i++) {
                if (projectileList.get(i) != null) { entityList.add(projectileList.get(i)); }
             }
             //Particles
             for (int i = 0; i < particleList.size(); i++) {
                if (particleList.get(i) != null) { entityList.add(particleList.get(i)); }
             }
             //Sort
             Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    int result = Integer.compare(e1.worldY, e2.worldY);
                    return result;
                }
             });
             //Draw entities
             for (int i = 0; i < entityList.size(); i++) { entityList.get(i).draw(g2); }
             //Empty entity list
             entityList.clear();
            //UI
            ui.draw(g2);
        }
        //DEBUG
        if (keyH.checkDrawTime == true) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            g2.setColor(java.awt.Color.white);
            g2.drawString("Draw Time: " + passed, 10, 400);
            //Find player col and row
            int col = player.worldX / tileSize;
            int row = player.worldY / tileSize;
            g2.drawString("Col: " + col + " Row: " + row, 10, 430);
        }
    }

    public void drawToScreen() {
        Graphics g = getGraphics();
        g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null);
        g.dispose();
    }
}
