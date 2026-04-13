package object;

import entity.Projectile;
import main.GamePanel;
import entity.Entity;

public class OBJ_Bullet extends Projectile{
    GamePanel gp;
    public OBJ_Bullet(GamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Bullet";
        speed = 10;
        maxLife = 40;
        life = maxLife;
        attack = 2;
        ammoCost = 1;
        alive = false;
        getImage();
    }

    public void getImage() {
        up1 = setup("projectile/bullet_up", gp.tileSize/4, gp.tileSize/4);
        down1 = setup("projectile/bullet_down", gp.tileSize/4, gp.tileSize/4);
        left1 = setup("projectile/bullet_left", gp.tileSize/4, gp.tileSize/4);
        right1 = setup("projectile/bullet_right", gp.tileSize/4, gp.tileSize/4);
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
