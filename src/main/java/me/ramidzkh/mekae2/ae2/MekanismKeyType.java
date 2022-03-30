package me.ramidzkh.mekae2.ae2;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import me.ramidzkh.mekae2.AppliedMekanistics;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;

public class MekanismKeyType extends AEKeyType {

    public static final AEKeyType TYPE = new MekanismKeyType();

    private MekanismKeyType() {
        super(AppliedMekanistics.id("chemical"), MekanismKey.class, new TranslatableComponent("gui." + AppliedMekanistics.ID + ".chemical"));
    }

    @Nullable
    @Override
    public AEKey readFromPacket(FriendlyByteBuf input) {
        return switch (input.readByte()) {
            case 0 -> MekanismKey.of(GasStack.readFromPacket(input));
            case 1 -> MekanismKey.of(InfusionStack.readFromPacket(input));
            case 2 -> MekanismKey.of(PigmentStack.readFromPacket(input));
            case 3 -> MekanismKey.of(SlurryStack.readFromPacket(input));
            default -> null;
        };
    }

    @Nullable
    @Override
    public AEKey loadKeyFromTag(CompoundTag tag) {
        return switch (tag.getByte("t")) {
            case 0 -> MekanismKey.of(GasStack.readFromNBT(tag));
            case 1 -> MekanismKey.of(InfusionStack.readFromNBT(tag));
            case 2 -> MekanismKey.of(PigmentStack.readFromNBT(tag));
            case 3 -> MekanismKey.of(SlurryStack.readFromNBT(tag));
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
