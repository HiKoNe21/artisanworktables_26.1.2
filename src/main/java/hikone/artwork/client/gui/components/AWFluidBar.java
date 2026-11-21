package hikone.artwork.client.gui.components;

import hikone.artwork.network.AWDestroyFluidPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class AWFluidBar extends AbstractWidget
{
    protected final Supplier<FluidStack> fluidSupplier;
    protected final IntSupplier capacitySupplier;

    public AWFluidBar(
            int x,
            int y,
            int width,
            int height,
            Supplier<FluidStack> fluidSupplier,
            IntSupplier capacitySupplier)
    {
        super(x, y, width, height, Component.empty());
        this.fluidSupplier = fluidSupplier;
        this.capacitySupplier = capacitySupplier;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        if (event.button() == 0 && event.hasShiftDown() && this.isMouseOver(event.x(), event.y()))
        {
            FluidStack fluid = this.fluidSupplier.get();
            if (!fluid.isEmpty())
            {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                ClientPacketDistributor.sendToServer(new AWDestroyFluidPayload());
                return true;
            }
        }
        return false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        var fluid = this.fluidSupplier.get();
        int capacity = this.capacitySupplier.getAsInt();

        if (!fluid.isEmpty() && capacity > 0)
        {
            int filledHeight = (int) ((long) fluid.getAmount() * this.height / capacity);
            if (filledHeight > 0)
            {
                var fluidState = fluid
                        .getFluid()
                        .defaultFluidState();
                var fluidModel = Minecraft
                        .getInstance()
                        .getModelManager()
                        .getFluidStateModelSet()
                        .get(fluidState);
                var sprite = fluidModel
                        .stillMaterial()
                        .sprite();
                int tintColor = fluidModel.fluidTintSource() != null ? fluidModel
                        .fluidTintSource()
                        .color(fluidState) : 0xFFFFFFFF;

                int renderY = this.getY() + this.height - filledHeight;
                graphics.enableScissor(this.getX(), renderY, this.getX() + this.width, this.getY() + this.height);
                for (int bottomY = this.getY() + this.height; bottomY > renderY; bottomY -= 16)
                {
                    graphics.blitSprite(
                            RenderPipelines.GUI_TEXTURED,
                            sprite,
                            this.getX(),
                            bottomY - 16,
                            16,
                            16,
                            tintColor
                    );
                }
                graphics.disableScissor();
            }
        }

        if (this.isHovered())
        {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x80FFFFFF);

            var tooltipLines = new ArrayList<FormattedCharSequence>();

            if (fluid.isEmpty())
            {
                tooltipLines.add(Component.translatable("gui.artwork.tooltip.fluid.empty", capacity).getVisualOrderText());
            }
            else
            {
                tooltipLines.add(Component.translatable("gui.artwork.tooltip.fluid.amount", fluid.getHoverName(), fluid.getAmount(), capacity).getVisualOrderText());
                tooltipLines.add(Component.translatable("gui.artwork.tooltip.fluid.destroy").withStyle(ChatFormatting.GRAY).getVisualOrderText());
            }

            graphics.setTooltipForNextFrame(
                    Minecraft.getInstance().font,
                    tooltipLines,
                    mouseX,
                    mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {

    }
}
