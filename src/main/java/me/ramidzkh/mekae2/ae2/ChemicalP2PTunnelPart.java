package me.ramidzkh.mekae2.ae2;

import java.util.List;

import net.minecraftforge.common.capabilities.Capability;

import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.impl.P2PModels;
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

import appeng.api.config.PowerUnits;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;

public class ChemicalP2PTunnelPart extends MultipleCapabilityP2PTunnelPart<ChemicalP2PTunnelPart> {

    private static final P2PModels MODELS = new P2PModels("part/chemical_p2p_tunnel");

    public ChemicalP2PTunnelPart(IPartItem<?> partItem) {
        super(partItem, self -> List.of(
                new CapabilitySet<>(MekCapabilities.GAS_HANDLER_CAPABILITY, new InputChemicalHandler.OfGas(self),
                        new OutputChemicalHandler.OfGas(self), NullChemicalHandler.GAS),
                new CapabilitySet<>(MekCapabilities.INFUSION_HANDLER_CAPABILITY,
                        new InputChemicalHandler.OfInfusion(self), new OutputChemicalHandler.OfInfusion(self),
                        NullChemicalHandler.INFUSION),
                new CapabilitySet<>(MekCapabilities.PIGMENT_HANDLER_CAPABILITY,
                        new InputChemicalHandler.OfPigment(self), new OutputChemicalHandler.OfPigment(self),
                        NullChemicalHandler.PIGMENT),
                new CapabilitySet<>(MekCapabilities.SLURRY_HANDLER_CAPABILITY, new InputChemicalHandler.OfSlurry(self),
                        new OutputChemicalHandler.OfSlurry(self), NullChemicalHandler.SLURRY)));
    }

    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }

    @Override
    public IPartModel getStaticModels() {
        return MODELS.getModel(this.isPowered(), this.isActive());
    }

    private static abstract sealed class InputChemicalHandler<C extends Chemical<C>, S extends ChemicalStack<C>, H extends IChemicalHandler<C, S>>
            implements IChemicalHandler<C, S> {
        private final ChemicalP2PTunnelPart part;
        private final Capability<H> capability;

        protected InputChemicalHandler(ChemicalP2PTunnelPart part, Capability<H> capability) {
            this.part = part;
            this.capability = capability;
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
                try (var capabilityGuard = target.getAdjacentCapability(capability)) {
                    var output = capabilityGuard.get();
                    var toSend = amountPerOutput + overflow;

                    overflow = output.insertChemical(ChemicalBridge.withAmount(stack, toSend), action).getAmount();
                    total += toSend - overflow;
                }
            }

            if (action == Action.EXECUTE) {
                part.queueTunnelDrain(PowerUnits.RF, total);
            }

            return ChemicalBridge.withAmount(stack, stack.getAmount() - total);
        }

        @Override
        public S extractChemical(int tank, long maxAmount, Action action) {
            return getEmptyStack();
        }

        private static final class OfGas extends InputChemicalHandler<Gas, GasStack, IGasHandler>
                implements IGasHandler {
            private OfGas(ChemicalP2PTunnelPart part) {
                super(part, MekCapabilities.GAS_HANDLER_CAPABILITY);
            }
        }

        private static final class OfInfusion extends InputChemicalHandler<InfuseType, InfusionStack, IInfusionHandler>
                implements IInfusionHandler {
            private OfInfusion(ChemicalP2PTunnelPart part) {
                super(part, MekCapabilities.INFUSION_HANDLER_CAPABILITY);
            }
        }

        private static final class OfPigment extends InputChemicalHandler<Pigment, PigmentStack, IPigmentHandler>
                implements IPigmentHandler {
            private OfPigment(ChemicalP2PTunnelPart part) {
                super(part, MekCapabilities.PIGMENT_HANDLER_CAPABILITY);
            }
        }

        private static final class OfSlurry extends InputChemicalHandler<Slurry, SlurryStack, ISlurryHandler>
                implements ISlurryHandler {
            private OfSlurry(ChemicalP2PTunnelPart part) {
                super(part, MekCapabilities.SLURRY_HANDLER_CAPABILITY);
            }
        }
    }

    private static abstract sealed class OutputChemicalHandler<C extends Chemical<C>, S extends ChemicalStack<C>, H extends IChemicalHandler<C, S>>
            implements IChemicalHandler<C, S> {
        private final ChemicalP2PTunnelPart part;
        private final Capability<H> capability;

        private OutputChemicalHandler(ChemicalP2PTunnelPart part, Capability<H> capability) {
            this.part = part;
            this.capability = capability;
        }

        @Override
        public int getTanks() {
            try (var input = part.getInputCapability(capability)) {
                return input.get().getTanks();
            }
        }

        @Override
        public S getChemicalInTank(int tank) {
            try (var input = part.getInputCapability(capability)) {
                return input.get().getChemicalInTank(tank);
            }
        }

        @Override
        public void setChemicalInTank(int tank, S stack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getTankCapacity(int tank) {
            try (var input = part.getInputCapability(capability)) {
                return input.get().getTankCapacity(tank);
            }
        }

        @Override
        public boolean isValid(int tank, S stack) {
            try (var input = part.getInputCapability(capability)) {
                return input.get().isValid(tank, stack);
            }
        }

        @Override
        public S insertChemical(int tank, S stack, Action action) {
            return getEmptyStack();
        }

        @Override
        public S extractChemical(int tank, long maxAmount, Action action) {
            try (var input = part.getInputCapability(capability)) {
                var result = input.get().extractChemical(tank, maxAmount, action);

                if (action.execute()) {
                    part.queueTunnelDrain(PowerUnits.RF, result.getAmount());
                }

                return result;
            }
        }

        private static final class OfGas extends OutputChemicalHandler<Gas, GasStack, IGasHandler>
                implements IGasHandler {
            private OfGas(ChemicalP2PTunnelPart part) {
                super(part, MekCapabilities.GAS_HANDLER_CAPABILITY);
            }
        }

        private static final class OfInfusion extends OutputChemicalHandler<InfuseType, InfusionStack, IInfusionHandler>
                implements IInfusionHandler {
            private OfInfusion(ChemicalP2PTunnelPart part) {
                super(part, MekCapabilities.INFUSION_HANDLER_CAPABILITY);
            }
        }

        private static final class OfPigment extends OutputChemicalHandler<Pigment, PigmentStack, IPigmentHandler>
                implements IPigmentHandler {
            private OfPigment(ChemicalP2PTunnelPart part) {
                super(part, MekCapabilities.PIGMENT_HANDLER_CAPABILITY);
            }
        }

        private static final class OfSlurry extends OutputChemicalHandler<Slurry, SlurryStack, ISlurryHandler>
                implements ISlurryHandler {
            private OfSlurry(ChemicalP2PTunnelPart part) {
                super(part, MekCapabilities.SLURRY_HANDLER_CAPABILITY);
            }
        }
    }

    private static abstract sealed class NullChemicalHandler<C extends Chemical<C>, S extends ChemicalStack<C>>
            implements IChemicalHandler<C, S> {

        private static final IGasHandler GAS = new OfGas();
        private static final IInfusionHandler INFUSION = new OfInfusion();
        private static final IPigmentHandler PIGMENT = new OfPigment();
        private static final ISlurryHandler SLURRY = new OfSlurry();

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

        private static final class OfInfusion extends NullChemicalHandler<InfuseType, InfusionStack>
                implements IInfusionHandler {
        }

        private static final class OfPigment extends NullChemicalHandler<Pigment, PigmentStack>
                implements IPigmentHandler {
        }

        private static final class OfSlurry extends NullChemicalHandler<Slurry, SlurryStack> implements ISlurryHandler {
        }
    }
}
