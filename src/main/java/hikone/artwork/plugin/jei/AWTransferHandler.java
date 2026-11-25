package hikone.artwork.plugin.jei;

import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.menu.AWMenu;
import hikone.artwork.network.AWJeiTransferPayload;
import hikone.artwork.recipe.AWRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AWTransferHandler<M extends AWMenu> implements IRecipeTransferHandler<M, RecipeHolder<AWRecipe>>
{
    private final IRecipeTransferHandlerHelper helper;
    private final Class<M> menuClass;
    private final MenuType<M> menuType;
    private final AWEnumTier recipeTier;
    private final AWEnumType recipeType;
    private final RecipeType<RecipeHolder<AWRecipe>> jeiRecipeType;

    public AWTransferHandler(
            IRecipeTransferHandlerHelper transferHelper,
            Class<M> menuClass,
            MenuType<M> menuType,
            AWEnumTier recipeTier,
            AWEnumType recipeType)
    {
        this.helper = transferHelper;
        this.menuClass = menuClass;
        this.menuType = menuType;
        this.recipeTier = recipeTier;
        this.recipeType = recipeType;
        this.jeiRecipeType = AWJEIPlugin.CATEGORY_KEYS.get(recipeTier).get(recipeType);
    }

    @Override
    public @NotNull Class<? extends M> getContainerClass()
    {
        return this.menuClass;
    }

    @Override
    public @NotNull Optional<MenuType<M>> getMenuType()
    {
        return Optional.ofNullable(this.menuType);
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<AWRecipe>> getRecipeType()
    {
        return this.jeiRecipeType;
    }

    @SuppressWarnings("removal")
    @Override
    public IRecipeTransferError transferRecipe(
            @NotNull M container,
            @NotNull RecipeHolder<AWRecipe> recipeHolder,
            @NotNull IRecipeSlotsView recipeSlots,
            @NotNull Player player,
            boolean maxTransfer,
            boolean doTransfer)
    {
        AWRecipe recipe = recipeHolder.value();

        if (container.getTableType() != this.recipeType || !recipe.matchTier(container.getTableTier()))
        {
            return this.helper.createInternalError();
        }

        List<ItemStack> available = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack s = player.getInventory().getItem(i);
            if (!s.isEmpty()) available.add(s.copy());
        }
        for (int i = AWMenu.SECONDARY_SLOT_START; i < AWMenu.SECONDARY_SLOT_END; i++)
        {
            ItemStack s = container.getSlot(i).getItem();
            if (!s.isEmpty()) available.add(s.copy());
        }
        int gridEnd = AWMenu.GRID_SLOT_START + (container.getGridSize() * container.getGridSize());
        for (int i = AWMenu.GRID_SLOT_START; i < gridEnd; i++)
        {
            ItemStack s = container.getSlot(i).getItem();
            if (!s.isEmpty()) available.add(s.copy());
        }

        List<IRecipeSlotView> allInputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
        int gridSlotCount = switch (this.recipeTier)
        {
            case WORKTABLE -> 9;
            case WORKSTATION -> 16;
            case WORKSHOP -> 25;
        };

        var secondaryReqs = recipe.getSecondary();
        if (secondaryReqs != null && !secondaryReqs.isEmpty())
        {
            List<IRecipeSlotView> missingTools = new ArrayList<>();

            for (int i = 0; i < secondaryReqs.size(); i++)
            {
                var req = secondaryReqs.get(i);
                var ingredient = req.sizedIngredient().ingredient();
                int neededCount = req.sizedIngredient().count();
                int neededDamage = req.damage();

                boolean satisfied = false;
                for (ItemStack avail : available)
                {
                    if (avail.isEmpty() || !ingredient.test(avail)) continue;

                    if (avail.isDamageableItem() && neededDamage > 0)
                    {
                        if ((avail.getMaxDamage() - avail.getDamageValue()) >= neededDamage)
                        {
                            avail.shrink(1);
                            satisfied = true;
                            break;
                        }
                    }
                    else
                    {
                        int take = Math.min(avail.getCount(), neededCount);
                        avail.shrink(take);
                        neededCount -= take;
                        if (neededCount <= 0)
                        {
                            satisfied = true;
                            break;
                        }
                    }
                }

                if (!satisfied && (gridSlotCount + i) < allInputSlots.size())
                {
                    missingTools.add(allInputSlots.get(gridSlotCount + i));
                }
            }

            if (!missingTools.isEmpty())
            {
                return this.helper.createUserErrorForMissingSlots(
                        Component.translatable("jei.artwork.tooltip.transfer.missing_tool"),
                        missingTools
                );
            }
        }

        List<IRecipeSlotView> missingGridSlots = new ArrayList<>();
        for (int i = 0; i < Math.min(gridSlotCount, allInputSlots.size()); i++)
        {
            IRecipeSlotView slotView = allInputSlots.get(i);
            List<ItemStack> variants = slotView.getItemStacks().toList();
            if (variants.isEmpty()) continue;

            boolean found = false;
            for (ItemStack variant : variants)
            {
                for (ItemStack avail : available)
                {
                    if (!avail.isEmpty() && ItemStack.isSameItemSameComponents(avail, variant))
                    {
                        avail.shrink(1);
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }

            if (!found)
            {
                missingGridSlots.add(slotView);
            }
        }

        if (!missingGridSlots.isEmpty())
        {
            return this.helper.createUserErrorForMissingSlots(
                    Component.translatable("jei.tooltip.error.recipe.transfer.missing"),
                    missingGridSlots
            );
        }

        if (doTransfer)
        {
            PacketDistributor.sendToServer(new AWJeiTransferPayload(recipeHolder.id(), maxTransfer));
        }

        return null;
    }
}
