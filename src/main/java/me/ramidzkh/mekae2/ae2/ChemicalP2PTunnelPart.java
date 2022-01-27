package me.ramidzkh.mekae2.ae2;

import appeng.api.config.PowerUnits;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.p2p.CapabilityP2PTunnelPart;
import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.impl.P2PModels;
import me.ramidzkh.mekae2.mixin.CapabilityGuardAccessor;
import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraftforge.common.capabilities.Capability;

import java.util.List;

public abstract sealed class ChemicalP2PTunnelPart<C extends Chemical<C>, S extends ChemicalStack<C>, H extends IChemicalHandler<C, S>, SELF extends ChemicalP2PTunnelPart<C, S, H, SELF>> extends CapabilityP2PTunnelPart<SELF, H> implements ChemicalBridge<S> {

    private static final P2PModels MODELS = new P2PModels("part/p2p/p2p_tunnel_chemical");

    private ChemicalP2PTunnelPart(IPartItem<?> partItem, Capability<H> capability) {
        super(partItem, capability);
    }

    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }

    @Override
    public IPartModel getStaticModels() {
        return MODELS.getModel(this.isPowered(), this.isActive());
    }

    protected void init(H inputHandler, H outputHandler, H emptyHandler) {
        this.inputHandler = inputHandler;
        this.outputHandler = outputHandler;
        this.emptyHandler = emptyHandler;
    }

    private H get(CapabilityGuard guard) {
        return ((CapabilityGuardAccessor<H>) guard).callGet();
    }

    public static final class GasP2PTunnelPart extends ChemicalP2PTunnelPart<Gas, GasStack, IGasHandler, GasP2PTunnelPart> implements ChemicalBridge.OfGas {
        public GasP2PTunnelPart(IPartItem<?> partItem) {
            super(partItem, MekCapabilities.GAS_HANDLER_CAPABILITY);
            init(new InputChemicalHandler.OfGas(this), new OutputChemicalHandler.OfGas(this), new NullChemicalHandler.OfGas());
        }
    }

    public static final class InfusionP2PTunnelPart extends ChemicalP2PTunnelPart<InfuseType, InfusionStack, IInfusionHandler, InfusionP2PTunnelPart> implements ChemicalBridge.OfInfusion {
        public InfusionP2PTunnelPart(IPartItem<?> partItem) {
            super(partItem, MekCapabilities.INFUSION_HANDLER_CAPABILITY);
            init(new InputChemicalHandler.OfInfusion(this), new OutputChemicalHandler.OfInfusion(this), new NullChemicalHandler.OfInfusion());
        }
    }

    public static final class PigmentP2PTunnelPart extends ChemicalP2PTunnelPart<Pigment, PigmentStack, IPigmentHandler, PigmentP2PTunnelPart> implements ChemicalBridge.OfPigment {
        public PigmentP2PTunnelPart(IPartItem<?> partItem) {
            super(partItem, MekCapabilities.PIGMENT_HANDLER_CAPABILITY);
            init(new InputChemicalHandler.OfPigment(this), new OutputChemicalHandler.OfPigment(this), new NullChemicalHandler.OfPigment());
        }
    }

    public static final class SlurryP2PTunnelPart extends ChemicalP2PTunnelPart<Slurry, SlurryStack, ISlurryHandler, SlurryP2PTunnelPart> implements ChemicalBridge.OfSlurry {
        public SlurryP2PTunnelPart(IPartItem<?> partItem) {
            super(partItem, MekCapabilities.SLURRY_HANDLER_CAPABILITY);
            init(new InputChemicalHandler.OfSlurry(this), new OutputChemicalHandler.OfSlurry(this), new NullChemicalHandler.OfSlurry());
        }
    }

    private static abstract sealed class InputChemicalHandler<C extends Chemical<C>, S extends ChemicalStack<C>, H extends IChemicalHandler<C, S>, P extends ChemicalP2PTunnelPart<C, S, H, P>> implements IChemicalHandler<C, S> {
        private final ChemicalP2PTunnelPart<C, S, H, P> part;

        protected InputChemicalHandler(ChemicalP2PTunnelPart<C, S, H, P> part) {
            this.part = part;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public S getChemicalInTank(int tank) {
            return getEmptyStack();
        }

        @Override
        public void setChemicalInTank(int tank, S stack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getTankCapacity(int tank) {
            return Long.MAX_VALUE;
        }

        @Override
        public boolean isValid(int tank, S stack) {
            return true;
        }

        @Override
        public S insertChemical(int tank, S stack, Action action) {
            var total = 0;

            var outputTunnels = part.getOutputs().size();
            var amount = stack.getAmount();

            if (outputTunnels == 0 || amount == 0) {
                return stack;
            }

            var amountPerOutput = amount / outputTunnels;
            var overflow = amountPerOutput == 0 ? amount : amount % amountPerOutput;

            for (var target : part.getOutputs()) {
                try (var capabilityGuard = target.getAdjacentCapability()) {
                    var output = part.get(capabilityGuard);
                    var toSend = amountPerOutput + overflow;

                    overflow = output.insertChemical(part.withAmount(stack, toSend), action).getAmount();
                    total += toSend - overflow;
                }
            }

            if (action == Action.EXECUTE) {
                part.queueTunnelDrain(PowerUnits.RF, total);
            }

            return part.withAmount(stack, stack.getAmount() - total);
        }

        @Override
        public S extractChemical(int tank, long maxAmount, Action action) {
            return getEmptyStack();
        }

        private static final class OfGas extends InputChemicalHandler<Gas, GasStack, IGasHandler, GasP2PTunnelPart> implements IGasHandler {
            private OfGas(ChemicalP2PTunnelPart<Gas, GasStack, IGasHandler, GasP2PTunnelPart> part) {
                super(part);
            }
        }

        private static final class OfInfusion extends InputChemicalHandler<InfuseType, InfusionStack, IInfusionHandler, InfusionP2PTunnelPart> implements IInfusionHandler {
            private OfInfusion(ChemicalP2PTunnelPart<InfuseType, InfusionStack, IInfusionHandler, InfusionP2PTunnelPart> part) {
                super(part);
            }
        }

        private static final class OfPigment extends InputChemicalHandler<Pigment, PigmentStack, IPigmentHandler, PigmentP2PTunnelPart> implements IPigmentHandler {
            private OfPigment(ChemicalP2PTunnelPart<Pigment, PigmentStack, IPigmentHandler, PigmentP2PTunnelPart> part) {
                super(part);
            }
        }

        private static final class OfSlurry extends InputChemicalHandler<Slurry, SlurryStack, ISlurryHandler, SlurryP2PTunnelPart> implements ISlurryHandler {
            private OfSlurry(ChemicalP2PTunnelPart<Slurry, SlurryStack, ISlurryHandler, SlurryP2PTunnelPart> part) {
                super(part);
            }
        }
    }

    private static abstract sealed class OutputChemicalHandler<C extends Chemical<C>, S extends ChemicalStack<C>, H extends IChemicalHandler<C, S>, P extends ChemicalP2PTunnelPart<C, S, H, P>> implements IChemicalHandler<C, S> {
        private final ChemicalP2PTunnelPart<C, S, H, P> part;

        private OutputChemicalHandler(ChemicalP2PTunnelPart<C, S, H, P> part) {
            this.part = part;
        }

        @Override
        public int getTanks() {
            try (var input = part.getInputCapability()) {
                return part.get(input).getTanks();
            }
        }

        @Override
        public S getChemicalInTank(int tank) {
            try (var input = part.getInputCapability()) {
                return part.get(input).getChemicalInTank(tank);
            }
        }

        @Override
        public void setChemicalInTank(int tank, S stack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getTankCapacity(int tank) {
            try (var input = part.getInputCapability()) {
                return part.get(input).getTankCapacity(tank);
            }
        }

        @Override
        public boolean isValid(int tank, S stack) {
            try (var input = part.getInputCapability()) {
                return part.get(input).isValid(tank, stack);
            }
        }

        @Override
        public S insertChemical(int tank, S stack, Action action) {
            return getEmptyStack();
        }

        @Override
        public S extractChemical(int tank, long maxAmount, Action action) {
            try (var input = part.getInputCapability()) {
                var result = part.get(input).extractChemical(tank, maxAmount, action);

                if (action.execute()) {
                    part.queueTunnelDrain(PowerUnits.RF, result.getAmount());
                }

                return result;
            }
        }

        private static final class OfGas extends OutputChemicalHandler<Gas, GasStack, IGasHandler, GasP2PTunnelPart> implements IGasHandler {
            private OfGas(ChemicalP2PTunnelPart<Gas, GasStack, IGasHandler, GasP2PTunnelPart> part) {
                super(part);
            }
        }

        private static final class OfInfusion extends OutputChemicalHandler<InfuseType, InfusionStack, IInfusionHandler, InfusionP2PTunnelPart> implements IInfusionHandler {
            private OfInfusion(ChemicalP2PTunnelPart<InfuseType, InfusionStack, IInfusionHandler, InfusionP2PTunnelPart> part) {
                super(part);
            }
        }

        private static final class OfPigment extends OutputChemicalHandler<Pigment, PigmentStack, IPigmentHandler, PigmentP2PTunnelPart> implements IPigmentHandler {
            private OfPigment(ChemicalP2PTunnelPart<Pigment, PigmentStack, IPigmentHandler, PigmentP2PTunnelPart> part) {
                super(part);
            }
        }

        private static final class OfSlurry extends OutputChemicalHandler<Slurry, SlurryStack, ISlurryHandler, SlurryP2PTunnelPart> implements ISlurryHandler {
            private OfSlurry(ChemicalP2PTunnelPart<Slurry, SlurryStack, ISlurryHandler, SlurryP2PTunnelPart> part) {
                super(part);
            }
        }
    }

    private static abstract sealed class NullChemicalHandler<C extends Chemical<C>, S extends ChemicalStack<C>> implements IChemicalHandler<C, S> {

        @Override
        public int getTanks() {
            return 0;
        }

        @Override
        public S getChemicalInTank(int tank) {
            return getEmptyStack();
        }

        @Override
        public void setChemicalInTank(int tank, S stack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getTankCapacity(int tank) {
            return 0;
        }

        @Override
        public boolean isValid(int tank, S stack) {
            return false;
        }

        @Override
        public S insertChemical(int tank, S stack, Action action) {
            return stack;
        }

        @Override
        public S extractChemical(int tank, long maxAmount, Action action) {
            return getEmptyStack();
        }

        private static final class OfGas extends NullChemicalHandler<Gas, GasStack> implements IGasHandler {
        }

        private static final class OfInfusion extends NullChemicalHandler<InfuseType, InfusionStack> implements IInfusionHandler {
        }

        private static final class OfPigment extends NullChemicalHandler<Pigment, PigmentStack> implements IPigmentHandler {
        }

        private static final class OfSlurry extends NullChemicalHandler<Slurry, SlurryStack> implements ISlurryHandler {
        }
    }
}
