package me.ramidzkh.mekae2.ae2;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;

import me.ramidzkh.mekae2.AppliedMekanistics;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;

public class MekanismKeyType extends AEKeyType {

    public static final AEKeyType TYPE = new MekanismKeyType();

    private MekanismKeyType() {
        super(AppliedMekanistics.id("chemical"), MekanismKey.class,
                new TranslatableComponent("gui." + AppliedMekanistics.ID + ".chemical"));
    }

    @Nullable
    @Override
    public AEKey readFromPacket(FriendlyByteBuf input) {
        return switch (input.readByte()) {
            case MekanismKey.GAS -> MekanismKey.of(GasStack.readFromPacket(input));
            case MekanismKey.INFUSION -> MekanismKey.of(InfusionStack.readFromPacket(input));
            case MekanismKey.PIGMENT -> MekanismKey.of(PigmentStack.readFromPacket(input));
            case MekanismKey.SLURRY -> MekanismKey.of(SlurryStack.readFromPacket(input));
            default -> null;
        };
    }

    @Nullable
    @Override
    public AEKey loadKeyFromTag(CompoundTag tag) {
        return switch (tag.getByte("t")) {
            case MekanismKey.GAS -> MekanismKey.of(GasStack.readFromNBT(tag));
            case MekanismKey.INFUSION -> MekanismKey.of(InfusionStack.readFromNBT(tag));
            case MekanismKey.PIGMENT -> MekanismKey.of(PigmentStack.readFromNBT(tag));
            case MekanismKey.SLURRY -> MekanismKey.of(SlurryStack.readFromNBT(tag));
            default -> null;
        };
    }

    // Copied from AEFluidKeys
    @Override
    public int getAmountPerOperation() {
        return AEFluidKey.AMOUNT_BUCKET * 125 / 1000;
    }

    // Copied from AEFluidKeys
    @Override
    public int getAmountPerByte() {
        return 8 * AEFluidKey.AMOUNT_BUCKET;
    }

    // Copied from AEFluidKeys
    @Override
    public int getAmountPerUnit() {
        return AEFluidKey.AMOUNT_BUCKET;
    }

    // Copied from AEFluidKeys
    @Override
    public String getUnitSymbol() {
        return "B";
    }
}
