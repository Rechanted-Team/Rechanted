package net.tagtart.rechanted.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.tagtart.rechanted.Rechanted;
import net.tagtart.rechanted.item.custom.RechantedBookItem;
import net.tagtart.rechanted.util.BookRarityProperties;
import net.tagtart.rechanted.util.ClientUtils;
import net.tagtart.rechanted.util.EnchantmentPoolEntry;
import net.tagtart.rechanted.util.UtilFunctions;

import java.util.ArrayList;
import java.util.List;

import static net.tagtart.rechanted.util.UtilFunctions.getIncompatibleEnchantments;

@OnlyIn(Dist.CLIENT)
public class HoverableLootTablePoolEntryRenderable extends HoverableGuiRenderable {

    private static final ResourceLocation TABLE_ENTRY_BOX_LOCATION = ResourceLocation
            .fromNamespaceAndPath(Rechanted.MOD_ID, "textures/gui/enchant_table_loot_pool_entry_box.png");
    private static final ResourceLocation INFO_ICON_LOCATION = ResourceLocation.fromNamespaceAndPath(Rechanted.MOD_ID,
            "textures/gui/info_button.png");

    private ShaderInstance gridShader;

    private final int enchantmentInfoOffsetY = 14;
    private HoverableWithTooltipGuiRenderable nestedInfoHoverable;

    private int propertiesIndex;
    private RechantedTablePoolDisplayScreen screen;
    private EnchantmentPoolEntry poolEntry;
    private Font renderFont;

    // All these strings are cached for rendering pool info:
    private Enchantment enchantment;
    private ArrayList<ArrayList<ItemStack>> iconLists = new ArrayList<>();
    private String enchantmentName;
    private String enchantmentDropRate;
    private ArrayList<String> levelDropRates;
    private int maxLevel;

    public int scrollOffset = 0;

    public static float globalTimeElapsed = 0f; // Will be relative to the screen being used.

    public HoverableLootTablePoolEntryRenderable(RechantedTablePoolDisplayScreen pScreen, Font pFont,
            EnchantmentPoolEntry pPoolEntry, int pPropertiesIndex, int posX, int posY) {
        super(TABLE_ENTRY_BOX_LOCATION, posX, posY);
        propertiesIndex = pPropertiesIndex;
        screen = pScreen;
        renderFont = pFont;
        poolEntry = pPoolEntry;

        imageWidth = 144;
        imageHeight = 51;
        imageViewWidth = imageWidth;
        imageViewHeight = imageHeight;
        renderDefaultTexture = false;

        // Position of this adjusts with text
        nestedInfoHoverable = new HoverableWithTooltipGuiRenderable(this::infoIconTooltip, INFO_ICON_LOCATION,
                renderOffsetPosX, getEntryLabelOffsetY());
        nestedInfoHoverable.imageWidth = 9;
        nestedInfoHoverable.imageHeight = 9;
        nestedInfoHoverable.imageViewWidth = 9;
        nestedInfoHoverable.imageViewHeight = 9;
        nestedInfoHoverable.tooltipEnabled = false; // Manually rendering tooltip later due to scissoring conflicts.

        this.gridShader = ModShaders.ENCHANT_TABLE_LOOT_POOL_GRID_SHADER;

        levelDropRates = new ArrayList<>();
        generatePoolEntryInfo();
    }

    protected ArrayList<Component> infoIconTooltip() {
        ArrayList<Component> retVal = new ArrayList<>();

        String[] enchantmentInfo = poolEntry.enchantment.split(":");
        Component translatable = Component
                .translatable("enchantment." + enchantmentInfo[0] + "." + enchantmentInfo[1] + ".desc");
        String resolvedText = translatable.getString();
        List<String> splitText = UtilFunctions.wrapText(resolvedText, 165);
        for (String line : splitText) {
            retVal.add(Component.literal(line.trim()));
        }

        Holder.Reference<Enchantment> enchantmentHolder = UtilFunctions.getEnchantmentReferenceIfPresent(
                Minecraft.getInstance().player.registryAccess(),
                poolEntry.enchantment
        );
        if (enchantmentHolder == null) {
            return retVal;
        }


        List<Holder<Enchantment>> incompatibleEnchantments = UtilFunctions.getIncompatibleEnchantments(enchantmentHolder, Minecraft.getInstance().player.registryAccess());
        if (!incompatibleEnchantments.isEmpty()) {

            int incompatibleColor = 0xf29811;
            retVal.add(Component.literal(" "));
            retVal.add(Component.literal("Incompatible with:").withColor(incompatibleColor));

            for (Holder<Enchantment> incompatible : incompatibleEnchantments) {
                retVal.add(Component.literal("- ").withColor(incompatibleColor)
                        .append(incompatible.value().description().copy().withColor(incompatibleColor)));
            }

        }

        return retVal;
    }

