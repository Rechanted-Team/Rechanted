package net.tagtart.rechanted.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.tagtart.rechanted.Rechanted;
import net.tagtart.rechanted.config.RechantedCommonConfigs;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BookRarityProperties {

    public enum BonusPoolEntryType {
        MYSTERIOUS_BOOK,
        COMMON_GEM_POOL,
        RARE_GEM_POOL
    }

    public String key;
    public float rarity;
    public int color;
    public int requiredExp;
    public int worldSpawnWeight;
    public int minSuccess;
    public int maxSuccess;
    public int forcedBookBreaks;
    public int forcedFloorBreaks;
    public double bookBreakChance;
    public double floorBreakChance;
    // Retains the legacy config name; the value now measures enchanting power.
    public int requiredBookShelves;
    public int requiredLapis;
    public Block floorBlock;
    public double bonusItemRollChance;
    public int bonusItemMysteriousBookWeight;
    public int bonusItemCommonGemPoolWeight;
    public int bonusItemRareGemPoolWeight;
    public int minGrindstoneXP;
    public int maxGrindstoneXP;
    public List<EnchantmentPoolEntry> enchantmentPool;
    public int enchantmentPoolTotalWeights;
    public ResourceLocation iconResourceLocation;

    private BookRarityProperties()
    {

    }

    public boolean isEnchantmentInPool(String pEnchantment) {
        for (EnchantmentPoolEntry entry : enchantmentPool) {
            if (entry.enchantment.equals(pEnchantment)) {
                return true;
            }
        }

        return false;
    }

    public Style colorAsStyle() {
        return Style.EMPTY.withColor(color);
    }

    public EnchantmentPoolEntry getRandomEnchantmentWeighted() {
        Random rand = new Random();
        int randVal = rand.nextInt(enchantmentPoolTotalWeights);
        int cumulativeWeight = 0;
        for (EnchantmentPoolEntry entry : enchantmentPool) {
            cumulativeWeight += entry.weight;
            if (randVal < cumulativeWeight) {
                return entry;
            }
        }

        throw new IllegalStateException("Failed to select an enchantment based on weight.\nEnsure Rechanted config is set up properly.");
    }

    public Optional<BonusPoolEntryType> getRandomBonusPoolEntryWeighted(Random random) {
        int totalWeight = bonusItemMysteriousBookWeight + bonusItemCommonGemPoolWeight + bonusItemRareGemPoolWeight;
        if (totalWeight <= 0) {
            Rechanted.LOGGER.warn(
                    "Bonus pool for rarity '{}' is empty. Make sure your config is set up correctly", key);
            return Optional.empty();
        }

        int randomWeight = random.nextInt(totalWeight);
        if (randomWeight < bonusItemMysteriousBookWeight) {
            return Optional.of(BonusPoolEntryType.MYSTERIOUS_BOOK);
        }

        randomWeight -= bonusItemMysteriousBookWeight;
        if (randomWeight < bonusItemCommonGemPoolWeight) {
            return Optional.of(BonusPoolEntryType.COMMON_GEM_POOL);
        }

        if (bonusItemRareGemPoolWeight > 0) {
            return Optional.of(BonusPoolEntryType.RARE_GEM_POOL);
        }

        Rechanted.LOGGER.warn(
                "Bonus pool for rarity '{}' has no valid weight. Make sure your config is set up correctly", key);
        return Optional.empty();
    }

    // For caching the weight sum later.
    private static int getEnchantmentPoolTotalWeight(List<EnchantmentPoolEntry> entries) {
        int currentSum = 0;
        for (int i = 0; i < entries.size(); ++i) {
            currentSum += entries.get(i).weight;
        }

        return currentSum;
    }





    public static BookRarityProperties getRandomRarityWeighted() {

        BookRarityProperties[] properties = getAllProperties();

        int totalWeight = Arrays.stream(properties).mapToInt(property -> property.worldSpawnWeight).sum();

        Random random = new Random();
        int roll = random.nextInt(totalWeight);

        int cumulativeWeight = 0;
        for (BookRarityProperties property : properties) {
            cumulativeWeight += property.worldSpawnWeight;
            if (roll < cumulativeWeight) {
                return property;
            }
        }
        throw new IllegalStateException("Failed to select a rarity based on weight.\nEnsure Rechanted config is set up properly.");
    }

    private static BookRarityProperties[] ALL_BOOK_PROPERTIES;

    public static BookRarityProperties[] getAllProperties() {
        if (ALL_BOOK_PROPERTIES != null) {
            return ALL_BOOK_PROPERTIES;
        }

        BookRarityProperties dustyProperties = new BookRarityProperties();
        BookRarityProperties simpleProperties = new BookRarityProperties();
        BookRarityProperties uniqueProperties = new BookRarityProperties();
        BookRarityProperties eliteProperties = new BookRarityProperties();
        BookRarityProperties ultimateProperties = new BookRarityProperties();
        BookRarityProperties legendaryProperties = new BookRarityProperties();

        // Binding dusty tier configs with an accessible properties class.
        // Not accessible with enchanting table, but some properties that would only apply if it was
        // are still defined for consistency's sake.
        dustyProperties.rarity = 0.0f;
        dustyProperties.key =                  RechantedCommonConfigs.RARITY_0_KEY.get();
        dustyProperties.color =                RechantedCommonConfigs.RARITY_0_COLOR.get();
        dustyProperties.requiredExp =          RechantedCommonConfigs.RARITY_0_EXP_COST.get();
        dustyProperties.worldSpawnWeight =     RechantedCommonConfigs.RARITY_0_WORLD_SPAWN_WEIGHT.get();
        dustyProperties.minSuccess =           RechantedCommonConfigs.RARITY_0_MIN_SUCCESS.get();
        dustyProperties.maxSuccess =           RechantedCommonConfigs.RARITY_0_MAX_SUCCESS.get();
        dustyProperties.forcedBookBreaks =     RechantedCommonConfigs.RARITY_0_FORCED_BOOK_BREAKS.get();
        dustyProperties.forcedFloorBreaks =    RechantedCommonConfigs.RARITY_0_FORCED_FLOOR_BREAKS.get();
        dustyProperties.bookBreakChance =      RechantedCommonConfigs.RARITY_0_BOOK_BREAK_CHANCE.get();
        dustyProperties.floorBreakChance =     RechantedCommonConfigs.RARITY_0_FLOOR_BREAK_CHANCE.get();
        dustyProperties.requiredBookShelves =  RechantedCommonConfigs.RARITY_0_REQUIRED_BOOKSHELVES.get();
        dustyProperties.requiredLapis =        RechantedCommonConfigs.RARITY_0_REQUIRED_LAPIS.get();
        dustyProperties.bonusItemRollChance =  RechantedCommonConfigs.RARITY_0_BONUS_ITEM_ROLL_CHANCE.get();
        dustyProperties.bonusItemMysteriousBookWeight = RechantedCommonConfigs.RARITY_0_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT.get();
        dustyProperties.bonusItemCommonGemPoolWeight = RechantedCommonConfigs.RARITY_0_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT.get();
        dustyProperties.bonusItemRareGemPoolWeight = RechantedCommonConfigs.RARITY_0_BONUS_ITEM_RARE_GEM_POOL_WEIGHT.get();
        dustyProperties.minGrindstoneXP =      RechantedCommonConfigs.RARITY_0_GRINDSTONE_XP_MIN.get();
        dustyProperties.maxGrindstoneXP =      RechantedCommonConfigs.RARITY_0_GRINDSTONE_XP_MAX.get();
        dustyProperties.enchantmentPool =      EnchantmentPoolEntry.listFromString(RechantedCommonConfigs.RARITY_0_ENCHANTMENTS.get());
        dustyProperties.enchantmentPoolTotalWeights = getEnchantmentPoolTotalWeight(dustyProperties.enchantmentPool);
        dustyProperties.iconResourceLocation = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/item/dusty.png");

        ResourceLocation blockLocation =  ResourceLocation.parse(RechantedCommonConfigs.RARITY_0_FLOOR_BLOCK_TYPE.get());
        dustyProperties.floorBlock = BuiltInRegistries.BLOCK.get(blockLocation);

        // Simple tier; first one accessible with enchanting table.
        simpleProperties.rarity = 1.0f;
        simpleProperties.key =                  RechantedCommonConfigs.RARITY_1_KEY.get();
        simpleProperties.color =                RechantedCommonConfigs.RARITY_1_COLOR.get();
        simpleProperties.requiredExp =          RechantedCommonConfigs.RARITY_1_EXP_COST.get();
        simpleProperties.worldSpawnWeight =     RechantedCommonConfigs.RARITY_1_WORLD_SPAWN_WEIGHT.get();
        simpleProperties.minSuccess =           RechantedCommonConfigs.RARITY_1_MIN_SUCCESS.get();
        simpleProperties.maxSuccess =           RechantedCommonConfigs.RARITY_1_MAX_SUCCESS.get();
        simpleProperties.forcedBookBreaks =     RechantedCommonConfigs.RARITY_1_FORCED_BOOK_BREAKS.get();
        simpleProperties.forcedFloorBreaks =    RechantedCommonConfigs.RARITY_1_FORCED_FLOOR_BREAKS.get();
        simpleProperties.bookBreakChance =      RechantedCommonConfigs.RARITY_1_BOOK_BREAK_CHANCE.get();
        simpleProperties.floorBreakChance =     RechantedCommonConfigs.RARITY_1_FLOOR_BREAK_CHANCE.get();
        simpleProperties.requiredBookShelves =  RechantedCommonConfigs.RARITY_1_REQUIRED_BOOKSHELVES.get();
        simpleProperties.requiredLapis =        RechantedCommonConfigs.RARITY_1_REQUIRED_LAPIS.get();
        simpleProperties.bonusItemRollChance =  RechantedCommonConfigs.RARITY_1_BONUS_ITEM_ROLL_CHANCE.get();
        simpleProperties.bonusItemMysteriousBookWeight = RechantedCommonConfigs.RARITY_1_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT.get();
        simpleProperties.bonusItemCommonGemPoolWeight = RechantedCommonConfigs.RARITY_1_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT.get();
        simpleProperties.bonusItemRareGemPoolWeight = RechantedCommonConfigs.RARITY_1_BONUS_ITEM_RARE_GEM_POOL_WEIGHT.get();
        simpleProperties.minGrindstoneXP =      RechantedCommonConfigs.RARITY_1_GRINDSTONE_XP_MIN.get();
        simpleProperties.maxGrindstoneXP =      RechantedCommonConfigs.RARITY_1_GRINDSTONE_XP_MAX.get();
        simpleProperties.enchantmentPool =      EnchantmentPoolEntry.listFromString(RechantedCommonConfigs.RARITY_1_ENCHANTMENTS.get());
        simpleProperties.enchantmentPoolTotalWeights = getEnchantmentPoolTotalWeight(simpleProperties.enchantmentPool);
        simpleProperties.iconResourceLocation = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/item/simple.png");

        blockLocation =  ResourceLocation.parse(RechantedCommonConfigs.RARITY_1_FLOOR_BLOCK_TYPE.get());
        simpleProperties.floorBlock = BuiltInRegistries.BLOCK.get(blockLocation);

        // Unique properties.
        uniqueProperties.rarity = 2.0f;
        uniqueProperties.key =                  RechantedCommonConfigs.RARITY_2_KEY.get();
        uniqueProperties.color =                RechantedCommonConfigs.RARITY_2_COLOR.get();
        uniqueProperties.requiredExp =          RechantedCommonConfigs.RARITY_2_EXP_COST.get();
        uniqueProperties.worldSpawnWeight =     RechantedCommonConfigs.RARITY_2_WORLD_SPAWN_WEIGHT.get();
        uniqueProperties.minSuccess =           RechantedCommonConfigs.RARITY_2_MIN_SUCCESS.get();
        uniqueProperties.maxSuccess =           RechantedCommonConfigs.RARITY_2_MAX_SUCCESS.get();
        uniqueProperties.forcedBookBreaks =     RechantedCommonConfigs.RARITY_2_FORCED_BOOK_BREAKS.get();
        uniqueProperties.forcedFloorBreaks =    RechantedCommonConfigs.RARITY_2_FORCED_FLOOR_BREAKS.get();
        uniqueProperties.bookBreakChance =      RechantedCommonConfigs.RARITY_2_BOOK_BREAK_CHANCE.get();
        uniqueProperties.floorBreakChance =     RechantedCommonConfigs.RARITY_2_FLOOR_BREAK_CHANCE.get();
        uniqueProperties.requiredLapis =        RechantedCommonConfigs.RARITY_2_REQUIRED_LAPIS.get();
        uniqueProperties.requiredBookShelves =  RechantedCommonConfigs.RARITY_2_REQUIRED_BOOKSHELVES.get();
        uniqueProperties.bonusItemRollChance =  RechantedCommonConfigs.RARITY_2_BONUS_ITEM_ROLL_CHANCE.get();
        uniqueProperties.bonusItemMysteriousBookWeight = RechantedCommonConfigs.RARITY_2_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT.get();
        uniqueProperties.bonusItemCommonGemPoolWeight = RechantedCommonConfigs.RARITY_2_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT.get();
        uniqueProperties.bonusItemRareGemPoolWeight = RechantedCommonConfigs.RARITY_2_BONUS_ITEM_RARE_GEM_POOL_WEIGHT.get();
        uniqueProperties.minGrindstoneXP =      RechantedCommonConfigs.RARITY_2_GRINDSTONE_XP_MIN.get();
        uniqueProperties.maxGrindstoneXP =      RechantedCommonConfigs.RARITY_2_GRINDSTONE_XP_MAX.get();
        uniqueProperties.enchantmentPool =      EnchantmentPoolEntry.listFromString(RechantedCommonConfigs.RARITY_2_ENCHANTMENTS.get());
        uniqueProperties.enchantmentPoolTotalWeights = getEnchantmentPoolTotalWeight(uniqueProperties.enchantmentPool);
        uniqueProperties.iconResourceLocation = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/item/unique.png");



        blockLocation = ResourceLocation.parse(RechantedCommonConfigs.RARITY_2_FLOOR_BLOCK_TYPE.get());
        uniqueProperties.floorBlock = BuiltInRegistries.BLOCK.get(blockLocation);

        // Elite properties.
        eliteProperties.rarity = 3.0f;
        eliteProperties.key =                  RechantedCommonConfigs.RARITY_3_KEY.get();
        eliteProperties.color =                RechantedCommonConfigs.RARITY_3_COLOR.get();
        eliteProperties.requiredExp =          RechantedCommonConfigs.RARITY_3_EXP_COST.get();
        eliteProperties.worldSpawnWeight =     RechantedCommonConfigs.RARITY_3_WORLD_SPAWN_WEIGHT.get();
        eliteProperties.minSuccess =           RechantedCommonConfigs.RARITY_3_MIN_SUCCESS.get();
        eliteProperties.maxSuccess =           RechantedCommonConfigs.RARITY_3_MAX_SUCCESS.get();
        eliteProperties.forcedBookBreaks =     RechantedCommonConfigs.RARITY_3_FORCED_BOOK_BREAKS.get();
        eliteProperties.forcedFloorBreaks =    RechantedCommonConfigs.RARITY_3_FORCED_FLOOR_BREAKS.get();
        eliteProperties.bookBreakChance =      RechantedCommonConfigs.RARITY_3_BOOK_BREAK_CHANCE.get();
        eliteProperties.floorBreakChance =     RechantedCommonConfigs.RARITY_3_FLOOR_BREAK_CHANCE.get();
        eliteProperties.requiredBookShelves =  RechantedCommonConfigs.RARITY_3_REQUIRED_BOOKSHELVES.get();
        eliteProperties.requiredLapis =        RechantedCommonConfigs.RARITY_3_REQUIRED_LAPIS.get();
        eliteProperties.bonusItemRollChance =  RechantedCommonConfigs.RARITY_3_BONUS_ITEM_ROLL_CHANCE.get();
        eliteProperties.bonusItemMysteriousBookWeight = RechantedCommonConfigs.RARITY_3_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT.get();
        eliteProperties.bonusItemCommonGemPoolWeight = RechantedCommonConfigs.RARITY_3_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT.get();
        eliteProperties.bonusItemRareGemPoolWeight = RechantedCommonConfigs.RARITY_3_BONUS_ITEM_RARE_GEM_POOL_WEIGHT.get();
        eliteProperties.minGrindstoneXP =      RechantedCommonConfigs.RARITY_3_GRINDSTONE_XP_MIN.get();
        eliteProperties.maxGrindstoneXP =      RechantedCommonConfigs.RARITY_3_GRINDSTONE_XP_MAX.get();
        eliteProperties.enchantmentPool =      EnchantmentPoolEntry.listFromString(RechantedCommonConfigs.RARITY_3_ENCHANTMENTS.get());
        eliteProperties.enchantmentPoolTotalWeights = getEnchantmentPoolTotalWeight(eliteProperties.enchantmentPool);
        eliteProperties.iconResourceLocation = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/item/elite.png");

        blockLocation = ResourceLocation.parse(RechantedCommonConfigs.RARITY_3_FLOOR_BLOCK_TYPE.get());
        eliteProperties.floorBlock = BuiltInRegistries.BLOCK.get(blockLocation);

        // Ultimate properties.
        ultimateProperties.rarity = 4.0f;
        ultimateProperties.key =                  RechantedCommonConfigs.RARITY_4_KEY.get();
        ultimateProperties.color =                RechantedCommonConfigs.RARITY_4_COLOR.get();
        ultimateProperties.requiredExp =          RechantedCommonConfigs.RARITY_4_EXP_COST.get();
        ultimateProperties.worldSpawnWeight =     RechantedCommonConfigs.RARITY_4_WORLD_SPAWN_WEIGHT.get();
        ultimateProperties.minSuccess =           RechantedCommonConfigs.RARITY_4_MIN_SUCCESS.get();
        ultimateProperties.maxSuccess =           RechantedCommonConfigs.RARITY_4_MAX_SUCCESS.get();
        ultimateProperties.forcedBookBreaks =     RechantedCommonConfigs.RARITY_4_FORCED_BOOK_BREAKS.get();
        ultimateProperties.forcedFloorBreaks =    RechantedCommonConfigs.RARITY_4_FORCED_FLOOR_BREAKS.get();
        ultimateProperties.bookBreakChance =      RechantedCommonConfigs.RARITY_4_BOOK_BREAK_CHANCE.get();
        ultimateProperties.floorBreakChance =     RechantedCommonConfigs.RARITY_4_FLOOR_BREAK_CHANCE.get();
        ultimateProperties.requiredBookShelves =  RechantedCommonConfigs.RARITY_4_REQUIRED_BOOKSHELVES.get();
        ultimateProperties.requiredLapis =        RechantedCommonConfigs.RARITY_4_REQUIRED_LAPIS.get();
        ultimateProperties.bonusItemRollChance =  RechantedCommonConfigs.RARITY_4_BONUS_ITEM_ROLL_CHANCE.get();
        ultimateProperties.bonusItemMysteriousBookWeight = RechantedCommonConfigs.RARITY_4_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT.get();
        ultimateProperties.bonusItemCommonGemPoolWeight = RechantedCommonConfigs.RARITY_4_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT.get();
        ultimateProperties.bonusItemRareGemPoolWeight = RechantedCommonConfigs.RARITY_4_BONUS_ITEM_RARE_GEM_POOL_WEIGHT.get();
        ultimateProperties.minGrindstoneXP =      RechantedCommonConfigs.RARITY_4_GRINDSTONE_XP_MIN.get();
        ultimateProperties.maxGrindstoneXP =      RechantedCommonConfigs.RARITY_4_GRINDSTONE_XP_MAX.get();
        ultimateProperties.enchantmentPool =      EnchantmentPoolEntry.listFromString(RechantedCommonConfigs.RARITY_4_ENCHANTMENTS.get());
        ultimateProperties.enchantmentPoolTotalWeights = getEnchantmentPoolTotalWeight(ultimateProperties.enchantmentPool);
        ultimateProperties.iconResourceLocation = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/item/ultimate.png");


        blockLocation = ResourceLocation.parse(RechantedCommonConfigs.RARITY_4_FLOOR_BLOCK_TYPE.get());
        ultimateProperties.floorBlock =  BuiltInRegistries.BLOCK.get(blockLocation);

        // Legendary properties.
        legendaryProperties.rarity = 5.0f;
        legendaryProperties.key =                  RechantedCommonConfigs.RARITY_5_KEY.get();
        legendaryProperties.color =                RechantedCommonConfigs.RARITY_5_COLOR.get();
        legendaryProperties.requiredExp =          RechantedCommonConfigs.RARITY_5_EXP_COST.get();
        legendaryProperties.worldSpawnWeight =     RechantedCommonConfigs.RARITY_5_WORLD_SPAWN_WEIGHT.get();
        legendaryProperties.minSuccess =           RechantedCommonConfigs.RARITY_5_MIN_SUCCESS.get();
        legendaryProperties.maxSuccess =           RechantedCommonConfigs.RARITY_5_MAX_SUCCESS.get();
        legendaryProperties.forcedBookBreaks =     RechantedCommonConfigs.RARITY_5_FORCED_BOOK_BREAKS.get();
        legendaryProperties.forcedFloorBreaks =    RechantedCommonConfigs.RARITY_5_FORCED_FLOOR_BREAKS.get();
        legendaryProperties.bookBreakChance =      RechantedCommonConfigs.RARITY_5_BOOK_BREAK_CHANCE.get();
        legendaryProperties.floorBreakChance =     RechantedCommonConfigs.RARITY_5_FLOOR_BREAK_CHANCE.get();
        legendaryProperties.requiredBookShelves =  RechantedCommonConfigs.RARITY_5_REQUIRED_BOOKSHELVES.get();
        legendaryProperties.requiredLapis =        RechantedCommonConfigs.RARITY_5_REQUIRED_LAPIS.get();
        legendaryProperties.bonusItemRollChance =  RechantedCommonConfigs.RARITY_5_BONUS_ITEM_ROLL_CHANCE.get();
        legendaryProperties.bonusItemMysteriousBookWeight = RechantedCommonConfigs.RARITY_5_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT.get();
        legendaryProperties.bonusItemCommonGemPoolWeight = RechantedCommonConfigs.RARITY_5_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT.get();
        legendaryProperties.bonusItemRareGemPoolWeight = RechantedCommonConfigs.RARITY_5_BONUS_ITEM_RARE_GEM_POOL_WEIGHT.get();
        legendaryProperties.minGrindstoneXP =      RechantedCommonConfigs.RARITY_5_GRINDSTONE_XP_MIN.get();
        legendaryProperties.maxGrindstoneXP =      RechantedCommonConfigs.RARITY_5_GRINDSTONE_XP_MAX.get();
        legendaryProperties.enchantmentPool =      EnchantmentPoolEntry.listFromString(RechantedCommonConfigs.RARITY_5_ENCHANTMENTS.get());
        legendaryProperties.enchantmentPoolTotalWeights = getEnchantmentPoolTotalWeight(legendaryProperties.enchantmentPool);
        legendaryProperties.iconResourceLocation = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/item/legendary.png");

        blockLocation = ResourceLocation.parse(RechantedCommonConfigs.RARITY_5_FLOOR_BLOCK_TYPE.get());
        legendaryProperties.floorBlock =  BuiltInRegistries.BLOCK.get(blockLocation);

        // Now add them all to hardcoded list.
        ALL_BOOK_PROPERTIES = new BookRarityProperties[6];
        ALL_BOOK_PROPERTIES[0] = dustyProperties;
        ALL_BOOK_PROPERTIES[1] = simpleProperties;
        ALL_BOOK_PROPERTIES[2] = uniqueProperties;
        ALL_BOOK_PROPERTIES[3] = eliteProperties;
        ALL_BOOK_PROPERTIES[4] = ultimateProperties;
        ALL_BOOK_PROPERTIES[5] = legendaryProperties;

        return ALL_BOOK_PROPERTIES;
    }
}
