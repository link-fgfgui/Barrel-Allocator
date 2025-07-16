package io.github.linkfgfgui.barrel_allocator;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AllocatorBlockEntity extends BlockEntity implements Nameable {
    private final int ITEMSTACKSIZE = 27;
    private final int FLUIDTANKCAPA = 4000;

    @Nullable
    private Component name;

    private final Direction left = getLeft();
    private final BlockPos targetBlockPos = getBlockPos().relative(left);
    private final ItemStackHandler inventory = new ItemStackHandler(ITEMSTACKSIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public final FluidTank tank = new FluidTank(FLUIDTANKCAPA) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };



    public AllocatorBlockEntity(BlockPos pos, BlockState blockState) {
        super(Barrel_allocator.ALLOCATOR_BE_TYPE.get(), pos, blockState);
    }

    private @NotNull Direction getLeft() {
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        return getLeft(facing);
    }

    private @NotNull Direction getLeft(Direction facing) {
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inv", inventory.serializeNBT(registries));
        tag.put("tank", tank.writeToNBT(registries,new CompoundTag()));
        if (this.name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.name, registries));
        }
        this.setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inv"));
        tank.readFromNBT(registries,tag.getCompound("tank"));
        if (tag.contains("CustomName", 8)) {
            this.name = parseCustomNameSafe(tag.getString("CustomName"), registries);
        }
    }

    public IItemHandler getCap(Direction side) {
        return new DirectionalIItemHandlerWrapper(inventory, side == left, level, targetBlockPos, left.getOpposite());
    }

    public IFluidHandler getCapF(Direction side) {
        return new DirectionalIFluidHandlerWrapper(tank, side == left, level, targetBlockPos, left.getOpposite());
    }

    @Override
    public Component getName() {
        return this.name != null ? this.name : Component.translatable("block.barrel_allocator.allocator");
    }

    @Override
    public boolean hasCustomName() {
        return this.getCustomName() != null;
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    public @Nullable Component getCustomName() {
        return name;
    }
    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.name);
    }



}
