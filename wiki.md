# Prism wiki

Everything in the Prism mod, how to get it, and what it does. Written for version **3.2.11**,
Minecraft 26.1.2 (Fabric). `README.md` has the technical side; this page is the quick reference.

## The short version

1. Make a **Diamond Grinder**, grind diamonds into **Diamond Powder**.
2. Put **50 Diamond Powder and a Pink Dye** into a bundle to get **Prism Powder**.
3. Use the powder on stone or deepslate to make **Prism Ore**, and mine it for **Prism** (the gem).
4. With Prism you can build a **Prism Block**, an **Egg Reclaimer**, a **Pylon**, and the Prism tools.
   The **Prism Sword** and **Prism Pickaxe** also need a **dragon egg**, so the Ender Dragon comes first.

```
Diamond --grinder--> Diamond Powder --50 + Pink Dye in a bundle--> Prism Powder
Prism Powder --use on stone/deepslate--> Prism Ore --mine (diamond pickaxe+)--> Prism
Prism --> Prism Block, Egg Reclaimer, Pylon parts, Prism's Power book
Prism + a tool of every kind + dragon egg --> Prism Sword / Prism Pickaxe
```

## Crafting recipes

Grids read left to right, top to bottom. Empty squares are empty.

### Diamond Grinder
```
Ni Ni Ni        Ni = Netherite Ingot
Ni SC Ni        SC = Stonecutter
Ni Ni Ni
```

### Prism Block, and back
```
Pr Pr Pr        Pr = Prism
Pr Pr Pr        -> 1 Prism Block
Pr Pr Pr
```
One Prism Block alone in the grid gives back **9 Prism**.

### Egg Reclaimer
```
Pr Pr Pr        Pr = Prism
Pr EE Pr        EE = Eye of Ender
Pr Pr Pr
```

### Prism's Power (enchanted book)
```
Pr Pr Pr        Pr = Prism
Pr Bk Pr        Bk = Book
Pr Pr Pr        -> Enchanted Book: Prism's Power
```
Apply it to a Prism Sword on an anvil. It only fits the Prism Sword.

### Prism Sword (shapeless, any arrangement)
Wooden, Stone, Copper, Iron, Golden, Diamond and Netherite **Sword**, plus **1 Prism** and
**1 Dragon Egg**. Nine items, one of each.

### Prism Pickaxe (shapeless, any arrangement)
Wooden, Stone, Copper, Iron, Golden, Diamond and Netherite **Pickaxe**, plus **1 Prism** and
**1 Dragon Egg**. Nine items, one of each.

### Pylon Core
```
Ob Pr Ob        Ob = Obsidian
Ob NS Ob        Pr = Prism
Ob Pr Ob        NS = Nether Star
```

### Pylon Shell
```
DP DP DP        DP = Diamond Powder
DP Pr DP        Pr = Prism
DP DP DP
```

### Pylon Conductor
```
Rs Rs Rs        Rs = Redstone Dust
Rs Pr Rs        Pr = Prism
Rs Rs Rs
```

### Pylon
```
Sh Co Sh        Sh = Pylon Shell
Co Cr Co        Co = Pylon Conductor
Sh Co Sh        Cr = Pylon Core        (4 shells, 4 conductors, 1 core)
```

### Things with no crafting grid
| Item | How you get it |
| --- | --- |
| **Diamond Powder** | Put diamonds in the Diamond Grinder. |
| **Prism Powder** | Put 50 Diamond Powder and 1 Pink Dye into any bundle. |
| **Prism Ore / Deepslate Prism Ore** | Use Prism Powder on stone / deepslate. |
| **Prism** | Mine Prism Ore. |

## Items and blocks

### Prism
The gem. It is what most recipes want. Dropped by Prism Ore (Fortune works on it). A pink
version of a diamond.

### Prism Ore and Deepslate Prism Ore
Never generate naturally. They only exist where Prism Powder has been used. Mining needs a
**diamond pickaxe or better**. They drop Prism, or themselves with Silk Touch.

### Diamond Powder
Ground diamond. Used for Prism Powder and Pylon Shells.

### Diamond Grinder
A black stonecutter with a diamond-coloured blade. **Right-click** it to open a small screen:
- Put diamonds in the left slot.
- The right slot shows **2 Diamond Powder**.
- Each click on the result grinds **one** diamond (a stack of 64 goes 64, 63, 62, ...).
- Shift-click on the result repeats until the diamonds run out.

### Prism Powder
A pink bundle. Made by putting **50 Diamond Powder and 1 Pink Dye** into any bundle; it turns into
Prism Powder by itself within a quarter of a second. Anything else in the bundle is handed back.
**Right-click** a block with it: every **stone or deepslate** in a 3x3x3 cube around that block
becomes Prism Ore. One use per powder.

### Prism Block
Decorative storage block. Mined with a diamond pickaxe or better.

