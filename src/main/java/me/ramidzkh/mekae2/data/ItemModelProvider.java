package me.ramidzkh.mekae2.data;

import appeng.core.AppEng;
import me.ramidzkh.mekae2.AppliedMekanistics;
import me.ramidzkh.mekae2.AMItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {

    private static final ResourceLocation P2P_TUNNEL_BASE_ITEM = AppEng.makeId("item/p2p_tunnel_base");
    private static final ResourceLocation P2P_TUNNEL_BASE_PART = AppEng.makeId("part/p2p/p2p_tunnel_base");
    private static final ResourceLocation STORAGE_CELL_LED = AppEng.makeId("item/storage_cell_led");
    private static final ResourceLocation PORTABLE_CELL_LED = AppEng.makeId("item/portable_cell_led");
    private static final ResourceLocation OSMIUM_BLOCK = new ResourceLocation("mekanism", "block/block_osmium");

    public ItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, AppliedMekanistics.ID, existingFileHelper);

        existingFileHelper.trackGenerated(P2P_TUNNEL_BASE_ITEM, MODEL);
        existingFileHelper.trackGenerated(P2P_TUNNEL_BASE_PART, MODEL);
        existingFileHelper.trackGenerated(STORAGE_CELL_LED, TEXTURE);
        existingFileHelper.trackGenerated(PORTABLE_CELL_LED, TEXTURE);
        existingFileHelper.trackGenerated(OSMIUM_BLOCK, TEXTURE);
    }

    @Override
    protected void registerModels() {
        var housing = AMItems.CHEMICAL_CELL_HOUSING;
        flatSingleLayer(housing, "item/" + housing.getId().getPath());

        var creative = AMItems.CHEMICAL_CELL_CREATIVE;
        flatSingleLayer(creative, "item/" + creative.getId().getPath());

        for (var tier : AMItems.Tier.values()) {
            var cell = AMItems.get(tier);
            var portableCell = AMItems.getPortableCell(tier);
            cell(cell, "item/" + cell.getId().getPath());
            portableCell(portableCell, "item/portable_" + cell.getId().getPath());
        }

        withExistingParent("item/chemical_p2p_tunnel", P2P_TUNNEL_BASE_ITEM)
                .texture("type", OSMIUM_BLOCK);
        withExistingParent("part/chemical_p2p_tunnel", P2P_TUNNEL_BASE_PART)
                .texture("type", OSMIUM_BLOCK);
    }

    private void cell(RegistryObject<Item> cell, String background) {
        singleTexture(cell.getId().getPath(), mcLoc("item/generated"), "layer0", AppliedMekanistics.id(background))
                .texture("layer1", STORAGE_CELL_LED);
    }

    private void portableCell(RegistryObject<Item> portable, String background) {
        singleTexture(portable.getId().getPath(), mcLoc("item/generated"), "layer0", AppliedMekanistics.id(background))
                .texture("layer1", PORTABLE_CELL_LED);
    }

    private void flatSingleLayer(RegistryObject<Item> item, String texture) {
        singleTexture(item.getId().getPath(), mcLoc("item/generated"), "layer0", AppliedMekanistics.id(texture));
    }
}
