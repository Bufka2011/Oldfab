package com.wuest.prefab.Util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Minecraft 1.7.10 does not have block states. This simple class pairs a block with its metadata value and optional
 * tile entity data so the rest of the mod can be written in a similar way to the 1.8+ code.
 */
public class BlockState {

    public static final BlockState AIR = new BlockState(Blocks.air, 0);

    private final Block block;
    private final int meta;
    private final NBTTagCompound tileEntityData;

    public BlockState(Block block) {
        this(block, 0);
    }

    public BlockState(Block block, int meta) {
        this(block, meta, null);
    }

    public BlockState(Block block, int meta, NBTTagCompound tileEntityData) {
        this.block = block == null ? Blocks.air : block;
        this.meta = meta & 15;
        this.tileEntityData = tileEntityData;
    }

    public Block getBlock() {
        return this.block;
    }

    public int getMeta() {
        return this.meta;
    }

    public NBTTagCompound getTileEntityData() {
        return this.tileEntityData;
    }

    public BlockState withMeta(int meta) {
        return new BlockState(this.block, meta, this.tileEntityData);
    }

    public static BlockState get(World world, BlockPos pos) {
        return new BlockState(
            world.getBlock(pos.getX(), pos.getY(), pos.getZ()),
            world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockState)) {
            return false;
        }

        BlockState other = (BlockState) obj;
        return other.block == this.block && other.meta == this.meta;
    }

    @Override
    public int hashCode() {
        return Block.getIdFromBlock(this.block) * 16 + this.meta;
    }

    @Override
    public String toString() {
        return Block.blockRegistry.getNameForObject(this.block) + "@" + this.meta;
    }
}
