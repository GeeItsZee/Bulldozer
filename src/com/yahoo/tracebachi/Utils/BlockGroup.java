package com.yahoo.tracebachi.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

@SuppressWarnings("deprecation")
public class BlockGroup
{
			
	// Class Variables
	private List< BlockInfo > blockInfoList = null;
	private World groupWorld = null;
	
	// Key Block: The block from which the offsets for pasting is determined
	private int keyblockX , keyblockY , keyblockZ;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	BlockGroup Default Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public BlockGroup( World blockWorld )
	{
		// Create the blockInfoList
		blockInfoList = new ArrayList< BlockInfo >();
		
		// Create a reference to the world
		groupWorld = blockWorld;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	addBlock
	// Purpose: 	Add a block to the group based of specific data
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean addBlock( int blockX , int blockY , int blockZ , int blockType , byte blockSubType )
	{
		// Add it to the blockInfoList
		return blockInfoList.add( new BlockInfo( blockX , blockY , blockZ , blockType , blockSubType ) );
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	addBlock
	// Purpose: 	Add a block to the group based of the passed block
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean addBlock( Block toAdd )
	{
		// Add it to the blockInfoList
		return blockInfoList.add( new BlockInfo( toAdd.getX() , toAdd.getY() , toAdd.getZ() , toAdd.getTypeId() , toAdd.getData() ) );
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	addBlock
	// Purpose: 	Add a block to the group based of specific data
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean addBlockAndChange( Block toAdd , int newID , byte newData )
	{
		// Store the original ID in a temp
		int tempID = toAdd.getTypeId();
		byte tempData = toAdd.getData();
		
		// Modify the block
		toAdd.setTypeId( newID );
		toAdd.setData( newData );
		
		// Add the original values to the blockInfoList
		return blockInfoList.add( new BlockInfo( toAdd.getX() , toAdd.getY() , toAdd.getZ() , tempID , tempData ) );
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method:	setKeyBlock
	// Purpose:	Set the data of the key block for relative position based procedures
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void setKeyBlock( Block toSet )
	{
		// Set the values
		keyblockX = toSet.getX();
		keyblockY = toSet.getY();
		keyblockZ = toSet.getZ();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method:	setKeyBlock
	// Purpose:	Set the data of the key block for relative position based procedures from
	//			singular values
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void setKeyBlock( int blockX , int blockY , int blockZ )
	{
		// Set the values
		keyblockX = blockX;
		keyblockY = blockY;
		keyblockZ = blockZ;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method:	getKeyBlock
	// Purpose:	Return the key block in the world
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Block getKeyBlock()
	{
		// Set the values
		return groupWorld.getBlockAt( keyblockX , keyblockY , keyblockZ );
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method:	updateSelection
	// Purpose:	Loop through the selection and check if the block was changed and update the 
	//			stored block data accordingly
	//////////////////////////////////////////////////////////////////////////////////////////////
	public void updateSelection()
	{
		// Initialize variables
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
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	revertBlocks
	// Purpose: 	Revert the blocks to their original IDs and locations
	// 			Additional: If 'clearList' is true, clear the list
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean revertBlocks( boolean clearList )
	{
		// Method variables
		int blockInfoListSize = blockInfoList.size();
		BlockInfo cursorNode = null;
		Block cursorBlock = null;
		
		// Loop through the blockInfoList
		for( int iter = 0 ; iter < blockInfoListSize ; iter++ )
		{
			// Get the block
			cursorNode = blockInfoList.get( iter );
			cursorBlock = groupWorld.getBlockAt( cursorNode.xPos , cursorNode.yPos , cursorNode.zPos );
			
			// Revert the block
			cursorBlock.setTypeId( cursorNode.blockID );
			cursorBlock.setData( cursorNode.blockSubID );
		}
		
		// Check the 'clearList' boolean
		if( clearList ) { clearBlocks(); }
		
		// Always return true
		return true;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	pasteBlocks
	// Purpose: 	Recreate the blocks from the group starting from the passed start point
	//////////////////////////////////////////////////////////////////////////////////////////////
	public BlockGroup duplicateBlocks( int startX , int startY , int startZ )
	{
		// Method variables
		int blockInfoListSize = blockInfoList.size();
		int offX , offY , offZ;
		BlockGroup toReturn = null;
		BlockInfo infoNode = null;
		Block cursorBlock = null;
		
		// Verify the list is not empty
		if( ! blockInfoList.isEmpty() )
		{
			// Initialize the block group to return
			toReturn = new BlockGroup( groupWorld );
			
			// Figure out the offset
			offX = startX - keyblockX;
			offY = startY - keyblockY;
			offZ = startZ - keyblockZ;
			
			// Loop through the blockInfoList
			for( int iter = 0 ; iter < blockInfoListSize ; iter++ )
			{
				// Get the block
				infoNode = blockInfoList.get( iter );
				cursorBlock = groupWorld.getBlockAt( infoNode.xPos + offX , infoNode.yPos + offY , infoNode.zPos + offZ );
				
				// Add to the group
				toReturn.addBlock( cursorBlock );
				
				// Change the block
				cursorBlock.setTypeId( infoNode.blockID );
				cursorBlock.setData( infoNode.blockSubID );
			}
		}
		
		// Clear the blockInfoList and return
		return toReturn;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getSize
	// Purpose: 	Return the integer size of the list
	//////////////////////////////////////////////////////////////////////////////////////////////
	public int getSize()
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


	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getFirst
	// Purpose: 	Get the first block in the list if the list is not empty
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Block getFirst()
	{	
		// Method variables
		BlockInfo infoNode = null;
		
		// If the list is not empty
		if( !isEmpty() )
		{
			// Get the first block in the list
			infoNode = blockInfoList.get(0);
			
			// Return the first block
			return groupWorld.getBlockAt( infoNode.xPos , infoNode.yPos , infoNode.zPos );
		}

		// Return the null block otherwise
		return null;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getMaximums
	// Purpose: 	Loop through the list and return the highest values
	// Return:	Returns null if empty list, array of highest values otherwise
	//////////////////////////////////////////////////////////////////////////////////////////////
	public int[] getMaximums()
	{
		// Initialize variables
		int listSize, compareVal;
		int[] toReturn = null;
		
		// Check if the list empty
		if( !isEmpty() )
		{
			// Set the size and initialize the return array
			listSize = blockInfoList.size();
			toReturn = new int[3];
			
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


	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getMinimums
	// Purpose: 	Loop through the list and return the lowest values
	// Return:	Returns null if empty list, array of lowest values otherwise
	//////////////////////////////////////////////////////////////////////////////////////////////
	public int[] getMinimums()
	{
		// Initialize variables
		int listSize, compareVal;
		int[] toReturn = null;
		
		// Check if the list empty
		if( !isEmpty() )
		{
			// Set the size and initialize the return array
			listSize = blockInfoList.size();
			toReturn = new int[3];
			
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


	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	getChunkOfBlock
	// Purpose: 	Get the chunk belonging to the block at the 'blockInList' position of the list 
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Chunk getChunkOfBlock( int blockInList )
	{	
		// Method variables
		BlockInfo node = null;
		
		// If the list is not empty
		if( !isEmpty() && (blockInList < blockInfoList.size()) )
		{
			// Get the i block in the list
			node = blockInfoList.get( blockInList );
			
			// Return the chunk containing the block
			return groupWorld.getBlockAt( node.xPos , node.yPos , node.zPos ).getChunk();
		}

		// Return the null chunk otherwise
		return null;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	isEmpty
	// Purpose: 	Returns true if the group-list is empty or null
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isEmpty()
	{
		// Return if the list is empty or not
		return blockInfoList.isEmpty();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	clearAllBlocks
	// Purpose: 	Remove all the blocks from the group
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean clearBlocks()
	{
		// Clear the blockInfoList
		if( blockInfoList != null ) 
		{ 
			blockInfoList.clear();
		}
		
		// Remove the reference to the world
		groupWorld = null;
		
		// Return for completion
		return true;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Class: 	Block Info
	// Purpose: 	Store Block Data
	//////////////////////////////////////////////////////////////////////////////////////////////
	private class BlockInfo
	{
		// Class Variables
		public int xPos = 0 ;
		public int yPos = 0 ;
		public int zPos = 0 ;
		public int blockID = 0 ;
		public byte blockSubID = 0 ;
		
		////////////////////////////////////////////////////////////////////////////////////
		// Method: 	BlockInfo Default Constructor
		// Purpose: 	Construct and store all the block information
		////////////////////////////////////////////////////////////////////////////////////
		public BlockInfo( int blockX , int blockY , int blockZ , int blockType , byte blockSubType )
		{
			// Copy over the values
			xPos = blockX ; yPos = blockY ; zPos = blockZ ;
			blockID = blockType ;
			blockSubID = blockSubType ;
		}
	}
			
}