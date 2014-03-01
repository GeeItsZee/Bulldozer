package com.yahoo.tracebachi.Utils;

import java.util.HashMap;

import org.bukkit.World;

/**
 * SingleBlockGroupManager <p>
 * Used for the storage of one BlockGroup per user identified with a string.
 * 
 * @author Alias: TheCriticalError
 */
public class SingleBlockGroupManager
{
	// Class variables
	private HashMap< String, BlockSet > map = 
		new HashMap< String, BlockSet >();
	
	/**
	 * Searches for the BlockGroup mapped to the string that identifies
	 * the player. If there is no mapping, one is created with a new 
	 * and empty {@link BlockGroup}. <p>
	 * 
	 * @param playerName	: Name of the player to find the group for
	 * 
	 * @return The BlockGroup mapped to the player-string.
	 */
	public BlockSet getGroupFor( String playerName )
	{
		// Check if player is in the map
		if( ! map.containsKey( playerName ) )
		{
			// Add player to the map
			map.put( playerName, new BlockSet() );
		}
		
		// Return the selection
		return map.get( playerName );
	}
	
	/**
	 * Adds a block for the player specified with the string. <p>
	 * 
	 * @param playerName	: Name of the player for whom to add the
	 * block for
	 * @param selectedBlock	: Block object to add from
	 */
	/*public void addBlockFor( String playerName , Block selectedBlock )
	{
		// Initialize a temporary reference
		BlockGroup targetGroup = getGroupFor( playerName );
		
		// Check if this is the first block
		if( targetGroup.isEmpty() )
		{
			// Add modified as a gold block
			targetGroup.setKeyBlock( selectedBlock );
			targetGroup.addBlockAndChange( selectedBlock , 41 , (byte) 0 );
		}
		else
		{
			// Add modified as glass
			targetGroup.addBlockAndChange( selectedBlock , 20 , (byte) 0 );
		}
	}*/
	
	/**
	 * Removes the player from the manager by removing the BlockGroup and the
	 * mapping. If the boolean {@code restore} is true, the blocks are restored
	 * when the group is removed. Otherwise, they are simply removed. <p> 
	 * 
	 * @param playerName	: Name of the player (Search key for mapping)
	 * @param restore		: Boolean indicating if the BlockGroup should
	 * be restored (if true) or cleared (if false)
	 * @param playerWorld	: The world in which the blocks should be
	 * restored in the case that {@code restore} is true
	 * 
	 * @return Boolean value indicating true if the player was removed and false
	 * if the player was not found in the map.
	 */
	public boolean removeGroupAndClearFor( String playerName )
	{	
		// Check if player is in the map
		if( map.containsKey( playerName ) )
		{
			// Remove the player from the map and clear the blocks
			map.remove( playerName ).cleanup();
			return true;
		}
		
		// Otherwise
		return false;
	}
	
	public boolean removeGroupAndRestoreFor( String playerName, 
		World targetWorld )
	{	
		// Check if player is in the map
		if( map.containsKey( playerName ) )
		{
			// Remove the player from the map and revert the blocks
			map.remove( playerName ).restoreInWorld( true, targetWorld );
			return true;
		}
		
		// Otherwise
		return false;
	}

	/**
	 * Resets the HashMap by removing all mappings and removing all blocks
	 * in the all the BlockGroups. <p>
	 */
	public void closeManager()
	{
		// Remove all entries in the map
		for( BlockSet inMap : map.values() )
		{
			inMap.cleanup();
		}
		
		// Clear the map
		map.clear();
	}
}
