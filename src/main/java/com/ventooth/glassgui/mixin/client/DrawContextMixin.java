package com.ventooth.glassgui.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ventooth.glassgui.GlassGUI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static org.spongepowered.asm.mixin.injection.At.Shift;

@Mixin(DrawContext.class)
@Environment(EnvType.CLIENT)
public abstract class DrawContextMixin {
    @Unique
    private boolean enabledBlend;

    @Inject(at = @At(value = "INVOKE",
                     target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V",
                     shift = Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD,
            method = "drawTexturedQuad(Lnet/minecraft/util/Identifier;IIIIIFFFF)V")
    private void enableBlend(Identifier texture,
                             int x1,
                             int x2,
                             int y1,
                             int y2,
                             int z,
                             float u1,
                             float u2,
                             float v1,
                             float v2,
                             CallbackInfo ci) {
        if (GlassGUI.isTextureTranslucent(texture)) {
            RenderSystem.enableBlend();
            enabledBlend = true;
        }
    }

    @Inject(at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            method = "drawTexturedQuad(Lnet/minecraft/util/Identifier;IIIIIFFFF)V")
    private void disableBlend(Identifier texture,
                              int x1,
                              int x2,
                              int y1,
                              int y2,
                              int z,
                              float u1,
                              float u2,
                              float v1,
                              float v2,
                              CallbackInfo ci,
                              Matrix4f matrix4f,
                              BufferBuilder bufferBuilder) {
        if (enabledBlend) {
            RenderSystem.disableBlend();
            enabledBlend = false;
        }
    }
}
