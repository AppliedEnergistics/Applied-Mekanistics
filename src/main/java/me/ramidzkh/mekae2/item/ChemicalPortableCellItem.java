package me.ramidzkh.mekae2.item;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;

import appeng.api.stacks.AEKey;
import appeng.items.storage.StorageTier;
import appeng.items.tools.powered.PortableCellItem;

public class ChemicalPortableCellItem extends PortableCellItem {

    public ChemicalPortableCellItem(int totalTypes, MenuType<?> menuType, StorageTier tier, Properties props,
            int defaultColor) {
        super(MekanismKeyType.TYPE, totalTypes, menuType, tier, props, defaultColor);
    }

    @Override
    public boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        if (requestedAddition instanceof MekanismKey key) {
            // Disallow storage cells to contain radioactive stuff
            return !ChemicalAttributeValidator.DEFAULT.process(key.getStack());
        }

        return true;
    }

    @Override
    public ResourceLocation getRecipeId() {
        return Objects.requireNonNull(getRegistryName());
    }
}
