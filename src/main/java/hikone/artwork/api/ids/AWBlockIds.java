package hikone.artwork.api.ids;

import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import net.minecraft.resources.ResourceLocation;

public final class AWBlockIds
{
    public static ResourceLocation get(AWEnumType type, AWEnumTier tier)
    {
        return create(tier.getName() + "_" + type.getName());
    }

    private static ResourceLocation create(String path)
    {
        return ArtWork.makeId(path);
    }
}
