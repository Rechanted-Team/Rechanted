package net.tagtart.rechanted.networking.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.tagtart.rechanted.Rechanted;
import net.tagtart.rechanted.block.entity.RechantedTableBlockEntity;
import net.tagtart.rechanted.component.ModDataComponents;
import net.tagtart.rechanted.item.ModItems;
import net.tagtart.rechanted.screen.RechantedTableMenu;
import net.tagtart.rechanted.util.AdvancementHelper;
import net.tagtart.rechanted.util.BonusItemResolver;
import net.tagtart.rechanted.util.BookRarityProperties;
import net.tagtart.rechanted.util.EnchantmentPoolEntry;
import net.tagtart.rechanted.util.UtilFunctions;

import java.util.*;

import static net.tagtart.rechanted.util.BookRarityProperties.BonusPoolEntryType.*;

public record PlayerPurchaseEnchantedBookC2SPayload(int bookPropertiesIndex, BlockPos enchantTablePos) implements CustomPacketPayload {


    public static final Type<PlayerPurchaseEnchantedBookC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "player_purchase_book"));

    public static final StreamCodec<ByteBuf, PlayerPurchaseEnchantedBookC2SPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PlayerPurchaseEnchantedBookC2SPayload::bookPropertiesIndex,
            BlockPos.STREAM_CODEC,
            PlayerPurchaseEnchantedBookC2SPayload::enchantTablePos,
            PlayerPurchaseEnchantedBookC2SPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Docs show making a whole new class just to hold static functions like this but that seems dumb and hard to manage.
    // Just gonna keep payloads' handle functions in their own class.
    public static void handlePayloadOnServerNetwork(final PlayerPurchaseEnchantedBookC2SPayload payload, final IPayloadContext context) {


        context.enqueueWork(() -> {
            ServerPlayer player = null;
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            player = (ServerPlayer)context.player();
            ServerLevel level = player.serverLevel();

            Inventory playerInventory = player.getInventory();
            BookRarityProperties bookProperties = BookRarityProperties.getAllProperties()[payload.bookPropertiesIndex];
            BlockEntity blockEntity = level.getBlockEntity(payload.enchantTablePos);
            RechantedTableBlockEntity enchTableEntity = (blockEntity instanceof RechantedTableBlockEntity) ?
                    (RechantedTableBlockEntity)blockEntity : null;

            if (enchTableEntity == null)
                return;

            var bookshelves = UtilFunctions.scanAroundBlockForBookshelves(level, payload.enchantTablePos);
            var floorBlocks = UtilFunctions.scanAroundBlockForValidFloors(bookProperties.floorBlock, level, payload.enchantTablePos);

            boolean meetsEXPRequirement = UtilFunctions.playerMeetsExpRequirement(bookProperties, player);
            boolean meetsBookshelfRequirement = UtilFunctions.playerMeetsBookshelfRequirement(bookProperties, bookshelves.getA());
            boolean meetsFloorBlocksRequirement = UtilFunctions.playerMeetsFloorRequirement(bookProperties, floorBlocks.getA());
            boolean meetsLapisRequirement = UtilFunctions.playerMeetsLapisRequirement(bookProperties, enchTableEntity.getItemHandlerLapisStack());
            boolean validEntityState = enchTableEntity.tableState == RechantedTableBlockEntity.CustomRechantedTableState.Normal;

            PurchaseBookResultCase failCase = PurchaseBookResultCase.SUCCESS;

            // Check if player inventory is full. If so, return failure.
            if (playerInventory.getFreeSlot() == -1) failCase = PurchaseBookResultCase.INVENTORY_FULL;

                // Check basic requirements with helper methods.
            else if (!meetsEXPRequirement) failCase = PurchaseBookResultCase.INSUFFICIENT_EXP;
            else if (!meetsBookshelfRequirement) failCase = PurchaseBookResultCase.INSUFFICIENT_BOOKS;
            else if (!meetsFloorBlocksRequirement) failCase = PurchaseBookResultCase.INSUFFICIENT_FLOOR;
            else if (!meetsLapisRequirement) failCase = PurchaseBookResultCase.INSUFFICIENT_LAPIS;
            else if (!validEntityState) failCase = PurchaseBookResultCase.BONUS_PENDING;

            // At this point, should be good to go. Can destroy blocks and reward the book.
            else
            {
                SoundEvent soundToPlay = SoundEvents.EXPERIENCE_ORB_PICKUP;
                Random random = new Random();

                // Destroy block at that position if rolls to do it. Also don't check more than
                // the number of shelves that are actually required just in case they all roll to break somehow.
                // -----
                // Also note that this procedure doesn't check the block type; we assume the scan earlier
                // already filtered the list to only have valid config'd blocks!
                ArrayList<Integer> randomIndices = new ArrayList<>();
                for (int i = 0; i < bookshelves.getB().length; ++i) {
                    randomIndices.add(i);
                }
                Collections.shuffle(randomIndices);
                for (int i = 0; i < bookshelves.getB().length && i < bookProperties.requiredBookShelves; ++i) {
                    int bookIndex = randomIndices.get(i);

                    BlockState state = bookshelves.getA()[bookIndex];
                    BlockPos position = bookshelves.getB()[bookIndex];

                    if (random.nextFloat() < bookProperties.bookBreakChance) {

                        // Edge case: if chiseled bookshelf, remove 3 books from it before destroying it
                        // (it normally drops all items in its inventory by default)
                        if (state.is(Blocks.CHISELED_BOOKSHELF)) {
                            ChiseledBookShelfBlockEntity chBkEntity = (ChiseledBookShelfBlockEntity) level.getBlockEntity(position);
                            if (chBkEntity != null)
                                UtilFunctions.scanBlockInventoryForBooks(chBkEntity, true);
                        }

                        level.destroyBlock(position, false);
                    }
                }

                // Do same for floor blocks, with extra logic prevent the block under
                // the table from breaking if possible.
                HashSet<Integer> remainingBlocks = new HashSet<>();
                BlockPos[] floorPositions = floorBlocks.getB();
                for (int i = 0; i < floorPositions.length; ++i)
                    remainingBlocks.add(i);

                int underTableIndex = -1; // If still -1, don't offset the block under table's destruction to another block!
                for (int i = 0; i < floorPositions.length; ++i) {
                    BlockPos position = floorPositions[i];
                    if (random.nextFloat() < bookProperties.floorBreakChance) {

                        if (position.getX() == payload.enchantTablePos.getX() && position.getZ() == payload.enchantTablePos.getZ()) {
                            underTableIndex = i;
                        }
                        else {
                            level.destroyBlock(position, false);
                        }
                        remainingBlocks.remove(i);
                    }
                }

                // This will be true if the center block (under the enchant table) was rolled to be destroyed.
                // This should hopefully pick a random block that hasn't been destroyed. If all others were destroyed,
                // then the center block itself DOES get destroyed since no other blocks remain.
                if (underTableIndex != -1) {
                    ArrayList<Integer> remainingBlocksIterable = new ArrayList<>(remainingBlocks);
                    int randomBlock = underTableIndex;
                    if (!remainingBlocksIterable.isEmpty())
                        randomBlock = remainingBlocksIterable.get(random.nextInt(remainingBlocksIterable.size()));

                    BlockPos position = floorPositions[randomBlock];
                    level.destroyBlock(position, false);
                }

                // Remove EXP and Lapis from player.
                //player.giveExperiencePoints(-Math.min(player.totalExperience, bookProperties.requiredExp));
                player.giveExperiencePoints(-Math.min(UtilFunctions.getPlayerExperiencePoints(player), bookProperties.requiredExp));
                enchTableEntity.getItemHandlerLapisStack().shrink(bookProperties.requiredLapis);

                ItemStack toGive = new ItemStack(ModItems.RECHANTED_BOOK.get());
                EnchantmentPoolEntry randomEnchantment = bookProperties.getRandomEnchantmentWeighted();
                int randomEnchantmentLevel = randomEnchantment.getRandomEnchantLevelWeighted();


                int successRate = Math.min(100, bookProperties.maxSuccess);
                if (bookProperties.minSuccess < bookProperties.maxSuccess) {
                    successRate = random.nextInt(bookProperties.minSuccess, bookProperties.maxSuccess + 1);
                }

                ItemEnchantments.Mutable storedEnchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

                Holder.Reference<Enchantment> enchantment = UtilFunctions.getEnchantmentReferenceIfPresent(level.registryAccess(), randomEnchantment.enchantment);
                if (enchantment == null)
                    return; // This should basically never happen, but return early just in case

                storedEnchants.set(enchantment, randomEnchantmentLevel);

                toGive.set(DataComponents.STORED_ENCHANTMENTS, storedEnchants.toImmutable().withTooltip(false));
                toGive.set(ModDataComponents.SUCCESS_RATE, successRate);

                if (UtilFunctions.shouldAnnounceDrop(randomEnchantment.enchantment, randomEnchantmentLevel))
                    toGive.set(ModDataComponents.ANNOUNCE_ON_FOUND, true);

                // Give enchanted book
                // Note: had to make the call to set the item directly in inventory to have announced message
                player.getInventory().setItem(player.getInventory().getFreeSlot(), toGive);
                AdvancementHelper.awardEnchantItemAdvancement(player, level);

                if (random.nextDouble() < bookProperties.bonusItemRollChance) {
                    Optional<BookRarityProperties.BonusPoolEntryType> bonusType =  bookProperties.getRandomBonusPoolEntryWeighted(random);
                    Optional<ItemStack> bonusStack = BonusItemResolver.resolveRandomBonusItem(bonusType, random);
                    if (bonusType.isPresent() && bonusStack.isPresent()) {

                        Item bonusItem = bonusStack.get().getItem();
                        if (UtilFunctions.shouldAnnounceGemDrop(bonusItem.getDescriptionId())) {
                            bonusStack.get().set(ModDataComponents.SHOULD_ANNOUNCE_GEM, true);
                        }

                        switch (bonusType.get()) {
                            case MYSTERIOUS_BOOK:
                                enchTableEntity.startLightBonusPendingAnimation(bonusStack.get());
                                break;
                            case COMMON_GEM_POOL:
                                enchTableEntity.startBonusPendingAnimation(bonusStack.get());
                                break;
                            case RARE_GEM_POOL:
                                enchTableEntity.startSuperBonusPendingAnimation(bonusStack.get());
                                break;

                            default:

                        }

                        // For triggering the rainbow effect in the enchanter screen shader.
                        if (player.containerMenu instanceof RechantedTableMenu rechantedTableMenu) {
                            if (bonusType.get() == MYSTERIOUS_BOOK) {
                                rechantedTableMenu.lightBonusEarnedEffectQueued.set(rechantedTableMenu.lightBonusEarnedEffectQueued.get() + 1);
                            }
                            else {
                                rechantedTableMenu.bonusEarnedEffectQueued.set(rechantedTableMenu.bonusEarnedEffectQueued.get() + 1);
                            }
                            rechantedTableMenu.broadcastChanges();
                        }

                        soundToPlay = SoundEvents.PLAYER_LEVELUP;
                        player.sendSystemMessage(Component.translatable("message.rechanted.bonus_item_found").withStyle(ChatFormatting.GREEN));
                    }
                }

                level.playSound(null, payload.enchantTablePos, soundToPlay, SoundSource.BLOCKS, 1f, 1f);
            }

            sendEnchantResultPlayerMessage(player, failCase);
        });
}

