package me.ramidzkh.mekae2.item;

import appeng.api.stacks.AEKey;
import appeng.items.storage.BasicStorageCell;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ChemicalStorageCell extends BasicStorageCell {

    public ChemicalStorageCell(Properties properties, ItemLike coreItem, ItemLike housingItem, double idleDrain, int kilobytes, int bytesPerType, int totalTypes) {
        super(properties, coreItem, housingItem, idleDrain, kilobytes, bytesPerType, totalTypes, MekanismKeyType.TYPE);
    }

    @Override
    public boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        if (requestedAddition instanceof MekanismKey key) {
            // Disallow storage cells to contain radioactive stuff
            return !ChemicalAttributeValidator.process(key.getStack(), ChemicalAttributeValidator.DEFAULT);
        }

        return true;
    }
}
