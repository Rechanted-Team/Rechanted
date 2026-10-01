package net.tagtart.rechanted.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.tagtart.rechanted.Rechanted;
import net.tagtart.rechanted.block.entity.RechantedTableBlockEntity;
import net.tagtart.rechanted.config.RechantedCommonConfigs;
import net.tagtart.rechanted.networking.data.OpenEnchantTableScreenC2SPayload;
import net.tagtart.rechanted.networking.data.PlayerPurchaseEnchantedBookC2SPayload;
import net.tagtart.rechanted.util.BookRarityProperties;
import net.tagtart.rechanted.util.ClientUtils;
import net.tagtart.rechanted.util.UtilFunctions;
import org.joml.Vector2i;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RechantedTableScreen extends AbstractContainerScreen<RechantedTableMenu> {

    private static final ResourceLocation SIMPLE_LINE_LOCATION = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/gui/enchant_table_effect_simple.png");
    private static final ResourceLocation UNIQUE_LINE_LOCATION = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/gui/enchant_table_effect_unique.png");
    private static final ResourceLocation ELITE_LINE_LOCATION = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/gui/enchant_table_effect_elite.png");
    private static final ResourceLocation ULTIMATE_LINE_LOCATION = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/gui/enchant_table_effect_ultimate.png");
    private static final ResourceLocation LEGENDARY_LINE_LOCATION = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/gui/enchant_table_effect_legendary.png");
    private static final ResourceLocation EMPTY_LINE_LOCATION = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/gui/enchant_table_effect_empty.png");
    private ResourceLocation[] shaderEffectsByBookID;
    private ShaderInstance lineShader;

    // NOTE:
    // Some mods do weird things to textures that have a .mcmeta, like automatically split them up into individual textures silently.
    // Since this the manual sprite sheet animation code needs to work with or without those mods, this points to a COPY of the legendary book icon
    // sprite sheet that DOES NOT have a .mcmeta file associated with it. I hate this, but it's the only place we do it (beside loot table screen) so it's fine for now but is big code smell
    private static final ResourceLocation LEGENDARY_BOOK_MANUAL_ANIMATION_LOCATION = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/gui/legendary_manual_animate.png");

    public static final Style PINK_COLOR_STYLE = Style.EMPTY.withColor(0xFCB4B4);
    public static final Style MID_GRAY_COLOR_STYLE = Style.EMPTY.withColor(0xA8A8A8);

    public static final float BONUS_PENDING_EFFECT_Y_MOVE_SPEED = 0.01f;
    public static final float LIGHT_BONUS_EFFECT_FLASH_DECREMENT_SPEED = 0.016f;

    private static final Component grayHyphen = Component.literal("- ").withStyle(MID_GRAY_COLOR_STYLE);
    private static final Component whiteArrow = Component.literal("→ ").withStyle(ChatFormatting.WHITE);
    private static final Component redX = Component.literal("✘ ").withStyle(ChatFormatting.DARK_RED);

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/gui/enchantment_table.png");

    private static final ResourceLocation GREYSCALE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID, "textures/gui/enchantment_table_greyscale.png");

    private ArrayList<HoverableWithTooltipGuiRenderable> hoverables;

    public Inventory playerInventory;

    private double cachedEnchantingPower;
    private BlockState[] cachedFloorBlocksInRange;

    private float timeElapsed = 0.0f;
    private float bonusEarnedEffectVCoord = 1000.0f;
    private float lightBonusEarnedFlashAmount = 0.0f;
    private static float lineEffectSpeed = 0.23f;

    private final float REQ_CHECK_RATE = 0.2f;  // How often shader will check if requirements are met to display effect.
    private float timeSinceLastReqCheck = 0.0f; // Time since last requirement check was made for shader effect.
    private int currentIndexRequirementsMet = 0; // Which book has its current requirements met; if -1, none are met.

    public RechantedTableScreen(RechantedTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        playerInventory = pPlayerInventory;
    }

    private static ResourceLocation getEnabledGUITexture() {
        return (RechantedCommonConfigs.USE_GREYSCALE_GUI.get()) ? GREYSCALE_TEXTURE : TEXTURE;
    }

    @Override
    protected void init() {
        super.init();

        this.cachedEnchantingPower = 0;
        this.cachedFloorBlocksInRange = new BlockState[0];

        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;

        // Why isn't there an easy to just read these from the PNG?
        this.imageWidth = 196;
        this.imageHeight = 219;

        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2;

        hoverables = new ArrayList<>();

        hoverables.add(new HoverableWithTooltipGuiRenderable(() -> getEnchantTableTooltipLines(1), BookRarityProperties.getAllProperties()[1].iconResourceLocation, leftPos + 19, topPos + 45));
        hoverables.add(new HoverableWithTooltipGuiRenderable(() -> getEnchantTableTooltipLines(2),BookRarityProperties.getAllProperties()[2].iconResourceLocation,leftPos + 159, topPos + 45));
        hoverables.add(new HoverableWithTooltipGuiRenderable(() -> getEnchantTableTooltipLines(3),BookRarityProperties.getAllProperties()[3].iconResourceLocation, leftPos + 52,  topPos + 42));
        hoverables.add(new HoverableWithTooltipGuiRenderable(() -> getEnchantTableTooltipLines(4),BookRarityProperties.getAllProperties()[4].iconResourceLocation, leftPos + 127, topPos + 41));

        HoverableWithTooltipGuiRenderable animatedLegendaryHoverable = new HoverableWithTooltipGuiRenderable(() -> getEnchantTableTooltipLines(5), LEGENDARY_BOOK_MANUAL_ANIMATION_LOCATION,  leftPos + 90, topPos + 39);
        Vector2i texSize = ClientUtils.queryTextureSize(LEGENDARY_BOOK_MANUAL_ANIMATION_LOCATION);
        animatedLegendaryHoverable.setAnimatedData(new HoverableGuiRenderable.AnimatedTextureData(
                 texSize.x, texSize.y, 3, 7, true
        ));
        hoverables.add(animatedLegendaryHoverable);

        // Diffuse textures for line shader effect.
        // Indices are offset by 1 for efficiency; empty/no effect is index zero.
        this.shaderEffectsByBookID = new ResourceLocation[6];
        this.shaderEffectsByBookID[0] = EMPTY_LINE_LOCATION;
        this.shaderEffectsByBookID[1] = SIMPLE_LINE_LOCATION;
        this.shaderEffectsByBookID[2] = UNIQUE_LINE_LOCATION;
        this.shaderEffectsByBookID[3] = ELITE_LINE_LOCATION;
        this.shaderEffectsByBookID[4] = ULTIMATE_LINE_LOCATION;
        this.shaderEffectsByBookID[5] = LEGENDARY_LINE_LOCATION;

        this.lineShader = ModShaders.ENCHANT_TABLE_FBM_LINE_SHADER;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {

        // Normal blitting of GUI is first. Custom shader effect is applied on top.
        guiGraphics.blit(getEnabledGUITexture(), this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        renderBGEffect(guiGraphics, pPartialTick, pMouseX, pMouseY);
    }

    protected void renderBGEffect(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        timeElapsed += lineEffectSpeed * pPartialTick;

        timeSinceLastReqCheck += pPartialTick;

        // If a bonus item is earned, this will act as a flag to retain most recently met requirements instead
        // of checking which rarity's is met. This is just so that if a bonus item is earned, the line
        // shader stays active a little longer in cases where blocks/bookshelves also break at the same time.
        if (menu.bonusEarnedEffectQueued.get() != 0) {
            menu.bonusEarnedEffectQueued.set(0);  // Doesn't actually broadcast change; only client side so this check doesn't repeat.
            bonusEarnedEffectVCoord = -0.1f;
        }
        if (menu.lightBonusEarnedEffectQueued.get() != 0) {
            menu.lightBonusEarnedEffectQueued.set(0);
            lightBonusEarnedFlashAmount = 1.0f;
        }
        // Check if requirements are met currently after certain interval passed,
        // then set which book index can currently be crafted for use elsewhere.
        if (timeSinceLastReqCheck >= REQ_CHECK_RATE && menu.blockEntity.tableState == RechantedTableBlockEntity.CustomRechantedTableState.Normal)
        {
            currentIndexRequirementsMet = 0;
            for (int i = 1; i < 6; ++i) {
                BookRarityProperties properties = BookRarityProperties.getAllProperties()[i];
                refreshCachedBlockStates(properties);
                if (playerMeetsAllEnchantRequirements(properties, cachedEnchantingPower, cachedFloorBlocksInRange)) {
                    currentIndexRequirementsMet = i;
                    break;
                }

                // Update tooltip line if it's being hovered over currently as well
                // This will account for destroyed blocks shortly after destroyed if user is still hovering for tooltip.
                if (i < hoverables.size()) {
                    HoverableWithTooltipGuiRenderable hoverable = hoverables.get(i);
                    if (hoverable.isMouseOverlapped(pMouseX, pMouseY)) {
                        hoverable.updateTooltipLines();
                    }
                }
            }

            timeSinceLastReqCheck = 0.0f;
        }

        bonusEarnedEffectVCoord += BONUS_PENDING_EFFECT_Y_MOVE_SPEED * pPartialTick;
        lightBonusEarnedFlashAmount -= LIGHT_BONUS_EFFECT_FLASH_DECREMENT_SPEED * pPartialTick;

        lineShader.safeGetUniform("GemEarnEffectVCoord").set(bonusEarnedEffectVCoord);
        lineShader.safeGetUniform("LightBonusFlashMult").set(lightBonusEarnedFlashAmount);
        lineShader.safeGetUniform("Time").set(timeElapsed);
        lineShader.safeGetUniform("Resolution").set((float)imageWidth, (float)imageHeight);

        RenderSystem.setShader(() -> lineShader);
        /*RenderSystem.activeTexture(GL_TEXTURE0);
        Minecraft.getInstance().getTextureManager().bindForSetup(TEXTURE);
        lineShader.safeGetUniform("MainDiffuse").set(0);*/

        Minecraft.getInstance().getTextureManager().bindForSetup(shaderEffectsByBookID[currentIndexRequirementsMet]);
        lineShader.safeGetUniform("LineEffectMap").set(0);

        lineShader.apply();

        ClientUtils.fakeInnerBlit(guiGraphics, this.leftPos, this.leftPos + imageWidth, this.topPos, this.topPos + imageHeight, 0,
                0.0f, 1.0f, 0.0f, 1.0f);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

        renderBackground(guiGraphics, mouseX, mouseY, delta);

        super.render(guiGraphics, mouseX, mouseY, delta);

        // Render all hoverables. Defer rendering the tooltip due to overlap that can occur.
        HoverableWithTooltipGuiRenderable deferredTooltipToRender = null;
        for (HoverableWithTooltipGuiRenderable hoverable : hoverables) {
            hoverable.render(guiGraphics, mouseX, mouseY, delta);

            if (hoverable.tooltipShouldRenderThisFrame) {
                deferredTooltipToRender = hoverable;
            }
        }

        if (deferredTooltipToRender != null) {
            deferredTooltipToRender.renderCustomTooltip(guiGraphics, mouseX, mouseY);
        }
        else {
            renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        // If mouse overlaps a custom Hoverable and was left-clicked:
        if (pButton == 0) {
            Player player = playerInventory.player;
            Level level = player.level();

            for (int i = 1; i < hoverables.size() + 1; ++i) {
                HoverableWithTooltipGuiRenderable hoverable = hoverables.get(i - 1);
                if (hoverable.tryClickMouse(pMouseX, pMouseY, pButton)) {
                    BookRarityProperties properties = BookRarityProperties.getAllProperties()[i];
                    BlockPos pos = menu.blockEntity.getBlockPos();

                    // If a bonus item is being earned, don't allow player to purchase book.
                    if (menu.blockEntity.tableState != RechantedTableBlockEntity.CustomRechantedTableState.Normal) {
                        player.closeContainer();
                        Minecraft.getInstance().player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK, 0.7F, 1.0f);

                        String translatedMsg = Component.translatable("message.rechanted.bonus_pending").getString();
                        player.sendSystemMessage(Component.literal(translatedMsg).withStyle(ChatFormatting.RED));

                        break;
                    }

                    if (!floorRequirementsMet(properties, cachedFloorBlocksInRange)
                            || !bookshelfRequirementsMet(properties, cachedEnchantingPower)
                            || !lapisRequirementsMet(properties)) {
                        Minecraft.getInstance().player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK, 0.7F, 1.0f);
                        break;
                    }

                    if (playerInventory.getFreeSlot() == -1) {
                        // Send warning message to player, and close screen.
                        player.closeContainer();
                        Minecraft.getInstance().player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK, 0.7F, 1.0f);

                        Component translatedMsg = Component.translatable("message.rechanted.inventory_full").withStyle(ChatFormatting.RED);
                        player.sendSystemMessage(translatedMsg);
                        break;
                    }

                    if (!expRequirementMet(properties)) {
                        // Also send warning message to player, and close screen.
                        player.closeContainer();
                        Minecraft.getInstance().player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK, 0.7F, 1.0f);

                        String translatedMsg = Component.translatable("message.rechanted.insufficient_exp").getString();
                        String argsAdded = String.format(translatedMsg, UtilFunctions.getPlayerExperiencePoints(player), properties.requiredExp);
                        player.sendSystemMessage(Component.literal(argsAdded).withStyle(ChatFormatting.RED));

                        break;
                    }

                    // At this point, they meet the requirements. Can try to send a packet to server!
                    PacketDistributor.sendToServer(new PlayerPurchaseEnchantedBookC2SPayload(i, menu.blockEntity.getBlockPos()));
                    hoverable.updateTooltipLines();
                    return true;
                }
            }
        }
        else if (pButton == 1) {

            // On right click, open loot table display screen.
            for (int i = 1; i < hoverables.size() + 1; ++i) {
                HoverableWithTooltipGuiRenderable hoverable = hoverables.get(i - 1);
                if (hoverable.tryClickMouse(pMouseX, pMouseY, pButton)) {

                    RechantedTableBlockEntity rbe = menu.blockEntity;
                    int propertyIndex = i;

                    //ModPackets.sentToServer(new OpenEnchantTableScreenC2SPacket(1, i, menu.blockEntity.getBlockPos()));
                    PacketDistributor.sendToServer(new OpenEnchantTableScreenC2SPayload(1, i, menu.blockEntity.getBlockPos()));
                }
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public ArrayList<Component> getEnchantTableTooltipLines(int propertiesIndex) {
        BookRarityProperties properties = BookRarityProperties.getAllProperties()[propertiesIndex];

        // VERY IMPORTANT to note. On client side, we only recompute if requirements are valid when tooltip is refreshed,
        // which only happens when a new item is initially hovered in this case. Server-side checks are in PurchaseEnchantedBookC2SPacket.
        refreshCachedBlockStates(properties);

        ArrayList<Component> tooltipLines = new ArrayList<>();

        // Tooltip "title", with rarity icon and generic book name.
        Component rarityIcon = Component.translatable("enchantment.rarity." + properties.key);
        Component bookTitle = Component.translatable("item.rechanted.rechanted_book").withStyle(properties.colorAsStyle());
        tooltipLines.add(Component.literal(rarityIcon.getString()).append(" ").append(bookTitle));
        tooltipLines.add(Component.literal(" "));

        String description = Component.translatable("tooltip.rechanted.enchantment_table.enchanted_book_desc").getString();
        List<String> splitText = UtilFunctions.wrapText(description, 165);
        for (String line : splitText) {
            tooltipLines.add(Component.literal(line.trim()));
        }
        tooltipLines.add(Component.literal(" "));

        // Experience requirement
        Component experienceTitle = Component.translatable("tooltip.rechanted.enchantment_table.cost").withStyle(MID_GRAY_COLOR_STYLE);
        ChatFormatting expReqMetColor = expRequirementMet(properties) ? ChatFormatting.GREEN : ChatFormatting.RED;
        Component experienceFrac = Component.literal(UtilFunctions.getPlayerExperiencePoints(playerInventory.player) + " / " + properties.requiredExp + " EXP").withStyle(expReqMetColor);
        tooltipLines.add(experienceTitle.copy().append(":"));
        tooltipLines.add(grayHyphen.copy().append(experienceFrac));

        // Lapis Lazuli count requirement.
        String lapisCount = String.valueOf(properties.requiredLapis);
        String lapisName = Component.translatable("tooltip.rechanted.enchantment_table.lapis").getString();
        ChatFormatting lapisReqMetColor = lapisRequirementsMet(properties) ? ChatFormatting.GREEN : ChatFormatting.RED;
        Component fullLapisRequirementColored = Component.literal(lapisCount + " " + lapisName).withStyle(lapisReqMetColor);
        tooltipLines.add(grayHyphen.copy().append(fullLapisRequirementColored));
        tooltipLines.add(Component.literal(" "));

        // Block requirements:
        Component requirementsTitle = Component.translatable("tooltip.rechanted.enchantment_table.requirements").append(": ").withStyle(MID_GRAY_COLOR_STYLE);
        tooltipLines.add(requirementsTitle);

        // Enchanting power requirement. Green color if requirement met.
        String bookshelfCount = String.valueOf(properties.requiredBookShelves);
        String bookshelvesName = Component.translatable("tooltip.rechanted.enchantment_table.bookshelves").getString();
        ChatFormatting bookReqMetColor = bookshelfRequirementsMet(properties, cachedEnchantingPower) ? ChatFormatting.GREEN : ChatFormatting.RED;
        Component fullBookRequirementColored = Component.literal(bookshelfCount + " " + bookshelvesName).withStyle(bookReqMetColor);
        tooltipLines.add(grayHyphen.copy().append(fullBookRequirementColored));

        // Floor block requirement. Green color if requirement met.
        String floorBlockName = properties.floorBlock.getName().getString();
        String floorTranslated = Component.translatable("tooltip.rechanted.enchantment_table.floor").getString();
        ChatFormatting floorReqMetColor = floorRequirementsMet(properties, cachedFloorBlocksInRange) ? ChatFormatting.GREEN : ChatFormatting.RED;
        Component fullFloorRequirementColored = Component.literal(floorBlockName + " " + floorTranslated).withStyle(floorReqMetColor);
        tooltipLines.add(grayHyphen.copy().append(fullFloorRequirementColored));
        tooltipLines.add(Component.literal(" "));

        // Break chances:
        String chancePerBlock = Component.translatable("tooltip.rechanted.enchantment_table.chance_per").getString();
        Component breakChanceTitle = Component.translatable("tooltip.rechanted.enchantment_table.break_chance").append(":").withStyle(MID_GRAY_COLOR_STYLE);
        tooltipLines.add(breakChanceTitle);

        // Bookshelves break chance
        if (properties.bookBreakChance > 0.0001f) {
            String bookBreakChance = String.format("%.1f%%", properties.bookBreakChance * 100f);
            tooltipLines.add(Component.literal("- " + bookshelvesName + ":"));
            tooltipLines.add(Component.literal("    - " + bookBreakChance + " " + chancePerBlock).withStyle(PINK_COLOR_STYLE));
        }

        // Floor break chance
        if (properties.floorBreakChance > 0.0001f) {
            String floorBreakChance = String.format("%.1f%%", properties.floorBreakChance * 100f);
            tooltipLines.add(Component.literal("- " + floorTranslated + ":"));
            tooltipLines.add(Component.literal("    - " + floorBreakChance + " " + chancePerBlock).withStyle(PINK_COLOR_STYLE));
            tooltipLines.add(Component.literal(" "));
        }

        // Shows left-click prompt if met, otherwise shows warning that requirements not met.
        if (playerMeetsAllEnchantRequirements(properties, cachedEnchantingPower, cachedFloorBlocksInRange)) {
            Component leftClickPrompt = Component.translatable("tooltip.rechanted.enchantment_table.left_click").withStyle(properties.colorAsStyle());
            tooltipLines.add(whiteArrow.copy().append(leftClickPrompt));
        }
        else {
            Component reqNotMet = Component.translatable("tooltip.rechanted.enchantment_table.requirements_not_met").withStyle(ChatFormatting.RED);
            tooltipLines.add(redX.copy().append(reqNotMet));
        }

        // Final line, show right click prompt.
        Component rightClickPrompt = Component.translatable("tooltip.rechanted.enchantment_table.right_click").withStyle(properties.colorAsStyle());
        tooltipLines.add(whiteArrow.copy().append(rightClickPrompt));

        return tooltipLines;
    }

    public void refreshCachedBlockStates(BookRarityProperties bookProperties) {
        var reqBlockStates = getReqBlockStates(bookProperties);
        cachedEnchantingPower = reqBlockStates.getA();
        cachedFloorBlocksInRange = reqBlockStates.getB();
    }

    private Pair<Double, BlockState[]> getReqBlockStates(BookRarityProperties bookProperties) {
        Level level = playerInventory.player.level();
        BlockPos enchantTablePos = menu.blockEntity.getBlockPos();

        double enchantingPower = UtilFunctions.getEnchantingPower(level, UtilFunctions.scanAroundBlockForBookshelves(level, enchantTablePos));
        BlockState[] floorStates = UtilFunctions.scanAroundBlockForValidFloors(bookProperties.floorBlock, level, enchantTablePos).getA();
        return new Pair<>(enchantingPower, floorStates);
    }

    // These extra requirement checks are kinda pointless but uh yeah whatever
    protected boolean expRequirementMet(BookRarityProperties bookProperties) {
        return UtilFunctions.playerMeetsExpRequirement(bookProperties, playerInventory.player);
    }

    protected boolean bookshelfRequirementsMet (BookRarityProperties bookProperties, double enchantingPower) {
        return UtilFunctions.playerMeetsBookshelfRequirement(bookProperties, enchantingPower);
    }

    protected boolean floorRequirementsMet(BookRarityProperties bookProperties, BlockState[] floorStates) {
        return UtilFunctions.playerMeetsFloorRequirement(bookProperties, floorStates);
    }

    protected boolean lapisRequirementsMet(BookRarityProperties bookProperties) {
        return UtilFunctions.playerMeetsLapisRequirement(bookProperties, menu.getLapisSlotStack());
    }

    protected boolean playerMeetsAllEnchantRequirements(BookRarityProperties bookProperties, double enchantingPower, BlockState[] floorStates) {
        return  bookshelfRequirementsMet(bookProperties, enchantingPower) &&
                expRequirementMet(bookProperties) &&
                floorRequirementsMet(bookProperties, floorStates) &&
                lapisRequirementsMet(bookProperties);
    }
}
