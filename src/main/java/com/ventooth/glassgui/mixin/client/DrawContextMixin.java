package com.ventooth.glassgui.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.spongepowered.asm.mixin.injection.At.*;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
	@Inject(at = @At(value = "INVOKE",
					 target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V",
					 shift = Shift.AFTER),
			method = "drawTexturedQuad(Lnet/minecraft/util/Identifier;IIIIIFFFF)V")
	private void enableBlend(CallbackInfo info) {
		RenderSystem.enableBlend();
	}

	@Inject(at = @At("RETURN"),
			method = "drawTexturedQuad(Lnet/minecraft/util/Identifier;IIIIIFFFF)V")
	private void disableBlend(CallbackInfo info) {
		RenderSystem.disableBlend();
	}
}
