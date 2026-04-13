package object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Coin_Bronze extends Entity{
    GamePanel gp;
    public OBJ_Coin_Bronze(GamePanel gp) {
        super(gp);
        this.gp = gp;

        type = type_pickupOnly;
        name = "Bronze Coin";
        value = 1;
        down1 = setup("objects/coin_bronze", gp.tileSize, gp.tileSize);
    }
    
    public void use(Entity user, GamePanel gp) {
        gp.ui.addMessage("Money +" + value);
        gp.player.money += value;
    }
}
