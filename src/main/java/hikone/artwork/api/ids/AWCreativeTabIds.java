package hikone.artwork.api.ids;

import hikone.artwork.ArtWork;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public final class AWCreativeTabIds
{
    public static final ResourceKey<CreativeModeTab> MAIN = create("main");

    private static ResourceKey<CreativeModeTab> create(String path)
    {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, ArtWork.makeId(path));
    }
}
