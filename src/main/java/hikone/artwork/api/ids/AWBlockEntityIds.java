package hikone.artwork.api.ids;

import hikone.artwork.ArtWork;
import net.minecraft.resources.Identifier;

public final class AWBlockEntityIds
{
    public static final Identifier BASIC_WORKTABLE = create("basic_worktable");

    private static Identifier create(String path)
    {
        return ArtWork.makeId(path);
    }
}
