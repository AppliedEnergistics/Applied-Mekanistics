package me.ramidzkh.mekae2.ae2;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import appeng.api.client.AEKeyRenderHandler;
import appeng.api.client.AEKeyRendering;
import appeng.client.gui.style.Blitter;
import appeng.util.Platform;

public class AMChemicalStackRenderer implements AEKeyRenderHandler<MekanismKey> {

    public static void initialize(IEventBus bus) {
        bus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            AEKeyRendering.register(MekanismKeyType.TYPE, MekanismKey.class, new AMChemicalStackRenderer());
        }));
    }

    @Override
    public void drawInGui(Minecraft minecraft, GuiGraphics guiGraphics, int x, int y, MekanismKey what) {
        var stack = what.getStack();

        Blitter.sprite(
                Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(stack.getType().getIcon()))
                .colorRgb(stack.getChemicalTint())
                // Most fluid texture have transparency, but we want an opaque slot
                .blending(false)
                .dest(x, y, 16, 16)
                .blit(guiGraphics);
    }

    @Override
    public void drawOnBlockFace(PoseStack poseStack, MultiBufferSource buffers, MekanismKey what, float scale,
            int combinedLight, Level level) {
        var stack = what.getStack();
        var sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(stack.getType().getIcon());
        var color = stack.getChemicalTint();

        poseStack.pushPose();
        // Push it out of the block face a bit to avoid z-fighting
        poseStack.translate(0, 0, 0.01f);

        var buffer = buffers.getBuffer(RenderType.solid());

        // In comparison to items, make it _slightly_ smaller because item icons
        // usually don't extend to the full size.
        scale -= 0.05f;

        // y is flipped here
        var x0 = -scale / 2;
        var y0 = scale / 2;
        var x1 = scale / 2;
        var y1 = -scale / 2;

        var transform = poseStack.last().pose();
        buffer.vertex(transform, x0, y1, 0)
                .color(color)
                .uv(sprite.getU0(), sprite.getV1())
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(combinedLight)
                .normal(0, 0, 1)
                .endVertex();
        buffer.vertex(transform, x1, y1, 0)
                .color(color)
                .uv(sprite.getU1(), sprite.getV1())
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(combinedLight)
                .normal(0, 0, 1)
                .endVertex();
        buffer.vertex(transform, x1, y0, 0)
                .color(color)
                .uv(sprite.getU1(), sprite.getV0())
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(combinedLight)
                .normal(0, 0, 1)
                .endVertex();
        buffer.vertex(transform, x0, y0, 0)
                .color(color)
                .uv(sprite.getU0(), sprite.getV0())
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(combinedLight)
                .normal(0, 0, 1)
                .endVertex();
        poseStack.popPose();
    }

    @Override
    public Component getDisplayName(MekanismKey stack) {
        return stack.getDisplayName();
    }

    @Override
    public List<Component> getTooltip(MekanismKey stack) {
        var tooltip = new ArrayList<Component>();
        tooltip.add(getDisplayName(stack));

        stack.getStack().getAttributes().forEach(attribute -> attribute.addTooltipText(tooltip));

        // Heuristic: If the last line doesn't include the modname, add it ourselves
        var modName = Platform.formatModName(stack.getModId());
        if (tooltip.isEmpty() || !tooltip.get(tooltip.size() - 1).getString().equals(modName)) {
            tooltip.add(Component.literal(modName));
        }

        return tooltip;
    }
}
