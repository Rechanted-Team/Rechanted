package net.tagtart.rechanted.util;

import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.tagtart.rechanted.config.RechantedCommonConfigs;
import net.tagtart.rechanted.event.ParticleEmitter;
import net.tagtart.rechanted.item.ModItems;
import net.tagtart.rechanted.item.custom.WarpGemItem;
import net.tagtart.rechanted.component.ModDataComponents;
import net.tagtart.rechanted.networking.data.TriggerRebirthItemEffectS2CPayload;
import net.tagtart.rechanted.sound.CustomClientSoundInstanceHandler;
import net.tagtart.rechanted.sound.ModSounds;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class UtilFunctions {
    private static final List<Supplier<Item>> WORLD_LOOT_GEM_ITEMS = List.of(
            ModItems.CHANCE_GEM,
            ModItems.SHINY_CHANCE_GEM,
            ModItems.RETURN_GEM,
            ModItems.TASTY_GEM,
            ModItems.WARP_GEM,
            ModItems.LUCKY_GEM,
            ModItems.CLONE_GEM,
            ModItems.SMITHING_GEM
    );

    // Utility method to wrap text into lines
    public static List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        Font font = Minecraft.getInstance().font; // Get the Minecraft font renderer

        for (String word : text.split(" ")) {
            // Check if adding the next word exceeds the maxWidth
            if (font.width(currentLine + word) > maxWidth) {
                lines.add(currentLine.toString()); // Add the current line to the list
                currentLine = new StringBuilder(); // Start a new line
            }
            currentLine.append(word).append(" ");
        }

        // Add the last line if it exists
        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }

    public static List<Component> getIncompatibilityTooltipLines(Holder<Enchantment> enchantment, Item.TooltipContext context, TooltipFlag flag) {
        if (enchantment == null || context == null) {
            return List.of();
        }

        return getIncompatibilityTooltipLines(enchantment, context.registries(), flag);
    }

    public static List<Component> getIncompatibilityTooltipLines(Holder<Enchantment> enchantment, HolderLookup.Provider registryAccess, TooltipFlag flag) {
        if (enchantment == null || registryAccess == null) {
            return List.of();
        }

        List<Holder<Enchantment>> incompatibleEnchantments = getIncompatibleEnchantments(enchantment, registryAccess);
        if (incompatibleEnchantments.isEmpty()) {
            return List.of();
        }

        List<Component> tooltipLines = new ArrayList<>();
        if (!flag.hasShiftDown()) {
            tooltipLines.add(Component.literal("Hold ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Shift").withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC))
                    .append(Component.literal(" for more info").withStyle(ChatFormatting.GRAY)));
            return tooltipLines;
        }

        int incompatibleColor = 0xf29811;
        tooltipLines.add(Component.literal("Incompatible with:").withColor(incompatibleColor));
        for (Holder<Enchantment> incompatible : incompatibleEnchantments) {
            tooltipLines.add(Component.literal("- ").withColor(incompatibleColor)
                    .append(incompatible.value().description().copy().withColor(incompatibleColor)));
        }

        return tooltipLines;
    }


    public static ItemStack getItemStackFromString(String itemName) {
        ResourceLocation itemResource = ResourceLocation.parse(itemName);

        Item item = BuiltInRegistries.ITEM.get(itemResource);

        if (item == null) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(item);
    }

    @Nullable
    public static BookRarityProperties getPropertiesFromEnchantment(String enchantmentRaw) {
        for (BookRarityProperties bookProperties : BookRarityProperties.getAllProperties()) {
            if (bookProperties.isEnchantmentInPool(enchantmentRaw)) {
                return bookProperties;
            }
        }

        return null;
    }

    // TODO: Reimplement when enchantment system is ported.
