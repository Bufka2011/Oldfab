package com.wuest.prefab.Blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
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
 * A block which becomes transparent (phases out) when right clicked or powered by redstone.
 * <p>
 * Metadata: the phasing progress (0, 2, 4, 6, 8 or 10) plus 1 if the block is phasing out.
 *
 * @author WuestMan
 */
public class BlockPhasing extends Block {

    public static final Material BlockMaterial = new BlockPaperLantern.SeeThroughMaterial(MapColor.airColor)
        .setImmovable();

    protected int tickRate = 2;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockPhasing(String name) {
        super(BlockPhasing.BlockMaterial);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setStepSound(Block.soundTypeStone);
        this.setHardness(0.6F);
    }

    public static EnumPhasingProgress getProgress(int meta) {
        return EnumPhasingProgress.ValueOf(meta - (meta % 2));
    }

    public static boolean isPhasingOut(int meta) {
        return meta % 2 == 1;
    }

    public static int getMeta(EnumPhasingProgress progress, boolean phasingOut) {
        return progress.getMeta() + (phasingOut ? 1 : 0);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote) {
            EnumPhasingProgress progress = BlockPhasing.getProgress(world.getBlockMetadata(x, y, z));

            if (progress == EnumPhasingProgress.base) {
                // Only phase this block out if it's currently a full block.
                this.removeBlockAndNeighborsFromList(new BlockPos(x, y, z), world);
                world.scheduleBlockUpdate(x, y, z, this, this.tickRate);
            }
        }

        return true;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        boolean poweredSide = world.isBlockIndirectlyGettingPowered(x, y, z);

        if (poweredSide) {
            return BlockPhasing.getMeta(EnumPhasingProgress.transparent, true);
        }

