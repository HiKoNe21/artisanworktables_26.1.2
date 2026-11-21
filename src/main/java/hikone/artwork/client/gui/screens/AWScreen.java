package hikone.artwork.client.gui.screens;

import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.client.gui.components.AWFluidBar;
import hikone.artwork.client.gui.components.AWGuiButton;
import hikone.artwork.menu.AWMenu;
import hikone.artwork.menu.slot.AWExtraResultSlot;
import hikone.artwork.network.AWClearTablePayload;
import hikone.artwork.util.GUIHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class AWScreen<S extends AWMenu> extends AbstractContainerScreen<S>
{
    protected static final Identifier CLEAR_TEXTURE = ArtWork.makeId("textures/gui/extra/clear.png");

    // FIELDS ##########################################################################################################

    protected final Identifier backgroundTexture;
    protected final int textColor;
    protected AWFluidBar fluidBar;

    // CONSTRUCTORS ####################################################################################################

    public AWScreen(S menu, Inventory inventory, Component title)
    {
        super(
                menu, inventory, title, 176, switch (menu.getTableTier())
                {
                    case WORKTABLE -> 188;
                    case WORKSTATION -> 206;
                    case WORKSHOP -> 224;
                });

        var tableType = menu.getTableType();
        var tableTier = menu.getTableTier();
        this.backgroundTexture = ArtWork.makeId(
                "textures/gui/" + tableType.getName() + "/" + tableTier.getName() + ".png");
        this.textColor = tableType.getTextColor();
    }

    // OVERRIDES #######################################################################################################

    @Override
    protected void init()
    {
        super.init();

        int xOffset = switch (this.menu.getTableTier())
        {
            case WORKTABLE -> 84;
            case WORKSTATION -> 93;
            case WORKSHOP -> 111;
        };
        this.addRenderableWidget(
                new AWGuiButton(
                        this.leftPos + xOffset, this.topPos + 16, 9, 9, CLEAR_TEXTURE, 0, 0, 9, 9, Component.translatable("gui.artwork.tooltip.clear_table"),
                        _ -> ClientPacketDistributor.sendToServer(AWClearTablePayload.INSTANCE)));
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTexture, this.leftPos, this.topPos, 0.0F, 0.0F, 176, 224, 256, 256);

        if (this.fluidBar != null)
        {
            this.fluidBar.setPosition(this.leftPos + 8, this.topPos + 17);
        }
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int xm, int ym)
    {
        GUIHelper.drawStringOutlined(graphics, this.title, this.titleLabelX, this.titleLabelY, this.font, textColor);
        GUIHelper.drawStringOutlined(
                graphics, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY,
                this.font, textColor);

        this.menu.getCurrentRecipe().ifPresent(recipe ->
        {
            var exp = recipe.getExperience();
            if (!exp.isEmpty())
            {
                int boxX;
                int boxY;
                int boxWidth;
                int boxHeight = 10;

                switch (this.menu.getTableTier())
                {
                    case WORKTABLE -> { boxX = 84; boxY = 60; boxWidth = 66; }
                    case WORKSTATION -> { boxX = 93; boxY = 78; boxWidth = 76; }
                    case WORKSHOP -> { boxX = 111; boxY = 96; boxWidth = 58; }
                    default -> { return; }
                }

                Component text = exp.getDisplayText();
                int color = exp.getTextColor(this.minecraft.player);

                graphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0x80555555);

                int rawWidth = this.font.width(text);
                float maxTextWidth = boxWidth - 4.0f;
                float scale = rawWidth > maxTextWidth ? (maxTextWidth / rawWidth) : 1.0f;

                float scaledWidth = rawWidth * scale;
                float scaledHeight = 8.0f * scale;

                float textX = boxX + (boxWidth - scaledWidth) / 2.0f;
                float textY = boxY + (boxHeight - scaledHeight) / 2.0f;

                var pose = graphics.pose();
                pose.pushMatrix();
                pose.translate(textX, textY);
                pose.scale(scale, scale);

                graphics.text(this.font, text, 0, 0, color, true);

                pose.popMatrix();
            }
        });
    }

    @Override
    protected void renderSlotContents(
            @NonNull GuiGraphicsExtractor graphics, @NonNull ItemStack itemStack, @NonNull Slot slot,
            @Nullable String itemCount)
    {
        super.renderSlotContents(graphics, itemStack, slot, itemCount);

        if (slot instanceof AWExtraResultSlot extraSlot && !slot.hasItem())
        {
            ItemStack ghost = extraSlot.getGhostStack();
            if (!ghost.isEmpty())
            {
                graphics.item(ghost, slot.x, slot.y);
                graphics.itemDecorations(this.font, ghost, slot.x, slot.y);

                graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x808B8B8B);
            }
        }
    }
}
