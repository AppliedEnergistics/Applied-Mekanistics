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

import javax.annotation.Nullable;
import java.util.Objects;

public abstract sealed class MekanismKey<S extends ChemicalStack<?>> extends AEKey {

    private final AEKeyType type;
    private final S stack;

    private MekanismKey(AEKeyType type, S stack) {
        this.type = type;
        this.stack = stack;
    }

    public S getStack() {
        return stack;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (MekanismKey<?>) o;
        return Objects.equals(type, that.type) && Objects.equals(stack.getType(), that.stack.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, stack.getType());
    }

    @Override
    public String toString() {
        return "MekanismKey{" +
                "type=" + type +
                ", stack=" + stack +
                '}';
    }

    public static final class Gas extends MekanismKey<GasStack> {
        private Gas(GasStack stack) {
            super(MekanismKeyType.GAS, stack);
        }

        @Nullable
        public static MekanismKey.Gas of(GasStack stack) {
            if (stack.isEmpty()) {
                return null;
            }

            return new MekanismKey.Gas(stack);
        }

        public static MekanismKey.Gas fromPacket(FriendlyByteBuf input) {
            return new MekanismKey.Gas(GasStack.readFromPacket(input));
        }

        public static MekanismKey.Gas fromTag(CompoundTag tag) {
            return new MekanismKey.Gas(GasStack.readFromNBT(tag));
        }
    }

    public static final class Infusion extends MekanismKey<InfusionStack> {
        private Infusion(InfusionStack stack) {
            super(MekanismKeyType.INFUSION, stack);
        }

        @Nullable
        public static MekanismKey.Infusion of(InfusionStack stack) {
            if (stack.isEmpty()) {
                return null;
            }

            return new MekanismKey.Infusion(stack);
        }

        public static MekanismKey.Infusion fromPacket(FriendlyByteBuf input) {
            return new MekanismKey.Infusion(InfusionStack.readFromPacket(input));
        }

        public static MekanismKey.Infusion fromTag(CompoundTag tag) {
            return new MekanismKey.Infusion(InfusionStack.readFromNBT(tag));
        }
    }

    public static final class Pigment extends MekanismKey<PigmentStack> {
        private Pigment(PigmentStack stack) {
            super(MekanismKeyType.PIGMENT, stack);
        }

        @Nullable
        public static MekanismKey.Pigment of(PigmentStack stack) {
            if (stack.isEmpty()) {
                return null;
            }

            return new MekanismKey.Pigment(stack);
        }

        public static MekanismKey.Pigment fromPacket(FriendlyByteBuf input) {
            return new MekanismKey.Pigment(PigmentStack.readFromPacket(input));
        }

        public static MekanismKey.Pigment fromTag(CompoundTag tag) {
            return new MekanismKey.Pigment(PigmentStack.readFromNBT(tag));
        }
    }

    public static final class Slurry extends MekanismKey<SlurryStack> {
        private Slurry(SlurryStack stack) {
            super(MekanismKeyType.SLURRY, stack);
        }

        @Nullable
        public static MekanismKey.Slurry of(SlurryStack stack) {
            if (stack.isEmpty()) {
                return null;
            }

            return new MekanismKey.Slurry(stack);
        }

        public static MekanismKey.Slurry fromPacket(FriendlyByteBuf input) {
            return new MekanismKey.Slurry(SlurryStack.readFromPacket(input));
        }

        public static MekanismKey.Slurry fromTag(CompoundTag tag) {
            return new MekanismKey.Slurry(SlurryStack.readFromNBT(tag));
        }
    }
}
