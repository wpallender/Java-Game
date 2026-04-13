package tile_interactive;

//import entity.Entity;
import main.GamePanel;

public class IT_Trunk extends InteractiveTile{

    public IT_Trunk(GamePanel gp, int col, int row) {
        super(gp);
        this.gp = gp;

        this.worldX = col * gp.tileSize;
        this.worldY = row * gp.tileSize;

        down1 = setup("tiles_interactive/trunk", gp.tileSize, gp.tileSize);
        //destructible = true;

        //Temporary for no collision (remove when shovel is added)
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = 0;
        solidArea.height = 0;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    // public boolean isCorrectTool(Entity entity) {
    //     boolean isCorrectTool = false;
    //     if (entity.currentTool.type == type_shovel) {
    //         isCorrectTool = true;
    //     }
    //     return isCorrectTool;
    // }
}
