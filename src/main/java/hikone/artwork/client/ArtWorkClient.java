package hikone.artwork.client;

import hikone.artwork.ArtWork;
import hikone.artwork.ArtWorkCommon;
import hikone.artwork.config.AWClientConfig;
import hikone.artwork.init.AWScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ArtWork.MODID, dist = Dist.CLIENT)
public class ArtWorkClient extends ArtWorkCommon
{
    public ArtWorkClient(IEventBus modEventBus, ModContainer modContainer)
    {
        super(modEventBus, modContainer);

        modContainer.registerConfig(ModConfig.Type.CLIENT, AWClientConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modEventBus.addListener(AWScreens::init);
    }
}
