package hikone.artwork.client.gui.screens;

import hikone.artwork.client.gui.components.AWFluidBar;
import hikone.artwork.menu.WorkstationMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class WorkstationScreen extends AWScreen<WorkstationMenu>
{
    // CONSTRUCTORS ####################################################################################################

    public WorkstationScreen(WorkstationMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
    }

    // OVERRIDES #######################################################################################################

    @Override
    protected void init()
    {
        super.init();
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 112;
        this.fluidBar = this.addRenderableWidget(new AWFluidBar(
                this.leftPos + 8,
                this.topPos + 17,
                7,
                70,
                this.menu::getFluid,
                this.menu::getFluidCapacity));
    }
}
