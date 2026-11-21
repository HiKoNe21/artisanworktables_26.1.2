package hikone.artwork.plugin.jei;

import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.client.gui.screens.WorkshopScreen;
import hikone.artwork.client.gui.screens.WorkstationScreen;
import hikone.artwork.client.gui.screens.WorktableScreen;
import hikone.artwork.init.AWBlocks;
import hikone.artwork.init.AWMenuTypes;
import hikone.artwork.init.AWRecipeTypes;
import hikone.artwork.menu.AWMenu;
import hikone.artwork.menu.WorkshopMenu;
import hikone.artwork.menu.WorkstationMenu;
import hikone.artwork.menu.WorktableMenu;
import hikone.artwork.recipe.AWRecipe;
import hikone.artwork.recipe.AWShapedRecipe;
import hikone.artwork.recipe.AWShapelessRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.registration.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;
import java.util.*;

@JeiPlugin
public class AWJEIPlugin implements IModPlugin
{
    public static final Identifier PLUGIN_UID = ArtWork.makeId("jei_plugin");

    public static final Map<AWEnumTier, Map<AWEnumType, IRecipeHolderType<AWRecipe>>> CATEGORY_KEYS;

    static
    {
        CATEGORY_KEYS = new EnumMap<>(AWEnumTier.class);
        for (var type : AWEnumType.values())
        {
            for (var tier : AWEnumTier.values())
            {
                var map = CATEGORY_KEYS.computeIfAbsent(tier, t -> new EnumMap<>(AWEnumType.class));
                map.put(type, IRecipeHolderType.create(ArtWork.makeId(tier.getName() + "_" + type.getName())));
            }
        }
    }

    @Override
    public void registerCategories(@NonNull IRecipeCategoryRegistration registration)
    {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        for (var type : AWEnumType.values())
        {
            for (var tier : AWEnumTier.values())
            {
                registration.addRecipeCategories(new AWCategory(guiHelper, type, tier));
            }
        }
    }

    @Override
    public void registerRecipes(@NonNull IRecipeRegistration registration)
    {
        var recipeManager = ArtWork.instance().getRecipeManager();
        if (recipeManager == null)
        {
            return;
        }

        for (var type : AWEnumType.values())
        {
            for (var tier : AWEnumTier.values())
            {
                int maxGridSize = switch (tier)
                {
                    case WORKTABLE -> 3;
                    case WORKSTATION -> 4;
                    case WORKSHOP -> 5;
                };

                Collection<RecipeHolder<AWRecipe>> recipeHolders = recipeManager.recipeMap().byType(
                        AWRecipeTypes.get(type));
                registration.addRecipes(
                        CATEGORY_KEYS.get(tier).get(type),
                        recipeHolders.stream().filter(holder -> fitsInTier(holder.value(), tier, maxGridSize)).toList());
            }
        }
    }

    @Override
    public void registerRecipeCatalysts(@NonNull IRecipeCatalystRegistration registration)
    {
        for (var type : AWEnumType.values())
        {
            for (var tier : AWEnumTier.values())
            {
                registration.addCraftingStation(CATEGORY_KEYS.get(tier).get(type), AWBlocks.get(type, tier));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerRecipeTransferHandlers(@NonNull IRecipeTransferRegistration registration)
    {
        var transferHelper = registration.getTransferHelper();

        for (var menuTier : AWEnumTier.values())
        {
            Class<? extends AWMenu> containerClass = switch (menuTier)
            {
                case WORKTABLE -> WorktableMenu.class;
                case WORKSTATION -> WorkstationMenu.class;
                case WORKSHOP -> WorkshopMenu.class;
            };

            MenuType<? extends AWMenu> menuType = switch (menuTier)
            {
                case WORKTABLE -> AWMenuTypes.WORKTABLE.get();
                case WORKSTATION -> AWMenuTypes.WORKSTATION.get();
                case WORKSHOP -> AWMenuTypes.WORKSHOP.get();
            };

            for (var recipeTier : AWEnumTier.values())
            {
                if (recipeTier.getId() > menuTier.getId())
                {
                    continue;
                }

                for (var recipeType : AWEnumType.values())
                {
                    var categoryType = CATEGORY_KEYS.get(recipeTier).get(recipeType);
                    registration.addRecipeTransferHandler(
                            new AWTransferHandler<>(
                                    transferHelper,
                                    (Class<AWMenu>) containerClass,
                                    (MenuType<AWMenu>) menuType,
                                    recipeTier,
                                    recipeType
                            ),
                            categoryType
                    );
                }
            }
        }
    }

    @Override
    public void registerGuiHandlers(@NonNull IGuiHandlerRegistration registration)
    {
        registration.addGuiContainerHandler(
                WorktableScreen.class, new IGuiContainerHandler<>()
                {
                    @Override
                    public @NonNull Collection<IGuiClickableArea> getGuiClickableAreas(
                            @NonNull WorktableScreen containerScreen, double guiMouseX, double guiMouseY)
                    {
                        var tableType = containerScreen.getMenu().getTableType();
                        var result = new ArrayList<IGuiClickableArea>();
                        result.add(IGuiClickableArea.createBasic(
                                90, 35, 22, 15,
                                CATEGORY_KEYS.get(AWEnumTier.WORKTABLE).get(tableType)));
                        return result;
                    }
                });
        registration.addGuiContainerHandler(
                WorkstationScreen.class, new IGuiContainerHandler<>()
                {
                    @Override
                    public @NonNull Collection<IGuiClickableArea> getGuiClickableAreas(
                            @NonNull WorkstationScreen containerScreen, double guiMouseX, double guiMouseY)
                    {
                        var tableType = containerScreen.getMenu().getTableType();
                        var result = new ArrayList<IGuiClickableArea>();
                        result.add(IGuiClickableArea.createBasic(
                                99, 53, 22, 15,
                                CATEGORY_KEYS.get(AWEnumTier.WORKSTATION).get(tableType)));
                        return result;
                    }
                });
        registration.addGuiContainerHandler(
                WorkshopScreen.class, new IGuiContainerHandler<>()
                {
                    @Override
                    public @NonNull Collection<IGuiClickableArea> getGuiClickableAreas(
                            @NonNull WorkshopScreen containerScreen, double guiMouseX, double guiMouseY)
                    {
                        var tableType = containerScreen.getMenu().getTableType();
                        var result = new ArrayList<IGuiClickableArea>();
                        result.add(IGuiClickableArea.createBasic(
                                112, 71, 22, 15,
                                CATEGORY_KEYS.get(AWEnumTier.WORKSHOP).get(tableType)));
                        return result;
                    }
                });
    }

    @Nonnull
    @Override
    public Identifier getPluginUid()
    {
        return PLUGIN_UID;
    }

    private static boolean fitsInTier(AWRecipe recipe, AWEnumTier tier, int maxGridSize)
    {
        if (!recipe.matchTier(tier))
        {
            return false;
        }

        if (recipe instanceof AWShapedRecipe shaped)
        {
            var pattern = shaped.getPattern();
            return pattern.width() <= maxGridSize && pattern.height() <= maxGridSize;
        }
        if (recipe instanceof AWShapelessRecipe shapeless)
        {
            return shapeless.getIngredients().size() <= maxGridSize * maxGridSize;
        }
        return true;
    }
}
