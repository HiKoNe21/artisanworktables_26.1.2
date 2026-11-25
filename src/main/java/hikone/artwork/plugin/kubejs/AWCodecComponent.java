package hikone.artwork.plugin.kubejs;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.util.JsonUtils;
import dev.latvian.mods.rhino.type.TypeInfo;
import hikone.artwork.ArtWork;
import hikone.artwork.recipe.format.AWExperienceIngredient;
import hikone.artwork.recipe.format.AWItemStackTemplate;
import hikone.artwork.recipe.format.AWSizedIngredient;

public record AWCodecComponent<T>(
        RecipeComponentType<?> type,
        Codec<T> codec,
        TypeInfo typeInfo
) implements RecipeComponent<T>
{
    public static final RecipeComponentType<AWSizedIngredient> SIZED_INGREDIENT_TYPE = RecipeComponentType.unit(
            ArtWork.makeId("sized_ingredient"),
            type -> new AWCodecComponent<>(type, AWSizedIngredient.FLAT_CODEC, TypeInfo.of(AWSizedIngredient.class))
    );

    public static final RecipeComponentType<AWItemStackTemplate> CHANCED_RESULT_TYPE = RecipeComponentType.unit(
            ArtWork.makeId("chanced_result"),
            type -> new AWCodecComponent<>(type, AWItemStackTemplate.CODEC, TypeInfo.of(AWItemStackTemplate.class))
    );

    public static final RecipeComponentType<AWExperienceIngredient> EXPERIENCE_TYPE = RecipeComponentType.unit(
            ArtWork.makeId("experience"),
            type -> new AWCodecComponent<>(type, AWExperienceIngredient.CODEC, TypeInfo.of(AWExperienceIngredient.class))
    );

    public static final RecipeComponent<AWSizedIngredient> SIZED_INGREDIENT = SIZED_INGREDIENT_TYPE.instance();
    public static final RecipeComponent<AWItemStackTemplate> CHANCED_RESULT = CHANCED_RESULT_TYPE.instance();
    public static final RecipeComponent<AWExperienceIngredient> EXPERIENCE = EXPERIENCE_TYPE.instance();

    @SuppressWarnings("unchecked")
    @Override
    public T wrap(RecipeScriptContext cx, Object from)
    {
        if (this.typeInfo.asClass().isInstance(from))
        {
            return (T) from;
        }

        var json = JsonUtils.objectOf(cx.cx(), from);
        if (json != null)
        {
            return this.codec.parse(cx.registries().json(), json).getOrThrow();
        }

        return RecipeComponent.super.wrap(cx, from);
    }
}