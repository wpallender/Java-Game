package entity;

import main.GamePanel;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Color;

import main.UtilityTool;

public class Entity {

    GamePanel gp;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2, attackRight1, attackRight2;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public Rectangle attackArea = new Rectangle(0, 0, 0, 0);
    public int solidAreaDefaultX, solidAreaDefaultY;
    String dialogues[] = new String[20];
    public BufferedImage image, image2, image3;
    public boolean collision = false;
    //State
    public int worldX, worldY;
    public String direction = "down";
    public int spriteNum = 1;
    int dialogueIndex = 0;
    public boolean collisionOn = false;
    public boolean invincible = false;
    boolean attacking = false;
    public boolean alive = true;
    public boolean dying = false;
    boolean healthBarOn = false;
    //Counter
    public int spriteCounter = 0;
    public int actionLockCounter = 0;
    public int invincibleCounter = 0;
    public int shotCounter = 0;
    int dyingCounter = 0;
    int healthBarCounter = 0;
    //Entity attributes
    public String name;
    public int speed;
    public int maxLife;
    public int life;
    public int maxMoney;
    public int money;
    public int maxAmmo;
    public int ammo;
    public int level;
    public int strength;
    public int skill;
    public int attack;
    public int defense;
    public int exp;
    public int nextLevelExp;
    public Entity currentTool;
    public Entity currentShield;
    public Projectile projectile;
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 9;
    //Item attributes
    public int value;
    public int attackValue;
    public int defenseValue;
    public String description = "";
    public int ammoCost;
    //Type
    public int type; 
    public static final int type_player = 0;
    public static final int type_npc = 1;
    public static final int type_enemy = 2;
    public static final int type_sword = 3;
    public static final int type_axe = 4;
    public static final int type_shield = 5;
    public static final int type_consumable = 6;
    public static final int type_pickupOnly = 7;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public void setAction() {}

    public void damageReaction() {}

