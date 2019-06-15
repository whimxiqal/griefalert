package com.minecraftonline.griefalert.core;

import java.util.Collection;
import java.util.LinkedList;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.minecraftonline.griefalert.core.GriefAction.GriefType;

/**
 * A data managing class to house all tables associated with grief actions.
 */
public class GriefActionTableManager {

	/** Table housing all information about the USE events to be watched by the GriefAlert plugin. */
    private static Table<String, String, GriefAction> useGriefActions;
    /** Table housing all information about the INTERACT events to be watched by the GriefAlert plugin. */
    private static Table<String, String, GriefAction> interactGriefActions;
    /** Table housing all information about the DESTROY events to be watched by the GriefAlert plugin. */
    private static Table<String, String, GriefAction> destroyGriefActions;
	
    /** Constructor which just creates all necessary tables. */
    public GriefActionTableManager() {
    	useGriefActions = HashBasedTable.create();
    	interactGriefActions = HashBasedTable.create();
    	destroyGriefActions = HashBasedTable.create();
    }
    
    /**
     * A put function reminiscent of put functions in maps and tables.
     * @param type The GriefType (Does not house data for GriefType.DEGRIEFED)
     * @param blockId The id assocaited with the griefed object
     * @param dimension The String representation of the dimension
     * @param griefAction The GriefAction to add to the table
     * @return Either the GriefAction that was replaced when put into the table
     * or null if no GriefAction was there before
     */
    public GriefAction put(GriefType type, String blockId, String dimension, GriefAction griefAction) {
    	if (type.equals(GriefType.USED)) {
    		return useGriefActions.put(blockId, dimension, griefAction);
    	} else if (type.equals(GriefType.INTERACTED)) {
    		return interactGriefActions.put(blockId, dimension, griefAction);
    	} else if (type.equals(GriefType.DESTROYED)) {
    		return destroyGriefActions.put(blockId, dimension, griefAction);
    	}
    	return null;
    }
    
    /** Clear all tables. */
    public void clear() {
    	useGriefActions.clear();
    	interactGriefActions.clear();
    	destroyGriefActions.clear();
    }
    
    /**
     * Check if the table of the corresponding GriefType contains a GriefAction here.
     * @param type The GriefType
     * @param blockId The string representation of the griefed object
     * @param dimension The string representation of the dimension
     * @return Whether the tables contain a GriefAction here
     */
    public boolean contains(GriefType type, String blockId, String dimension) {
    	if (type.equals(GriefType.USED)) {
    		return useGriefActions.contains(blockId, dimension) || useGriefActions.contains(blockId, "All");
    	} else if (type.equals(GriefType.INTERACTED)) {
    		return interactGriefActions.contains(blockId, dimension) || interactGriefActions.contains(blockId, "All");
    	} else if (type.equals(GriefType.DESTROYED)) {
    		return destroyGriefActions.contains(blockId, dimension) || destroyGriefActions.contains(blockId, "All");
    	}
    	return false;
    }

    /**
     * Get the corresponding GriefType with this griefed object and this dimension
     * @param type The GriefType
     * @param blockId The string representation of the griefed object
     * @param dimension The string representation of the dimension
     * @return The corresponding GriefAction, or null if none exists
     */
	public GriefAction get(GriefType type, String blockId, String dimension) {
		if (type.equals(GriefType.USED)) {
    		return useGriefActions.get(blockId, dimension);
    	} else if (type.equals(GriefType.INTERACTED)) {
    		return interactGriefActions.get(blockId, dimension);
    	} else if (type.equals(GriefType.DESTROYED)) {
    		return destroyGriefActions.get(blockId, dimension);
    	}
    	return null;
	}

	public Collection<GriefAction> values() {
		Collection<GriefAction> useGriefActionValues = useGriefActions.values();
		Collection<GriefAction> interactGriefActionValues = interactGriefActions.values();
		Collection<GriefAction> destroyGriefActionValues = destroyGriefActions.values();
		Collection<GriefAction> output = new LinkedList<GriefAction>();
		output.addAll(useGriefActionValues);
		output.addAll(interactGriefActionValues);
		output.addAll(destroyGriefActionValues);
		return output;
	}
    
}
