package com.yahoo.tracebachi.Utils;

import java.util.HashMap;
import java.util.Stack;

import org.bukkit.block.Block;

public class BlockGroupManager
{

	// Class Variables
	private HashMap< String , Stack< BlockGroup > > dataMap = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	BlockStorageManager Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public BlockGroupManager()
	{
		// Create a new map
		dataMap = new HashMap< String , Stack < BlockGroup > >();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	pushGroupFor
	// Purpose: 	Push a pre-made group into the stack for the passed player
	//////////////////////////////////////////////////////////////////////////////////////////////
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
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	revertBlocksFor
	// Purpose: 	Revert the top group on the stack to their original IDs
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean revertBlocksFor( String playerName )
	{
		// Verify the stack is not empty
		if( ! getStorageFor( playerName ).isEmpty() )
		{
			// Revert the blocks
			return getStorageFor( playerName ).pop().revertBlocks( true );
		}
		
		// Otherwise
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	pasteBlockFor
	// Purpose: 	Make a copy of the blocks starting from 'startPoint' and return it for storage
	//			in an undo storage
	//////////////////////////////////////////////////////////////////////////////////////////////
	public BlockGroup duplicateBlocksFor( String playerName , Block startPoint )
	{
		// Verify the stack is not empty
		if( ! getStorageFor( playerName ).isEmpty() )
		{
			return getStorageFor( playerName ).peek().duplicateBlocks( startPoint.getX() , startPoint.getY() , startPoint.getZ() );
		}
		
		// Otherwise
		return null;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	clearAllStoredBlocksFor
	// Purpose: 	Wipe the stack for the passed player
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void clearBlocksFor( String playerName )
	{
		// Create a temporary reference to the stack
		Stack< BlockGroup > tempGroup = getStorageFor( playerName ); 
			
		// While not empty
		while( ! tempGroup.isEmpty() )
		{
			// Clear the blocks in the group
			tempGroup.pop().clearBlocks();
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	clearAllStoredBlocks
	// Purpose: 	Wipe the stacks for all players
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void clearAllStoredBlocks()
	{
		// Loop through the map and clear all groups
		for( Stack< BlockGroup > inMap : dataMap.values() )
		{
			// While not empty
			while( ! inMap.isEmpty() )
			{
				// Clear the blocks in the group
				inMap.pop().clearBlocks();
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	clearEverythingForAll
	// Purpose: 	Clean up the class by clearing and closing everything before plugin disable
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void closeManager()
	{
		// Clear all storage
		clearAllStoredBlocks();
		
		// Clear the map
		dataMap.clear();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getStorageFor
	// Purpose: 	Get the stack for the passed player
	//////////////////////////////////////////////////////////////////////////////////////////////
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
