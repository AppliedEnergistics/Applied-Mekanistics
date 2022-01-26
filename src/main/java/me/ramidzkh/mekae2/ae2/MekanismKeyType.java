package me.ramidzkh.mekae2.ae2;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import me.ramidzkh.mekae2.AE2MekanismAddons;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;
import java.util.function.Function;

public class MekanismKeyType<K extends AEKey> extends AEKeyType {

    public static final AEKeyType GAS = new MekanismKeyType<>("gas", MekanismKey.Gas.class, MekanismKey.Gas::fromPacket, MekanismKey.Gas::fromTag);
    public static final AEKeyType INFUSION = new MekanismKeyType<>("infusion", MekanismKey.Infusion.class, MekanismKey.Infusion::fromPacket, MekanismKey.Infusion::fromTag);
    public static final AEKeyType PIGMENT = new MekanismKeyType<>("pigment", MekanismKey.Pigment.class, MekanismKey.Pigment::fromPacket, MekanismKey.Pigment::fromTag);
    public static final AEKeyType SLURRY = new MekanismKeyType<>("slurry", MekanismKey.Slurry.class, MekanismKey.Slurry::fromPacket, MekanismKey.Slurry::fromTag);

    private final Function<FriendlyByteBuf, K> fromPacket;
    private final Function<CompoundTag, K> fromTag;

    private MekanismKeyType(String id, Class<K> key, Function<FriendlyByteBuf, K> fromPacket, Function<CompoundTag, K> fromTag) {
        super(AE2MekanismAddons.id(id), key, new TranslatableComponent("gui." + AE2MekanismAddons.ID + "." + id));
        this.fromPacket = fromPacket;
        this.fromTag = fromTag;
    }

    @Nullable
    @Override
    public AEKey readFromPacket(FriendlyByteBuf input) {
        return fromPacket.apply(input);
    }

    @Nullable
    @Override
    public AEKey loadKeyFromTag(CompoundTag tag) {
        return fromTag.apply(tag);
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
