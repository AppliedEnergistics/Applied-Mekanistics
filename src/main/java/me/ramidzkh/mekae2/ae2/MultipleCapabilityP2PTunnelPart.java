package me.ramidzkh.mekae2.ae2;

import appeng.api.parts.IPartItem;
import appeng.parts.p2p.P2PTunnelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultipleCapabilityP2PTunnelPart<P extends MultipleCapabilityP2PTunnelPart<P>> extends P2PTunnelPart<P> {

    private final Map<Capability<?>, CapabilitySetInner<?, P>> capabilities;
    // Prevents recursive access to the adjacent capability in case P2P input/output faces touch
    int accessDepth = 0;
    // Prevents recursive block updates.
    private boolean inBlockUpdate = false;

    public MultipleCapabilityP2PTunnelPart(IPartItem<?> partItem, Function<P, Collection<CapabilitySet<?>>> capabilities) {
        super(partItem);
        this.capabilities = capabilities.apply((P) this).stream()
                .collect(Collectors.toMap(CapabilitySet::capability, set -> set.toInner((P) this)));
    }

    @Override
    protected float getPowerDrainPerTick() {
        return 2.0f;
    }

    public final <T> LazyOptional<T> getCapability(Capability<T> capability) {
        var set = capabilities.get(capability);

        if (set != null) {
            if (isOutput()) {
                return LazyOptional.of(set::outputHandler).cast();
            } else {
                return LazyOptional.of(set::inputHandler).cast();
            }
        }

        return LazyOptional.empty();
    }

    /**
     * Return the capability connected to this side of this P2P connection. If this method is called again on this
     * tunnel while the returned object has not been closed, further calls to {@link CapabilityGuard#get()} will return
     * a dummy capability.
     */
    protected final <C> CapabilityGuard<C, P> getAdjacentCapability(Capability<C> capability) {
        accessDepth++;
        return (CapabilityGuard<C, P>) capabilities.get(capability).guard();
    }

    /**
     * Returns the capability attached to the input side of this tunnel's P2P connection. If this method is called again
     * on this tunnel while the returned object has not been closed, further calls to {@link CapabilityGuard#get()} will
     * return a dummy capability.
     */
    protected final <C> CapabilityGuard<C, P> getInputCapability(Capability<C> capability) {
        var input = getInput();
        return input == null ? (CapabilityGuard<C, P>) capabilities.get(capability).empty() : input.getAdjacentCapability(capability);
    }

    /**
     * The position right in front of this P2P tunnel.
     */
    BlockPos getFacingPos() {
        return getHost().getLocation().getPos().relative(getSide());
    }

    // Send a block update on p2p status change, or any update on another endpoint.
    protected void sendBlockUpdate() {
        // Prevent recursive block updates.
        if (!inBlockUpdate) {
            inBlockUpdate = true;

            try {
                // getHost().notifyNeighbors() would queue a callback, but we want to do an update synchronously!
                // (otherwise we can't detect infinite recursion, it would just queue updates endlessly)
                var self = getBlockEntity();
                self.getLevel().updateNeighborsAt(self.getBlockPos(), Blocks.AIR);
            } finally {
                inBlockUpdate = false;
            }
        }
    }

    @Override
    public void onTunnelNetworkChange() {
        sendBlockUpdate();
    }

    /**
     * Forward block updates from the attached tile's position to the other end of the tunnel. Required for TE's on the
     * other end to know that the available caps may have changed.
     */
    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        // We only care about block updates on the side this tunnel is facing
        if (!getFacingPos().equals(neighbor)) {
            return;
        }

        // Prevent recursive block updates.
        if (!inBlockUpdate) {
            inBlockUpdate = true;

            try {
                if (isOutput()) {
                    var input = getInput();

                    if (input != null) {
                        input.sendBlockUpdate();
                    }
                } else {
                    for (var output : getOutputs()) {
                        output.sendBlockUpdate();
                    }
                }
            } finally {
                inBlockUpdate = false;
            }
        }
    }

    public record CapabilitySet<C>(Capability<C> capability, C inputHandler, C outputHandler, C emptyHandler) {
        private <P extends MultipleCapabilityP2PTunnelPart<P>> CapabilitySetInner<C, P> toInner(P part) {
            return new CapabilitySetInner<>(new CapabilityGuard<>(part, capability(), emptyHandler()),
                    new EmptyCapabilityGuard<>(part, capability(), emptyHandler()),
                    inputHandler(),
                    outputHandler());
        }
    }

    private record CapabilitySetInner<C, P extends MultipleCapabilityP2PTunnelPart<P>>(CapabilityGuard<C, P> guard,
                                                                                       CapabilityGuard<C, P> empty,
                                                                                       C inputHandler,
                                                                                       C outputHandler) {
    }

    protected static class CapabilityGuard<C, P extends MultipleCapabilityP2PTunnelPart<P>> implements AutoCloseable {
        protected final C emptyHandler;
        private final P part;
        private final Capability<C> capability;

        public CapabilityGuard(P part, Capability<C> capability, C emptyHandler) {
            this.part = part;
            this.capability = capability;
            this.emptyHandler = emptyHandler;
        }

        /**
         * Get the capability, or a null handler if not available. Use within the scope of the enclosing AdjCapability.
         */
        public C get() {
            if (part.accessDepth == 0) {
                throw new IllegalStateException("get was called after closing the wrapper");
            } else if (part.accessDepth == 1) {
                if (part.isActive()) {
                    var self = part.getBlockEntity();
                    var te = self.getLevel().getBlockEntity(part.getFacingPos());

                    if (te != null) {
                        return te.getCapability(capability, part.getSide().getOpposite())
                                .orElse(emptyHandler);
                    }
                }

                return emptyHandler;
            } else {
                // This capability is already in use (as the nesting is > 1), so we return an empty handler to prevent
                // infinite recursion.
                return emptyHandler;
            }
        }

        @Override
        public void close() {
            if (--part.accessDepth < 0) {
                throw new IllegalStateException("Close has been called multiple times");
            }
        }
    }

    /**
     * This specialization is used when the tunnel is not connected.
     */
    private static class EmptyCapabilityGuard<C, P extends MultipleCapabilityP2PTunnelPart<P>> extends CapabilityGuard<C, P> implements AutoCloseable {
        public EmptyCapabilityGuard(P part, Capability<C> capability, C emptyHandler) {
            super(part, capability, emptyHandler);
        }

        @Override
        public void close() {
        }

        @Override
        public C get() {
            return emptyHandler;
        }
    }
}
