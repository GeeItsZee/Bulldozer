package com.yahoo.tracebachi.Utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockSet
{
	// Static class constant
	public static final int INVALID_OBJ = -1;
	public static final double DEGREE_TO_RADIAN = (Math.PI * 2) / 360.0;
	
	// Class Variables
	private int keyX = 0;
	private int keyY = 0;
	private int keyZ = 0;
	private Set< BlockInfo > data = new HashSet< BlockInfo >();
	
	// Add Block
	@SuppressWarnings("deprecation")
	public boolean addBlock( Block toAdd )
	{
		// Verify not null
		if( toAdd == null ) { return false; }
		
		// Add to the set
		return data.add( new BlockInfo(
			toAdd.getX(),
			toAdd.getY(),
			toAdd.getZ(),
			toAdd.getTypeId(),
			toAdd.getData() ) );
	}
	
	// Add Block
	public boolean addBlock( BlockInfo toAdd )
	{
		// Verify not null
		if( toAdd == null ) { return false; }
		
		// Add to the set
		return data.add( toAdd );
	}
	
	public int getSize()
	{
		// Check if map is null
		if( data == null )
		{
			return BlockSet.INVALID_OBJ;
		}
		
		// Otherwise
		return data.size();
	}

	// Get Key Block
	public Block getKeyBlock( World targetWorld )
	{
		// Verify not null
		if( targetWorld == null ) { return null; }

		// Otherwise
		return targetWorld.getBlockAt( keyX, keyY, keyZ );
	}
	
	public Location getMaxLocation( World targetWorld )
	{
		// Initialize variables
		int[] coordinates = new int[3];
		int[] values = new int[3];
		BlockInfo node = null;
		Iterator< BlockInfo > iter = data.iterator();
		
		// Verify map is not empty
		if( data.isEmpty() ) { return null; }
		
		// Set values to first
		node = iter.next();
		values = node.getCoordinates();
		
		// While there are more in the set
		while( iter.hasNext() )
		{
			// Get information from object
			node = iter.next();
			coordinates = node.getCoordinates();
			
			// Compare X
			if( coordinates[0] > values[0] ) { values[0] = coordinates[0]; }
			
			// Compare Y
			if( coordinates[1] > values[1] ) { values[1] = coordinates[1]; }
			
			// Compare Z
			if( coordinates[2] > values[2] ) { values[2] = coordinates[2]; }
		}
		
		// Return a new location
		return new Location( targetWorld,
			values[0],
			values[1],
			values[2]);
	}

	public Location getMinLocation( World targetWorld )
	{
		// Initialize variables
		int[] coordinates = new int[3];
		int[] values = new int[3];
		BlockInfo node = null;
		Iterator< BlockInfo > iter = data.iterator();
		
		// Verify map is not empty
		if( data.isEmpty() ) { return null; }
		
		// Set values to first
		node = iter.next();
		values = node.getCoordinates();
		
		// While there are more in the set
		while( iter.hasNext() )
		{
			// Get information from object
			node = iter.next();
			coordinates = node.getCoordinates();
			
			// Compare X
			if( coordinates[0] < values[0] ) { values[0] = coordinates[0]; }
			
			// Compare Y
			if( coordinates[1] < values[1] ) { values[1] = coordinates[1]; }
			
			// Compare Z
			if( coordinates[2] < values[2] ) { values[2] = coordinates[2]; }
		}
		
		// Return a new location
		return new Location( targetWorld,
			values[0],
			values[1],
			values[2]);
	}
	
	public Set< BlockInfo > getImmutableVersion()
	{
		return Collections.unmodifiableSet( data );
	}
	
	public Set< Chunk > getChunkSet( World targetWorld )
	{
		// Method variables
		int[] coordinates = null;
		Set< Chunk > toReturn = new HashSet< Chunk >();
		
		// Verify non null world
		if( targetWorld == null ) { return null; }
		
		// Loop through the set
		for( BlockInfo iter : data )
		{
			// Get the coordinates
			coordinates = iter.getCoordinates();
			
			// Add the chunk at the location
			toReturn.add( targetWorld.getBlockAt( 
				coordinates[0], coordinates[1], coordinates[2] )
				.getChunk() );
		}
		
		// Return the set
		return toReturn;
	}

	// Set Key Block
	public void setKeyBlock( int x, int y, int z )
	{
		// Set the key block
		keyX = x;
		keyY = y;
		keyZ = z;
	}

	// Set Relative
	public void setRelativeToKeyBlock()
	{
		// Loop through the set
		for( BlockInfo iter : data )
		{
			// Subtract from the coordinates
			iter.subtractRelative( keyX, keyY, keyZ );
		}
		
		// Set the key values to 0
		keyX = keyY = keyZ = 0;
	}
	
	// Restore
	@SuppressWarnings("deprecation")
	public boolean restoreInWorld( boolean clear, World targetWorld )
	{
		// Verify not null
		if( targetWorld == null ) { return false; }
		
		// Method variables
		int[] coordinates = null;
		int[] blockType = null;
		Block cursorBlock = null;
		
		// Loop through the set
		for( BlockInfo iter : data )
		{
			// Get the information from the object
			coordinates = iter.getCoordinates();
			blockType = iter.getData();
			
			// Get the block in the world
			cursorBlock = targetWorld.getBlockAt( 
				coordinates[0],
				coordinates[1],
				coordinates[2] );
			
			// Set the properties
			cursorBlock.setTypeId( blockType[0] );
			cursorBlock.setData( (byte) blockType[1] );
		}
		
		// Clear if needed
		if( clear ) 
		{
			// Clear the object and nullify the map
			data.clear();
			data = null;
		}
		
		// Return for completed
		return true;
	}
	
	// Recreate
	@SuppressWarnings("deprecation")
	public BlockSet recreateInWorld( boolean clear, int offsetX, int offsetY, 
		int offsetZ, World targetWorld )
	{
		// Verify not null
		if( targetWorld == null ) { return null; }
		
		// Method variables
		int[] coordinates = null;
		int[] blockType = null;
		Block cursorBlock = null;
		BlockSet toReturn = new BlockSet();
		
		// Loop through the set
		for( BlockInfo iter : data )
		{
			// Get the information from the object
			coordinates = iter.getCoordinates();
			blockType = iter.getData();
			
			// Get the block in the world
			cursorBlock = targetWorld.getBlockAt( 
				coordinates[0] - keyX + offsetX,
				coordinates[1] - keyY + offsetY,
				coordinates[2] - keyZ + offsetZ );
			
			// Add a copy to the return set
			toReturn.addBlock( cursorBlock );
			
			// Set the properties
			cursorBlock.setTypeId( blockType[0] );
			cursorBlock.setData( (byte) blockType[1] );
		}
		
		// Copy the key block information
		toReturn.keyX = keyX;
		toReturn.keyY = keyY;
		toReturn.keyZ = keyZ;
		
		// Clear if needed
		if( clear ) 
		{
			// Clear the object and nullify the map
			data.clear();
			data = null;
		}
		
		// Return the set with the updated blocks
		return toReturn;
	}
	
	public void rotateInPlane_XZ( int iterations )
	{
		// Method variables
		double angleInRad = ((iterations * 90) % 360) * DEGREE_TO_RADIAN;
		long cosOfAngle = Math.round( Math.cos( angleInRad ) );
		long sinOfAngle = Math.round( Math.sin( angleInRad ) );
		int[] coordinates = null;
		
		// Set Relative
		setRelativeToKeyBlock();
		
		// Loop through the list
		for( BlockInfo iter : data )
		{
			// Get the block info
			coordinates = iter.getCoordinates();
			
			// Adjust the coordinates for a fixed y-coordinate	
			iter.setX( (int) Math.round( 
				( coordinates[0] * cosOfAngle ) 
				- ( coordinates[2] * sinOfAngle ) ) );
			
			iter.setZ( (int) Math.round( 
				( coordinates[0] * sinOfAngle ) 
				+ ( coordinates[2] * cosOfAngle ) ) );
		}
	}
	
	public void clearForReuse()
	{
		// Reset members
		keyX = keyY = keyZ = 0;
		data.clear();
		data = new HashSet< BlockInfo >();
	}
	
	public void cleanup()
	{
		data.clear();
		data = null;
	}

	@Override
	public BlockSet clone()
	{
		// Method variables
		BlockSet toReturn = new BlockSet();
		
		// Copy the key block info
		toReturn.keyX = keyX;
		toReturn.keyY = keyY;
		toReturn.keyZ = keyZ;
		
		// Loop through the set
		for( BlockInfo iter : data )
		{
			// Store a duplicate in a new set
			toReturn.data.add( iter.clone() );
		}
		
		// Return the cloned object
		return toReturn;
	}
}
