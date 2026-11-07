# Prism

Fabric mod for Minecraft **26.1.2**. Adds the Prism and the legendary Prism Sword.

## Items

| Item | Notes |
| --- | --- |
| **Prism** (`prismmod:prism`) | Dropped by Prism Ore. A pink-scaled diamond. Rare. |
| **Prism Sword** (`prismmod:prism_sword`) | 25 attack damage, 64 durability, 1.6 attacks/sec, Epic. Light pink, shaded like an iron sword. |
| **Prism Pickaxe** (`prismmod:prism_pickaxe`) | A pink netherite pickaxe: 512 durability, netherite tier, damage and enchantability, 5% faster mining (9.45 vs 9.0). Hold right-click on any block (not just stone) to mine it. A block with a right-click of its own (chest, furnace, lever, ...) keeps it; crouch + right-click mines it instead at the normal break time; when it breaks, the 8 blocks around it (3x3 in the face you mined) break too. Left-click is a plain netherite pickaxe. Epic, fire resistant. |
| **Pylon** (`prismmod:pylon`) | Fast-travel block, crafted from a core, 4 shells and 4 conductors (see Recipes). Pink block with galactic-alphabet text. Placing one asks you to name it; right-clicking any pylon lists every active pylon and its dimension, and picking one teleports you to it. Max 10 active. |
| **Prism Block** (`prismmod:prism_block`) | Decorative. A pink-scaled diamond block. Pickaxe-mined (diamond or better). |
| **Prism Ore / Deepslate Prism Ore** | Only exist where Prism Powder has been used. Needs a diamond pickaxe or better. Drops a Prism (Fortune applies), or itself with Silk Touch. |
| **Diamond Grinder** (`prismmod:diamond_grinder`) | A black stonecutter with a diamond-edged blade. Right-click to open a screen: diamonds in the left slot come out as 2 Diamond Powder each in the right slot. |
| **Diamond Powder** | Ground diamond. |
| **Prism Powder** | A pink bundle. Right-click a block: all stone and deepslate in a 3x3x3 cube around it becomes Prism Ore. One use each. |

## Getting Prism

1. **Diamond Grinder** — 8 netherite ingots around a stonecutter.
2. Open the grinder and put diamonds in the left slot: each becomes 2 Diamond Powder in the right slot (one stack of powder at a time).
3. Put **50 Diamond Powder and 1 Pink Dye into any bundle**. It turns into Prism Powder on
   its own (checked every 5 ticks; anything extra in the bundle is handed back).
4. Use the Prism Powder on stone or deepslate, then mine the ore with a diamond pickaxe.

## Recipes

**Pylon** (`prismmod:pylon`) is made from three parts, each a 3x3 ring:

```
Pylon Core            Pylon Shell            Pylon Conductor        Pylon
O P O                 D D D                  R R R                  S C S
O N O                 D L D                  R P R                  C K C
O P O                 D D D                  R R R                  S C S

O obsidian            D Diamond Powder       R redstone dust        S shell
P Prism (the gem)     L Prism (the gem)      P Prism (the gem)      C conductor
N nether star                                                       K core
```

Each recipe makes one.
The core is drawn as a small half-size cube whose faces are the pinkscaled glow of a beacon; the shell and
conductor are pink to match the pylon.

**Prism Sword** — every sword in the game, the Prism, and a dragon egg. Nine
ingredients, one of each:

```
wooden    prism     netherite
stone     egg       diamond
copper    iron      golden
```

The recipe is **shapeless**, so that arrangement works and so does any other. The egg is
consumed, so the sword cannot exist before the Ender Dragon is beaten.

**Prism Pickaxe** — every pickaxe in the game (wooden through netherite), a Prism, and a
dragon egg, shapeless, like the sword.

**Egg Reclaimer** (`prismmod:egg_reclaimer`; 8 Prisms around an eye of ender) — a pink lodestone. Right-click
it holding a Prism Sword or Prism Pickaxe: the tool is taken, an ominous sound plays, and the tool and a
dragon egg come back out together (with a 2 second pickup delay). The returned tool is
stamped "Egg reclaimed" and the reclaimer refuses it from then on, so one tool gives one
egg, ever. Enchantments and durability are kept. A newly crafted tool costs an egg and
starts unstamped. There is no longer a crafting-grid refund.

**Prism Block** — 9 Prisms in a 3x3, and a shapeless recipe turns the block back into
9 Prisms. Standard storage-block convention, so nothing is ever lost by crafting it.

