# Rechanted

## Enchanting power providers

The Rechanted table uses NeoForge's `BlockState.getEnchantPowerBonus(level, pos)`
to total enchanting power in its existing 7×3×7 area: up to three blocks away
horizontally, from the table's height through two blocks above it. Rechanted's
existing placement rules apply; no vanilla air-gap requirement is added.

The `required_bookshelves` config keys remain compatible and now specify the
required enchanting power. A normal bookshelf supplies one power; fractional
and higher values supplied by other mods count at their actual value, with a
small float-precision tolerance at the requirement. The table
screen, activation effects, and server purchase checks use the same calculation.

By default, NeoForge gives blocks in `minecraft:enchantment_power_provider` one
power. A datapack can extend that tag, for example in
`data/minecraft/tags/block/enchantment_power_provider.json`:

```json
{
  "replace": false,
  "values": ["minecraft:chiseled_bookshelf"]
}
```

This example gives every chiseled bookshelf one power regardless of its contents.
Mods can override the hook to provide power based on block state, inventory,
or world position instead. Chiseled bookshelves are not added by Rechanted itself.

Only blocks supplying finite, positive power are eligible for break-chance rolls.
Custom providers can therefore be destroyed without block drops when purchasing
books. Inventory-bearing providers are not protected; do not use valuable storage
blocks as power sources unless you accept that risk.

Each purchase randomly selects sources covering the required power, whether or
not the rolls actually destroy them. Each selected source gets one roll at the
configured `book_break_chance`. If only part of the final source's power is needed,
its chance is reduced by that fraction. For example, a requirement of four uses
four ordinary shelves or eight half-power sources. A single eight-power source
gets half the configured break chance. This preserves normal bookshelf behavior
and the expected power lost without making fractional sources cheaper to consume.
Extra sources beyond the requirement do not add destruction rolls. Requirements,
XP, lapis, floor costs, and placement bounds otherwise remain unchanged.

Run `./gradlew build` to compile the mod and run the enchanting-power tests.
