package entity;

import main.GamePanel;
import main.KeyHandler;
import object.OBJ_Shield_Wood;
import object.OBJ_Sword_Normal;
import object.OBJ_Bullet;
import object.OBJ_Key;
import main.EventHandler;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;

public class Player extends Entity {

    KeyHandler keyH;
    EventHandler eventH;

    public final int screenX;
    public final int screenY;
    // public int hasKey = 0;
    int standCounter = 0;
    boolean moving = false;
    int pixelCounter = 0;

    public Player(GamePanel gp, KeyHandler keyH) {

        super(gp);

        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new java.awt.Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 32;
        solidArea.height = 32;

        setDefaultValues();
        getPlayerImage();
        getPlayerAttackImage();
        setItems();
    }

    //Spawn
    public void setDefaultValues() {
        worldX = gp.tileSize*24;
        worldY = gp.tileSize*26;
        speed = 4;
        direction = "down";
        //Player status
        maxLife = 20;
        life = maxLife;
        level = 1;
        maxAmmo = 10;
        ammo = maxAmmo;
        strength = 1; //More strength = more damage given
        skill = 1; //More skill = less damage taken
        exp = 0;
        nextLevelExp = 5;
        money = 0;
        currentTool = new OBJ_Sword_Normal(gp);
        currentShield = new OBJ_Shield_Wood(gp);
        projectile = new OBJ_Bullet(gp);
        attack = getAttack(); //Decided by strength and tool
        defense = getDefense(); //DEcided by skill and shield
    }

    public void setDefaultPositions() {
        worldX = gp.tileSize*24;
        worldY = gp.tileSize*22;
        direction = "down";
    }

    public void restoreLife() {
        life = maxLife;
        invincible = false;
    }

    public void setItems() {
        inventory.clear();
        inventory.add(currentTool);
        inventory.add(currentShield);
        inventory.add(new OBJ_Key(gp));
    }

    public int getAttack() { 
        attackArea = currentTool.attackArea;
        return attack = strength * currentTool.attackValue; 
    }
    
    public int getDefense() { return defense = skill * currentShield.defenseValue; }

