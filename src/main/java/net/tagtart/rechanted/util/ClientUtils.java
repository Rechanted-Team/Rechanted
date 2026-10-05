package net.tagtart.rechanted.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientUtils {

    public static void fakeInnerBlit(GuiGraphics guiGraphics, int pX1, int pX2, int pY1, int pY2, int pBlitOffset, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        Matrix4f matrix4f = new Matrix4f();
        if (guiGraphics != null) {
            matrix4f = guiGraphics.pose().last().pose();
        }
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, (float)pX1, (float)pY1, (float)pBlitOffset).setUv(pMinU, pMinV);
        bufferbuilder.addVertex(matrix4f, (float)pX1, (float)pY2, (float)pBlitOffset).setUv(pMinU, pMaxV);
        bufferbuilder.addVertex(matrix4f, (float)pX2, (float)pY2, (float)pBlitOffset).setUv(pMaxU, pMaxV);
        bufferbuilder.addVertex(matrix4f, (float)pX2, (float)pY1, (float)pBlitOffset).setUv(pMaxU, pMinV);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static Vector2i queryTextureSize(ResourceLocation textureLocation) {

        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(textureLocation);


        // Force bind to get dimensions if not already loaded
        texture.bind();
        int id = texture.getId();
        // Use GL11 to query width/height from the bound texture
        int width = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        return new Vector2i(width, height);
    }

    public static void renderItemWithShadow(GuiGraphics gfx, ItemStack stack, int x, int y) {
        gfx.flush(); // Must do this call or black shader color set later can still persist in future draw calls

        gfx.pose().pushPose();
        gfx.pose().translate(1, 1, -1);          // -1 z is important!!!
        RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 1.0f);
        gfx.renderItem(stack, x, y);
        gfx.flush(); // Must do this too!
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        gfx.pose().popPose();

        // Real Item
        gfx.renderItem(stack, x, y);
    }
}
