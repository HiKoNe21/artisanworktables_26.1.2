package hikone.artwork.plugin.kubejs;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TinyMap;
import hikone.artwork.recipe.format.AWExperienceIngredient;
import hikone.artwork.recipe.format.AWItemStackTemplate;
import hikone.artwork.recipe.format.AWSizedIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public final class AWRecipeSchemas
{
    public static final RecipeKey<ItemStack> RESULT = ItemStackComponent.ITEM_STACK.instance().outputKey("result");
    public static final RecipeKey<List<String>> PATTERN = StringComponent.STRING.instance().asList().inputKey("pattern");
    public static final RecipeKey<TinyMap<Character, Ingredient>> KEY = IngredientComponent.INGREDIENT.instance().asPatternKey().inputKey("key");
    public static final RecipeKey<List<Ingredient>> INGREDIENTS = IngredientComponent.INGREDIENT.instance().asList().inputKey("ingredients");

    public static final RecipeKey<List<AWSizedIngredient>> SECONDARY = ListRecipeComponent.create(AWCodecComponent.SIZED_INGREDIENT, true, true).inputKey("secondary").defaultOptional();
    public static final RecipeKey<SizedFluidIngredient> FLUID = SizedFluidIngredientComponent.FLAT.otherKey("fluid").defaultOptional();
    public static final RecipeKey<List<AWItemStackTemplate>> EXTRA = ListRecipeComponent.create(AWCodecComponent.CHANCED_RESULT, true, true).outputKey("extra").defaultOptional();
    public static final RecipeKey<List<Integer>> TIER = ListRecipeComponent.create(NumberComponent.INT, true, false).otherKey("tier").defaultOptional();
    public static final RecipeKey<AWExperienceIngredient> EXPERIENCE = AWCodecComponent.EXPERIENCE.otherKey("experience").defaultOptional();
    public static final RecipeKey<String> SOUND = StringComponent.ID.otherKey("sound").defaultOptional();

    public static final RecipeSchema SHAPED = new RecipeSchema(
            RESULT, PATTERN, KEY, SECONDARY, FLUID, EXTRA, TIER,
            EXPERIENCE, SOUND);

    public static final RecipeSchema SHAPELESS = new RecipeSchema(
            RESULT, INGREDIENTS, SECONDARY, FLUID, EXTRA, TIER,
            EXPERIENCE, SOUND);

    private AWRecipeSchemas()
    {
    }
}