    public void getPlayerImage() {
        up1 = setup("player/boy_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("player/boy_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("player/boy_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("player/boy_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("player/boy_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("player/boy_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("player/boy_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("player/boy_right_2", gp.tileSize, gp.tileSize);
    }

    public void getPlayerAttackImage() {
        if (currentTool.type == type_sword) {
            attackUp1 = setup ("player/boy_attack_up_1", gp.tileSize, gp.tileSize * 2);
            attackUp2 = setup ("player/boy_attack_up_2", gp.tileSize, gp.tileSize * 2);
            attackDown1 = setup ("player/boy_attack_down_1", gp.tileSize, gp.tileSize * 2);
            attackDown2 = setup ("player/boy_attack_down_2", gp.tileSize, gp.tileSize * 2);
            attackLeft1 = setup ("player/boy_attack_left_1", gp.tileSize * 2, gp.tileSize);
            attackLeft2 = setup ("player/boy_attack_left_2", gp.tileSize * 2, gp.tileSize);
            attackRight1 = setup ("player/boy_attack_right_1", gp.tileSize * 2, gp.tileSize);
            attackRight2 = setup ("player/boy_attack_right_2", gp.tileSize * 2, gp.tileSize);
        }
        if (currentTool.type == type_axe) {
            attackUp1 = setup ("player/boy_axe_up_1", gp.tileSize, gp.tileSize * 2);
            attackUp2 = setup ("player/boy_axe_up_2", gp.tileSize, gp.tileSize * 2);
            attackDown1 = setup ("player/boy_axe_down_1", gp.tileSize, gp.tileSize * 2);
            attackDown2 = setup ("player/boy_axe_down_2", gp.tileSize, gp.tileSize * 2);
            attackLeft1 = setup ("player/boy_axe_left_1", gp.tileSize * 2, gp.tileSize);
            attackLeft2 = setup ("player/boy_axe_left_2", gp.tileSize * 2, gp.tileSize);
            attackRight1 = setup ("player/boy_axe_right_1", gp.tileSize * 2, gp.tileSize);
            attackRight2 = setup ("player/boy_axe_right_2", gp.tileSize * 2, gp.tileSize);
        }
    }

    public void update() {
        // Attack/destroy/mine (ENTER)
        if (keyH.attackPressed == true && attacking == false) {
            attacking = true;
            spriteCounter = 0;
            keyH.attackPressed = false;
        }
        // If attacking, play attack animation and stop here
        if (attacking == true) {
            attacking();
            return;
        }
        // Interact (E)
        int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
        if (keyH.interactPressed == true) {
            interactNPC(npcIndex);
            gp.eventH.checkEvent();
            keyH.interactPressed = false;
        }
        // Player movement
        boolean moving =
                keyH.upPressed || keyH.downPressed ||
                keyH.leftPressed || keyH.rightPressed;
        if (moving) {
            if (keyH.upPressed == true) direction = "up";
            else if (keyH.downPressed == true) direction = "down";
            else if (keyH.leftPressed == true) direction = "left";
            else if (keyH.rightPressed == true) direction = "right";
            collisionOn = false;
            // Tile collision
            gp.cChecker.checkTile(this);
            // Object collision
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);
            // Enemy collision
            int enemyIndex = gp.cChecker.checkEntity(this, gp.enemies);
            contactEnemies(enemyIndex);
            // Interactive tile collision
            gp.cChecker.checkEntity(this, gp.iTile);
            // Move if no collision
            if (collisionOn == false) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }
            // Walking animation
            spriteCounter++;
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            // Standing animation
            standCounter++;
            if (standCounter > 20) {
                spriteNum = 1;
                standCounter = 0;
            }
        }
        //Projectiles
        if (gp.keyH.shotKeyPressed == true && projectile.alive == false && 
            shotCounter == 30 && projectile.haveResource(this) == true) {
            //Set default coordinates, direction, and user
            projectile.set(worldX, worldY, direction, true, this);
            //Subtract the cost (ammo)
            projectile.subtractResource(this);
            //Add it to list
            gp.projectileList.add(projectile);
            //Reset timer
            shotCounter = 0;
        }
        // Invincibility
        if (invincible == true) {
            invincibleCounter++;
            if (invincibleCounter > 60) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
        //Shot counter
        if (shotCounter < 30) { shotCounter++; }
        //If values are greater than max, set it to max
        if (life > maxLife) { life = maxLife; }
        if (ammo > maxAmmo) { ammo = maxAmmo; }
        if (money > maxMoney) { money = maxMoney; }

        //Player life/death
        if (life <= 0) { 
            gp.gameState = gp.gameOverState; 
            gp.ui.commandNum = -1;
            gp.stopMusic();
            //gp.playSE(##);
        }
    }

    public void attacking() {
        spriteCounter++;

        if (spriteCounter <= 5 ) {
            spriteNum = 1;
        }
        if (spriteCounter > 5 && spriteCounter <= 25) {
            spriteNum = 2;
            //Save the current worldX, worldY, and solidArea
            int currentWorldX = worldX;
            int currentWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;
            //Adjust player's worldX and worldY for the attackArea
            switch (direction) {
                case "up": worldY -= attackArea.height; break;
                case "down": worldY += attackArea.height; break;
                case "left": worldX -= attackArea.width; break;
                case "right": worldX += attackArea.width; break;
            }
            //attackArea becomes solidArea
            solidArea.width = attackArea.width;
            solidArea.height = attackArea.height;
            //Check enemy collision with updated worldX, worldY, and solidArea
            int enemiesIndex = gp.cChecker.checkEntity(this, gp.enemies);
            damageEnemies(enemiesIndex, attack);
            //Interactive
            int iTileIndex = gp.cChecker.checkEntity(this, gp.iTile);
            damageInteractiveTile(iTileIndex);

            //After checking collision, restore original data
            worldX = currentWorldX;
            worldY = currentWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;
        }
        if (spriteCounter > 25) {
            spriteNum = 1;
            spriteCounter = 0;
            attacking = false;
        }
    }

    //Player picks up an object
    public void pickUpObject (int i) {
        if (i != 999) {
            //Pickup-only items
            if (gp.obj[gp.currentMap][i].type == type_pickupOnly) {
                gp.obj[gp.currentMap][i].use(this);
                gp.obj[gp.currentMap][i] = null;
            } else {
                //Inventory items
                String text = "";
                if (inventory.size() != maxInventorySize) {
                    inventory.add(gp.obj[gp.currentMap][i]);
                } else { text = "Inventory full!"; }
                gp.ui.addMessage(text);
                gp.obj[gp.currentMap][i] = null;
            }
        }
    }

    //Player interacts with npc (E key)
    public void interactNPC (int i) {
    if (i != 999) {
        gp.gameState = gp.dialogueState;
        gp.npc[gp.currentMap][i].speak();
    }
}


    //Player attacks/destroys (ENTER key)
    public void attackEnemy (int i) {
        if (i != 999) {
            if (gp.keyH.attackPressed == true) {
                attacking = true;
            }
        }
    }

    //Player is in contact with enemy
    public void contactEnemies (int i) {
        if (i != 999) {
            if (invincible == false && gp.enemies[gp.currentMap][i].dying == false) {
                int damage = gp.enemies[gp.currentMap][i].attack - defense;
                if (damage < 0) { damage = 0; }
                life -= damage;
                invincible = true;
            }
        }
    }

    public void damageEnemies (int i, int attack) {
        if (i != 999) {
            if (gp.enemies[gp.currentMap][i].invincible == false) {
                int damage = attack - gp.enemies[gp.currentMap][i].defense;
                if (damage < 0) { damage = 0; }
                gp.enemies[gp.currentMap][i].life -= damage;
                gp.ui.addMessage(damage + " damage!");
                gp.enemies[gp.currentMap][i].invincible = true;
                gp.enemies[gp.currentMap][i].damageReaction();
                if (gp.enemies[gp.currentMap][i].life <= 0) { 
                    gp.enemies[gp.currentMap][i].dying = true; 
                    gp.ui.addMessage("Killed the " + gp.enemies[gp.currentMap][i].name + "!");
                    gp.ui.addMessage("+" + gp.enemies[gp.currentMap][i].exp + " exp!");
                    exp += gp.enemies[gp.currentMap][i].exp;
                    checkLevelUp();
                } 
            }
        }
    }

    public void damageInteractiveTile(int i) {
        if (i != 999 && gp.iTile[gp.currentMap][i].destructible == true && 
            gp.iTile[gp.currentMap][i].isCorrectTool(this) == true && gp.iTile[gp.currentMap][i].invincible == false) {
            //gp.iTile[i].playSE();
            gp.iTile[gp.currentMap][i].life--;
            gp.iTile[gp.currentMap][i].invincible = true;
            generateParticle(gp.iTile[gp.currentMap][i], gp.iTile[gp.currentMap][i]); //Generate particle
            if (gp.iTile[gp.currentMap][i].life == 0) {
                gp.iTile[gp.currentMap][i] = gp.iTile[gp.currentMap][i].getDestroyedForm();
                collision = false;
            }
        }
    }

    public void checkLevelUp() {
        if (exp >= nextLevelExp) {
            level++;
            nextLevelExp = nextLevelExp * 2;
            maxLife += 2;
            skill++;
            attack = getAttack();
            defense = getDefense();
            gp.ui.addMessage("New Level: " + level + "!");
        }
    }

    public void selectItem() {
        int itemIndex = gp.ui.getItemIndexOnSlot();
        if (itemIndex < inventory.size()) {
            Entity selectedItem = inventory.get(itemIndex);
            if (selectedItem.type == type_sword || selectedItem.type == type_axe) {
                currentTool = selectedItem;
                attack = getAttack();
                getPlayerAttackImage();
            }
            if (selectedItem.type == type_shield) {
                currentShield = selectedItem;
                defense = getDefense();
            }
            if (selectedItem.type == type_consumable && keyH.consumePressed) {
                selectedItem.use(this);  // only pass the player (this)
                inventory.remove(itemIndex);
                keyH.consumePressed = false;
            }
        }
    }

    public void useItem(Entity item) {
        item.use(this); //Calls item's specific use method
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = down1;  // Default to avoid null
        int x = screenX;
        int y = screenY;

        switch(direction) {
            case "up":
                if (attacking == false) {
                    if (spriteNum == 1) { image = up1; }
                    else if (spriteNum == 2) { image = up2; }
                } else if (attacking == true) {
                    y = screenY - gp.tileSize;
                    if (spriteNum == 1) { image = attackUp1; }
                    else if (spriteNum == 2) { image = attackUp2; }
                }
                break;
            case "down":
                if (attacking == false) {
                    if (spriteNum == 1) { image = down1; }
                    else if (spriteNum == 2) { image = down2; }
                } else if (attacking == true) {
                    if (spriteNum == 1) { image = attackDown1; }
                    else if (spriteNum == 2) { image = attackDown2; }
                }
                break;
            case "left":
                if (attacking == false) {
                    if (spriteNum == 1) { image = left1; }
                    else if (spriteNum == 2) { image = left2; }
                } else if (attacking == true) {
                    x = screenX - gp.tileSize;
                    if (spriteNum == 1) { image = attackLeft1; }
                    else if (spriteNum == 2) { image = attackLeft2; }
                }
                break;
            case "right":
                if (attacking == false) {
                    if (spriteNum == 1) { image = right1; }
                    else if (spriteNum == 2) { image = right2; }
                } else if (attacking == true) {
                    if (spriteNum == 1) { image = attackRight1; }
                    else if (spriteNum == 2) { image = attackRight2; }
                }
                break;
        }

        if (screenX > worldX) { x = worldX; }
        if (screenY > worldY) { y = worldY; }
        int rightOffset = gp.screenWidth - screenX;
        if (rightOffset > (gp.worldWidth - worldX)) { x = gp.screenWidth - (gp.worldWidth - worldX); }
        int bottomOffset = gp.screenHeight - screenY;
        if (bottomOffset > (gp.worldHeight - worldY)) { y = gp.screenHeight - (gp.worldHeight - worldY); }
        
        if (invincible == true) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4F));
        }

        g2.drawImage(image, x, y, null);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
    }
}
