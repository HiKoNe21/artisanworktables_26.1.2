package hikone.artwork.api.ids;

import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AWTags
{
    public static final class Items
    {
        public static final TagKey<Item> ALL_TABLES = tag("tables");

        public static final TagKey<Item> WORKTABLES = tag("worktables");
        public static final TagKey<Item> WORKSTATIONS = tag("workstations");
        public static final TagKey<Item> WORKSHOPS = tag("workshops");

        public static TagKey<Item> forTier(AWEnumTier tier)
        {
            return switch (tier)
            {
                case WORKTABLE -> WORKTABLES;
                case WORKSTATION -> WORKSTATIONS;
                case WORKSHOP -> WORKSHOPS;
            };
        }

        public static TagKey<Item> forType(AWEnumType type)
        {
            return tag("type/" + type.getName());
        }

        private static TagKey<Item> tag(String name)
        {
            return ItemTags.create(ArtWork.makeId(name));
        }
    }

    private AWTags()
    {
    }
}
