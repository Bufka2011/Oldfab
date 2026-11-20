package com.wuest.prefab.Blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.wuest.prefab.Events.ModEventHandler;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.EnumFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The "smart glass" boundary block. This block is invisible but solid until it's powered by redstone.
 * <p>
 * Metadata: 1 when powered, otherwise 0.
 *
 * @author WuestMan
 */
public class BlockBoundary extends Block {

    @SideOnly(Side.CLIENT)
    private IIcon transparentIcon;

    public BlockBoundary(String name) {
        super(BlockPhasing.BlockMaterial);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setStepSound(Block.soundTypeStone);
        this.setHardness(0.6F);
    }

    public static boolean isPowered(int meta) {
        return (meta & 1) == 1;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);
        BlockPos pos = new BlockPos(x, y, z);

        if (ModEventHandler.RedstoneAffectedBlockPositions.contains(pos)) {
            ModEventHandler.RedstoneAffectedBlockPositions.remove(pos);
        }

        boolean poweredSide = world.isBlockIndirectlyGettingPowered(x, y, z);

        if (poweredSide) {
            // Set this block and all neighbor Boundary Blocks to not powered.
            this.setNeighborGlassBlocksPoweredStatus(world, pos, !poweredSide, 0, new ArrayList<BlockPos>(), false);
        }
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return world.isBlockIndirectlyGettingPowered(x, y, z) ? 1 : 0;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);

        if (!world.isRemote && world.isBlockIndirectlyGettingPowered(x, y, z)) {
            this.setNeighborGlassBlocksPoweredStatus(
                world,
                new BlockPos(x, y, z),
                true,
                0,
                new ArrayList<BlockPos>(),
                false);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        if (!world.isRemote && neighborBlock.canProvidePower()) {
            boolean poweredSide = world.isBlockIndirectlyGettingPowered(x, y, z);
            this.setNeighborGlassBlocksPoweredStatus(
                world,
                new BlockPos(x, y, z),
                poweredSide,
                0,
                new ArrayList<BlockPos>(),
                true);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        // Always solid.
        return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        if (!BlockBoundary.isPowered(world.getBlockMetadata(x, y, z))) {
            return null;
        }

        return super.collisionRayTrace(world, x, y, z, start, end);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        if (world.getBlock(x, y, z) == this) {
            return false;
        }

        return super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(Prefab.MODID + ":block_boundary");
        this.transparentIcon = register.registerIcon(Prefab.MODID + ":block_phasing_transparent");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return BlockBoundary.isPowered(world.getBlockMetadata(x, y, z)) ? this.blockIcon : this.transparentIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        // The item should always look like the boundary block.
        return this.blockIcon;
    }

    protected void setNeighborGlassBlocksPoweredStatus(World world, BlockPos pos, boolean isPowered, int cascadeCount,
        ArrayList<BlockPos> cascadedBlockPos, boolean setCurrentBlock) {
        cascadeCount++;

        if (cascadeCount > 100) {
            return;
        }

        if (setCurrentBlock) {
            world.setBlockMetadataWithNotify(pos.getX(), pos.getY(), pos.getZ(), isPowered ? 1 : 0, 3);
            world.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }

        cascadedBlockPos.add(pos);

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(facing);
            Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());

            if (neighborBlock instanceof BlockBoundary && !cascadedBlockPos.contains(neighbor)) {
                ((BlockBoundary) neighborBlock).setNeighborGlassBlocksPoweredStatus(
                    world,
                    neighbor,
                    isPowered,
                    cascadeCount,
                    cascadedBlockPos,
                    true);
            }
        }
    }
}
