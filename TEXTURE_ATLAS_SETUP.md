# Texture Atlas Setup Guide

## Overview
Your game has been updated to use a **unified texture atlas** system, where all graphics (tiles, players, NPCs, objects, enemies, projectiles, and UI) are contained in a single PNG file, just like Minecraft.

## Why Use a Texture Atlas?
✅ **Performance**: Single image load instead of 50+ individual PNG files  
✅ **Memory**: All sprites stored contiguously in one image  
✅ **Organization**: All assets in one place  
✅ **Scalability**: Easy to add new sprites  
✅ **Industry Standard**: Used in professional games

## Creating Your Texture Atlas

### 1. **Image Specifications**
- **Size**: 256×256 pixels (or larger, e.g., 512×512)
- **Base Sprite Size**: 16×16 pixels (your `originalTileSize`)
- **Format**: PNG with transparency
- **Arrangement**: Grid layout, starting from top-left (0,0)

### 2. **Atlas Layout Structure**

The atlas is organized by rows and columns. Each sprite occupies a 16×16 grid space:

```
COLUMN:  0           1           2           3           4           5           6           7
ROW 0: [grass]    [wall]      [water]     [earth]     [tree]      [sand]      [?]         [?]
ROW 1: [player_ᐁ] [player_↑]  [player_←]  [player_→]  [......]    [......]    [......]    [......]
ROW 2: [......]    [......]    [......]    [......]    [......]    [......]    [......]    [......]
ROW 3: [p_atk_↓] [p_atk_↑]  [p_atk_←]  [p_atk_→]  [......]    [......]    [......]    [......]
ROW 4: [......]    [......]    [......]    [......]    [......]    [......]    [......]    [......]
ROW 5: [p_axe_↓] [p_axe_↑]  [p_axe_←]  [p_axe_→]  [......]    [......]    [......]    [......]
ROW 6: [......]    [......]    [......]    [......]    [......]    [......]    [......]    [......]
ROW 7: [coin]     [key]       [sword]     [axe]       [shield]    [heart]     [potion]    [ammo]
ROW 8: [boots]    [chest]     [door]      [heart_f]   [heart_h]   [heart_e]   [......]    [......]
ROW 9: [npc_↓]   [npc_↑]    [npc_←]    [npc_→]    [......]    [......]    [......]    [......]
ROW 10:[......]    [......]    [......]    [......]    [......]    [......]    [......]    [......]
ROW 11:[slime_↓] [slime_↑]  [slime_←]  [slime_→]  [bullet]    [rock]      [......]    [......]
ROW 12:[......]    [......]    [......]    [......]    [......]    [......]    [......]    [......]
ROW 13:[ui_full] [ui_half]   [ui_empty]  [......]    [......]    [......]    [......]    [......]
```

### 3. **Creating the Atlas Image**

#### Option A: Using Dedicated Tools
- **TexturePacker** (Professional, paid) - Optimal for large projects
- **Aseprite** (Paid, but great for pixel art)
- **Tiled** (Free) - Good for level editing, also does texture atlasing
- **Free Tools**: GIMP, Photoshop, Paint.NET

#### Option B: Manual Creation
1. Create a **256×256 PNG** image with transparent background
2. Place each sprite in the correct grid position
3. Each sprite must be exactly **16×16 pixels** (at original size)
4. Arrange according to the layout below

#### Option C: Python Script (Automated)
If you have many individual sprites, you can create a script to generate the atlas:

```python
from PIL import Image
import os

# Grid settings
SPRITE_SIZE = 16
ATLAS_WIDTH = 256
SPRITES_PER_ROW = ATLAS_WIDTH // SPRITE_SIZE

# Create blank atlas
atlas = Image.new('RGBA', (ATLAS_WIDTH, ATLAS_WIDTH), (0, 0, 0, 0))

# Sprite layout (adjust paths to your res/images directory)
sprite_positions = {
    # (row, col): 'path_to_sprite.png'
    (0, 0): 'res/tiles/grass.png',
    (0, 1): 'res/tiles/wall.png',
    (0, 2): 'res/tiles/water.png',
    (0, 3): 'res/tiles/earth.png',
    (0, 4): 'res/tiles/tree.png',
    (0, 5): 'res/tiles/sand.png',
    (1, 0): 'res/player/boy_down_1.png',
    (1, 1): 'res/player/boy_up_1.png',
    # ... add all your sprites here
}

# Paste sprites into atlas
for (row, col), sprite_path in sprite_positions.items():
    if os.path.exists(sprite_path):
        sprite = Image.open(sprite_path).resize((SPRITE_SIZE, SPRITE_SIZE))
        x = col * SPRITE_SIZE
        y = row * SPRITE_SIZE
        atlas.paste(sprite, (x, y))

# Save
atlas.save('res/atlas.png')
print("Atlas created: res/atlas.png")
```

### 4. **Sprite Registry Mapping**

All sprites are registered by name in `TextureAtlas.java`. The mappings are:

#### Tiles (Row 0)
- `tile_grass` → (0, 0)
- `tile_wall` → (0, 1)
- `tile_water` → (0, 2)
- `tile_earth` → (0, 3)
- `tile_tree` → (0, 4)
- `tile_sand` → (0, 5)

