package com.wuest.prefab;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;

import com.wuest.prefab.Config.Structures.HouseConfiguration;
import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.BlockState;
import com.wuest.prefab.Util.EnumFacing;

/**
 * This class is used to hold utility methods for building structures.
 *
 * @author WuestMan
 */
public class BuildingMethods {

    /**
     * Clears a space for the structure, making sure that there is an extra space around the area.
     */
    public static void ClearSpace(World world, BlockPos startingPosition, int width, int height, int depth,
        EnumFacing houseFacing) {
        BlockPos northSide = startingPosition.offset(houseFacing, 5)
            .offset(houseFacing.rotateY(), 5);
        int clearedWidth = width + 12;
        int wallLength = depth + 12;

        for (int i = 0; i < clearedWidth; i++) {
            BuildingMethods
                .CreateWall(world, height, wallLength, houseFacing.getOpposite(), northSide, Blocks.air, null);
            northSide = northSide.offset(houseFacing.rotateYCCW());
        }
    }

    /**
     * Clears an exact space.
     */
    public static void ClearSpaceExact(World world, BlockPos startingPosition, int width, int height, int depth,
        EnumFacing houseFacing) {
        BlockPos otherCorner = startingPosition.offset(houseFacing.getOpposite(), depth)
            .offset(houseFacing.rotateYCCW(), width);

        for (BlockPos pos : BlockPos.getAllInBox(startingPosition, otherCorner)) {
            if (!BuildingMethods.isAir(world, pos)) {
                BuildingMethods.ReplaceBlock(world, pos, Blocks.air);
            }
        }
    }

    /**
     * Consolidates the drops of a block into an existing list of item stacks.
     */
    public static ArrayList<ItemStack> ConsolidateDrops(Block block, World world, BlockPos pos, int meta,
        ArrayList<ItemStack> originalStacks, ArrayList<Item> itemsToNotAdd) {
        for (ItemStack stack : block.getDrops(world, pos.getX(), pos.getY(), pos.getZ(), meta, 1)) {
            if (itemsToNotAdd != null && itemsToNotAdd.contains(stack.getItem())) {
                continue;
            }

            boolean foundStack = false;

            for (ItemStack existingStack : originalStacks) {
                if (existingStack.isItemEqual(stack)
                    && existingStack.stackSize + stack.stackSize <= stack.getMaxStackSize()) {
                    existingStack.stackSize = existingStack.stackSize + stack.stackSize;
                    foundStack = true;
                    break;
                }
            }

            if (!foundStack) {
                originalStacks.add(stack);
            }
        }

        return originalStacks;
    }

    /**
     * Creates a wall of blocks.
     */
    public static ArrayList<ItemStack> CreateWall(World world, int height, int length, EnumFacing direction,
        BlockPos startingPosition, Block replacementBlock, ArrayList<Item> itemsToNotAdd) {
        return BuildingMethods.CreateWall(
            world,
            height,
            length,
            direction,
            startingPosition,
            new BlockState(replacementBlock, 0),
            itemsToNotAdd);
    }

    /**
     * Creates a wall of blocks.
     */
    public static ArrayList<ItemStack> CreateWall(World world, int height, int length, EnumFacing direction,
        BlockPos startingPosition, BlockState replacementBlock) {
        return BuildingMethods.CreateWall(world, height, length, direction, startingPosition, replacementBlock, null);
    }

    /**
     * Creates a wall of blocks.
     */
    public static ArrayList<ItemStack> CreateWall(World world, int height, int length, EnumFacing direction,
        BlockPos startingPosition, BlockState replacementBlock, ArrayList<Item> itemsToNotAdd) {
        ArrayList<ItemStack> itemsDropped = new ArrayList<>();

        BlockPos wallPos;

        for (int i = 0; i < height; i++) {
            wallPos = startingPosition.up(i);

            for (int j = 0; j < length; j++) {
                BlockState existing = BlockState.get(world, wallPos);

                if (existing.getBlock() != Blocks.air) {
                    for (ItemStack stack : existing.getBlock()
                        .getDrops(world, wallPos.getX(), wallPos.getY(), wallPos.getZ(), existing.getMeta(), 1)) {
                        if (itemsToNotAdd != null && itemsToNotAdd.contains(stack.getItem())) {
                            continue;
                        }

                        itemsDropped.add(stack);
                    }
                }

                BuildingMethods.ReplaceBlock(world, wallPos, replacementBlock);
                wallPos = wallPos.offset(direction);
            }
        }

        return itemsDropped;
    }

