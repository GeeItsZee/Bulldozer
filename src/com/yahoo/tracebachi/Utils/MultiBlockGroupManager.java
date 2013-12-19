package com.yahoo.tracebachi.Utils;

import java.util.HashMap;
import java.util.Stack;

import org.bukkit.World;

/**
 * MultiBlockGroupManager <p>
 * Used for the storage of multiple BlockGroups per user identified with a string.
 * 
 * @author Alias: TheCriticalError
 */
public class MultiBlockGroupManager
{
	// Class Variables
	private HashMap< String , Stack< BlockGroup > > dataMap = null;
	
	/**
	 * Constructs the MultiBlockGroupManager with a HashMap relating
	 * String to {@code Stack< BlockGroup >}. <p>
	 */
	public MultiBlockGroupManager()
	{
		// Create a new map
		dataMap = new HashMap< String , Stack < BlockGroup > >();
	}

	/**
	 * Pushes a new and pre-made block group into the stack for the player
	 * identified by the player name-string. <p>
	 * 
	 * @param playerName	: Name of the player to store for
	 * @param toStore		: Reference to the BlockGroup to push into 
	 * the stack
	 * 
	 * @return Boolean value of true if the BlockGroup was not null and
	 * was stored in the stack and false if the group was null.
	 */
	public boolean pushGroupFor( String playerName , BlockGroup toStore )
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
	 * Checks there are any BlockGroups stored in the stack mapped to the
	 * name-string. If there are, it returns a reference to the BlockGroup
	 * at the top of the stack using peek(). NOTE: The group can be modified
	 * while the reference is live. If major modifications need to be made,
	 * use the getCopy() function of BlockGroup so that the stored data is
	 * not corrupted. <p>
	 * 
	 * @param playerName	: String identifier of the player/user
	 * 
	 * @return The BlockGroup at the top of the stack OR null if the stack is
	 * empty.
	 */
	public BlockGroup peekGroupFor( String playerName )
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
	 * Checks there are any BlockGroups stored in the stack mapped to the
	 * name-string. If there are, it returns a reference to the BlockGroup
	 * at the top of the stack using pop(). NOTE: The group is permanently
	 * removed from the stack. If the reference is lost, the group is lost.
	 * 
	 * @param playerName	: String identifier of the player/user
	 * 
	 * @return The BlockGroup at the top of the stack OR null if the stack is
	 * empty.
	 */
	public BlockGroup popGroupFor( String playerName )
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

	/**
	 * Removes the player from the manager by removing the stack of BlockGroups.
	 * If {@code restore} is set to true, the BlockGroups will be reverted as
	 * they are popped off the stack. In which case, the world needs to be
	 * provided or a null pointer exception will be thrown. <p>
	 * 
	 * @param playerName	: String identifier for the player/user
	 * 
	 * @return The integer for the number of BlockGroups removed.
	 */
	public int removePlayer( String playerName , boolean restore , World playerWorld )
	{
		// Initialize counter
		int counter = 0;
		
		// Create a temporary reference to the stack
		Stack< BlockGroup > tempGroup = getStorageFor( playerName ); 
		
		// Check if restore is true
		if( restore )
		{
			// While not empty
			while( ! tempGroup.isEmpty() )
			{
				// Clear the blocks in the group
				tempGroup.pop().restoreBlocks( playerWorld , true );
				counter++;
			}
		}
		else
		{
			// While not empty
			while( ! tempGroup.isEmpty() )
			{
				// Clear the blocks in the group
				tempGroup.pop().clearBlockInfo();
				counter++;
			}
		}
		
		// Return counter
		return counter;
	}

	/**
	 * Resets the HashMap by removing all mappings and removing all BlockGroup
	 * stacks for all the players mapped. <p>
	 */
	public void closeManager()
	{
		// Loop through the map and clear all groups
		for( String inMap : dataMap.keySet() )
		{
			// Remove the player from the map
			removePlayer( inMap , false , null);
		}
		
		// Clear the map
		dataMap.clear();
	}

	/**
	 * Check the map for a mapping between the string and a Stack< BlockGroup >.
	 * If there is no mapping, one is created. <p>
	 * 
	 * @param playerName	: String identifier for the player / user
	 * 
	 * @return The Stack< BlockGroup > mapped to the string passed into the
	 * function.
	 */
	private Stack< BlockGroup > getStorageFor( String playerName )
	{
		// Check if the player is in the map
		if( ! ( dataMap.containsKey( playerName ) ) )
		{
			// Add if not there
			dataMap.put( playerName , new Stack< BlockGroup >() );
		}
		
		// Return the storage
		return dataMap.get( playerName );
	}
		
}
