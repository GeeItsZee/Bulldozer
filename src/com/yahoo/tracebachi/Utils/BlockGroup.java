package com.yahoo.tracebachi.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * BlockGroup <p>
 * Used for storage of data pertaining to a single group of blocks. <p>
 * 
 * @author Alias: TheCriticalError
 */
@SuppressWarnings("deprecation")
public class BlockGroup
{
	// Static constants
	public static final double ANGLE_TO_RADS = (Math.PI * 2) / 360.0;
	
	// Class Variables
	private List< BlockInfo > blockInfoList = null;
	private ListIterator< BlockInfo > iterForNext = null;
	
	// Key Block: The block from which the offsets for pasting is determined
	private int keyX , keyY , keyZ;
	
	/**
	 * Constructs the BlockGroup object with a new ArrayList of BlockInfo.
	 */
	public BlockGroup()
	{
		// Create the blockInfoList
		blockInfoList = new ArrayList< BlockInfo >();
		
		// Initialize the key variables
		keyX = keyY = keyZ = 0;
	}
	
	/**
	 * Constructs, allocates, and inserts a new {@link BlockInfo} object with 
	 * the parameters passed to the method. <p>
	 * 
	 * @param x		: X-Coordinate of the block
	 * @param y		: Y-Coordinate of the block
	 * @param z		: Z-Coordinate of the block
	 * @param type		: Main ID of the block
	 * @param subType	: Data of the block
	 * 
	 * @return Boolean value indicating true if the block info was inserted 
	 * into the list or false if not.
	 */
	public boolean addBlock( int x , int y , int z , int type , byte subType )
	{
		// Add it to the blockInfoList
		return blockInfoList.add( new BlockInfo( x , y , z , type , subType ) );
	}
	
	/**
	 * Constructs, allocates, and inserts a new {@link BlockInfo} object from 
	 * the x, y, and z coordinates, block ID, and block data of the block object. <p>
	 * 
	 * @param toAdd	: Block object
	 * 
	 * @return Boolean value indicating true if the block info was inserted 
	 * into the list or false if not.
	 */
	public boolean addBlock( Block toAdd )
	{
		// Add it to the blockInfoList
		return blockInfoList.add( new BlockInfo( toAdd.getX() , toAdd.getY() , toAdd.getZ() , toAdd.getTypeId() , toAdd.getData() ) );
	}
	
	/**
	 * Constructs, allocates, and inserts a new {@link BlockInfo} object with
	 * the information of the passed Block object. However, the block's type
	 * and subType are changed on the server with the values passed to 
	 * the function. <p>
	 * 
	 * @param toAdd	: Block object
	 * @param type		: Main ID of the block after the original is stored
	 * @param subType	: SubID of the block after the original is stored
	 */
	public void addBlockAndChange( Block toAdd , int type , byte subType )
	{
		// Add the original values to the blockInfoList
		blockInfoList.add( new BlockInfo( toAdd.getX() , toAdd.getY() , toAdd.getZ() , toAdd.getTypeId() , toAdd.getData() ) );
		
		// Modify the block
		toAdd.setTypeId( type );
		toAdd.setData( subType );
	}
	
	/**
	 * Sets the x, y, and z coordinates (key coordinates) used for relative load
	 * and paste operations from the passed block object. <p>
	 * 
	 * @param srcBlock	: Block object
	 */
	public void setKeyBlock( Block srcBlock )
	{
		// Set the values
		keyX = srcBlock.getX();
		keyY = srcBlock.getY();
		keyZ = srcBlock.getZ();
	}
	
	/**
	 * Sets the x, y, and z coordinates (key coordinates) used for relative load
	 * and paste operations from the integers passed to the function. <p>
	 * 
	 * @param x	: X-Coordinate
	 * @param y	: Y-Coordinate
	 * @param z	: Z-Coordinate
	 */
	public void setKeyBlock( int x , int y , int z )
	{
		// Set the values
		keyX = x;
		keyY = y;
		keyZ = z;
	}

	/**
	 * Fetches the block in the passed world corresponding with the key values 
	 * stored in the class. <p>
	 * 
	 * @param playerWorld	: World to fetch the block from
	 * 
	 * @return A block object that represents the "key" block.
	 */
	public Block getKeyBlock( World playerWorld )
	{
		// Set the values
		return playerWorld.getBlockAt( keyX , keyY , keyZ );
	}
	