    /**
     * Sets a floor of blocks.
     */
    public static ArrayList<ItemStack> SetFloor(World world, BlockPos pos, Block block, int width, int depth,
        ArrayList<ItemStack> originalStack, EnumFacing facing, ArrayList<Item> itemsToNotAdd) {
        return BuildingMethods
            .SetFloor(world, pos, new BlockState(block, 0), width, depth, originalStack, facing, itemsToNotAdd);
    }

    /**
     * Sets a floor of blocks.
     */
    public static ArrayList<ItemStack> SetFloor(World world, BlockPos pos, BlockState block, int width, int depth,
        ArrayList<ItemStack> originalStack, EnumFacing facing, ArrayList<Item> itemsToNotAdd) {
        for (int i = 0; i < width; i++) {
            originalStack.addAll(BuildingMethods.CreateWall(world, 1, depth, facing, pos, block, itemsToNotAdd));
            pos = pos.offset(facing.rotateY());
        }

        return originalStack;
    }

    /**
     * Sets the ceiling (roof) of a house.
     */
    public static void SetCeiling(World world, BlockPos pos, BlockState block, int width, int depth, Block stairs,
        HouseConfiguration configuration, EnumFacing houseFacing, ArrayList<Item> itemsToNotAdd) {
        // If the ceiling is flat, call SetFloor since it's laid out the exact same way.
        if (configuration.isCeilingFlat) {
            BuildingMethods.SetFloor(
                world,
                pos,
                block,
                width,
                depth,
                new ArrayList<ItemStack>(),
                houseFacing.getOpposite(),
                itemsToNotAdd);
            return;
        }

        int wallWidth = configuration.houseWidth;
        int wallDepth = configuration.houseDepth;
        int height = wallWidth / 2;
        boolean isWider = false;

        if (wallWidth > wallDepth) {
            height = wallDepth / 2;
            isWider = true;
        }

        for (int i = 0; i <= height; i++) {
            // I is the vertical position.
            for (int j = 0; j < 4; j++) {
                // Default to wall 1 (back wall).
                EnumFacing facing = houseFacing.rotateYCCW();
                EnumFacing flowDirection = houseFacing.getOpposite();
                int wallSize = wallDepth;

                switch (j) {
                    case 1: {
                        facing = houseFacing;
                        flowDirection = houseFacing.rotateYCCW();
                        wallSize = wallWidth;
                        break;
                    }

                    case 2: {
                        facing = houseFacing.rotateY();
                        flowDirection = houseFacing;
                        wallSize = wallDepth;
                        break;
                    }

                    case 3: {
                        facing = houseFacing.getOpposite();
                        flowDirection = houseFacing.rotateY();
                        wallSize = wallWidth;
                        break;
                    }
                }

                for (int k = 0; k <= wallSize; k++) {
                    // j is the north/south counter.
                    BuildingMethods
                        .ReplaceBlock(world, pos, new BlockState(stairs, BuildingMethods.getStairsMeta(facing, false)));
                    pos = pos.offset(flowDirection);
                }
            }

            pos = pos.offset(houseFacing.rotateYCCW())
                .offset(houseFacing.getOpposite())
                .up();
            wallWidth = wallWidth - 2;
            wallDepth = wallDepth - 2;
        }

        // Check to see if the house's width or depth is greater than 1/2 the length of the other.
        if (BuildingMethods.isAir(world, pos.down())) {
            int finalStoneCount = wallDepth;

            if (isWider) {
                finalStoneCount = wallWidth;
            }

            // Add the number of sides to the final stone count.
            if (finalStoneCount < 1) {
                finalStoneCount = 1;
            } else {
                finalStoneCount = finalStoneCount + 2;
            }

            BlockPos torchPos = pos;

            for (int i = 0; i < finalStoneCount; i++) {
                BuildingMethods.ReplaceBlock(world, pos, block);

                if (isWider) {
                    pos = pos.offset(houseFacing.rotateYCCW());
                } else {
                    pos = pos.offset(houseFacing.getOpposite());
                }
            }

            Block torchLocation = world.getBlock(torchPos.getX(), torchPos.getY(), torchPos.getZ());

            if (torchLocation.canPlaceTorchOnTop(world, torchPos.getX(), torchPos.getY(), torchPos.getZ())) {
                BuildingMethods.ReplaceBlock(world, torchPos.up(), new BlockState(Blocks.torch, 5));
            }
        }
    }

    /**
     * Gets the 1.7.10 metadata for a stairs block facing the specified direction.
     */
    public static int getStairsMeta(EnumFacing facing, boolean upsideDown) {
        int meta = facing.isHorizontal() ? 5 - facing.getIndex() : 0;
        return meta | (upsideDown ? 4 : 0);
    }

