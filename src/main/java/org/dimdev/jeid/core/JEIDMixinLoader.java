package org.dimdev.jeid.core;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;


public class JEIDMixinLoader implements ILateMixinLoader {
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();

        if (JEIDLoadingPlugin.isClient) {
            if (Loader.isModLoaded("advancedrocketry")) {
                configs.add("mixins.jeid.advancedrocketry.client.json");
            }
        }
        if (Loader.isModLoaded("abyssalcraft")) {
            configs.add("mixins.jeid.abyssalcraft.json");
        }
        if (Loader.isModLoaded("advancedrocketry")) {
            String version = Loader.instance().getIndexedModList().get("advancedrocketry").getVersion();
            if (version.split("-")[1].contains(".") && Integer.parseInt(version.split("-")[1].split("\\.")[0]) >= 2) {
                configs.add("mixins.jeid.advancedrocketry.v2_0_0.json");
            } else {
                configs.add("mixins.jeid.advancedrocketry.v1_7_0.json");
            }
        }
        if (Loader.isModLoaded("atum")) {
            configs.add("mixins.jeid.atum.json");
        }
        if (Loader.isModLoaded("biomesoplenty")) {
            configs.add("mixins.jeid.biomesoplenty.json");
        }
        if (Loader.isModLoaded("biomestaff")) {
            configs.add("mixins.jeid.biomestaff.json");
        }
        if (Loader.isModLoaded("biometweaker")) {
            configs.add("mixins.jeid.biometweaker.json");
        }
        if (Loader.isModLoaded("bookshelf")) {
            configs.add("mixins.jeid.bookshelf.json");
        }
        if (Loader.isModLoaded("compactmachines3")) {
            configs.add("mixins.jeid.compactmachines.json");
        }
        if (Loader.isModLoaded("creepingnether")) {
            configs.add("mixins.jeid.creepingnether.json");
        }
        if (Loader.isModLoaded("cubicchunks")) {
            configs.add("mixins.jeid.cubicchunks.json");
        }
        if (Loader.isModLoaded("cyclopscore")) {
            configs.add("mixins.jeid.cyclopscore.json");
        }
        if (Loader.isModLoaded("extrautils2")) {
            configs.add("mixins.jeid.extrautils2.json");
        }
        if (Loader.isModLoaded("gaiadimension")) {
            configs.add("mixins.jeid.gaiadimension.json");
        }
        if (Loader.isModLoaded("geographicraft")) {
            configs.add("mixins.jeid.geographicraft.json");
        }
        if (Loader.isModLoaded("hammercore")) {
            configs.add("mixins.jeid.hammercore.json");
        }
        if (Loader.isModLoaded("journeymap")) {
            configs.add("mixins.jeid.journeymap.json");
        }
        if (Loader.isModLoaded("kathairis")) {
            configs.add("mixins.jeid.kathairis.json");
        }
        if (Loader.isModLoaded("moreplanets")) {
            configs.add("mixins.jeid.moreplanets.json");
        }
        if (Loader.isModLoaded("mystcraft")) {
            configs.add("mixins.jeid.mystcraft.json");
        }
        if (Loader.isModLoaded("naturescompass")) {
            configs.add("mixins.jeid.naturescompass.json");
        }
        if (Loader.isModLoaded("rtg")) {
            configs.add("mixins.jeid.rtg.json");
        }
        if (Loader.isModLoaded("srparasites")) {
            configs.add("mixins.jeid.srparasites.json");
        }
        if (Loader.isModLoaded("thaumcraft")) {
            configs.add("mixins.jeid.thaumcraft.json");
        }
        if (Loader.isModLoaded("thebetweenlands")) {
            configs.add("mixins.jeid.thebetweenlands.json");
        }
        if (Loader.isModLoaded("tofucraft")) {
            configs.add("mixins.jeid.tofucraft.json");
        }
        if (Loader.isModLoaded("tropicraft")) {
            configs.add("mixins.jeid.tropicraft.json");
        }
        if (Loader.isModLoaded("twilightforest")) {
            configs.add("mixins.jeid.twilightforest.json");
        }
        if (Loader.isModLoaded("worldedit")) {
            configs.add("mixins.jeid.worldedit.json");
        }

        // Checks if the mod is within the version that has the legacy Biome Spread code
        if (Loader.isModLoaded("wyrmsofnyrus"))
            //TODO: Doesn't work since this code runs before anything is actually loaded, so we'll need to find another way before v0.6 comes out.
                // Loader.instance().getCustomModProperties("wyrmsofnyrus").get("version").matches("0.5.[1-9][0-9]{1,3}") )
            // this checks any version between v0.5.10 (introduced the system) and v0.5.9999 (Impossible number of versions for a LTS version, but let's futureproof it to be safe.)
            configs.add("mixins.jeid.wyrmsofnyrus.json");


        return configs;
    }
}
