package me.ramidzkh.mekae2.ae2;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class MekanismKey<S extends ChemicalStack<?>> extends AEKey {

    private final AEKeyType type;
    private final S stack;

    private MekanismKey(AEKeyType type, S stack) {
        this.type = type;
        this.stack = stack;
    }

    public static class Gas extends MekanismKey<GasStack> {
        private Gas(GasStack stack) {
            super(MekanismKeyType.GAS, stack);
        }

        public static MekanismKey.Gas fromPacket(FriendlyByteBuf input) {
            return new MekanismKey.Gas(GasStack.readFromPacket(input));
        }

        public static MekanismKey.Gas fromTag(CompoundTag tag) {
            return new MekanismKey.Gas(GasStack.readFromNBT(tag));
        }
    }

    public static class Infusion extends MekanismKey<InfusionStack> {
        private Infusion(InfusionStack stack) {
            super(MekanismKeyType.INFUSION, stack);
        }

        public static MekanismKey.Infusion fromPacket(FriendlyByteBuf input) {
            return new MekanismKey.Infusion(InfusionStack.readFromPacket(input));
        }

        public static MekanismKey.Infusion fromTag(CompoundTag tag) {
            return new MekanismKey.Infusion(InfusionStack.readFromNBT(tag));
        }
    }

    public static class Pigment extends MekanismKey<PigmentStack> {
        private Pigment(PigmentStack stack) {
            super(MekanismKeyType.PIGMENT, stack);
        }

        public static MekanismKey.Pigment fromPacket(FriendlyByteBuf input) {
            return new MekanismKey.Pigment(PigmentStack.readFromPacket(input));
        }

        public static MekanismKey.Pigment fromTag(CompoundTag tag) {
            return new MekanismKey.Pigment(PigmentStack.readFromNBT(tag));
        }
    }

    public static class Slurry extends MekanismKey<SlurryStack> {
        private Slurry(SlurryStack stack) {
            super(MekanismKeyType.SLURRY, stack);
        }

        public static MekanismKey.Slurry fromPacket(FriendlyByteBuf input) {
            return new MekanismKey.Slurry(SlurryStack.readFromPacket(input));
        }

        public static MekanismKey.Slurry fromTag(CompoundTag tag) {
            return new MekanismKey.Slurry(SlurryStack.readFromNBT(tag));
        }
    }

    @Override
    public AEKeyType getType() {
        return type;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag() {
        return stack.write(new CompoundTag());
    }

    @Override
    public Object getPrimaryKey() {
        return stack.getType();
    }

    @Override
    public String getModId() {
        return stack.getTypeRegistryName().getNamespace();
    }

    @Override
    public void writeToPacket(FriendlyByteBuf data) {
        stack.writeToPacket(data);
    }

    @Override
    public ItemStack wrapForDisplayOrFilter() {
        return wrap(0);
    }

    @Override
    public ItemStack wrap(int amount) {
        return GenericStack.wrapInItemStack(this, amount);
    }

    @Override
    public Component getDisplayName() {
        return stack.getType().getTextComponent();
    }
}
