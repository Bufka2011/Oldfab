package com.wuest.prefab.Proxy.Messages.Handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import com.wuest.prefab.Config.ModConfiguration;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.Proxy.ClientProxy;
import com.wuest.prefab.Proxy.Messages.ConfigSyncMessage;
import com.wuest.prefab.Util.MainThreadTasks;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Handles the configuration sent from the server.
 *
 * @author WuestMan
 */
public class ConfigSyncHandler implements IMessageHandler<ConfigSyncMessage, IMessage> {

    @Override
    public IMessage onMessage(final ConfigSyncMessage message, final MessageContext ctx) {
        MainThreadTasks.addClientTask(new Runnable() {

            @Override
            public void run() {
                // This is client side.
                ((ClientProxy) Prefab.proxy).serverConfiguration = ModConfiguration
                    .getFromNBTTagCompound(message.getMessageTag());
                ModConfiguration config = ((ClientProxy) Prefab.proxy).getServerConfiguration();

                if (config.showMessage && config.enableVersionCheckMessage
                    && Minecraft.getMinecraft().thePlayer != null) {
                    Minecraft.getMinecraft().thePlayer
                        .addChatComponentMessage(new ChatComponentText(config.versionMessage));
                }
            }
        });

        // no response in this case
        return null;
    }
}
