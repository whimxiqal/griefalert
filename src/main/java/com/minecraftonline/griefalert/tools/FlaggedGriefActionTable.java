package com.minecraftonline.griefalert.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.Triple;

import com.minecraftonline.griefalert.GriefAction;
import com.minecraftonline.griefalert.GriefAction.Type;
import com.minecraftonline.griefalert.ImmutableGriefAction;

public class FlaggedGriefActionTable {

	Map<Triple<Type, String, String>, ImmutableGriefAction> data;
	
	public FlaggedGriefActionTable() {
		data = new HashMap<Triple<Type, String, String>, ImmutableGriefAction>();
	}

	public void clear() {
		data = new HashMap<Triple<Type, String, String>, ImmutableGriefAction>();
	}

	public boolean contains(Type type, String blockId, String dimension) {
		return data.containsKey(Triple.of(type, blockId, dimension));
	}

	public boolean containsValue(GriefAction griefAction) {
		return data.containsValue(griefAction);
	}

	public ImmutableGriefAction get(Type type, String blockId, String dimension) {
		return data.get(Triple.of(type, blockId, dimension));
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public ImmutableGriefAction put(Type type, String blockId, String dimension, ImmutableGriefAction immutableGriefAction) {
		return data.put(Triple.of(type, blockId, dimension), immutableGriefAction);
	}

	public Object remove(Type type, String blockId, String dimension) {
		return data.remove(Triple.of(type, blockId, dimension));
	}

	public int size() {
		return data.size();
	}

	public Collection<ImmutableGriefAction> values() {
		return data.values();
	}
	
}
