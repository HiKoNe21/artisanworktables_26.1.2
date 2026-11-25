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
    public static void gatherData(GatherDataEvent event)
    {
        var generator = event.getGenerator();
        var pack = generator.getVanillaPack(true);
        var lookupProvider = event.getLookupProvider();
        var existingFileHelper = event.getExistingFileHelper();

        // Клиентские провайдеры (модели и blockstates)
        pack.addProvider(output -> new AWModelProvider(output, existingFileHelper));

        // Серверные провайдеры (лут-таблицы и теги)
        pack.addProvider(output -> new LootTableProvider(
                output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(
                AWBlockLootSubProvider::new,
                LootContextParamSets.BLOCK)), lookupProvider));

        var blockTagsProvider = pack.addProvider(
                output -> new AWBlockTagsProvider(output, lookupProvider, existingFileHelper));

        pack.addProvider(output -> new AWItemTagsProvider(
                output, lookupProvider, blockTagsProvider.contentsGetter(),
                existingFileHelper));
    }
}
