package com.wuest.prefab.Proxy.Messages.Handlers;

import net.minecraft.entity.player.EntityPlayerMP;

import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.Proxy.Messages.StructureTagMessage;
import com.wuest.prefab.Proxy.Messages.StructureTagMessage.EnumStructureConfiguration;
import com.wuest.prefab.Util.MainThreadTasks;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Handles the structure build request sent from the client.
 *
 * @author WuestMan
 */
public class StructureHandler implements IMessageHandler<StructureTagMessage, IMessage> {

    @Override
    public IMessage onMessage(final StructureTagMessage message, final MessageContext ctx) {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;

        MainThreadTasks.addServerTask(new Runnable() {

            @Override
            public void run() {
                // This is server side. Build the structure.
                EnumStructureConfiguration structureConfig = message.getStructureConfig();
                StructureConfiguration configuration = structureConfig.structureConfig
                    .ReadFromNBTTagCompound(message.getMessageTag());
                configuration.BuildStructure(player, player.worldObj);
            }
        });

        // no response in this case
        return null;
    }
}
