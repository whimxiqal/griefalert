package com.minecraftonline.griefalert;

import com.minecraftonline.griefalert.tools.General;

import org.spongepowered.api.text.format.TextColor;

public class GriefAction {
	
	public static final char DEFAULT_ALERT_COLOR = 'c';
	
    protected final String blockId;
    protected final TextColor alertColor;
    protected final boolean denied;
    protected final boolean stealthyAlert;
    protected final GriefType type;

    public enum GriefType {
        DEGRIEFED,
        DESTROYED,
        INTERACTED {
            @Override
            public String toString() {
                return "interacted with";
            }
        },
        USED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public GriefAction(String blockId, TextColor color, boolean deny, boolean silent, GriefType type) {
        this.blockId = blockId;
        this.alertColor = color;
        this.denied = deny;
        this.stealthyAlert = silent;
        this.type = type;
    }
    
    public GriefAction(String blockId, char color, boolean deny, boolean silent, GriefType type) {
    	this(blockId, General.charToColor(color), deny, silent, type);
    	
    }
    
    public GriefAction(String[] splitLine) throws IllegalArgumentException {
    	// 0: type, 1: ID, 2: color, 3: stealth alarm 4: allow/deny 5: onlyIn list
    	
    	if (splitLine.length < 3 ) {
        	throw new IllegalArgumentException("Too few arguments");
        }
    	
    	if (splitLine[0].equalsIgnoreCase("USE")) {
            this.type = GriefType.USED;
        } else if (splitLine[0].equalsIgnoreCase("DESTROY")) {
            this.type = GriefType.DESTROYED;
        } else if (splitLine[0].equalsIgnoreCase("INTERACT")) {
            this.type = GriefType.INTERACTED;
        } else {
            throw new IllegalArgumentException("unrecognized activator : " + splitLine[0]);
        }

    	// Fix blockId in the case that the 'minecraft:' prefix was not explicitly marked
    	String blockId = splitLine[1].replace('-', ':');
        if (!blockId.contains("minecraft:")) {
        	blockId = "minecraft:" + blockId;
        }
        this.blockId = blockId;
        
        // Throws an IllegalColorCodeException if the value doesn't work
        this.alertColor = General.charToColor(splitLine[2].charAt(0));
        
        this.stealthyAlert = splitLine.length > 3 && splitLine[3].equalsIgnoreCase("stealth");

    	this.denied = splitLine.length > 4 && splitLine[4].equalsIgnoreCase("deny");
    	
	}

	public String getBlockId() {
    	return blockId;
    }

    public TextColor getAlertColor() {
        return alertColor;
    }

    public boolean isDenied() {
        return denied;
    }

    public boolean isStealthyAlert() {
        return stealthyAlert;
    }

    public GriefType getType() {
        return type;
    }

}
