package com.yahoo.tracebachi.ThreadTasks;

import java.util.Scanner;
import java.util.concurrent.Callable;

import com.yahoo.tracebachi.Managers.BlockInfo;
import com.yahoo.tracebachi.Managers.BlockSet;

public class FileInput_Block implements Callable< Boolean >
{
	// Class Variables
	private Scanner inputFile;
	private BlockSet blockContainer;
	private int numBlocks;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	FileLoader Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public FileInput_Block( Scanner target, BlockSet storage , int numToRead )
	{
		// Copy variables
		inputFile = target;
		numBlocks = numToRead;
		blockContainer = storage;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Call
	// Purpose:	Read the file and return a block group containing all the blocks 
	//			stored in the file
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Boolean call() throws Exception
	{
		// Initialize variables
		int tempI = 0;
		long tempL = 0;
		
		// Set the key block
		blockContainer.setKeyBlock( 0, 0, 0 );
		
		// Loop through and add to the storage
		for( int i = 0 ; i < numBlocks ; i++ )
		{
			tempL = inputFile.nextLong();
			tempI = inputFile.nextInt();
			blockContainer.addBlock( new BlockInfo(
				tempL, tempI) );
		}

		// Close the scanner
		inputFile.close();
		
		// Return true through Boolean class
		return new Boolean( true );
	}

}
