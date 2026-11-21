package hikone.artwork.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.awt.*;

public final class GUIHelper
{
    public static void drawStringOutlined(
            GuiGraphicsExtractor graphics,
            Component textComponent,
            int x,
            int y,
            Font font,
            int textShadowColor)
    {
        graphics.text(font, textComponent, x + 0, y + 1, textShadowColor, false);
        graphics.text(font, textComponent, x + 1, y + 1, textShadowColor, false);
        graphics.text(font, textComponent, x + 1, y - 1, textShadowColor, false);
        graphics.text(font, textComponent, x + 1, y + 0, textShadowColor, false);

        graphics.text(font, textComponent, x - 0, y - 1, textShadowColor, false);
        graphics.text(font, textComponent, x - 1, y - 1, textShadowColor, false);
        graphics.text(font, textComponent, x - 1, y + 1, textShadowColor, false);
        graphics.text(font, textComponent, x - 1, y - 0, textShadowColor, false);

        graphics.text(font, textComponent, x, y, Color.BLACK.getRGB(), false);
    }
}