    public void speak() {
        if (dialogues[dialogueIndex] == null) {dialogueIndex = 0;}
        gp.ui.currentDialogue = dialogues[dialogueIndex];
        dialogueIndex++;

        switch(gp.player.direction) {
            case "up":
                direction = "down";
                break;
            case "down":
                direction = "up";
                break;
            case "left":
                direction = "right";
                break;
            case "right":
                direction = "left";
                break;
        }
    }
    public void update() {
        setAction();
        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkObject(this, false);
        gp.cChecker.checkEntity(this, gp.npc);
        gp.cChecker.checkEntity(this, gp.enemies);
        gp.cChecker.checkEntity(this, gp.iTile);
        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        //Enemy attacks player
        if (this.type == type_enemy && contactPlayer == true) {
            damagePlayer(attack);
        }
        //If collision is false, player can move
            if (collisionOn == false) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed;break;
                }
            }
            //Sprite counter
            spriteCounter++;
            if (spriteCounter > 12) {
                if (spriteNum == 1) {
                    spriteNum = 2;
                }
                else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
           
            }
            //Invincible
            if (invincible == true) {
                invincibleCounter++;
                if (invincibleCounter > 40) {
                    invincible = false;
                    invincibleCounter = 0;
                }
            }
            //Shot counter
            if (shotCounter < 30) {
                shotCounter++;
            }
    }

    public void damagePlayer(int attack) {
        if (gp.player.invincible == false) {
            int damage = attack - gp.player.defense;
            if (damage < 0) { damage = 0; }
            gp.player.life -= damage;
            gp.player.invincible = true;
        }
    }

    public void use(Entity entity) {}

    public void checkDrop() {}

    public void dropItem(Entity droppedItem) {
        for (int i = 0; i< gp.obj[1].length; i++) {
            if (gp.obj[gp.currentMap][i] == null) { 
                gp.obj[gp.currentMap][i] = droppedItem; 
                //Dead enemy coordinates
                gp.obj[gp.currentMap][i].worldX = worldX;
                gp.obj[gp.currentMap][i].worldY = worldY;
                break;
            }
        }
    }

    //Particles
    public Color getParticleColor() { Color color = null; return color; }
    public int getParticleSize() { int size = 0; return size; }
    public int getParticleSpeed() { int speed = 0; return speed; }
    public int getParticleMaxLife() { int maxLife = 0; return maxLife; }
    public void generateParticle(Entity generator, Entity target) {
        Color color = generator.getParticleColor();
        int size = generator.getParticleSize();
        int speed = generator.getParticleSpeed();
        int maxLife = generator.getParticleMaxLife();

        //Set amount of particles
        Particle p1 = new Particle(gp, target, color, size, speed, maxLife, -1, -1);
        Particle p2 = new Particle(gp, target, color, size, speed, maxLife, 1, -1);
        Particle p3 = new Particle(gp, target, color, size, speed, maxLife, -1, 1);
        Particle p4 = new Particle(gp, target, color, size, speed, maxLife, 1, 1);
        gp.particleList.add(p1);
        gp.particleList.add(p2);
        gp.particleList.add(p3);
        gp.particleList.add(p4);
    }

    public void draw(Graphics2D g2) {

        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
            
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            switch(direction) {
                case "up":
                    if (spriteNum == 1) { image = up1; }
                    else if (spriteNum == 2) { image = up2; }
                    break;
                case "down":
                    if (spriteNum == 1) { image = down1; }
                    else if (spriteNum == 2) { image = down2; }
                    break;
                case "left":
                    if (spriteNum == 1) { image = left1; }
                    else if (spriteNum == 2) { image = left2; }
                    break;
                case "right":
                    if (spriteNum == 1) { image = right1; }
                    else if (spriteNum == 2) { image = right2; }
                    break;
            }

            //Enemy health bar
            if (type == 2 && healthBarOn == true) {
                double oneScale = (double)gp.tileSize / maxLife;
                double healthBarValue = oneScale * life;

                g2.setColor(new Color(35, 35, 35));
                g2.fillRect(screenX - 2, screenY - 12, gp.tileSize + 4, 14);
                g2.setColor(new Color(255, 0, 30));
                g2.fillRect(screenX, screenY - 10, (int)healthBarValue, 10);

                healthBarCounter++;

                if (healthBarCounter > 600) {
                    healthBarCounter = 0;
                    healthBarOn = false;
                }
            }

            //Enemy invincibility
            if (invincible == true) {
                healthBarOn = true;
                healthBarCounter = 0;
                changeAlpha(g2, 0.4F);
            }

            if (dying == true) { dyingAnimation(g2); }

            g2.drawImage(image, screenX, screenY, null);
            
            changeAlpha(g2, 1F);
        }
    }

    public void dyingAnimation(Graphics2D g2) {
        dyingCounter++;
        int i = 5;
        if (dyingCounter <= i) { changeAlpha(g2, 0F); }
        if (dyingCounter > i && dyingCounter <= (i * 2)) { changeAlpha(g2, 1F); }
        if (dyingCounter > (i * 2) && dyingCounter <= (i * 3)) { changeAlpha(g2, 0F); }
        if (dyingCounter > (i * 3) && dyingCounter <= (i * 4)) { changeAlpha(g2, 1F); }
        if (dyingCounter > (i * 4) && dyingCounter <= (i * 5)) { changeAlpha(g2, 0F); }
        if (dyingCounter > (i * 5) && dyingCounter <= (i * 6)) { changeAlpha(g2, 1F); }
        if (dyingCounter > (i * 6) && dyingCounter <= (i * 7)) { changeAlpha(g2, 0F); }
        if (dyingCounter > (i * 7) && dyingCounter <= (i * 8)) { changeAlpha(g2, 1F);}
        if (dyingCounter > (i * 8)) {
            alive = false;
        }
    }

    public void changeAlpha(Graphics2D g2, float alphaValue) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
    }

    public BufferedImage setup(String imagePath, int width, int height) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try {
            // Remove leading slash if present
            String cleanPath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
            
            // Try resource loading with different variations
            InputStream is = getClass().getResourceAsStream("/res/" + cleanPath + ".png");
            if (is == null) { is = getClass().getResourceAsStream("res/" + cleanPath + ".png"); }
            
            if (is != null) {
                image = ImageIO.read(is);
                image = uTool.scaleImage(image, width, height);
            } else {
                System.err.println("ERROR: Could not find resource: res/" + cleanPath + ".png");
            }
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
        return image;
    }
}
