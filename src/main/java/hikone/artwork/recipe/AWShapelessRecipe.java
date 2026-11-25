package hikone.artwork.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.init.AWRecipeSerializers;
import hikone.artwork.init.AWRecipeTypes;
import hikone.artwork.recipe.format.AWExperienceIngredient;
import hikone.artwork.recipe.format.AWItemStackTemplate;
import hikone.artwork.recipe.format.AWSizedIngredient;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class AWShapelessRecipe extends AWRecipe
{
    // FIELDS ##########################################################################################################

    protected final NonNullList<Ingredient> ingredients;
    protected final boolean isSimple;

    // CONSTRUCTORS ####################################################################################################

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public AWShapelessRecipe(
            AWEnumType worktableType, ItemStack result, List<Ingredient> ingredients, List<AWSizedIngredient> secondary,
            Optional<SizedFluidIngredient> fluid, List<AWItemStackTemplate> extra,
            List<Integer> availableTiers, AWExperienceIngredient experience, Optional<Holder<SoundEvent>> sound)
    {
        super(worktableType, result, secondary, fluid, extra, availableTiers, experience, sound);
        this.ingredients = NonNullList.copyOf(ingredients);
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
    }

    // GETTERS #########################################################################################################

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients()
    {
        return ingredients;
    }

    // OVERRIDES #######################################################################################################

    @Override
    public @NotNull RecipeType<?> getType()
    {
        return AWRecipeTypes.get(this.tableType);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer()
    {
        return AWRecipeSerializers.SHAPELESS.get(this.tableType).get();
    }

    @Override
    public boolean matches(@NotNull AWCraftingInput input, @NotNull Level level)
    {
        var a = this.matchTier(input.getTier());
        var b = this.matchIngredients(input);
        var c = this.matchSecondary(input.getSecondary());
        var d = this.matchFluid(input.getFluid());
        return a && b && c && d;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull AWCraftingInput input, @NotNull HolderLookup.Provider registries)
    {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return width * height >= this.ingredients.size();
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries)
    {
        return this.result;
    }

    @Override
    public boolean showNotification()
    {
        return false;
    }

    // OTHERS ##########################################################################################################

    protected boolean matchIngredients(AWCraftingInput input)
    {
        if (input.ingredientCount() != this.ingredients.size())
        {
            return false;
        }
        else if (!isSimple)
        {
            var nonEmptyItems = new java.util.ArrayList<ItemStack>(input.ingredientCount());
            for (var item : input.items())
            {
                if (!item.isEmpty())
                {
                    nonEmptyItems.add(item);
                }
            }
            return net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(nonEmptyItems, this.ingredients) != null;
        }
        else
        {
            return input.size() == 1 && this.ingredients.size() == 1
                    ? this.ingredients.getFirst().test(input.getItem(0))
                    : input.stackedContents().canCraft(this, null);
        }
    }

    public static RecipeSerializer<AWShapelessRecipe> createSerializer(AWEnumType type)
    {
        MapCodec<AWShapelessRecipe> codec = RecordCodecBuilder.mapCodec(builder -> builder.group(
                        ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                        Codec.lazyInitialized(() -> Ingredient.CODEC_NONEMPTY.listOf(1, 25)).fieldOf("ingredients").forGetter(r -> r.ingredients),
                        AWSizedIngredient.FLAT_CODEC.listOf().optionalFieldOf("secondary", List.of()).forGetter(r -> r.secondary),
                        SizedFluidIngredient.FLAT_CODEC.optionalFieldOf("fluid").forGetter(r -> r.fluid),
                        AWItemStackTemplate.CODEC.listOf(0, 3).optionalFieldOf("extra", List.of()).forGetter(r -> r.extra),
                        Codec.intRange(0, 2).listOf(1, 3).optionalFieldOf("tier", List.of(0, 1, 2)).forGetter(AWRecipe::getAvailableTiers),
                        AWExperienceIngredient.CODEC.optionalFieldOf("experience", AWExperienceIngredient.DEFAULT).forGetter(r -> r.experience),
                        SoundEvent.CODEC.optionalFieldOf("sound").forGetter(AWRecipe::getSound))
                .apply(builder, (r, i, s, f, e, t, x, sd) ->
                        new AWShapelessRecipe(type, r, i, s, f, e, t, x, sd)));

        StreamCodec<RegistryFriendlyByteBuf, AWShapelessRecipe> streamCodec = StreamCodec.of(
                (buf, recipe) -> {
                    ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.ingredients);
                    AWSizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.secondary);
                    SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(buf, recipe.fluid);
                    AWItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.extra);
                    ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()).encode(buf, recipe.availableTiers);
                    AWExperienceIngredient.STREAM_CODEC.encode(buf, recipe.experience);
                    SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(buf, recipe.getSound());
                },
                buf -> new AWShapelessRecipe(
                        type,
                        ItemStack.STREAM_CODEC.decode(buf),
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
                        AWSizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
                        SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(buf),
                        AWItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf),
                        ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()).decode(buf),
                        AWExperienceIngredient.STREAM_CODEC.decode(buf),
                        SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(buf)
                )
        );

        return new RecipeSerializer<>()
        {
            @Override
            public @NotNull MapCodec<AWShapelessRecipe> codec()
            {
                return codec;
            }

            @Override
            public @NotNull StreamCodec<RegistryFriendlyByteBuf, AWShapelessRecipe> streamCodec()
            {
                return streamCodec;
            }
        };
    }
}
