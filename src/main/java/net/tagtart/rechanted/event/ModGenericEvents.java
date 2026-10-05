package net.tagtart.rechanted.event;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.tagtart.rechanted.Rechanted;
import net.tagtart.rechanted.block.ModBlocks;
import net.tagtart.rechanted.block.entity.RechantedTableBlockEntity;
import net.tagtart.rechanted.component.ModDataComponents;
import net.tagtart.rechanted.config.RechantedCommonConfigs;
import net.tagtart.rechanted.event.enchantment.TelekinesisEnchantmentHandler;
import net.tagtart.rechanted.enchantment.custom.InquisitiveEnchantmentEffect;
import net.tagtart.rechanted.item.ModItems;
import net.tagtart.rechanted.item.custom.RechantedBookItem;
import net.tagtart.rechanted.networking.data.OpenEnchantTableScreenC2SPayload;
import net.tagtart.rechanted.networking.data.PlayerPurchaseEnchantedBookC2SPayload;
import net.tagtart.rechanted.networking.data.TriggerRebirthItemEffectS2CPayload;
import net.tagtart.rechanted.screen.InlineItemStackIconsComponent;
import net.tagtart.rechanted.util.AdvancementHelper;
import net.tagtart.rechanted.util.BookRarityProperties;
import net.tagtart.rechanted.util.UtilFunctions;

