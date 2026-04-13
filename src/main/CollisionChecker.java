package main;

import entity.Entity;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int maxCol = gp.tileM.mapCol[gp.currentMap];
        int maxRow = gp.tileM.mapRow[gp.currentMap];

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                entityTopRow = Math.max(0, Math.min(entityTopRow, maxRow - 1));
                entityLeftCol = Math.max(0, Math.min(entityLeftCol, maxCol - 1));
                entityRightCol = Math.max(0, Math.min(entityRightCol, maxCol - 1));
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) { entity.collisionOn = true; } break;

            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                entityBottomRow = Math.max(0, Math.min(entityBottomRow, maxRow - 1));
                entityLeftCol = Math.max(0, Math.min(entityLeftCol, maxCol - 1));
                entityRightCol = Math.max(0, Math.min(entityRightCol, maxCol - 1));
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) { entity.collisionOn = true; } break;

            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                entityLeftCol = Math.max(0, Math.min(entityLeftCol, maxCol - 1));
                entityTopRow = Math.max(0, Math.min(entityTopRow, maxRow - 1));
                entityBottomRow = Math.max(0, Math.min(entityBottomRow, maxRow - 1));
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) { entity.collisionOn = true; } break;

            case "right":
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                entityRightCol = Math.max(0, Math.min(entityRightCol, maxCol - 1));
                entityTopRow = Math.max(0, Math.min(entityTopRow, maxRow - 1));
                entityBottomRow = Math.max(0, Math.min(entityBottomRow, maxRow - 1));
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) { entity.collisionOn = true; } break;
        }
    }

    // Objects collision
    public int checkObject(Entity entity, boolean player) {
        int index = 999;

        for (int i = 0; i < gp.obj[1].length; i++) {
            if (gp.obj[gp.currentMap][i] != null) {
                // Save original positions
                int entitySolidX = entity.solidArea.x;
                int entitySolidY = entity.solidArea.y;
                int objSolidX = gp.obj[gp.currentMap][i].solidArea.x;
                int objSolidY = gp.obj[gp.currentMap][i].solidArea.y;

                // Update solid area positions to world coordinates
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                gp.obj[gp.currentMap][i].solidArea.x = gp.obj[gp.currentMap][i].worldX + gp.obj[gp.currentMap][i].solidArea.x;
                gp.obj[gp.currentMap][i].solidArea.y = gp.obj[gp.currentMap][i].worldY + gp.obj[gp.currentMap][i].solidArea.y;

                // Move entity solid area in direction
                switch (entity.direction) {
                    case "up": entity.solidArea.y -= entity.speed; break;
                    case "down": entity.solidArea.y += entity.speed; break;
                    case "left": entity.solidArea.x -= entity.speed; break;
                    case "right": entity.solidArea.x += entity.speed; break;
                }

                // Check intersection
                if (entity.solidArea.intersects(gp.obj[gp.currentMap][i].solidArea)) {
                    if (gp.obj[gp.currentMap][i].collision) entity.collisionOn = true;
                    if (player) index = i;
                }

                // Reset solid areas
                entity.solidArea.x = entitySolidX;
                entity.solidArea.y = entitySolidY;
                gp.obj[gp.currentMap][i].solidArea.x = objSolidX;
                gp.obj[gp.currentMap][i].solidArea.y = objSolidY;
            }
        }

        return index;
    }

    // NPC or enemy collision
    public int checkEntity(Entity entity, Entity[][] target) {
        int index = 999;

        for (int i = 0; i < target[1].length; i++) {
            if (target[gp.currentMap][i] != null) {
                int entitySolidX = entity.solidArea.x;
                int entitySolidY = entity.solidArea.y;
                int targetSolidX = target[gp.currentMap][i].solidArea.x;
                int targetSolidY = target[gp.currentMap][i].solidArea.y;

                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                target[gp.currentMap][i].solidArea.x = target[gp.currentMap][i].worldX + target[gp.currentMap][i].solidArea.x;
                target[gp.currentMap][i].solidArea.y = target[gp.currentMap][i].worldY + target[gp.currentMap][i].solidArea.y;

                switch (entity.direction) {
                    case "up": entity.solidArea.y -= entity.speed; break;
                    case "down": entity.solidArea.y += entity.speed; break;
                    case "left": entity.solidArea.x -= entity.speed; break;
                    case "right": entity.solidArea.x += entity.speed; break;
                }

                if (entity.solidArea.intersects(target[gp.currentMap][i].solidArea) && target[gp.currentMap][i] != entity) {
                    entity.collisionOn = true;
                    index = i;
                }

                entity.solidArea.x = entitySolidX;
                entity.solidArea.y = entitySolidY;
                target[gp.currentMap][i].solidArea.x = targetSolidX;
                target[gp.currentMap][i].solidArea.y = targetSolidY;
            }
        }

        return index;
    }

    // Check player collision
    public boolean checkPlayer(Entity entity) {
        boolean contactPlayer = false;

        int entitySolidX = entity.solidArea.x;
        int entitySolidY = entity.solidArea.y;
        int playerSolidX = gp.player.solidArea.x;
        int playerSolidY = gp.player.solidArea.y;

        entity.solidArea.x = entity.worldX + entity.solidArea.x;
        entity.solidArea.y = entity.worldY + entity.solidArea.y;

        gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

        switch (entity.direction) {
            case "up": entity.solidArea.y -= entity.speed; break;
            case "down": entity.solidArea.y += entity.speed; break;
            case "left": entity.solidArea.x -= entity.speed; break;
            case "right": entity.solidArea.x += entity.speed; break;
        }

        if (entity.solidArea.intersects(gp.player.solidArea)) {
            entity.collisionOn = true;
            contactPlayer = true;
        }

        entity.solidArea.x = entitySolidX;
        entity.solidArea.y = entitySolidY;
        gp.player.solidArea.x = playerSolidX;
        gp.player.solidArea.y = playerSolidY;

        return contactPlayer;
    }
}
