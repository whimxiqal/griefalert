package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.core.GriefInstance;
import com.minecraftonline.griefalert.core.GriefAction.GriefType;
import com.minecraftonline.griefalert.tools.General;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.DimensionType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class GriefDestroyListener implements EventListener<ChangeBlockEvent.Break> {
	
	/** The main plugin object. */
    private final GriefAlert plugin;

    /**
     * The generic constructor.
     * @param plugin The main plugin object
     */
    public GriefDestroyListener(GriefAlert plugin) {
    	this.plugin = plugin;
    }

    // TODO Add: destroying entities
    
    @Override
    public void handle(@Nonnull ChangeBlockEvent.Break event) {
    	// Make sure the event was caused by a player
        if (event.getCause().root() instanceof Player) {
            Optional<Player> poption = event.getCause().first(Player.class);
            if (poption.isPresent()) {
                Player player = poption.get();
                List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
                for (Transaction<BlockSnapshot> transaction : transactions) {
                    BlockSnapshot blockSnapshot = transaction.getOriginal();
                    String blockID = blockSnapshot.getState().getType().getId();
                    plugin.getDebugLogger().log(player.getName() + " broke a " + blockID);
                    DimensionType dType = blockSnapshot.getLocation().get().getExtent().getDimension().getType();
                    plugin.getDebugLogger().log("Checking if this is a registered Grief Action."
                			+ "Type: " + GriefType.DESTROYED + ", "
                			+ "BlockID: " + blockID + ", "
                			+ "Dimension: " + dType);
                    if (plugin.isGriefAction(GriefType.DESTROYED, blockID, dType)) {
                    	plugin.getDebugLogger().log("This is registered as a Grief Action.");
                        if (!plugin.getGriefAction(GriefType.DESTROYED, blockID, dType).isDenied()) {
                        	plugin.getDebugLogger().log("This is not a denied Grief Action.");
                            plugin.getGriefManager().processGriefInstance(new GriefInstance(
                            		plugin.getGriefAction(GriefType.DESTROYED, blockID, dType),
                            		blockSnapshot,
                            		player));
                            plugin.getDebugLogger().log("Grief Instance of this Destroy Grief Action processed.");
                        } else {
                            event.setCancelled(true);
                            plugin.getDebugLogger().log("This is a denied Grief Action.");
                            try {
                            plugin.getGriefManager().printToStaff(Text.of(
                            							plugin.getGriefAction(GriefType.DESTROYED, blockID, dType).getAlertColor(), 
                            							General.correctIndefiniteArticles(
                            									player.getName() + 
                            									" tried to break a " + 
                            									blockSnapshot.getState().getType().getTranslation().get() + 
                            									", but it was denied.")));
                            } catch (NoSuchElementException e) {
                            	e.printStackTrace();
                            }
                        }
                    } else {
                    	plugin.getDebugLogger().log("This is not registered as a Grief Action.");
                    }
                }
            }
        }
    }
}
