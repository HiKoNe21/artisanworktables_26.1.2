package hikone.artwork.datagen.provider;

import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.block.AWBlock;
import hikone.artwork.init.AWBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

public class AWModelProvider extends ModelProvider
{
    public AWModelProvider(PackOutput output)
    {
        super(output, ArtWork.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
    {
        for (var tierMap : AWBlocks.BLOCKS.values())
        {
            for (var deferredBlock : tierMap.values())
            {
                var block = deferredBlock.get();
                var blockName = deferredBlock.getId().getPath();
                var split = blockName.split("_");
                var modelLocation = ArtWork.makeId("block/" + split[1] + "/" + split[0]);

                blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                        block,
                        BlockModelGenerators.plainVariant(modelLocation)));
                blockModels.registerSimpleItemModel(block.asItem(), modelLocation);
            }
        }
    }
}
