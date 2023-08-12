package com.ventooth.glassgui.mixin.client;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

import static com.ventooth.glassgui.GlassGUIResourcePackProvider.glassGUIResourcePackProvider;

@Environment(EnvType.CLIENT)
@Mixin(ResourcePackManager.class)
public abstract class ResourcePackManagerMixin {
    @Final
    @Mutable
    @Shadow
    private Set<ResourcePackProvider> providers;

    @Inject(method = "<init>",
            at = @At("TAIL"))
    private void registerLoader(CallbackInfo info) {
        providers = new HashSet<>(providers);
        providers.add(glassGUIResourcePackProvider());
        providers = ImmutableSet.copyOf(providers);
    }
}