## Tooltips

Prism items show short description lines. They are lang entries named
`tooltip.prismmod.<item>.1`, `.2`, and so on; an item with none shows nothing extra, so adding
or editing a description is a lang change only.

## Pylons

- **Placing:** a popup asks for a name (24 characters). Escape or Done keeps whatever is in the box,
  which starts as the next free "Pylon n". An eleventh pylon is refused with
  "You have the max amount of pylons active (10)." Breaking a pylon frees its place.
- **Colour** records the dimension it was placed in: Overworld green, Nether dark red, End
  yellow, anything else (the Aether, Twilight Forest, ...) the normal pink. It is the block state
  `dimension=overworld|nether|end|other`.
- **Right-click** opens the list: name and dimension for every pylon, with the one you clicked greyed
  out. Picking one teleports you onto or beside it, across dimensions. The server only allows it
  while you are within 10 blocks of the pylon you opened the list at, and only to pylons that are
  in the network.
- **One network per world**, shared by everyone on it, saved in the overworld's data
  (`pylon_network`). The limit of 10 is for the whole world, not per player.
- A pylon that arrives some other way (`/setblock`) joins the network the first time someone
  right-clicks it, if there is room.

## Prism Right Clicks

A key binding under Controls, category *Prism*, default `` ` `` (backtick). It switches **Prism's
Power and the Prism Pickaxe's right-click mining** off and on, so the right click falls
through to the other hand and a shield works. The state shows as an action-bar message, is
kept per player on the server, and is re-sent on every world join. It resets to on at each
launch.

## Prism's Power

An enchantment that does exactly one thing: hold right-click to draw the Prism Sword
back, release to be thrown in the direction you are looking, like a firework under an
elytra. Left-click still swings.

**Craft it in a crafting table:** one book in the middle, 8 Prisms around it. That
yields a Prism's Power book, which you apply to the sword on an anvil.

- 3 second cooldown.
- Costs one durability, taken down the same path as a swing, so Unbreaking gets its roll.
- No fall protection. With an elytra you fly; without one you come back down the hard way.
- Wind-up is 5 ticks. Release earlier and nothing fires.
- The sword draws back over four stages across the first 9 ticks, then holds there for as
  long as you keep the button down.

It **stacks with everything else**. The sword is an ordinary sword as far as the game is
concerned, so Sharpness, Looting, Fire Aspect, Mending and the rest apply normally from
the enchanting table or an anvil, and Prism's Power sits alongside them.

What Prism's Power will *not* do is go on anything else. Its definition names
`prismmod:prism_sword` as the only item it supports, so the anvil refuses it on any
other weapon.

### How it is built

The enchantment JSON carries **no effects at all** — it exists to bind to the sword and be
read. No enchantment effect component can move the player, so the launch is item code in
`PrismSwordItem` that checks for the enchantment.

The drawn-back pose is two things: `getUseAnimation` returning `SPEAR` for the arm, plus a
model swap in `assets/prismmod/items/prism_sword.json` that range-dispatches on
`minecraft:use_duration` through `prism_sword_charging_1` to `_4` and holds at the last.
The bow pulls the same way. The arm pose alone is nearly invisible on a flat sprite, so
the model swap is what you actually see.

## Tuning

- Damage and attack speed: `PrismItems.PRISM_SWORD`, the `sword(PRISM, 24.0F, -2.4F)` call.
  The tooltip number is the player's base 1 plus the 24 here.
- Durability and enchantability: the `PRISM` `ToolMaterial` in the same file.
- Powder per Prism Powder: `POWDER_NEEDED` in `PrismPowderMaking`. Powder radius: `RADIUS` in `PrismPowderItem`.

## Building

Needs JDK 25. The toolchain used here lives at
`../TwilightBoss/toolchain/jdk-25.0.4+7`.

```
JAVA_HOME=../TwilightBoss/toolchain/jdk-25.0.4+7 ./gradlew build
```

Output lands in `build/libs/prismmod-3.2.11.jar`.

## Testing

`./gradlew runClientGameTest` starts a real client in a throwaway world and mines with the
Prism Pickaxe's right click. It checks that a second mining of the same block starts cleanly,
that no crack is left on release or when looking away, and that a finished break takes exactly
the 3x3. A second test places, names, lists and travels between pylons across the Nether and
Overworld, checks the limit of ten, and checks the saved format round-trips. The test lives in `src/gametest` and is not part of the shipped jar.