#### Player Movement (Row 1)
- `player_down_1` → (1, 0)
- `player_down_2` → (1, 1)
- `player_up_1` → (1, 2)
- `player_up_2` → (1, 3)
- `player_left_1` → (1, 4)
- `player_left_2` → (1, 5)
- `player_right_1` → (1, 6)
- `player_right_2` → (1, 7)

#### Player Sword Attack (Row 3)
- `player_attack_down_1` → (3, 0)
- `player_attack_down_2` → (3, 1)
- `player_attack_up_1` → (3, 2)
- `player_attack_up_2` → (3, 3)
- `player_attack_left_1` → (3, 4)
- `player_attack_left_2` → (3, 5)
- `player_attack_right_1` → (3, 6)
- `player_attack_right_2` → (3, 7)

#### Player Axe Attack (Row 5)
- `player_axe_down_1` → (5, 0)
- `player_axe_down_2` → (5, 1)
- `player_axe_up_1` → (5, 2)
- `player_axe_up_2` → (5, 3)
- `player_axe_left_1` → (5, 4)
- `player_axe_left_2` → (5, 5)
- `player_axe_right_1` → (5, 6)
- `player_axe_right_2` → (5, 7)

#### Objects/Items (Row 7-8)
- `obj_coin_bronze` → (7, 0)
- `obj_key` → (7, 1)
- `obj_sword` → (7, 2)
- `obj_axe` → (7, 3)
- `obj_shield_wood` → (7, 4)
- `obj_heart` → (7, 5)
- `obj_potion` → (7, 6)
- `obj_ammo` → (7, 7)
- `obj_boots` → (8, 0)
- `obj_chest` → (8, 1)
- `obj_door` → (8, 2)
- `obj_heart_full` → (8, 3)
- `obj_heart_half` → (8, 4)
- `obj_heart_empty` → (8, 5)

#### NPCs (Row 9)
- `npc_oldman_down` → (9, 0)
- `npc_oldman_up` → (9, 1)
- `npc_oldman_left` → (9, 2)
- `npc_oldman_right` → (9, 3)

#### Enemies (Row 11)
- `enemy_slime_down_1` → (11, 0)
- `enemy_slime_down_2` → (11, 1)
- `enemy_slime_up_1` → (11, 2)
- `enemy_slime_up_2` → (11, 3)
- `enemy_slime_left_1` → (11, 4)
- `enemy_slime_left_2` → (11, 5)
- `enemy_slime_right_1` → (11, 6)
- `enemy_slime_right_2` → (11, 7)

#### Projectiles (Row 11)
- `proj_bullet` → (11, 8)
- `proj_rock` → (11, 9)

#### UI Elements (Row 13)
- `ui_heart_full` → (13, 0)
- `ui_heart_half` → (13, 1)
- `ui_heart_empty` → (13, 2)

## File Location
Place your atlas PNG file at: **`res/atlas.png`**

The game will automatically load it when initializing in `GamePanel.setupGame()`.

## Adding New Sprites

### Step 1: Add sprite to atlas
Place the new 16×16 sprite in the appropriate grid position in `res/atlas.png`

### Step 2: Register in TextureAtlas.java
Add a line to the `registerAllSprites()` method:

```java
spriteRegistry.put("my_new_sprite", new int[]{row, col});
```

### Step 3: Use in your code
```java
BufferedImage sprite = setupAtlas("my_new_sprite", gp.tileSize, gp.tileSize);
```

## Troubleshooting

### "ERROR: Could not find texture atlas!"
- Ensure `res/atlas.png` exists in the correct location
- Check the file extension is `.png`

### "ERROR: Sprite not found in atlas: sprite_name"
- Check the sprite name in `TextureAtlas.java` - it's case-sensitive
- Verify the sprite position (row, col) is correct in the atlas image

### Sprites appear incorrectly/corrupted
- Ensure each sprite is exactly 16×16 pixels
- Check the grid alignment in the atlas image
- Verify transparency channel is preserved

### Performance is still slow
- Ensure only ONE atlas image is loaded (not multiple)
- Check that individual PNG files are no longer being loaded
- Monitor with Java profiler if needed

## Code Structure

- **TextureAtlas.java**: Loads atlas image and manages sprite registry
- **Entity.setupAtlas()**: Gets sprites from the atlas
- All game objects now call `setupAtlas()` instead of `setup()`

## Comparison: Before vs After

### Before (Individual Files)
```
res/
├── tiles/
│   ├── grass.png
│   ├── wall.png
│   ├── water.png
│   └── ... (50+ files)
├── player/
│   ├── boy_down_1.png
│   ├── boy_down_2.png
│   └── ... (16+ files)
├── objects/
│   ├── coin_bronze.png
│   └── ... (10+ files)
└── ... (more folders)
```
**Total: 80+ PNG files, 50+ MB**

### After (Single Atlas)
```
res/
└── atlas.png (256×256, ~100 KB)
```
**Total: 1 PNG file, optimized size**

---

## Next Steps

1. **Create your `res/atlas.png`** following the layout guide
2. **Run your game** - it will now load all sprites from the atlas
3. **Add more sprites** as needed using the registration system

Enjoy your optimized texture atlas system! 🎮
