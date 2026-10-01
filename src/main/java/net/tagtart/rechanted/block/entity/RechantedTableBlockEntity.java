package net.tagtart.rechanted.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.tagtart.rechanted.screen.RechantedTableMenu;
import net.tagtart.rechanted.event.ItemEntityTrailHandler;
import net.tagtart.rechanted.sound.ModSounds;
import net.tagtart.rechanted.util.AdvancementHelper;
import net.tagtart.rechanted.util.AnimHelper;
import net.tagtart.rechanted.util.BookRarityProperties;
import net.tagtart.rechanted.util.UtilFunctions;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RechantedTableBlockEntity extends EnchantingTableBlockEntity implements MenuProvider {

    // For super basic state-machine-esque logic; mainly allows the block renderer/clients to know
    // how to render the book based on custom logic happening server side.
    public enum CustomRechantedTableState {
        // Keep order stable for NBT ordinal compatibility.
        Normal,             // Normal state. In this state 99.99% of the time.
        LightBonusPending,  // Server rolled a bonus item that is not deserving of a crazy anim, book will close then open
        LightBonusEarned,   // After light bonus pending is done, item spawns and book simply closes
        BonusPending,       // Server rolled bonus item, book will begin floating up in the air before it's earned.
        BonusEarned,        // After bonus pending is done, item spawns and book floats back down to normal position.
        SuperBonusPending,  // Server rolled RARE bonus item. Very similar as BonusPending, but with small extra flare.
        SuperBonusEarned,   // Identical to BonusEarned animation wise, but separating it out in case we want to change that.
    }

    public static final int BONUS_PENDING_ANIMATION_LENGTH_TICKS = 130;
    public static final int BONUS_EARNED_ANIMATION_LENGTH_TICKS = 20;

    public static final double BONUS_EARNED_ITEM_SPAWN_Y_OFFSET = 1.5;
    public static final double BONUS_EARNED_ITEM_MOVE_SPEED_ON_SPAWN = 0.3;  // Speed item will move when spawned by table; moves in facing direction of lapis holder.

    public static final int LIGHT_BONUS_PENDING_ANIMATION_LENGTH_TICKS = 80;
    public static final int LIGHT_BONUS_EARNED_ANIMATION_LENGTH_TICKS = 20;

    public static final double LIGHT_BONUS_EARNED_ITEM_SPAWN_Y_OFFSET = .5;
    public static final double LIGHT_BONUS_EARNED_ITEM_MOVE_SPEED_ON_SPAWN = 0.42;  // Slightly faster so light bonus drops don't stall near the table.
    public static final float LIGHT_BONUS_EARNED_ITEM_LAUNCH_Y_BIAS = 1.8f;  // Higher value = higher arc; reduces horizontal distance due normalize().

    // NOTE: THESE VALUES ARE HERE FOR CLARITY'S SAKE, BUT IF WE CHANGE THEM FROM 130 AND 20, WE WILL HAVE TO
    // MAKE SOME NEW KEYFRAMES IN THE RENDERER FOR THIS STATE. IT CURRENTLY RE-USES SOME FROM BONUS PENDING
    // AND THAT ONLY WORKS BECAUSE BOTH STATES LAST THE SAME AMOUNT OF TICKS!
    public static final int SUPER_BONUS_PENDING_ANIMATION_LENGTH_TICKS = 130;
    public static final int SUPER_BONUS_EARNED_ANIMATION_LENGTH_TICKS = 20;
    
    public static final Vec3 UP = new Vec3(0f, 1f, 0f);
    public static final Vec3 NORTH = new Vec3(0f, 0f, 1f);

    public static final ArrayList<AnimHelper.FloatKeyframe> SUPER_BONUS_PENDING_ORBS_RADIUS_KEYFRAMES = new ArrayList(
            List.of(
                    new AnimHelper.FloatKeyframe(0.0f, 0.0f, AnimHelper::easeOutBack),
                    new AnimHelper.FloatKeyframe(90.0f, 1.0f, AnimHelper::linear),
                    new AnimHelper.FloatKeyframe(98.0f, 1.3f, AnimHelper::easeInBack),
                    new AnimHelper.FloatKeyframe(126.0f, 0.0f, AnimHelper::linear)
            )
    );

    // This is the same as the one from the Renderer, but idk if I'll want different values later so it's just copied.
    // (Also can't reference the one from the Renderer or server crashes lol)
    public static final ArrayList<AnimHelper.FloatKeyframe> BONUS_PENDING_Y_TRANSLATION_KEYFRAMES = new ArrayList<>(
            List.of(
                    new AnimHelper.FloatKeyframe(0f, 0f, AnimHelper::easeOutBack),
                    new AnimHelper.FloatKeyframe(110f, 1.15f, AnimHelper::linear)));

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    //private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private double cachedEnchantingPower;
    private BlockState[] cachedFloorBlocksInRange;

    private long totalTicks = 0;
    private int currentIndexRequirementsMet = -1;

    public CustomRechantedTableState tableState = CustomRechantedTableState.Normal;
    public long currentStateTimeRemaining = 0;
    private ItemStack pendingBonusItem = ItemStack.EMPTY;


    public RechantedTableBlockEntity(BlockPos pPos, BlockState pBlockState)
    {
        super(pPos, pBlockState);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ItemStack getItemHandlerLapisStack() { return itemHandler.getStackInSlot(0); }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.RECHANTED_TABLE_BE.get();
    }

    // Makes the object drop the items currently inside the itemHandler slots.
    public void dropInventory() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void onBreak() {
        dropInventory();
        stopAmbientSound();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory inventory, Player player) {
        return new RechantedTableMenu(pContainerId, inventory, this);
    }

    // For saving the data of what is inside the block when the game is saved.
    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
        super.saveAdditional(pTag, registries);
        pTag.put("inventory", itemHandler.serializeNBT(registries));

        pTag.putInt("CustomTableState", tableState.ordinal());
        pTag.putLong("CurrentStateTimeRemaining", currentStateTimeRemaining);

        if (pendingBonusItem != ItemStack.EMPTY) {
            pTag.put("PendingBonus", pendingBonusItem.save(registries));
        }
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
        super.loadAdditional(pTag, registries);
        itemHandler.deserializeNBT(registries, pTag.getCompound("inventory"));

        tableState = CustomRechantedTableState.values()[pTag.getInt("CustomTableState")];
        currentStateTimeRemaining = pTag.getLong("CurrentStateTimeRemaining");

        Optional<ItemStack> loadedBonusItem = ItemStack.parse(registries, pTag.getCompound("PendingBonus"));
        if (loadedBonusItem.isEmpty()) {
            loadedBonusItem = ItemStack.parse(registries, pTag.getCompound("PendingGem"));
        }
        loadedBonusItem.ifPresent(itemStack -> pendingBonusItem = itemStack);
    }

    @Override
    public void handleUpdateTag(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(pTag, lookupProvider);

        tableState = CustomRechantedTableState.values()[pTag.getInt("CustomTableState")];
        currentStateTimeRemaining = pTag.getLong("CurrentStateTimeRemaining");

        Optional<ItemStack> loadedBonusItem = ItemStack.parse(lookupProvider, pTag.getCompound("PendingBonus"));
        if (loadedBonusItem.isEmpty()) {
            loadedBonusItem = ItemStack.parse(lookupProvider, pTag.getCompound("PendingGem"));
        }
        loadedBonusItem.ifPresent(itemStack -> pendingBonusItem = itemStack);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {

        currentStateTimeRemaining--;
        if (!level.isClientSide && tableState != CustomRechantedTableState.Normal) {
            //System.out.println(tableState);
            //System.out.println(currentStateTimeRemaining);
        }

        switch (tableState) {
            case Normal:
                if (totalTicks == 0) {
                    soundLogicOnTick(pPos, pLevel);
                }
                totalTicks++;
                newBookAnimationTick(pLevel, pPos, pState);

                // This is so that any sound that needs to be playing when level is loaded gives player time to load.
                // There's not really an elegant way to do this but most loading takes less than 3 seconds.
                if (totalTicks < 60) {
                    return;
                }

                // Check if requirements are met currently after certain interval passed,
                // then set which book index can currently be crafted for use elsewhere.
                if (totalTicks % 4 == 0)
                {
                    soundLogicOnTick(pPos, pLevel);
                }
                break;
            case BonusPending:

                if (pLevel instanceof ServerLevel serverLevel) {
                    float elapsedFraction = (1.0f - ((float) currentStateTimeRemaining / BONUS_PENDING_ANIMATION_LENGTH_TICKS));

                    if (currentStateTimeRemaining == BONUS_PENDING_ANIMATION_LENGTH_TICKS - 10) {
                        serverLevel.playSound(null, getBlockPos(), ModSounds.ENCHANT_TABLE_CLOSE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    }

                    if (currentStateTimeRemaining % 10 == 0 && currentStateTimeRemaining > 80) {
                        serverLevel.playSound(null, getBlockPos(), ModSounds.ENCHANT_TABLE_OPEN.get(), SoundSource.BLOCKS, 1.0f, 1.1f);
                    }

                    if (currentStateTimeRemaining % 6 == 0 && currentStateTimeRemaining > 20) {
                        float yOffset = AnimHelper.evaluateKeyframes(BONUS_PENDING_Y_TRANSLATION_KEYFRAMES, BONUS_PENDING_ANIMATION_LENGTH_TICKS - currentStateTimeRemaining);

                        sendRainbowCircleParticles(
                                serverLevel,
                                new Vec3(0.0f, yOffset + 0.95f, 0.0f),
                                UP,
                                NORTH,
                                8,
                                0.13f,
                                0.95f,
                                0.95f,
                                1.0f,
                                0,
                                0
                        );
                    }

                    if (currentStateTimeRemaining % 15 == 0 && currentStateTimeRemaining > 15) {
                        float s = 0.92f;
                        float v = Mth.clamp(elapsedFraction + 0.6f, 0.0f, 1.0f);
                        float scale = elapsedFraction + 0.1f;
                        //float yOffset = AnimHelper.evaluateKeyframes(RechantedTableRenderer.BONUS_PENDING_Y_TRANSLATION_KEYFRAMES, BONUS_PENDING_ANIMATION_LENGTH_TICKS - currentStateTimeRemaining);
                        Vec3 pos = new Vec3(0, 0.3f, 0);

                        // Next 3 particle calls are for the ring of particles on top of the table.
                        sendRainbowCircleParticles(
                                serverLevel,
                                pos,
                                UP,
                                NORTH,
                                20,
                                0.8f * elapsedFraction,
                                s,
                                v,
                                scale,
                                0,
                                0
                        );

                        sendRainbowCircleParticles(
                                serverLevel,
                                pos,
                                UP,
                                NORTH,
                                8,
                                1.0f * elapsedFraction,
                                s,
                                v,
                                scale,
                                0,
                                0
                        );

                        sendRainbowCircleParticles(
                                serverLevel,
                                pos,
                                UP,
                                NORTH,
                                8,
                                1.2f * elapsedFraction,
                                s,
                                v,
                                scale,
                                0,
                                0

                        );
                    }

                    if (currentStateTimeRemaining <= 0) {
                        completePendingBonusAnimation();
                    }
                }
                break;
            case BonusEarned:

                if (pLevel instanceof ServerLevel serverLevel) {
                    if (currentStateTimeRemaining <= 0) {
                        returnToDefaultState();
                    }

                    // Needs to play slightly before book lands instead of right when it does; feels off for some reason if not.
                    if (currentStateTimeRemaining == 2) {
                        level.playSound(null, getBlockPos(), ModSounds.ENCHANT_TABLE_CLOSE.get(), SoundSource.BLOCKS, 1.5f, 1.0f);
                    }
                }
                break;

            case LightBonusPending:
                if (pLevel instanceof ServerLevel serverLevel) {
                    if (currentStateTimeRemaining == LIGHT_BONUS_PENDING_ANIMATION_LENGTH_TICKS - 10) {
                        serverLevel.playSound(null, getBlockPos(), ModSounds.ENCHANT_TABLE_OPEN.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    }

                    if (currentStateTimeRemaining % 20 == 0 && currentStateTimeRemaining > 15) {
                        float elapsedFraction = (1.0f - ((float) currentStateTimeRemaining / LIGHT_BONUS_PENDING_ANIMATION_LENGTH_TICKS)) + 0.3f;
                        sendRainbowCircleParticles(
                                serverLevel,
                                new Vec3(0, 0.3f, 0),
                                UP,
                                NORTH,
                                10,
                                0.8f * elapsedFraction,
                                0.9f,
                                Mth.clamp(elapsedFraction + 0.4f, 0.0f, 1.0f),
                                elapsedFraction + 0.1f,
                                0,
                                0
                        );
                    }

                    if (currentStateTimeRemaining <= 0) {
                        completeLightPendingBonusAnimation();
                    }
                }
                break;

            case LightBonusEarned:
                if (pLevel instanceof ServerLevel) {
                    if (currentStateTimeRemaining <= 0) {
                        returnToDefaultState();
                    }

                    // Needs to play slightly before book lands instead of right when it does; feels off for some reason if not.
                    if (currentStateTimeRemaining == 2) {
                        level.playSound(null, getBlockPos(), ModSounds.ENCHANT_TABLE_CLOSE.get(), SoundSource.BLOCKS, 1.5f, 1.0f);
                    }
                }
                break;

            case SuperBonusPending:
                if (pLevel instanceof ServerLevel serverLevel) {
                    float elapsedFraction = (1.0f - ((float) currentStateTimeRemaining / SUPER_BONUS_PENDING_ANIMATION_LENGTH_TICKS));

                    if (currentStateTimeRemaining == BONUS_PENDING_ANIMATION_LENGTH_TICKS - 10) {
                        serverLevel.playSound(null, getBlockPos(), ModSounds.ENCHANT_TABLE_CLOSE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    }

                    if (currentStateTimeRemaining % 10 == 0 && currentStateTimeRemaining > 80) {
                        serverLevel.playSound(null, getBlockPos(), ModSounds.ENCHANT_TABLE_OPEN.get(), SoundSource.BLOCKS, 1.0f, 1.1f);
                    }

                    // For the 3 "Orbs" rotating around the book as it floats up.
                    if (currentStateTimeRemaining > 2) {
                        float t = SUPER_BONUS_PENDING_ANIMATION_LENGTH_TICKS - currentStateTimeRemaining;
                        float yOffset = AnimHelper.evaluateKeyframes(BONUS_PENDING_Y_TRANSLATION_KEYFRAMES, t);
                        float radius = AnimHelper.evaluateKeyframes(SUPER_BONUS_PENDING_ORBS_RADIUS_KEYFRAMES, t);

                        sendRainbowCircleParticles(
                                serverLevel,
                                new Vec3(0.0f, yOffset + 0.4f, 0.0f),
                                UP,
                                NORTH,
                                3,
                                radius,
                                0.95f,
                                0.95f,
                                1.0f,
                                elapsedFraction * (float)Math.PI * 4.0f,
                                0
                        );
                    }

                    if (currentStateTimeRemaining % 15 == 0 && currentStateTimeRemaining > 15) {
                        float s = 0.92f;
                        float v = Mth.clamp(elapsedFraction + 0.6f, 0.0f, 1.0f);
                        float scale = elapsedFraction + 0.1f;
                        //float yOffset = AnimHelper.evaluateKeyframes(RechantmentTableRenderer.BONUS_PENDING_Y_TRANSLATION_KEYFRAMES, BONUS_PENDING_ANIMATION_LENGTH_TICKS - currentStateTimeRemaining);
                        Vec3 pos = new Vec3(0, 0.3f, 0);

                        // Next 3 particle calls are for the ring of particles on top of the table.
                        sendRainbowCircleParticles(
                                serverLevel,
                                pos,
                                UP,
                                NORTH,
                                20,
                                0.8f * elapsedFraction,
                                s,
                                v,
                                scale,
                                0,
                                0
                        );

                        sendRainbowCircleParticles(
                                serverLevel,
                                pos,
                                UP,
                                NORTH,
                                8,
                                1.0f * elapsedFraction,
                                s,
                                v,
                                scale,
                                0,
                                0
                        );

                        sendRainbowCircleParticles(
                                serverLevel,
                                pos,
                                UP,
                                NORTH,
                                8,
                                1.2f * elapsedFraction,
                                s,
                                v,
                                scale,
                                0,
                                0
                        );
                    }

                    if (currentStateTimeRemaining <= 0) {
                        completePendingSuperBonusAnimation();
                    }
                }
                break;
            case SuperBonusEarned:
                if (pLevel instanceof ServerLevel serverLevel) {
                    if (currentStateTimeRemaining <= 0) {
                        UtilFunctions.spawnRandomFireworkExplosion(serverLevel, serverLevel.random, getBlockPos().getX() + 0.5f, getBlockPos().getY() + 1.5f, getBlockPos().getZ() + 0.5f);
                        returnToDefaultState();
                    }

                    if (currentStateTimeRemaining == 2) {
                        level.playSound(null, getBlockPos(), ModSounds.ENCHANT_TABLE_CLOSE.get(), SoundSource.BLOCKS, 1.5f, 1.0f);
                    }
                }
                break;
        }
    }

    public void sendRainbowCircleParticles(ServerLevel serverLevel, Vec3 offset, Vec3 up, Vec3 north, int numParticles, float radius, float saturation, float value, float particleScale, float angleOffset, int overrideRGB) {
        Random rand = new Random();
        float tau = (float)Math.PI * 2.0f;
        for (int i = 0; i < numParticles; ++i) {
            Vec3 particleBasePos = getBlockPos().getCenter();

            float angle = (((float) i / numParticles) * tau) + angleOffset;
            double relativeX = Math.cos(angle);
            double relativeZ = Math.sin(angle);

            Vec3 west = north.cross(up).normalize();
            Vec3 particleDirection = north.scale(relativeX).add(west.scale(relativeZ)).normalize();

            //double hue = (float)particleDirection.dot(north);
            double hue = particleDirection.toVector3f().angleSigned(north.toVector3f(), up.toVector3f());
            hue = UtilFunctions.remap(-Math.PI, Math.PI, 0.0, 1.0, hue);

            int rgb = (overrideRGB != 0) ? overrideRGB : Mth.hsvToRgb((float) hue, saturation, value);

            DustParticleOptions dustParticles = new DustParticleOptions(Vec3.fromRGB24(rgb).toVector3f(), particleScale);
            particleDirection = particleDirection.scale(radius);

            serverLevel.sendParticles(
                    dustParticles,
                    particleBasePos.x + particleDirection.x + offset.x,
                    particleBasePos.y + particleDirection.y + offset.y,
                    particleBasePos.z + particleDirection.z + offset.z,
                    2,
                    0,
                    0,
                    0,
                    0.0

            );
        }
    }


    @Override
    public Component getDisplayName() {
        return Component.literal("Enchanting Table");
    }

    public void stopAmbientSound() {
        if (level.isClientSide()) {
            UtilFunctions.tryStopAmbientSound(getBlockPos());
        }
    }

    public boolean getIsCharged() {
        return currentIndexRequirementsMet >= 0;
    }

    public Direction getLapisHolderFacingDirection() {
        return getBlockState().getValue(BlockStateProperties.FACING).getCounterClockWise();
    }

    // Starts the bonus item animation process; once this state is complete and BonusEarned state
    // completes as well, the provided ItemStack will be spawned by the table.
    public void startBonusPendingAnimation(ItemStack bonusItem) {
        if (level.isClientSide || tableState != CustomRechantedTableState.Normal) return;

        tableState = CustomRechantedTableState.BonusPending;
        currentStateTimeRemaining = BONUS_PENDING_ANIMATION_LENGTH_TICKS;

        pendingBonusItem = bonusItem.copy();

        level.playSound(null, getBlockPos(), ModSounds.TIER_2_ITEM_PENDING.get(), SoundSource.BLOCKS, 1.5f, 1.1f);

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void startLightBonusPendingAnimation(ItemStack bonusItem) {
        if (level.isClientSide || tableState != CustomRechantedTableState.Normal) return;

        tableState = CustomRechantedTableState.LightBonusPending;
        currentStateTimeRemaining = LIGHT_BONUS_PENDING_ANIMATION_LENGTH_TICKS;

        pendingBonusItem = bonusItem.copy();

        level.playSound(null, getBlockPos(), ModSounds.TIER_1_ITEM_PENDING.get(), SoundSource.BLOCKS, 1.5f, 0.75f);

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

    }

    // If state is BonusPending, this will complete the animation and spawn pendingBonusItem.
    // The rendered book will return to its resting position.
    public void completePendingBonusAnimation() {

        if (level.isClientSide || tableState != CustomRechantedTableState.BonusPending || pendingBonusItem == ItemStack.EMPTY) return;

        tableState = CustomRechantedTableState.BonusEarned;
        currentStateTimeRemaining = BONUS_EARNED_ANIMATION_LENGTH_TICKS;

        ItemEntity item = new ItemEntity(
                level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + BONUS_EARNED_ITEM_SPAWN_Y_OFFSET,
                worldPosition.getZ() + 0.5,
                pendingBonusItem
        );
        item.setDefaultPickUpDelay();

        Direction facing = getLapisHolderFacingDirection();
        Vec3 moveDir = new Vec3(facing.getStepX(), 0.2f, facing.getStepZ()).normalize();
        moveDir = moveDir.scale(BONUS_EARNED_ITEM_MOVE_SPEED_ON_SPAWN);
        item.setDeltaMovement(moveDir);
        ItemEntityTrailHandler.enableTrailUntilGround(
                item,
                ParticleTypes.ENCHANT,
                1,
                2
        );

        ServerLevel serverLevel = (ServerLevel)level;
        serverLevel.addFreshEntity(item);
        serverLevel.playSound(null, getBlockPos(), SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.BLOCKS, 1.0f, 1.0f);
        serverLevel.playSound(null, getBlockPos(), ModSounds.TIER_2_ITEM_EARNED.get(), SoundSource.BLOCKS, 0.8f, 1.15f);

        Vec3 yOffset = new Vec3(0, 1.5f, 0);
        sendRainbowCircleParticles(serverLevel, yOffset, UP,NORTH, 20, 0.5f, 0.8f, 0.8f, 0.4f, 0,0);
        sendRainbowCircleParticles(serverLevel, yOffset, NORTH, new Vec3(1, 0, 0), 20, 0.7f, 0.8f, 0.9f, 0.5f, 0,0);
        sendRainbowCircleParticles(serverLevel, yOffset, new Vec3(1, 0, 0),UP, 20, 0.9f, 0.9f, 0.95f, 0.6f, 0,0);
        pendingBonusItem = ItemStack.EMPTY;

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void completeLightPendingBonusAnimation() {

        if (level.isClientSide || tableState != CustomRechantedTableState.LightBonusPending || pendingBonusItem == ItemStack.EMPTY) return;

        tableState = CustomRechantedTableState.LightBonusEarned;
        currentStateTimeRemaining = LIGHT_BONUS_EARNED_ANIMATION_LENGTH_TICKS;

        ItemEntity item = new ItemEntity(
                level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + LIGHT_BONUS_EARNED_ITEM_SPAWN_Y_OFFSET,
                worldPosition.getZ() + 0.5,
                pendingBonusItem
        );
        item.setDefaultPickUpDelay();

        Direction facing = getLapisHolderFacingDirection();
        Vec3 moveDir = new Vec3(facing.getStepX(), LIGHT_BONUS_EARNED_ITEM_LAUNCH_Y_BIAS, facing.getStepZ()).normalize();
        moveDir = moveDir.scale(LIGHT_BONUS_EARNED_ITEM_MOVE_SPEED_ON_SPAWN);
        item.setDeltaMovement(moveDir);
        ItemEntityTrailHandler.enableTrailUntilGround(
                item,
                ParticleTypes.ENCHANT,
                1,
                2
        );

        ServerLevel serverLevel = (ServerLevel)level;
        serverLevel.addFreshEntity(item);
        serverLevel.playSound(null, getBlockPos(), SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.BLOCKS, 1.0f, 1.0f);
        serverLevel.playSound(null, getBlockPos(), ModSounds.TIER_1_ITEM_EARNED.get(), SoundSource.BLOCKS, 0.8f, 1.15f);

        pendingBonusItem = ItemStack.EMPTY;

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void startSuperBonusPendingAnimation(ItemStack bonusItem) {
        if (level.isClientSide || tableState != CustomRechantedTableState.Normal) return;

        tableState = CustomRechantedTableState.SuperBonusPending;
        currentStateTimeRemaining = SUPER_BONUS_PENDING_ANIMATION_LENGTH_TICKS;

        pendingBonusItem = bonusItem.copy();

        level.playSound(null, getBlockPos(), ModSounds.TIER_3_ITEM_PENDING.get(), SoundSource.BLOCKS, 1.5f, 1.1f);

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void completePendingSuperBonusAnimation() {

        if (level.isClientSide || tableState != CustomRechantedTableState.SuperBonusPending || pendingBonusItem == ItemStack.EMPTY) return;

        tableState = CustomRechantedTableState.SuperBonusEarned;
        currentStateTimeRemaining = SUPER_BONUS_EARNED_ANIMATION_LENGTH_TICKS;

        ItemEntity item = new ItemEntity(
                level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + BONUS_EARNED_ITEM_SPAWN_Y_OFFSET,
                worldPosition.getZ() + 0.5,
                pendingBonusItem
        );
        item.setDefaultPickUpDelay();

        Direction facing = getLapisHolderFacingDirection();
        Vec3 moveDir = new Vec3(facing.getStepX(), 0.2f, facing.getStepZ()).normalize();
        moveDir = moveDir.scale(BONUS_EARNED_ITEM_MOVE_SPEED_ON_SPAWN);
        item.setDeltaMovement(moveDir);
        ItemEntityTrailHandler.enableTrailUntilGround(
                item,
                ParticleTypes.ENCHANT,
                1,
                2
        );

        ServerLevel serverLevel = (ServerLevel)level;
        serverLevel.addFreshEntity(item);
        serverLevel.playSound(null, getBlockPos(), SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.BLOCKS, 1.0f, 1.0f);
        serverLevel.playSound(null, getBlockPos(), ModSounds.TIER_3_ITEM_EARNED.get(), SoundSource.BLOCKS, 0.8f, 1.15f);

        Vec3 yOffset = new Vec3(0, 1.5f, 0);
        sendRainbowCircleParticles(serverLevel, yOffset, UP,NORTH, 20, 0.5f, 0.8f, 0.8f, 0.4f, 0,0);
        sendRainbowCircleParticles(serverLevel, yOffset, NORTH, new Vec3(1, 0, 0), 20, 0.7f, 0.8f, 0.9f, 0.5f, 0,0);
        sendRainbowCircleParticles(serverLevel, yOffset, new Vec3(1, 0, 0),UP, 20, 0.9f, 0.9f, 0.95f, 0.6f, 0,0);
        pendingBonusItem = ItemStack.EMPTY;

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    // If state is BonusEarned (or any derivative for different tiers of reward), this will return the entity back to the default state.
    public void returnToDefaultState() {

        if (level.isClientSide || (
                tableState != CustomRechantedTableState.BonusEarned
                && tableState != CustomRechantedTableState.LightBonusEarned
                && tableState != CustomRechantedTableState.SuperBonusEarned)
        )
            return;

        tableState = CustomRechantedTableState.Normal;
        currentStateTimeRemaining = 0;  // Irrelevant for default state
        System.out.println(tableState);

        pendingBonusItem = ItemStack.EMPTY;

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }


    public void refreshCachedBlockStates(BookRarityProperties bookProperties, BlockPos pPos) {
        var reqBlockStates = getReqBlockStates(bookProperties, pPos);
        cachedEnchantingPower = reqBlockStates.getA();
        cachedFloorBlocksInRange = reqBlockStates.getB();
    }

    private Pair<Double, BlockState[]> getReqBlockStates(BookRarityProperties bookProperties, BlockPos pPos) {

        double enchantingPower = UtilFunctions.scanEnchantingPowerSources(level, pPos).totalPower();
        BlockState[] floorStates = UtilFunctions.scanAroundBlockForValidFloors(bookProperties.floorBlock, level, pPos).getA();
        return new Pair<>(enchantingPower, floorStates);
    }

    // Copy-pasted from enchantment table entity, but changing the "random" calls to prevent an IllegalStateException that can
    // occasionally occur when the original implementation uses the legacy global RANDOM.
    public void newBookAnimationTick(Level pLevel, BlockPos pPos, BlockState pState) {
        this.oOpen = this.open;
        this.oRot = this.rot;

        Player nearestPlayer = pLevel.getNearestPlayer((double)pPos.getX() + (double)0.5F, (double)pPos.getY() + (double)0.5F, (double)pPos.getZ() + (double)0.5F, (double)3.0F, false);
        if (this.getIsCharged()) {
            if (nearestPlayer != null) {
                double $$5 = nearestPlayer.getX() - ((double)pPos.getX() + (double)0.5F);
                double $$6 = nearestPlayer.getZ() - ((double)pPos.getZ() + (double)0.5F);
                this.tRot = (float) Mth.atan2($$6, $$5);
            }
            else {
                this.tRot += 0.02F;
            }

            this.open += 0.075F;
            if (this.open < 0.5F || pLevel.random.nextInt(40) == 0) {
                float $$7 = this.flipT;

                do {
                    this.flipT += (float)(pLevel.random.nextInt(4) - pLevel.random.nextInt(4));
                } while($$7 == this.flipT);
            }
        } else {
            this.tRot += 0.02F;
            this.open -= 0.35F;
        }

        while(this.rot >= (float)Math.PI) {
            this.rot -= ((float)Math.PI * 2F);
        }

        while(this.rot < -(float)Math.PI) {
            this.rot += ((float)Math.PI * 2F);
        }

        while(this.tRot >= (float)Math.PI) {
            this.tRot -= ((float)Math.PI * 2F);
        }

        while(this.tRot < -(float)Math.PI) {
            this.tRot += ((float)Math.PI * 2F);
        }

        float $$8;
        for($$8 = this.tRot - this.rot; $$8 >= (float)Math.PI; $$8 -= ((float)Math.PI * 2F)) {
        }

        while($$8 < -(float)Math.PI) {
            $$8 += ((float)Math.PI * 2F);
        }

        this.rot += $$8 * 0.4F;
        this.open = Mth.clamp(this.open, 0.0F, 1.0F);
        ++this.time;
        this.oFlip = this.flip;
        float $$9 = (this.flipT - this.flip) * 0.4F;
        float $$10 = 0.2F;
        $$9 = Mth.clamp($$9, -0.2F, 0.2F);
        this.flipA += ($$9 - this.flipA) * 0.9F;
        this.flip += this.flipA;
    }

    protected int checkAllRequirements(BlockPos pPos) {
        int reqMet = -1;
        for (int i = 1; i < 6; ++i) { // elements 1 - 5 to ignore dusty tier
            BookRarityProperties properties = BookRarityProperties.getAllProperties()[i];
            refreshCachedBlockStates(properties, pPos);
            if (meetsAllChargedEffectRequirements(properties, cachedEnchantingPower, cachedFloorBlocksInRange)) {
                if (level instanceof ServerLevel serverLevel) {
                    AdvancementHelper.awardPowerUpEnchantTableAdvancementNearPos(serverLevel, pPos);
                }

                reqMet = i;
                break;
            }
        }

        return reqMet;
    }

    protected void soundLogicOnTick(BlockPos pPos, Level pLevel) {

        int prevIndexRequirementsMet = currentIndexRequirementsMet;
        currentIndexRequirementsMet = checkAllRequirements(pPos);

        // Requirements have started being met on this tick.
        if (currentIndexRequirementsMet >= 0 && prevIndexRequirementsMet == -1) {
            if (pLevel.isClientSide()) {
                if (totalTicks != 0) {
                    pLevel.playLocalSound(pPos, ModSounds.ENCHANT_TABLE_CHARGE.get(), SoundSource.BLOCKS, 0.5f, 1.0f, false);
                    pLevel.playLocalSound(pPos, ModSounds.ENCHANT_TABLE_OPEN.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
                }

                UtilFunctions.createAndPlayAmbientSound(ModSounds.ENCHANT_TABLE_AMBIENT.get(), pPos, 0.5f);
            }
        }

        // Requirements no longer being met on this tick.
        else if (currentIndexRequirementsMet == -1 && prevIndexRequirementsMet != -1) {
            if (totalTicks != 0) {
                if (pLevel.isClientSide()) {
                    pLevel.playLocalSound(pPos, ModSounds.ENCHANT_TABLE_DISCHARGE.get(), SoundSource.BLOCKS, 0.5f, 1.0f, false);
                    pLevel.playLocalSound(pPos, ModSounds.ENCHANT_TABLE_CLOSE.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
                    stopAmbientSound();
                }
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        stopAmbientSound();
    }

    protected boolean enchantingPowerRequirementMet(BookRarityProperties bookProperties, double enchantingPower) {
        return UtilFunctions.playerMeetsEnchantingPowerRequirement(bookProperties, enchantingPower);
    }

    protected boolean floorRequirementsMet(BookRarityProperties bookProperties, BlockState[] floorStates) {
        return UtilFunctions.playerMeetsFloorRequirement(bookProperties, floorStates);
    }

    protected boolean meetsAllChargedEffectRequirements(BookRarityProperties bookProperties, double enchantingPower, BlockState[] floorStates) {
        return  enchantingPowerRequirementMet(bookProperties, enchantingPower) &&
                floorRequirementsMet(bookProperties, floorStates);
    }

}
