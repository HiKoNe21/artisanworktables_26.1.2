package hikone.artwork.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class AWGuiButton extends Button
{
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int texWidth;
    private final int texHeight;

    public AWGuiButton(
            int x, int y, int width, int height, Identifier texture, int u, int v, Component tooltip,
            OnPress onPress)
    {
        this(x, y, width, height, texture, u, v, 256, 256, tooltip, onPress);
    }

    public AWGuiButton(
            int x, int y, int width, int height, Identifier texture, int u, int v, int texWidth, int texHeight,
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
    protected void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), this.u, this.v, this.width,
                this.height, this.texWidth, this.texHeight);

        if (this.isHovered())
        {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x80FFFFFF);
        }
    }
}
