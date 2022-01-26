package me.ramidzkh.mekae2.item;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.items.tools.powered.PortableCellItem;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class ChemicalPortableCellItem extends PortableCellItem {

    public ChemicalPortableCellItem(AEKeyType keyType, MenuType<?> menuType, StorageTier tier, Properties props) {
        super(keyType, menuType, tier, props);
    }

    @Override
    public boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        if (requestedAddition instanceof MekanismKey<?> key) {
            // Disallow storage cells to contain radioactive stuff
            return !ChemicalAttributeValidator.process(key.getStack(), ChemicalAttributeValidator.DEFAULT);
        }

        return true;
    }
}
