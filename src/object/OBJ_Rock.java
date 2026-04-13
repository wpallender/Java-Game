package object;

import entity.Entity;
import entity.Projectile;
import main.GamePanel;

public class OBJ_Rock extends Projectile{
    GamePanel gp;
    public OBJ_Rock(GamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Rock";
        speed = 5;
        maxLife = 40;
        life = maxLife;
        attack = 2;
        ammoCost = 0;
        alive = false;
        getImage();
    }

    public void getImage() {
        up1 = setup("projectile/rock", gp.tileSize/3, gp.tileSize/3);
        down1 = setup("projectile/rock", gp.tileSize/3, gp.tileSize/3);
        left1 = setup("projectile/rock", gp.tileSize/3, gp.tileSize/3);
        right1 = setup("projectile/rock", gp.tileSize/3, gp.tileSize/3);
    }

    public boolean haveResource(Entity user) {
        boolean haveResource = false;
        if (user.ammo >= ammoCost) {
            haveResource = true;
        }
        return haveResource;
    }

    public void subtractResource(Entity user) {
        user.ammo -= ammoCost;
    }
}
