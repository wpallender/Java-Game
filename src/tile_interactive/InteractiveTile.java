package tile_interactive;

import entity.Entity;
import main.GamePanel;

public class InteractiveTile extends Entity{
    GamePanel gp;
    public boolean destructible = false;

    public InteractiveTile(GamePanel gp) {
        super(gp);
        this.gp = gp;
    }
    
    public boolean isCorrectTool(Entity entity) {
        boolean isCorrectTool = false;
        return isCorrectTool;
    }

    public void playSE() {}

    public InteractiveTile getDestroyedForm() {
        InteractiveTile tile = null;
        return tile;
    }

    public void update() {
        //Invincible
        if (invincible == true) {
            invincibleCounter++;
            if (invincibleCounter > 20) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }
}