package hikone.artwork.plugin.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumType;

public class AWKubeJSPlugin implements KubeJSPlugin
{
    @Override
    public void registerRecipeComponents(RecipeComponentTypeRegistry registry)
    {
        registry.register(AWCodecComponent.SIZED_INGREDIENT_TYPE);
        registry.register(AWCodecComponent.CHANCED_RESULT_TYPE);
        registry.register(AWCodecComponent.EXPERIENCE_TYPE);
    }

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry)
    {
        var namespace = registry.namespace(ArtWork.MODID);
        for (var type : AWEnumType.values())
        {
            namespace.register(type.getName() + "_shaped", AWRecipeSchemas.SHAPED);
            namespace.register(type.getName() + "_shapeless", AWRecipeSchemas.SHAPELESS);
        }
    }
}
