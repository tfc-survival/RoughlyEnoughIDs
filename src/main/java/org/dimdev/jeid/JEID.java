package org.dimdev.jeid;

import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.jeid.biome.BiomeError;
import org.dimdev.jeid.core.JEIDTransformer;
import org.dimdev.jeid.debug.RegistryDebug;
import org.dimdev.jeid.jeid.Tags;
import org.dimdev.jeid.network.MessageManager;

@Mod(modid = JEID.MODID,
     name = JEID.NAME,
     version = JEID.VERSION,
     dependencies = "required:mixinbooter@[7.1,)")
public class JEID {
    public static final String MODID = Tags.MOD_ID;
    public static final String NAME = Tags.MOD_NAME;
    public static final String VERSION = Tags.VERSION;
    public static final Logger LOGGER = LogManager.getLogger(JEID.NAME);

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        // Register messages
        MessageManager.init();
        // Register Error Biome
        ForgeRegistries.BIOMES.register(BiomeError.getInstance());
        // Debug code
        MinecraftForge.EVENT_BUS.register(new RegistryDebug());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        JEIDTransformer.REGISTRY = net.minecraftforge.registries.GameData.getWrapper(Potion.class);
    }
}