package com.ventooth.glassgui;

import com.google.gson.GsonBuilder;
import lombok.NoArgsConstructor;
import lombok.val;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public final class GlassGUI implements ClientModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("Glass GUI");

    private static Config config;

    @Override
    public void onInitializeClient() {
        val configPath = FabricLoader.getInstance().getConfigDir();
        val configFile = configPath.resolve("glass_gui.json").toFile();

        val gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            val json = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
            try {
                config = gson.fromJson(json, Config.class);
                if (config.translucentTextures == null)
                    throw new IllegalStateException(
                            "Config does not contain \"translucentTextures\": [\"texture_path\"] block");
            } catch (RuntimeException e) {
                LOG.error("Failed parsing config: %s".formatted(configFile), e);
            }
            LOG.info("Config loaded");
        } catch (IOException e) {
            config = new Config(new HashSet<>());
            config.translucentTextures.add("textures/gui/container/inventory.png");
            LOG.info("Generated new config");
            config_saving:
            {
                final String json;
                try {
                    json = gson.toJson(config);
                } catch (RuntimeException re) {
                    LOG.error("Failed converting config to JSON: %s".formatted(configFile), e);
                    break config_saving;
                }
                try {
                    FileUtils.writeStringToFile(configFile, json, StandardCharsets.UTF_8);
                } catch (IOException ioe) {
                    LOG.error("Failed saving new config to: %s".formatted(configFile), e);
                }
            }
        }
        LOG.info("Translucent textures:");
        config.translucentTextures().forEach(LOG::info);
    }

    public static boolean isTextureTranslucent(Identifier texture) {
        if (config == null)
            return false;
        return config.translucentTextures().contains(texture.getPath());
    }

    public record Config(Set<String> translucentTextures) {}
}
