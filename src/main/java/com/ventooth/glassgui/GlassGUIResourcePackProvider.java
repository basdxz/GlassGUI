package com.ventooth.glassgui;

import com.google.gson.annotations.SerializedName;
import lombok.NoArgsConstructor;
import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.metadata.PackFeatureSetMetadata;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GlassGUIResourcePackProvider implements ResourcePackProvider {
    private static final int METADATA_FORMAT = SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES);
    private static final PackResourceMetadata METADATA = new PackResourceMetadata(Text.of("§3Glass §bGUI"), METADATA_FORMAT);
    private static final PackFeatureSetMetadata FEATURE_FLAGS = new PackFeatureSetMetadata(FeatureFlags.DEFAULT_ENABLED_FEATURES);
    private static final ResourceMetadataMap METADATA_MAP = ResourceMetadataMap.of(PackResourceMetadata.SERIALIZER,
                                                                                   METADATA,
                                                                                   PackFeatureSetMetadata.SERIALIZER,
                                                                                   FEATURE_FLAGS);

    private static final Identifier POS_TEX_FRAG_ID = new Identifier("minecraft:shaders/core/position_tex.fsh");

    private static final GlassGUIResourcePackProvider INSTANCE = new GlassGUIResourcePackProvider();

    public static GlassGUIResourcePackProvider glassGUIResourcePackProvider() {
        return INSTANCE;
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        profileAdder.accept(ResourcePackProfile.of("Glass GUI",
                                                   METADATA.getDescription(),
                                                   false,
                                                   name -> new GlassGUIResourcePack(),
                                                   new ResourcePackProfile.Metadata(METADATA.getDescription(),
                                                                                    METADATA_FORMAT,
                                                                                    FEATURE_FLAGS.flags()),
                                                   ResourceType.CLIENT_RESOURCES,
                                                   ResourcePackProfile.InsertionPosition.TOP,
                                                   false,
                                                   ResourcePackSource.BUILTIN));
    }

    private static class GlassGUIResourcePack implements ResourcePack {
        @Nullable
        @Override
        public InputSupplier<InputStream> openRoot(String... segments) {
            return null;
        }

        @Nullable
        @Override
        public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
            if (type != ResourceType.CLIENT_RESOURCES)
                return null;
            if (!POS_TEX_FRAG_ID.equals(id))
                return null;
            return posTexFragInputStreamSupplier();
        }

        @Override
        public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
            if (type != ResourceType.CLIENT_RESOURCES)
                return;
            if (!"minecraft".equals(namespace))
                return;
            if (!"shaders".equals(prefix))
                return;
            consumer.accept(POS_TEX_FRAG_ID, posTexFragInputStreamSupplier());
        }

        @Override
        public Set<String> getNamespaces(ResourceType type) {
            if (type == ResourceType.CLIENT_RESOURCES)
                return Set.of("minecraft");
            return Collections.emptySet();
        }

        @Nullable
        @Override
        public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
            return METADATA_MAP.get(metaReader);
        }

        @Override
        public String getName() {
            return "Glass GUI";
        }

        @Override
        public void close() {
        }
    }

    private static InputSupplier<InputStream> posTexFragInputStreamSupplier() {
        return () -> GlassGUIResourcePackProvider.class.getResourceAsStream("/assets/glassgui/shaders/position_tex.fsh");
    }

    public record ResourcePackMcMeta(@SerializedName("pack") ResourcePackInfo packInfo) {}

    public record ResourcePackInfo(@SerializedName("pack_format") int packFormat, String description) {}
}
