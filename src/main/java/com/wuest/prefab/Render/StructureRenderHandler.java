package com.wuest.prefab.Render;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.Gui.GuiLangKeys;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.StructureGen.BuildBlock;
import com.wuest.prefab.StructureGen.Structure;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.BlockState;
import com.wuest.prefab.Util.EnumFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Renders a transparent preview of a structure in the world.
 *
 * @author WuestMan
 */
@SideOnly(Side.CLIENT)
public class StructureRenderHandler {

    // player's overlapping on structures and other things.
    public static StructureConfiguration currentConfiguration;
    public static Structure currentStructure;
    public static EnumFacing assumedNorth;
    public static boolean rendering = false;
    public static boolean showedMessage = false;
    private static int dimension;

    private static final RenderBlocks renderBlocks = new RenderBlocks();

    /**
     * The cached list of blocks to render. Each entry is the position and the block state.
     */
    private static ArrayList<Object[]> renderList = new ArrayList<>();

    /**
     * Resets the structure to show in the world.
     *
     * @param structure     The structure to show in the world, pass null to clear out the client.
     * @param assumedNorth  The assumed norther facing for this structure.
     * @param configuration The configuration for this structure.
     */
    public static void setStructure(Structure structure, EnumFacing assumedNorth,
        StructureConfiguration configuration) {
        StructureRenderHandler.currentStructure = structure;
        StructureRenderHandler.assumedNorth = assumedNorth;
        StructureRenderHandler.currentConfiguration = configuration;
        StructureRenderHandler.showedMessage = false;
        StructureRenderHandler.renderList = new ArrayList<>();

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld != null) {
            StructureRenderHandler.dimension = mc.theWorld.provider.dimensionId;
        }

        if (structure != null && configuration != null && configuration.pos != null) {
            for (BuildBlock buildBlock : structure.getBlocks()) {
                StructureRenderHandler.addToRenderList(buildBlock, configuration, assumedNorth);
            }
        }
    }

    private static void addToRenderList(BuildBlock buildBlock, StructureConfiguration configuration,
        EnumFacing assumedNorth) {
        BlockState state = buildBlock.getTranslatedState(configuration, assumedNorth);

        if (state != null && state.getBlock()
            .getMaterial() != net.minecraft.block.material.Material.air) {
            BlockPos pos = buildBlock.getStartingPosition()
                .getRelativePosition(configuration.pos, configuration.houseFacing);
            StructureRenderHandler.renderList.add(new Object[] { pos, state });
        }

        if (buildBlock.getSubBlock() != null) {
            StructureRenderHandler.addToRenderList(buildBlock.getSubBlock(), configuration, assumedNorth);
        }
    }

    /**
     * This is to render the currently bound structure.
     *
     * @param player       The player to render the structure for.
     * @param partialTicks The partial ticks for this frame.
     */
    public static void renderPlayerLook(EntityPlayer player, float partialTicks) {
        if (StructureRenderHandler.currentStructure == null
            || StructureRenderHandler.dimension != player.worldObj.provider.dimensionId
            || StructureRenderHandler.currentConfiguration == null
            || !Prefab.proxy.proxyConfiguration.enableStructurePreview) {
            return;
        }

        World world = player.worldObj;
        boolean didAny = false;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendColor(1.0F, 1.0F, 1.0F, 0.4F);
        GL11.glBlendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        RenderHelper.disableStandardItemLighting();

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        rendering = true;

        for (Object[] entry : StructureRenderHandler.renderList) {
            BlockPos pos = (BlockPos) entry[0];
            BlockState state = (BlockState) entry[1];

            if (!world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())) {
                continue;
            }

            didAny = true;
            Block block = state.getBlock();

            if (!RenderBlocks.renderItemIn3d(block.getRenderType())) {
                continue;
            }

            GL11.glPushMatrix();
            GL11.glTranslated(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            try {
                StructureRenderHandler.renderBlocks.renderBlockAsItem(block, state.getMeta(), 1.0F);
            } catch (Exception e) {
                // Some blocks cannot be rendered this way, ignore them.
            }

            GL11.glPopMatrix();
        }

        rendering = false;

        GL11.glDepthMask(true);
        GL11.glPopAttrib();
        GL11.glPopMatrix();

        if (!didAny) {
            // Nothing was generated, tell the user this through a chat message and re-set the structure information.
            StructureRenderHandler.setStructure(null, EnumFacing.NORTH, null);
            player.addChatComponentMessage(
                new ChatComponentTranslation(GuiLangKeys.GUI_PREVIEW_COMPLETE)
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
        } else if (!StructureRenderHandler.showedMessage) {
            player.addChatComponentMessage(
                new ChatComponentTranslation(GuiLangKeys.GUI_PREVIEW_NOTICE)
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            StructureRenderHandler.showedMessage = true;
        }
    }
}