import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EventBusSubscriber(modid = Rechanted.MOD_ID)
public class ModGenericEvents {
    private static final String ENCHANTMENT_DESCRIPTIONS_MOD_ID = "enchdesc";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.NETWORK);

        registrar.commonToServer(
                OpenEnchantTableScreenC2SPayload.TYPE,
                OpenEnchantTableScreenC2SPayload.STREAM_CODEC,
                OpenEnchantTableScreenC2SPayload::handlePayloadOnServerNetwork);

        registrar.commonToServer(
                PlayerPurchaseEnchantedBookC2SPayload.TYPE,
                PlayerPurchaseEnchantedBookC2SPayload.STREAM_CODEC,
                PlayerPurchaseEnchantedBookC2SPayload::handlePayloadOnServerNetwork);

        registrar.commonToClient(
                TriggerRebirthItemEffectS2CPayload.TYPE,
                TriggerRebirthItemEffectS2CPayload.STREAM_CODEC,
                TriggerRebirthItemEffectS2CPayload::handlePayloadOnClientMain);
    }

    @SubscribeEvent
    public static void onItemUsedOnBlock(UseItemOnBlockEvent event) {
        Level level = event.getUseOnContext().getLevel();
        BlockPos useOnPos = event.getUseOnContext().getClickedPos();

        if (level instanceof ServerLevel serverLevel) {

            ItemStack usedItem = event.getItemStack();
            boolean isVanillaEnchantingTable = level.getBlockState(useOnPos).is(Blocks.ENCHANTING_TABLE);
            boolean isNewEnchantingTable = level.getBlockState(useOnPos).is(ModBlocks.RECHANTED_TABLE_BLOCK);

            // If player right clicks on vanilla enchanting table block with an emerald,
            // the block becomes our modded enchanting table block (circumvents having to
            // craft it)
            if (usedItem.is(Items.EMERALD) && isVanillaEnchantingTable) {

                // level.destroyBlock(useOnPos, false);
                usedItem.setCount(usedItem.getCount() - 1);

                BlockState newBlockState = ModBlocks.RECHANTED_TABLE_BLOCK.get().defaultBlockState();
                try {
                    Direction dir = event.getPlayer().getDirection();
                    newBlockState = newBlockState.setValue(BlockStateProperties.FACING, dir.getCounterClockWise());
                } catch (NullPointerException ignored) {
                }
                level.setBlock(useOnPos, newBlockState, 3);
                level.playSound(null, useOnPos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.5f, 1.0f);
                level.playSound(null, useOnPos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.4f, 2.5f);

                Random rand = new Random();

                double x = rand.nextDouble(-0.6, 0.6);
                double y = rand.nextDouble(-0.2, 0.2);
                double z = rand.nextDouble(-0.6, 0.6);

                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, useOnPos.getX() + 0.5, useOnPos.getY() + 1.0,
                        useOnPos.getZ() + 0.5, 13, x, y, z, 2.5);

                AdvancementHelper.awardUpgradeEnchantingTableAdvancement(event.getPlayer(), serverLevel);

                event.cancelWithResult(ItemInteractionResult.CONSUME);
            }

            // Allow the player to right-click our modded enchanting table block with lapis
            // and transfer
            // all possible lapis directly to the table's lapis slot; should make things
            // convenient for some,
            // even though I'd rather just open the menu and shift-click, you never know :)
            else if (usedItem.is(Items.LAPIS_LAZULI) && isNewEnchantingTable) {

                RechantedTableBlockEntity rbe = (RechantedTableBlockEntity) level.getBlockEntity(useOnPos);
                ItemStack lapisStack = rbe.getItemHandlerLapisStack();
                ItemStack replaceItemStack = new ItemStack(Items.LAPIS_LAZULI);

                int maxStackSize = (lapisStack.is(Items.AIR)) ? 64 : lapisStack.getMaxStackSize();
                int availableLapisSpace = maxStackSize - lapisStack.getCount();

                // Replaces the item stack with a new one since just setting the count doesn't
                // immediately
                // update to clients and therefore the renderer. Is there a better way? Idk.
                if (availableLapisSpace > 0) {

                    int toPlace = Math.min(availableLapisSpace, usedItem.getCount());
                    replaceItemStack.setCount(lapisStack.getCount() + toPlace);
                    rbe.getItemHandler().setStackInSlot(0, replaceItemStack);

                    usedItem.setCount(usedItem.getCount() - toPlace);

                    event.getLevel().playSound(null, useOnPos, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0f, 1.0f);

                    event.cancelWithResult(ItemInteractionResult.CONSUME);
                } else {
                    event.cancelWithResult(ItemInteractionResult.FAIL);
                }
            }

        }
    }

    @SubscribeEvent
    public static void onItemToolTip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        if (event.getEntity() == null)
            return;

        if (stack.getItem() instanceof EnchantedBookItem) {
            tooltip.add(Component.translatable("tooltip.rechanted.vanilla_book_disabled").withStyle(ChatFormatting.RED));
        }

        else if (stack.isEnchanted()) {

            ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
            if (enchantments == null || enchantments.isEmpty()) {
                if (stack.getOrDefault(ModDataComponents.REBORN, false)) {
                    addRebornTooltipAtTop(tooltip);
                }
                return;
            }

            List<Object2IntMap.Entry<Holder<Enchantment>>> enchantsSorted = new ArrayList<>(enchantments.entrySet());
            boolean hasRebornState = stack.getOrDefault(ModDataComponents.REBORN, false);
            boolean hasEnchantmentDescriptions = hasEnchantmentDescriptionsLoaded();

            enchantsSorted.sort((component1, component2) -> {
                String enchantmentRaw1 = component1.getKey().unwrapKey().get().location().toString();
                String enchantmentRaw2 = component2.getKey().unwrapKey().get().location().toString();

                BookRarityProperties rarity1 = UtilFunctions.getPropertiesFromEnchantment(enchantmentRaw1);
                BookRarityProperties rarity2 = UtilFunctions.getPropertiesFromEnchantment(enchantmentRaw2);

                float rarityValue1 = 0f;
                float rarityValue2 = 0f;

                if (rarity1 == null && component1.getKey().is(EnchantmentTags.CURSE)) {
                    rarityValue1 = 99.0f;
                } else if (rarity1 != null) {

                    rarityValue1 = rarity1.rarity;
                }

                if (rarity2 == null && component2.getKey().is(EnchantmentTags.CURSE)) {
                    rarityValue2 = 99.0f;
                } else if (rarity2 != null) {
                    rarityValue2 = rarity2.rarity;
                }

                return Float.compare(rarityValue2, rarityValue1);
            });

            if (hasEnchantmentDescriptions) {
                // Compatibility path:
                // Keep rarity/curse coloring, but do not reorder lines to avoid index conflicts
                // with Enchantment Descriptions.
                Set<Integer> consumedIndices = new HashSet<>();
                for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantsSorted) {
                    Component fullEnchantName = Enchantment.getFullname(entry.getKey(),
                            enchantments.getLevel(entry.getKey()));
                    String translatedString = fullEnchantName.getString();

                    for (int tooltipIndex = 0; tooltipIndex < tooltip.size(); tooltipIndex++) {
                        if (consumedIndices.contains(tooltipIndex)) {
                            continue;
                        }

                        if (tooltip.get(tooltipIndex).getString().equalsIgnoreCase(translatedString)) {
                            tooltip.set(tooltipIndex,
                                    fullEnchantName.copy().withStyle(getStyleForEnchantment(entry.getKey())));
                            consumedIndices.add(tooltipIndex);
                            break;
                        }
                    }
                }
            } else {
                // First pass:
                // Only replace line indices that match an enchantment's translated name.
                // This is technically a foolproof way of doing this but is very slow and I hate
                // to do it like this.
                // If there's a better way to accomplish the same thing then this def should be
                // refactored.
                int enchantmentTooltipsStartIndex = tooltip.size();
                for (int i = 1; i <= enchantsSorted.size(); i++) {

                    Object2IntMap.Entry<Holder<Enchantment>> entry = enchantsSorted.get(i - 1);
                    Component fullEnchantName = Enchantment.getFullname(entry.getKey(),
                            enchantments.getLevel(entry.getKey()));
                    String translatedString = fullEnchantName.getString();
                    Optional<Component> replacedComponent = tooltip.stream().filter((text) -> {
                        String existingTranslated = text.getString();
                        if (existingTranslated.equalsIgnoreCase(translatedString)) {
                            return true;
                        }
                        return false;
                    }).findFirst();
                    if (replacedComponent.isPresent()) {
                        int replaceIndex = tooltip.indexOf(replacedComponent.get());
                        enchantmentTooltipsStartIndex = Math.min(replaceIndex, enchantmentTooltipsStartIndex);
                    }
                }

                // Second pass:
                // Now actually replace tooltips of enchantments in our desired order.
                int replacementIndex = enchantmentTooltipsStartIndex;
                for (int i = 1; i <= enchantsSorted.size(); i++) {
                    Object2IntMap.Entry<Holder<Enchantment>> entry = enchantsSorted.get(i - 1);
                    Component fullEnchantName = Enchantment.getFullname(entry.getKey(),
                            enchantments.getLevel(entry.getKey()));
                    tooltip.set(replacementIndex++,
                            fullEnchantName.copy().withStyle(getStyleForEnchantment(entry.getKey())));
                }
            }

            if (hasRebornState) {
                addRebornTooltipAtTop(tooltip);
            }
        } else if (stack.getOrDefault(ModDataComponents.REBORN, false)) {
            addRebornTooltipAtTop(tooltip);
        }

    }

    private static boolean hasEnchantmentDescriptionsLoaded() {
        return ModList.get().isLoaded(ENCHANTMENT_DESCRIPTIONS_MOD_ID);
    }

    private static Style getStyleForEnchantment(Holder<Enchantment> enchantmentHolder) {
        if (enchantmentHolder.is(EnchantmentTags.CURSE)) {
            return Style.EMPTY.withColor(ChatFormatting.RED);
        }

        String enchantmentRaw = enchantmentHolder.unwrapKey().get().location().toString();
        BookRarityProperties rarityProperties = UtilFunctions.getPropertiesFromEnchantment(enchantmentRaw);
        if (rarityProperties != null) {
            return Style.EMPTY.withColor(rarityProperties.color);
        }

        return Style.EMPTY;
    }

    private static void addRebornTooltipAtTop(List<Component> tooltip) {
        Component rebornText = Component.translatable("enchantment.rechanted.reborn")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE).withBold(true));
        String rebornTextString = rebornText.getString();
        boolean alreadyPresent = tooltip.stream()
                .anyMatch(component -> component.getString().equalsIgnoreCase(rebornTextString));
        if (alreadyPresent) {
            return;
        }

        int rebornInsertIndex = Math.min(1, tooltip.size());
        tooltip.add(rebornInsertIndex, rebornText);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {

        // Prevents teleporting player drops
        if (event.getEntity() instanceof Player)
            return;

        if ((event.getSource().getEntity() instanceof Player player)) {
            ItemStack weapon = player.getMainHandItem();
            int telekinesisEnchantment = UtilFunctions.getEnchantmentFromItem("rechanted:telekinesis", weapon,
                    event.getEntity().registryAccess());
            if (telekinesisEnchantment == 0)
                return;

            for (ItemEntity item : event.getDrops()) {
                TelekinesisEnchantmentHandler.markItemEntityForTelekinesis(item, player);
            }
        }
    }

    @SubscribeEvent
    public static void onExpDropFromHostile(LivingExperienceDropEvent event) {
        if (RechantedCommonConfigs.DISABLE_XP_FROM_SLIMES_AND_SILVERFISH.get()
                && (event.getEntity() instanceof Slime || event.getEntity() instanceof Silverfish)) {
            event.setDroppedExperience(0);
            return;
        }

        MobCategory mobCategory = event.getEntity().getType().getCategory();
        if (event.getAttackingPlayer() == null)
            return;
        ItemStack weapon = event.getAttackingPlayer().getMainHandItem();
        int inquisitiveEnchantmentLevel = UtilFunctions.getEnchantmentFromItem("rechanted:inquisitive", weapon,
                event.getAttackingPlayer().registryAccess());
        int telekinesisEnchantment = UtilFunctions.getEnchantmentFromItem("rechanted:telekinesis", weapon,
                event.getAttackingPlayer().registryAccess());

        int expToDrop = event.getDroppedExperience();

        if (inquisitiveEnchantmentLevel != 0 && mobCategory == MobCategory.MONSTER) {
            expToDrop = (int) InquisitiveEnchantmentEffect.trueProcess(inquisitiveEnchantmentLevel,
                    RandomSource.create(), (float) expToDrop);
        }

        if (telekinesisEnchantment != 0) {
            Player player = event.getAttackingPlayer();
            if (expToDrop > 0) {
                ExperienceOrb expOrb = new ExperienceOrb(
                        event.getAttackingPlayer().level(),
                        event.getEntity().getX(),
                        event.getEntity().getY() + 0.5D,
                        event.getEntity().getZ(),
                        expToDrop);
                TelekinesisEnchantmentHandler.markExperienceOrbForTelekinesis(expOrb, player);
                event.getAttackingPlayer().level().addFreshEntity(expOrb);
            }
            event.setDroppedExperience(0);
        } else {
            event.setDroppedExperience(expToDrop);
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        // Turn off vanilla enchanted book from applying
        if (left.getItem() instanceof EnchantedBookItem || right.getItem() instanceof EnchantedBookItem) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onGrindstoneChange(GrindstoneEvent.OnPlaceItem event) {

        ItemStack topSlot = event.getTopItem();
        ItemStack bottomSlot = event.getBottomItem();

        boolean rechantedBookInTopOnly = (!topSlot.isEmpty() && topSlot.is(ModItems.RECHANTED_BOOK.get())
                && bottomSlot.isEmpty());
        boolean rechantedBookInBottomOnly = (!bottomSlot.isEmpty() && bottomSlot.is(ModItems.RECHANTED_BOOK.get())
                && topSlot.isEmpty());

        if (rechantedBookInTopOnly || rechantedBookInBottomOnly) {

            ItemStack currentStack = rechantedBookInTopOnly ? topSlot : bottomSlot;

            ItemEnchantments enchantments = currentStack.get(DataComponents.STORED_ENCHANTMENTS);
            Holder<Enchantment> enchantmentHolder = enchantments.entrySet().iterator().next().getKey();
            String enchantmentRaw = enchantmentHolder.unwrapKey().orElseThrow().location().toString();
            BookRarityProperties enchantRarityInfo = UtilFunctions.getPropertiesFromEnchantment(enchantmentRaw);

            Random rand = new Random();
            event.setXp(rand.nextInt(enchantRarityInfo.minGrindstoneXP, enchantRarityInfo.maxGrindstoneXP + 1));

            ResourceLocation itemLocation = ResourceLocation
                    .parse(RechantedCommonConfigs.GRINDSTONE_RESULT_ITEM.get());
            event.setOutput(new ItemStack(BuiltInRegistries.ITEM.get(itemLocation)));
        }

    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.isNewChunk()) {
            event.getChunk().findBlocks((blockState -> blockState.is(Blocks.ENCHANTING_TABLE)),
                    ((blockPos, blockState) -> {
                        event.getChunk().setBlockState(blockPos,
                                ModBlocks.RECHANTED_TABLE_BLOCK.get().defaultBlockState(), false);
                    }));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        AdvancementHelper.clearMysteriousBookOpenTracker(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (player.tickCount % 5 != 0) {
            return;
        }

        if (player.tickCount % 40 == 0) {
            AdvancementHelper.awardExcaliburAdvancementIfEligible(player, serverLevel);
        }

        Inventory inventory = player.getInventory();
        for (ItemStack stack : inventory.items) {
            processGemObtainedState(player, serverLevel, stack);
            processBookObtainedState(player, serverLevel, stack);
            announceFoundBook(player, stack);
            announceFoundGem(player, stack);
        }
        for (ItemStack stack : inventory.offhand) {
            processGemObtainedState(player, serverLevel, stack);
            processBookObtainedState(player, serverLevel, stack);
            announceFoundBook(player, stack);
            announceFoundGem(player, stack);
        }
    }

    // Extremely silly regex pattern, but if any random string in any tooltips matches this
    // pattern I'd be really really surprised.
    private static final Pattern ICON_TOOLTIP_TOKEN = Pattern.compile("\\|\\| ([a-z0-9_.-]+:[a-z0-9_./-]+) \\|\\|");

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onGather(RenderTooltipEvent.GatherComponents event) {
        if (!event.getItemStack().is(ModItems.RECHANTED_BOOK)) return;
        var elements = event.getTooltipElements();

        int numElements = elements.size(); // Doing add-while-iterating nastiness but it simplifies line breaking
        for (int i = 0; i < numElements; i++) {

            ArrayList<ItemStack> tooltipStack = new ArrayList<>();

            Optional<FormattedText> left = elements.get(i).left();
            if (left.isEmpty()) continue;

            // REGEX FOR "|| (anything) ||", grab all instances of this.
            // Check each, and if it exists in "TOOLTIP_ICON_ITEMS" grab that item stack and add
            // that stack to dynamic array of item stacks. Pass that into tooltip component.
            Matcher matcher = ICON_TOOLTIP_TOKEN.matcher(left.get().getString());
            while (matcher.find()) {
                String iconIdentifier = matcher.group();
                HashMap<String, ItemStack> tooltipIconItems = RechantedBookItem.getTooltipIconItems();
                if (tooltipIconItems.containsKey(iconIdentifier)) {
                    tooltipStack.add(tooltipIconItems.get(iconIdentifier));
                }
            }

            if (!tooltipStack.isEmpty()) {

                // Fits on one row, just replace
                if (tooltipStack.size() <= 10) {
                    elements.set(i, Either.right(new InlineItemStackIconsComponent.IconRowTooltip(tooltipStack)));
                }
                else {

                    // Dead simple lazy ass loop that forces 10 item stacks max per line of tooltip.
                    int rowCount = (tooltipStack.size() / 10) + 1;
                    for (int r = 0; r < rowCount; r++) {
                        ArrayList<ItemStack> splitStack = new ArrayList<>(tooltipStack.subList(r * 10, Math.min(tooltipStack.size(), (r * 10) + 10)));

                        if (r == 0)
                            elements.set(i, Either.right(new InlineItemStackIconsComponent.IconRowTooltip(splitStack)));
                        else
                            elements.add(i + r, Either.right(new InlineItemStackIconsComponent.IconRowTooltip(splitStack)));
                    }

                    numElements += rowCount - 1;
                    i += rowCount - 1;
                }
            }
        }
    }

    private static void announceFoundBook(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        if (!stack.getOrDefault(ModDataComponents.ANNOUNCE_ON_FOUND, false)) {
            return;
        }

        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (enchantments.isEmpty()) {
            enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        }

        if (enchantments.isEmpty()) {
            stack.remove(ModDataComponents.ANNOUNCE_ON_FOUND);
            return;
        }

        Object2IntMap.Entry<Holder<Enchantment>> entry = enchantments.entrySet().iterator().next();
        Holder<Enchantment> enchantmentHolder = entry.getKey();
        int enchantmentLevel = enchantments.getLevel(enchantmentHolder);
        String enchantmentRaw = enchantmentHolder.unwrapKey().map(key -> key.location().toString()).orElse("");

        BookRarityProperties bookProps = UtilFunctions.getPropertiesFromEnchantment(enchantmentRaw);
        Style enchantStyle = Style.EMPTY;
        if (bookProps != null) {
            enchantStyle = enchantStyle.withColor(bookProps.color).withUnderlined(true);
        }
        enchantStyle = enchantStyle.withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                new HoverEvent.ItemStackInfo(stack.copyWithCount(1))));

        Component enchantName = Enchantment.getFullname(enchantmentHolder, enchantmentLevel).copy()
                .withStyle(enchantStyle);
        Component playerName = player.getDisplayName();

        MutableComponent message = Component.empty()
                .append(playerName)
                .append(Component.literal(" found "))
                .append(enchantName);

        if (stack.has(ModDataComponents.SUCCESS_RATE)) {
            int successRate = stack.getOrDefault(ModDataComponents.SUCCESS_RATE, 0);
            message = message
                    .append(Component.literal(" at "))
                    .append(Component.literal(successRate + "%").withStyle(enchantStyle));
        }

        message = message.append(Component.literal("!"));

        if (player.level() instanceof ServerLevel level) {
            for (Player otherPlayer : level.players()) {
                otherPlayer.sendSystemMessage(message);
            }

            level.playSound(null, player.getOnPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1f,
                    1f);
        }

        stack.remove(ModDataComponents.ANNOUNCE_ON_FOUND);
    }

    private static void announceFoundGem(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        if (!stack.getOrDefault(ModDataComponents.SHOULD_ANNOUNCE_GEM, false)) {
            return;
        }

        Style gemStyle = Style.EMPTY.withColor(ChatFormatting.AQUA).withUnderlined(true);
        if (stack.is(ModItems.SHINY_CHANCE_GEM.get())) {
            gemStyle = gemStyle.withColor(ChatFormatting.LIGHT_PURPLE);
        }

        gemStyle = gemStyle.withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                new HoverEvent.ItemStackInfo(stack.copyWithCount(1))));

        Component gemName = stack.getHoverName().copy().withStyle(gemStyle);
        Component playerName = player.getDisplayName();

        MutableComponent message = Component.empty()
                .append(playerName)
                .append(Component.literal(" found a "))
                .append(gemName)
                .append(Component.literal("!"));

        if (player.level() instanceof ServerLevel level) {
            for (Player otherPlayer : level.players()) {
                otherPlayer.sendSystemMessage(message);
            }

            level.playSound(null, player.getOnPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1f, 1f);
        }

        stack.remove(ModDataComponents.SHOULD_ANNOUNCE_GEM);
    }

    private static void processGemObtainedState(Player player, ServerLevel level, ItemStack stack) {
        if (stack.isEmpty() || !AdvancementHelper.isTrackedGem(stack)) {
            return;
        }
        if (hasBeenObtained(stack)) {
            return;
        }

        markAsObtained(stack);
        AdvancementHelper.awardGemPickupAdvancements(player, level, stack);
    }

    private static void processBookObtainedState(Player player, ServerLevel level, ItemStack stack) {
        if (stack.isEmpty() || !AdvancementHelper.isTrackedBook(stack)) {
            return;
        }

        if (hasBeenObtained(stack)) {
            return;
        }

        markAsObtained(stack);

        AdvancementHelper.awardUnforgottenAdvancementIfEligible(player, level, stack);
        AdvancementHelper.awardLegendaryPullAdvancementIfEligible(player, level, stack);
        AdvancementHelper.awardArchmageProgressFromBook(player, level, stack);
    }

    private static boolean hasBeenObtained(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.OBTAINED, false);
    }

    private static void markAsObtained(ItemStack stack) {
        stack.set(ModDataComponents.OBTAINED, true);
    }
}
