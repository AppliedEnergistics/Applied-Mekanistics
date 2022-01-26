package me.ramidzkh.mekae2.mixin;

import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageMonitorableAccessor;
import appeng.capabilities.Capabilities;
import appeng.helpers.iface.IInterfaceTarget;
import me.ramidzkh.mekae2.ae2.impl.ChemicalExternalStorageFacade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(value = IInterfaceTarget.class, remap = false)
public interface IInterfaceTargetMixin {

    @Shadow
    static IInterfaceTarget wrapStorageMonitorable(IStorageMonitorableAccessor accessor, IActionSource src) {
        throw new AssertionError();
    }

    @Shadow
    static IInterfaceTarget wrapHandlers(IItemHandler itemHandler, IFluidHandler fluidHandler, IActionSource src) {
        throw new AssertionError();
    }

    @Overwrite
    @Nullable
    static IInterfaceTarget get(Level l, BlockPos pos, @Nullable BlockEntity be, Direction side, IActionSource src) {
        if (be == null)
            return null;

        // our capability first: allows any storage channel
        var accessor = be.getCapability(Capabilities.STORAGE_MONITORABLE_ACCESSOR, side).orElse(null);
        if (accessor != null) {
            return wrapStorageMonitorable(accessor, src);
        }

        // otherwise fall back to the platform capability
        // TODO: look into exposing this for other storage channels
        var itemHandler = be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        var fluidHandler = be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);

        if (itemHandler.isPresent() || fluidHandler.isPresent()) {
            return ChemicalExternalStorageFacade.get(be, side, src, wrapHandlers(
                    itemHandler.orElse(EmptyHandler.INSTANCE),
                    fluidHandler.orElse(EmptyFluidHandler.INSTANCE),
                    src));
        }

        return be == null ? null : ChemicalExternalStorageFacade.get(be, side, src, null);
    }
}
