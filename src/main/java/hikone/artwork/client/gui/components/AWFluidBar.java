package hikone.artwork.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import hikone.artwork.network.AWDestroyFluidPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class AWFluidBar extends AbstractWidget
{
    protected final Supplier<FluidStack> fluidSupplier;
    protected final IntSupplier capacitySupplier;

    public AWFluidBar(
            int x, int y, int width, int height, Supplier<FluidStack> fluidSupplier,
            IntSupplier capacitySupplier)
    {
        super(x, y, width, height, Component.empty());
        this.fluidSupplier = fluidSupplier;
        this.capacitySupplier = capacitySupplier;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (button == 0 && Screen.hasShiftDown() && this.isMouseOver(mouseX, mouseY))
        {
            FluidStack fluid = this.fluidSupplier.get();
            if (!fluid.isEmpty())
            {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                PacketDistributor.sendToServer(new AWDestroyFluidPayload());
                return true;
            }
        }
        return false;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        var fluid = this.fluidSupplier.get();
        int capacity = this.capacitySupplier.getAsInt();

        if (!fluid.isEmpty() && capacity > 0)
        {
            int filledHeight = (int) ((long) fluid.getAmount() * this.height / capacity);
            if (filledHeight > 0)
            {
                IClientFluidTypeExtensions fluidExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
                TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(
                        fluidExtensions.getStillTexture(fluid));

                int tintColor = fluidExtensions.getTintColor(fluid);
                float r = FastColor.ARGB32.red(tintColor) / 255.0F;
                float g = FastColor.ARGB32.green(tintColor) / 255.0F;
                float b = FastColor.ARGB32.blue(tintColor) / 255.0F;
                float a = FastColor.ARGB32.alpha(tintColor) / 255.0F;

                RenderSystem.enableBlend();
                graphics.setColor(r, g, b, a);

                int renderY = this.getY() + this.height - filledHeight;
                graphics.enableScissor(this.getX(), renderY, this.getX() + this.width, this.getY() + this.height);
                for (int bottomY = this.getY() + this.height; bottomY > renderY; bottomY -= 16)
                {
                    graphics.blit(this.getX(), bottomY - 16, 0, 16, 16, sprite);
                }
                graphics.disableScissor();

                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableBlend();
            }
        }

        if (this.isHovered())
        {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x80FFFFFF);

            var tooltipLines = new ArrayList<FormattedCharSequence>();

            if (fluid.isEmpty())
            {
                tooltipLines.add(
                        Component.translatable("gui.artwork.tooltip.fluid.empty", capacity).getVisualOrderText());
            }
            else
            {
                tooltipLines.add(Component
                        .translatable(
                                "gui.artwork.tooltip.fluid.amount", fluid.getHoverName(), fluid.getAmount(),
                                capacity).getVisualOrderText());
                tooltipLines.add(Component.translatable("gui.artwork.tooltip.fluid.destroy")
                        .withStyle(ChatFormatting.GRAY).getVisualOrderText());
            }

            graphics.renderTooltip(Minecraft.getInstance().font, tooltipLines, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output)
    {
    }
}
