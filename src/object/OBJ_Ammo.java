package object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Ammo extends Entity{
    GamePanel gp;
    public OBJ_Ammo (GamePanel gp){
        super(gp);
        this.gp = gp;

        type = type_pickupOnly;
        name = "Ammo";
        value = 1;
        down1 = setup("objects/ammo_full", gp.tileSize, gp.tileSize);
        image = setup("objects/ammo_full", gp.tileSize, gp.tileSize);
        image2 = setup("objects/ammo_blank", gp.tileSize, gp.tileSize);
    }
    
    public void use(Entity entity) {
        gp.ui.addMessage("Ammo +" + value);
        gp.ui.addMessage("Ammo: " + ammo);
        entity.ammo += value;
    }
}
