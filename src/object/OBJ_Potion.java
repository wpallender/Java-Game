package object;

import main.GamePanel;
import entity.Entity;

public class OBJ_Potion extends Entity {
    GamePanel gp;

    public OBJ_Potion(GamePanel gp) {
        super(gp);
        type = type_consumable;
        name = "Potion";
        value = 10;
        down1 = setup("objects/potion_red", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nHeals " + value + " HP.";
    }

    public void use(Entity user, GamePanel gp) {
        gp.player.life += value;
        gp.ui.addMessage("Healed " + value + " HP!");
    }
}