// This is specifically for server side checks. If there is a de-sync of some kind, player will always
// be forced out of their container and a message sent (unlike on client side, where behavior/message is result-dependent).
private static void sendEnchantResultPlayerMessage(Player player, PurchaseBookResultCase failCase) {
    if (failCase == PurchaseBookResultCase.SUCCESS) return;

    player.closeContainer();
    switch(failCase) {
        case INVENTORY_FULL:
            // No message; a sound should play since this is a common issue preventing purchase and not a desync.
            break;
        case INSUFFICIENT_EXP:
            player.sendSystemMessage(Component.literal("Server desync error: Player does not have enough XP!"));
            break;
        case INSUFFICIENT_BOOKS:
            player.sendSystemMessage(Component.literal("Server desync error: Insufficient book count around table!"));
            break;
        case INSUFFICIENT_FLOOR:
            player.sendSystemMessage(Component.literal("Server desync error: Insufficient 3x3 floor around table!"));
            break;
        case INSUFFICIENT_LAPIS:
            player.sendSystemMessage(Component.literal("Server desync error: Not enough lapis lazuli in enchantment table!"));
            break;
        case BONUS_PENDING:
            player.sendSystemMessage(Component.literal("Server desync error: Can't purchase book while a bonus item is pending!"));
            break;
    }

}


}
