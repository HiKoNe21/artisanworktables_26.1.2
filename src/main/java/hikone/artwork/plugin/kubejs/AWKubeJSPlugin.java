package hikone.artwork.plugin.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumType;
import org.jspecify.annotations.NonNull;

public class AWKubeJSPlugin implements KubeJSPlugin
{
    @Override
    public void registerRecipeComponents(@NonNull RecipeComponentTypeRegistry registry)
    {
        registry.unit(AWCodecComponent.SIZED_INGREDIENT);
        registry.unit(AWCodecComponent.CHANCED_RESULT);
        registry.unit(AWCodecComponent.EXPERIENCE);
    }

    @Override
    public void registerRecipeSchemas(@NonNull RecipeSchemaRegistry registry)
    {
        var namespace = registry.namespace(ArtWork.MODID);
        for (var type : AWEnumType.values())
        {
            namespace.register(type.getName() + "_shaped", AWRecipeSchemas.SHAPED);
            namespace.register(type.getName() + "_shapeless", AWRecipeSchemas.SHAPELESS);
        }
    }
}
