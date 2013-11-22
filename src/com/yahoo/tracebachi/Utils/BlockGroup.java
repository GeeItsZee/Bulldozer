package com.yahoo.tracebachi.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;

@SuppressWarnings("deprecation")
public class BlockGroup
{
			
	// Class Variables
	private List< blockInfo > blockInfoList = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	BlockGroup Default Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public BlockGroup()
	{
		// Create the blockInfoList
		blockInfoList = new ArrayList< blockInfo >();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	addBlock
	// Purpose: 	Add a block to the group
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean addBlock( int blockX , int blockY , int blockZ , int blockType , byte blockSubType )
	{
		// Add it to the blockInfoList
		return blockInfoList.add( new blockInfo( blockX , blockY , blockZ , blockType , blockSubType ) );
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	revertBlocks
	// Purpose: 	Revert the blocks to their original IDs and locations
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean revertBlocks( World blockWorld )
	{
		// Method variables
		int blockInfoListSize = blockInfoList.size();
		blockInfo cursorNode = null;
		Block cursorBlock = null;
		
		// Loop through the blockInfoList
		for( int iter = 0 ; iter < blockInfoListSize ; iter++ )
		{
			// Get the block
			cursorNode = blockInfoList.get( iter );
			cursorBlock = blockWorld.getBlockAt( cursorNode.xPos , cursorNode.yPos , cursorNode.zPos );
			
			// Revert the block
			cursorBlock.setTypeId( cursorNode.blockID );
			cursorBlock.setData( cursorNode.blockSubID );
		}
		
		// Clear the blockInfoList and return
		return clearAllBlocks();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	clearAllBlocks
	// Purpose: 	Remove all the blocks from the group
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean clearAllBlocks()
	{
		// Clear the blockInfoList
		if( blockInfoList != null ) 
		{ 
			blockInfoList.clear(); 
			blockInfoList = null ;
		}
		
		// Return for completion
		return true;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Class: 	Block Info
	// Purpose: 	Store Block Data
	//////////////////////////////////////////////////////////////////////////////////////////////
	private class blockInfo
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
			public blockInfo( int blockX , int blockY , int blockZ , int blockType , byte blockSubType )
			{
					// Copy over the values
					xPos = blockX ; yPos = blockY ; zPos = blockZ ;
					blockID = blockType ;
					blockSubID = blockSubType ;
			}
			
	}
			
}