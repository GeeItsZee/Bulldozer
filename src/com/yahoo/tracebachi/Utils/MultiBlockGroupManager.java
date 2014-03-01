package com.yahoo.tracebachi.Utils;

import java.util.HashMap;
import java.util.Stack;

import org.bukkit.World;

/**
 * MultiBlockSetManager <p>
 * Used for the storage of multiple BlockSets per user identified with a string.
 * 
 * @author Alias: TheCriticalError
 */
public class MultiBlockGroupManager
{
	// Class Variables
	private HashMap< String, Stack< BlockSet > > dataMap = 
		new HashMap< String , Stack < BlockSet > >();

	/**
	 * Pushes a new and pre-made block group into the stack for the player
	 * identified by the player name-string. <p>
	 * 
	 * @param playerName	: Name of the player to store for
	 * @param toStore		: Reference to the BlockSet to push into 
	 * the stack
	 * 
	 * @return Boolean value of true if the BlockSet was not null and
	 * was stored in the stack and false if the group was null.
	 */
	public boolean pushGroupFor( String playerName , BlockSet toStore )
	{
		// Verify the group is not null
		if( toStore != null )
		{
			// Add a new group to the storage
			getStorageFor(playerName).push( toStore );
			return true;
		}
		
		// Else
		return false;
	}
	
	/**
	 * Checks there are any BlockSets stored in the stack mapped to the
	 * name-string. If there are, it returns a reference to the BlockSet
	 * at the top of the stack using peek(). NOTE: The group can be modified
	 * while the reference is live. If major modifications need to be made,
	 * use the getCopy() function of BlockSet so that the stored data is
	 * not corrupted. <p>
	 * 
	 * @param playerName	: String identifier of the player/user
	 * 
	 * @return The BlockSet at the top of the stack OR null if the stack is
	 * empty.
	 */
	public BlockSet peekGroupFor( String playerName )
	{
		// Verify the stack is not empty
		if( ! getStorageFor( playerName ).isEmpty() )
		{
			// Return the group at the top
			return getStorageFor( playerName ).peek();
		}
		
		// Otherwise
		return null;
	}
	
	/**
	 * Checks there are any BlockSets stored in the stack mapped to the
	 * name-string. If there are, it returns a reference to the BlockSet
	 * at the top of the stack using pop(). NOTE: The group is permanently
	 * removed from the stack. If the reference is lost, the group is lost.
	 * 
	 * @param playerName	: String identifier of the player/user
	 * 
	 * @return The BlockSet at the top of the stack OR null if the stack is
	 * empty.
	 */
	public BlockSet popGroupFor( String playerName )
	{
		// Verify the stack is not empty
		if( ! getStorageFor( playerName ).isEmpty() )
		{
			// Return the group at the top
			return getStorageFor( playerName ).pop();
		}
		
		// Otherwise
		return null;
	}

	public void removeGroupsAndClearFor( String playerName )
	{		
		// Create a temporary reference to the stack
		Stack< BlockSet > tempGroup = getStorageFor( playerName );

		// While not empty
		while( ! tempGroup.isEmpty() )
		{
			// Clear the blocks in the group
			tempGroup.pop().cleanup();
		}
	}
	
	public int removeGroupsAndRestoreFor( String playerName, 
		World targetWorld )
	{
		// Initialize counter
		int counter = 0;
		
		// Create a temporary reference to the stack
		Stack< BlockSet > tempGroup = getStorageFor( playerName ); 

		// While not empty
		while( ! tempGroup.isEmpty() )
		{
			// Clear the blocks in the group
			tempGroup.pop().restoreInWorld( true, targetWorld );
			counter++;
		}
		
		// Return counter
		return counter;
	}

	/**
	 * Resets the HashMap by removing all mappings and removing all BlockSet
	 * stacks for all the players mapped. <p>
	 */
	public void closeManager()
	{
		// Loop through the map and clear all groups
		for( String inMap : dataMap.keySet() )
		{
			// Remove the player from the map
			removeGroupsAndClearFor( inMap );
		}
		
		// Clear the map
		dataMap.clear();
	}

	/**
	 * Check the map for a mapping between the string and a Stack< BlockSet >.
	 * If there is no mapping, one is created. <p>
	 * 
	 * @param playerName	: String identifier for the player / user
	 * 
	 * @return The Stack< BlockSet > mapped to the string passed into the
	 * function.
	 */
	private Stack< BlockSet > getStorageFor( String playerName )
	{
		// Check if the player is in the map
		if( ! ( dataMap.containsKey( playerName ) ) )
		{
			// Add if not there
			dataMap.put( playerName , new Stack< BlockSet >() );
		}
		
		// Return the storage
		return dataMap.get( playerName );
	}
}
