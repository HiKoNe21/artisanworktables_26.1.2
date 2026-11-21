package hikone.artwork.init;

import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.recipe.AWShapedRecipe;
import hikone.artwork.recipe.AWShapelessRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

public final class AWRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> DR = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ArtWork.MODID);

    public static final Map<AWEnumType, DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AWShapedRecipe>>> SHAPED = new EnumMap<>(AWEnumType.class);
    public static final Map<AWEnumType, DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AWShapelessRecipe>>> SHAPELESS = new EnumMap<>(AWEnumType.class);

    static
    {
        for (AWEnumType type : AWEnumType.values())
        {
            var prefix = type.getName();

            SHAPED.put(type, DR.register(
                    prefix + "_shaped",
                    () -> AWShapedRecipe.createSerializer(type)
            ));

            SHAPELESS.put(type, DR.register(
                    prefix + "_shapeless",
                    () -> AWShapelessRecipe.createSerializer(type)
            ));
        }
    }
}
