package hikone.artwork.init;

import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.recipe.AWRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

public final class AWRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> DR = DeferredRegister.create(
            Registries.RECIPE_TYPE,
            ArtWork.MODID);

    public static final Map<AWEnumType, DeferredHolder<RecipeType<?>, RecipeType<AWRecipe>>> TYPES = new EnumMap<>(AWEnumType.class);

    static
    {
        for (AWEnumType type : AWEnumType.values())
        {
            var name = type.getName();
            DeferredHolder<RecipeType<?>, RecipeType<AWRecipe>> holder = DR.register(name, () -> RecipeType.simple(ArtWork.makeId(name)));
            TYPES.put(type, holder);
        }
    }

    public static RecipeType<AWRecipe> get(AWEnumType type)
    {
        return TYPES.get(type).get();
    }
}
