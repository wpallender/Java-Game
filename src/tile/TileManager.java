package tile;

import main.GamePanel;
import main.UtilityTool;
import tile_interactive.IT_DryTree;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][][];
    public int mapCol[];
    public int mapRow[];

    public TileManager(GamePanel gp) {
        this.gp = gp;

        tile = new Tile[10];
        mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];
        mapCol = new int[gp.maxMap];
        mapRow = new int[gp.maxMap];

        loadMap("res/maps/world02.txt", 0);
        loadMap("res/maps/interior01.txt", 1);
        getTileImage();
        refreshInteractiveTiles();
    }

    //Assign Tiles
    public void getTileImage() {
            setup(0, "grass", false);
            setup(1, "wall", true);
            setup(2, "water", true);
            setup(3, "earth", false);
            setup(4, "grass", false); //Trees
            setup(5, "sand", false);
            setup(6, "hut", false);
            setup(7, "floor01", false);
            setup(8, "table01", true);
    }

    public void setup(int index, String imageName, boolean collision) {
        UtilityTool uTool = new UtilityTool();

        try {
            tile[index] = new Tile();
            InputStream is = getClass().getResourceAsStream("/res/tiles/" + imageName + ".png");
            if (is == null) { is = getClass().getResourceAsStream("res/tiles/" + imageName + ".png"); }
            
            if (is != null) {
                tile[index].image = ImageIO.read(is);
                tile[index].image = uTool.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
                tile[index].collision = collision;
            } else {
                System.err.println("ERROR: Could not find tile resource: " + imageName + ".png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath, int map) {
        try {
            InputStream is = getClass().getResourceAsStream("/" + filePath);
            if (is == null) { is = getClass().getResourceAsStream(filePath); }
            if (is == null) {
                java.io.File f = new java.io.File("src/"+filePath);
                if (!f.exists()) f = new java.io.File(filePath);
                if (f.exists()) { is = new java.io.FileInputStream(f); }
            }

            if (is == null) { throw new java.io.FileNotFoundException("Map file not found: " + filePath); }

            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));

            String line;
            int row = 0;
            int maxCol = 0;

            while ((line = br.readLine()) != null && row < gp.maxWorldRow) {
                String[] numbers = line.trim().split("\\s+");
                maxCol = Math.max(maxCol, numbers.length);
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    int num = 0;
                    if (col < numbers.length) {
                        try { num = Integer.parseInt(numbers[col]); }
                        catch (NumberFormatException nfe) { num = 0; }
                    }
                    mapTileNum[map][col][row] = num;
                }
                row++;
            }
            mapCol[map] = maxCol;
            mapRow[map] = row;
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Call this whenever the map changes to recreate interactive tiles for the current map
    public void refreshInteractiveTiles() {
        // Clear previous interactive tiles for the current map
        for (int i = 0; i < gp.iTile[gp.currentMap].length; i++) {
            gp.iTile[gp.currentMap][i] = null;
        }
        int i = 0;
        int mapMaxCol = mapCol[gp.currentMap];
        int mapMaxRow = mapRow[gp.currentMap];
        for (int col = 0; col < mapMaxCol; col++) {
            for (int row = 0; row < mapMaxRow; row++) {
                int tileNum = mapTileNum[gp.currentMap][col][row];
                int rand = new Random().nextInt(100) + 1;
                if (tileNum == 4 && rand <= 50) {
                    gp.iTile[gp.currentMap][i] = new IT_DryTree(gp, col, row);
                    i++;
                }
            }
        }
    }

    public void draw(java.awt.Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;
        int mapMaxCol = gp.tileM.mapCol[gp.currentMap];
        int mapMaxRow = gp.tileM.mapRow[gp.currentMap];
        while(worldCol < mapMaxCol && worldRow < mapMaxRow) {
            int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;
            //Stop movement of camera at edge
            if (gp.player.screenX > gp.player.worldX) { screenX = worldX; }
            if (gp.player.screenY > gp.player.worldY) { screenY = worldY; }
            int rightOffset = gp.screenWidth - gp.player.screenX;
            if (rightOffset > (gp.worldWidth - gp.player.worldX)) { screenX = gp.screenWidth - (gp.worldWidth - worldX); }
            int bottomOffset = gp.screenHeight - gp.player.screenY;
            if (bottomOffset > (gp.worldHeight - gp.player.worldY)) { screenY = gp.screenHeight - (gp.worldHeight - worldY); }
            //Camera
            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                    g2.drawImage(tile[tileNum].image, screenX, screenY, null);
            //Camera at edge
            } else if (gp.player.screenX > gp.player.worldX || gp.player.screenY > gp.player.worldY || 
                        rightOffset > (gp.worldWidth - gp.player.worldX) || bottomOffset > (gp.worldHeight - gp.player.worldY)) {
                g2.drawImage(tile[tileNum].image, screenX, screenY, null);
            }
            worldCol++;
            if (worldCol == mapMaxCol) {  // Changed from gp.maxWorldCol
                worldCol = 0;
                worldRow++;
            }
        }
    }
}