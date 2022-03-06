package me.ramidzkh.mekae2.ae2;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Objects;

public class MekanismKey extends AEKey {

    private final ChemicalStack<?> stack;

    private MekanismKey(ChemicalStack<?> stack) {
        this.stack = stack;
    }

    @Nullable
    public static MekanismKey of(ChemicalStack<?> stack) {
        if (stack.isEmpty()) {
            return null;
        }

        return new MekanismKey(stack);
    }

    public ChemicalStack<?> getStack() {
        return stack;
    }

    private byte getForm() {
        if (stack instanceof GasStack) {
            return 0;
        } else if (stack instanceof InfusionStack) {
            return 1;
        } else if (stack instanceof PigmentStack) {
            return 2;
        } else if (stack instanceof SlurryStack) {
            return 3;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public AEKeyType getType() {
        return MekanismKeyType.TYPE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag() {
        var tag = new CompoundTag();
        tag.putByte("t", getForm());
        return stack.write(tag);
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
        data.writeByte(getForm());
        stack.writeToPacket(data);
    }

    @Override
    public Component getDisplayName() {
        return stack.getType().getTextComponent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (MekanismKey) o;
        return Objects.equals(stack.getType(), that.stack.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack.getType());
    }

    @Override
    public String toString() {
        return "MekanismKey{" +
                "stack=" + stack.getType() +
                '}';
    }
}
