package object;

import main.GamePanel;

import entity.Entity;

public class OBJ_Door extends Entity{
    
    public OBJ_Door(GamePanel gp) {
        super(gp);

        name = "Door";
        down1 = setup("objects/door", gp.tileSize, gp.tileSize);
        collision = true;

        //Hitbox
        solidArea.x = 0;
        solidArea.y = 16;
        solidArea.width = 48;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }
}
