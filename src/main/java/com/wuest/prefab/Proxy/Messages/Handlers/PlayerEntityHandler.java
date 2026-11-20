package com.wuest.prefab.Proxy.Messages.Handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import com.wuest.prefab.Config.EntityPlayerConfiguration;
import com.wuest.prefab.Events.ClientEventHandler;
import com.wuest.prefab.Proxy.Messages.PlayerEntityTagMessage;
import com.wuest.prefab.Util.MainThreadTasks;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Handles the player entity tag sent from the server.
 *
 * @author WuestMan
 */
public class PlayerEntityHandler implements IMessageHandler<PlayerEntityTagMessage, IMessage> {

    @Override
    public IMessage onMessage(final PlayerEntityTagMessage message, final MessageContext ctx) {
        MainThreadTasks.addClientTask(new Runnable() {

            @Override
            public void run() {
                // This is client side.
                ClientEventHandler.playerConfig.loadFromNBTTagCompound(message.getMessageTag());

                if (Minecraft.getMinecraft().thePlayer != null) {
                    NBTTagCompound newPlayerTag = Minecraft.getMinecraft().thePlayer.getEntityData();
                    newPlayerTag.setTag(EntityPlayerConfiguration.PLAYER_ENTITY_TAG, message.getMessageTag());
                }
            }
        });

        // no response in this case
        return null;
    }
}
