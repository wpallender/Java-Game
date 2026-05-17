package main;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import entity.Player;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.Color;

import world.Chunk;
import world.TileManager;
import world.World;

public class GamePanel extends JPanel implements Runnable{
	
	//Screen Settings
	public final int tileSize = 32;
	public final int maxScreenCol = 40;
	public final int maxScreenRow = 22;
	
	public final int screenWidth = tileSize * maxScreenCol;
	public final int screenHeight = tileSize * maxScreenRow;
	
	// Camera position (in world pixels)
	double cameraX = 0;
	double cameraY = 0;
	
	// User seed input
	private String userInput;
	
	//FPS
	int fps = 60;
	
	// Seed
	long seed = 0;
	
	World world = new World(0);
	TileManager tileM = new TileManager(this);
	KeyHandler keyH = new KeyHandler();
	Thread gameThread;
	Player player = new Player(this, keyH);
	
	JTextField seedBox;
	JButton submitButton;
	
	public GamePanel() {		
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
		
		setLayout(null);
		seedBox = new JTextField();
		seedBox.setBounds(300, 240, 200, 30);
		add(seedBox);
		
		submitButton = new JButton("Generate");
		submitButton.setBounds(350, 240, 100, 30);
		add(submitButton);
		
		submitButton.addActionListener (e -> {
			
			try {
				seed = Long.parseLong(seedBox.getText());
			} catch (NumberFormatException ex) {}
		});
	}
	
	public void generateWorld() {
		world = new World(seed);
	}
	
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	@Override
	public void run() {
		
		double drawInterval = 1000000000/fps;
		double delta = 0;
		double lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;
		int drawCount = 0;
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;
			
			if(delta >= 1) {
				double deltaTime = 1.0 / fps; // Pass delta time in seconds
				update(deltaTime);
				repaint();
				delta--;
				drawCount++;
			}
			
			if(timer >= 1000000000) {
				drawCount = 0;
				timer = 0;
			}
			
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Calculate which tiles are visible (in world coordinates)
		int startTileX = (int)(cameraX / tileSize);
		int startTileY = (int)(cameraY / tileSize);
		int endTileX = (int)((cameraX + screenWidth) / tileSize) + 1;
		int endTileY = (int)((cameraY + screenHeight) / tileSize) + 1;
		
		// Render all visible tiles
		for(int tileY = startTileY; tileY <= endTileY; tileY++) {
			for(int tileX = startTileX; tileX <= endTileX; tileX++) {
				int chunkX = tileX / Chunk.SIZE;
				int chunkY = tileY / Chunk.SIZE;
				int localX = tileX - (chunkX * Chunk.SIZE);
				int localY = tileY - (chunkY * Chunk.SIZE);
				
				// Handle negative coordinates
				if(localX < 0) {
					chunkX--;
					localX += Chunk.SIZE;
				}
				if(localY < 0) {
					chunkY--;
					localY += Chunk.SIZE;
				}
				
				Chunk chunk = world.getChunk(chunkX, chunkY);
				if(chunk == null) continue;
				
				int tile = chunk.tiles[localX][localY];
				
				// Calculate screen position
				int screenX = (int)(tileX * tileSize - cameraX);
				int screenY = (int)(tileY * tileSize - cameraY);
				
				// Render terrain tile
				switch(tile) {
					case 0:
						g2.drawImage(tileM.tile[3].image, screenX, screenY, tileSize, tileSize, null);
						break;
					case 1:
						g2.drawImage(tileM.tile[2].image, screenX, screenY, tileSize, tileSize, null);
						break;
					case 2:
						g2.drawImage(tileM.tile[0].image, screenX, screenY, tileSize, tileSize, null);
						break;
				}
				
				// Render object on top (trees, etc.)
				int object = chunk.objects[localX][localY];
				if(object > 0) {
					switch(object) {
						case 3:
							g2.drawImage(tileM.tile[1].image, screenX, screenY, tileSize, tileSize, null);
							break;
					}
				}
			}
		}
		g2.setColor(Color.WHITE);
		g2.drawString("FPS: " + fps, 20, 20);
		player.draw(g2);
		g2.dispose();
	}

	public void update(double deltaTime) {
		player.update(deltaTime);
		
		// Camera follows player with proper rounding
		int playerScreenCenterX = (int)player.worldX + (tileSize / 2);
		int playerScreenCenterY = (int)player.worldY + (tileSize / 2);
		
		cameraX = playerScreenCenterX - (screenWidth / 2);
		cameraY = playerScreenCenterY - (screenHeight / 2);
		
		// Generate chunks around camera with 2-chunk buffer to prevent loading lag
		int bufferChunks = 2;
		int startChunkX = (int)cameraX / Chunk.SIZE - bufferChunks;
		int startChunkY = (int)cameraY / Chunk.SIZE - bufferChunks;
		int endChunkX = (int)(cameraX + screenWidth) / Chunk.SIZE + bufferChunks;
		int endChunkY = (int)(cameraY + screenHeight) / Chunk.SIZE + bufferChunks;
		
		for(int chunkY = startChunkY; chunkY <= endChunkY; chunkY++) {
			for(int chunkX = startChunkX; chunkX <= endChunkX; chunkX++) {
				if(world.getChunk(chunkX, chunkY) == null) {
					world.getOrCreateChunk(chunkX, chunkY);
				}
			}
		}
	}
}
