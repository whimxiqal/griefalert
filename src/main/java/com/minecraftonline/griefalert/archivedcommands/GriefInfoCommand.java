//package com.minecraftonline.griefalert.commands;
//
//import java.util.Arrays;
//
//import org.spongepowered.api.command.CommandSource;
//import org.spongepowered.api.entity.living.player.Player;
//import org.spongepowered.api.text.Text;
//import org.spongepowered.api.text.format.TextColors;
//
//import com.minecraftonline.griefalert.GriefAlert;
//import com.minecraftonline.griefalert.core.GriefInstance;
//
//import co.aikar.commands.BaseCommand;
//import co.aikar.commands.annotation.CommandAlias;
//import co.aikar.commands.annotation.CommandPermission;
//import co.aikar.commands.annotation.Conditions;
//import co.aikar.commands.annotation.Default;
//import co.aikar.commands.annotation.Description;
//import co.aikar.commands.annotation.Syntax;
//
///**
// * The CommandExecutor for the command to check alerts for GriefInstances in game
// */
//@CommandPermission("griefalert.command.ginfo")
//@CommandAlias("ginfo")
//@Description("Checks the details about a grief alert")
//public final class GriefInfoCommand extends BaseCommand {
//
//	/** The main plugin object. */
//    private final GriefAlert plugin;
//
//    /**
//     * Basic constructor.
//     * @param plugin The main plugin object.
//     */
//    public GriefInfoCommand(GriefAlert plugin) {
//        this.plugin = plugin;
//    }
//
//    @Default
//    @Syntax("<id>")
//    @Conditions("player")
//    public void onGinfo(CommandSource src, int code) {
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
//        		player.sendMessage(Text.builder("That Grief Alert doesn't exist!").color(TextColors.RED).build());
//        	} else {
//        		player.sendMessage(Text.builder("Grief Alert ID " + code + " is out of range.").color(TextColors.RED).build());
//        	}
//        	return;
//        }
//
//        if (instance == null) {
//        	// No Grief Instance at that array location
//            player.sendMessage(Text.builder("There is no current alert at ID " + code + ".").color(TextColors.RED).build());
//            return;
//        }
//        player.sendMessage(Text.of(TextColors.GOLD, "Grief Alert Information (")
//        		.concat(Text.of(TextColors.RED, String.valueOf(code)))
//        		.concat(Text.of(TextColors.GOLD, ")")));
//        player.sendMessage(generateInfoLine("Player", instance.getGrieferAsPlayer().getName()));
//        player.sendMessage(generateInfoLine("Action", instance.getType().toString()));
//        player.sendMessage(generateInfoLine("Object", instance.getGriefObjectAsString()));
//        player.sendMessage(generateInfoLine("Coordinates", String.join(", ", Arrays.asList(
//        																			String.valueOf(instance.getLocation().getBlockX()),
//        																			String.valueOf(instance.getLocation().getBlockY()),
//        																			String.valueOf(instance.getLocation().getBlockZ())))));
//        player.sendMessage(generateInfoLine("Dimension", instance.getLocation().getExtent().getDimension().getType().getName()));
//    }
//
//    private Text generateInfoLine(String type, String info) {
//    	return Text.builder()
//    			.append(Text.of(TextColors.YELLOW, type))
//    			.append(Text.of(": "))
//    			.append(Text.of(TextColors.WHITE, info))
//    			.build();
//    }
//}
