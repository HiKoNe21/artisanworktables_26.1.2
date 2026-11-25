package hikone.artwork.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;

public final class GUIHelper
{
    public static void drawStringOutlined(
            GuiGraphics graphics,
            Component textComponent,
            int x,
            int y,
            Font font,
            int textShadowColor)
    {
        graphics.drawString(font, textComponent, x + 0, y + 1, textShadowColor, false);
        graphics.drawString(font, textComponent, x + 1, y + 1, textShadowColor, false);
        graphics.drawString(font, textComponent, x + 1, y - 1, textShadowColor, false);
        graphics.drawString(font, textComponent, x + 1, y + 0, textShadowColor, false);

        graphics.drawString(font, textComponent, x - 0, y - 1, textShadowColor, false);
        graphics.drawString(font, textComponent, x - 1, y - 1, textShadowColor, false);
        graphics.drawString(font, textComponent, x - 1, y + 1, textShadowColor, false);
        graphics.drawString(font, textComponent, x - 1, y - 0, textShadowColor, false);

        graphics.drawString(font, textComponent, x, y, Color.BLACK.getRGB(), false);
    }
}