    /**
     * Gets the 1.7.10 metadata for a torch attached to the side of a block.
     */
    public static int GetTorchFacing(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                return 4;
            case EAST:
                return 1;
            case SOUTH:
                return 3;
            case WEST:
                return 2;
            default:
                return 5;
        }
    }

    public static boolean isSideSolid(World world, BlockPos pos, EnumFacing side) {
        return world.isSideSolid(pos.getX(), pos.getY(), pos.getZ(), BuildingMethods.toForgeDirection(side));
    }

    public static TileEntity getTileEntity(World world, BlockPos pos) {
        return world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isAir(World world, BlockPos pos) {
        return world.isAirBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void setBlockToAir(World world, BlockPos pos) {
        world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void ReplaceBlock(World world, BlockPos pos, Block replacementBlock) {
        BuildingMethods.ReplaceBlock(world, pos, new BlockState(replacementBlock, 0), 3);
    }

    public static void ReplaceBlockNoAir(World world, BlockPos pos, Block replacementBlock) {
        BuildingMethods.ReplaceBlockNoAir(world, pos, new BlockState(replacementBlock, 0), 3);
    }

    public static void ReplaceBlock(World world, BlockPos pos, BlockState replacementBlockState) {
        BuildingMethods.ReplaceBlock(world, pos, replacementBlockState, 3);
    }

    public static void ReplaceBlockNoAir(World world, BlockPos pos, BlockState replacementBlockState) {
        BuildingMethods.ReplaceBlockNoAir(world, pos, replacementBlockState, 3);
    }

    public static void ReplaceBlock(World world, BlockPos pos, BlockState replacementBlockState, int flags) {
        world.setBlock(pos.getX(), pos.getY(), pos.getZ(), Blocks.air, 0, flags);
        BuildingMethods.ReplaceBlockNoAir(world, pos, replacementBlockState, flags);
    }

    public static void ReplaceBlockNoAir(World world, BlockPos pos, BlockState replacementBlockState, int flags) {
        world.setBlock(
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            replacementBlockState.getBlock(),
            replacementBlockState.getMeta(),
            flags);

        // Make sure that the meta data is actually set, some blocks change it when they are added to the world.
        if (world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()) != replacementBlockState.getMeta()
            && world.getBlock(pos.getX(), pos.getY(), pos.getZ()) == replacementBlockState.getBlock()) {
            world
                .setBlockMetadataWithNotify(pos.getX(), pos.getY(), pos.getZ(), replacementBlockState.getMeta(), flags);
        }

        NBTTagCompound tileData = replacementBlockState.getTileEntityData();

        if (tileData != null) {
            TileEntity tileEntity = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());

            if (tileEntity != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tileEntity.writeToNBT(tag);

                for (Object key : tileData.func_150296_c()) {
                    tag.setTag(
                        (String) key,
                        tileData.getTag((String) key)
                            .copy());
                }

                tag.setInteger("x", pos.getX());
                tag.setInteger("y", pos.getY());
                tag.setInteger("z", pos.getZ());
                tileEntity.readFromNBT(tag);
                tileEntity.markDirty();
                world.markBlockForUpdate(pos.getX(), pos.getY(), pos.getZ());
            }
        }
    }

    /**
     * Checks to see if the player is allowed to replace the blocks in the build area.
     */
    public static boolean CheckBuildSpaceForAllowedBlockReplacement(StructureConfiguration configuration, World world,
        BlockPos startBlockPos, BlockPos endBlockPos, EntityPlayer player) {
        // Check each block in the space to be cleared if it's protected from breaking or placing.
        for (BlockPos currentPos : BlockPos.getAllInBox(startBlockPos, endBlockPos)) {
            int x = currentPos.getX();
            int y = currentPos.getY();
            int z = currentPos.getZ();

            if (y < 0 || y >= world.getHeight()) {
                continue;
            }

            Block block = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);

            if (!block.isAir(world, x, y, z)) {
                BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(x, y, z, world, block, meta, player);

                if (MinecraftForge.EVENT_BUS.post(breakEvent)) {
                    return false;
                }
            }

            BlockEvent.PlaceEvent placeEvent = new BlockEvent.PlaceEvent(
                new BlockSnapshot(world, x, y, z, block, meta),
                Blocks.air,
                player);

            if (MinecraftForge.EVENT_BUS.post(placeEvent)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Converts the back-ported facing into a forge direction.
     */
    public static ForgeDirection toForgeDirection(EnumFacing facing) {
        return ForgeDirection.getOrientation(facing.getIndex());
    }
}
