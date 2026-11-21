package hikone.artwork.api.reference;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Comparator;
import java.util.stream.Stream;

public enum AWEnumType
{
    TAILOR("tailor", new Color(122, 171, 219)),
    CARPENTER("carpenter", new Color(188, 152, 98)),
    MASON("mason", new Color(151, 151, 151)),
    BLACKSMITH("blacksmith", new Color(162, 162, 162)),
    JEWELER("jeweler", new Color(105, 89, 133)),
    BASIC("basic", new Color(188, 152, 98)),
    ENGINEER("engineer", new Color(255, 146, 122)),
    MAGE("mage", new Color(140, 50, 255)),
    SCRIBE("scribe", new Color(182, 136, 79)),
    CHEMIST("chemist", new Color(71, 97, 71)),
    FARMER("farmer", new Color(128, 198, 82)),
    CHEF("chef", new Color(255, 255, 255)),
    DESIGNER("designer", new Color(255, 255, 255)),
    TANNER("tanner", new Color(199, 125, 79)),
    POTTER("potter", new Color(183, 90, 64));

    public static final String[] NAMES = Stream
            .of(AWEnumType.values())
            .sorted(Comparator.comparing(AWEnumType::getName))
            .map(AWEnumType::getName)
            .toArray(String[]::new);

    private final String name;
    private final int textColor;

    AWEnumType(String name, Color textColor)
    {
        this.name = name;
        this.textColor = textColor.getRGB();
    }

    @Nonnull
    public String getName()
    {
        return this.name;
    }

    public int getTextColor()
    {
        return this.textColor;
    }

    public static AWEnumType fromName(String name)
    {
        var values = AWEnumType.values();
        name = name.toLowerCase();

        for (var value : values)
        {
            if (value.name.equals(name))
            {
                return value;
            }
        }

        throw new IllegalArgumentException("Unknown name: " + name);
    }
}
