package main;

import java.awt.Graphics2D;
// import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
//import java.text.DecimalFormat;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Color;
import java.awt.BasicStroke;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import entity.Entity;
import object.OBJ_Ammo;
import object.OBJ_Heart;

// import object.OBJ_Key;

public class UI {
    UtilityTool uTool = new UtilityTool();
    GamePanel gp;
    public Font minecraft;
    Graphics2D g2;
    BufferedImage heart_full, heart_half, heart_blank, ammo_full, ammo_blank;
    public boolean messageOn = false;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();
    public boolean gameFinished = false;
    public String currentDialogue = "";
    public int commandNum = 0;
    public int titleScreenState = 0; // State 0 is first screen, state 1 is second screen (in title)
    public int slotCol = 0;
    public int slotRow = 0;
    int subState = 0;
    int counter = 0;

    //Stopwatch
    // double playTime;
    // DecimalFormat dFormat = new DecimalFormat("0.00");

    public UI(GamePanel gp) {
        this.gp = gp;
        
        try {
            InputStream is = getClass().getResourceAsStream("/res/font/Minecraft.ttf");    
            minecraft = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create HUD object
        //Heart
        Entity heart = new OBJ_Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;
        //Ammo
        Entity ammo = new OBJ_Ammo(gp);
        ammo_full = ammo.image;
        ammo_blank = ammo.image2;
    }

    public void addMessage(String text) {
        message.add(text);
        messageCounter.add(0);
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        g2.setFont(minecraft);
        g2.setColor(Color.white);
        // g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //Title state
        if (gp.gameState == gp.titleState) { drawTitleScreen();}
        //Play state
        if (gp.gameState == gp.playState) { 
            drawPlayerHealth(); 
            drawPlayerAmmo(); 
            drawMessage(); 
            drawInventory(); 
        }
        //Pause state
        else if (gp.gameState == gp.pauseState) { 
            drawPauseScreen(); 
            drawPlayerHealth(); 
            drawPlayerAmmo(); 
        }
        //Dialogue state
        if (gp.gameState == gp.dialogueState) { 
            drawPlayerHealth(); 
            drawPlayerAmmo(); 
            drawDialogueScreen();
        }
        //Character stats state
        if (gp.gameState == gp.characterStatsState) { drawCharacterStatsScreen(); }
        //Game over state
        if (gp.gameState == gp.gameOverState) { drawGameOverScreen(); }
        //Settings state
        if (gp.gameState == gp.settingsState) { drawSettingsScreen(); }
        //Transition state
        if (gp.gameState == gp.transitionState) { drawTransition(); }
    }

    //Player health
    public void drawPlayerHealth() {
        //Size
        int heartSize = gp.tileSize / 2;   // smaller hearts
        int spacing = 2;
        //Change position depending on inventory height
        int inventoryHeight = gp.tileSize + 20;      
        int totalHearts = gp.player.maxLife / 2;
        int totalWidth = totalHearts * heartSize + (totalHearts - 1) * spacing;
        //Center hearts horizontally
        int xStart = gp.screenWidth / 2 - totalWidth / 2;
        //Position hearts just above where inventory will be
        int y = gp.screenHeight - inventoryHeight - heartSize - 25;
        int x = xStart;
        int i = 0;
        //Draw current life
        while (i < totalHearts) {
            g2.drawImage(heart_blank, x, y, heartSize, heartSize, null);
            i++;
            x += heartSize + spacing;
        }
        // Reset
        x = xStart;
        i = 0;
        //Draw current life
        while (i < gp.player.life) {
            g2.drawImage(heart_half, x, y, heartSize, heartSize, null);
            i++;
            if (i < gp.player.life) { g2.drawImage(heart_full, x, y, heartSize, heartSize, null); }
            i++;
            x += heartSize + spacing;
        }
    }

    //Player ammo
    public void drawPlayerAmmo() {
        //Size
        int ammoSize = gp.tileSize / 2;   // smaller ammo
        int spacing = 2;
        //Change position depending on inventory height
        int inventoryHeight = gp.tileSize + 20;      
        int totalAmmo = gp.player.maxLife / 2;
        int totalWidth = totalAmmo * ammoSize + (totalAmmo - 1) * spacing;
        //Center ammo horizontally
        int xStart = gp.screenWidth / 2 - totalWidth / 2;
        //Position ammo just above where ammo will be
        int y = gp.screenHeight - (inventoryHeight + 25) - ammoSize - 30;
        int x = xStart;
        int i = 0;
        //Draw current ammo
        while (i < totalAmmo) {
            g2.drawImage(ammo_blank, x, y, ammoSize, ammoSize, null);
            i++;
            x += ammoSize + spacing;
        }
        // Reset
        x = xStart;
        i = 0;
        //Draw current ammo
        while (i < gp.player.maxAmmo) {
            if (i < gp.player.ammo) { g2.drawImage(ammo_full, x, y, ammoSize, ammoSize, null); }
            i++;
            x += ammoSize + spacing;
        }
    }

    //Inventory
    public void drawInventory() {
        //Inventory bar sizing
        int hotbarSlots = 9;
        int slotSize = gp.tileSize;
        int slotPadding = 6;
        int frameWidth = (slotSize + slotPadding) * hotbarSlots + slotPadding;
        int frameHeight = slotSize + slotPadding * 2;
        int frameX = (gp.screenWidth - frameWidth) / 2;
        int frameY = gp.screenHeight - frameHeight - 30;
        //inventory bar background
        Color bg = new Color(0, 0, 0, 180);
        g2.setColor(bg);
        g2.fillRoundRect(frameX, frameY, frameWidth, frameHeight, 25, 25);
        //Slot positions
        int slotXstart = frameX + slotPadding;
        int slotYstart = frameY + slotPadding;
        int slotX = slotXstart;
        int slotY = slotYstart;
        //Draw items
        for (int i = 0; i < hotbarSlots; i++) {
            if (i < gp.player.inventory.size()) {
                if (gp.player.inventory.get(i) == gp.player.currentTool ||
                    gp.player.inventory.get(i) == gp.player.currentShield) {
                    g2.setColor(new Color(240, 190, 90));
                    g2.fillRoundRect(slotX, slotY, slotSize, slotSize, 10, 10);
                }
                g2.drawImage( gp.player.inventory.get(i).down1, slotX + 4, slotY + 4, slotSize - 8, slotSize - 8, null );
            }
            slotX += slotSize + slotPadding;
        }
        //Cursor
        int cursorX = slotXstart + (slotSize + slotPadding) * slotCol;
        int cursorY = slotYstart;
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect( cursorX, cursorY, slotSize, slotSize, 10, 10 );
    }

    //Message
    public void drawMessage() {
        int messageX = gp.tileSize;
        int messageY = gp.tileSize * 4;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));

