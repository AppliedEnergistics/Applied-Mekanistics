package me.ramidzkh.mekae2.ae2.impl;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.helpers.iface.IInterfaceTarget;
import appeng.me.storage.ExternalStorageFacade;
import me.ramidzkh.mekae2.MekCapabilities;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
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
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Predicate;

public abstract sealed class ChemicalExternalStorageFacade<C extends Chemical<C>, S extends ChemicalStack<C>, H extends IChemicalHandler<C, S>> extends ExternalStorageFacade {

    private final H handler;

    private ChemicalExternalStorageFacade(H handler) {
        this.handler = handler;
    }

    @Nullable
    public static IInterfaceTarget get(BlockEntity be, Direction side, IActionSource src, @Nullable IInterfaceTarget in) {
        var facades = new HashMap<Predicate<AEKey>, ExternalStorageFacade>();

        be.getCapability(MekCapabilities.GAS_HANDLER_CAPABILITY, side).ifPresent(x -> facades.put(key -> key instanceof MekanismKey k && k.getStack() instanceof GasStack, new OfGas(x)));
        be.getCapability(MekCapabilities.INFUSION_HANDLER_CAPABILITY, side).ifPresent(x -> facades.put(key -> key instanceof MekanismKey k && k.getStack() instanceof InfusionStack, new OfInfusion(x)));
        be.getCapability(MekCapabilities.PIGMENT_HANDLER_CAPABILITY, side).ifPresent(x -> facades.put(key -> key instanceof MekanismKey k && k.getStack() instanceof PigmentStack, new OfPigment(x)));
        be.getCapability(MekCapabilities.SLURRY_HANDLER_CAPABILITY, side).ifPresent(x -> facades.put(key -> key instanceof MekanismKey k && k.getStack() instanceof SlurryStack, new OfSlurry(x)));

        if (facades.isEmpty()) {
            return in;
        }

        return new CascadingInterfaceTarget(in, facades, src);
    }

    private static Action action(Actionable actionable) {
        return switch (actionable) {
            case MODULATE -> Action.EXECUTE;
            case SIMULATE -> Action.SIMULATE;
        };
    }

    @Override
    public int getSlots() {
        return handler.getTanks();
    }

    @Nullable
    @Override
    public GenericStack getStackInSlot(int slot) {
        var stack = handler.getChemicalInTank(slot);
        var key = MekanismKey.of(stack);
        return key == null ? null : new GenericStack(key, stack.getAmount());
    }

    @Override
    protected int insertExternal(AEKey what, int amount, Actionable mode) {
        if (!(what instanceof MekanismKey key)) {
            return 0;
        }

        var stack = (S) ChemicalBridge.withAmount(key.getStack(), amount);
        return (int) (amount - handler.insertChemical(stack, action(mode)).getAmount());
    }

    @Override
    protected int extractExternal(AEKey what, int amount, Actionable mode) {
        if (!(what instanceof MekanismKey key)) {
            return 0;
        }

        var stack = (S) ChemicalBridge.withAmount(key.getStack(), amount);
        return (int) handler.extractChemical(stack, action(mode)).getAmount();
    }

    @Override
    public boolean containsAnyFuzzy(Set<AEKey> keys) {
        for (var i = 0; i < handler.getTanks(); i++) {
            var what = MekanismKey.of(handler.getChemicalInTank(i));

            if (what != null) {
                if (keys.contains(what)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public AEKeyType getKeyType() {
        return MekanismKeyType.TYPE;
    }

    public static final class OfGas extends ChemicalExternalStorageFacade<Gas, GasStack, IGasHandler> {
        public OfGas(IGasHandler handler) {
            super(handler);
        }
    }

    public static final class OfInfusion extends ChemicalExternalStorageFacade<InfuseType, InfusionStack, IInfusionHandler> {
        public OfInfusion(IInfusionHandler handler) {
            super(handler);
        }
    }

    public static final class OfPigment extends ChemicalExternalStorageFacade<Pigment, PigmentStack, IPigmentHandler> {
        public OfPigment(IPigmentHandler handler) {
            super(handler);
        }
    }

    public static final class OfSlurry extends ChemicalExternalStorageFacade<Slurry, SlurryStack, ISlurryHandler> {
        public OfSlurry(ISlurryHandler handler) {
            super(handler);
        }
    }
}
