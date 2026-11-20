package com.wuest.prefab.StructureGen.CustomStructures;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;

import com.wuest.prefab.BuildingMethods;
import com.wuest.prefab.Config.Structures.HouseConfiguration;
import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.StructureGen.BuildBlock;
import com.wuest.prefab.StructureGen.Structure;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.BlockState;
import com.wuest.prefab.Util.EnumFacing;

/**
 * 
 * @author WuestMan
 *
 */
public class StructureAlternateStart extends Structure {

    private BlockPos chestPosition = null;
    private BlockPos furnacePosition = null;
    private BlockPos trapDoorPosition = null;
    private BlockPos signPosition = null;
    private static ArrayList<BlockPos> torchPositions = null;

    @Override
    protected Boolean CustomBlockProcessingHandled(StructureConfiguration configuration, BuildBlock block, World world,
        BlockPos originalPos, EnumFacing assumedNorth, Block foundBlock, BlockState blockState, EntityPlayer player) {
        HouseConfiguration houseConfig = (HouseConfiguration) configuration;

        if ((!houseConfig.addBed && foundBlock instanceof BlockBed)
            || (!houseConfig.addChest && foundBlock instanceof BlockChest)
            || (!houseConfig.addTorches && foundBlock instanceof BlockTorch)
            || (!houseConfig.addCraftingTable
                && (foundBlock instanceof BlockWorkbench || foundBlock instanceof BlockFurnace))) {
            // Don't place the block, returning true means that this has been
            // "handled"
            return true;
        }

        if (foundBlock instanceof BlockFurnace) {
            this.furnacePosition = block.getStartingPosition()
                .getRelativePosition(originalPos, configuration.houseFacing);
        } else if (foundBlock instanceof BlockTrapDoor && houseConfig.addMineShaft) {
            // The trap door will still be added, but the mine shaft may not be
            // built.
            this.trapDoorPosition = block.getStartingPosition()
                .getRelativePosition(originalPos, configuration.houseFacing);
        } else if (foundBlock instanceof BlockChest && this.chestPosition == null) {
            this.chestPosition = block.getStartingPosition()
                .getRelativePosition(originalPos, configuration.houseFacing);
        } else if (foundBlock == Blocks.standing_sign) {
            this.signPosition = block.getStartingPosition()
                .getRelativePosition(originalPos, configuration.houseFacing);
        }

        if (foundBlock == Blocks.stained_glass) {
            blockState = blockState.withMeta(houseConfig.glassColor.getMetadata());
            block.setBlockState(blockState);
            this.priorityOneBlocks.add(block);

            return true;
        } else if (foundBlock == Blocks.stained_glass_pane) {
            block.setBlockState(blockState.withMeta(houseConfig.glassColor.getMetadata()));
            this.priorityOneBlocks.add(block);
            return true;
        }

        return false;
    }

    /**
     * This method is used after the main building is build for any additional
     * structures or modifications.
     * 
     * @param configuration The structure configuration.
     * @param world         The current world.
     * @param originalPos   The original position clicked on.
     * @param assumedNorth  The assumed northern direction.
     * @param player        The player which initiated the construction.
     */
    @Override
    public void AfterBuilding(StructureConfiguration configuration, World world, BlockPos originalPos,
        EnumFacing assumedNorth, EntityPlayer player) {
        HouseConfiguration houseConfig = (HouseConfiguration) configuration;

        if (this.furnacePosition != null) {
            // Fill the furnace.
            TileEntity tileEntity = world
                .getTileEntity(this.furnacePosition.getX(), this.furnacePosition.getY(), this.furnacePosition.getZ());
            if (tileEntity instanceof TileEntityFurnace) {
                TileEntityFurnace furnaceTile = (TileEntityFurnace) tileEntity;
                furnaceTile.setInventorySlotContents(1, new ItemStack(Items.coal, 20));
            }
        }

        if (this.chestPosition != null && houseConfig.addChestContents) {
            // Fill the chest.
            StructureAlternateStart.FillChest(world, this.chestPosition, houseConfig, player);
        }

        if (this.trapDoorPosition != null && this.trapDoorPosition.getY() > 15 && houseConfig.addMineShaft) {
            // Build the mineshaft.
            StructureAlternateStart.PlaceMineShaft(world, this.trapDoorPosition.down(), houseConfig.houseFacing, false);
        }

        if (this.signPosition != null) {
            TileEntity tileEntity = world
                .getTileEntity(this.signPosition.getX(), this.signPosition.getY(), this.signPosition.getZ());

            if (tileEntity instanceof TileEntitySign) {
                TileEntitySign signTile = (TileEntitySign) tileEntity;
                signTile.signText[0] = "This is";

                if (player.getCommandSenderName()
                    .length() >= 15) {
                    signTile.signText[1] = player.getCommandSenderName();
                } else {
                    signTile.signText[1] = player.getCommandSenderName() + "'s";
                }

                signTile.signText[2] = "house!";
                signTile.markDirty();
                world.markBlockForUpdate(this.signPosition.getX(), this.signPosition.getY(), this.signPosition.getZ());
            }
        }
    }