        for (int i = 0; i < message.size(); i++) {
            if (message.get(i) != null) {
                g2.setColor(Color.black);
                g2.drawString(message.get(i), messageX + 2, messageY + 2);
                g2.setColor(Color.white);
                g2.drawString(message.get(i), messageX, messageY);

                messageCounter.set(i, messageCounter.get(i) + 1);
                messageY += 40;

                if (messageCounter.get(i) > 180) {
                    message.remove(i);
                    messageCounter.remove(i);
                }
            }
        }
    }

    //Title screen
    public void drawTitleScreen() {
        if (titleScreenState == 0) {
            g2.setColor(new Color(70, 120, 80));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            //Title name
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            String text = "Prison Game";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 3;
            //Shadow
            g2.setColor(new Color(0, 0, 0, 150));
            g2.drawString(text, x + 5, y + 5);
            //Main color
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
            //Character image
            x = gp.screenWidth / 2 - (gp.tileSize * 2) / 2;
            y += gp.tileSize * 2;
            g2.drawImage(gp.player.down1, x, y, gp.tileSize * 2, gp.tileSize * 2, null);
            //Menu
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
            text = "New Game";
            x = getXforCenteredText(text);
            y += gp.tileSize * 4;
            g2.drawString(text, x, y);
            if (commandNum == 0) { g2.drawString(">", x - gp.tileSize, y); }
            text = "Load Game";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 1) { g2.drawString(">", x - gp.tileSize, y); }
            text = "Quit";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 2) { g2.drawString(">", x - gp.tileSize, y); }
        } else if (titleScreenState == 1) {
            //Character selection screen
            g2.setColor(new Color(70, 120, 80));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
            
            String text = "Select your character";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 3;
            g2.drawString(text, x, y);

            text = "Johnny";
            x = getXforCenteredText(text);
            y += gp.tileSize * 3;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Billy";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Steve";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "Back";
            x = getXforCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (commandNum == 3) {
                g2.drawString(">", x - gp.tileSize, y);
            }
        }
    }

    //Dialogue
    public void drawDialogueScreen() {
        //Window
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 4;

        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));

        x += gp.tileSize;
        y += gp.tileSize;

        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y+= 40;
        }
    }

    public void drawCharacterStatsScreen() {
        //Create frame
        final int frameX = gp.tileSize;
        final int frameY = gp.tileSize;
        final int frameWidth = gp.tileSize * 6;
        final int frameHeight = gp.tileSize * 10;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);
        //Text
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));
        //Variables
        int textX = frameX + (gp.tileSize / 2);
        int textY = frameY + gp.tileSize;
        final int lineHeight = 35;
        //Names
        g2.drawString("Level", textX, textY);
        textY += lineHeight;
        g2.drawString("Strength", textX, textY);
        textY += lineHeight;
        g2.drawString("Skill", textX, textY);
        textY += lineHeight;
        g2.drawString("Attack", textX, textY);
        textY += lineHeight;
        g2.drawString("Defense", textX, textY);
        textY += lineHeight;
        g2.drawString("Experience", textX, textY);
        textY += lineHeight;
        g2.drawString("Next Level", textX, textY);
        textY += lineHeight;
        g2.drawString("Coins", textX, textY);
        //Values
        int tailX = frameX + frameWidth - gp.tileSize;
        textY = frameY + gp.tileSize;
        String value;
        //Level
        value = String.valueOf(gp.player.level);
        textX = getXforRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;
        //Strength
        value = String.valueOf(gp.player.strength);
        textX = getXforRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;
        //Skill
        value = String.valueOf(gp.player.skill);
        textX = getXforRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;
        //Attack
        value = String.valueOf(gp.player.attack);
        textX = getXforRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;
        //Defense
        value = String.valueOf(gp.player.defense);
        textX = getXforRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;
        //Exp
        value = String.valueOf(gp.player.exp);
        textX = getXforRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;
        //Next Level
        value = String.valueOf(gp.player.nextLevelExp);
        textX = getXforRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;
        //Money
        value = String.valueOf(gp.player.money);
        textX = getXforRightText(value, tailX);
        g2.drawString(value, textX, textY);
        textY += lineHeight;
    }

    public void drawGameOverScreen() {
        g2.setColor(new Color(130, 33, 29, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int x;
        int y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 100F));

        //Shadow
        text = "Game Over";
        g2.setColor(Color.black);
        x = getXforCenteredText(text);
        y = gp.tileSize * 4;
        g2.drawString(text, x + 5, y + 5);

        //Main
        text = "Game Over";
        g2.setColor(Color.white);
        x = getXforCenteredText(text);
        y = gp.tileSize * 4;
        g2.drawString(text, x, y);

        //Respawn
        g2.setFont(g2.getFont().deriveFont(50F));
        text = "Respawn";
        x = getXforCenteredText(text);
        y += gp.tileSize * 4;
        g2.drawString(text, x, y);
        if (commandNum == 0) { g2.drawString(">", x - 40, y); }

        //Quit to title screen
        g2.setFont(g2.getFont().deriveFont(50F));
        text = "Quit";
        x = getXforCenteredText(text);
        y += 55;
        g2.drawString(text, x, y);
        if (commandNum == 1) { g2.drawString(">", x - 40, y); }
    }

    public void drawSettingsScreen() {
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));

        //Sub window
        int frameX = gp.tileSize * 6;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 8;
        int frameHeight = gp.tileSize * 10;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        switch (subState) {
            case 0: settings_top(frameX, frameY); break;
            case 1: gp.setFullScreen(); break;
            case 2: settings_control(frameX, frameY); break;
            case 3: settings_endGame(frameX, frameY); break;
        }

        gp.keyH.attackPressed = false;
    }

    public void drawTransition() {
        counter++;
        g2.setColor(new Color(0, 0, 0, (counter * 5)));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if (counter == 51) {
            for (var i = 0; i < 51; i++) {
                counter--;
                if (counter <= 0) { counter = 0; }
            }
            gp.gameState = gp.playState;
            gp.currentMap = gp.eventH.tempMap;
            gp.player.worldX = gp.tileSize * gp.eventH.tempCol;
            gp.player.worldY = gp.tileSize * gp.eventH.tempRow;
            gp.eventH.previousEventX = gp.player.worldX;
            gp.eventH.previousEventY = gp.player.worldY;
        }
    }

    public void settings_top(int frameX, int frameY) {
        int textX;
        int textY;

        //Title
        String text = "Options:";
        textX = getXforCenteredText(text);
        textY = frameY + gp.tileSize;
        g2.drawString(text, textX, textY);

        //Fullscreen on/off
        textX = frameX + gp.tileSize;
        textY += gp.tileSize * 2;
        g2.drawString("Full Screen", textX, textY);
        if (commandNum == 0) { 
            g2.drawString(">", textX - 25, textY); 
            if (gp.keyH.attackPressed == true) {
                if (gp.fullScreenOn == false) { gp.fullScreenOn = true; }
                else if (gp.fullScreenOn == true) { gp.fullScreenOn = false; }
                subState = 1;
            }
        }

        //Music
        textY += gp.tileSize;
        g2.drawString("Music", textX, textY);
        if (commandNum == 1) { g2.drawString(">", textX - 25, textY); }

        //Sound effects
        textY += gp.tileSize;
        g2.drawString("SFX", textX, textY);
        if (commandNum == 2) { g2.drawString(">", textX - 25, textY); }

        //Controls
        textY += gp.tileSize;
        g2.drawString("Controls", textX, textY);
        if (commandNum == 3) { 
            g2.drawString(">", textX - 25, textY); 
            if (gp.keyH.attackPressed == true) {
                subState = 2;
                commandNum = 0;
            }
        }

        //End game
        textY += gp.tileSize;
        g2.drawString("Quit Game", textX, textY);
        if (commandNum == 4) { 
            g2.drawString(">", textX - 25, textY); 
            if (gp.keyH.attackPressed == true) {
                subState = 3;
                commandNum = 0;
            }
        }

        //Back to game
        textY += gp.tileSize * 2;
        g2.drawString("Back", textX, textY);
        if (commandNum == 5) { 
            g2.drawString(">", textX - 25, textY); 
            if (gp.keyH.attackPressed == true) { 
                gp.gameState = gp.playState; 
                commandNum = 0;
            }
        }

        //Sliders and checkboxes
            //Fullscreen checkbox
            textX = frameX + (int)(gp.tileSize * 4.5);
            textY = frameY + (gp.tileSize * 2) + (gp.tileSize / 2);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(textX, textY, gp.tileSize/2, gp.tileSize/2);
            if (gp.fullScreenOn == true) { g2.fillRect(textX, textY, gp.tileSize/2, gp.tileSize/2); }

            //Music slider
            textY+= gp.tileSize;
            g2.drawRect(textX, textY, 120, gp.tileSize/2); 
            int volumeWidth = 24 * gp.music.volumeScale;
            g2.fillRect(textX, textY, volumeWidth, gp.tileSize/2);

            //SFX slider
            textY+= gp.tileSize;
            g2.drawRect(textX, textY, 120, gp.tileSize/2);
            volumeWidth = 24 * gp.soundEffect.volumeScale;
            g2.fillRect(textX, textY, volumeWidth, gp.tileSize/2);

            gp.config.saveConfig();
    }

    public void settings_control(int frameX, int frameY) {
        int textX;
        int textY;

        //Title
        String text = "Controls";
        textX = getXforCenteredText(text);
        textY = frameY + gp.tileSize;
        g2.drawString(text, textX, textY);

        textX = frameX + gp.tileSize;
        textY += gp.tileSize;
        g2.drawString("Move", textX, textY); textY+=gp.tileSize;
        g2.drawString("Confirm/Attack", textX, textY); textY+=gp.tileSize;
        g2.drawString("Shoot", textX, textY); textY+=gp.tileSize;
        g2.drawString("Character Stats", textX, textY); textY+=gp.tileSize;
        g2.drawString("Inventory Slots", textX, textY); textY+=gp.tileSize;
        g2.drawString("Options", textX, textY); textY+=gp.tileSize;

        textX = frameX + gp.tileSize * 6;
        textY = frameY + gp.tileSize * 2;
        g2.drawString("WASD", textX, textY); textY+=gp.tileSize;
        g2.drawString("Enter", textX, textY); textY+=gp.tileSize;
        g2.drawString("Shift", textX, textY); textY+=gp.tileSize;
        g2.drawString("C", textX, textY); textY+=gp.tileSize;
        g2.drawString("1-9", textX, textY); textY+=gp.tileSize;
        g2.drawString("ESC", textX, textY); textY+=gp.tileSize;

        //Back
        textX = frameX + gp.tileSize;
        textY = frameY + gp.tileSize * 9;
        g2.drawString("Back", textX, textY);
        if (commandNum == 0) { 
            g2.drawString(">", textX - 25, textY); 
            if (gp.keyH.attackPressed == true) { 
                subState = 0; 
                commandNum = 3;
            }
        }
    }

    public void settings_endGame(int frameX, int frameY) {
        int textX = frameX + gp.tileSize;
        int textY = frameY + (gp.tileSize * 3); 

        currentDialogue = "Quit game and \nreturn to title \nscreen?";

        for (String line: currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40;
        }
        
        //Yes
        String text = "Yes";
        textX = getXforCenteredText(text);
        textY += gp.tileSize * 3;
        g2.drawString(text, textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.attackPressed == true) {
                subState = 0;
                gp.gameState = gp.titleState;
            }
        }

        //No
        text = "No";
        textX = getXforCenteredText(text);
        textY += gp.tileSize;
        g2.drawString(text, textX, textY);
        if (commandNum == 1) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.attackPressed == true) {
                subState = 0;
                commandNum = 4;
            }
        }
    }

    public int getItemIndexOnSlot() {
        int itemIndex = slotCol + (slotRow * 5);
        return itemIndex;
    }

    public void drawSubWindow(int x, int y, int width, int height) {
        //Set color
        Color c = new Color (0, 0,0, 210);
        g2.setColor(c);
        //Draw rectangle
        g2.fillRoundRect(x, y, width, height, 35, 35);
        //Outline
        c = new Color (255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    //Pause
    public void drawPauseScreen() {
        
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,80F));
        String text = "PAUSED";
    
        int x = getXforCenteredText(text);
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x = gp.screenWidth/2 - length/2;

        int y = gp.screenHeight/2;

        g2.drawString(text, x, y);
    }

    //Center Text
    public int getXforCenteredText (String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth/2 - length/2;
        return x;
    }

    //Right Text
    public int getXforRightText (String text, int tailX) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = tailX - length;
        return x;
    }
}