package com.minecraftonline.griefalert;

import com.google.inject.Inject;
import com.minecraftonline.griefalert.listeners.GriefHangingEntity;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.HashMap;

import static com.minecraftonline.griefalert.GriefAlert.VERSION;

@Plugin(id = "griefalert",
        name = "GriefAlert",
        version = VERSION,
        description = "Grief alert tool")
public class GriefAlert {
    static final String VERSION = "20.0";

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    private ConfigurationNode rootNode;

    static HashMap<String, GriefAction> onUseWatchList;
    static HashMap<String, GriefAction> onRightClickWatchList;
    static HashMap<String, GriefAction> onBreakWatchList;

    @Listener
    public void initialize(GamePreInitializationEvent event) {
        // CONFIG

        registerListeners();
    }

    private void registerListeners() {
        // Priority post event since this is purely a logger of events that actually happen
        Sponge.getEventManager().registerListener(this, InteractEntityEvent.class, Order.POST, new GriefHangingEntity());
    }


    public static boolean isUseWatched(String blockName) {
        return onUseWatchList.containsKey(blockName);
    }

    public static boolean isRightClickWatched(String blockName) {
        return onRightClickWatchList.containsKey(blockName);
    }

    public static boolean isBreakWatched(String blockName) {
        return onBreakWatchList.containsKey(blockName);
    }

    public static GriefAction getUseWatchedAction(String blockName) {
        return onUseWatchList.get(blockName);
    }

    public static GriefAction getRightClickWatchedAction(String blockName) {
        return onRightClickWatchList.get(blockName);
    }
}
