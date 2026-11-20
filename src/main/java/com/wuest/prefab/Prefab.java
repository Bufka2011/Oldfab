package com.wuest.prefab;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wuest.prefab.Proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * The starting point to load all of the blocks, items and other objects associated with this mod.
 *
 * @author WuestMan
 */
@Mod(
    modid = Prefab.MODID,
    name = "Prefab",
    version = Prefab.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    guiFactory = "com.wuest.prefab.Gui.ConfigGuiFactory")
public class Prefab {

    /**
     * This is the ModID
     */
    public static final String MODID = "prefab";

    /**
     * This is the current mod version.
     */
    public static final String VERSION = Tags.VERSION;

    /**
     * The logger for this mod.
     */
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    /**
     * This is used to determine if the mod is currently being debugged.
     */
    public static boolean isDebug = false;

    /**
     * This is the static instance of this class.
     */
    @Instance(value = Prefab.MODID)
    public static Prefab instance;

    /**
     * Says where the client and server 'proxy' code is loaded.
     */
    @SidedProxy(clientSide = "com.wuest.prefab.Proxy.ClientProxy", serverSide = "com.wuest.prefab.Proxy.CommonProxy")
    public static CommonProxy proxy;

    /**
     * The network class used to send messages.
     */
    public static SimpleNetworkWrapper network;

    /**
     * This is the configuration of the mod.
     */
    public static Configuration config;

    static {
        Prefab.isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean()
            .getInputArguments()
            .toString()
            .contains("-agentlib:jdwp");
    }

    /**
     * The pre-initialization event.
     *
     * @param event The event from forge.
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Prefab.proxy.preInit(event);
    }

    /**
     * The initialization event.
     *
     * @param event The event from forge.
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        Prefab.proxy.init(event);
    }

    /**
     * The post-initialization event.
     *
     * @param event The event from forge.
     */
    @EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        Prefab.proxy.postinit(event);
    }

    /**
     * Re-maps the old (camel case) names used in very old versions of the mod.
     *
     * @param event The event from forge.
     */
    @EventHandler
    public void OnMissingMapping(FMLMissingMappingsEvent event) {
        List<MissingMapping> missingMappings = event.get();

        for (MissingMapping mapping : missingMappings) {
            String path = mapping.name.substring(mapping.name.indexOf(':') + 1)
                .toLowerCase();

            if (mapping.type == GameRegistry.Type.BLOCK) {
                if (path.equals("blockcompressedstone")) {
                    mapping.remap((Block) ModRegistry.CompressedStoneBlock());
                }
            } else {
                Item mappedItem = null;

                switch (path) {
                    case "blockcompressedstone":
                        mappedItem = Item.getItemFromBlock(ModRegistry.CompressedStoneBlock());
                        break;
                    case "itemproducefarm":
                        mappedItem = ModRegistry.ProduceFarm();
                        break;
                    case "itempileofbricks":
                        mappedItem = ModRegistry.PileOfBricks();
                        break;
                    case "itemhorsestable":
                        mappedItem = ModRegistry.HorseStable();
                        break;
                    case "itemnethergate":
                        mappedItem = ModRegistry.NetherGate();
                        break;
                    case "itemwarehouseupgrade":
                        mappedItem = ModRegistry.WareHouseUpgrade();
                        break;
                    case "itemchickencoop":
                        mappedItem = ModRegistry.ChickenCoop();
                        break;
                    case "itemtreefarm":
                        mappedItem = ModRegistry.TreeFarm();
                        break;
                    case "itemcompressedchest":
                        mappedItem = ModRegistry.CompressedChestItem();
                        break;
                    case "itembundleoftimber":
                        mappedItem = ModRegistry.BundleOfTimber();
                        break;
                    case "itemwarehouse":
                        mappedItem = ModRegistry.WareHouse();
                        break;
                    case "itempalletofbricks":
                        mappedItem = ModRegistry.PalletOfBricks();
                        break;
                    case "itemfishpond":
                        mappedItem = ModRegistry.FishPond();
                        break;
                    case "itemmonstermasher":
                        mappedItem = ModRegistry.MonsterMasher();
                        break;
                    case "itemstarthouse":
                        mappedItem = ModRegistry.StartHouse();
                        break;
                    case "itemadvancedwarehouse":
                        mappedItem = ModRegistry.AdvancedWareHouse();
                        break;
                }

                if (mappedItem != null) {
                    mapping.remap(mappedItem);
                }
            }
        }
    }
}
