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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class AWShapelessRecipe extends AWRecipe
{
    // FIELDS ##########################################################################################################

    protected final List<Ingredient> ingredients;
    protected final boolean isSimple;

    // CONSTRUCTORS ####################################################################################################

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public AWShapelessRecipe(
            AWEnumType worktableType, ItemStackTemplate result, List<Ingredient> ingredients, List<AWSizedIngredient> secondary,
            Optional<SizedFluidIngredient> fluid, List<AWItemStackTemplate> extra,
            List<Integer> availableTiers, AWExperienceIngredient experience, Optional<Holder<SoundEvent>> sound)
    {
        super(worktableType, result, secondary, fluid, extra, availableTiers, experience, sound);
        this.ingredients = ingredients;
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
    }

    // GETTERS #########################################################################################################

    public List<Ingredient> getIngredients()
    {
        return ingredients;
    }

    // OVERRIDES #######################################################################################################

    @Override
    public @NonNull RecipeType<? extends Recipe<AWCraftingInput>> getType()
    {
        return AWRecipeTypes.get(this.tableType);
    }

    @Override
    public @NonNull RecipeSerializer<? extends Recipe<AWCraftingInput>> getSerializer()
    {
        return AWRecipeSerializers.SHAPELESS.get(this.tableType).get();
    }

    @Override
    public boolean matches(@NonNull AWCraftingInput input, @NonNull Level level)
    {
        var a = this.matchTier(input.getTier());
        var b = this.matchIngredients(input);
        var c = this.matchSecondary(input.getSecondary());
        var d = this.matchFluid(input.getFluid());
        return a && b && c && d;
    }

    @Override
    public @NonNull ItemStack assemble(@NonNull AWCraftingInput input)
    {
        return this.result.create();
    }

    @Override
    public boolean showNotification()
    {
        return false;
    }

    @Override
    public @NonNull String group()
    {
        return "";
    }

    @Override
    public @NonNull PlacementInfo placementInfo()
    {
        return PlacementInfo.create(this.ingredients);
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory()
    {
        return RecipeBookCategories.CRAFTING_MISC;
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
            return net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(nonEmptyItems, this.ingredients) !=
                    null;
        }
        else
        {
            return input.size() == 1 && this.ingredients.size() == 1 ? this.ingredients.getFirst().test(
                    input.getItem(0)) : input.stackedContents().canCraft(this, null);
        }
    }

    public static RecipeSerializer<AWShapelessRecipe> createSerializer(AWEnumType type)
    {
        MapCodec<AWShapelessRecipe> codec = RecordCodecBuilder.mapCodec(builder -> builder.group(
                        ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.result),
                        com.mojang.serialization.Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, 25)).fieldOf("ingredients").forGetter(r -> r.ingredients),
                        AWSizedIngredient.CODEC.listOf().optionalFieldOf("secondary", List.of()).forGetter(r -> r.secondary),
                        SizedFluidIngredient.CODEC.optionalFieldOf("fluid").forGetter(r -> r.fluid),
                        AWItemStackTemplate.CODEC.listOf(0, 3).optionalFieldOf("extra", List.of()).forGetter(r -> r.extra),
                        Codec.intRange(0, 2).listOf(1, 3).optionalFieldOf("tier", List.of(0, 1, 2)).forGetter(AWRecipe::getAvailableTiers),
                        AWExperienceIngredient.CODEC.optionalFieldOf("experience", AWExperienceIngredient.DEFAULT).forGetter(r -> r.experience),
                        SoundEvent.CODEC.optionalFieldOf("sound").forGetter(AWRecipe::getSound))
                .apply(builder, (r, i, s, f, e, t, x, sd) ->
                        new AWShapelessRecipe(type, r, i, s, f, e, t, x, sd)));

        StreamCodec<RegistryFriendlyByteBuf, AWShapelessRecipe> streamCodec = StreamCodec.composite(
                ItemStackTemplate.STREAM_CODEC, r -> r.result,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
                AWSizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.secondary,
                SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs::optional), r -> r.fluid,
                AWItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.extra,
                ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), r -> r.availableTiers,
                AWExperienceIngredient.STREAM_CODEC, r -> r.experience,
                SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional), AWRecipe::getSound,
                (r, i, s, f, e, t, x, sd) ->
                        new AWShapelessRecipe(type, r, i, s, f, e, t, x, sd));

        return new RecipeSerializer<>(codec, streamCodec);
    }
}
