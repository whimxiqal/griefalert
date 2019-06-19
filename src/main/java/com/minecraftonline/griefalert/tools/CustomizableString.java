package com.minecraftonline.griefalert.tools;

import java.util.List;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.World;

import com.minecraftonline.griefalert.core.GriefAction.GriefType;

public final class CustomizableString {
	
	private final static String PLAYER_PLACEHOLDER = "\\(PLAYER\\)";
	private final static String GRIEF_ID_PLACEHOLDER = "\\(GRIEF_ID\\)";
	private final static String LOCATION_COORDINATES_PLACEHOLDER = "\\(LOCATION:COORDINATES\\)";
	private final static String LOCATION_WORLD_PLACEHOLDER = "\\(LOCATION:WORLD\\)";
	private final static String LOCATION_DIMENSION_PLACEHOLDER = "\\(LOCATION:DIMENSION\\)";
	private final static String GRIEF_VERB_PLACEHOLDER = "\\(GRIEF_VERB\\)";
	private final static String GRIEF_OBJECT_PLACEHOLDER = "\\(GRIEF_OBJECT\\)";
	private final static String GRIEF_ID_LIST_PLACEHOLDER = "\\(GRIEF_ID_LIST\\)";
	private final static String SIGN_LINE_NUMBER_PLACEHOLDER = "\\(SIGN_LINE_NUMBER\\)";
	private final static String SIGN_LINE_CONTENT_PLACEHOLDER = "\\(SIGN_LINE_CONTENT\\)";
	
	private String string;
	
	public CustomizableString(String string) {
		this.string = string;
	}
	
	public CustomizableString replacePlayer(Player player) {
		return replace(PLAYER_PLACEHOLDER, player.getName());
	}
	
	public CustomizableString replaceGriefID(int id) {
		if (id == 0) {
			return replace(GRIEF_ID_PLACEHOLDER, "");
		} else {
			return replace(GRIEF_ID_PLACEHOLDER, String.valueOf(id));
		}
	}
	
	public CustomizableString replaceLocationCoordinates(List<String> coordinates) {
		return replace(LOCATION_COORDINATES_PLACEHOLDER, String.join(", ", coordinates));			
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
	
	public CustomizableString replaceGriefIDList(List<String> ids) {
		return replace(GRIEF_ID_LIST_PLACEHOLDER, String.join(", ", ids));
	}
	
	public CustomizableString replaceSignLineContent(String line) {
		return replace(SIGN_LINE_CONTENT_PLACEHOLDER, line);
	}
	
	public CustomizableString replaceSignLineNumber(int lineNumber) {
		return replace(SIGN_LINE_NUMBER_PLACEHOLDER, String.valueOf(lineNumber));
	}
	
	private CustomizableString replace(String placeholder, String object) {
		this.string = this.string.replaceAll(placeholder, object);
		return this;
	}
	
	public String complete() {
		return General.correctIndefiniteArticles(string);
	}
	
}
