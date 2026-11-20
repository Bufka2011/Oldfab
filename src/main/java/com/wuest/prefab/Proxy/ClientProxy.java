package com.wuest.prefab.Proxy;

import net.minecraftforge.common.MinecraftForge;

import com.wuest.prefab.Blocks.BlockPaperLantern;
import com.wuest.prefab.Config.ModConfiguration;
import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.Events.ClientEventHandler;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Render.PaperLanternRenderer;
import com.wuest.prefab.Render.StructureRenderHandler;
import com.wuest.prefab.StructureGen.Structure;
import com.wuest.prefab.Util.EnumFacing;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * The client side proxy.
 *
 * @author WuestMan
 */
public class ClientProxy extends CommonProxy {

    public ModConfiguration serverConfiguration = null;

    public static ClientEventHandler clientEventHandler = new ClientEventHandler();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ModRegistry.AddGuis();

        // After all items have been registered and all recipes loaded, register any necessary renderer.
        this.registerRenderers();

        MinecraftForge.EVENT_BUS.register(ClientProxy.clientEventHandler);
        FMLCommonHandler.instance()
            .bus()
            .register(ClientProxy.clientEventHandler);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postinit(FMLPostInitializationEvent event) {
        super.postinit(event);
    }

    @Override
    public void registerRenderers() {
        PaperLanternRenderer lanternRenderer = new PaperLanternRenderer();
        BlockPaperLantern.renderType = lanternRenderer.getRenderId();
        RenderingRegistry.registerBlockHandler(lanternRenderer);
    }

    @Override
    public ModConfiguration getServerConfiguration() {
        if (this.serverConfiguration == null) {
            // Get the server configuration.
            return CommonProxy.proxyConfiguration;
        } else {
            return this.serverConfiguration;
        }
    }

    @Override
    public void setPreviewStructure(Structure structure, EnumFacing assumedNorth, StructureConfiguration configuration,
        boolean showedMessage) {
        StructureRenderHandler.setStructure(structure, assumedNorth, configuration);
        StructureRenderHandler.showedMessage = showedMessage;
    }

    @Override
    public boolean isPreviewStructure(Structure structure) {
        return structure != null && StructureRenderHandler.currentStructure == structure;
    }
}
