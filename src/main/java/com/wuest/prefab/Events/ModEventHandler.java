package com.wuest.prefab.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.wuest.prefab.BuildingMethods;
import com.wuest.prefab.Config.EntityPlayerConfiguration;
import com.wuest.prefab.Config.ModConfiguration;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Proxy.Messages.ConfigSyncMessage;
import com.wuest.prefab.Proxy.Messages.PlayerEntityTagMessage;
import com.wuest.prefab.StructureGen.BuildBlock;
import com.wuest.prefab.StructureGen.Structure;
import com.wuest.prefab.Util.BlockPos;
import com.wuest.prefab.Util.MainThreadTasks;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

/**
 * This is the server side event hander.
 *
 * @author WuestMan
 */
public class ModEventHandler {

    /**
     * Determines the affected blocks by redstone power.
     */
    public static ArrayList<BlockPos> RedstoneAffectedBlockPositions = new ArrayList<>();

    /**
     * Contains a hashmap for the structures to build and for whom.
     */
    public static HashMap<EntityPlayer, ArrayList<Structure>> structuresToBuild = new HashMap<>();

    /**
     * This event is used to determine if the player should be given the starting house item when they log in.
     *
     * @param event The event object.
     */
    @SubscribeEvent
    public void PlayerJoinedWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.entity;
            EntityPlayerConfiguration playerConfig = EntityPlayerConfiguration.loadFromEntityData(player);

            if (!playerConfig.givenHouseBuilder && Prefab.proxy.proxyConfiguration.addHouseItem) {
                ItemStack stack = new ItemStack(ModRegistry.StartHouse());
                player.inventory.addItemStackToInventory(stack);
                player.inventoryContainer.detectAndSendChanges();

                // Make sure to set the tag for this player so they don't get the item again.
                playerConfig.givenHouseBuilder = true;
                playerConfig.saveToPlayer(player);
            }

