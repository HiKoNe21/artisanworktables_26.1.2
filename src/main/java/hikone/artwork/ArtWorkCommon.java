package hikone.artwork;

import hikone.artwork.config.AWServerConfig;
import hikone.artwork.datagen.DataGenerator;
import hikone.artwork.init.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jspecify.annotations.Nullable;

public abstract class ArtWorkCommon implements ArtWork
{
    static ArtWorkCommon INSTANCE;

    public ArtWorkCommon(IEventBus modEventBus, ModContainer modContainer)
    {
        if (INSTANCE != null)
        {
            throw new IllegalStateException();
        }
        INSTANCE = this;

        ShapedRecipePattern.setCraftingSize(5, 5);

        modContainer.registerConfig(ModConfig.Type.SERVER, AWServerConfig.SPEC);

        AWBlocks.DR.register(modEventBus);
        AWItems.DR.register(modEventBus);
        AWBlockEntities.DR.register(modEventBus);
        AWMenuTypes.DR.register(modEventBus);
        AWRecipeTypes.DR.register(modEventBus);
        AWRecipeSerializers.DR.register(modEventBus);

        modEventBus.addListener(DataGenerator::gatherClientData);
        modEventBus.addListener(DataGenerator::gatherServerData);

        modEventBus.addListener(AWCreativeTabs::initExternal);
        modEventBus.addListener(AWCapabilityProviders::register);
        modEventBus.addListener(AWNetworks::register);

        modEventBus.addListener((RegisterEvent event) ->
        {
            if (event.getRegistryKey() == Registries.CREATIVE_MODE_TAB)
            {
                registerCreativeTabs(BuiltInRegistries.CREATIVE_MODE_TAB);
            }
        });
    }

    public void registerCreativeTabs(Registry<CreativeModeTab> registry)
    {
        AWCreativeTabs.init(registry);
    }

    @Override
    public @Nullable RecipeManager getRecipeManager()
    {
        var minecraftServer = ServerLifecycleHooks.getCurrentServer();

        if (minecraftServer != null)
        {
            return minecraftServer.getRecipeManager();
        }

        return null;
    }
}