//    public static void announceDrop(String enchantmentRaw, int enchantmentLevel, ServerPlayer player, BookRarityProperties bookProperties, int successRate) {
//        if (shouldAnnounceDrop(enchantmentRaw, enchantmentLevel)) {
//            ServerLevel level = (ServerLevel) player.level();
//            String enchantmentFormatted = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantmentRaw)).getFullname(enchantmentLevel).getString();
//            Component announceMessage = Component.literal(player.getName().getString() + " found ")
//                    .append(Component.literal(enchantmentFormatted).withStyle(Style.EMPTY.withColor(bookProperties.color)))
//                    .append(" at ")
//                    .append(Component.literal(successRate + "%").withStyle(Style.EMPTY.withColor(bookProperties.color)))
//                    .append("!");
//            for (ServerPlayer otherPlayer: level.players()) {
//                otherPlayer.sendSystemMessage(announceMessage);
//            }
//        }
//    }

    public static boolean shouldAnnounceDrop(String enchantmentRaw, int enchantmentLevel) {
        List<? extends String> enchantmentsList = RechantedCommonConfigs.ANNOUNCEMENT_ENCHANTMENTS.get();
        for (String enchantmentRawConfig : enchantmentsList) {
            String[] parts = enchantmentRawConfig.split("\\|");
            String[] range = parts[1].split("-");


            int lowerBound = Integer.parseInt(range[0]);
            int upperBound = lowerBound;
            if (range.length > 1) {
                upperBound = Integer.parseInt(range[1]);
            }

            if (enchantmentLevel >= lowerBound && enchantmentLevel <= upperBound && Objects.equals(parts[0], enchantmentRaw)) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldAnnounceGemDrop(String gemTranslationKey) {
        return switch (gemTranslationKey) {
            case "item.rechanted.chance_gem" -> RechantedCommonConfigs.ANNOUNCE_CHANCE_GEM.get();
            case "item.rechanted.shiny_chance_gem" -> RechantedCommonConfigs.ANNOUNCE_SHINY_CHANCE_GEM.get();
            case "item.rechanted.return_gem" -> RechantedCommonConfigs.ANNOUNCE_RETURN_GEM.get();
            case "item.rechanted.tasty_gem" -> RechantedCommonConfigs.ANNOUNCE_TASTY_GEM.get();
            case "item.rechanted.warp_gem" -> RechantedCommonConfigs.ANNOUNCE_WARP_GEM.get();
            case "item.rechanted.lucky_gem" -> RechantedCommonConfigs.ANNOUNCE_LUCKY_GEM.get();
            case "item.rechanted.clone_gem" -> RechantedCommonConfigs.ANNOUNCE_CLONE_GEM.get();
            case "item.rechanted.smithing_gem" -> RechantedCommonConfigs.ANNOUNCE_SMITHING_GEM.get();
            default -> false;
        };
    }

    public static ItemStack rollModdedBook(RegistryAccess registryAccess, boolean allowWorldLootGemDrops) {
        return rollModdedBook(registryAccess, allowWorldLootGemDrops, -1);
    }


    public static ItemStack rollModdedBook(RegistryAccess registryAccess, boolean allowWorldLootGemDrops, int forcedRarity) {
        Random random = new Random();

        if (allowWorldLootGemDrops) {
            double gemChance = RechantedCommonConfigs.WORLD_LOOT_GEM_DROP_CHANCE.get();
            if (gemChance > 0.0 && random.nextDouble() < gemChance) {
                ItemStack rolledGem = rollUniformWorldLootGem(random);
                if (UtilFunctions.shouldAnnounceGemDrop(rolledGem.getDescriptionId())) {
                    rolledGem.set(ModDataComponents.SHOULD_ANNOUNCE_GEM, true);
                }
                return rolledGem;
            }
        }

        ItemStack replacementBook = new ItemStack(ModItems.RECHANTED_BOOK.get());
        BookRarityProperties bookRarityProperties;
        if (forcedRarity == -1) {
            bookRarityProperties = BookRarityProperties.getRandomRarityWeighted();
        } else {
            forcedRarity = Math.clamp(forcedRarity, 0, BookRarityProperties.getAllProperties().length - 1);
            bookRarityProperties = BookRarityProperties.getAllProperties()[forcedRarity];
        }
        EnchantmentPoolEntry randomEnchantment = bookRarityProperties.getRandomEnchantmentWeighted();
        int enchantmentLevel = randomEnchantment.getRandomEnchantLevelWeighted();


        int successRate = Math.min(100, bookRarityProperties.maxSuccess);
        if (bookRarityProperties.minSuccess < bookRarityProperties.maxSuccess) {
            successRate = random.nextInt(bookRarityProperties.minSuccess, bookRarityProperties.maxSuccess + 1);
        }

        Holder.Reference<Enchantment> enchantment = UtilFunctions.getEnchantmentReferenceIfPresent(
                registryAccess,
                randomEnchantment.enchantment
        );
        if (enchantment == null) {
            return replacementBook;
        }

        ItemEnchantments.Mutable storedEnchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        storedEnchants.set(enchantment, enchantmentLevel);

        replacementBook.set(DataComponents.STORED_ENCHANTMENTS, storedEnchants.toImmutable().withTooltip(false));
        replacementBook.set(ModDataComponents.SUCCESS_RATE, successRate);

        if (UtilFunctions.shouldAnnounceDrop(randomEnchantment.enchantment, enchantmentLevel)) {
            replacementBook.set(ModDataComponents.ANNOUNCE_ON_FOUND, true);
        }

        return replacementBook;
    }

    private static ItemStack rollUniformWorldLootGem(Random random) {
        Supplier<Item> gemSupplier = WORLD_LOOT_GEM_ITEMS.get(random.nextInt(WORLD_LOOT_GEM_ITEMS.size()));
        ItemStack rolledGem = new ItemStack(gemSupplier.get());
        WarpGemItem.initializeRandomizedDurability(rolledGem, random);
        return rolledGem;
    }

    // Forms a bounding box around the provided position by offsetting the corners by the provided offset values,
    // then returns an array of all block states in the level inside that bounding box. If a filter is provided,
    // only blocks matching the type provided will be returned (otherwise, all are returned). Keep in mind the offsets are absolutes,
    // as in, lowerLeft will be subtracted from given position, and upperRight will be added!
    public static Pair<BlockState[], BlockPos[]> getAllBlockInfoAroundBlock(Level level, BlockPos centerPos, Vec3i lowerLeftOffset, Vec3i upperRightOffset, @Nullable Block blockFilter) {

        Vec3i lowerLeft = centerPos.subtract(lowerLeftOffset);
        Vec3i upperRight = centerPos.offset(upperRightOffset);

        ArrayList<BlockPos> positions = new ArrayList<>();
        ArrayList<BlockState> states = new ArrayList<>();
        for (int x = lowerLeft.getX(); x <= upperRight.getX(); ++x) {
            for (int y = lowerLeft.getY(); y <= upperRight.getY(); ++y) {
                for (int z = lowerLeft.getZ(); z <= upperRight.getZ(); ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (blockFilter == null || state.is(blockFilter)) {
                        positions.add(pos);
                        states.add(state);
                    }
                }
            }
        }

        return new Pair<>(states.toArray(BlockState[]::new), positions.toArray(BlockPos[]::new));
    }

    // The player.totalExperience value isn't trustworthy. It desyncs if levels are added/removed directly
    // via commands or using an anvil. So, we calculate it manually always.
    // Formula from:
    // https://minecraft.wiki/w/Experience#:~:text=using%20the%20equations%3A-,Total%20experience,-%3D
    public static int getPlayerExperiencePoints(Player player) {
        int level = Math.max(player.experienceLevel, 0);

        int totalEXPtoLevel = 0;
        if (level <= 16) { // Levels 0 - 16
            totalEXPtoLevel = (level * level) + 6 * level;
        }
        else if (level <= 31 ) { // Levels 17 - 31
            totalEXPtoLevel = (int)(2.5 * (level * level) - 40.5 * level + 360.0);
        }
        else {
            totalEXPtoLevel = (int)(4.5 * (level * level) - 162.5 * level + 2220);
        }

        int xpBarFillAmount = (int)(player.experienceProgress * player.getXpNeededForNextLevel());

        return totalEXPtoLevel + xpBarFillAmount;
    }

    public static boolean playerMeetsExpRequirement(BookRarityProperties bookProperties, Player player) {
        return getPlayerExperiencePoints(player) >= bookProperties.requiredExp;
    }

    public static boolean playerMeetsBookshelfRequirement(BookRarityProperties bookProperties, double enchantingPower) {
        return enchantingPower >= bookProperties.requiredBookShelves;
    }

    public static double getEnchantingPower(Level level, Pair<BlockState[], BlockPos[]> sources) {
        double enchantingPower = 0;
        for (int i = 0; i < sources.getA().length; ++i) {
            float power = sources.getA()[i].getEnchantPowerBonus(level, sources.getB()[i]);
            if (power > 0) {
                enchantingPower += power;
            }
        }
        return enchantingPower;
    }

    // Note that is checks if the blocks match the required type for safety.
    // Recommended to pass in smaller (possibly already filtered) arrays for performance.
    public static boolean playerMeetsFloorRequirement(BookRarityProperties bookProperties, BlockState[] states) {
        // Air should never count as a valid activation floor, even if configured accidentally.
        if (bookProperties.floorBlock == Blocks.AIR) {
            return false;
        }

        int blocksPresent = Arrays.stream(states).filter(blockState -> blockState.is(bookProperties.floorBlock)).toArray().length;
        return blocksPresent >= 9;
    }

    public static boolean playerMeetsLapisRequirement(BookRarityProperties bookProperties, ItemStack inventoryItemStack) {
        return inventoryItemStack.getCount() >= bookProperties.requiredLapis;
    }

    public static Pair<BlockState[], BlockPos[]> scanAroundBlockForBookshelves(Level level, BlockPos blockPos){

        // Might read these from config instead but probably not for now?
        // Compute AABB corners for scanning level:
        Vec3i bookshelfCheckLowerLeftOffset = new Vec3i(3, 0, 3);
        Vec3i bookshelfCheckUpperRightOffset = new Vec3i(3, 2, 3);

        var blockInfo = UtilFunctions.getAllBlockInfoAroundBlock(
                level,
                blockPos,
                bookshelfCheckLowerLeftOffset,
                bookshelfCheckUpperRightOffset,
                null
        );

        // Keep states and positions aligned, including only positive power providers.
        // The positions also determine which blocks can be consumed during a purchase.
        ArrayList<BlockState> states = new ArrayList<>();
        ArrayList<BlockPos> positions = new ArrayList<>();
        for (int i = 0; i < blockInfo.getA().length; ++i) {
            BlockState state = blockInfo.getA()[i];
            BlockPos pos = blockInfo.getB()[i];
            if (state.getEnchantPowerBonus(level, pos) > 0) {
                states.add(state);
                positions.add(pos);
            }
        }
        return new Pair<>(states.toArray(BlockState[]::new), positions.toArray(BlockPos[]::new));
    }

    public static Pair<BlockState[], BlockPos[]> scanAroundBlockForValidFloors(Block validBlock, Level level, BlockPos blockPos){

        Vec3i floorCheckLowerLeftOffset = new Vec3i(1, 0, 1);
        Vec3i floorCheckUpperRightOffset = new Vec3i(1, 0, 1);

        var floorBlockInfo = UtilFunctions.getAllBlockInfoAroundBlock(
                level,
                blockPos.subtract(new Vec3i(0, 1, 0)), // Checking one block under enchant table.
                floorCheckLowerLeftOffset,
                floorCheckUpperRightOffset,
                validBlock
        );

        return floorBlockInfo;
    }

    public static boolean playerMeetsAllEnchantRequirements(BookRarityProperties bookProperties, Player player, double enchantingPower, BlockState[] floorBlocks) {

        boolean expReqMet      = playerMeetsExpRequirement(bookProperties, player);
        boolean shelvesReqMet  = playerMeetsBookshelfRequirement(bookProperties, enchantingPower);
        boolean floorReqMet    = playerMeetsFloorRequirement(bookProperties, floorBlocks);

        return expReqMet && shelvesReqMet && floorReqMet;
    }

    @Nullable
    public static Holder.Reference<Enchantment> getEnchantmentReferenceIfPresent(RegistryAccess registryAccess, String enchantmentRaw) {


        ResourceLocation enchantmentResourceLocation = ResourceLocation.parse(enchantmentRaw);

        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);

        ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, enchantmentResourceLocation);
        Optional<Holder.Reference<Enchantment>> enchantmentOptional = enchantmentRegistry.get(key);

        return enchantmentOptional.orElse(null);
    }

    @Nullable
    public static Holder.Reference<Enchantment> getEnchantmentReferenceIfPresent(RegistryAccess registryAccess, ResourceKey<Enchantment> enchantmentKey) {

        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);
        Optional<Holder.Reference<Enchantment>> enchantmentOptional = enchantmentRegistry.get(enchantmentKey);

        return enchantmentOptional.orElse(null);
    }


    public static int getEnchantmentFromItem (String enchantmentRaw, ItemStack enchantedItem, RegistryAccess registryAccess) {

        Holder.Reference<Enchantment> enchantment = getEnchantmentReferenceIfPresent(registryAccess, enchantmentRaw);
        if (enchantment == null) {
            return 0;
        }

        return enchantedItem.getEnchantmentLevel(enchantment);
    }

    // Non-recursive, 3D, Breadth-first-search through the world for a particular block type including matching ones connected to each other
    // Returns the block states and associated block positions from the provided level that match provided block tag key.
    // Not async, ideally should provide a small search limit when called so the game doesn't shit itself.
    public static BlockPos[] BFSLevelForBlocks(Level pLevel, TagKey<Block> pCheckBlock, BlockPos startPos, int searchLimit, boolean checkDiagonally) {

        HashSet<BlockPos> matchPositions = new HashSet<>();

        // BFS with queue, start at provided position, keep going until no more left to scan, or we've
        // retrieved as many as the caller wants.
        ArrayDeque<BlockPos> positionsToScan = new ArrayDeque<>();
        HashSet<BlockPos> alreadyScanned = new HashSet<>();
        positionsToScan.offer(startPos);
        while (matchPositions.size() < searchLimit && !positionsToScan.isEmpty()) {

            BlockPos scanPos = positionsToScan.poll();
            BlockState scanState = pLevel.getBlockState(scanPos);
            alreadyScanned.add(scanPos);

            if (scanState.is(pCheckBlock)) {
                matchPositions.add(scanPos);

                // Getting all positions around current one
                for (int x = scanPos.getX() - 1; x <= scanPos.getX() + 1; ++x) {
                    for (int y = scanPos.getY() - 1; y <= scanPos.getY() + 1; ++y) {
                        for (int z = scanPos.getZ() - 1; z <= scanPos.getZ() + 1; ++z) {

                            BlockPos newPos = new BlockPos(x, y, z);
                            BlockPos diffVector = scanPos.subtract(newPos);
                            int sqrDist = getSqrDistOfBlock(diffVector);

                            // Omit position we are actually scanning right now (so difference distance is zero)...
                            // ... and omit positions that already have been checked...
                            // ... also if we want to omit diagonals, skip this position if distance is greater than 1
                            // from scanPos, since adjacent blocks are always exactly 1 unit away
                            if (sqrDist == 0 || alreadyScanned.contains(newPos) || (!checkDiagonally && sqrDist > 1))
                                continue;

                            positionsToScan.add(newPos);
                        }
                    }
                }
            }
        }

        return matchPositions.toArray(BlockPos[]::new);
    }

    public static int getSqrDistOfBlock(BlockPos pos) {
        int dx = pos.getX();
        int dy = pos.getY();
        int dz = pos.getZ();

        return dx * dx + dy * dy + dz * dz;
    }

    public static List<Holder<Enchantment>> getIncompatibleEnchantments(Holder<Enchantment> enchantment, HolderLookup.Provider registryAccess) {
        Map<ResourceKey<Enchantment>, Holder.Reference<Enchantment>> configuredEnchantments = getConfiguredEnchantments(registryAccess);
        if (configuredEnchantments.isEmpty()) {
            return List.of();
        }

        List<Holder<Enchantment>> incompatibles = new ArrayList<>();
        ResourceKey<Enchantment> currentEnchantmentKey = enchantment.unwrapKey().orElse(null);
        for (Map.Entry<ResourceKey<Enchantment>, Holder.Reference<Enchantment>> configuredEnchantment : configuredEnchantments.entrySet()) {
            if (configuredEnchantment.getKey().equals(currentEnchantmentKey)) {
                continue;
            }

            Holder<Enchantment> otherEnchantmentHolder = configuredEnchantment.getValue();
            if (enchantment.equals(otherEnchantmentHolder)) {
                continue;
            }

            boolean compatibleForward = Enchantment.areCompatible(enchantment, otherEnchantmentHolder);
            boolean compatibleReverse = Enchantment.areCompatible(otherEnchantmentHolder, enchantment);
            if (!compatibleForward || !compatibleReverse) {
                incompatibles.add(otherEnchantmentHolder);
            }
        }

        incompatibles.sort(Comparator.comparing(
                incompatEnchantment -> incompatEnchantment.value().description().getString(),
                String.CASE_INSENSITIVE_ORDER));

        return incompatibles;
    }

    private static Map<ResourceKey<Enchantment>, Holder.Reference<Enchantment>> getConfiguredEnchantments(HolderLookup.Provider registryAccess) {
        Optional<? extends HolderLookup.RegistryLookup<Enchantment>> enchantmentRegistryOptional = registryAccess.lookup(Registries.ENCHANTMENT);
        if (enchantmentRegistryOptional.isEmpty()) {
            return Map.of();
        }

        HolderLookup.RegistryLookup<Enchantment> enchantmentRegistry = enchantmentRegistryOptional.get();
        Map<ResourceKey<Enchantment>, Holder.Reference<Enchantment>> configuredEnchantments = new LinkedHashMap<>();

        for (BookRarityProperties rarityProperties : BookRarityProperties.getAllProperties()) {
            for (EnchantmentPoolEntry poolEntry : rarityProperties.enchantmentPool) {
                ResourceLocation enchantmentId = ResourceLocation.tryParse(poolEntry.enchantment);
                if (enchantmentId == null) {
                    continue;
                }

                ResourceKey<Enchantment> enchantmentKey = ResourceKey.create(Registries.ENCHANTMENT, enchantmentId);
                enchantmentRegistry.get(enchantmentKey).ifPresent(holder ->
                        configuredEnchantments.putIfAbsent(enchantmentKey, holder));
            }
        }

        return configuredEnchantments;
    }

    public static void translatePoseByInterpolatedPlayerPos(PoseStack poseStack, Player player, Entity entity, float partialTick) {

        double px = Mth.lerp(partialTick, player.xOld, player.getX());
        double py = Mth.lerp(partialTick, player.yOld, player.getY());
        double pz = Mth.lerp(partialTick, player.zOld, player.getZ());

        double ex = Mth.lerp(partialTick, entity.xOld, entity.getX());
        double ey = Mth.lerp(partialTick, entity.yOld, entity.getY());
        double ez = Mth.lerp(partialTick, entity.zOld, entity.getZ());

        double dx = px - ex;
        double dy = py - ey;
        double dz = pz - ez;

        poseStack.pushPose();
        poseStack.translate(dx, dy, dz);
    }


    // Derived from inverse equations for total EXP level from here:
    // https://minecraft.fandom.com/wiki/Experience#Formulas_and_Tables
    public static double getTotalLevelFromEXP(int experience) {
        if (experience < 352) {
            return Math.sqrt(experience + 9.0) - 3.0;
        }
        else if (experience < 1507) {
            return 8.1 + Math.sqrt(0.4 * (experience - 195.975));
        }
        else {
            return 18.05556 + Math.sqrt(0.22223 * (experience - 752.98611));
        }
    }

    // Mapping of Roman numerals to values.
    // Can't lie this entirely 100% copy-pasted from ChatGPT because I'm a slob <--- (shame on you, me from 2024! wtf!! I'm a changed man now I swear)
    private static final int[] ROMAN_VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] ROMAN_SYMBOLS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
    public static String intToRoman(int num) {
        if (num < 1 || num > 3999) {
            throw new IllegalArgumentException("Input must be between 1 and 3999");
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < ROMAN_VALUES.length; i++) {
            while (num >= ROMAN_VALUES[i]) {
                result.append(ROMAN_SYMBOLS[i]);
                num -= ROMAN_VALUES[i];
            }
        }

        return result.toString();
    }


    public static void triggerRebirthClientEffects(ServerPlayer player, ServerLevel level, ItemStack itemToActivate) {
        SimpleParticleType[] particlesArray = new SimpleParticleType[] {
                ParticleTypes.SOUL_FIRE_FLAME,
                ParticleTypes.FIREWORK,
                ParticleTypes.ENCHANT,
                ParticleTypes.ENCHANTED_HIT,

        };
        ParticleEmitter.emitParticlesOverTime(player, level, 100, 60, particlesArray);

        PacketDistributor.sendToPlayer(player, new TriggerRebirthItemEffectS2CPayload(itemToActivate));

        level.playSound(null,  player.blockPosition(), ModSounds.REBIRTH_ITEM.get(), SoundSource.PLAYERS, 0.7F, 1.0F);
    }

    // For new implementation of fortune nerf.
    public static int calcluateOreDropsNerfed(RandomSource random, int originalCount, int enchantmentLevel) {
            double chanceToDouble = 0.0;
            switch(enchantmentLevel) {
                case 1: {
                    chanceToDouble = RechantedCommonConfigs.FORTUNE_1_CHANCE.get(); // Config later
                    break;
                }
                case 2: {
                    chanceToDouble = RechantedCommonConfigs.FORTUNE_2_CHANCE.get(); // Config later
                    break;
                }
                case 3: {
                    chanceToDouble = RechantedCommonConfigs.FORTUNE_3_CHANCE.get(); // Config later
                    break;
                }
                default:
                    break;
            }
            if (random.nextDouble() < chanceToDouble) {
                return originalCount * 2;
            }
            return originalCount;
    }

    public static void spawnRandomFireworkExplosion(Level level, RandomSource random, double x, double y, double z) {
        if (level.isClientSide) {
            return;
        }

        FireworkExplosion.Shape[] shapes = FireworkExplosion.Shape.values();
        FireworkExplosion.Shape randomShape = shapes[random.nextInt(shapes.length)];
        int mainColor = Mth.hsvToRgb(random.nextFloat(), 1.0F, 1.0F);
        int fadeColor = Mth.hsvToRgb(random.nextFloat(), 0.85F, 1.0F);

        FireworkExplosion explosion = new FireworkExplosion(
                randomShape,
                IntList.of(mainColor),
                IntList.of(fadeColor),
                random.nextBoolean(),
                random.nextBoolean());

        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
        fireworkStack.set(DataComponents.FIREWORKS, new Fireworks(0, List.of(explosion)));

        FireworkRocketEntity firework = new FireworkRocketEntity(level, x, y, z,
                fireworkStack);
        firework.setDeltaMovement(Vec3.ZERO);
        level.addFreshEntity(firework);
        level.broadcastEntityEvent(firework, (byte) 17);
        firework.discard();
    }

    public static double remap(double fromMin, double fromMax, double toMin, double toMax, double value) {
        return Mth.lerp(Mth.inverseLerp(value, fromMin, fromMax), toMin, toMax);
    }

    // If EVER called on server, game will shit its pants. Do not do that.
    public static void createAndPlayAmbientSound(SoundEvent pSoundEvent, BlockPos pPos, float volume) {
        CustomClientSoundInstanceHandler.createAnyPlayAmbientSound(pSoundEvent, pPos, volume);
    }

    public static void tryStopAmbientSound(BlockPos pPos) {
        CustomClientSoundInstanceHandler.tryStopAmbientSound(pPos);
    }
}
