package org.dimdev.jeid.core;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.dimdev.jeid.core.Obf;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(-7500)
@IFMLLoadingPlugin.Name("JustEnoughIDs Extension Plugin")
//@IFMLLoadingPlugin.TransformerExclusions("org.dimdev.jeid.")
public class JEIDLoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public static final boolean isDeobf = FMLLaunchHandler.isDeobfuscatedEnvironment();

    @Override
    public String[] getASMTransformerClass() {
        Obf.loadData();
        return null;//new String[]{"org.dimdev.jeid.core.JEIDTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return "org.dimdev.jeid.core.JEIDTransformer";
    }

    @Override
    public List<String> getMixinConfigs() {
        return ImmutableList.of("mixins.jeid.core.json");
    }
}
