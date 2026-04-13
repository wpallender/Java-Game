package main;

import enemies.ENM_GreenSlime;
import entity.NPC_OldMan;
import object.OBJ_Ammo;
import object.OBJ_Axe;
import object.OBJ_Coin_Bronze;
import object.OBJ_Key;
import object.OBJ_Potion;

public class AssetSetter {
    GamePanel gp;

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    public void setObject() {
        int i = 0;
        int mapNum = 0;

        gp.obj[mapNum][i] = new OBJ_Ammo(gp);
        gp.obj[mapNum][i].worldX = gp.tileSize * 21;
        gp.obj[mapNum][i].worldY = gp.tileSize * 22;
        i++;
        gp.obj[mapNum][i] = new OBJ_Ammo(gp);
        gp.obj[mapNum][i].worldX = gp.tileSize * 20;
        gp.obj[mapNum][i].worldY = gp.tileSize * 39;
        i++;
        gp.obj[mapNum][i] = new OBJ_Coin_Bronze(gp);
        gp.obj[mapNum][i].worldX = gp.tileSize * 22;
        gp.obj[mapNum][i].worldY = gp.tileSize * 21;
        i++;
        gp.obj[mapNum][i] = new OBJ_Key(gp);
        gp.obj[mapNum][i].worldX = gp.tileSize * 22;
        gp.obj[mapNum][i].worldY = gp.tileSize * 22;
        i++;
        gp.obj[mapNum][i] = new OBJ_Axe(gp);
        gp.obj[mapNum][i].worldX = gp.tileSize * 33;
        gp.obj[mapNum][i].worldY = gp.tileSize * 21;
        i++;
        gp.obj[mapNum][i] = new OBJ_Potion(gp);
        gp.obj[mapNum][i].worldX = gp.tileSize * 31;
        gp.obj[mapNum][i].worldY = gp.tileSize * 21;
        i++;

        mapNum = 1;
        gp.obj[mapNum][i] = new OBJ_Axe(gp);
        gp.obj[mapNum][i].worldX = gp.tileSize * 16;
        gp.obj[mapNum][i].worldY = gp.tileSize * 14;
        i++;
    }

    public void setNPC() {
        int i = 0;
        int mapNum = 0;

        gp.npc[mapNum][i] = new NPC_OldMan(gp);
        gp.npc[mapNum][i].worldX = gp.tileSize * 21;
        gp.npc[mapNum][i].worldY = gp.tileSize * 21;
        i++;

    }

    public void setEnemies() {
        int i = 0;
        int mapNum = 0;

        gp.enemies[mapNum][i] = new ENM_GreenSlime(gp);
        gp.enemies[mapNum][i].worldX = gp.tileSize * 19;
        gp.enemies[mapNum][i].worldY = gp.tileSize * 39;
        i++;
        gp.enemies[mapNum][i] = new ENM_GreenSlime(gp);
        gp.enemies[mapNum][i].worldX = gp.tileSize * 23;
        gp.enemies[mapNum][i].worldY = gp.tileSize * 39;
        i++;
        gp.enemies[mapNum][i] = new ENM_GreenSlime(gp);
        gp.enemies[mapNum][i].worldX = gp.tileSize * 26;
        gp.enemies[mapNum][i].worldY = gp.tileSize * 39;
        i++;
        gp.enemies[mapNum][i] = new ENM_GreenSlime(gp);
        gp.enemies[mapNum][i].worldX = gp.tileSize * 19;
        gp.enemies[mapNum][i].worldY = gp.tileSize * 37;
        i++;
        gp.enemies[mapNum][i] = new ENM_GreenSlime(gp);
        gp.enemies[mapNum][i].worldX = gp.tileSize * 26;
        gp.enemies[mapNum][i].worldY = gp.tileSize * 37;
        i++;
    }
}
