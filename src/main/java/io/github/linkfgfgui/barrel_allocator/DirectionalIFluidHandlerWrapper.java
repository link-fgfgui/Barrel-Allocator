package io.github.linkfgfgui.barrel_allocator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class DirectionalIFluidHandlerWrapper implements IFluidHandler {
    private final IFluidHandler handler;
    private final boolean isLeft;
    private final Level level;
    private final BlockPos targetBlockPos;
    private final Direction targetBlockSide;

    public DirectionalIFluidHandlerWrapper(IFluidHandler handler, boolean isLeft, Level level, BlockPos pos, Direction side) {
        this.handler = handler;
        this.isLeft = isLeft;
        this.level = level;
        targetBlockPos = pos;
        targetBlockSide = side;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (isLeft) {
            return handler.fill(resource,action);
        } else {
            IFluidHandler targetBlockHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, targetBlockPos, targetBlockSide);
            if (targetBlockHandler == null) return 0;
            return targetBlockHandler.fill(resource,action);
        }
    }

    @Override
    public int getTanks() {
        return handler.getTanks();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return handler.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return handler.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return handler.isFluidValid(tank,stack);
    }



    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return handler.drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return handler.drain(maxDrain,action);
    }
}
