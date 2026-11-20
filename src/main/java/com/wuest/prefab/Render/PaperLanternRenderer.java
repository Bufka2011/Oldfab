package com.wuest.prefab.Render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.wuest.prefab.Blocks.BlockPaperLantern;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Renders the paper lantern. This is a port of the 1.10 JSON block model.
 */
@SideOnly(Side.CLIENT)
public class PaperLanternRenderer implements ISimpleBlockRenderingHandler {

    private final int renderId;

    /**
     * The model elements: from x/y/z, to x/y/z (in 1/16 block) and the texture index (0 = torch, 1 = stick, 2 =
     * paper).
     */
    private static final float[][] ELEMENTS = new float[][] { { 7, 0, 7, 9, 10, 9, 0 }, { 7, 0, 1, 9, 1, 7, 1 },
        { 1, 0, 7, 7, 1, 9, 1 }, { 7, 0, 9, 9, 1, 15, 1 }, { 9, 0, 7, 15, 1, 9, 1 }, { 0, 0, 0, 16, 16, 1, 2 },
        { 0, 0, 1, 1, 16, 16, 2 }, { 1, 0, 15, 16, 16, 16, 2 }, { 15, 0, 1, 16, 16, 15, 2 }, { 7, 15, 1, 9, 16, 8, 1 },
        { 1, 15, 7, 7, 16, 9, 1 }, { 7, 15, 8, 9, 16, 15, 1 }, { 9, 15, 7, 15, 16, 9, 1 } };

    public PaperLanternRenderer() {
        this.renderId = RenderingRegistry.getNextAvailableRenderId();
    }

    private IIcon getIcon(BlockPaperLantern block, int index) {
        switch (index) {
            case 0:
                return block.torchIcon;
            case 1:
                return block.stickIcon;
            default:
                return block.getIcon(0, 0);
        }
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        BlockPaperLantern lantern = (BlockPaperLantern) block;
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        for (float[] element : ELEMENTS) {
            renderer.setRenderBounds(
                element[0] / 16.0D,
                element[1] / 16.0D,
                element[2] / 16.0D,
                element[3] / 16.0D,
                element[4] / 16.0D,
                element[5] / 16.0D);
            IIcon icon = this.getIcon(lantern, (int) element[6]);

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
            tessellator.draw();
        }

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        BlockPaperLantern lantern = (BlockPaperLantern) block;

        for (float[] element : ELEMENTS) {
            renderer.setOverrideBlockTexture(this.getIcon(lantern, (int) element[6]));
            renderer.setRenderBounds(
                element[0] / 16.0D,
                element[1] / 16.0D,
                element[2] / 16.0D,
                element[3] / 16.0D,
                element[4] / 16.0D,
                element[5] / 16.0D);
            renderer.renderStandardBlock(block, x, y, z);
        }

        renderer.clearOverrideBlockTexture();
        renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return this.renderId;
    }
}
