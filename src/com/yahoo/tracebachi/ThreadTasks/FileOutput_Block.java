package com.yahoo.tracebachi.ThreadTasks;

import java.io.BufferedWriter;
import java.util.concurrent.Callable;

import com.yahoo.tracebachi.Managers.BlockInfo;
import com.yahoo.tracebachi.Managers.BlockSet;


public class FileOutput_Block implements Callable< Boolean >
{
	// Class Variables
	private int numBlocks;
	private String fDesc;
	private BufferedWriter outputFile;
	private BlockSet blockContainer;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	FileLoader Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public FileOutput_Block( BufferedWriter target , String description , BlockSet storage )
	{
		// Copy variables
		fDesc = description;
		numBlocks = (int) storage.getSize();
		
		// Copy references
		blockContainer = storage;
		blockContainer.setRelativeToKeyBlock();
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
		// Write the description to the file
		outputFile.write( fDesc );
		outputFile.newLine();
		
		// Write the number of blocks to the file
		outputFile.write( String.valueOf( numBlocks ) );
		outputFile.newLine();
		
		// Flush the buffer
		outputFile.flush();
		
		// Loop through and add
		for( BlockInfo iter : blockContainer.getImmutableVersion() )
		{
			outputFile.write( iter.compressToString() );
			outputFile.newLine();
		}

		// Close the file writing object (flushed by method)
		outputFile.close();
		
		// Return a copy of the container
		return new Boolean( true );
	}
}
