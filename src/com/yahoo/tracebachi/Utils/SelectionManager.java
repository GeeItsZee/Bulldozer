package com.yahoo.tracebachi.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;

public class SelectionManager
	{
		
		// Class variables
		private HashMap< String , List< Location > > map = null ;
		
		// Default Constructor
		public SelectionManager()
			{
				
				map = new HashMap< String , List < Location > >() ;
				
			}
		
		// Method: Add to Selection
		public boolean addSelectionFor( String playerName , Location selectedBlock )
			{
				
				// Add player to map if not there
				if( !(map.containsKey( playerName )) )
					{
						
						map.put( playerName , new ArrayList< Location >() );
						
					}
				
				// Check if block is already in the list
				if( !(map.get( playerName ).contains( selectedBlock )) )
					{
				
						// Add to the list
						map.get( playerName ).add( selectedBlock );
						return true;
						
					}
				
				// Return false for un-added / already exists
				return false;

			}
		
		// Method: Get Selections
		public List< Location > getSelectionFor( String playerName )
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
		
		// Method: getMaximumsFor
		// Purpose: Get the maximum X, Y, and Z from the selection
		public int[] getMaximumsFor( String playerName )
			{
				
				// Method Variables
				List< Location > selectionList = getSelectionFor( playerName );
				int listSize = 0 ;
				int toCompare = 0 ;
				int[] toReturn = new int[3];
				
				// Check if the list is null (Exit if needed)
				if( selectionList == null || selectionList.isEmpty() ){ return toReturn; }
				
				// Set the size if not null
				listSize = selectionList.size() ;
				
				// Set return to the first block's X, Y, and Z
				toReturn[0] = selectionList.get(0).getBlockX() ;
				toReturn[1] = selectionList.get(0).getBlockY() ;
				toReturn[2] = selectionList.get(0).getBlockZ() ;
				
				// Loop through the rest of the list to find one bigger
				for( int counter = 1 ; counter < listSize ; counter++ )
					{
						
						// Set the compare variable for X
						toCompare = selectionList.get( counter ).getBlockX();
						
						// If it is bigger, it is now the max
						if( toCompare > toReturn[0] )
							{
								
								toReturn[0] = toCompare ;
								
							}
						
						// Set the compare variable for Y
						toCompare = selectionList.get( counter ).getBlockY();
						
						// If it is bigger, it is now the max
						if( toCompare > toReturn[1] )
							{
								
								toReturn[1] = toCompare ;
								
							}
						
						// Set the compare variable for Z
						toCompare = selectionList.get( counter ).getBlockZ();
						
						// If it is bigger, it is now the max
						if( toCompare > toReturn[2] )
							{
								
								toReturn[2] = toCompare ;
								
							}
						
					}
				
				// Return the values
				return toReturn;
				
			}
		
		// Method: getMinimumsFor
		// Purpose: Get the minimum X, Y, and Z from the selection
		public int[] getMinimumsFor( String playerName )
			{
				
				// Method Variables
				List< Location > selectionList = getSelectionFor( playerName );
				int listSize = 0 ;
				int toCompare = 0 ;
				int[] toReturn = new int[3];
				
				// Check if the list is null (Exit if needed)
				if( selectionList == null || selectionList.isEmpty() ){ return toReturn; }
				
				// Set the size if not null
				listSize = selectionList.size() ;
				
				// Set return to the first block's X, Y, and Z
				toReturn[0] = selectionList.get(0).getBlockX() ;
				toReturn[1] = selectionList.get(0).getBlockY() ;
				toReturn[2] = selectionList.get(0).getBlockZ() ;
				
				// Loop through the rest of the list to find one bigger
				for( int counter = 1 ; counter < listSize ; counter++ )
					{
						
						// Set the compare variable for X
						toCompare = selectionList.get( counter ).getBlockX();
						
						// If it is smaller, it is now the min
						if( toCompare < toReturn[0] )
							{
								
								toReturn[0] = toCompare ;
								
							}
						
						// Set the compare variable for Y
						toCompare = selectionList.get( counter ).getBlockY();
						
						// If it is smaller, it is now the min
						if( toCompare < toReturn[1] )
							{
								
								toReturn[1] = toCompare ;
								
							}
						
						// Set the compare variable for Z
						toCompare = selectionList.get( counter ).getBlockZ();
						
						// If it is smaller, it is now the min
						if( toCompare < toReturn[2] )
							{
								
								toReturn[2] = toCompare ;
								
							}
						
					}
				
				// Return the values
				return toReturn;
				
			}
		
		// Method: getMaxDistanceFor
		// Purpose: Get the maximum distance between the origin block and the rest in the list
		public int getMaxDistanceFor( String playerName , Location origin )
			{
				
				// Method variables
				List< Location > selectionList = getSelectionFor( playerName );
				int listSize = 0;
				int toCompare = 0;
				int toReturn = 0;
				
				// Check if the list is null (Exit if needed)
				if( selectionList == null || selectionList.isEmpty() ){ return 0; }
				
				// Set the size if not null
				listSize = selectionList.size();
				
				// Set up the values to compare against
				int firstX = origin.getBlockX();
				int firstY = origin.getBlockY();
				int firstZ = origin.getBlockZ();
				
				int compareX , compareY , compareZ ;
				
				// Loop through the rest of the list to find one bigger
				for( int counter = 0 ; counter < listSize ; counter++ )
					{
						
						// Assign the compare values for the block
						compareX = selectionList.get( counter ).getBlockX();
						compareY = selectionList.get( counter ).getBlockY(); 
						compareZ = selectionList.get( counter ).getBlockZ();

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
		
		// Method: Remove selection
		public void removeSelectionFor( String playerName )
			{
				
				// Check if player is in the map
				if( map.containsKey( playerName ) )
					{
						
						// Remove the key and clear the list that is mapped to it
						map.get( playerName ).clear();
						map.remove( playerName );
						
					}

			}
		
		// Method: Remove all selections
		public void removeAll()
			{
				
				// Remove all entries in the map
				map.clear();
				
			}
		

	}
