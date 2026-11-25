package hikone.artwork.api.ids;

import hikone.artwork.ArtWork;
import net.minecraft.resources.ResourceLocation;

public final class AWBlockEntityIds
{
    public static final ResourceLocation BASIC_WORKTABLE = create("basic_worktable");

    private static ResourceLocation create(String path)
    {
        return ArtWork.makeId(path);
    }
}
