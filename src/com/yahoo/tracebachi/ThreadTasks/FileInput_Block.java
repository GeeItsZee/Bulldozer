package com.yahoo.tracebachi.ThreadTasks;

import java.util.Scanner;
import java.util.concurrent.Callable;

import com.yahoo.tracebachi.Utils.BlockGroup;


public class FileInput_Block implements Callable< Boolean >
{
	// Class Variables
	private Scanner inputFile;
	private BlockGroup blockContainer;
	private int numBlocks;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	FileLoader Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public FileInput_Block( Scanner target , BlockGroup storage , int numToRead )
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
		int tempX , tempY , tempZ , tempID ;
		byte tempData;
		
		// Set the key block
		blockContainer.setKeyBlock( 0 , 0 , 0 );
		
		// Loop through and add to the storage
		for( int i = 0 ; i < numBlocks ; i++ )
		{
			// Read and save
			tempX = inputFile.nextInt();
			tempY = inputFile.nextInt();
			tempZ = inputFile.nextInt();
			
			tempID = inputFile.nextInt();
			tempData = inputFile.nextByte();
			
			// Push into the storage
			blockContainer.addBlock( tempX , tempY , tempZ, tempID , tempData );
		}

		// Close the scanner
		inputFile.close();
		
		// Return true through Boolean class
		return new Boolean( true );
	}

}
