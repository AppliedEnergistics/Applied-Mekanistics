package me.ramidzkh.mekae2.ae2.impl;

import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.parts.PartModel;
import me.ramidzkh.mekae2.AE2MekanismAddons;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class P2PModels {

    public static final ResourceLocation MODEL_STATUS_OFF = AppEng.makeId("part/p2p/p2p_tunnel_status_off");
    public static final ResourceLocation MODEL_STATUS_ON = AppEng.makeId("part/p2p/p2p_tunnel_status_on");
    public static final ResourceLocation MODEL_STATUS_HAS_CHANNEL = AppEng.makeId("part/p2p/p2p_tunnel_status_has_channel");
    public static final ResourceLocation MODEL_FREQUENCY = AppEng.makeId("part/p2p/p2p_tunnel_frequency");

    private final IPartModel modelsOff;
    private final IPartModel modelsOn;
    private final IPartModel modelsHasChannel;

    public P2PModels(String frontModelPath) {
        var frontModel = AE2MekanismAddons.id(frontModelPath);

        this.modelsOff = new PartModel(MODEL_STATUS_OFF, MODEL_FREQUENCY, frontModel);
        this.modelsOn = new PartModel(MODEL_STATUS_ON, MODEL_FREQUENCY, frontModel);
        this.modelsHasChannel = new PartModel(MODEL_STATUS_HAS_CHANNEL, MODEL_FREQUENCY, frontModel);
    }

    public IPartModel getModel(boolean hasPower, boolean hasChannel) {
        if (hasPower && hasChannel) {
            return this.modelsHasChannel;
        } else if (hasPower) {
            return this.modelsOn;
        } else {
            return this.modelsOff;
        }
    }

    public List<IPartModel> getModels() {
        List<IPartModel> result = new ArrayList<>();
        result.add(this.modelsOff);
        result.add(this.modelsOn);
        result.add(this.modelsHasChannel);
        return result;
    }

}
