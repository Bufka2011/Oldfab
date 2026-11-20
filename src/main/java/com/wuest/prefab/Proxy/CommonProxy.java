package com.wuest.prefab.Proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.wuest.prefab.Config.ModConfiguration;
import com.wuest.prefab.Config.Structures.StructureConfiguration;
import com.wuest.prefab.Events.ModEventHandler;
import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.Prefab;
import com.wuest.prefab.StructureGen.Structure;
import com.wuest.prefab.Util.EnumFacing;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

/**
 * This is the server side proxy.
 *
 * @author WuestMan
 */
public class CommonProxy implements IGuiHandler {

    public static ModEventHandler eventHandler = new ModEventHandler();
    public static ModConfiguration proxyConfiguration;

    /*
     * Methods for ClientProxy to Override
     */
    public void registerRenderers() {}

    public void preInit(FMLPreInitializationEvent event) {
        Prefab.network = NetworkRegistry.INSTANCE.newSimpleChannel("PrefabChannel");
        Prefab.config = new Configuration(event.getSuggestedConfigurationFile());
        Prefab.config.load();
        ModConfiguration.syncConfig();

        // Register the blocks and items.
        ModRegistry.RegisterWithGameRegistry();

        // Register messages.
        ModRegistry.RegisterMessages();

        // Register the recipes here.
        ModRegistry.RegisterRecipes();

        // Make sure that the mod configuration is re-synced after loading all of the recipes.
        ModConfiguration.syncConfig();

        MinecraftForge.EVENT_BUS.register(CommonProxy.eventHandler);
        FMLCommonHandler.instance()
            .bus()
            .register(CommonProxy.eventHandler);
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Prefab.instance, Prefab.proxy);
    }

    public void postinit(FMLPostInitializationEvent event) {}

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // All of the GUIs in this mod are client side only.
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return ModRegistry.GetModGuiByID(ID, x, y, z);
    }

    public ModConfiguration getServerConfiguration() {
        return CommonProxy.proxyConfiguration;
    }

    /**
     * Sets the structure which is previewed in the world. This only does something on the client.
     */
    public void setPreviewStructure(Structure structure, EnumFacing assumedNorth, StructureConfiguration configuration,
        boolean showedMessage) {}

    /**
     * Determines if the structure is the structure currently being previewed. This only does something on the client.
     */
    public boolean isPreviewStructure(Structure structure) {
        return false;
    }
}
