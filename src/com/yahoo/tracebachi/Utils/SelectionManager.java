package com.yahoo.tracebachi.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.block.Block;

public class SelectionManager
{
	
	// Class variables
	private HashMap< String , List< SelBlock > > map = null ;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	SelectionManager Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public SelectionManager()
	{
		map = new HashMap< String , List < SelBlock > >() ;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	addSelectionFor
	// Purpose: 	Add the passed block to the selection storage
	/////////////////////////////////////////////////////////////////////////////////////////
	public boolean addSelectionFor( String playerName , Block selectedBlock )
	{
		// Add player to map if not there
		if( !(map.containsKey( playerName )) )
		{
				map.put( playerName , new ArrayList< SelBlock >() );
		}
		
		// Check if block is already in the list
		if( !(map.get( playerName ).contains( selectedBlock )) )
		{
			// Add to the list
			map.get( playerName ).add( new SelBlock( selectedBlock ) );
			return true;
		}
		
		// Return false for un-added / already exists
		return false;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getSelectionFor
	// Purpose: 	Return the list of blocks in the selection
	/////////////////////////////////////////////////////////////////////////////////////////
	public List< SelBlock > getSelectionFor( String playerName )
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
	// Purpose: 	Iterate through the selection list and return an 1D integer array of max
	//			points where [0] is x, [1] is y, [2] is z
	/////////////////////////////////////////////////////////////////////////////////////////
	public int[] getMaximumsFor( String playerName )
	{
		// Method Variables
		List< SelBlock > selectionList = getSelectionFor( playerName );
		int listSize = 0 ;
		int toCompare = 0 ;
		int[] toReturn = new int[3];
		
		// Check if the list is null (Exit if needed)
		if( selectionList == null || selectionList.isEmpty() ){ return toReturn; }
		
		// Set the size if not null
		listSize = selectionList.size() ;
		
		// Set return to the first block's X, Y, and Z
		toReturn[0] = selectionList.get(0).toStore.getX() ;
		toReturn[1] = selectionList.get(0).toStore.getY() ;
		toReturn[2] = selectionList.get(0).toStore.getZ() ;
		
		// Loop through the rest of the list to find one bigger
		for( int counter = 1 ; counter < listSize ; counter++ )
		{
			
			// Set the compare variable for X
			toCompare = selectionList.get( counter ).toStore.getX();
			
			// If it is bigger, it is now the max
			if( toCompare > toReturn[0] )
			{
				toReturn[0] = toCompare ;
			}
			
			// Set the compare variable for Y
			toCompare = selectionList.get( counter ).toStore.getY();
			
			// If it is bigger, it is now the max
			if( toCompare > toReturn[1] )
			{
				toReturn[1] = toCompare ;
			}
			
			// Set the compare variable for Z
			toCompare = selectionList.get( counter ).toStore.getZ();
			
			// If it is bigger, it is now the max
			if( toCompare > toReturn[2] )
			{
				toReturn[2] = toCompare ;
			}
		}
		
		// Return the values
		return toReturn;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getMinimumsFor
	// Purpose: 	Iterate through the selection list and return an 1D integer array of min
	//			points where [0] is x, [1] is y, [2] is z
	/////////////////////////////////////////////////////////////////////////////////////////
	public int[] getMinimumsFor( String playerName )
	{
		// Method Variables
		List< SelBlock > selectionList = getSelectionFor( playerName );
		int listSize = 0 ;
		int toCompare = 0 ;
		int[] toReturn = new int[3];
		
		// Check if the list is null (Exit if needed)
		if( selectionList == null || selectionList.isEmpty() ){ return toReturn; }
		
		// Set the size if not null
		listSize = selectionList.size() ;
		
		// Set return to the first block's X, Y, and Z
		toReturn[0] = selectionList.get(0).toStore.getX() ;
		toReturn[1] = selectionList.get(0).toStore.getY() ;
		toReturn[2] = selectionList.get(0).toStore.getZ() ;
		
		// Loop through the rest of the list to find one bigger
		for( int counter = 1 ; counter < listSize ; counter++ )
		{
			// Set the compare variable for X
			toCompare = selectionList.get( counter ).toStore.getX();
			
			// If it is smaller, it is now the min
			if( toCompare < toReturn[0] )
			{
				toReturn[0] = toCompare ;
			}
			
			// Set the compare variable for Y
			toCompare = selectionList.get( counter ).toStore.getY();
			
			// If it is smaller, it is now the min
			if( toCompare < toReturn[1] )
			{
				toReturn[1] = toCompare ;
			}
			
			// Set the compare variable for Z
			toCompare = selectionList.get( counter ).toStore.getZ();
			
			// If it is smaller, it is now the min
			if( toCompare < toReturn[2] )
			{	
				toReturn[2] = toCompare ;
			}
		}
		
		// Return the values
		return toReturn;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getMaxDistanceFor
	// Purpose: 	Iterate through the player's list and find the block farthest from the
	//			passed SelBlock
	/////////////////////////////////////////////////////////////////////////////////////////
	public int getMaxDistanceFor( String playerName , SelBlock origin )
	{
		// Method variables
		List< SelBlock > selectionList = getSelectionFor( playerName );
		int listSize = 0;
		int toCompare = 0;
		int toReturn = 0;
		
		// Check if the list is null (Exit if needed)
		if( selectionList == null || selectionList.isEmpty() ){ return 0; }
		
		// Set the size if not null
		listSize = selectionList.size();
		
		// Set up the values to compare against
		int firstX = origin.toStore.getX();
		int firstY = origin.toStore.getY();
		int firstZ = origin.toStore.getZ();
		
		int compareX , compareY , compareZ ;
		
		// Loop through the rest of the list to find one bigger
		for( int counter = 0 ; counter < listSize ; counter++ )
		{
			// Assign the compare values for the block
			compareX = selectionList.get( counter ).toStore.getX();
			compareY = selectionList.get( counter ).toStore.getY(); 
			compareZ = selectionList.get( counter ).toStore.getZ();

			// Generate the compare value
			toCompare = ((compareX - firstX) * (compareX - firstX)) +
					((compareY - firstY) * (compareY - firstY)) +
					((compareZ - firstZ) * (compareZ - firstZ));
			
			// Set the return value to the generated compare value
			if( toCompare > toReturn )
			{
				toReturn = toCompare;
			}
		}
		
		// Return the values
		return (int) Math.round( Math.sqrt( toReturn ) );	
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	removeSelectionFor
	// Purpose: 	Remove the player from the selection storage
	/////////////////////////////////////////////////////////////////////////////////////////
	public void removeSelectionFor( String playerName )
	{
		// Initialize variables
		List< SelBlock > selected = null;
		
		// Check if player is in the map
		if( map.containsKey( playerName ) )
		{
			// Remove the key and clear the list that is mapped to it
			selected = map.get( playerName );
			for( SelBlock i : selected )
				i.revert();
			
			// Remove the player from the map
			map.remove( playerName );
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	removeAll
	// Purpose: 	Clear the selection storage of all mappings and selections
	/////////////////////////////////////////////////////////////////////////////////////////
	public void removeAll()
	{
		// Initialize values
		List< SelBlock > selected = null;
		
		// Remove all entries in the map
		for( List<SelBlock> inMap : map.values() )
		{
			selected = inMap;
			for( SelBlock i : selected )
				i.revert();
			inMap.clear();
		}
		
		// Clear the map
		map.clear();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Class: 	selLoc
	// Purpose: 	To store the location and the block information of one block
	//////////////////////////////////////////////////////////////////////////////////////////////
	public class SelBlock
	{
		
		// Class variables
		public Block toStore = null;
		private int blockID = 0; 
		private byte blockData = 0;
		
		/////////////////////////////////////////////////////////////////////////////////////////
		// Method: 	SelBlock Constructor
		/////////////////////////////////////////////////////////////////////////////////////////
		public SelBlock( Block toInit )
		{			
			toStore = toInit;
			blockID = toInit.getTypeId();
			blockData = toInit.getData();
			toInit.setTypeId( 20 );
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////
		// Method: 	Revert
		// Purpose: 	Revert the block's previous state from before the selection
		/////////////////////////////////////////////////////////////////////////////////////////
		public void revert()
		{
			toStore.setTypeId( blockID );
			toStore.setData( blockData );
			toStore = null;
		}
	}
	
}
