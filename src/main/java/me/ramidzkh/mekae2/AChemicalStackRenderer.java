package me.ramidzkh.mekae2;

import appeng.api.client.AEStackRendering;
import appeng.api.client.IAEStackRenderHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class AChemicalStackRenderer<T extends MekanismKey<?>> implements IAEStackRenderHandler<T> {

    public static void initialize(IEventBus bus) {
        bus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            AEStackRendering.register(MekanismKeyType.GAS, MekanismKey.Gas.class, new AChemicalStackRenderer<>());
            AEStackRendering.register(MekanismKeyType.INFUSION, MekanismKey.Infusion.class, new AChemicalStackRenderer<>());
            AEStackRendering.register(MekanismKeyType.PIGMENT, MekanismKey.Pigment.class, new AChemicalStackRenderer<>());
            AEStackRendering.register(MekanismKeyType.SLURRY, MekanismKey.Slurry.class, new AChemicalStackRenderer<>());
        }));
    }

    @Override
    public void drawInGui(Minecraft minecraft, PoseStack poseStack, int x, int y, int zIndex, T what) {
        minecraft.font.drawShadow(poseStack, what.getStack().getTextComponent(), x, y, 0xFF | what.getStack().getType().getColorRepresentation());
    }

    @Override
    public void drawOnBlockFace(PoseStack poseStack, MultiBufferSource buffers, T what, float scale, int combinedLight) {
        Minecraft.getInstance().font.drawInBatch(what.getStack().getTextComponent(), 0, 0, 0xFF | what.getStack().getType().getColorRepresentation(), true, poseStack.last().pose(), buffers, false, 0, combinedLight);
    }

    @Override
    public Component getDisplayName(T stack) {
        return stack.getDisplayName();
    }
}
