package hikone.artwork;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public interface ArtWork
{
    public static final String MODID = "artwork";
    public static final Logger LOGGER = LogUtils.getLogger();

    static ArtWork instance()
    {
        return ArtWorkCommon.INSTANCE;
    }

    static ResourceLocation makeId(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @Nullable
    RecipeManager getRecipeManager();
}
