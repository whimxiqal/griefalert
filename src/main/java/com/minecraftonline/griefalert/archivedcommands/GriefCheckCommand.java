//package com.minecraftonline.griefalert.commands;
//
//import org.spongepowered.api.command.CommandSource;
//import org.spongepowered.api.entity.EntitySnapshot;
//import org.spongepowered.api.entity.living.player.Player;
//import org.spongepowered.api.text.Text;
//import org.spongepowered.api.text.format.TextColors;
//
//import com.minecraftonline.griefalert.GriefAlert;
//import com.minecraftonline.griefalert.core.GriefInstance;
//import com.minecraftonline.griefalert.tools.General;
//
//import co.aikar.commands.BaseCommand;
//import co.aikar.commands.annotation.CommandAlias;
//import co.aikar.commands.annotation.CommandPermission;
//import co.aikar.commands.annotation.Conditions;
//import co.aikar.commands.annotation.Default;
//import co.aikar.commands.annotation.Description;
//import co.aikar.commands.annotation.Syntax;
//
//import java.util.NoSuchElementException;
//
//import static org.spongepowered.api.text.format.TextColors.RED;
//import static org.spongepowered.api.text.format.TextColors.YELLOW;
//
///**
// * The CommandExecutor for the command to check alerts for GriefInstances in game.
// */
//@CommandPermission("griefalert.command.gcheck")
//@CommandAlias("gcheck")
//@Description("Checks a grief alert")
//public final class GriefCheckCommand extends BaseCommand {
//
//	/** The main plugin object. */
//    private final GriefAlert plugin;
//
//    /**
//     * Basic constructor.
//     * @param plugin The main plugin object.
//     */
//    public GriefCheckCommand(GriefAlert plugin) {
//        this.plugin = plugin;
//    }
//
//    @Default
//    @Syntax("<id>")
//    @Conditions("player")
//    public void onGcheck(CommandSource src, int code) {
//        Player player = (Player) src;
//        GriefInstance instance;
//
//        try {
//        	instance = plugin.getGriefManager().get(code);
//        } catch (IndexOutOfBoundsException e) {
//        	// Code is not within the valid range
//        	if (code == -1) {
//        		// Grief Instances that are no longer in the Recent Grief Instance Array are given alertID -1 so
//        		// to signal that they are no longer on call.
//        		player.sendMessage(Text.builder("That Grief Alert doesn't exist!").color(RED).build());
//        	} else {
//        		player.sendMessage(Text.builder("Grief Alert ID " + code + " is out of range.").color(RED).build());
//        	}
//        	return;
//        }
//
//        if (instance == null) {
//        	// No Grief Instance at that array location
//            player.sendMessage(Text.builder("There is no current alert at ID " + code + ".").color(RED).build());
//            return;
//        }
//
//        // Teleport checker to a safe location near the grief. If failed, notify the checker
//        EntitySnapshot checkerPriorSnapshot = player.createSnapshot();
//        if (player.setLocationSafely(instance.getGrieferSnapshot().getLocation().get())) {
//            try {
//            	player.setRotation(instance.getGrieferSnapshot().getTransform().get().getRotation());
//            	plugin.getGriefManager().printToStaff(General.formatPlayerName(player).toBuilder().append(
//                        Text.builder(" is checking ").color(TextColors.YELLOW).build()).append(
//                        Text.builder(Integer.toString(code)).color(TextColors.WHITE).build()).append(
//                        Text.builder(" for grief.").color(TextColors.YELLOW).build()).build());
//            	plugin.getGriefInfoCommand().onGinfo(player, code);
//            	instance.addChecker(player, checkerPriorSnapshot);
//            } catch (NoSuchElementException e) {
//            	plugin.getLogger().warn("When checking for grief, player " + player.getName() + " did not find the transform within"
//            			+ "the snapshot of the griefer.");
//            }
//        } else {
//        	player.sendMessage(Text.builder("No safe location was found for teleportation.").color(YELLOW).build());
//        }
//    }
//}