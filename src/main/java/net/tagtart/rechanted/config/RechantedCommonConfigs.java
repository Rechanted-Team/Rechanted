package net.tagtart.rechanted.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class RechantedCommonConfigs {

        public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        public static final ModConfigSpec SPEC;

        // Dusty tier configs.
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_0_KEY;
        public static final ModConfigSpec.IntValue RARITY_0_COLOR;
        public static final ModConfigSpec.IntValue RARITY_0_EXP_COST;
        public static final ModConfigSpec.IntValue RARITY_0_WORLD_SPAWN_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_0_MIN_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_0_MAX_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_0_FORCED_BOOK_BREAKS;
        public static final ModConfigSpec.IntValue RARITY_0_FORCED_FLOOR_BREAKS;
        public static final ModConfigSpec.DoubleValue RARITY_0_BOOK_BREAK_CHANCE;
        public static final ModConfigSpec.DoubleValue RARITY_0_FLOOR_BREAK_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_0_REQUIRED_BOOKSHELVES;
        public static final ModConfigSpec.IntValue RARITY_0_REQUIRED_LAPIS;
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_0_FLOOR_BLOCK_TYPE;
        public static final ModConfigSpec.DoubleValue RARITY_0_BONUS_ITEM_ROLL_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_0_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_0_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_0_BONUS_ITEM_RARE_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_0_GRINDSTONE_XP_MIN;
        public static final ModConfigSpec.IntValue RARITY_0_GRINDSTONE_XP_MAX;
        public static final ModConfigSpec.ConfigValue<List<? extends String>> RARITY_0_ENCHANTMENTS;

        // Chance for dusty book to drop from fishing loot pool.
        public static final ModConfigSpec.DoubleValue RARITY_0_FISHING_JUNK_DROP_CHANCE;

        // Simple tier configs.
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_1_KEY;
        public static final ModConfigSpec.IntValue RARITY_1_COLOR;
        public static final ModConfigSpec.IntValue RARITY_1_EXP_COST;
        public static final ModConfigSpec.IntValue RARITY_1_WORLD_SPAWN_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_1_MIN_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_1_MAX_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_1_FORCED_BOOK_BREAKS;
        public static final ModConfigSpec.IntValue RARITY_1_FORCED_FLOOR_BREAKS;
        public static final ModConfigSpec.DoubleValue RARITY_1_BOOK_BREAK_CHANCE;
        public static final ModConfigSpec.DoubleValue RARITY_1_FLOOR_BREAK_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_1_REQUIRED_BOOKSHELVES;
        public static final ModConfigSpec.IntValue RARITY_1_REQUIRED_LAPIS;
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_1_FLOOR_BLOCK_TYPE;
        public static final ModConfigSpec.DoubleValue RARITY_1_BONUS_ITEM_ROLL_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_1_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_1_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_1_BONUS_ITEM_RARE_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_1_GRINDSTONE_XP_MIN;
        public static final ModConfigSpec.IntValue RARITY_1_GRINDSTONE_XP_MAX;
        public static final ModConfigSpec.ConfigValue<List<? extends String>> RARITY_1_ENCHANTMENTS;

        // Unique tier configs.
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_2_KEY;
        public static final ModConfigSpec.IntValue RARITY_2_COLOR;
        public static final ModConfigSpec.IntValue RARITY_2_EXP_COST;
        public static final ModConfigSpec.IntValue RARITY_2_WORLD_SPAWN_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_2_MIN_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_2_MAX_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_2_FORCED_BOOK_BREAKS;
        public static final ModConfigSpec.IntValue RARITY_2_FORCED_FLOOR_BREAKS;
        public static final ModConfigSpec.DoubleValue RARITY_2_BOOK_BREAK_CHANCE;
        public static final ModConfigSpec.DoubleValue RARITY_2_FLOOR_BREAK_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_2_REQUIRED_BOOKSHELVES;
        public static final ModConfigSpec.IntValue RARITY_2_REQUIRED_LAPIS;
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_2_FLOOR_BLOCK_TYPE;
        public static final ModConfigSpec.DoubleValue RARITY_2_BONUS_ITEM_ROLL_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_2_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_2_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_2_BONUS_ITEM_RARE_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_2_GRINDSTONE_XP_MIN;
        public static final ModConfigSpec.IntValue RARITY_2_GRINDSTONE_XP_MAX;
        public static final ModConfigSpec.ConfigValue<List<? extends String>> RARITY_2_ENCHANTMENTS;

        // Elite tier configs.
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_3_KEY;
        public static final ModConfigSpec.IntValue RARITY_3_COLOR;
        public static final ModConfigSpec.IntValue RARITY_3_EXP_COST;
        public static final ModConfigSpec.IntValue RARITY_3_WORLD_SPAWN_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_3_MIN_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_3_MAX_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_3_FORCED_BOOK_BREAKS;
        public static final ModConfigSpec.IntValue RARITY_3_FORCED_FLOOR_BREAKS;
        public static final ModConfigSpec.DoubleValue RARITY_3_BOOK_BREAK_CHANCE;
        public static final ModConfigSpec.DoubleValue RARITY_3_FLOOR_BREAK_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_3_REQUIRED_BOOKSHELVES;
        public static final ModConfigSpec.IntValue RARITY_3_REQUIRED_LAPIS;
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_3_FLOOR_BLOCK_TYPE;
        public static final ModConfigSpec.DoubleValue RARITY_3_BONUS_ITEM_ROLL_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_3_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_3_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_3_BONUS_ITEM_RARE_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_3_GRINDSTONE_XP_MIN;
        public static final ModConfigSpec.IntValue RARITY_3_GRINDSTONE_XP_MAX;
        public static final ModConfigSpec.ConfigValue<List<? extends String>> RARITY_3_ENCHANTMENTS;

        // Ultimate tier configs.
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_4_KEY;
        public static final ModConfigSpec.IntValue RARITY_4_COLOR;
        public static final ModConfigSpec.IntValue RARITY_4_EXP_COST;
        public static final ModConfigSpec.IntValue RARITY_4_WORLD_SPAWN_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_4_MIN_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_4_MAX_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_4_FORCED_BOOK_BREAKS;
        public static final ModConfigSpec.IntValue RARITY_4_FORCED_FLOOR_BREAKS;
        public static final ModConfigSpec.DoubleValue RARITY_4_BOOK_BREAK_CHANCE;
        public static final ModConfigSpec.DoubleValue RARITY_4_FLOOR_BREAK_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_4_REQUIRED_BOOKSHELVES;
        public static final ModConfigSpec.IntValue RARITY_4_REQUIRED_LAPIS;
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_4_FLOOR_BLOCK_TYPE;
        public static final ModConfigSpec.DoubleValue RARITY_4_BONUS_ITEM_ROLL_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_4_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_4_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_4_BONUS_ITEM_RARE_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_4_GRINDSTONE_XP_MIN;
        public static final ModConfigSpec.IntValue RARITY_4_GRINDSTONE_XP_MAX;
        public static final ModConfigSpec.ConfigValue<List<? extends String>> RARITY_4_ENCHANTMENTS;

        // Legendary tier configs.
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_5_KEY;
        public static final ModConfigSpec.IntValue RARITY_5_COLOR;
        public static final ModConfigSpec.IntValue RARITY_5_EXP_COST;
        public static final ModConfigSpec.IntValue RARITY_5_WORLD_SPAWN_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_5_MIN_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_5_MAX_SUCCESS;
        public static final ModConfigSpec.IntValue RARITY_5_FORCED_BOOK_BREAKS;
        public static final ModConfigSpec.IntValue RARITY_5_FORCED_FLOOR_BREAKS;
        public static final ModConfigSpec.DoubleValue RARITY_5_BOOK_BREAK_CHANCE;
        public static final ModConfigSpec.DoubleValue RARITY_5_FLOOR_BREAK_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_5_REQUIRED_BOOKSHELVES;
        public static final ModConfigSpec.IntValue RARITY_5_REQUIRED_LAPIS;
        public static final ModConfigSpec.ConfigValue<? extends String> RARITY_5_FLOOR_BLOCK_TYPE;
        public static final ModConfigSpec.DoubleValue RARITY_5_BONUS_ITEM_ROLL_CHANCE;
        public static final ModConfigSpec.IntValue RARITY_5_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_5_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_5_BONUS_ITEM_RARE_GEM_POOL_WEIGHT;
        public static final ModConfigSpec.IntValue RARITY_5_GRINDSTONE_XP_MIN;
        public static final ModConfigSpec.IntValue RARITY_5_GRINDSTONE_XP_MAX;
        public static final ModConfigSpec.ConfigValue<List<? extends String>> RARITY_5_ENCHANTMENTS;

        public static final ModConfigSpec.ConfigValue<List<? extends String>> COMPATIBLE_TOOLTIP_ITEMS_LIST;
        public static final ModConfigSpec.ConfigValue<List<? extends String>> VALID_ENCHANTING_POWER_BLOCKS;
        public static final ModConfigSpec.ConfigValue<? extends String> GRINDSTONE_RESULT_ITEM;
        public static final ModConfigSpec.ConfigValue<List<? extends String>> COMMON_GEM_POOL;
        public static final ModConfigSpec.ConfigValue<List<? extends String>> RARE_GEM_POOL;

        public static final ModConfigSpec.ConfigValue<List<? extends String>> ANNOUNCEMENT_ENCHANTMENTS;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> ANNOUNCE_CHANCE_GEM;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> ANNOUNCE_SHINY_CHANCE_GEM;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> ANNOUNCE_RETURN_GEM;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> ANNOUNCE_TASTY_GEM;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> ANNOUNCE_WARP_GEM;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> ANNOUNCE_LUCKY_GEM;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> ANNOUNCE_CLONE_GEM;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> ANNOUNCE_SMITHING_GEM;

        public static final ModConfigSpec.ConfigValue<? extends Boolean> REMOVE_MENDING_ENABLED;

        public static final ModConfigSpec.ConfigValue<? extends Boolean> REPLACE_ENCHANTED_LOOT;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> EXCLUDE_LOWER_TIER_LOOT;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> NERF_FISHING_LOOT;
        public static final ModConfigSpec.DoubleValue WORLD_LOOT_GEM_DROP_CHANCE;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> MODIFY_VILLAGER_TRADES;
        public static final ModConfigSpec.IntValue VILLAGER_MYSTERIOUS_BOOK_EMERALD_COST;
        public static final ModConfigSpec.IntValue VILLAGER_DUSTY_MYSTERIOUS_BOOK_EMERALD_COST;
        // Fortune nerf configs
        public static final ModConfigSpec.ConfigValue<? extends Boolean> FORTUNE_NERF_ENABLED;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> DISABLE_XP_FROM_SLIMES_AND_SILVERFISH;
        public static final ModConfigSpec.ConfigValue<? extends Boolean> GEAR_BREAK_WARNING_ENABLED;
        public static final ModConfigSpec.DoubleValue FORTUNE_1_CHANCE;
        public static final ModConfigSpec.DoubleValue FORTUNE_2_CHANCE;
        public static final ModConfigSpec.DoubleValue FORTUNE_3_CHANCE;

        // Misc. settings
        public static final ModConfigSpec.ConfigValue<? extends Boolean> USE_GREYSCALE_GUI;

        static {

                // Simple rarity builder default config
                BUILDER.translation("config.rechanted.book_rarities.title").comment("Configs for Each Book Rarity")
                                .push("Book Rarities");

                // Dusty rarity properties; not accessible from enchanting table by default, but still here
                // for consistency or if someone wants to swap a tier out for it.
                BUILDER.translation("config.rechanted.dusty.name").push("Dusty");
                BUILDER.comment("This rarity is not accessible from the enchanter, but has the same configs for consistency.",
                        "Tiers obtainable in the enchanter start at Simple tier.");
                RARITY_0_KEY = BUILDER.translation("config.rechanted.dusty.key.title").define("key", "dusty");
                RARITY_0_COLOR = BUILDER.translation("config.rechanted.dusty.color.title").defineInRange("color",
                        3881787, 0, Integer.MAX_VALUE);
                RARITY_0_EXP_COST = BUILDER.translation("config.rechanted.dusty.exp_cost.title")
                        .defineInRange("exp_cost", 75, 0, Integer.MAX_VALUE);
                RARITY_0_WORLD_SPAWN_WEIGHT = BUILDER.translation("config.rechanted.dusty.world_spawn_weight.title")
                        .defineInRange("world_spawn_weight", 0, 0, Integer.MAX_VALUE);
                RARITY_0_MIN_SUCCESS = BUILDER.translation("config.rechanted.dusty.min_success.title")
                        .defineInRange("min_success", 45, 0, 100);
                RARITY_0_MAX_SUCCESS = BUILDER.translation("config.rechanted.dusty.max_success.title")
                        .defineInRange("max_success", 100, 0, 100);
                RARITY_0_FORCED_BOOK_BREAKS = BUILDER
                        .translation("config.rechanted.dusty.guaranteed_bookshelf_breaks.title")
                        .defineInRange("guaranteed_bookshelf_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_0_FORCED_FLOOR_BREAKS = BUILDER
                        .translation("config.rechanted.dusty.guaranteed_floor_breaks.title")
                        .defineInRange("guaranteed_floor_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_0_BOOK_BREAK_CHANCE = BUILDER.translation("config.rechanted.dusty.book_break_chance.title")
                        .defineInRange("book_break_chance", 0.0, 0.0, 1.0);
                RARITY_0_FLOOR_BREAK_CHANCE = BUILDER.translation("config.rechanted.dusty.floor_break_chance.title")
                        .defineInRange("floor_break_chance", 0.05, 0.0, 1.0);
                RARITY_0_REQUIRED_BOOKSHELVES = BUILDER
                        .translation("config.rechanted.dusty.required_bookshelves.title")
                        .defineInRange("required_bookshelves", 3, 0, Integer.MAX_VALUE);
                RARITY_0_REQUIRED_LAPIS = BUILDER.translation("config.rechanted.dusty.required_lapis.title")
                        .defineInRange("required_lapis", 1, 0, 64);
                BUILDER.comment("Floor block required to target this tier in the enchanting table. It doesn't do anything for Dusty tier and is only here for consistency.");
                RARITY_0_FLOOR_BLOCK_TYPE = BUILDER.translation("config.rechanted.dusty.floor_block_type.title")
                        .define("floor_block_type", "minecraft:bedrock");
                RARITY_0_BONUS_ITEM_ROLL_CHANCE = BUILDER
                        .translation("config.rechanted.dusty.bonus_item_roll_chance.title")
                        .defineInRange("bonus_item_roll_chance", 0.085, 0.0, 1.0);
                RARITY_0_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT = BUILDER
                        .translation("config.rechanted.dusty.bonus_item_mysterious_book_weight.title")
                        .defineInRange("bonus_item_mysterious_book_weight", 60, 0, Integer.MAX_VALUE);
                RARITY_0_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT = BUILDER
                        .translation("config.rechanted.dusty.bonus_item_common_gem_pool_weight.title")
                        .defineInRange("bonus_item_common_gem_pool_weight", 40, 0, Integer.MAX_VALUE);
                RARITY_0_BONUS_ITEM_RARE_GEM_POOL_WEIGHT = BUILDER
                        .translation("config.rechanted.dusty.bonus_item_rare_gem_pool_weight.title")
                        .defineInRange("bonus_item_rare_gem_pool_weight", 10, 0, Integer.MAX_VALUE);

                BUILDER.comment(
                        "Minimum and maximum amount of xp orbs this tier provides when put into a grindstone",
                        "Make sure min is less than max, or problems may occur");
                RARITY_0_GRINDSTONE_XP_MIN = BUILDER.translation("config.rechanted.simple.grindstone_xp_min.title")
                        .defineInRange("grindstone_xp_min", 2, 0, Integer.MAX_VALUE);
                RARITY_0_GRINDSTONE_XP_MAX = BUILDER.translation("config.rechanted.simple.grindstone_xp_max.title")
                        .defineInRange("grindstone_xp_max", 6, 0, Integer.MAX_VALUE);
                BUILDER.comment(
                        "List of potential enchantments with weights, levels, and per-level weights.",
                        "Format: <enchantment>|<weight>|<level-range>|<level-weights>",
                        "Example: minecraft:unbreaking|10|1-3|1,2,3");
                ArrayList<String> rarity_0_default_enchantments = new ArrayList<>();
                rarity_0_default_enchantments.add("minecraft:bane_of_arthropods|1|1-4|3,3,2,1");
                rarity_0_default_enchantments.add("minecraft:piercing|1|1-4|3,3,2,1");
                rarity_0_default_enchantments.add("minecraft:quick_charge|1|1-3|3,2,1");
                rarity_0_default_enchantments.add("minecraft:multishot|1|1|1");
                rarity_0_default_enchantments.add("minecraft:impaling|1|1-5|2,2,2,1,1");
                rarity_0_default_enchantments.add("minecraft:riptide|1|1-3|2,2,1");
                rarity_0_default_enchantments.add("minecraft:channeling|1|1|1");
                rarity_0_default_enchantments.add("minecraft:loyalty|1|1-3|2,2,1");

                RARITY_0_ENCHANTMENTS = BUILDER.translation("config.rechanted.dusty.enchantments.title").defineList(
                        "enchantments", rarity_0_default_enchantments,
                        () -> "minecraft:sharpness|1|1-4|3,3,2,1", s -> s instanceof String);

                RARITY_0_FISHING_JUNK_DROP_CHANCE = BUILDER.translation("config.rechanted.dusty.fishing_junk_drop_chance.title")
                        .defineInRange("fishing_junk_drop_chance", 0.075, 0.0, 1.0);

                BUILDER.pop();

                // Simple rarity properties.
                BUILDER.translation("config.rechanted.simple.name").push("Simple");
                RARITY_1_KEY = BUILDER.translation("config.rechanted.simple.key.title").define("key", "simple");
                RARITY_1_COLOR = BUILDER.translation("config.rechanted.simple.color.title").defineInRange("color",
                               11184810, 0, Integer.MAX_VALUE);
                RARITY_1_EXP_COST = BUILDER.translation("config.rechanted.simple.exp_cost.title")
                               .defineInRange("exp_cost", 75, 0, Integer.MAX_VALUE);
                RARITY_1_WORLD_SPAWN_WEIGHT = BUILDER.translation("config.rechanted.simple.world_spawn_weight.title")
                               .defineInRange("world_spawn_weight", 50, 0, Integer.MAX_VALUE);
                RARITY_1_MIN_SUCCESS = BUILDER.translation("config.rechanted.simple.min_success.title")
                              .defineInRange("min_success", 45, 0, 100);
                RARITY_1_MAX_SUCCESS = BUILDER.translation("config.rechanted.simple.max_success.title")
                               .defineInRange("max_success", 100, 0, 100);
                RARITY_1_FORCED_BOOK_BREAKS = BUILDER
                               .translation("config.rechanted.simple.guaranteed_bookshelf_breaks.title")
                               .defineInRange("guaranteed_bookshelf_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_1_FORCED_FLOOR_BREAKS = BUILDER
                               .translation("config.rechanted.simple.guaranteed_floor_breaks.title")
                               .defineInRange("guaranteed_floor_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_1_BOOK_BREAK_CHANCE = BUILDER.translation("config.rechanted.simple.book_break_chance.title")
                               .defineInRange("book_break_chance", 0.0, 0.0, 1.0);
                RARITY_1_FLOOR_BREAK_CHANCE = BUILDER.translation("config.rechanted.simple.floor_break_chance.title")
                               .defineInRange("floor_break_chance", 0.05, 0.0, 1.0);
                RARITY_1_REQUIRED_BOOKSHELVES = BUILDER
                               .translation("config.rechanted.simple.required_bookshelves.title")
                               .defineInRange("required_bookshelves", 4, 0, Integer.MAX_VALUE);
                RARITY_1_REQUIRED_LAPIS = BUILDER.translation("config.rechanted.simple.required_lapis.title")
                               .defineInRange("required_lapis", 2, 0, 64);
                BUILDER.comment("Floor block required to target this tier in the enchanting table.");
                RARITY_1_FLOOR_BLOCK_TYPE = BUILDER.translation("config.rechanted.simple.floor_block_type.title")
                               .define("floor_block_type", "minecraft:iron_block");
                RARITY_1_BONUS_ITEM_ROLL_CHANCE = BUILDER
                               .translation("config.rechanted.simple.bonus_item_roll_chance.title")
                               .defineInRange("bonus_item_roll_chance", 0.085, 0.0, 1.0);
                RARITY_1_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT = BUILDER
                               .translation("config.rechanted.simple.bonus_item_mysterious_book_weight.title")
                               .defineInRange("bonus_item_mysterious_book_weight", 60, 0, Integer.MAX_VALUE);
                RARITY_1_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT = BUILDER
                               .translation("config.rechanted.simple.bonus_item_common_gem_pool_weight.title")
                               .defineInRange("bonus_item_common_gem_pool_weight", 40, 0, Integer.MAX_VALUE);
                RARITY_1_BONUS_ITEM_RARE_GEM_POOL_WEIGHT = BUILDER
                                .translation("config.rechanted.simple.bonus_item_rare_gem_pool_weight.title")
                                .defineInRange("bonus_item_rare_gem_pool_weight", 10, 0, Integer.MAX_VALUE);

                BUILDER.comment(
                                "Minimum and maximum amount of xp orbs this tier provides when put into a grindstone",
                                "Make sure min is less than max, or problems may occur");
                RARITY_1_GRINDSTONE_XP_MIN = BUILDER.translation("config.rechanted.simple.grindstone_xp_min.title")
                                .defineInRange("grindstone_xp_min", 7, 0, Integer.MAX_VALUE);
                RARITY_1_GRINDSTONE_XP_MAX = BUILDER.translation("config.rechanted.simple.grindstone_xp_max.title")
                                .defineInRange("grindstone_xp_max", 20, 0, Integer.MAX_VALUE);
                BUILDER.comment(
                                "List of potential enchantments with weights, levels, and per-level weights.",
                                "Format: <enchantment>|<weight>|<level-range>|<level-weights>",
                                "Example: minecraft:unbreaking|10|1-3|1,2,3");
                ArrayList<String> rarity_1_default_enchantments = new ArrayList<>();
                rarity_1_default_enchantments.add("minecraft:sharpness|3|1-4|3,3,1,1");
                rarity_1_default_enchantments.add("minecraft:efficiency|3|2-4|1,1,1");
                rarity_1_default_enchantments.add("minecraft:protection|3|1-4|3,3,2,1");
                rarity_1_default_enchantments.add("minecraft:knockback|2|1-2|3,2");
                rarity_1_default_enchantments.add("minecraft:power|2|1-4|4,3,2,1");
                rarity_1_default_enchantments.add("minecraft:smite|2|1-4|3,3,2,1");
                rarity_1_default_enchantments.add("minecraft:lure|1|1-3|3,2,1");
                rarity_1_default_enchantments.add("minecraft:aqua_affinity|1|1|1");
                rarity_1_default_enchantments.add("minecraft:punch|1|1-2|3,1");
                rarity_1_default_enchantments.add("minecraft:density|1|1-4|2,2,1,1");

                RARITY_1_ENCHANTMENTS = BUILDER.translation("config.rechanted.simple.enchantments.title").defineList(
                                "enchantments", rarity_1_default_enchantments,
                                () -> "minecraft:sharpness|1|1-4|3,3,2,1", s -> s instanceof String);
                BUILDER.pop();
                // Unique rarity builder default config
                BUILDER.translation("config.rechanted.unique.name").comment("Configs for Unique Rarity Enchantments")
                                .push("unique");
                RARITY_2_KEY = BUILDER.translation("config.rechanted.unique.key.title").define("key", "unique");
                RARITY_2_COLOR = BUILDER.translation("config.rechanted.unique.color.title").defineInRange("color",
                                5635925, 0, Integer.MAX_VALUE);
                RARITY_2_EXP_COST = BUILDER.translation("config.rechanted.unique.exp_cost.title")
                                .defineInRange("exp_cost", 100, 0, Integer.MAX_VALUE);
                RARITY_2_WORLD_SPAWN_WEIGHT = BUILDER.translation("config.rechanted.unique.world_spawn_weight.title")
                                .defineInRange("world_spawn_weight", 40, 0, Integer.MAX_VALUE);
                RARITY_2_MIN_SUCCESS = BUILDER.translation("config.rechanted.unique.min_success.title")
                                .defineInRange("min_success", 45, 0, 100);
                RARITY_2_MAX_SUCCESS = BUILDER.translation("config.rechanted.unique.max_success.title")
                                .defineInRange("max_success", 100, 0, 100);
                RARITY_2_FORCED_BOOK_BREAKS = BUILDER
                                .translation("config.rechanted.unique.guaranteed_bookshelf_breaks.title")
                                .defineInRange("guaranteed_bookshelf_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_2_FORCED_FLOOR_BREAKS = BUILDER
                                .translation("config.rechanted.unique.guaranteed_floor_breaks.title")
                                .defineInRange("guaranteed_floor_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_2_BOOK_BREAK_CHANCE = BUILDER.translation("config.rechanted.unique.book_break_chance.title")
                                .defineInRange("book_break_chance", 0.0, 0.0, 1.0);
                RARITY_2_FLOOR_BREAK_CHANCE = BUILDER.translation("config.rechanted.unique.floor_break_chance.title")
                                .defineInRange("floor_break_chance", 0.045, 0.0, 1.0);
                RARITY_2_REQUIRED_BOOKSHELVES = BUILDER
                                .translation("config.rechanted.unique.required_bookshelves.title")
                                .defineInRange("required_bookshelves", 8, 0, Integer.MAX_VALUE);
                RARITY_2_REQUIRED_LAPIS = BUILDER.translation("config.rechanted.unique.required_lapis.title")
                                .defineInRange("required_lapis", 3, 0, 64);
                RARITY_2_FLOOR_BLOCK_TYPE = BUILDER.translation("config.rechanted.unique.floor_block_type.title")
                                .define("floor_block_type", "minecraft:gold_block");
                RARITY_2_BONUS_ITEM_ROLL_CHANCE = BUILDER
                                .translation("config.rechanted.unique.bonus_item_roll_chance.title")
                                .defineInRange("bonus_item_roll_chance", 0.085, 0.0, 1.0);
                RARITY_2_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT = BUILDER
                                .translation("config.rechanted.unique.bonus_item_mysterious_book_weight.title")
                                .defineInRange("bonus_item_mysterious_book_weight", 60, 0, Integer.MAX_VALUE);
                RARITY_2_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT = BUILDER
                                .translation("config.rechanted.unique.bonus_item_common_gem_pool_weight.title")
                                .defineInRange("bonus_item_common_gem_pool_weight", 40, 0, Integer.MAX_VALUE);
                RARITY_2_BONUS_ITEM_RARE_GEM_POOL_WEIGHT = BUILDER
                                .translation("config.rechanted.unique.bonus_item_rare_gem_pool_weight.title")
                                .defineInRange("bonus_item_rare_gem_pool_weight", 12, 0, Integer.MAX_VALUE);
                RARITY_2_GRINDSTONE_XP_MIN = BUILDER.translation("config.rechanted.unique.grindstone_xp_min.title")
                                .defineInRange("grindstone_xp_min", 10, 0, Integer.MAX_VALUE);
                RARITY_2_GRINDSTONE_XP_MAX = BUILDER.translation("config.rechanted.unique.grindstone_xp_max.title")
                                .defineInRange("grindstone_xp_max", 30, 0, Integer.MAX_VALUE);
                ArrayList<String> rarity_2_default_enchantments = new ArrayList<>();
                rarity_2_default_enchantments.add("minecraft:projectile_protection|2|2-4|2,1,1");
                rarity_2_default_enchantments.add("minecraft:fire_protection|2|1-4|2,2,1,1");
                rarity_2_default_enchantments.add("minecraft:blast_protection|2|2-4|3,2,1");
                rarity_2_default_enchantments.add("minecraft:feather_falling|2|1-4|3,3,2,1");
                rarity_2_default_enchantments.add("rechanted:bash|2|1-3|3,2,1");
                rarity_2_default_enchantments.add("rechanted:spring|2|1-3|3,2,1");
                rarity_2_default_enchantments.add("minecraft:unbreaking|1|1-3|4,2,1");
                rarity_2_default_enchantments.add("minecraft:thorns|1|1-3|2,2,1");
                rarity_2_default_enchantments.add("minecraft:respiration|1|1-3|3,2,1");
                rarity_2_default_enchantments.add("minecraft:breach|1|1-4|2,2,1,1");
                RARITY_2_ENCHANTMENTS = BUILDER.translation("config.rechanted.unique.enchantments.title").defineList(
                                "enchantments", rarity_2_default_enchantments,
                                () -> "minecraft:sharpness|1|1-4|3,3,2,1", s -> s instanceof String);

                BUILDER.pop();

                BUILDER.translation("config.rechanted.elite.name").comment("Configs for Elite Rarity Enchantments")
                                .push("elite");
                RARITY_3_KEY = BUILDER.translation("config.rechanted.elite.key.title").define("key", "elite");
                RARITY_3_COLOR = BUILDER.translation("config.rechanted.elite.color.title").defineInRange("color",
                                5636095, 0, Integer.MAX_VALUE);
                RARITY_3_EXP_COST = BUILDER.translation("config.rechanted.elite.exp_cost.title")
                                .defineInRange("exp_cost", 200, 0, Integer.MAX_VALUE);
                RARITY_3_WORLD_SPAWN_WEIGHT = BUILDER.translation("config.rechanted.elite.world_spawn_weight.title")
                                .defineInRange("world_spawn_weight", 5, 0, Integer.MAX_VALUE);
                RARITY_3_MIN_SUCCESS = BUILDER.translation("config.rechanted.elite.min_success.title")
                                .defineInRange("min_success", 50, 0, 100);
                RARITY_3_MAX_SUCCESS = BUILDER.translation("config.rechanted.elite.max_success.title")
                                .defineInRange("max_success", 100, 0, 100);
                RARITY_3_FORCED_BOOK_BREAKS = BUILDER
                                .translation("config.rechanted.elite.guaranteed_bookshelf_breaks.title")
                                .defineInRange("guaranteed_bookshelf_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_3_FORCED_FLOOR_BREAKS = BUILDER
                                .translation("config.rechanted.elite.guaranteed_floor_breaks.title")
                                .defineInRange("guaranteed_floor_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_3_BOOK_BREAK_CHANCE = BUILDER.translation("config.rechanted.elite.book_break_chance.title")
                                .defineInRange("book_break_chance", 0.015, 0.0, 1.0);
                RARITY_3_FLOOR_BREAK_CHANCE = BUILDER.translation("config.rechanted.elite.floor_break_chance.title")
                                .defineInRange("floor_break_chance", 0.03, 0.0, 1.0);
                RARITY_3_REQUIRED_BOOKSHELVES = BUILDER
                                .translation("config.rechanted.elite.required_bookshelves.title")
                                .defineInRange("required_bookshelves", 16, 0, Integer.MAX_VALUE);
                RARITY_3_REQUIRED_LAPIS = BUILDER.translation("config.rechanted.elite.required_lapis.title")
                                .defineInRange("required_lapis", 3, 0, 64);
                RARITY_3_FLOOR_BLOCK_TYPE = BUILDER.translation("config.rechanted.elite.floor_block_type.title")
                                .define("floor_block_type", "minecraft:diamond_block");
                RARITY_3_BONUS_ITEM_ROLL_CHANCE = BUILDER
                                .translation("config.rechanted.elite.bonus_item_roll_chance.title")
                                .defineInRange("bonus_item_roll_chance", 0.095, 0.0, 1.0);
                RARITY_3_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT = BUILDER
                                .translation("config.rechanted.elite.bonus_item_mysterious_book_weight.title")
                                .defineInRange("bonus_item_mysterious_book_weight", 60, 0, Integer.MAX_VALUE);
                RARITY_3_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT = BUILDER
                                .translation("config.rechanted.elite.bonus_item_common_gem_pool_weight.title")
                                .defineInRange("bonus_item_common_gem_pool_weight", 50, 0, Integer.MAX_VALUE);
                RARITY_3_BONUS_ITEM_RARE_GEM_POOL_WEIGHT = BUILDER
                                .translation("config.rechanted.elite.bonus_item_rare_gem_pool_weight.title")
                                .defineInRange("bonus_item_rare_gem_pool_weight", 20, 0, Integer.MAX_VALUE);
                RARITY_3_GRINDSTONE_XP_MIN = BUILDER.translation("config.rechanted.elite.grindstone_xp_min.title")
                                .defineInRange("grindstone_xp_min", 50, 0, Integer.MAX_VALUE);
                RARITY_3_GRINDSTONE_XP_MAX = BUILDER.translation("config.rechanted.elite.grindstone_xp_max.title")
                                .defineInRange("grindstone_xp_max", 100, 0, Integer.MAX_VALUE);
                ArrayList<String> rarity_3_default_enchantments = new ArrayList<>();
                rarity_3_default_enchantments.add("rechanted:hells_fury|3|1-4|3,3,2,1");
                rarity_3_default_enchantments.add("rechanted:voids_bane|3|1-4|3,3,2,1");
                rarity_3_default_enchantments.add("rechanted:berserk|3|1-3|3,2,1");
                rarity_3_default_enchantments.add("rechanted:reach|3|1-4|2,2,1,1");
                rarity_3_default_enchantments.add("minecraft:looting|2|1-3|4,2,1");
                rarity_3_default_enchantments.add("minecraft:flame|2|1|1");
                rarity_3_default_enchantments.add("minecraft:depth_strider|2|1-3|2,2,1");
                rarity_3_default_enchantments.add("minecraft:soul_speed|2|2-3|2,1");
                rarity_3_default_enchantments.add("minecraft:fire_aspect|2|1-2|2,1");
                rarity_3_default_enchantments.add("minecraft:fortune|1|1-3|4,2,1");
                rarity_3_default_enchantments.add("minecraft:frost_walker|1|1-2|2,1");
                RARITY_3_ENCHANTMENTS = BUILDER.translation("config.rechanted.elite.enchantments.title").defineList(
                                "enchantments", rarity_3_default_enchantments,
                                () -> "minecraft:sharpness|1|1-4|3,3,2,1", s -> s instanceof String);

                BUILDER.pop();

                // Ultimate rarity builder default config
                BUILDER.translation("config.rechanted.ultimate.name")
                                .comment("Configs for Ultimate Rarity Enchantments").push("ultimate");
                RARITY_4_KEY = BUILDER.translation("config.rechanted.ultimate.key.title").define("key", "ultimate");
                RARITY_4_COLOR = BUILDER.translation("config.rechanted.ultimate.color.title").defineInRange("color",
                                16777045, 0, Integer.MAX_VALUE);
                RARITY_4_EXP_COST = BUILDER.translation("config.rechanted.ultimate.exp_cost.title")
                                .defineInRange("exp_cost", 400, 0, Integer.MAX_VALUE);
                RARITY_4_WORLD_SPAWN_WEIGHT = BUILDER
                                .translation("config.rechanted.ultimate.world_spawn_weight.title")
                                .defineInRange("world_spawn_weight", 3, 0, Integer.MAX_VALUE);
                RARITY_4_MIN_SUCCESS = BUILDER.translation("config.rechanted.ultimate.min_success.title")
                                .defineInRange("min_success", 50, 0, 100);
                RARITY_4_MAX_SUCCESS = BUILDER.translation("config.rechanted.ultimate.max_success.title")
                                .defineInRange("max_success", 100, 0, 100);
                RARITY_4_FORCED_BOOK_BREAKS = BUILDER
                                .translation("config.rechanted.ultimate.guaranteed_bookshelf_breaks.title")
                                .defineInRange("guaranteed_bookshelf_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_4_FORCED_FLOOR_BREAKS = BUILDER
                                .translation("config.rechanted.ultimate.guaranteed_floor_breaks.title")
                                .defineInRange("guaranteed_floor_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_4_BOOK_BREAK_CHANCE = BUILDER.translation("config.rechanted.ultimate.book_break_chance.title")
                                .defineInRange("book_break_chance", 0.015, 0.0, 1.0);
                RARITY_4_FLOOR_BREAK_CHANCE = BUILDER
                                .translation("config.rechanted.ultimate.floor_break_chance.title")
                                .defineInRange("floor_break_chance", 0.075, 0.0, 1.0);
                RARITY_4_REQUIRED_BOOKSHELVES = BUILDER
                                .translation("config.rechanted.ultimate.required_bookshelves.title")
                                .defineInRange("required_bookshelves", 32, 0, Integer.MAX_VALUE);
                RARITY_4_REQUIRED_LAPIS = BUILDER.translation("config.rechanted.ultimate.required_lapis.title")
                                .defineInRange("required_lapis", 4, 0, 64);
                RARITY_4_FLOOR_BLOCK_TYPE = BUILDER.translation("config.rechanted.ultimate.floor_block_type.title")
                                .define("floor_block_type", "minecraft:emerald_block");
                RARITY_4_BONUS_ITEM_ROLL_CHANCE = BUILDER
                                .translation("config.rechanted.ultimate.bonus_item_roll_chance.title")
                                .defineInRange("bonus_item_roll_chance", 0.09, 0.0, 1.0);
                RARITY_4_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT = BUILDER
                                .translation("config.rechanted.ultimate.bonus_item_mysterious_book_weight.title")
                                .defineInRange("bonus_item_mysterious_book_weight", 60, 0, Integer.MAX_VALUE);
                RARITY_4_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT = BUILDER
                                .translation("config.rechanted.ultimate.bonus_item_common_gem_pool_weight.title")
                                .defineInRange("bonus_item_common_gem_pool_weight", 50, 0, Integer.MAX_VALUE);
                RARITY_4_BONUS_ITEM_RARE_GEM_POOL_WEIGHT = BUILDER
                                .translation("config.rechanted.ultimate.bonus_item_rare_gem_pool_weight.title")
                                .defineInRange("bonus_item_rare_gem_pool_weight", 20, 0, Integer.MAX_VALUE);
                RARITY_4_GRINDSTONE_XP_MIN = BUILDER.translation("config.rechanted.ultimate.grindstone_xp_min.title")
                                .defineInRange("grindstone_xp_min", 100, 0, Integer.MAX_VALUE);
                RARITY_4_GRINDSTONE_XP_MAX = BUILDER.translation("config.rechanted.ultimate.grindstone_xp_max.title")
                                .defineInRange("grindstone_xp_max", 150, 0, Integer.MAX_VALUE);
                ArrayList<String> rarity_4_default_enchantments = new ArrayList<>();
                rarity_4_default_enchantments.add("minecraft:swift_sneak|2|1-3|3,2,1");
                rarity_4_default_enchantments.add("minecraft:luck_of_the_sea|2|1-3|4,2,1");
                rarity_4_default_enchantments.add("minecraft:sweeping_edge|2|1-3|3,2,1");
                rarity_4_default_enchantments.add("minecraft:silk_touch|2|1|1");
                rarity_4_default_enchantments.add("rechanted:ice_aspect|2|1-2|3,1");
                rarity_4_default_enchantments.add("rechanted:timber|2|1-3|4,2,1");
                rarity_4_default_enchantments.add("rechanted:courage|2|1-2|2,1");
                rarity_4_default_enchantments.add("rechanted:wisdom|2|1-2|3,1");
                rarity_4_default_enchantments.add("rechanted:vein_miner|1|1|1");
                rarity_4_default_enchantments.add("rechanted:telekinesis|1|1|1");
                rarity_4_default_enchantments.add("minecraft:wind_burst|1|1-3|3,2,1");
                RARITY_4_ENCHANTMENTS = BUILDER.translation("config.rechanted.ultimate.enchantments.title")
                                .defineList("enchantments", rarity_4_default_enchantments,
                                                () -> "minecraft:sharpness|1|1-4|3,3,2,1", s -> s instanceof String);

                BUILDER.pop();

                // Legendary rarity builder default config
                BUILDER.translation("config.rechanted.legendary.name")
                                .comment("Configs for Legendary Rarity Enchantments").push("legendary");
                RARITY_5_KEY = BUILDER.translation("config.rechanted.legendary.key.title").define("key", "legendary");
                RARITY_5_COLOR = BUILDER.translation("config.rechanted.legendary.color.title").defineInRange("color",
                                16755200, 0, Integer.MAX_VALUE);
                RARITY_5_EXP_COST = BUILDER.translation("config.rechanted.legendary.exp_cost.title")
                                .defineInRange("exp_cost", 1250, 0, Integer.MAX_VALUE);
                RARITY_5_WORLD_SPAWN_WEIGHT = BUILDER
                                .translation("config.rechanted.legendary.world_spawn_weight.title")
                                .defineInRange("world_spawn_weight", 1, 0, Integer.MAX_VALUE);
                RARITY_5_MIN_SUCCESS = BUILDER.translation("config.rechanted.legendary.min_success.title")
                                .defineInRange("min_success", 55, 0, 100);
                RARITY_5_MAX_SUCCESS = BUILDER.translation("config.rechanted.legendary.max_success.title")
                                .defineInRange("max_success", 100, 0, 100);
                RARITY_5_FORCED_BOOK_BREAKS = BUILDER
                                .translation("config.rechanted.legendary.guaranteed_bookshelf_breaks.title")
                                .defineInRange("guaranteed_bookshelf_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_5_FORCED_FLOOR_BREAKS = BUILDER
                                .translation("config.rechanted.legendary.guaranteed_floor_breaks.title")
                                .defineInRange("guaranteed_floor_breaks", 0, 0, Integer.MAX_VALUE);
                RARITY_5_BOOK_BREAK_CHANCE = BUILDER.translation("config.rechanted.legendary.book_break_chance.title")
                                .defineInRange("book_break_chance", 0.015, 0.0, 1.0);
                RARITY_5_FLOOR_BREAK_CHANCE = BUILDER
                                .translation("config.rechanted.legendary.floor_break_chance.title")
                                .defineInRange("floor_break_chance", 0.09, 0.0, 1.0);
                RARITY_5_REQUIRED_BOOKSHELVES = BUILDER
                                .translation("config.rechanted.legendary.required_bookshelves.title")
                                .defineInRange("required_bookshelves", 45, 0, Integer.MAX_VALUE);
                RARITY_5_REQUIRED_LAPIS = BUILDER.translation("config.rechanted.legendary.required_lapis.title")
                                .defineInRange("required_lapis", 5, 0, 64);
                RARITY_5_FLOOR_BLOCK_TYPE = BUILDER.translation("config.rechanted.legendary.floor_block_type.title")
                                .define("floor_block_type", "minecraft:ancient_debris");
                RARITY_5_BONUS_ITEM_ROLL_CHANCE = BUILDER
                                .translation("config.rechanted.legendary.bonus_item_roll_chance.title")
                                .defineInRange("bonus_item_roll_chance", 0.12, 0.0, 1.0);
                RARITY_5_BONUS_ITEM_MYSTERIOUS_BOOK_WEIGHT = BUILDER
                                .translation("config.rechanted.legendary.bonus_item_mysterious_book_weight.title")
                                .defineInRange("bonus_item_mysterious_book_weight", 15, 0, Integer.MAX_VALUE);
                RARITY_5_BONUS_ITEM_COMMON_GEM_POOL_WEIGHT = BUILDER
                                .translation("config.rechanted.legendary.bonus_item_common_gem_pool_weight.title")
                                .defineInRange("bonus_item_common_gem_pool_weight", 50, 0, Integer.MAX_VALUE);
                RARITY_5_BONUS_ITEM_RARE_GEM_POOL_WEIGHT = BUILDER
                                .translation("config.rechanted.legendary.bonus_item_rare_gem_pool_weight.title")
                                .defineInRange("bonus_item_rare_gem_pool_weight", 20, 0, Integer.MAX_VALUE);
                RARITY_5_GRINDSTONE_XP_MIN = BUILDER.translation("config.rechanted.legendary.grindstone_xp_min.title")
                                .defineInRange("grindstone_xp_min", 150, 0, Integer.MAX_VALUE);
                RARITY_5_GRINDSTONE_XP_MAX = BUILDER.translation("config.rechanted.legendary.grindstone_xp_max.title")
                                .defineInRange("grindstone_xp_max", 250, 0, Integer.MAX_VALUE);
                ArrayList<String> rarity_5_default_enchantments = new ArrayList<>();
                rarity_5_default_enchantments.add("rechanted:inquisitive|3|1-4|5,4,2,1");
                rarity_5_default_enchantments.add("rechanted:thunder_strike|3|1-4|5,3,2,1");
                rarity_5_default_enchantments.add("rechanted:overload|3|1-3|4,2,1");
                rarity_5_default_enchantments.add("rechanted:blitz|3|1-4|5,3,2,1");
                rarity_5_default_enchantments.add("rechanted:volley|2|1-3|5,2,1");
                rarity_5_default_enchantments.add("minecraft:infinity|1|1|1");
                rarity_5_default_enchantments.add("rechanted:rebirth|1|1|1");
                RARITY_5_ENCHANTMENTS = BUILDER.translation("config.rechanted.legendary.enchantments.title")
                                .defineList("enchantments", rarity_5_default_enchantments,
                                                () -> "minecraft:sharpness|1|1-4|3,3,2,1", s -> s instanceof String);

                BUILDER.pop();
                BUILDER.pop();

                BUILDER.translation("config.rechanted.configs_for_all_rarities.name")
                                .comment("Global settings that apply to all rarity tiers")
                                .push("Configs for all rarities");

                ArrayList<String> compat_tooltip_items = new ArrayList<>();
                compat_tooltip_items.add("minecraft:iron_helmet");
                compat_tooltip_items.add("minecraft:iron_chestplate");
                compat_tooltip_items.add("minecraft:iron_leggings");
                compat_tooltip_items.add("minecraft:iron_boots");
                compat_tooltip_items.add("minecraft:iron_pickaxe");
                compat_tooltip_items.add("minecraft:iron_axe");
                compat_tooltip_items.add("minecraft:iron_shovel");
                compat_tooltip_items.add("minecraft:iron_hoe");
                compat_tooltip_items.add("minecraft:iron_sword");
                compat_tooltip_items.add("minecraft:fishing_rod");
                compat_tooltip_items.add("minecraft:trident");
                compat_tooltip_items.add("minecraft:shield");
                compat_tooltip_items.add("minecraft:bow");
                compat_tooltip_items.add("minecraft:crossbow");
                compat_tooltip_items.add("minecraft:elytra");
                compat_tooltip_items.add("minecraft:iron_spear");
                compat_tooltip_items.add("minecraft:mace");
                COMPATIBLE_TOOLTIP_ITEMS_LIST = BUILDER.translation("config.rechanted.compatible_tooltip_items_list.title")
                        .defineList("compatible_tooltip_items", compat_tooltip_items,
                                () -> "minecraft:iron_helmet", s -> s instanceof String);
                ArrayList<String> valid_enchanting_blocks = new ArrayList<>();
                valid_enchanting_blocks.add("minecraft:bookshelf");
                valid_enchanting_blocks.add("minecraft:chiseled_bookshelf");
                VALID_ENCHANTING_POWER_BLOCKS = BUILDER.translation("config.rechanted.valid_enchanting_power_blocks.title")
                                .defineList("valid_enchanting_power_blocks", valid_enchanting_blocks,
                                                () -> "minecraft:bookshelf", s -> s instanceof String);
                GRINDSTONE_RESULT_ITEM = BUILDER.translation("config.rechanted.grindstone_result_item.title")
                                .define("grindstone_result_item", "minecraft:paper");
                BUILDER.comment("Global weighted pools used by bonus item rewards.",
                                "Format: <gem-item-name>|<weight>");
                ArrayList<String> common_gem_pool_defaults = new ArrayList<>();
                common_gem_pool_defaults.add("rechanted:chance_gem|75");
                common_gem_pool_defaults.add("rechanted:warp_gem|35");
                common_gem_pool_defaults.add("rechanted:lucky_gem|35");
                common_gem_pool_defaults.add("rechanted:tasty_gem|20");
                COMMON_GEM_POOL = BUILDER.translation("config.rechanted.common_gem_pool.title").defineList(
                                "common_gem_pool", common_gem_pool_defaults, () -> "rechanted:chance_gem|75",
                                s -> s instanceof String);

                ArrayList<String> rare_gem_pool_defaults = new ArrayList<>();
                rare_gem_pool_defaults.add("rechanted:shiny_chance_gem|30");
                rare_gem_pool_defaults.add("rechanted:return_gem|20");
                rare_gem_pool_defaults.add("rechanted:clone_gem|20");
                rare_gem_pool_defaults.add("rechanted:smithing_gem|15");
                RARE_GEM_POOL = BUILDER.translation("config.rechanted.rare_gem_pool.title").defineList(
                                "rare_gem_pool", rare_gem_pool_defaults, () -> "rechanted:shiny_chance_gem|30",
                                s -> s instanceof String);
                BUILDER.comment("The game will broadcast a message to all players if a player gets any listed enchantments within the level range to drop from the enchantment table",
                                "Format: <enchantment>|<level-range>",
                                "Example: minecraft:unbreaking|1-3");
                ArrayList<String> announce_enchantments = new ArrayList<>();
                announce_enchantments.add("rechanted:blitz|4");
                announce_enchantments.add("rechanted:volley|3");
                announce_enchantments.add("rechanted:overload|3");
                announce_enchantments.add("rechanted:thunder_strike|4");
                announce_enchantments.add("minecraft:fortune|1-3");
                announce_enchantments.add("rechanted:inquisitive|4");
                announce_enchantments.add("minecraft:looting|3");
                announce_enchantments.add("rechanted:rebirth|1");
                ANNOUNCEMENT_ENCHANTMENTS = BUILDER.translation("config.rechanted.announce_enchantments.title")
                                .defineList("announce_enchantments", announce_enchantments,
                                                () -> "rechanted:sharpness|3-5", s -> s instanceof String);

                BUILDER.translation("config.rechanted.announce_gem_drop_list.name")
                                .comment("Settings for gem drop announcement toggles")
                                .push("Announce Gem Drop List");
                BUILDER.comment("Gem drop announcement toggles.");
                ANNOUNCE_CHANCE_GEM = BUILDER.translation("config.rechanted.announce_chance_gem.title")
                                .define("announce_chance_gem", true);
                ANNOUNCE_SHINY_CHANCE_GEM = BUILDER.translation("config.rechanted.announce_shiny_chance_gem.title")
                                .define("announce_shiny_chance_gem", true);
                ANNOUNCE_RETURN_GEM = BUILDER.translation("config.rechanted.announce_return_gem.title")
                                .define("announce_return_gem", true);
                ANNOUNCE_TASTY_GEM = BUILDER.translation("config.rechanted.announce_tasty_gem.title")
                                .define("announce_tasty_gem", true);
                ANNOUNCE_WARP_GEM = BUILDER.translation("config.rechanted.announce_warp_gem.title")
                                .define("announce_warp_gem", true);
                ANNOUNCE_LUCKY_GEM = BUILDER.translation("config.rechanted.announce_lucky_gem.title")
                                .define("announce_lucky_gem", true);
                ANNOUNCE_CLONE_GEM = BUILDER.translation("config.rechanted.announce_clone_gem.title")
                                .define("announce_clone_gem", true);
                ANNOUNCE_SMITHING_GEM = BUILDER.translation("config.rechanted.announce_smithing_gem.title")
                                .define("announce_smithing_gem", true);
                BUILDER.pop();
                BUILDER.pop();

                BUILDER.translation("config.rechanted.loot_table_enhancements.name").comment(
                                "Configurations for all things related to generated loot drops. Ex: end_city_treasure")
                                .push("Loot Table Enhancements");

                BUILDER.comment("Replace all enchanted loot into Rechanted books based on world_spawn_weight config from each rarity section");
                BUILDER.comment("Example: A chest plate found with enchants will be replaced entirely with a rolled enchanted book based on the rarity configs");
                REPLACE_ENCHANTED_LOOT = BUILDER.translation("config.rechanted.replace_enchanted_loot.title")
                                .define("replace_enchanted_loot", true);

                BUILDER.comment("Removes mending enchantment from found enchanted loot from generated world loot. Ex: end_city_treasure");
                BUILDER.comment("Mending books can be found only in book form as long as you have minecraft:mending set in a rarity pool.");
                BUILDER.comment("Having REPLACE_ENCHANTED_LOOT set to true defaults this to true.");
                REMOVE_MENDING_ENABLED = BUILDER.translation("config.rechanted.remove_mending.title")
                                .define("remove_mending", true);

                BUILDER.comment("Excludes gold, leather, stone, wood enchanted drops from being affected by the REPLACE_ENCHANTED_LOOT configuration");
                BUILDER.comment("Example: Gold tools and armor from nether portal ruins will remain and not be replaced by Rechanted books");
                EXCLUDE_LOWER_TIER_LOOT = BUILDER.translation("config.rechanted.exclude_lower_tier_loot.title")
                                .define("exclude_lower_tier_loot", true);

                BUILDER.comment("Without this enabled, fishing loot becomes an overpowered source for enchanted books when REPLACE_ENCHANTED_LOOT is enabled");
                BUILDER.comment("Also makes enchanted fished treasure have weaker enchants (level 5 enchants) and excludes it from REPLACE_ENCHANTED_LOOT");
                BUILDER.comment("Example: Enchanted bows and fishing rods will now commonly have Power I or Lure I respectively but still roll with Unbreaking I");
                BUILDER.comment("We recommend enabling this if using Rechanted with Tide/Tide 2.0, otherwise players are showered in books when using Luck of the Sea");
                NERF_FISHING_LOOT = BUILDER.translation("config.rechanted.nerf_fishing_loot.title")
                                .define("nerf_fishing_loot", false);
                BUILDER.comment("Chance for replaced world loot books to become a random gem instead.");
                BUILDER.comment("Set to 0 to disable. Gems from this roll are uniform across all mod gems.");
                WORLD_LOOT_GEM_DROP_CHANCE = BUILDER.translation("config.rechanted.world_loot_gem_drop_chance.title")
                                .defineInRange("world_loot_gem_drop_chance", 0.002, 0.0, 1.0);

                BUILDER.translation("config.rechanted.villager_trades.name")
                                .comment("Settings that control villager trade modifications").push("Villager Trades");
                BUILDER.comment("If false, villager trades are left untouched (no enchanted book replacement or enchant stripping).");
                MODIFY_VILLAGER_TRADES = BUILDER.translation("config.rechanted.modify_villager_trades.title")
                                .define("modify_villager_trades", true);
                BUILDER.comment("Cost in emeralds for the mysterious book trade when replacing enchanted books.");
                VILLAGER_MYSTERIOUS_BOOK_EMERALD_COST = BUILDER
                                .translation("config.rechanted.mysterious_book_emerald_cost.title")
                                .defineInRange("mysterious_book_emerald_cost", 32, 1, 64);

                VILLAGER_DUSTY_MYSTERIOUS_BOOK_EMERALD_COST = BUILDER
                        .translation("config.rechanted.dusty_mysterious_book_emerald_cost.title")
                        .defineInRange("dusty_mysterious_book_emerald_cost", 8, 1, 64);

                BUILDER.pop();

                BUILDER.comment("To nerf overpowered XP farms using the infestation and oozing potions.");
                DISABLE_XP_FROM_SLIMES_AND_SILVERFISH = BUILDER
                                .translation("config.rechanted.disable_xp_from_slimes_and_silverfish.title")
                                .define("disable_xp_from_slimes_and_silverfish", false);
                GEAR_BREAK_WARNING_ENABLED = BUILDER
                                .translation("config.rechanted.gear_break_warning_enabled.title")
                                .define("gear_break_warning_enabled", true);

                BUILDER.translation("config.rechanted.fortune_nerf.name")
                                .comment("Settings to control Fortune enchantment drop multipliers")
                                .push("Fortune Nerf");
                BUILDER.comment(
                                "If enabled, fortune will only double drops based on the chances defined at each level");
                FORTUNE_NERF_ENABLED = BUILDER.translation("config.rechanted.nerf_enabled.title")
                                .define("nerf_enabled", false);
                FORTUNE_1_CHANCE = BUILDER.translation("config.rechanted.fortune_1_chance.title")
                                .defineInRange("fortune_1_chance", 0.33, 0.0, 1.0);
                FORTUNE_2_CHANCE = BUILDER.translation("config.rechanted.fortune_2_chance.title")
                                .defineInRange("fortune_2_chance", 0.5, 0.0, 1.0);
                FORTUNE_3_CHANCE = BUILDER.translation("config.rechanted.fortune_3_chance.title")
                                .defineInRange("fortune_3_chance", 0.65, 0.0, 1.0);

                BUILDER.pop();

                BUILDER.pop();

                BUILDER.comment("Random settings");
                BUILDER.push("Miscellaneous Settings");
                USE_GREYSCALE_GUI = BUILDER.translation("config.rechanted.use_greyscale_gui.title")
                        .comment("Enabled greyscale variants of the enchanting table and loot pool gui")
                        .define("use_greyscale_gui", false);

                SPEC = BUILDER.build();
        }
}
