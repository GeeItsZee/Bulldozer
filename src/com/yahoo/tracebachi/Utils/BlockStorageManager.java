package com.yahoo.tracebachi.Utils;

import java.util.HashMap;
import java.util.Stack;

import org.bukkit.World;

public class BlockStorageManager
{

	// Class Variables
	private HashMap< String , BlockStorage > dataMap = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	BlockStorageManager Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public BlockStorageManager()
	{
		// Create a new map
		dataMap = new HashMap< String , BlockStorage >();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	pushGroupFor
	// Purpose: 	Push a pre-made group into the stack for the passed player
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean pushGroupFor( String playerName , BlockGroup toStore )
	{
		// Add a new group to the storage
		return getStorageFor(playerName).pushGroup( toStore );
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	revertBlocksFor
	// Purpose: 	Revert the top group on the stack to their original IDs
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean revertBlocksFor( String playerName , World playerWorld )
	{
		// Revert the blocks
		return getStorageFor( playerName ).revertTop( playerWorld ) ;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	clearAllStoredBlocksFor
	// Purpose: 	Wipe the stack for the passed player
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void clearAllStoredBlocksFor( String playerName )
	{
		// Clear for player
		getStorageFor( playerName ).clearGroups();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	clearAllStoredBlocks
	// Purpose: 	Wipe the stacks for all players
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean clearAllStoredBlocks()
	{
		// Loop through the map and clear all groups
		for( BlockStorage inMap : dataMap.values() )
		{
			inMap.clearGroups();
		}
		
		// Return when complete
		return true;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	clearEverythingForAll
	// Purpose: 	Clean up the class by clearing and closing everything before plugin disable
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void clearEverythingForAll()
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
	private BlockStorage getStorageFor( String playerName )
	{
		// Check if the player is in the map
		if( ! ( dataMap.containsKey( playerName ) ) )
		{
			// Add if not there
			dataMap.put( playerName , new BlockStorage() );
		}
		
		// Return the storage
		return dataMap.get( playerName );
	}

	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Class: 	BlockStorage
	// Purpose: 	Store the block groups for ONE player
	//////////////////////////////////////////////////////////////////////////////////////////////
	public class BlockStorage
	{
		
		// Class Variables
		private Stack< BlockGroup > groups = null;
		
		/////////////////////////////////////////////////////////////////////////////////////////
		// Method: 	Block Storage Default Constructor
		/////////////////////////////////////////////////////////////////////////////////////////
		public BlockStorage()
		{
			// Make a new stack
			groups = new Stack< BlockGroup >();
		}
		
		
		/////////////////////////////////////////////////////////////////////////////////////////
		// Method: 	pushGroup
		// Purpose: 	Push a reference to the passed block group into the stack
		/////////////////////////////////////////////////////////////////////////////////////////
		public boolean pushGroup( BlockGroup toPush )
		{
			// Check if the reference is null
			if( toPush != null )
			{
				// Push the reference
				groups.push( toPush );
				
				// Set own reference to null
				toPush = null;
				return true;
			}
			
			// Return false for bad reference
			return false;
		}
		
		
		/////////////////////////////////////////////////////////////////////////////////////////
		// Method: 	popGroup
		// Purpose: 	Remove the top of the stack and clear all the blocks inside for a safe
		// 			removal of the data
		/////////////////////////////////////////////////////////////////////////////////////////
		public boolean popGroup()
		{
			// Verify not empty
			if( !groups.isEmpty() )
			{
				// Pop the top and return true
				groups.pop().clearAllBlocks();
				return true;
			}
			
			// Else
			return false;
		}
		
		
		/////////////////////////////////////////////////////////////////////////////////////////
		// Method: 	revertTop
		// Purpose: 	Pop the top of the stack and revert all the blocks to their original IDs
		/////////////////////////////////////////////////////////////////////////////////////////
		public boolean revertTop( World toRevertIn )
		{
			// Verify group is not empty
			if( !groups.isEmpty() )
			{
				// Revert the top of the group
				return groups.pop().revertBlocks( toRevertIn );
			}
			
			// Otherwise
			return false;
		}
		
		
		/////////////////////////////////////////////////////////////////////////////////////////
		// Method: 	isStorageEmpty
		// Purpose: 	Check and return if the stacks is empty
		/////////////////////////////////////////////////////////////////////////////////////////
		public boolean isStorageEmpty()
		{
			// Check if the groups is empty
			return groups.isEmpty();
		}
		
		
		/////////////////////////////////////////////////////////////////////////////////////////
		// Method: 	clearGroups
		// Purpose: 	Pop all the groups and clear them as they come out
		/////////////////////////////////////////////////////////////////////////////////////////
		public void clearGroups()
		{
			// Loop until done
			while( !groups.isEmpty() )
			{
				// Clear them with the custom method to ensure removal
				groups.pop().clearAllBlocks();
			}
		}
		
	}
		
}