    protected void generatePoolEntryInfo() {
        Holder.Reference<Enchantment> holder = UtilFunctions.getEnchantmentReferenceIfPresent(
                Minecraft.getInstance().player.registryAccess(), poolEntry.enchantment);

        if (holder == null) {
            enchantment = null;
            iconLists = new ArrayList<>();
            enchantmentName = "Invalid:Enchantment!";
            enchantmentDropRate = "-";
            levelDropRates.add("-");
            maxLevel = 0;
            return;
        }
        enchantment = holder.value();

        String nameID = enchantment.description().getString();
        enchantmentName = Component.translatable(nameID).getString();
        enchantmentDropRate = String.format("%6.2f%%",
                ((float) poolEntry.weight / getBookProperties().enchantmentPoolTotalWeights) * 100.0f);

        ArrayList<ItemStack> allIcons = RechantedBookItem.getApplicableItemStacks(holder);
        if (allIcons.size() <= 12) {
            iconLists.add(allIcons);
        }
        else {
            int rowCount = (allIcons.size() / 12) + 1;
            for (int r = 0; r < rowCount; r++) {
                ArrayList<ItemStack> splitStack = new ArrayList<>(allIcons.subList(r * 12, Math.min(allIcons.size(), (r * 12) + 12)));
                iconLists.add(splitStack);
            }
        }


        for (int i = 0; i < poolEntry.levelWeights.size(); ++i) {
            levelDropRates.add(String.format("%6.2f%%",
                    ((float) poolEntry.levelWeights.get(i) / poolEntry.levelWeightsSum) * 100.0f));
        }
        maxLevel = enchantment.getMaxLevel();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        renderText(guiGraphics, mouseX, mouseY, delta);
    }

    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.blitSprite(renderTexture, imageWidth, imageHeight, 0, 0, renderOffsetPosX,
                renderOffsetPosY - scrollOffset, imageWidth, imageHeight);

        if (gridShader != null) {
            gridShader.safeGetUniform("Time").set(globalTimeElapsed);
            gridShader.safeGetUniform("Resolution").set((float) imageWidth, (float) getEntryLabelBottomY());

            RenderSystem.setShader(() -> gridShader);

            Minecraft.getInstance().getTextureManager().bindForSetup(renderTexture);
            gridShader.safeGetUniform("MainDiffuse").set(0);

            gridShader.apply();
        }

        ClientUtils.fakeInnerBlit(guiGraphics, renderOffsetPosX, renderOffsetPosX + imageWidth,
                renderOffsetPosY - scrollOffset, (renderOffsetPosY - scrollOffset) + getEntryLabelBottomY(), 0,
                0.0f, 1.0f, 0.0f, 1.0f);
    }

    public void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.pose().pushPose();
        float labelScaleFac = 0.75f;
        float invLabelScaleFac = 1.0f / labelScaleFac;
        guiGraphics.pose().scale(labelScaleFac, labelScaleFac, labelScaleFac);

        int extraNameOffset = 0;
        if (displayShortenedVersion()) {
            extraNameOffset = 1;
        }
        guiGraphics.drawString(renderFont, enchantmentName, (renderOffsetPosX + 3) * invLabelScaleFac,
                (getEntryLabelOffsetY() + extraNameOffset) * invLabelScaleFac, 0xFFFFFF, true);
        renderFont.drawInBatch(enchantmentName, (renderOffsetPosX + 3) * invLabelScaleFac,
                (getEntryLabelOffsetY() + extraNameOffset) * invLabelScaleFac, 0xFFFFFF, true,
                guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 0xF000F0);

        renderFont.drawInBatch(enchantmentDropRate, (renderOffsetPosX + 114) * invLabelScaleFac,
                (getEntryLabelOffsetY() + extraNameOffset) * invLabelScaleFac, 0xFFFFFF, true,
                guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 0xF000F0);
        if (!displayShortenedVersion()) {
            renderFont.drawInBatch("______________________________", (renderOffsetPosX + 3) * invLabelScaleFac,
                    (getEntryLabelOffsetY() + 2) * invLabelScaleFac, 0xFFFFFF, true, guiGraphics.pose().last().pose(),
                    guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 0xF000F0);
            renderFont.drawInBatch("______________________________", (renderOffsetPosX + 4) * invLabelScaleFac,
                    (getEntryLabelOffsetY() + 2) * invLabelScaleFac, 0xFFFFFF, true, guiGraphics.pose().last().pose(),
                    guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 0xF000F0);
        }
        guiGraphics.pose().popPose();