	/**
	 * Using the key block data stored in the class, this function iterates
	 * through the block information list and recalculates the coordinates
	 * off a key block of (0,0,0). <p>
	 */
	public void recalculateCoordinates()
	{
		// Method variables
		BlockInfo node = null;
		ListIterator< BlockInfo > iter = blockInfoList.listIterator();
		
		// Loop through the blockInfoList
		while( iter.hasNext() )
		{
			// Get the block info using iterator
			node = iter.next();
			
			// Update coordinates
			node.xPos = node.xPos - keyX;
			node.yPos = node.yPos - keyY;
			node.zPos = node.zPos - keyZ;
		}
		
		// Set the key values
		keyX = keyY = keyZ = 0;
	}
	
	/**
	 * Using a list iterator through the list stored in the class, this method 
	 * restores all blocks into their original positions in the world that 
	 * was passed as the argument. If the clearList boolean is true, the list 
	 * will be wiped on exit from this function. <p>
	 * 
	 * @param playerWorld	: World to place blocks in
	 * @param clearList		: Boolean instructing on whether or not to clear the 
	 * 						list on exit
	 */
	public void restoreBlocks( World playerWorld , boolean clearList )
	{
		// Method variables
		BlockInfo node = null;
		Block cursorBlock = null;
		ListIterator< BlockInfo > iter = blockInfoList.listIterator();
		
		// Loop through the blockInfoList
		while( iter.hasNext() )
		{
			// Get the block info using iterator
			node = iter.next();
			cursorBlock = playerWorld.getBlockAt( node.xPos , node.yPos , node.zPos );
			
			// Revert the block
			cursorBlock.setTypeId( node.blockID );
			cursorBlock.setData( node.blockSubID );
		}
		
		// Check the 'clearList' boolean and clear if needed
		if( clearList ) { clearBlockInfo(); }
	}
	
	/**
	 * Recreates the blocks from the stored list by subtracting the key values
	 * of the class from the passed values in order to establish an offset
	 * for each of the coordinates. An iterator through the list gets the block
	 * being modified and stores the block info before the recreation into a 
	 * temporary BlockGroup. Once the list loop has completed, the temporary 
	 * BlockGroup is returned. <p>
	 * 
	 * @param playerWorld	: World to place the blocks in
	 * @param x			: Starting X-Coordinate
	 * @param y			: Starting Y-Coordinate
	 * @param z			: Starting Z-Coordinate
	 * 
	 * @return BlockGroup containing all the blocks that were edited by
	 * the recreation from the current class's block information list.
	 */
	public BlockGroup recreateAt( World playerWorld, int x , int y , int z )
	{
		// Method variables
		int offX , offY , offZ;
		BlockGroup toReturn = new BlockGroup();
		BlockInfo node = null;
		Block cursorBlock = null;
		ListIterator< BlockInfo > iter = blockInfoList.listIterator();
		
		// Verify the list is not empty
		if( ! blockInfoList.isEmpty() )
		{
			// Figure out the offset
			offX = x - keyX;
			offY = y - keyY;
			offZ = z - keyZ;
			
			// Loop through the blockInfoList
			while( iter.hasNext() )
			{
				// Get the block info using iterator
				node = iter.next();
				cursorBlock = playerWorld.getBlockAt( node.xPos + offX , node.yPos + offY , node.zPos + offZ );
				
				// Add to the group
				toReturn.addBlock( cursorBlock );
				
				// Change the block
				cursorBlock.setTypeId( node.blockID );
				cursorBlock.setData( node.blockSubID );
			}
		}
		
		// Return
		return toReturn;
	}
	
	/**
	 * While iterating through the block information list, this function
	 * recalculates the new coordinates of the block after "rotating"
	 * the point relative to the key block. <p>
	 * 
	 * @param iterations	: Number of 90 degree rotations to perform.
	 */
	public void rotateRelativeToY( int iterations )
	{
		// Method variables
		double angleInRad = ((iterations * 90) % 360) * ANGLE_TO_RADS;
		double cosOfAngle = Math.round( Math.cos( angleInRad ) );
		double sinOfAngle = Math.round( Math.sin( angleInRad ) );
		int tempX = 0;
		BlockInfo node = null;
		
		// Recalculate the coordinates according to key block
		recalculateCoordinates();
		
		// Reset the iterator
		// Note: Order of recalculate and then initialization prevents 
		// ConcurrentModificationException caused by modification of list
		// through an "older" iterator
		ListIterator< BlockInfo > iter = blockInfoList.listIterator();
		
		// Loop through the list
		while( iter.hasNext() )
		{
			// Get the block info
			node = iter.next();
			tempX = node.xPos;
			
			// Adjust the coordinates for a fixed y-coordinate			
			node.xPos = (int) Math.round( (( tempX * cosOfAngle ) - ( node.zPos * sinOfAngle )) );
			node.zPos = (int) Math.round( (( tempX * sinOfAngle ) + ( node.zPos * cosOfAngle )) );
		}
	}
	