### Prism Sword
- **25 attack damage**, 64 durability, normal sword attack speed. Epic.
- Locked behind the Ender Dragon, because the recipe uses a dragon egg.
- Works with every normal sword enchantment.
- With **Prism's Power** enchantment on it: hold right-click to draw back, release to launch yourself the way
  you are looking, like a firework. 3 second cooldown, costs 1 durability.

### Prism's Power (enchantment)
An enchantment that does one thing: it lets the Prism Sword **launch you** like a firework under an
elytra.
- **Getting it:** craft the Prism's Power book (8 Prism around a Book, see the recipes above), then
  apply it to a Prism Sword on an anvil. It is not found in the enchanting table. It has one level.
- **Only fits the Prism Sword.** The anvil refuses it on any other item, including the Prism Pickaxe.
- **Using it:** hold **right-click** to draw the sword back, then **release** to be thrown in the
  direction you are looking. Left-click still swings as normal.
  - The wind-up is a quarter of a second. Let go earlier and nothing fires.
  - The launch is about as fast as Riptide III. If you are standing on the ground you are lifted
    off it first so the boost is not swallowed by the floor.
  - **3 second cooldown** between launches.
  - Costs **1 durability** each time (Unbreaking can save it).
  - There is **no fall protection**: with an elytra you fly, without one you come back down the
    hard way.
- **Stacks with everything else.** Sharpness, Looting, Fire Aspect, Mending and so on all work on the
  sword alongside it.
- **Turning it off:** the **Prism Right Clicks** key (backtick) switches it off, so a right click
  passes to your other hand and a shield works. See Controls.

### Prism Pickaxe
- Netherite damage and enchantability, **512 durability**, mines **5% faster** than netherite.
- **Left-click** is a normal pickaxe.
- **Hold right-click** on any block to mine it at the normal speed. When it breaks, the **8 blocks
  around it** (a 3x3 in the face you were mining) break too. Each block costs 1 durability.
  Works on any breakable block, not only stone. Fortune and Silk Touch apply.
- Works on **every** breakable block. A block that has a right-click of its own (a chest, furnace,
  crafting table, barrel, anvil, lever, and this mod's Egg Reclaimer, Pylon and Diamond Grinder)
  **keeps it**: a normal right-click opens or uses it, as it would with any item in your hand.
  To mine one of those instead, **crouch (hold Shift) and right-click** it. Air, liquids and
  unbreakable blocks like bedrock are skipped.
- Epic and fire resistant.

### Egg Reclaimer
A pink lodestone. **Right-click** it holding a **Prism Sword or Prism Pickaxe**: the tool is taken,
an ominous sound plays, and the tool comes back out together with a **dragon egg**. The returned
tool is stamped "Egg reclaimed" and the reclaimer refuses it from then on, so each tool gives one
egg, ever. Enchantments and durability are kept. A freshly crafted tool costs an egg to make, so
it starts unstamped.

### Pylon
A fast-travel block. A network of up to **10 pylons for the whole world**.
- **Placing** one opens a popup to name it (24 characters). Done, Enter or Escape keeps the text,
  which starts as "Pylon 1", "Pylon 2", and so on.
- The **colour** shows the dimension it was placed in:

  | Dimension | Pylon colour |
  | --- | --- |
  | Overworld | green |
  | The Nether | dark red |
  | The End | yellow |
  | Anything else (Aether, Twilight Forest, ...) | pink |

- **Right-click** a pylon to open the list of every active pylon with its dimension. Pick one to
  teleport to it, even across dimensions. You must be within 10 blocks of the pylon you opened
  the list at.
- Trying to place an eleventh shows "You have the max amount of pylons active (10)." Breaking a
  pylon frees its place.

### Pylon Core, Pylon Shell, Pylon Conductor
Crafting parts for the pylon, nothing else. The core is drawn as a small pink cube.

## Controls

| Key | What it does |
| --- | --- |
| **`** (backtick) | **Prism Right Clicks** on and off. Off disables Prism's Power and the pickaxe's right-click mining, so the right click falls through to your other hand and a shield works. Shown in the action bar. Rebind it under Controls, category Prism. |

## Quick stats

| Thing | Number |
| --- | --- |
| Prism Sword damage | 25 |
| Prism Sword durability | 64 |
| Prism Pickaxe durability | 512 |
| Prism Pickaxe mining speed | 9.45 (netherite is 9.0) |
| Diamond to Diamond Powder | 1 to 2 |
| Prism Powder cost | 50 Diamond Powder + 1 Pink Dye |
| Prism Powder area | 3x3x3 |
| Max pylons | 10 |
| Prism's Power cooldown | 3 seconds |

Only the Ender Dragon gate, the one-egg-per-tool rule and the pylon limit are meant to stay fixed;
everything else is open to change.
