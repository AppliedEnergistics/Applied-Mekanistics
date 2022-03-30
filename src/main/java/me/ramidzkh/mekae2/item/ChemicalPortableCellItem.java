package me.ramidzkh.mekae2.item;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;

import appeng.api.stacks.AEKey;
import appeng.items.tools.powered.PortableCellItem;

public class ChemicalPortableCellItem extends PortableCellItem {

    public ChemicalPortableCellItem(MenuType<?> menuType, StorageTier tier, Properties props) {
        super(MekanismKeyType.TYPE, menuType, tier, props);
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