	/**
	 * Returns the size of the {@link BlockInfo} list in the class. <p>
	 * 
	 * @return The long value of the number of blocks stored in the list.
	 */
	public long getSize()
	{	
		// If the list is not empty
		if( blockInfoList != null )
		{
			// Return the size of the list
			return blockInfoList.size();
		}
	
		// Return 0 otherwise
		return 0;
	}
	
	/**
	 * Creates a deep copy of the current BlockGroup object. <p>
	 * 
	 * @return A new BlockGroup object identical to the one on which the 
	 * function was called.
	 */
	public BlockGroup getCopy()
	{	
		// Method variables
		BlockInfo node = null;
		BlockGroup toReturn = new BlockGroup();
		ListIterator< BlockInfo > iter = blockInfoList.listIterator();
		
		// Copy the key block
		toReturn.keyX = keyX;
		toReturn.keyY = keyY;
		toReturn.keyZ = keyZ;
		
		// Loop through the list using iterator
		while( iter.hasNext() )
		{
			// Get the node
			node = iter.next();
			
			// Push a copy of the data into the group
			toReturn.addBlock( node.xPos , node.yPos , node.zPos , node.blockID , node.blockSubID );
		}
	
		// Return the copied group otherwise
		return toReturn;
	}
	
	/**
	 * Fetches the next BlockInfo in the list if there is more to fetch. This
	 * information is stored in an integer array of size five and returned
	 * to the caller. <p>
	 * Note: The user must take care to reset the iterator to the beginning
	 * using the {@link BlockGroup#resetIterator() resetIterator} function. <p>
	 * 
	 * @param index	: Position of the BlockInfo from which the results
	 * are desired. If index is out of bounds, the list will throw an exception.
	 * 
	 * @return An integer array (all 0 by default) containing:
	 * <ul> 
	 * <li>At [0], X-Coordinate </li>
	 * <li>At [1], Y-Coordinate </li>
	 * <li>At [2], Z-Coordinate </li>
	 * <li>At [3], Type ID </li>
	 * <li>At [4], Sub-Type </li>
	 * </ul>
	 */
	public int[] getNext()
	{	
		// Method variables
		int[] toReturn = new int[5];
		BlockInfo node = null;
		
		// If the list is not empty
		if( iterForNext.hasNext() )
		{
			// Get the first block in the list
			node = iterForNext.next();
			
			// Copy the data
			toReturn[0] = node.xPos - keyX;
			toReturn[1] = node.yPos - keyY;
			toReturn[2] = node.zPos - keyZ;
			
			toReturn[3] = node.blockID;
			toReturn[4] = node.blockSubID;
			
			// Return the first block
			return toReturn;
		}

		// Return the empty array (Java: arrays are 0 by default)
		return toReturn;
	}
	
	public Location getFirstLocation( World targetWorld )
	{
		// Method variables
		BlockInfo node = null;
		
		if( blockInfoList.size() != 0 )
		{
			node = blockInfoList.get( 0 );
			
			return new Location( targetWorld,
				node.xPos,
				node.yPos,
				node.zPos );
		}
		
		return null;
	}
	
	/**
	 * Resets the internal iterator to the beginning of the BlockInfo list.
	 */
	public void resetIterator()
	{
		// Check if the list exists
		if( blockInfoList != null )
		{
			// Reset the iterator to the beginning
			iterForNext = blockInfoList.listIterator();
		}
	}
	
	/**
	 * Iterates through the list and searches for the highest values for the
	 * x, y, and z coordinates.
	 * 
	 * @return {@link Location} object or null for empty list.
	 */
	public Location getMaxLocation( World targetWorld )
	{
		// Initialize variables
		int compareVal;
		int[] values = new int[3];
		BlockInfo node = null;
		ListIterator< BlockInfo > iter = blockInfoList.listIterator();
		
		// Check if the list empty
		if( !isEmpty() )
		{
			// Set the initial values
			node = iter.next();
			values[0] = node.xPos;
			values[1] = node.yPos;
			values[2] = node.zPos;
			
			// Loop through the list
			while( iter.hasNext() )
			{
				// Get the next block info
				node = iter.next();
				
				// Compare X
				compareVal = node.xPos;
				if( compareVal > values[0] ) { values[0] = compareVal; }
				
				// Compare Y
				compareVal = node.yPos;
				if( compareVal > values[1] ) { values[1] = compareVal; }
				
				// Compare Z
				compareVal = node.zPos;
				if( compareVal > values[2] ) { values[2] = compareVal; }
			}
			
			// Return constructed location
			return new Location( targetWorld, 
				values[0], values[1], values[2] );
		}
		
		// Return unfound
		return null;
	}

