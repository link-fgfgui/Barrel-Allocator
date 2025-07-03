package io.github.linkfgfgui.barrel_allocator;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class AllocatorBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level p_155062_, BlockPos p_155063_, BlockState p_155064_) {
            AllocatorBlockEntity.this.playSound(p_155064_, SoundEvents.BARREL_OPEN);
            AllocatorBlockEntity.this.updateBlockState(p_155064_, true);
        }

        @Override
        protected void onClose(Level p_155072_, BlockPos p_155073_, BlockState p_155074_) {
            AllocatorBlockEntity.this.playSound(p_155074_, SoundEvents.BARREL_CLOSE);
            AllocatorBlockEntity.this.updateBlockState(p_155074_, false);
        }

        @Override
        protected void openerCountChanged(Level p_155066_, BlockPos p_155067_, BlockState p_155068_, int p_155069_, int p_155070_) {
        }

        @Override
        protected boolean isOwnContainer(Player p_155060_) {
            if (p_155060_.containerMenu instanceof ChestMenu) {
                Container container = ((ChestMenu) p_155060_.containerMenu).getContainer();
                return container == AllocatorBlockEntity.this;
            } else {
                return false;
            }
        }
    };
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);

    public AllocatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(Barrel_allocator.ALLOCATOR_BE_TYPE.get(), pos, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.barrel_allocator.allocator");
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return IntStream.range(0, this.getContainerSize()).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        if (direction == null) return false;
//        Barrel_allocator.LOGGER.info(Config.direction.name());
        Direction facing = this.getBlockState().getValue(BlockStateProperties.FACING);
//        Barrel_allocator.LOGGER.info(facing.name());
//        Direction front = facing;
//        Direction back = facing.getOpposite();

        Direction left = getLeft(facing);

        boolean DirectionMatched = left == direction;
//        Barrel_allocator.LOGGER.info(left.name()+" "+direction.name()+" "+(DirectionMatched?"True":"False"));
        if (DirectionMatched) {
            return true;
        }
        BlockEntity target = level.getBlockEntity(worldPosition.relative(left));
        if (target instanceof Container container) {
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                if (container.canPlaceItem(slot, itemStack)) {
                    if (container.getItem(slot).isEmpty()) {
                        container.setItem(slot, itemStack.copyAndClear());
                    }
                }
            }
        }
        return false;
    }

    private static @NotNull Direction getLeft(Direction facing) {
        Direction left;
        Direction right;
        Direction top;
        Direction bottom;
        if (facing == Direction.DOWN || facing == Direction.UP) {
            left = Direction.EAST;
            right = Direction.WEST;
            if (facing == Direction.DOWN) {
                top = Direction.NORTH;
                bottom = Direction.SOUTH;
            } else {
                top = Direction.SOUTH;
                bottom = Direction.NORTH;
            }
        } else {
            top = Direction.UP;
            bottom = Direction.DOWN;
            left = facing.getClockWise();
            right = facing.getCounterClockWise();
        }
        return left;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return this.canTakeItem(this, index, stack);
    }


    //copied from net.minecraft.world.level.block.entity.BarrelBlockEntity

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return ChestMenu.threeRows(id, player, this);
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    void updateBlockState(BlockState state, boolean open) {
        this.level.setBlock(this.getBlockPos(), state.setValue(BarrelBlock.OPEN, Boolean.valueOf(open)), 3);
    }

    void playSound(BlockState state, SoundEvent sound) {
        Vec3i vec3i = state.getValue(BarrelBlock.FACING).getNormal();
        double d0 = (double) this.worldPosition.getX() + 0.5 + (double) vec3i.getX() / 2.0;
        double d1 = (double) this.worldPosition.getY() + 0.5 + (double) vec3i.getY() / 2.0;
        double d2 = (double) this.worldPosition.getZ() + 0.5 + (double) vec3i.getZ() / 2.0;
        this.level.playSound(null, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }


}
