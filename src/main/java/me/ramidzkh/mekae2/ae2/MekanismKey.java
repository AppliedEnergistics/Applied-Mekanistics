package me.ramidzkh.mekae2.ae2;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import me.ramidzkh.mekae2.util.ChemicalBridge;
import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;

public class MekanismKey extends AEKey {

    public static final byte GAS = 0;
    public static final byte INFUSION = 1;
    public static final byte PIGMENT = 2;
    public static final byte SLURRY = 3;

    private final ChemicalStack<?> stack;

    private MekanismKey(ChemicalStack<?> stack) {
        this.stack = stack;
    }

    @Nullable
    public static MekanismKey of(ChemicalStack<?> stack) {
        if (stack.isEmpty()) {
            return null;
        }

        return new MekanismKey(stack.copy());
    }

    public ChemicalStack<?> getStack() {
        return stack;
    }

    public byte getForm() {
        if (stack instanceof GasStack) {
            return GAS;
        } else if (stack instanceof InfusionStack) {
            return INFUSION;
        } else if (stack instanceof PigmentStack) {
            return PIGMENT;
        } else if (stack instanceof SlurryStack) {
            return SLURRY;
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
    public ResourceLocation getId() {
        return stack.getTypeRegistryName();
    }

    @Override
    public void writeToPacket(FriendlyByteBuf data) {
        data.writeByte(getForm());
        stack.writeToPacket(data);
    }

    @Override
    protected Component computeDisplayName() {
        return stack.getType().getTextComponent();
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {
        if (stack instanceof GasStack gasStack) {
            MekanismAPI.getRadiationManager().dumpRadiation(new Coord4D(pos, level),
                    ChemicalBridge.withAmount(gasStack, amount));
        }
    }

    @Override
    public boolean isTagged(TagKey<?> tag) {
        if (stack.getType() instanceof Gas gas) {
            return tag.registry().equals(MekanismAPI.gasRegistryName()) && gas.is((TagKey<Gas>) tag);
        } else if (stack.getType() instanceof InfuseType infuse) {
            return tag.registry().equals(MekanismAPI.infuseTypeRegistryName()) && infuse.is((TagKey<InfuseType>) tag);
        } else if (stack.getType() instanceof Pigment pigment) {
            return tag.registry().equals(MekanismAPI.pigmentRegistryName()) && pigment.is((TagKey<Pigment>) tag);
        } else if (stack.getType() instanceof Slurry slurry) {
            return tag.registry().equals(MekanismAPI.slurryRegistryName()) && slurry.is((TagKey<Slurry>) tag);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
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