	/**
	 * Iterates through the list and searches for the lowest values for the
	 * x, y, and z coordinates.
	 * 
	 * @return {@link Location} object or null for empty list.
	 */
	public Location getMinLocation( World targetWorld )
	{
		// Initialize variables
		int compareVal;
		int[] values = new int[3];
		BlockInfo node = null;
		ListIterator< BlockInfo > iter = blockInfoList.listIterator();
		
		// Check if the list empty
		if( !isEmpty() )
		{
			// Set the initial values
			node = iter.next();
			values[0] = node.xPos;
			values[1] = node.yPos;
			values[2] = node.zPos;
			
			// Loop through the list
			while( iter.hasNext() )
			{
				// Get the next block info
				node = iter.next();
				
				// Compare X
				compareVal = node.xPos;
				if( compareVal < values[0] ) { values[0] = compareVal; }
				
				// Compare Y
				compareVal = node.yPos;
				if( compareVal < values[1] ) { values[1] = compareVal; }
				
				// Compare Z
				compareVal = node.zPos;
				if( compareVal < values[2] ) { values[2] = compareVal; }
			}
			
			// Return constructed location
			return new Location( targetWorld, 
				values[0], values[1], values[2] );
		}
		
		// Return unfound
		return null;
	}

	/**
	 * Fetches the chunk of the passed world that contains the Block specified
	 * by the BlockInfo at the {@code index} position. The Block is
	 * reconstructed from the information and the passed world. The
	 * chunk returned is gathered by calling the chunk method of the
	 * block. <p>
	 * 
	 * @param playerWorld	: World to fetch the block from
	 * @param index		: Index of the desired BlockInfo. If index is out 
	 * of bounds, the list will throw an exception.
	 * 
	 * @return Reference to the chunk that contained the block specified by
	 * the BlockInfo in the list at {@code index}. If the index is negative, a
	 * null chunk will be returned, but an exception will be thrown before
	 * that.
	 */
	public Chunk getChunkOfBlock( World playerWorld , int index )
	{	
		// Method variables
		BlockInfo node = null;
		
		// If the list is not empty
		if( index < blockInfoList.size() )
		{
			// Get the i block in the list
			node = blockInfoList.get( index );
			
			// Return the chunk containing the block
			return playerWorld.getBlockAt( node.xPos , node.yPos , node.zPos ).getChunk();
		}

		// Return the null chunk otherwise
		return null;
	}

	/**
	 * Returns if the BlockGroup's BlockInfo list is empty. The function is a 
	 * wrapper for the list's isEmpty function. <p>
	 * 
	 * @return Boolean value of true if the BlockInfo list is empty and false if not.
	 */
	public boolean isEmpty()
	{
		// Return if the list is empty or not
		return blockInfoList.isEmpty();
	}

	/**
	 * Clears the list of BlockInfo regardless of whether or not the list is 
	 * already empty or not. <p>
	 */
	public void clearBlockInfo()
	{
		// Clear the blockInfoList
		blockInfoList.clear();
	}
	
	/**
	 * Data object containing all relevant information for a Block without
	 * the large storage overhead. Elements include: <p>
	 * 
	 * <ul>
	 * <li>X-Coordinate</li>
	 * <li>Y-Coordinate</li>
	 * <li>Z-Coordinate</li>
	 * <li>Block ID</li>
	 * <li>Block SubID</li>
	 * </ul>
	 */
	private class BlockInfo
	{
		// Class Variables
		public int xPos = 0 ;
		public int yPos = 0 ;
		public int zPos = 0 ;
		public int blockID = 0 ;
		public byte blockSubID = 0 ;
		
		/**
		 * Copies the data passed to the constructor into the object's
		 * private internal variable storage. <p>
		 * 
		 * @param x		: X-Coordinate
		 * @param y		: Y-Coordinate
		 * @param z		: Z-Coordinate
		 * @param type		: Block ID
		 * @param subType	: Block SubID (for materials with states)
		 */
		public BlockInfo( int x , int y , int z , int type , byte subType )
		{
			// Copy over the values
			xPos = x ; yPos = y ; zPos = z ;
			blockID = type ;
			blockSubID = subType ;
		}
	}
			
}