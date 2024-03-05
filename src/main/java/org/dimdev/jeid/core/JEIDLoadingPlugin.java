package org.dimdev.jeid.core;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.Name("JustEnoughIDs Extension Plugin")
//@IFMLLoadingPlugin.TransformerExclusions("org.dimdev.jeid.")
public class JEIDLoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public static final boolean isClient = FMLLaunchHandler.side().isClient();
    public static final boolean isDeobf = FMLLaunchHandler.isDeobfuscatedEnvironment();

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
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

    // Guarantees deobf-SRG names are present before transformer loads
    @Override
    public String getAccessTransformerClass() {
        return "org.dimdev.jeid.core.JEIDTransformer";
    }

    @Override
    public List<String> getMixinConfigs() {
        return ImmutableList.of("mixins.jeid.core.json");
    }
}
