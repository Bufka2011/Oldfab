package com.wuest.prefab.Events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.wuest.prefab.Config.EntityPlayerConfiguration;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Proxy.ClientProxy;
import com.wuest.prefab.Render.StructureRenderHandler;
import com.wuest.prefab.Util.EnumFacing;
import com.wuest.prefab.Util.MainThreadTasks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The client side event handler.
 *
 * @author WuestMan
 */
@SideOnly(Side.CLIENT)
public class ClientEventHandler {

    /**
     * Determines how long a shader has been running.
     */
    public static int ticksInGame;

    public static EntityPlayerConfiguration playerConfig = new EntityPlayerConfiguration();

    /**
     * The world render last event. This is used for structure rendering.
     *
     * @param event The event object.
     */
    @SubscribeEvent
    public void onWorldRenderLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer != null && !mc.thePlayer.isSneaking()) {
            StructureRenderHandler.renderPlayerLook(mc.thePlayer, event.partialTicks);
        }
    }

    /**
     * Cancels the right click when a structure is previewed so the preview can be removed.
     *
     * @param event The event object.
     */
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.world.isRemote
            && StructureRenderHandler.currentStructure != null
            && event.entityPlayer == Minecraft.getMinecraft().thePlayer) {
            StructureRenderHandler.setStructure(null, EnumFacing.NORTH, null);
            event.setCanceled(true);
        }
    }

    /**
     * This is used to clear out the server configuration on the client side.
     *
     * @param event The event object.
     */
    @SubscribeEvent
    public void OnClientDisconnectEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        // When the player logs out, make sure to re-set the server configuration.
        // This is so a new configuration can be successfully loaded when they switch servers or worlds (on single
        // player.
        ((ClientProxy) Prefab.proxy).serverConfiguration = null;
        ClientEventHandler.playerConfig.clearNonPersistedObjects();
    }

    /**
     * This is used to increment the ticks in game value.
     *
     * @param event The event object.
     */
    @SubscribeEvent
    public void ClientTickEnd(ClientTickEvent event) {
        if (event.phase == Phase.START) {
            MainThreadTasks.runClientTasks();
            return;
        }

        GuiScreen gui = Minecraft.getMinecraft().currentScreen;

        if (gui == null || !gui.doesGuiPauseGame()) {
            // Reset the ticks in game if we are getting close to the maximum value of an integer.
            if (Integer.MAX_VALUE - 100 == ClientEventHandler.ticksInGame) {
                ClientEventHandler.ticksInGame = 1;
            }

            ClientEventHandler.ticksInGame++;
        }
    }
}
