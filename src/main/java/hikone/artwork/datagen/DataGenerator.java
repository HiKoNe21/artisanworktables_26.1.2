package hikone.artwork.datagen;

import hikone.artwork.datagen.provider.AWBlockLootSubProvider;
import hikone.artwork.datagen.provider.AWBlockTagsProvider;
import hikone.artwork.datagen.provider.AWItemTagsProvider;
import hikone.artwork.datagen.provider.AWModelProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

public class DataGenerator
{
    public static void gatherClientData(GatherDataEvent.Client event)
    {
        var generator = event.getGenerator();
        var pack = generator.getVanillaPack(true);

        pack.addProvider(AWModelProvider::new);
    }

    public static void gatherServerData(GatherDataEvent.Server event)
    {
        var generator = event.getGenerator();
        var pack = generator.getVanillaPack(true);
        var lookupProvider = event.getLookupProvider();

        pack.addProvider(output -> new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(AWBlockLootSubProvider::new, LootContextParamSets.BLOCK)),
                event.getLookupProvider()));

        pack.addProvider(output -> new AWBlockTagsProvider(output, lookupProvider));

        pack.addProvider(output -> new AWItemTagsProvider(output, lookupProvider));
    }
}
