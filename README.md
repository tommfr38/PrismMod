# Prism

Fabric mod for Minecraft **26.1.2**. Adds the Prism and the legendary Prism Sword.

## Items

| Item | Notes |
| --- | --- |
| **Prism** (`prismmod:prism`) | Dropped by Prism Ore. A pink-scaled diamond. Rare. |
| **Prism Sword** (`prismmod:prism_sword`) | 25 attack damage, 64 durability, 1.6 attacks/sec, Epic. Light pink, shaded like an iron sword. |
| **Prism Block** (`prismmod:prism_block`) | Decorative. A pink-scaled diamond block. Pickaxe-mined (diamond or better). |
| **Prism Ore / Deepslate Prism Ore** | Only exist where Prism Powder has been used. Needs a diamond pickaxe or better. Drops a Prism (Fortune applies), or itself with Silk Touch. |
| **Diamond Grinder** (`prismmod:diamond_grinder`) | A black stonecutter with a diamond-edged blade. Right-click with a diamond to get 2 Diamond Powder. |
| **Diamond Powder** | Ground diamond. |
| **Prism Powder** | A pink bundle. Right-click a block: all stone and deepslate in a 3x3x3 cube around it becomes Prism Ore. One use each. |

## Getting Prism

1. **Diamond Grinder** — 8 netherite ingots around a stonecutter.
2. Grind diamonds at the grinder: 1 diamond becomes 2 Diamond Powder.
3. Put **50 Diamond Powder and 1 Pink Dye into any bundle**. It turns into Prism Powder on
   its own (checked every 5 ticks; anything extra in the bundle is handed back).
4. Use the Prism Powder on stone or deepslate, then mine the ore with a diamond pickaxe.

## Recipes

**Prism Sword** — every sword in the game, the Prism, and a dragon egg. Nine
ingredients, one of each:

```
wooden    prism     netherite
stone     egg       diamond
copper    iron      golden
```

The recipe is **shapeless**, so that arrangement works and so does any other. The egg is
consumed, so the sword cannot exist before the Ender Dragon is beaten.

**Dragon egg refund** — put the Prism Sword alone in a crafting grid to get the dragon
egg back. The sword is consumed; the Prisms, the stick and any enchantments are not
returned, and the sword's remaining durability does not matter.

**Prism Block** — 9 Prisms in a 3x3, and a shapeless recipe turns the block back into
9 Prisms. Standard storage-block convention, so nothing is ever lost by crafting it.

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

Output lands in `build/libs/prismmod-1.0.0.jar`.
