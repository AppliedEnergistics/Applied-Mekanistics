package me.ramidzkh.mekae2.qio;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.registries.ForgeRegistries;

import me.ramidzkh.mekae2.AppliedMekanistics;
import mekanism.api.inventory.qio.IQIOComponent;

import appeng.api.networking.GridHelper;
import appeng.api.storage.MEStorage;

public class QioSupport {
    public static final Capability<MEStorage> STORAGE = CapabilityManager
            .get(new CapabilityToken<>() {
            });
    private static final ResourceLocation DASHBOARD = new ResourceLocation("mekanism", "qio_dashboard");

    public static void initialize() {
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, QioSupport::onBlockEntityCapability);
    }

    public static void onBlockEntityCapability(AttachCapabilitiesEvent<BlockEntity> event) {
        var object = event.getObject();

        if (object instanceof IQIOComponent) {
            if (DASHBOARD.equals(ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(object.getType()))) {
                event.addCapability(AppliedMekanistics.id("qio_storage_monitorable"), new ICapabilityProvider() {
                    @NotNull
                    @Override
                    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                            @Nullable Direction arg) {
                        out: if (capability == STORAGE && arg != null) {
                            // guess the source...
                            // if you're trying to qio across a compact machine wall or something, sorry!
                            var host = GridHelper.getNodeHost(object.getLevel(), object.getBlockPos().relative(arg));

                            if (host == null) {
                                break out;
                            }

                            var source = host.getGridNode(arg.getOpposite());

                            // I don't know of any full-block nodes which query inventories, but we'll see
                            if (source == null) {
                                source = host.getGridNode(null);
                            }

                            if (source == null) {
                                break out;
                            }

                            var adapter = new QioStorageAdapter<>((BlockEntity & IQIOComponent) object, arg,
                                    source.getOwningPlayerProfileId());

                            if (adapter.getFrequency() != null) {
                                return LazyOptional.of(() -> adapter).cast();
                            }
                        }

                        return LazyOptional.empty();
                    }
                });
            }
        }
    }
}