//        renderFont.drawInBatch(iconList, (renderOffsetPosX + 2.7f) * invIconScaleFac,
//                (getEntryLabelOffsetY() - 8) * invIconScaleFac, 0x222222, false, guiGraphics.pose().last().pose(),
//                guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 0xF000F0);
//        renderFont.drawInBatch(iconList, (renderOffsetPosX + 2.2f) * invIconScaleFac,
//                (getEntryLabelOffsetY() - 9) * invIconScaleFac, 0xFFFFFF, false, guiGraphics.pose().last().pose(),
//                guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 0xF000F0);

        float iconScaleFac = 0.612f;
        float invIconScaleFac = 1.0f / iconScaleFac;

        for (int row = 0; row < iconLists.size(); row++) {
            for (int i = 0; i < iconLists.get(row).size(); i++) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(iconScaleFac, iconScaleFac, iconScaleFac);
                guiGraphics.pose().translate(
                        ((renderOffsetPosX + 3.0f) + (i * 10.0f)) * invIconScaleFac, // 10f is arbitrary 10px spacing
                        (((getEntryLabelOffsetY() - 11.0f)) - getIconListTotalHeight()) * invIconScaleFac,
                        0.0f
                );

                ClientUtils.renderItemWithShadow(guiGraphics, iconLists.get(row).get(i), 0, row * 16);

                guiGraphics.pose().popPose();
            }
        }

        float scaleFac = 0.5f;
        float invScaleFac = 1.0f / scaleFac;
        if (!displayShortenedVersion()) {

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(scaleFac, scaleFac, scaleFac);
            for (int i = 0; i < levelDropRates.size(); ++i) {

                String roman = UtilFunctions.intToRoman(poolEntry.potentialLevels.get(i)) + ":";
                renderFont.drawInBatch(roman, (renderOffsetPosX + 5) * invScaleFac,
                        (getEntryLabelOffsetY() + ((i + 2) * 5)) * invScaleFac, 0xFFFFFF, true,
                        guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0,
                        0xF000F0);
                renderFont.drawInBatch(levelDropRates.get(i), (renderOffsetPosX + 121) * invScaleFac,
                        (getEntryLabelOffsetY() + ((i + 2) * 5)) * invScaleFac, 0xFFFFFF, true,
                        guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), Font.DisplayMode.NORMAL, 0,
                        0xF000F0);
            }
            guiGraphics.pose().popPose();
        }
        guiGraphics.flush();

        guiGraphics.disableScissor();
        float infoIconOffset = renderFont.width(enchantmentName) * 0.75f;
        nestedInfoHoverable.scaleFac = scaleFac;
        nestedInfoHoverable.renderOffsetPosX = (int) ((renderOffsetPosX + infoIconOffset + 4) * invScaleFac);
        nestedInfoHoverable.renderOffsetPosY = (int) ((getEntryLabelOffsetY() + extraNameOffset) * invScaleFac);
        nestedInfoHoverable.renderCustomTooltip(guiGraphics, mouseX, mouseY);
        guiGraphics.flush();
        guiGraphics.enableScissor(screen.getScissorMinX(), screen.getScissorMinY(), screen.getScissorMaxX(),
                screen.getScissorMaxY());
        nestedInfoHoverable.render(guiGraphics, mouseX, mouseY, delta);
    }

    private boolean displayShortenedVersion() {
        return levelDropRates.size() <= 1 && maxLevel <= 1;
    }

    public int getEntryLabelBottomY() {
        int baseOffset = 26;// Based on position after where underlines "_______" are rendered.
        if (displayShortenedVersion())
            baseOffset -= 7;

        // Give 5 extra pixels for each potential level.
        return (baseOffset) + (levelDropRates.size() * 5) + getIconListTotalHeight();
    }

    private int getEntryLabelOffsetY() {
        return (enchantmentInfoOffsetY + renderOffsetPosY + getIconListTotalHeight()) - scrollOffset;
    }

    private int getIconListTotalHeight() {
        return (iconLists.size() - 1) * 10;
    }

    private BookRarityProperties getBookProperties() {
        return BookRarityProperties.getAllProperties()[propertiesIndex];
    }
}
