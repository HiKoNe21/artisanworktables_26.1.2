package hikone.artwork.init;

import hikone.artwork.client.gui.screens.WorkshopScreen;
import hikone.artwork.client.gui.screens.WorkstationScreen;
import hikone.artwork.client.gui.screens.WorktableScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class AWScreens
{
    public static void init(RegisterMenuScreensEvent event)
    {
        register(event, AWMenuTypes.WORKTABLE.get(), WorktableScreen::new);
        register(event, AWMenuTypes.WORKSTATION.get(), WorkstationScreen::new);
        register(event, AWMenuTypes.WORKSHOP.get(), WorkshopScreen::new);
    }

    public static <M extends AbstractContainerMenu, U extends AbstractContainerScreen<M>> void register(
            RegisterMenuScreensEvent event,
            MenuType<M> type,
            MenuScreens.ScreenConstructor<M, U> screenConstructor)
    {
        event.register(type, screenConstructor);
    }
}
