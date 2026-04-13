package main;

import java.awt.event.KeyEvent;
import entity.Entity;

public class KeyHandler implements java.awt.event.KeyListener {

    GamePanel gp;
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }
    
    public boolean upPressed, downPressed, leftPressed, rightPressed, shotKeyPressed;
    public boolean interactPressed;
    public boolean attackPressed;
    public boolean consumePressed;


    //DEBUG
    boolean checkDrawTime = false;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        //Game state
        //Title state
        if (gp.gameState == gp.titleState) { titleState(code); } 
        //Play state
        else if (gp.gameState == gp.playState) { playState(code); }
        //Pause state
        else if (gp.gameState == gp.pauseState) { pauseState(code); }
        //Dialogue state
        else if (gp.gameState == gp.dialogueState) { dialogueState(code); }
        //Character stats (C) exit
        else if (gp.gameState == gp.characterStatsState) { characterStatsState(code); }
        //Game over state
        else if (gp.gameState == gp.gameOverState) { gameOverState(code); }
        //Settings state
        else if (gp.gameState == gp.settingsState) { settingsState(code); }

    }

    public void titleState(int code) {
        if (gp.ui.titleScreenState == 0) {
                if (code == KeyEvent.VK_W) {
                    gp.ui.commandNum--;
                    if (gp.ui.commandNum < 0) { gp.ui.commandNum = 2; } 
                }
                if (code == KeyEvent.VK_S) {
                    gp.ui.commandNum++;
                    if (gp.ui.commandNum > 2) { gp.ui.commandNum = 0; }                 
                }
                if (code == KeyEvent.VK_E || code == KeyEvent.VK_ENTER) {
                    switch (gp.ui.commandNum) {
                        case 0: gp.ui.titleScreenState = 1; break;
                        case 1: break;
                        case 2: System.exit(0); break;
                    }
                }
            } else if (gp.ui.titleScreenState == 1) {
                if (code == KeyEvent.VK_W) {
                    gp.ui.commandNum--;
                    if (gp.ui.commandNum < 0) { gp.ui.commandNum = 3; } 
                }
                if (code == KeyEvent.VK_S) {
                    gp.ui.commandNum++;
                    if (gp.ui.commandNum > 3) { gp.ui.commandNum = 0; }                 
                }
                if (code == KeyEvent.VK_E) {
                    switch (gp.ui.commandNum) {
                        case 0: gp.gameState = gp.playState; break;
                        case 1: gp.gameState = gp.playState; break;
                        case 2: gp.gameState = gp.playState; break;
                        case 3: gp.ui.titleScreenState = 0; break;
                }
            }
        }
    }

    public void playState(int code) {
        // Movement
        if (code == KeyEvent.VK_W) { upPressed = true; }
        if (code == KeyEvent.VK_S) { downPressed = true; }
        if (code == KeyEvent.VK_A) { leftPressed = true; }
        if (code == KeyEvent.VK_D) { rightPressed = true; }
        // Pause
        if (code == KeyEvent.VK_P) { gp.gameState = gp.pauseState; }
        // Character stats
        if (code == KeyEvent.VK_C) { gp.gameState = gp.characterStatsState; }
        // Character stats
        if (code == KeyEvent.VK_ESCAPE) { gp.gameState = gp.settingsState; }
        //Shot key
        if (code == KeyEvent.VK_SHIFT) { shotKeyPressed = true; }
        // Interact
        if (code == KeyEvent.VK_E) { interactPressed = true; }
        // Inventory slot selection (1-9)
        if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9) {
            gp.ui.slotCol = code - KeyEvent.VK_1;  // select hotbar slot
            gp.player.selectItem();
        }
        // Enter key: either consume or attack
        if (code == KeyEvent.VK_ENTER) {
            boolean didConsume = false;
            // Check selected inventory slot
            if (gp.ui.slotCol >= 0 && gp.ui.slotCol < gp.player.inventory.size()) {
                Entity selectedItem = gp.player.inventory.get(gp.ui.slotCol);
                if (selectedItem != null && selectedItem.type == Entity.type_consumable) {
                    gp.player.useItem(selectedItem);        // Heal/effect
                    gp.player.inventory.remove(gp.ui.slotCol); // Remove item
                    consumePressed = true;
                    didConsume = true;
                }
            }
            if (!didConsume) {
                attackPressed = true; // normal attack if no consumable used
            }
        }
        //DEBUG
        if (code == KeyEvent.VK_T) {
             if (checkDrawTime == false) {
                 checkDrawTime = true;
             } else if (checkDrawTime == true) {
                 checkDrawTime = false;
             }
        }
    }

    
    public void pauseState(int code) { if (code == KeyEvent.VK_P) { gp.gameState = gp.playState; } }

    public void dialogueState(int code) { if (code == KeyEvent.VK_E) { gp.gameState = gp.playState; interactPressed = false; } }
    
    public void characterStatsState(int code) { if (code == KeyEvent.VK_C) { gp.gameState = gp.playState; } }

    public void settingsState(int code) {
        if (code == KeyEvent.VK_ESCAPE) { gp.gameState = gp.playState; }
        if (code == KeyEvent.VK_ENTER) { attackPressed = true; }
        int maxCommandNum = 0;
        switch(gp.ui.subState) {
            case 0: maxCommandNum = 5; break;
            case 3: maxCommandNum = 1; break;
        }
        if (code == KeyEvent.VK_W) { 
            gp.ui.commandNum--; 
            if (gp.ui.commandNum < 0) {
                gp.ui.commandNum = maxCommandNum;
            }
        }
        if (code == KeyEvent.VK_S) {
            gp.ui.commandNum++;
            if (gp.ui.commandNum > maxCommandNum) {
                gp.ui.commandNum = 0;
            }
        }
        if (code == KeyEvent.VK_A) { 
            if (gp.ui.subState == 0) {
                if (gp.ui.commandNum == 1 && gp.music.volumeScale > 0) {
                    gp.music.volumeScale--;
                    gp.music.checkVolume();
                }
                if (gp.ui.commandNum == 2 && gp.soundEffect.volumeScale > 0) {
                    gp.soundEffect.volumeScale--;
                }
            }
        }
        if (code == KeyEvent.VK_D) {
            if (gp.ui.subState == 0) {
                if (gp.ui.commandNum == 1 && gp.music.volumeScale < 5) {
                    gp.music.volumeScale++;
                    gp.music.checkVolume();
                }
                if (gp.ui.commandNum == 2 && gp.soundEffect.volumeScale < 5) {
                    gp.soundEffect.volumeScale++;
                }
            }
        }
    }
 
    public void gameOverState(int code) {
        if (code == KeyEvent.VK_W) { 
            gp.ui.commandNum--; 
            if (gp.ui.commandNum < 0) { gp.ui.commandNum = 1; }
            //gp.playSE(##)
        }
        if (code == KeyEvent.VK_S) { 
            gp.ui.commandNum++; 
            if (gp.ui.commandNum > 1) { gp.ui.commandNum = 0; }
            //gp.playSE(##)
        }
        if (code == KeyEvent.VK_ENTER) {
            if (gp.ui.commandNum == 0) { 
                gp.gameState = gp.playState; 
                gp.retry();
                gp.playMusic(0);
            }
            if (gp.ui.commandNum == 1) { 
                gp.gameState = gp.titleState; 
                gp.restart();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) { upPressed = false; }
        if (code == KeyEvent.VK_S) { downPressed = false; }
        if (code == KeyEvent.VK_A) { leftPressed = false; }
        if (code == KeyEvent.VK_D) { rightPressed = false;}        

        //Shot key
        if (code == KeyEvent.VK_SHIFT) { shotKeyPressed = false; }

    }
}
