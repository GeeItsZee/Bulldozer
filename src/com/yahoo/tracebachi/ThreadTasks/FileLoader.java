package com.yahoo.tracebachi.ThreadTasks;

import java.util.Scanner;
import java.util.concurrent.Callable;

import com.yahoo.tracebachi.Utils.BlockGroup;


public class FileLoader implements Callable< BlockGroup >
{
	// Class Variables
	private Scanner toRead;
	private BlockGroup loadedBlocks;
	private int numBlocks;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	FileLoader Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public FileLoader( Scanner target , BlockGroup storage , int numToRead )
	{
		// Copy variables
		toRead = target;
		numBlocks = numToRead;
		loadedBlocks = storage;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Call
	// Purpose:	Read the file and return a block group containing all the blocks 
	//			stored in the file
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public BlockGroup call() throws Exception
	{
		// Initialize variables
		int tempX , tempY , tempZ , tempID ;
		byte tempData;
		
		// Set the key block
		loadedBlocks.setKeyBlock( 0 , 0 , 0 );
		
		// Loop through and add to the storage
		for( int i = 0 ; i < numBlocks ; i++ )
		{
			// Read and save
			tempX = toRead.nextInt();
			tempY = toRead.nextInt();
			tempZ = toRead.nextInt();
			
			tempID = toRead.nextInt();
			tempData = toRead.nextByte();
			
			// Push into the storage
			loadedBlocks.addBlock( tempX , tempY , tempZ, tempID , tempData );
		}

		// Close the scanner
		toRead.close();
		
		// Return the group
		return loadedBlocks;
	}

}
