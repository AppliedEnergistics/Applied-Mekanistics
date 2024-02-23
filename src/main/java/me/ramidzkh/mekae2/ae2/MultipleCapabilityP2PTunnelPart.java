package me.ramidzkh.mekae2.ae2;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.neoforged.neoforge.capabilities.BlockCapability;

import appeng.api.parts.IPartItem;
import appeng.hooks.ticking.TickHandler;
import appeng.parts.p2p.P2PTunnelPart;

public class MultipleCapabilityP2PTunnelPart<P extends MultipleCapabilityP2PTunnelPart<P>> extends P2PTunnelPart<P> {

    private final Map<BlockCapability<?, Direction>, CapabilitySetInner<?, P>> capabilities;

    // Prevents recursive block updates.
    private boolean inBlockUpdate = false;
    // Prevents recursive access to the adjacent capability in case P2P input/output faces touch
    int accessDepth = 0;

    public MultipleCapabilityP2PTunnelPart(IPartItem<?> partItem,
            Function<P, Collection<CapabilitySet<?>>> capabilities) {
        super(partItem);
        var part = (P) this;
        this.capabilities = capabilities.apply(part).stream()
                .collect(Collectors.toMap(CapabilitySet::capability, set -> set.toInner(part)));
    }

    private <T> CapabilitySetInner<T, P> getSet(BlockCapability<T, Direction> capability) {
        return (CapabilitySetInner<T, P>) capabilities.get(capability);
    }

    protected <T> T getCapability(BlockCapability<T, Direction> capability) {
        var set = getSet(capability);

        if (isOutput()) {
            return set.outputHandler();
        } else {
            return set.inputHandler();
        }
    }

    /**
     * Return the capability connected to this side of this P2P connection. If this method is called again on this
     * tunnel while the returned object has not been closed, further calls to {@link CapabilityGuard#get()} will return
     * a dummy capability.
     */
    protected final <C> CapabilityGuard<C, P> getAdjacentCapability(BlockCapability<C, Direction> capability) {
        accessDepth++;
        return getSet(capability).guard();
    }

    /**
     * Returns the capability attached to the input side of this tunnel's P2P connection. If this method is called again
     * on this tunnel while the returned object has not been closed, further calls to {@link CapabilityGuard#get()} will
     * return a dummy capability.
     */
    protected final <C> CapabilityGuard<C, P> getInputCapability(BlockCapability<C, Direction> capability) {
        var input = (MultipleCapabilityP2PTunnelPart<P>) getInput();
        return input == null ? getSet(capability).empty() : input.getAdjacentCapability(capability);
    }

    /**
     * The position right in front of this P2P tunnel.
     */
    BlockPos getFacingPos() {
        return getHost().getLocation().getPos().relative(getSide());
    }

    // Send a block update on p2p status change, or any update on another endpoint.
    void sendBlockUpdate() {
        // Prevent recursive block updates.
        if (!inBlockUpdate) {
            inBlockUpdate = true;

            try {
                // getHost().notifyNeighbors() would queue a callback, but we want to do an update synchronously!
                // (otherwise we can't detect infinite recursion, it would just queue updates endlessly)
                getHost().notifyNeighborNow(getSide());
            } finally {
                inBlockUpdate = false;
            }
        }
    }

    @Override
    public void onTunnelNetworkChange() {
        // This might be invoked while the network is being unloaded and we don't want to send a block update then, so
        // we delay it until the next tick.
        TickHandler.instance().addCallable(getLevel(), () -> {
            if (getMainNode().isReady()) { // Check that the p2p tunnel is still there.
                sendBlockUpdate();
            }
        });
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

    public record CapabilitySet<C>(BlockCapability<C, Direction> capability, C inputHandler, C outputHandler,
            C emptyHandler) {
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
        private final BlockCapability<C, Direction> capability;

        public CapabilityGuard(P part, BlockCapability<C, Direction> capability, C emptyHandler) {
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
                    var cap = part.getLevel().getCapability(capability, part.getFacingPos(),
                            part.getSide().getOpposite());

                    if (cap != null) {
                        return cap;
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
    private static class EmptyCapabilityGuard<C, P extends MultipleCapabilityP2PTunnelPart<P>>
            extends CapabilityGuard<C, P> implements AutoCloseable {
        public EmptyCapabilityGuard(P part, BlockCapability<C, Direction> capability, C emptyHandler) {
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
