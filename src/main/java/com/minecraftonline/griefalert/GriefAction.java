package com.minecraftonline.griefalert;

import com.minecraftonline.griefalert.tools.General;

import org.spongepowered.api.text.format.TextColor;

/**
 * This is an immutable object type to hold information about actions which are
 * considered griefing. These are created by reading configurable files upon
 * start-up, and do not change throughout the use the plugin.
 */
public class GriefAction {
	
	/** The default color code for alerts. Must be one of: a,b,c,d,e,f,0,1,2,3,4,5,6,7,8,9 */
	public static final char DEFAULT_ALERT_COLOR = 'c';
	/** A grief action corresponding to Degriefing by staff. */
	public static final GriefAction DEGRIEF_ACTION = new GriefAction(null, 'F', false, true, GriefAction.GriefType.DEGRIEFED);
	/** String representation of the griefed object. */
    protected final String blockId;
    /** TextColor representation of the color in which an alert will appear. */
    protected final TextColor alertColor;
    /** Denotes whether this action will be cancelled upon triggering. */
    protected final boolean denied;
    /** Denotes whether this is muted from alerting. */
    protected final boolean stealthyAlert;
    /** The type of grief. */
    protected final GriefType type;

    /** An enumerated list of Types of Grief. */
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

    /**
     * A constructor for a GriefAction.
     * @param blockId The string representation of the griefed object
     * @param color The TextColor representation of the color in which an alert will appear
     * @param deny Denotes whether this action will be cancelled upon triggering
     * @param silent Denotes whether this is muted from alerting
     * @param type The type of grief
     */
    public GriefAction(String blockId, TextColor color, boolean deny, boolean silent, GriefType type) {
        this.blockId = blockId;
        this.alertColor = color;
        this.denied = deny;
        this.stealthyAlert = silent;
        this.type = type;
    }
    
    /**
     * A constructor for a GriefAction.
     * @param blockId The string representation of the griefed object
     * @param color The character representation of the color in which an alert will appear
     * @param deny Denotes whether this action will be cancelled upon triggering
     * @param silent Denotes whether this is muted from alerting
     * @param type The type of grief
     */
    public GriefAction(String blockId, char color, boolean deny, boolean silent, GriefType type) {
    	this(blockId, General.charToColor(color), deny, silent, type);
    	
    }
    
    /**
     * A constructor for a GriefAction, reading from a String array with a specific format in each index:
     * <ol start=0>
     * <li>GriefType: either <b>USE</b>, <b>DESTROY</b>, or <b>INTERACT</b></li>
     * <li>The Block ID of the griefed object: formatted <b>minecraft:<i>objectname</i></b> or just
     * <b><i>objectname</i></b></li>
     * <li>Character representation of color: Any of <b>a,b,c,d,e,f</b> or <b>1,2,3,4,5,6,7,8,9</b></li>
     * <li><i>Optional - Stealth Alert Mode: mute alerts by including <b>stealth</b> here</i></li>
     * <li><i>Optional - Deny Action: cancel the action which matches this GriefAction by 
     * including <b>deny</b> here</i></li>
     * <li> and beyond -> Ignore
     * </ol>
     * @param splitLine
     * @throws IllegalArgumentException
     */
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
    
    /** Get string representation of the griefed object. */
	public String getBlockId() {
    	return blockId;
    }

	/** Get the TextColor representation of the color in which an alert will appear. */
    public TextColor getAlertColor() {
        return alertColor;
    }
    
    /** Get whether this action will be cancelled upon triggering. */
    public boolean isDenied() {
        return denied;
    }

    /** Get whether this action is muted from alerting. */
    public boolean isStealthyAlert() {
        return stealthyAlert;
    }

    /** Get the type of grief. */
    public GriefType getType() {
        return type;
    }
    
	@Override
	/**
	 * Returns whether both the types of the grief actions and the blockId's associated with the
	 * actions are equal.
	 * <p>
	 * They are considered equal if the type of grief is the same and the String representation of
	 * the griefed object is the same.
	 * @param otherObject Another Grief Action to compare
	 * @return If these two Grief Actions are considered equal
	 */
	public boolean equals(Object otherObject) {
		if (!(otherObject instanceof GriefInstance)) {
			return false;
		}
		GriefInstance other = (GriefInstance) otherObject;
		return this.getType() == other.getType() &&
				this.getBlockId() == other.getBlockId();
	}

}
