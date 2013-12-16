package com.yahoo.tracebachi.Utils;

import java.util.HashMap;

import org.bukkit.block.Block;

public class SelectionManager
{
	// Class variables
	private HashMap< String , BlockGroup > map = null ;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	SelectionManager Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public SelectionManager()
	{
		map = new HashMap< String , BlockGroup >() ;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	addSelectionFor
	// Purpose: 	Add the passed block to the selection storage
	/////////////////////////////////////////////////////////////////////////////////////////
	public boolean addSelectionFor( String playerName , Block selectedBlock )
	{
		// Store a temporary reference to the group
		BlockGroup tempGroup;
		
		// Add player to map if not there
		if( !(map.containsKey( playerName )) )
		{
			map.put( playerName , new BlockGroup( selectedBlock.getWorld() ) );
		}
		
		// Set the temporary reference (to reduce processing for re-searching)
		tempGroup = map.get( playerName );
		
		// Check if this is the first block
		if( tempGroup.isEmpty() )
		{
			// Add modified as a gold block
			tempGroup.setKeyBlock( selectedBlock );
			return tempGroup.addBlockAndChange( selectedBlock , 41 , (byte) 0 );
		}
		else
		{
			// Add modified as glass
			return tempGroup.addBlockAndChange( selectedBlock , 20 , (byte) 0 );
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getSelectionFor
	// Purpose: 	Return the list of blocks in the selection
	/////////////////////////////////////////////////////////////////////////////////////////
	public BlockGroup getSelectionFor( String playerName )
	{
		// Check if player is in the map
		if( map.containsKey( playerName ) )
		{
			// Return the selection
			return map.get( playerName );
		}
		
		// Return null if not found
		return null;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getMaximumsFor
	// Purpose: 	Return the maximums of the selection for the player
	/////////////////////////////////////////////////////////////////////////////////////////
	public int[] getMaximumsFor( String playerName )
	{
		// Method Variables
		BlockGroup selectionList = getSelectionFor( playerName );
		int[] toReturn = new int[3];
		
		// If the selection was found
		if( selectionList != null )
		{
			// If selection is not empty
			if( !selectionList.isEmpty() )
			{
				// Set the values
				toReturn = selectionList.getMaximums();
			}
		}

		// Return the values
		return toReturn;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getMinimumsFor
	// Purpose: 	Return the minimums of the selection for the player
	/////////////////////////////////////////////////////////////////////////////////////////
	public int[] getMinimumsFor( String playerName )
	{
		// Method Variables
		BlockGroup selectionList = getSelectionFor( playerName );
		int[] toReturn = new int[3];
		
		// If the selection was found
		if( selectionList != null )
		{
			// If selection is not empty
			if( !selectionList.isEmpty() )
			{
				// Set the values
				toReturn = selectionList.getMinimums();
			}
		}

		// Return the values
		return toReturn;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	removeSelectionFor
	// Purpose: 	Remove the player from the selection storage
	/////////////////////////////////////////////////////////////////////////////////////////
	public boolean removeSelectionFor( String playerName )
	{	
		// Check if player is in the map
		if( map.containsKey( playerName ) )
		{	
			// Remove the player from the map and revert the blocks (revert always returns true)
			return map.remove( playerName ).revertBlocks( true );
		}
		
		// Otherwise
		return false;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	removeAll
	// Purpose: 	Clear the selection storage of all mappings and selections
	/////////////////////////////////////////////////////////////////////////////////////////
	public void removeAll()
	{
		// Initialize values
		BlockGroup selected = null;
		
		// Remove all entries in the map
		for( BlockGroup inMap : map.values() )
		{
			selected = inMap;
			selected.revertBlocks( true );
		}
		
		// Clear the map
		map.clear();
	}
	
}