    public static void FillChest(World world, BlockPos itemPosition, HouseConfiguration configuration,
        EntityPlayer player) {
        // Add each stone tool to the chest and leather armor.
        TileEntity tileEntity = world.getTileEntity(itemPosition.getX(), itemPosition.getY(), itemPosition.getZ());

        if (tileEntity instanceof TileEntityChest) {
            TileEntityChest chestTile = (TileEntityChest) tileEntity;

            int itemSlot = 0;

            // Add the tools.
            if (Prefab.proxy.proxyConfiguration.addAxe) {
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.stone_axe));
            }

            if (Prefab.proxy.proxyConfiguration.addHoe) {
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.stone_hoe));

                // Trigger the "Time to Farm!" achievement.
                player.triggerAchievement(AchievementList.buildHoe);
            }

            if (Prefab.proxy.proxyConfiguration.addPickAxe) {
                // Trigger the "Time to Mine" achievement and the better
                // pick axe achievement.
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.stone_pickaxe));

                player.triggerAchievement(AchievementList.buildPickaxe);
                player.triggerAchievement(AchievementList.buildBetterPickaxe);

                if (configuration.addCraftingTable) {
                    // If the furnace/crafting table was created, trigger
                    // the "Hot Topic" achievement.
                    player.triggerAchievement(AchievementList.buildFurnace);
                }
            }

            if (Prefab.proxy.proxyConfiguration.addShovel) {
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.stone_shovel));
            }

            if (Prefab.proxy.proxyConfiguration.addSword) {
                // Include the swift blade if WuestUtilities has registered the
                // swift blades.
                Item sword = (Item) Item.itemRegistry.getObject("wuestutilities:itemSwiftBladeStone");

                if (sword == null) {
                    sword = Items.stone_sword;
                }

                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(sword));

                // Trigger the "Time to Strike" achievement.
                player.triggerAchievement(AchievementList.buildSword);
            }

            if (Prefab.proxy.proxyConfiguration.addArmor) {
                // Add the armor.
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.leather_boots));
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.leather_chestplate));
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.leather_helmet));
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.leather_leggings));
            }

            if (Prefab.proxy.proxyConfiguration.addFood) {
                // Add some bread.
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.bread, 20));

                player.triggerAchievement(AchievementList.makeBread);
            }

            if (Prefab.proxy.proxyConfiguration.addCrops) {
                // Add potatoes.
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.potato, 3));

                // Add carrots.
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.carrot, 3));

                // Add seeds.
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Items.wheat_seeds, 3));
            }

            if (Prefab.proxy.proxyConfiguration.addCobble) {
                // Add Cobblestone.
                chestTile
                    .setInventorySlotContents(itemSlot++, new ItemStack(Item.getItemFromBlock(Blocks.cobblestone), 64));
            }

            if (Prefab.proxy.proxyConfiguration.addDirt) {
                // Add Dirt.
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Item.getItemFromBlock(Blocks.dirt), 64));
            }

            if (Prefab.proxy.proxyConfiguration.addSaplings) {
                // Add oak saplings.
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Item.getItemFromBlock(Blocks.sapling), 3));
            }

            if (Prefab.proxy.proxyConfiguration.addTorches) {
                // Add a set of 20 torches.
                chestTile.setInventorySlotContents(itemSlot++, new ItemStack(Item.getItemFromBlock(Blocks.torch), 20));
            }
        }
    }

    public static void PlaceMineShaft(World world, BlockPos pos, EnumFacing facing, boolean onlyGatherOres) {
        // Keep track of all of the items to add to the chest at the end of the
        // shaft.
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();

        ArrayList<Item> blocksToNotAdd = new ArrayList<Item>();

        if (onlyGatherOres) {
            blocksToNotAdd.add(Item.getItemFromBlock(Blocks.sand));
            blocksToNotAdd.add(Item.getItemFromBlock(Blocks.sandstone));
            blocksToNotAdd.add(Item.getItemFromBlock(Blocks.cobblestone));
            blocksToNotAdd.add(Item.getItemFromBlock(Blocks.stone));
            blocksToNotAdd.add(Item.getItemFromBlock(Blocks.dirt));
        }

        stacks = StructureAlternateStart.CreateLadderShaft(world, pos, stacks, facing, blocksToNotAdd);

        // Get to Y11;
        pos = pos.down(pos.getY() - 10);

        ArrayList<ItemStack> tempStacks = new ArrayList<ItemStack>();

        BlockPos ceilingLevel = pos.up(4);

        tempStacks = BuildingMethods.SetFloor(
            world,
            ceilingLevel.offset(facing, 2)
                .offset(facing.rotateY(), 2)
                .offset(facing.getOpposite()),
            Blocks.stone,
            4,
            4,
            tempStacks,
            facing.getOpposite(),
            blocksToNotAdd);

        // After setting the floor, make sure to replace the ladder.
        BuildingMethods.ReplaceBlock(world, ceilingLevel, new BlockState(Blocks.ladder, (facing).getIndex()));

        BlockState torchState = new BlockState(Blocks.torch, 5);

        // Place the torches at this point since the entire shaft has been set.
        for (BlockPos torchPos : StructureAlternateStart.torchPositions) {
            BlockState surroundingState = BlockState.get(world, torchPos);
            Block surroundingBlock = surroundingState.getBlock();
            tempStacks = BuildingMethods.ConsolidateDrops(
                surroundingBlock,
                world,
                torchPos,
                surroundingState.getMeta(),
                tempStacks,
                blocksToNotAdd);
            BuildingMethods.ReplaceBlock(world, torchPos, torchState);
        }

        // The entire ladder has been created. Create a platform at this level
        // and place a chest next to the ladder.
        tempStacks.addAll(
            BuildingMethods.SetFloor(
                world,
                pos.offset(facing)
                    .offset(facing.rotateY()),
                Blocks.stone,
                3,
                4,
                tempStacks,
                facing.getOpposite(),
                blocksToNotAdd));

        // Remove the ladder stack since they shouldn't be getting that.
        for (int i = 0; i < tempStacks.size(); i++) {
            ItemStack stack = tempStacks.get(i);

            if (stack.getItem() == Item.getItemFromBlock(Blocks.ladder)) {
                tempStacks.remove(i);
                i--;
            }
        }

        // Now that the floor has been set, go up 1 block to star creating the
        // walls.
        pos = pos.up();

        // Clear a space around the ladder pillar and make walls. The walls are
        // necessary if there is a lot of lava down here.
        // Make a wall of air then a wall of stone.

        // South wall.
        tempStacks.addAll(
            BuildingMethods.CreateWall(
                world,
                3,
                3,
                facing.rotateY(),
                pos.offset(facing.getOpposite(), 2)
                    .offset(facing.rotateYCCW()),
                Blocks.air,
                blocksToNotAdd));

        tempStacks.addAll(
            BuildingMethods.CreateWall(
                world,
                3,
                3,
                facing.rotateY(),
                pos.offset(facing.getOpposite(), 3)
                    .offset(facing.rotateYCCW()),
                Blocks.stone,
                blocksToNotAdd));

        // East wall.
        tempStacks.addAll(
            BuildingMethods.CreateWall(
                world,
                3,
                4,
                facing,
                pos.offset(facing.getOpposite(), 2)
                    .offset(facing.rotateY()),
                Blocks.air,
                blocksToNotAdd));
        tempStacks.addAll(
            BuildingMethods.CreateWall(
                world,
                3,
                4,
                facing,
                pos.offset(facing.getOpposite(), 2)
                    .offset(facing.rotateY(), 2),
                Blocks.stone,
                blocksToNotAdd));

        // North wall.
        tempStacks.addAll(
            BuildingMethods.CreateWall(
                world,
                3,
                3,
                facing.rotateYCCW(),
                pos.offset(facing)
                    .offset(facing.rotateY()),
                Blocks.air,
                blocksToNotAdd));
        tempStacks.addAll(
            BuildingMethods.CreateWall(
                world,
                3,
                3,
                facing.rotateYCCW(),
                pos.offset(facing, 2)
                    .offset(facing.rotateY()),
                Blocks.stone,
                blocksToNotAdd));

        // West wall.
        tempStacks.addAll(
            BuildingMethods.CreateWall(
                world,
                3,
                4,
                facing.getOpposite(),
                pos.offset(facing)
                    .offset(facing.rotateYCCW()),
                Blocks.air,
                blocksToNotAdd));
        tempStacks.addAll(
            BuildingMethods.CreateWall(
                world,
                3,
                4,
                facing.getOpposite(),
                pos.offset(facing, 1)
                    .offset(facing.rotateYCCW(), 2),
                Blocks.stone,
                blocksToNotAdd));

        // Consolidate the stacks.
        for (ItemStack tempStack : tempStacks) {
            Boolean foundStack = false;

            for (ItemStack existingStack : stacks) {
                if (existingStack.isItemEqual(tempStack)) {
                    // Make sure that this combined stack is at or smaller than
                    // the max.
                    if (existingStack.stackSize + tempStack.stackSize <= tempStack.getMaxStackSize()) {
                        existingStack.stackSize = existingStack.stackSize + tempStack.stackSize;
                        foundStack = true;
                        break;
                    }
                }
            }

            if (!foundStack) {
                stacks.add(tempStack);
            }
        }

        // Place a torch to the left of the ladder.
        BlockState blockState = new BlockState(Blocks.torch, 5);
        BuildingMethods.ReplaceBlock(world, pos.offset(facing.rotateYCCW()), blockState);

        if (!Prefab.proxy.proxyConfiguration.enableHouseGenerationRestrictions) {
            // Place a chest to the right of the ladder.
            BlockState chestState = new BlockState(Blocks.chest, (facing).getIndex());
            BuildingMethods.ReplaceBlock(world, pos.offset(facing.rotateY()), chestState);

            if (stacks.size() > 27) {
                // Add another chest to south of the existing chest.
                BuildingMethods.ReplaceBlock(
                    world,
                    pos.offset(facing.rotateY())
                        .offset(facing.getOpposite()),
                    chestState);
            }

            TileEntity tileEntity = BuildingMethods.getTileEntity(world, pos.offset(facing.rotateY()));
            TileEntity tileEntity2 = BuildingMethods.getTileEntity(
                world,
                pos.offset(facing.rotateY())
                    .offset(facing.getOpposite()));

            if (tileEntity instanceof TileEntityChest) {
                TileEntityChest chestTile = (TileEntityChest) tileEntity;
                TileEntityChest chestTile2 = (TileEntityChest) tileEntity2;

                int i = 0;
                boolean fillSecond = false;

                // All of the stacks should be consolidated at this point.
                for (ItemStack stack : stacks) {
                    if (i == 27 && !fillSecond) {
                        // Start filling the second chest.
                        fillSecond = true;
                        i = 0;
                        chestTile = chestTile2;
                    }

                    if (i >= 27 && fillSecond) {
                        // Too many items, discard the rest.
                        break;
                    } else {
                        chestTile.setInventorySlotContents(i, stack);
                    }

                    i++;
                }
            }
        }
    }

    private static ArrayList<ItemStack> CreateLadderShaft(World world, BlockPos pos,
        ArrayList<ItemStack> originalStacks, EnumFacing houseFacing, ArrayList<Item> blocksToNotAdd) {
        int torchCounter = 0;

        // Keep the "west" facing.
        EnumFacing westWall = houseFacing.rotateYCCW();

        // Get the ladder state based on the house facing.
        BlockState ladderState = new BlockState(Blocks.ladder, (houseFacing).getIndex());

        // Replace the main floor block with air since we don't want it placed in the chest at the end.
        BuildingMethods.ReplaceBlock(world, pos, Blocks.air);
        StructureAlternateStart.torchPositions = new ArrayList<BlockPos>();

        while (pos.getY() > 8) {
            BlockState state = BlockState.get(world, pos);
            Block block = state.getBlock();
            torchCounter++;

            // Make sure all blocks around this one are solid, if they are not
            // replace them with stone.
            for (int i = 0; i < 4; i++) {
                EnumFacing facing = houseFacing;

                switch (i) {
                    case 1: {
                        facing = houseFacing.rotateY();
                        break;
                    }
                    case 2: {
                        facing = houseFacing.getOpposite();
                        break;
                    }
                    case 3: {
                        facing = houseFacing.rotateYCCW();
                        break;
                    }
                    default: {
                        facing = houseFacing;
                    }
                }

                // Every 6 blocks, place a torch on the west wall.
                // If we are close to the bottom, don't place a torch. Do the
                // normal processing.
                if (facing == westWall && torchCounter == 6 && pos.getY() > 14) {
                    // First make sure the blocks around this block are stone, then place the torch.
                    for (int j = 0; j <= 2; j++) {
                        BlockPos tempPos = null;
                        BlockState surroundingState = null;
                        Block surroundingBlock = null;

                        if (j == 0) {
                            tempPos = pos.offset(facing, 2);
                            surroundingState = BlockState.get(world, tempPos);
                            surroundingBlock = surroundingState.getBlock();
                        } else if (j == 1) {
                            tempPos = pos.offset(facing)
                                .offset(facing.rotateY());
                            surroundingState = BlockState.get(world, tempPos);
                            surroundingBlock = surroundingState.getBlock();
                        } else {
                            tempPos = pos.offset(facing)
                                .offset(facing.rotateYCCW());
                            surroundingState = BlockState.get(world, tempPos);
                            surroundingBlock = surroundingState.getBlock();
                        }

                        // Make sure that this is a normal solid block and not a liquid or partial block.
                        if (!(surroundingBlock instanceof BlockStone)) {
                            // This is not a stone block. Get the drops then replace it with stone.
                            originalStacks = BuildingMethods.ConsolidateDrops(
                                surroundingBlock,
                                world,
                                tempPos,
                                surroundingState.getMeta(),
                                originalStacks,
                                blocksToNotAdd);

                            BuildingMethods.ReplaceBlock(world, tempPos, Blocks.stone);
                        }
                    }

                    StructureAlternateStart.torchPositions.add(pos.offset(facing));
                    torchCounter = 0;
                } else {
                    BlockPos tempPos = pos.offset(facing);
                    BlockState surroundingState = BlockState.get(world, tempPos);
                    Block surroundingBlock = surroundingState.getBlock();

                    if (!surroundingBlock.isNormalCube() || surroundingBlock instanceof BlockLiquid) {
                        // This is not a solid block. Get the drops then replace
                        // it with stone.
                        originalStacks = BuildingMethods.ConsolidateDrops(
                            surroundingBlock,
                            world,
                            tempPos,
                            surroundingState.getMeta(),
                            originalStacks,
                            blocksToNotAdd);

                        BuildingMethods.ReplaceBlock(world, tempPos, Blocks.stone);
                    }
                }
            }

            // Get the block drops then replace it with a ladder.
            originalStacks = BuildingMethods
                .ConsolidateDrops(block, world, pos, state.getMeta(), originalStacks, blocksToNotAdd);

            // Don't place a ladder at this location since it will be destroyed.
            if (pos.getY() >= 10) {
                BuildingMethods.ReplaceBlock(world, pos, ladderState);
            }

            pos = pos.down();
        }

        return originalStacks;
    }
}
