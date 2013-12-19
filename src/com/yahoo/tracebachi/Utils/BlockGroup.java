package com.yahoo.tracebachi.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
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
			
	// Class Variables
	private List< BlockInfo > blockInfoList = null;
	
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
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method:	updateSelection
	// Purpose:	Loop through the selection and check if the block was changed and update the 
	//			stored block data accordingly
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void updateSelection()
	{
		/*// Initialize variables
		int size = blockInfoList.size();
		Block cursorBlock = null;
		BlockInfo node = null;
		
		// Update the data in the selected blocks
		for( int i = 0 ; i < size ; i++ )
		{
			// Get the info
			node = blockInfoList.get( i );
			
			// Update the data of the block
			cursorBlock = groupWorld.getBlockAt( node.xPos , node.yPos , node.zPos );
			
			// Update the listing
			node.blockID = cursorBlock.getTypeId();
			node.blockSubID = cursorBlock.getData();
		}
		
		// Set the first to gold
		if( size != 0 )
		{
			// Get the info
			node = blockInfoList.get( 0 );
			
			// Update the data of the block
			cursorBlock = groupWorld.getBlockAt( node.xPos , node.yPos , node.zPos );
			
			// Set to gold
			cursorBlock.setTypeId( 41 );
		}
		
		// Set the rest to glass
		for( int i = 1 ; i < size ; i++ )
		{
			// Get the info
			node = blockInfoList.get( i );
			
			// Update the data of the block
			cursorBlock = groupWorld.getBlockAt( node.xPos , node.yPos , node.zPos );
			
			// Set to gold
			cursorBlock.setTypeId( 20 );
		}*/
	}
	
	/**
	 * Using the list stored in the class, this method restores all blocks into
	 * their original positions in the world that was passed to the function.
	 * If the clearList boolean is true, the list will be wiped on exit from
	 * this function. <p>
	 * 
	 * @param playerWorld	: World to place blocks in
	 * @param clearList		: Boolean instructing on whether or not to clear the 
	 * 						list on exit
	 */
	public void restoreBlocks( World playerWorld , boolean clearList )
	{
		// Method variables
		int size = blockInfoList.size();
		BlockInfo node = null;
		Block cursorBlock = null;
		
		// Loop through the blockInfoList
		for( int iter = 0 ; iter < size ; iter++ )
		{
			// Get the block
			node = blockInfoList.get( iter );
			cursorBlock = playerWorld.getBlockAt( node.xPos , node.yPos , node.zPos );
			
			// Revert the block
			cursorBlock.setTypeId( node.blockID );
			cursorBlock.setData( node.blockSubID );
		}
		
		// Check the 'clearList' boolean
		if( clearList ) { clearBlockInfo(); }
	}
	
	/**
	 * Recreates the blocks from the stored list by subtracting the key values
	 * of the class from the passed values in order to establish an offset
	 * for each of the coordinates. Using this offset value, a loop through
	 * the list stores the current state of each of the blocks at the same
	 * coordinates in a temporary BlockGroup object. Once the list loop
	 * has completed, the temporary BlockGroup is returned. <p>
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
		int blockInfoListSize = blockInfoList.size();
		int offX , offY , offZ;
		BlockGroup toReturn = new BlockGroup();
		BlockInfo node = null;
		Block cursorBlock = null;
		
		// Verify the list is not empty
		if( ! blockInfoList.isEmpty() )
		{
			// Figure out the offset
			offX = x - keyX;
			offY = y - keyY;
			offZ = z - keyZ;
			
			// Loop through the blockInfoList
			for( int iter = 0 ; iter < blockInfoListSize ; iter++ )
			{
				// Get the block
				node = blockInfoList.get( iter );
				cursorBlock = playerWorld.getBlockAt( node.xPos + offX , node.yPos + offY , node.zPos + offZ );
				
				// Add to the group
				toReturn.addBlock( cursorBlock );
				
				// Change the block
				cursorBlock.setTypeId( node.blockID );
				cursorBlock.setData( node.blockSubID );
			}
		}
		
		// Clear the blockInfoList and return
		return toReturn;
	}
	
	/**
	 * Returns the size of the {@link BlockInfo} list in the class. <p>
	 * 
	 * @return The long value of the number of blocks stored in the list.
	 */
	public long getSize()
	{	
		// Method variables
		int sizeVal = 0;
		
		// If the list is not empty
		if( blockInfoList != null )
		{
			// Return the size of the list
			return blockInfoList.size();
		}
	
		// Return 0 otherwise
		return sizeVal;
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
		int size = blockInfoList.size();
		BlockInfo node = null;
		BlockGroup toReturn = new BlockGroup();
		
		// Copy the key block
		toReturn.keyX = keyX;
		toReturn.keyY = keyY;
		toReturn.keyZ = keyZ;
		
		// Loop through the list and copy
		for( int i = 0 ; i < size ; i++ )
		{
			// Get the node
			node = blockInfoList.get( i );
			
			// Push a copy of the data into the group
			toReturn.addBlock( node.xPos , node.yPos , node.zPos , node.blockID , node.blockSubID );
		}
	
		// Return the copied group otherwise
		return toReturn;
	}
	
	/**
	 * Fetches the BlockInfo at the {@code index} position in the list. This
	 * information is stored in an integer array of size five and returned
	 * to the caller. <p>
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
	public int[] get( int index )
	{	
		// Method variables
		int[] toReturn = new int[5];
		BlockInfo node = null;
		
		// If the list is not empty
		if( index < blockInfoList.size() )
		{
			// Get the first block in the list
			node = blockInfoList.get( index );
			
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
	
	/**
	 * Loops through the list and searches for the highest values for the
	 * x, y, and z coordinates. The results are returned stored in an integer
	 * array of size three. <p>
	 * 
	 * @return An array (all default to 0) containing:
	 * <ul> 
	 * <li>At [0], X-Coordinate </li>
	 * <li>At [1], Y-Coordinate </li>
	 * <li>At [2], Z-Coordinate </li>
	 * </ul>
	 */
	public int[] getMaximums()
	{
		// Initialize variables
		int listSize = blockInfoList.size(), compareVal;
		int[] toReturn = new int[3];
		
		// Check if the list empty
		if( !isEmpty() )
		{
			// Set the initial values
			toReturn[0] = blockInfoList.get(0).xPos;
			toReturn[1] = blockInfoList.get(0).yPos;
			toReturn[2] = blockInfoList.get(0).zPos;
			
			// Loop through the list
			for( int i = 1 ; i < listSize ; i++ )
			{
				// Compare X
				compareVal = blockInfoList.get( i ).xPos;
				if( compareVal > toReturn[0] ) { toReturn[0] = compareVal; }
				
				// Compare Y
				compareVal = blockInfoList.get( i ).yPos;
				if( compareVal > toReturn[1] ) { toReturn[1] = compareVal; }
				
				// Compare Z
				compareVal = blockInfoList.get( i ).zPos;
				if( compareVal > toReturn[2] ) { toReturn[2] = compareVal; }
			}
		}
		
		// Return the array
		return toReturn;
	}

	/**
	 * Loops through the list and searches for the lowest values for the
	 * x, y, and z coordinates. The results are returned stored in an integer
	 * array of size three. <p>
	 * 
	 * @return An array (all default to 0) containing:
	 * <ul> 
	 * <li>At [0], X-Coordinate </li>
	 * <li>At [1], Y-Coordinate </li>
	 * <li>At [2], Z-Coordinate </li>
	 * </ul>
	 */
	public int[] getMinimums()
	{
		// Initialize variables
		int listSize = blockInfoList.size(), compareVal;
		int[] toReturn = new int[3];
		
		// Check if the list empty
		if( !isEmpty() )
		{
			// Set the initial values
			toReturn[0] = blockInfoList.get(0).xPos;
			toReturn[1] = blockInfoList.get(0).yPos;
			toReturn[2] = blockInfoList.get(0).zPos;
			
			// Loop through the list
			for( int i = 1 ; i < listSize ; i++ )
			{
				// Compare X
				compareVal = blockInfoList.get( i ).xPos;
				if( compareVal < toReturn[0] ) { toReturn[0] = compareVal; }
				
				// Compare Y
				compareVal = blockInfoList.get( i ).yPos;
				if( compareVal < toReturn[1] ) { toReturn[1] = compareVal; }
				
				// Compare Z
				compareVal = blockInfoList.get( i ).zPos;
				if( compareVal < toReturn[2] ) { toReturn[2] = compareVal; }
			}
		}
		
		// Return the array
		return toReturn;
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