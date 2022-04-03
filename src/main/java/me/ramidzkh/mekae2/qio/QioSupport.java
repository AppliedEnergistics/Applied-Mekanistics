package me.ramidzkh.mekae2.qio;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import me.ramidzkh.mekae2.AppliedMekanistics;
import mekanism.common.tile.qio.TileEntityQIODashboard;

import appeng.api.storage.IStorageMonitorableAccessor;

public class QioSupport {
    private static final Capability<IStorageMonitorableAccessor> STORAGE_MONITORABLE = CapabilityManager
            .get(new CapabilityToken<>() {
            });

    public static void initialize() {
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, QioSupport::onBlockEntityCapability);
    }

    public static void onBlockEntityCapability(AttachCapabilitiesEvent<BlockEntity> event) {
        var object = event.getObject();

        if (object instanceof TileEntityQIODashboard dashboard) {
            event.addCapability(AppliedMekanistics.id("qio_storage_monitorable"), new ICapabilityProvider() {
                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                    if (capability == STORAGE_MONITORABLE) {
                        return LazyOptional.of(() -> (IStorageMonitorableAccessor) querySrc -> {
                            var adapter = new QioStorageAdapter(dashboard, arg, querySrc);
                            // Make sure that we only allow non-null frequencies.
                            return adapter.getFrequency() == null ? null : adapter;
                        }).cast();
                    }
                    return LazyOptional.empty();
                }
            });
        }
    }
}
