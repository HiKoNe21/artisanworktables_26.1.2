package hikone.artwork.server;

import hikone.artwork.ArtWork;
import hikone.artwork.ArtWorkCommon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = ArtWork.MODID, dist = Dist.DEDICATED_SERVER)
public class ArtWorkServer extends ArtWorkCommon
{
    public ArtWorkServer(IEventBus modEventBus, ModContainer modContainer)
    {
        super(modEventBus, modContainer);
    }
}
