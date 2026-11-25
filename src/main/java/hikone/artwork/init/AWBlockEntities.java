package hikone.artwork.init;

import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.blockentity.AWBlockEntity;
import hikone.artwork.blockentity.WorkshopBlockEntity;
import hikone.artwork.blockentity.WorkstationBlockEntity;
import hikone.artwork.blockentity.WorktableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class AWBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> DR = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ArtWork.MODID);

    public static final Map<AWEnumTier, DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends AWBlockEntity>>> ENTITIES = new EnumMap<>(AWEnumTier.class);

    static
    {
        register(AWEnumTier.WORKTABLE, WorktableBlockEntity::new);
        register(AWEnumTier.WORKSTATION, WorkstationBlockEntity::new);
        register(AWEnumTier.WORKSHOP, WorkshopBlockEntity::new);
    }

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends AWBlockEntity>> get(AWEnumTier tier)
    {
        return ENTITIES.get(tier);
    }

    private static <T extends AWBlockEntity> void register(
            AWEnumTier tier,
            BlockEntityFactory<T> factory)
    {
        var id = ArtWork.makeId(tier.getName());
        var holder = DR.register(id.getPath(), () ->
        {
            Block[] validBlocks = AWBlocks.BLOCKS.values().stream()
                    .map(tierMap -> tierMap.get(tier))
                    .filter(Objects::nonNull)
                    .map(DeferredHolder::get)
                    .toArray(Block[]::new);

            return BlockEntityType.Builder.of(factory::create, validBlocks).build(null);
        });

        @SuppressWarnings("unchecked")
        var wildcardHolder = (DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends AWBlockEntity>>) (Object) holder;
        ENTITIES.put(tier, wildcardHolder);
    }

    @FunctionalInterface
    interface BlockEntityFactory<T extends BlockEntity>
    {
        T create(BlockPos pos, BlockState state);
    }
}
