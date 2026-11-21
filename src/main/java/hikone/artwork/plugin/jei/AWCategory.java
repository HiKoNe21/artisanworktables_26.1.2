package hikone.artwork.plugin.jei;

import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.init.AWBlocks;
import hikone.artwork.recipe.AWRecipe;
import hikone.artwork.recipe.AWShapedRecipe;
import hikone.artwork.recipe.AWShapelessRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AWCategory implements IRecipeCategory<RecipeHolder<AWRecipe>>
{
    // STATICS #########################################################################################################

    private static final Identifier SHAPELESS_ICON = ArtWork.makeId("textures/gui/extra/recipe_shapeless.png");

    private static final int OFFSET_X = 3;
    private static final int OFFSET_Y = 3;

    protected static IRecipeSlotBuilder addSlot(
            @NonNull IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x,
            int y)
    {
        return builder.addSlot(role, x - OFFSET_X + 1, y - OFFSET_Y + 1);
    }

    // FIELDS ##########################################################################################################

    protected final AWEnumType type;
    protected final AWEnumTier tier;
    protected final Component titleKey;
    protected final IDrawable background;
    protected final IDrawable icon;
    protected final IRecipeHolderType<AWRecipe> recipeType;

    // CONSTRUCTORS ####################################################################################################

    public AWCategory(IGuiHelper guiHelper, AWEnumType type, AWEnumTier tier)
    {
        this.type = type;
        this.tier = tier;
        this.titleKey = Component.translatable(
                "block.%s.%s_%s".formatted(ArtWork.MODID, tier.getName(), type.getName()));
        var gui = ArtWork.makeId("textures/gui/%s/%s.png".formatted(type.getName(), tier.getName()));
        this.background = guiHelper.createDrawable(
                gui, OFFSET_X, OFFSET_Y, 170,
                tier == AWEnumTier.WORKTABLE ? 102 : (tier == AWEnumTier.WORKSTATION ? 120 : 138));
        this.icon = guiHelper.createDrawableItemLike(AWBlocks.get(type, tier));
        this.recipeType = AWJEIPlugin.CATEGORY_KEYS.get(tier).get(type);
    }

    // OVERRIDES #######################################################################################################

    @Override
    public @NonNull IRecipeHolderType<AWRecipe> getRecipeType()
    {
        return this.recipeType;
    }

    @Override
    public @NonNull Component getTitle()
    {
        return this.titleKey;
    }

    @Override
    public int getWidth()
    {
        return this.background.getWidth();
    }

    @Override
    public int getHeight()
    {
        return this.background.getHeight();
    }

    @Override
    public @Nullable IDrawable getIcon()
    {
        return this.icon;
    }

    @Override
    public void setRecipe(
            @NonNull IRecipeLayoutBuilder builder, @NonNull RecipeHolder<AWRecipe> holder,
            @NonNull IFocusGroup focuses)
    {
        var recipe = holder.value();
        setupOutput(builder, recipe);
        setupExtra(builder, recipe);

        setupCraftingGrid(builder, recipe);
        setupSecondary(builder, recipe);
        setupFluid(builder, recipe);

        setupTransferButton(builder);
    }

    @Override
    public void draw(
            @NonNull RecipeHolder<AWRecipe> holder, @NonNull IRecipeSlotsView recipeSlotsView,
            @NonNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY)
    {
        this.background.draw(guiGraphics);

        var recipe = holder.value();
        drawExperience(guiGraphics, recipe);
        drawShapelessIndicator(guiGraphics, recipe);
    }

    @Override
    public void getTooltip(
            @NonNull ITooltipBuilder tooltip, @NonNull RecipeHolder<AWRecipe> holder,
            @NonNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
    {
        var recipe = holder.value();
        tooltipShapeless(tooltip, recipe, mouseX, mouseY);
    }

    // OTHERS ##########################################################################################################

    private void setupOutput(@NonNull IRecipeLayoutBuilder builder, @NonNull AWRecipe recipe)
    {
        switch (this.tier)
        {
            case WORKTABLE -> addSlot(builder, RecipeIngredientRole.OUTPUT, 123, 34).add(recipe.getResult());
            case WORKSTATION -> addSlot(builder, RecipeIngredientRole.OUTPUT, 132, 52).add(recipe.getResult());
            case WORKSHOP -> addSlot(builder, RecipeIngredientRole.OUTPUT, 143, 70).add(recipe.getResult());
        }
    }

    private void setupExtra(@NonNull IRecipeLayoutBuilder builder, @NonNull AWRecipe recipe)
    {
        var extraOutputs = recipe.getExtra();
        if (extraOutputs == null || extraOutputs.isEmpty())
        {
            return;
        }

        int startX = switch (this.tier)
        {
            case WORKTABLE -> 151;
            case WORKSTATION, WORKSHOP -> 115;
        };
        int startY = switch (this.tier)
        {
            case WORKTABLE, WORKSTATION -> 16;
            case WORKSHOP -> 34;
        };

        int limit = Math.min(extraOutputs.size(), 3);
        for (int i = 0; i < limit; i++)
        {
            int x = this.tier == AWEnumTier.WORKTABLE ? startX : startX + (i * 18);
            int y = this.tier == AWEnumTier.WORKTABLE ? startY + (i * 18) : startY;

            var extra = extraOutputs.get(i);
            var slotBuilder = addSlot(builder, RecipeIngredientRole.OUTPUT, x, y);

            ItemStack stack = extra.result().create();
            float chance = extra.chance();

            if (!stack.isEmpty())
            {
                slotBuilder.add(stack);

                if (chance < 1.0F)
                {
                    String label = String.format(java.util.Locale.ROOT, "%.0f%%", chance * 100.0F);
                    slotBuilder.setOverlay(
                            new IDrawable()
                            {
                                @Override
                                public int getWidth()
                                {
                                    return 16;
                                }

                                @Override
                                public int getHeight()
                                {
                                    return 16;
                                }

                                @Override
                                public void draw(@NonNull GuiGraphicsExtractor guiGraphics, int xOffset, int yOffset)
                                {
                                    var font = Minecraft.getInstance().font;
                                    float scale = 0.65f;
                                    int rawWidth = font.width(label);

                                    var pose = guiGraphics.pose();
                                    pose.pushMatrix();
                                    pose.translate(xOffset + 16.0f - (rawWidth * scale), yOffset);
                                    pose.scale(scale, scale);

                                    guiGraphics.text(font, label, 0, 0, 0xFFFFFF55, true);

                                    pose.popMatrix();
                                }
                            }, 0, 0);

                    String label2 = String.format(java.util.Locale.ROOT, "%.2f%%", chance * 100.0F);
                    slotBuilder.addRichTooltipCallback((_, tooltip) -> tooltip.add(
                            Component.translatable("jei.artwork.tooltip.chance", label2)
                                    .withStyle(ChatFormatting.YELLOW)));
                }
            }
        }
    }

    private void setupSecondary(@NonNull IRecipeLayoutBuilder builder, @NonNull AWRecipe recipe)
    {
        var secondary = recipe.getSecondary();
        int yPos = switch (this.tier)
        {
            case WORKTABLE -> 74;
            case WORKSTATION -> 92;
            case WORKSHOP -> 110;
        };

        for (int i = 0; i < 9; i++)
        {
            var slotBuilder = addSlot(builder, RecipeIngredientRole.INPUT, 7 + (18 * i), yPos);

            if (secondary != null && i < secondary.size())
            {
                var req = secondary.get(i);
                var sizedIngredient = req.sizedIngredient();
                int damage = req.damage();

                slotBuilder.addItemStacks(sizedIngredient.ingredient().getValues().stream()
                        .map(itemHolder -> new ItemStack(itemHolder, sizedIngredient.count())).toList());

                if (req.catalyst())
                {
                    slotBuilder.setOverlay(
                            new IDrawable()
                            {
                                @Override
                                public int getWidth()
                                {
                                    return 16;
                                }

                                @Override
                                public int getHeight()
                                {
                                    return 16;
                                }

                                @Override
                                public void draw(@NonNull GuiGraphicsExtractor guiGraphics, int xOffset, int yOffset)
                                {
                                    var font = Minecraft.getInstance().font;
                                    String label = "*";
                                    guiGraphics.text(
                                            font, label, xOffset + 16 - font.width(label), yOffset + 1, 0xFFFFFFFF,
                                            true);
                                }
                            }, 0, 0);
                    slotBuilder.addRichTooltipCallback((_, tooltip) -> tooltip.add(
                            Component.translatable("jei.artwork.tooltip.not_consumed")
                                    .withStyle(ChatFormatting.WHITE)));
                }
                else if (damage > 0)
                {
                    String label = "-" + damage;
                    slotBuilder.setOverlay(
                            new IDrawable()
                            {
                                @Override
                                public int getWidth()
                                {
                                    return 16;
                                }

                                @Override
                                public int getHeight()
                                {
                                    return 16;
                                }

                                @Override
                                public void draw(@NonNull GuiGraphicsExtractor guiGraphics, int xOffset, int yOffset)
                                {
                                    var font = Minecraft.getInstance().font;
                                    int rawWidth = font.width(label);

                                    float maxWidth = 16.0f;
                                    float maxScale = 0.9f;

                                    float scale = rawWidth > 0 ? Math.min(maxScale, maxWidth / rawWidth) : maxScale;

                                    float drawX = xOffset + 16.0f - (rawWidth * scale);
                                    float drawY = yOffset + 16.0f - (font.lineHeight * scale);

                                    var pose = guiGraphics.pose();
                                    pose.pushMatrix();
                                    pose.translate(drawX, drawY);
                                    pose.scale(scale, scale);

                                    guiGraphics.text(font, label, 0, 0, 0xFFFF5555, true);

                                    pose.popMatrix();
                                }
                            }, 0, 0);

                    slotBuilder.addRichTooltipCallback((_, tooltip) -> tooltip.add(
                            Component.translatable("jei.artwork.tooltip.damage", damage)
                                    .withStyle(ChatFormatting.RED)));
                }
            }
        }
    }

    private void setupFluid(@NonNull IRecipeLayoutBuilder builder, @NonNull AWRecipe recipe)
    {
        var fluidIngredient = recipe.getFluid();
        if (fluidIngredient.isEmpty())
        {
            return;
        }

        var sizedFluid = fluidIngredient.get();
        int amount = sizedFluid.amount();
        List<FluidStack> matchingFluids = sizedFluid.ingredient().fluids().stream().map(
                fluidHolder -> new FluidStack(fluidHolder, amount)).toList();

        int tankHeight = switch (this.tier)
        {
            case WORKTABLE -> 52;
            case WORKSTATION -> 70;
            case WORKSHOP -> 88;
        };

        addSlot(builder, RecipeIngredientRole.CRAFTING_STATION, 7, 16).setFluidRenderer(
                amount * 2L, false, 7, tankHeight).addIngredients(NeoForgeTypes.FLUID_STACK, matchingFluids);
    }

    private void setupCraftingGrid(@NonNull IRecipeLayoutBuilder builder, @NonNull AWRecipe recipe)
    {
        int gridSize = switch (this.tier)
        {
            case WORKTABLE -> 3;
            case WORKSTATION -> 4;
            case WORKSHOP -> 5;
        };

        var gridSlots = new ArrayList<IRecipeSlotBuilder>(gridSize * gridSize);

        for (int y = 0; y < gridSize; y++)
        {
            for (int x = 0; x < gridSize; x++)
            {
                gridSlots.add(
                        addSlot(
                                builder, RecipeIngredientRole.INPUT,
                                (this.tier == AWEnumTier.WORKTABLE ? 29 : 20) + (x * 18), 16 + (y * 18)));
            }
        }

        if (recipe instanceof AWShapedRecipe shapedRecipe)
        {
            var pattern = shapedRecipe.getPattern();
            int width = pattern.width();
            int height = pattern.height();
            var ingredients = pattern.ingredients();

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    if (x < gridSize && y < gridSize)
                    {
                        var optIngredient = ingredients.get(x + y * width);
                        if (optIngredient.isPresent())
                        {
                            gridSlots.get(x + y * gridSize).add(optIngredient.get());
                        }
                    }
                }
            }
        }
        else if (recipe instanceof AWShapelessRecipe shapelessRecipe)
        {
            var ingredients = shapelessRecipe.getIngredients();
            int limit = Math.min(ingredients.size(), gridSlots.size());

            for (int i = 0; i < limit; i++)
            {
                gridSlots.get(i).add(ingredients.get(i));
            }
        }
    }

    private void setupTransferButton(@NonNull IRecipeLayoutBuilder builder)
    {
        switch (this.tier)
        {
            case WORKTABLE -> builder.moveRecipeTransferButton(159, 93);
            case WORKSTATION -> builder.moveRecipeTransferButton(159, 111);
            case WORKSHOP -> builder.moveRecipeTransferButton(159, 129);
        }
    }

    private void drawExperience(@NonNull GuiGraphicsExtractor guiGraphics, @NonNull AWRecipe recipe)
    {
        var exp = recipe.getExperience();
        if (exp == null || exp.isEmpty())
        {
            return;
        }

        int boxX;
        int boxY;
        int boxWidth;
        int boxHeight = 10;

        switch (this.tier)
        {
            case WORKTABLE ->
            {
                boxX = 84 - OFFSET_X;
                boxY = 60 - OFFSET_Y;
                boxWidth = 66;
            }
            case WORKSTATION ->
            {
                boxX = 93 - OFFSET_X;
                boxY = 78 - OFFSET_Y;
                boxWidth = 76;
            }
            case WORKSHOP ->
            {
                boxX = 111 - OFFSET_X;
                boxY = 96 - OFFSET_Y;
                boxWidth = 58;
            }
            default ->
            {
                return;
            }
        }

        var minecraft = Minecraft.getInstance();
        var font = minecraft.font;

        Component text = exp.getDisplayText();
        int color = exp.getTextColor(minecraft.player);

        guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x80555555);

        int rawWidth = font.width(text);
        float maxTextWidth = boxWidth - 4.0f;
        float scale = rawWidth > maxTextWidth ? (maxTextWidth / rawWidth) : 1.0f;

        float scaledWidth = rawWidth * scale;
        float scaledHeight = 8.0f * scale;

        float textX = boxX + (boxWidth - scaledWidth) / 2.0f;
        float textY = boxY + (boxHeight - scaledHeight) / 2.0f;

        var pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate(textX, textY);
        pose.scale(scale, scale);

        guiGraphics.text(font, text, 0, 0, color, true);

        pose.popMatrix();
    }

    private void drawShapelessIndicator(@NonNull GuiGraphicsExtractor guiGraphics, @NonNull AWRecipe recipe)
    {
        if (recipe instanceof AWShapelessRecipe)
        {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, SHAPELESS_ICON, 157, 2, 0.0f, 0.0f, 11, 10, 18, 17, 18, 17);
        }
    }

    private void tooltipShapeless(@NonNull ITooltipBuilder tooltip, AWRecipe recipe, double mouseX, double mouseY)
    {
        if (recipe instanceof AWShapelessRecipe)
        {
            int x = 157;
            int y = 2;

            if (mouseX >= x && mouseX <= x + 11 && mouseY >= y && mouseY <= y + 10)
            {
                tooltip.add(Component.translatable("jei.tooltip.shapeless.recipe"));
            }
        }
    }
}
