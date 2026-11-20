package com.wuest.prefab.Items.Structures;

import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.wuest.prefab.BuildingMethods;
import com.wuest.prefab.Config.Structures.BasicStructureConfiguration;
import com.wuest.prefab.Gui.GuiLangKeys;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.StructureGen.CustomStructures.StructureBasic;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.EnumFacing;

/**
 * Creates a 50 block long bridge across water or empty space.
 *
 * @author WuestMan
 */
public class ItemInstantBridge extends Item {

    private StructureBasic basic;
    private BasicStructureConfiguration config;

    public ItemInstantBridge(String name) {
        super();
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setMaxDamage(10);
        this.setMaxStackSize(1);
        ModRegistry.setItemName(this, name);
    }

    /**
     * Gets the position of the player's feet.
     */
    public static BlockPos getPlayerPosition(EntityPlayer player) {
        return new BlockPos(player.posX, player.boundingBox.minY, player.posZ);
    }

    /**
     * Gets the horizontal facing of the player.
     */
    public static EnumFacing getHorizontalFacing(Entity entity) {
        return EnumFacing.fromAngle(entity.rotationYaw);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote) {
            MovingObjectPosition result = ItemInstantBridge.RayTrace(worldIn, player, 5.0F, true);
            BlockPos blockPos = null;

            if (result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
                blockPos = ItemInstantBridge.getPlayerPosition(player)
                    .down(2);
            } else if (result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                blockPos = new BlockPos(result.blockX, result.blockY, result.blockZ);
            }

            boolean hitLiquid = result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                && worldIn.getBlock(blockPos.getX(), blockPos.getY(), blockPos.getZ()) instanceof BlockLiquid;

            if (blockPos != null
                && (hitLiquid || result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS)) {
                if (this.BuildBridge(worldIn, player, blockPos)) {
                    stack.damageItem(1, player);
                    player.inventoryContainer.detectAndSendChanges();
                }
            }
        }

        return stack;
    }

    /**
     * Called each tick as long the item is on a player inventory.
     */
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity player, int itemSlot, boolean isSelected) {
        if (player instanceof EntityPlayer && worldIn.isRemote) {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            ItemStack mainHand = entityPlayer.getCurrentEquippedItem();

            boolean selected = mainHand != null && mainHand.getItem() instanceof ItemInstantBridge;

            if (selected) {
                MovingObjectPosition result = ItemInstantBridge.RayTrace(worldIn, entityPlayer, 5.0F, true);
                boolean miss = result == null || result.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
                BlockPos blockPos = miss ? ItemInstantBridge.getPlayerPosition(entityPlayer)
                    .down(1) : new BlockPos(result.blockX, result.blockY, result.blockZ);

                boolean hitLiquid = !miss && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                    && worldIn.getBlock(blockPos.getX(), blockPos.getY(), blockPos.getZ()) instanceof BlockLiquid;

                if (hitLiquid || miss) {
                    if (!miss) {
                        blockPos = blockPos.up();
                    }

                    ItemInstantBridge item = (ItemInstantBridge) stack.getItem();

                    if (item.basic == null) {
                        item.basic = StructureBasic
                            .CreateInstance("assets/prefab/structures/instant_bridge.zip", StructureBasic.class);
                        item.basic.setName("instant_bridge");
                        item.config = new BasicStructureConfiguration();
                        item.config.pos = new BlockPos(0, 0, 0);
                    }

                    EnumFacing facing = ItemInstantBridge.getHorizontalFacing(entityPlayer)
                        .getOpposite();

                    if (item.config.houseFacing != facing || !item.config.pos.equals(blockPos)) {
                        item.config.pos = blockPos;
                        item.config.houseFacing = facing;
                        Prefab.proxy.setPreviewStructure(item.basic, EnumFacing.NORTH, item.config, true);
                    }
                } else {
                    Prefab.proxy.setPreviewStructure(null, EnumFacing.NORTH, null, false);
                }
            } else {
                ItemInstantBridge item = (ItemInstantBridge) stack.getItem();

                if (item.basic != null && Prefab.proxy.isPreviewStructure(item.basic)) {
                    Prefab.proxy.setPreviewStructure(null, EnumFacing.NORTH, null, false);
                }

                item.basic = null;
                item.config = null;
            }
        }
    }

    public static MovingObjectPosition RayTrace(World world, EntityPlayer player, float distance,
        boolean includeFluids) {
        double eyeY = player.posY
            + (world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight());
        Vec3 vec3d = Vec3.createVectorHelper(player.posX, eyeY, player.posZ);
        Vec3 vec3d1 = player.getLook(1.0F);
        Vec3 vec3d2 = vec3d.addVector(vec3d1.xCoord * distance, vec3d1.yCoord * distance, vec3d1.zCoord * distance);
        return world.func_147447_a(vec3d, vec3d2, includeFluids, false, true);
    }

    private boolean BuildBridge(World worldIn, EntityPlayer playerIn, BlockPos originalBlockPos) {
        EnumFacing playerFacing = ItemInstantBridge.getHorizontalFacing(playerIn);

        // Check to see if the bridge can be built.
        BlockPos startingPos = originalBlockPos.offset(playerFacing.rotateYCCW(), 2);
        BlockPos endPos = originalBlockPos.offset(playerFacing, 50)
            .offset(playerFacing.rotateY(), 2)
            .up(2);

        if (!BuildingMethods.CheckBuildSpaceForAllowedBlockReplacement(null, worldIn, startingPos, endPos, playerIn)) {
            playerIn.addChatComponentMessage(
                new ChatComponentTranslation(GuiLangKeys.GUI_STRUCTURE_NOBUILD)
                    .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
            return false;
        }

        for (int i = 0; i < 50; i++) {
            BlockPos currentPos = originalBlockPos.offset(playerFacing, i)
                .up();

            BuildingMethods.ReplaceBlock(worldIn, currentPos, Blocks.cobblestone);
            BuildingMethods.ReplaceBlock(worldIn, currentPos.offset(playerFacing.rotateYCCW()), Blocks.cobblestone);
            BuildingMethods.ReplaceBlock(worldIn, currentPos.offset(playerFacing.rotateYCCW(), 2), Blocks.cobblestone);
            BuildingMethods.ReplaceBlock(worldIn, currentPos.offset(playerFacing.rotateY()), Blocks.cobblestone);
            BuildingMethods.ReplaceBlock(worldIn, currentPos.offset(playerFacing.rotateY(), 2), Blocks.cobblestone);
            BuildingMethods.ReplaceBlock(
                worldIn,
                currentPos.offset(playerFacing.rotateYCCW(), 2)
                    .up(),
                Blocks.cobblestone_wall);
            BuildingMethods.ReplaceBlock(
                worldIn,
                currentPos.offset(playerFacing.rotateY(), 2)
                    .up(),
                Blocks.cobblestone_wall);

            if (i % 6 == 0) {
                // Place torches on top of the walls.
                BuildingMethods.ReplaceBlock(
                    worldIn,
                    currentPos.offset(playerFacing.rotateYCCW(), 2)
                        .up(2),
                    Blocks.torch);
                BuildingMethods.ReplaceBlock(
                    worldIn,
                    currentPos.offset(playerFacing.rotateY(), 2)
                        .up(2),
                    Blocks.torch);
            }
        }

        return true;
    }
}
