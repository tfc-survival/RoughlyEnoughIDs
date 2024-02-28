package org.dimdev.jeid;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.IEarlyMixinLoader;
import zone.rong.mixinbooter.ILateMixinLoader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(-7500)
@IFMLLoadingPlugin.Name("JustEnoughIDs Extension Plugin")
//@IFMLLoadingPlugin.TransformerExclusions("org.dimdev.jeid.")
public class JEIDLoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override public String[] getASMTransformerClass() { Obf.loadData(); return new String[]{ "org.dimdev.jeid.JEIDTransformer" }; }
    @Override public String getModContainerClass() { return null; }
    @Nullable @Override public String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> data) {}
    @Override public String getAccessTransformerClass() { return null; }

    public List<String> getMixinConfigs() {
        return ImmutableList.of("mixins.jeid.core.json");
    }
}
