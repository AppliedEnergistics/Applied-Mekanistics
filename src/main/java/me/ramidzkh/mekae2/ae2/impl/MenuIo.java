package me.ramidzkh.mekae2.ae2.impl;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.menu.AEBaseMenu;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.util.ChemicalBridge;
import me.ramidzkh.mekae2.util.ChemicalUtil;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class MenuIo {

    private static long extract(Object accessor, long amount, Actionable mode) {
        try {
            var c = Class.forName("appeng.menu.AEBaseMenu$FillingSource");
            var m = c.getDeclaredMethod("extract", long.class, Actionable.class);
            m.setAccessible(true);
            return (long) m.invoke(accessor, amount, mode);
        } catch (Throwable e) {
            return 0;
        }
    }

    private static long insert(Object accessor, AEKey what, long amount, Actionable mode) {
        try {
            var c = Class.forName("appeng.menu.AEBaseMenu$EmptyingSink");
            var m = c.getDeclaredMethod("insert", AEKey.class, long.class, Actionable.class);
            m.setAccessible(true);
            return (long) m.invoke(accessor, what, amount, mode);
        } catch (Throwable e) {
            return 0;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean fill(AEBaseMenu menu, Object source, MekanismKey what) {
        IChemicalHandler sourceHandler;
        ChemicalUtil util;

        var stack = what.getStack();

        // Use switch feature once out of preview
        if (stack instanceof GasStack) {
            sourceHandler = new FillingChemicalHandler.OfGas(what, source);
            util = ChemicalUtil.GAS;
        } else if (stack instanceof InfusionStack) {
            sourceHandler = new FillingChemicalHandler.OfInfusion(what, source);
            util = ChemicalUtil.INFUSION;
        } else if (stack instanceof PigmentStack) {
            sourceHandler = new FillingChemicalHandler.OfPigment(what, source);
            util = ChemicalUtil.PIGMENT;
        } else if (stack instanceof SlurryStack) {
            sourceHandler = new FillingChemicalHandler.OfSlurry(what, source);
            util = ChemicalUtil.SLURRY;
        } else {
            throw new UnsupportedOperationException();
        }

        var playerInv = menu.getPlayer().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElse(EmptyHandler.INSTANCE);

        var result = util.tryFillContainerAndStow(
                menu.getCarried(),
                sourceHandler,
                playerInv,
                Long.MAX_VALUE,
                menu.getPlayer(),
                true
        );

        if (result.isSuccess()) {
            menu.setCarried(result.getResult());
            return true;
        }

        return false;
    }

    public static boolean empty(AEBaseMenu menu, Object sink) {
        // Binary OR is intentional, try dumping all the types of chemicals
        return emptyInternal(menu, new EmptyingChemicalHandler.OfGas(sink), ChemicalUtil.GAS)
                | emptyInternal(menu, new EmptyingChemicalHandler.OfInfusion(sink), ChemicalUtil.INFUSION)
                | emptyInternal(menu, new EmptyingChemicalHandler.OfPigment(sink), ChemicalUtil.PIGMENT)
                | emptyInternal(menu, new EmptyingChemicalHandler.OfSlurry(sink), ChemicalUtil.SLURRY);
    }

    private static <C extends Chemical<C>, S extends ChemicalStack<C>, H extends IChemicalHandler<C, S>> boolean emptyInternal(AEBaseMenu menu, H sinkHandler, ChemicalUtil<C, S, H> util) {
        var playerInv = menu.getPlayer().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElse(EmptyHandler.INSTANCE);

        var result = util.tryEmptyContainerAndStow(
                menu.getCarried(),
                sinkHandler,
                playerInv,
                Long.MAX_VALUE,
                menu.getPlayer(),
                true);

        if (result.isSuccess()) {
            menu.setCarried(result.getResult());
            return true;
        }

        return false;
    }

    private static abstract sealed class FillingChemicalHandler<C extends Chemical<C>, S extends ChemicalStack<C>> implements IChemicalHandler<C, S> {
        private final MekanismKey what;
        private final Object source;

        private FillingChemicalHandler(MekanismKey what, Object source) {
            this.what = what;
            this.source = source;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public S getChemicalInTank(int tank) {
            return (S) what.getStack();
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
            return stack.getType().equals(what.getStack().getType());
        }

        @Override
        public S insertChemical(int tank, S stack, Action action) {
            return stack;
        }

        @Override
        public S extractChemical(int tank, long maxAmount, Action action) {
            var extracted = extract(source, maxAmount, GenericStackChemicalStorage.actionable(action));
            return (S) ChemicalBridge.withAmount(what.getStack(), extracted);
        }

        public static final class OfGas extends FillingChemicalHandler<Gas, GasStack> implements IGasHandler {
            public OfGas(MekanismKey what, Object source) {
                super(what, source);
            }
        }

        public static final class OfInfusion extends FillingChemicalHandler<InfuseType, InfusionStack> implements IInfusionHandler {
            public OfInfusion(MekanismKey what, Object source) {
                super(what, source);
            }
        }

        public static final class OfPigment extends FillingChemicalHandler<Pigment, PigmentStack> implements IPigmentHandler {
            public OfPigment(MekanismKey what, Object source) {
                super(what, source);
            }
        }

        public static final class OfSlurry extends FillingChemicalHandler<Slurry, SlurryStack> implements ISlurryHandler {
            public OfSlurry(MekanismKey what, Object source) {
                super(what, source);
            }
        }
    }

    private static abstract sealed class EmptyingChemicalHandler<C extends Chemical<C>, S extends ChemicalStack<C>> implements IChemicalHandler<C, S> {
        private final Object sink;

        public EmptyingChemicalHandler(Object sink) {
            this.sink = sink;
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
            var gasKey = MekanismKey.of(stack);

            if (gasKey != null) {
                return ChemicalBridge.withAmount(stack, stack.getAmount() - insert(sink, gasKey, stack.getAmount(), GenericStackChemicalStorage.actionable(action)));
            }

            return stack;
        }

        @Override
        public S extractChemical(int tank, long maxAmount, Action action) {
            return getEmptyStack();
        }

        public static final class OfGas extends EmptyingChemicalHandler<Gas, GasStack> implements IGasHandler {
            public OfGas(Object source) {
                super(source);
            }
        }

        public static final class OfInfusion extends EmptyingChemicalHandler<InfuseType, InfusionStack> implements IInfusionHandler {
            public OfInfusion(Object source) {
                super(source);
            }
        }

        public static final class OfPigment extends EmptyingChemicalHandler<Pigment, PigmentStack> implements IPigmentHandler {
            public OfPigment(Object source) {
                super(source);
            }
        }

        public static final class OfSlurry extends EmptyingChemicalHandler<Slurry, SlurryStack> implements ISlurryHandler {
            public OfSlurry(Object source) {
                super(source);
            }
        }
    }
}
