package com.minecraftonline.griefalert;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.minecraftonline.griefalert.GriefAction.GriefType;
//TODO: PietElite: Fix
public class GriefActionTableManager {

	/** Table housing all information about the USE events to be watched by the GriefAlert plugin. */
    private static Table<String, String, GriefAction> useGriefActions;
    /** Table housing all information about the INTERACT events to be watched by the GriefAlert plugin. */
    private static Table<String, String, GriefAction> interactGriefActions;
    /** Table housing all information about the DESTROY events to be watched by the GriefAlert plugin. */
    private static Table<String, String, GriefAction> destroyGriefActions;
	
    public GriefActionTableManager() {
    	useGriefActions = HashBasedTable.create();
    	interactGriefActions = HashBasedTable.create();
    	destroyGriefActions = HashBasedTable.create();
    }
    
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
    
    public void clear() {
    	useGriefActions.clear();
    	interactGriefActions.clear();
    	destroyGriefActions.clear();
    }
    
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
    
}
