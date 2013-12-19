package com.yahoo.tracebachi.ThreadTasks;

import java.io.BufferedWriter;
import java.util.concurrent.Callable;

import com.yahoo.tracebachi.Utils.BlockGroup;


public class FileOutput_Block implements Callable< Boolean >
{
	// Class Variables
	private int numBlocks;
	private String fDesc;
	private BufferedWriter outputFile;
	private BlockGroup blockContainer;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	FileLoader Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public FileOutput_Block( BufferedWriter target , String description , BlockGroup storage )
	{
		// Copy variables
		fDesc = description;
		numBlocks = (int) storage.getSize();
		
		// Copy references
		blockContainer = storage;
		outputFile = target;
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
		int[] blockInfoArray = new int[5];
		
		// Write the description to the file
		outputFile.write( fDesc );
		outputFile.newLine();
		
		// Write the number of blocks to the file
		outputFile.write( String.valueOf( numBlocks ) );
		outputFile.newLine();
		
		// Flush the buffer
		outputFile.flush();
		
		// Loop through and add to the storage
		for( int i = 0 ; i < numBlocks ; i++ )
		{
			// Get the information from the array
			blockInfoArray = blockContainer.get( i );
			
			// Write the coordinates
			outputFile.write( String.valueOf( blockInfoArray[0] ) );
			outputFile.write( ' ' );
			outputFile.write( String.valueOf( blockInfoArray[1] ) );
			outputFile.write( ' ' );
			outputFile.write( String.valueOf( blockInfoArray[2] ) );
			outputFile.write( ' ' );
			
			// Write the block ID and data
			outputFile.write( String.valueOf( blockInfoArray[3] ) );
			outputFile.write( ' ' );
			outputFile.write( String.valueOf( blockInfoArray[4] ) );
			outputFile.newLine();
			
			// Flush the buffer
			outputFile.flush();
		}

		// Close the file writing object
		outputFile.close();
		
		// Return a copy of the container
		return new Boolean( true );
	}

}
