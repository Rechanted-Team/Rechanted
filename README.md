# Rechanted

=======

## Enchanting power providers

The Rechanted table uses NeoForge's `BlockState.getEnchantPowerBonus(level, pos)`
to total enchanting power in its existing 7×3×7 area: up to three blocks away
horizontally, from the table's height through two blocks above it. Rechanted's
existing placement rules apply; no vanilla air-gap requirement is added.

The `required_bookshelves` config keys remain compatible and now specify the
required enchanting power. A normal bookshelf supplies one power; fractional
and higher values supplied by other mods count at their actual value. The table
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

Only blocks currently supplying positive power are eligible for Rechanted's
existing bookshelf break-chance rolls. Custom providers can therefore be consumed
when purchasing books. The existing per-purchase limit remains a count of blocks,
so one high-power provider receives one break roll rather than a roll per power.

Run `./gradlew build` to compile the mod and run the enchanting-power tests.
