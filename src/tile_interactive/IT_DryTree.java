package tile_interactive;

import java.awt.Color;

import entity.Entity;
import main.GamePanel;

public class IT_DryTree extends InteractiveTile{

    public IT_DryTree(GamePanel gp, int col, int row) {
        super(gp);
        this.gp = gp;

        this.worldX = col * gp.tileSize;
        this.worldY = row * gp.tileSize;

        down1 = setup("tiles_interactive/tree", gp.tileSize, gp.tileSize);
        destructible = true;
        life = 3;
    }

    public boolean isCorrectTool(Entity entity) {
        boolean isCorrectTool = false;
        if (entity.currentTool.type == type_axe) {
            isCorrectTool = true;
        }
        return isCorrectTool;
    }

    public void playSE() {
        //gp.playSE(##);
    }

    public InteractiveTile getDestroyedForm() {
        InteractiveTile tile = new IT_Trunk(gp, worldX/gp.tileSize, worldY/gp.tileSize);
        return tile;
    }

    //Particles
    public Color getParticleColor() { Color color = new Color(65, 50, 30); return color; }
    public int getParticleSize() { int size = 6; return size; }
    public int getParticleSpeed() { int speed = 1; return speed; }
    public int getParticleMaxLife() { int maxLife = 20; return maxLife; }
}
