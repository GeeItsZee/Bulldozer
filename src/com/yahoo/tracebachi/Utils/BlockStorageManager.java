package com.yahoo.tracebachi.Utils;

import java.util.HashMap;
import java.util.Stack;

import org.bukkit.World;

public class BlockStorageManager
	{

		// Class Variables
		private HashMap< String , BlockStorage > dataMap = null;
		
		// Default Constructor
		public BlockStorageManager()
			{
				
				// Create a new map
				dataMap = new HashMap< String , BlockStorage >();
				
			}
		
		// Method: pushGroupFor
		// Purpose: Push a new group into the stack for storage of blocks
		public boolean pushGroupFor( String playerName , BlockGroup toStore )
			{
				
				// Add a new group to the storage
				return getStorageFor(playerName).pushGroup( toStore );
				
			}
		
		// Method: revertBlocksFor
		// Purpose: Revert the blocks of the top group and then pop that group
		public boolean revertBlocksFor( String playerName , World playerWorld )
			{
				
				// Revert the blocks
				return getStorageFor( playerName ).revertTop( playerWorld ) ;
				
			}
		
		// Method: clearAllStoredBlocksFor
		// Purpose: Remove all groups for the player
		public void clearAllStoredBlocksFor( String playerName )
			{
				
				// Clear for player
				getStorageFor( playerName ).clearGroups();
				
			}
		
		// Method: clearAllStoredBlocks
		// Purpose: Remove all groups for all players
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
		
		// Method: clearEverythingForAll
		// Purpose: Remove all groups and remove all data map mappings
		public void clearEverythingForAll()
			{
				
				// Clear all storage
				clearAllStoredBlocks();
				
				// Clear the map
				dataMap.clear();
				
			}
		
		// Method: getStorageFor
		// Purpose: Get the Storage object assigned to the player
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
		
		/////////////////////////////////////////////////////////////////////////////////////////////
		// Class: Block Storage
		// Purpose: Store the block groups for ONE player
		public class BlockStorage
		{
			
			// Class Variables
			private Stack< BlockGroup > groups = null;
			
			// Default Constructor
			public BlockStorage()
				{
					
					// Make a new stack
					groups = new Stack< BlockGroup >();
					
				}
			
			// Method: pushGroup
			// Purpose: Push a reference to the passed block group
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
			
			// Method: popGroup
			// Purpose: Remove the top-most stack and clear all the blocks inside (safe removal)
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
			
			// Method: revertTop
			// Purpose: Revert all the blocks at the top of the group
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
			
			// Method: isStorageEmpty
			// Purpose: Check if the stack is empty
			public boolean isStorageEmpty()
				{
					
					// Check if the groups is empty
					return groups.isEmpty();
					
				}
			
			// Method: clearGroups
			// Purpose: Pop all the groups and clear them as they come out
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