            // Send the tag to the client.
            Prefab.network.sendTo(new PlayerEntityTagMessage(playerConfig.getModIsPlayerNewTag(player)), player);
        }
    }

    /**
     * This occurs when a player dies and is about to respawn.
     *
     * @param event The event object.
     */
    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        if (event.entityPlayer instanceof EntityPlayerMP) {
            // Don't add the tag unless the house item was added. This way it can be added if the feature is turned on.
            // When the player is cloned, make sure to copy the tag. If this is not done the item can be given to the
            // player again if they die before the log out and log back in.
            NBTTagCompound originalTag = event.original.getEntityData();

            // Use the server configuration to determine if the house should be added for this player.
            if (Prefab.proxy.proxyConfiguration.addHouseItem
                && originalTag.hasKey(EntityPlayerConfiguration.PLAYER_ENTITY_TAG)) {
                NBTTagCompound newPlayerTag = event.entityPlayer.getEntityData();
                newPlayerTag.setTag(
                    EntityPlayerConfiguration.PLAYER_ENTITY_TAG,
                    originalTag.getTag(EntityPlayerConfiguration.PLAYER_ENTITY_TAG));

                // Send the persist tag to the client.
                Prefab.network.sendTo(
                    new PlayerEntityTagMessage(originalTag.getCompoundTag(EntityPlayerConfiguration.PLAYER_ENTITY_TAG)),
                    (EntityPlayerMP) event.entityPlayer);
            }
        }
    }

    /**
     * This event is called by each world tick on the server and is used to place the structure blocks.
     *
     * @param event The event object.
     */
    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        MainThreadTasks.runServerTasks();

        ArrayList<EntityPlayer> playersToRemove = new ArrayList<>();

        for (Entry<EntityPlayer, ArrayList<Structure>> entry : ModEventHandler.structuresToBuild.entrySet()) {
            ArrayList<Structure> structuresToRemove = new ArrayList<>();

            // Build the first 100 blocks of each structure for this player.
            for (Structure structure : entry.getValue()) {
                for (int i = 0; i < 100; i++) {
                    // Structures are built in this order: clear space, priority one, two and three.
                    if (structure.clearedBlockPos.size() > 0) {
                        BlockPos currentPos = structure.clearedBlockPos.get(0);
                        structure.clearedBlockPos.remove(0);

                        if (currentPos.getY() < 0 || currentPos.getY() >= structure.world.getHeight()) {
                            continue;
                        }

                        if (structure.world.getBlock(currentPos.getX(), currentPos.getY(), currentPos.getZ())
                            != Blocks.air) {
                            BuildingMethods.setBlockToAir(structure.world, currentPos);
                        } else {
                            // This is just an air block, move onto the next block don't need to wait for the next
                            // tick.
                            i--;
                        }

                        continue;
                    }

                    BuildBlock currentBlock = null;

                    if (structure.priorityOneBlocks.size() > 0) {
                        currentBlock = structure.priorityOneBlocks.get(0);
                        structure.priorityOneBlocks.remove(0);
                    } else if (structure.priorityTwoBlocks.size() > 0) {
                        currentBlock = structure.priorityTwoBlocks.get(0);
                        structure.priorityTwoBlocks.remove(0);
                    } else if (structure.priorityThreeBlocks.size() > 0) {
                        currentBlock = structure.priorityThreeBlocks.get(0);
                        structure.priorityThreeBlocks.remove(0);
                    } else {
                        // There are no more blocks to set.
                        structuresToRemove.add(structure);
                        break;
                    }

                    BlockPos pos = currentBlock.getStartingPosition()
                        .getRelativePosition(structure.originalPos, structure.configuration.houseFacing);

                    if (pos.getY() < 0 || pos.getY() >= structure.world.getHeight()) {
                        continue;
                    }

                    // Use flag 2 so neighbor updates don't break blocks which are not supported yet.
                    BuildingMethods.ReplaceBlock(structure.world, pos, currentBlock.getBlockState(), 2);

                    // After placing the initial block, set the sub-block. This needs to happen as the list isn't
                    // always in the correct order.
                    if (currentBlock.getSubBlock() != null && currentBlock.getSubBlock()
                        .getBlockState() != null) {
                        BuildBlock subBlock = currentBlock.getSubBlock();
                        BuildingMethods.ReplaceBlock(
                            structure.world,
                            subBlock.getStartingPosition()
                                .getRelativePosition(structure.originalPos, structure.configuration.houseFacing),
                            subBlock.getBlockState(),
                            2);
                    }
                }
            }

            for (Structure structure : structuresToRemove) {
                // This structure is done building. Do any post-building operations.
                structure.AfterBuilding(
                    structure.configuration,
                    structure.world,
                    structure.originalPos,
                    structure.assumedNorth,
                    entry.getKey());

                entry.getValue()
                    .remove(structure);
            }

            if (entry.getValue()
                .size() == 0) {
                playersToRemove.add(entry.getKey());
            }
        }

        // Remove each player that has their structure's built.
        for (EntityPlayer player : playersToRemove) {
            ModEventHandler.structuresToBuild.remove(player);
        }
    }

    /**
     * This occurs when a player logs in. This is used to send server configuration to the client.
     *
     * @param event The event object.
     */
    @SubscribeEvent
    public void onPlayerLoginEvent(PlayerLoggedInEvent event) {
        if (!event.player.worldObj.isRemote) {
            NBTTagCompound tag = Prefab.proxy.proxyConfiguration.ToNBTTagCompound();
            Prefab.network.sendTo(new ConfigSyncMessage(tag), (EntityPlayerMP) event.player);
            Prefab.LOGGER.info("Sent config to '" + event.player.getCommandSenderName() + ".'");
        }
    }

    /**
     * This is used to sync up the configuration when it's change by the user.
     *
     * @param onConfigChangedEvent The event object.
     */
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent) {
        if (onConfigChangedEvent.modID.equals(Prefab.MODID)) {
            ModConfiguration.syncConfig();
        }
    }
}
