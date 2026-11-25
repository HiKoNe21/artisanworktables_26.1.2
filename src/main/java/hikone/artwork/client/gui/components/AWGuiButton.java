package hikone.artwork.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AWGuiButton extends Button
{
    private final ResourceLocation texture;
    private final int u;
    private final int v;
    private final int texWidth;
    private final int texHeight;

    public AWGuiButton(
            int x, int y, int width, int height, ResourceLocation texture, int u, int v, Component tooltip,
            OnPress onPress)
    {
        this(x, y, width, height, texture, u, v, 256, 256, tooltip, onPress);
    }

    public AWGuiButton(
            int x, int y, int width, int height, ResourceLocation texture, int u, int v, int texWidth, int texHeight,
            Component tooltip, OnPress onPress)
    {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.setTooltip(Tooltip.create(tooltip));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float a)
    {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        graphics.blit(
                this.texture, this.getX(), this.getY(), this.u, this.v, this.width, this.height, this.texWidth,
                this.texHeight);

        RenderSystem.disableBlend();

        if (this.isHovered())
        {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x80FFFFFF);
        }
    }
}
