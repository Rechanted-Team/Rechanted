package net.tagtart.rechanted.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.tagtart.rechanted.util.ClientUtils;

import java.util.ArrayList;
import java.util.List;

public class InlineItemStackIconsComponent implements ClientTooltipComponent {

    public record IconRowTooltip(ArrayList<ItemStack> icons) implements TooltipComponent {}

    private static final float ICON_WIDTH = 10.0f;
    private final List<ItemStack> items;

    public InlineItemStackIconsComponent(IconRowTooltip iconRowTooltip) { this.items = iconRowTooltip.icons; }

    @Override public int getHeight() { return 12; }

    @Override public int getWidth(Font font) {
        return (int)ICON_WIDTH * items.size();
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics gfx) {
        int cx = x;
        for (ItemStack item : items) {
            gfx.pose().pushPose();
            gfx.pose().translate(cx, y, 0);
            gfx.pose().scale(0.62f, 0.62f, 1f);

            ClientUtils.renderItemWithShadow(gfx, item, 0, 0);

            gfx.pose().popPose();
            cx += (int)ICON_WIDTH + 1;
        }
    }
}