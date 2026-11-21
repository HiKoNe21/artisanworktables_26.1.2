package hikone.artwork.init;

import hikone.artwork.ArtWork;
import hikone.artwork.menu.WorkshopMenu;
import hikone.artwork.menu.WorkstationMenu;
import hikone.artwork.menu.WorktableMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AWMenuTypes
{
    public static final DeferredRegister<MenuType<?>> DR = DeferredRegister.create(BuiltInRegistries.MENU, ArtWork.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<WorktableMenu>> WORKTABLE = registerWithData("worktable", WorktableMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<WorkstationMenu>> WORKSTATION = registerWithData("workstation", WorkstationMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<WorkshopMenu>> WORKSHOP = registerWithData("workshop", WorkshopMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String name, MenuType.MenuSupplier<T> constructor)
    {
        return DR.register(name, () -> new MenuType<>(constructor, FeatureFlags.DEFAULT_FLAGS));
    }

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerWithData(String name, IContainerFactory<T> constructor)
    {
        return DR.register(name, () -> IMenuTypeExtension.create(constructor));
    }
}