        return 0;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);

        if (!world.isRemote && world.isBlockIndirectlyGettingPowered(x, y, z)) {
            this.setPoweredStatusAndUpdateNeighbors(
                true,
                world,
                new BlockPos(x, y, z),
                0,
                new ArrayList<BlockPos>(),
                false);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);
        BlockPos pos = new BlockPos(x, y, z);

        if (ModEventHandler.RedstoneAffectedBlockPositions.contains(pos)) {
            ModEventHandler.RedstoneAffectedBlockPositions.remove(pos);
        }

        boolean poweredSide = world.isBlockIndirectlyGettingPowered(x, y, z);
        EnumPhasingProgress currentState = BlockPhasing.getProgress(meta);

        if (poweredSide && currentState == EnumPhasingProgress.transparent) {
            // Set this block and all neighbor Phasic Blocks to base. This will cause all neighbors to update.
            this.setPoweredStatusAndUpdateNeighbors(false, world, pos, 0, new ArrayList<BlockPos>(), false);
        }
    }

    @Override
    public int tickRate(World world) {
        return this.tickRate;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        if (!world.isRemote && neighborBlock.canProvidePower()) {
            boolean poweredSide = world.isBlockIndirectlyGettingPowered(x, y, z);
            EnumPhasingProgress currentState = BlockPhasing.getProgress(world.getBlockMetadata(x, y, z));
            BlockPos pos = new BlockPos(x, y, z);

            if (poweredSide && currentState == EnumPhasingProgress.base) {
                this.setPoweredStatusAndUpdateNeighbors(true, world, pos, 0, new ArrayList<BlockPos>(), true);
            } else if (!poweredSide && currentState == EnumPhasingProgress.transparent) {
                this.setPoweredStatusAndUpdateNeighbors(false, world, pos, 0, new ArrayList<BlockPos>(), true);
            }
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        int tickDelay = this.tickRate;
        BlockPos pos = new BlockPos(x, y, z);

        if (ModEventHandler.RedstoneAffectedBlockPositions.contains(pos)) {
            return;
        }

        int meta = world.getBlockMetadata(x, y, z);
        EnumPhasingProgress progress = BlockPhasing.getProgress(meta);
        boolean phasingOut = BlockPhasing.isPhasingOut(meta);

        if (progress == EnumPhasingProgress.base) {
            // Phasing out; schedule updates for all of the neighbors.
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos neighbor = pos.offset(facing);
                Block currentBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());

                if (currentBlock instanceof BlockPhasing) {
                    world.scheduleBlockUpdate(
                        neighbor.getX(),
                        neighbor.getY(),
                        neighbor.getZ(),
                        currentBlock,
                        tickDelay);
                }
            }

            phasingOut = true;
        }

        int updatedMeta = progress.getMeta();

        if (updatedMeta == EnumPhasingProgress.eighty_percent.getMeta() && phasingOut) {
            // Wait 5 seconds when the block is transparent.
            tickDelay = 100;
        }

        if (updatedMeta == EnumPhasingProgress.transparent.getMeta() && phasingOut) {
            phasingOut = false;
        }

        if (updatedMeta == EnumPhasingProgress.twenty_percent.getMeta() && !phasingOut) {
            // Don't schedule another update, the block is back to normal.
            tickDelay = -1;
        }

        updatedMeta = phasingOut ? updatedMeta + 2 : updatedMeta - 2;
        progress = EnumPhasingProgress.ValueOf(updatedMeta);

        world.setBlockMetadataWithNotify(x, y, z, BlockPhasing.getMeta(progress, phasingOut), 3);
        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);

        if (tickDelay > 0) {
            world.scheduleBlockUpdate(x, y, z, this, tickDelay);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return EnumPhasingProgress.base.getMeta();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        if (BlockPhasing.getProgress(world.getBlockMetadata(x, y, z)) == EnumPhasingProgress.transparent) {
            return null;
        }

        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        if (BlockPhasing.getProgress(world.getBlockMetadata(x, y, z)) == EnumPhasingProgress.transparent) {
            return null;
        }

        return super.collisionRayTrace(world, x, y, z, start, end);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        Block block = world.getBlock(x, y, z);

        if (block == this) {
            return false;
        }

        return super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.icons = new IIcon[EnumPhasingProgress.values().length];

        for (EnumPhasingProgress progress : EnumPhasingProgress.values()) {
            this.icons[progress.ordinal()] = register
                .registerIcon(Prefab.MODID + ":block_phasing_" + progress.getName());
        }

        this.blockIcon = this.icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.icons[BlockPhasing.getProgress(meta)
            .ordinal()];
    }

    protected void removeBlockAndNeighborsFromList(BlockPos pos, World world) {
        if (ModEventHandler.RedstoneAffectedBlockPositions.contains(pos)) {
            ModEventHandler.RedstoneAffectedBlockPositions.remove(pos);
        }

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(facing);

            if (ModEventHandler.RedstoneAffectedBlockPositions.contains(neighbor)) {
                if (world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ()) instanceof BlockPhasing) {
                    this.removeBlockAndNeighborsFromList(neighbor, world);
                }
            }
        }
    }

    protected void setPoweredStatusAndUpdateNeighbors(boolean setToTransparent, World world, BlockPos pos,
        int cascadeCount, ArrayList<BlockPos> cascadedBlockPos, boolean setCurrentBlock) {
        if (setCurrentBlock) {
            world.setBlockMetadataWithNotify(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                BlockPhasing.getMeta(
                    setToTransparent ? EnumPhasingProgress.transparent : EnumPhasingProgress.base,
                    setToTransparent),
                3);
            world.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }

        // Make sure this block is in the list of redstone affected blocks so the ticks don't change it.
        if (!ModEventHandler.RedstoneAffectedBlockPositions.contains(pos)) {
            ModEventHandler.RedstoneAffectedBlockPositions.add(pos);
        }

        cascadedBlockPos.add(pos);
        cascadeCount++;

        if (cascadeCount > 100) {
            return;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(facing);
            Block neighborBlock = world.getBlock(neighbor.getX(), neighbor.getY(), neighbor.getZ());

            if (neighborBlock instanceof BlockPhasing && !cascadedBlockPos.contains(neighbor)) {
                ((BlockPhasing) neighborBlock).setPoweredStatusAndUpdateNeighbors(
                    setToTransparent,
                    world,
                    neighbor,
                    cascadeCount,
                    cascadedBlockPos,
                    true);
            }
        }
    }

    public enum EnumPhasingProgress {

        base(0, "base"),
        twenty_percent(2, "twenty_percent"),
        forty_percent(4, "forty_percent"),
        sixty_percent(6, "sixty_percent"),
        eighty_percent(8, "eighty_percent"),
        transparent(10, "transparent");

        private final int meta;
        private final String name;

        EnumPhasingProgress(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta() {
            return this.meta;
        }

        public String getName() {
            return this.name;
        }

        public static EnumPhasingProgress ValueOf(int meta) {
            for (EnumPhasingProgress progress : EnumPhasingProgress.values()) {
                if (progress.meta == meta) {
                    return progress;
                }
            }

            return EnumPhasingProgress.base;
        }
    }
}
