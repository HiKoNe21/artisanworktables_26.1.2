package hikone.artwork.init;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import hikone.artwork.api.ids.AWCreativeTabIds;
import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.ArrayList;
import java.util.List;

public final class AWCreativeTabs
{
    private static final Multimap<ResourceKey<CreativeModeTab>, ItemLike> externalItemDefs = HashMultimap.create();

    private static final List<ItemLike> itemDefs = new ArrayList<>();

    public static void init(Registry<CreativeModeTab> registry)
    {
        var tab = CreativeModeTab
                .builder()
                .title(Component.translatable("gui.artwork.creative_tab"))
                .icon(() -> AWBlocks.get(AWEnumType.BASIC, AWEnumTier.WORKTABLE).toStack(1))
                .displayItems(AWCreativeTabs::buildDisplayItems)
                .build();
        Registry.register(registry, AWCreativeTabIds.MAIN, tab);
    }

    public static void initExternal(BuildCreativeModeTabContentsEvent contents)
    {
        for (var itemDefinition : externalItemDefs.get(contents.getTabKey()))
        {
            contents.accept(itemDefinition);
        }
    }

    public static void add(ItemLike item)
    {
        itemDefs.add(item);
    }

    public static void addExternal(ResourceKey<CreativeModeTab> tab, ItemLike item)
    {
        externalItemDefs.put(tab, item);
    }

    private static void buildDisplayItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output)
    {
        for (var item : itemDefs)
        {
            output.accept(item);
        }
    }
}
