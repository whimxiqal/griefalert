package com.minecraftonline.griefalert.tools;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.World;

import com.minecraftonline.griefalert.core.GriefAction.GriefType;

public final class CustomizableString {
	
	private final static String PLAYER_PLACEHOLDER = "\\(PLAYER\\)";
	private final static String GRIEF_ID_PLACEHOLDER = "\\(GRIEF_ID\\)";
	private final static String LOCATION_BLOCK_PLACEHOLDER = "\\(LOCATION:BLOCK\\)";
	private final static String LOCATION_WORLD_PLACEHOLDER = "\\(LOCATION:WORLD\\)";
	private final static String LOCATION_DIMENSION_PLACEHOLDER = "\\(LOCATION:DIMENSION\\)";
	private final static String GRIEF_VERB_PLACEHOLDER = "\\(GRIEF_VERB\\)";
	private final static String GRIEF_OBJECT_PLACEHOLDER = "\\(GRIEF_OBJECT\\)";
	
	private String string;
	
	public CustomizableString(String string) {
		this.string = string;
	}
	
	public CustomizableString replacePlayer(Player player) {
		return replace(PLAYER_PLACEHOLDER, player.getName());
	}
	
	public CustomizableString replaceGriefID(int id) {
		return replace(GRIEF_ID_PLACEHOLDER, String.valueOf(id));
	}
	
	public CustomizableString replaceLocationBlock(int[] coordinates) {
		return replace(LOCATION_BLOCK_PLACEHOLDER, "" +
								coordinates[0] + ", " +
								coordinates[1] + ", " +
								coordinates[2]);			
	}
	
	public CustomizableString replaceLocationWorld(World world) {
		return replace(LOCATION_WORLD_PLACEHOLDER, world.getName());
	}
	
	public CustomizableString replaceLocationDimension(DimensionType type) {
		return replace(LOCATION_DIMENSION_PLACEHOLDER, type.getName());
	}
	
	public CustomizableString replaceGriefType(GriefType type) {
		return replace(GRIEF_VERB_PLACEHOLDER, type.toString());
	}
	
	public CustomizableString replaceGriefObject(String object) {
		return replace(GRIEF_OBJECT_PLACEHOLDER, object);
	}
	
	private CustomizableString replace(String placeholder, String object) {
		this.string = this.string.replaceAll(placeholder, object);
		return this;
	}
	
	public String complete() {
		return General.correctIndefiniteArticles(string);
	}
	
}
