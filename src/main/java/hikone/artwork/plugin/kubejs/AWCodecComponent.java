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
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NonNull;

public record AWCodecComponent<T>(ResourceKey<RecipeComponentType<?>> type, Codec<T> codec,
                                  TypeInfo typeInfo) implements RecipeComponent<T>
{
    public static final AWCodecComponent<AWSizedIngredient> SIZED_INGREDIENT = create(
            "sized_ingredient", AWSizedIngredient.CODEC, AWSizedIngredient.class);

    public static final AWCodecComponent<AWItemStackTemplate> CHANCED_RESULT = create(
            "chanced_result", AWItemStackTemplate.CODEC, AWItemStackTemplate.class);

    public static final AWCodecComponent<AWExperienceIngredient> EXPERIENCE = create(
            "experience", AWExperienceIngredient.CODEC, AWExperienceIngredient.class);

    public static <T> AWCodecComponent<T> create(String name, Codec<T> codec, Class<T> clazz)
    {
        var key = ResourceKey.create(RecipeComponentType.REGISTRY_KEY, ArtWork.makeId(name));
        return new AWCodecComponent<>(key, codec, TypeInfo.of(clazz));
    }

    @Override
    public @NonNull ResourceKey<RecipeComponentType<?>> type()
    {
        return this.type;
    }

    @Override
    public @NonNull Codec<T> codec()
    {
        return this.codec;
    }

    @Override
    public @NonNull TypeInfo typeInfo()
    {
        return this.typeInfo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull T wrap(@NonNull RecipeScriptContext cx, Object from)
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