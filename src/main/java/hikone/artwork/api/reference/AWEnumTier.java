package hikone.artwork.api.reference;

import java.util.Comparator;
import java.util.stream.Stream;

public enum AWEnumTier
{
    WORKTABLE(0, "worktable"),
    WORKSTATION(1, "workstation"),
    WORKSHOP(2, "workshop");

    public static final String[] NAMES = Stream
            .of(AWEnumTier.values())
            .sorted(Comparator.comparing(AWEnumTier::getName))
            .map(AWEnumTier::getName)
            .toArray(String[]::new);

    private final int id;
    private final String name;

    AWEnumTier(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public int getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public static AWEnumTier fromId(int id)
    {
        for (var tier : AWEnumTier.values())
        {
            if (tier.getId() == id)
            {
                return tier;
            }
        }

        throw new IllegalArgumentException("Invalid id: " + id);
    }

    public static AWEnumTier fromName(String name)
    {
        var values = AWEnumTier.values();
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
