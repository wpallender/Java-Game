package main;

public class EventHandler {

    GamePanel gp;
    EventRect eventRect[][][];
    // public boolean sameTileCollision = false;

    //Prevent repeated event
    int previousEventX, previousEventY;
    boolean canTouchEvent = true;
    int tempMap, tempCol, tempRow;

    public EventHandler(GamePanel gp) {
        this.gp = gp;

        eventRect = new EventRect[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];

        int map = 0;
        int col = 0;
        int row = 0;
        while (map < gp.maxMap && col < gp.maxWorldCol && row < gp.maxWorldRow) {
            eventRect[map][col][row] = new EventRect();
            eventRect[map][col][row].x = 23;
            eventRect[map][col][row].y = 23;
            eventRect[map][col][row].width = 2;
            eventRect[map][col][row].height = 2;
            eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
            eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;

            col++;
            if (col == gp.maxWorldCol) {
                col = 0;
                row++;
                if (row == gp.maxWorldRow) {
                    row = 0;
                    map++;
                }
            }
        }
    }

    public void checkEvent() {
        //Check if the player is more than one tile away from last event
        int xDistance = Math.abs(gp.player.worldX - previousEventX);
        int yDistance = Math.abs(gp.player.worldY - previousEventY);
        int distance = Math.max(xDistance, yDistance);
        if (distance > gp.tileSize) {
            canTouchEvent = true;
        }

        if (canTouchEvent == true) {
            if (hit(0, 26, 15, "right") == true) { damagePit(0, 26, 15, gp.dialogueState); }
            else if (hit(0, 23, 12, "up") == true) { healingPool(0, 23, 12, gp.dialogueState); }
            else if (hit(0, 10, 45, "any") == true) { teleport(1, 12, 12); }
            else if (hit(1, 12, 12, "any") == true) { teleport(0, 10, 45); }
        }
    }

    public boolean hit(int map, int col, int row, String requiredDirection) {
        boolean hit = false;

        if (map == gp.currentMap) {
            //Finding player's hitbox position
            gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
            gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
            eventRect[map][col][row].x = (col * gp.tileSize) + eventRect[map][col][row].x;
            eventRect[map][col][row].y = (row * gp.tileSize) + eventRect[map][col][row].y;
            
            //If player intersects danger
            if (gp.player.solidArea.intersects(eventRect[map][col][row]) && (eventRect[map][col][row].eventDone == false)) {
                if (gp.player.direction.contentEquals(requiredDirection) || requiredDirection.contentEquals("any")) {
                    hit = true;
                    previousEventX = gp.player.worldX;
                    previousEventY = gp.player.worldY;
                }
            }

            //Reset values
            gp.player.solidArea.x = gp.player.solidAreaDefaultX;
            gp.player.solidArea.y = gp.player.solidAreaDefaultY;
            eventRect[map][col][row].x = eventRect[map][col][row].eventRectDefaultX;
            eventRect[map][col][row].y = eventRect[map][col][row].eventRectDefaultY;
        }
        return hit;
    }

    public void damagePit(int map, int col, int row, int gameState) {
        gp.gameState = gameState;
        gp.ui.currentDialogue =  "You fell into a pit!";
        gp.player.life -= 1;
        eventRect[map][col][row].eventDone = true;
        canTouchEvent = false;
    }

    public void healingPool(int map, int col, int row, int gameState) {
        if (gp.keyH.interactPressed == true) {
            gp.gameState = gameState;
            gp.ui.currentDialogue = "You drink to heal";
            gp.player.life = gp.player.maxLife;
        }
    }

    public void teleport(int map, int col, int row) {
        gp.gameState = gp.transitionState;
        tempMap = map;
        tempCol = col;
        tempRow = row;
        canTouchEvent = false;
        //gp.playSE(##)
    }
}
