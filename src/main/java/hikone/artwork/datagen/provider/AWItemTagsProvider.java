package hikone.artwork.datagen.provider;

import hikone.artwork.ArtWork;
import hikone.artwork.api.ids.AWTags;
import hikone.artwork.init.AWBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class AWItemTagsProvider extends ItemTagsProvider
{
    public AWItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, ArtWork.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider)
    {
        var allTablesTag = this.tag(AWTags.Items.ALL_TABLES);

        for (var tierEntry : AWBlocks.BLOCKS.entrySet())
        {
            var type = tierEntry.getKey();
            var typeTag = this.tag(AWTags.Items.forType(type));

            for (var typeEntry : tierEntry.getValue().entrySet())
            {
                var tier = typeEntry.getKey();
                var item = typeEntry.getValue().get().asItem();
                var tierTag = this.tag(AWTags.Items.forTier(tier));

                allTablesTag.add(item);
                typeTag.add(item);
                tierTag.add(item);
            }
        }
    }
}