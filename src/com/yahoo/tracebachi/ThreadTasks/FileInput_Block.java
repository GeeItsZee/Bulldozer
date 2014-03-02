package com.yahoo.tracebachi.ThreadTasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Managers.BlockInfo;
import com.yahoo.tracebachi.Managers.BlockSet;

public class FileInput_Block implements Callable< Boolean >
{
	// Class Variables
	private int numBlocks;
	private String name = null;
	private String filename = null;
	private Scanner inputFile = null;
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	FileLoader Constructor
	//////////////////////////////////////////////////////////////////////////
	public FileInput_Block( File fileToOpen, String playerName )
	{
		// Constructor variables
		name = playerName;
		
		// Open the file
		try
		{			
			// Open the reader -> buffered -> scanner
			inputFile = new Scanner( new BufferedReader(	
				new FileReader( fileToOpen )));
			filename = fileToOpen.getName();
			
			// Toss the description and read the number of blocks
			inputFile.nextLine();
			numBlocks = inputFile.nextInt();
		}
		catch( FileNotFoundException excep ) { excep.printStackTrace(); }
	}

	//////////////////////////////////////////////////////////////////////////
	// Method: 	Call
	// Purpose:	Read the file and return the blocks.
	//////////////////////////////////////////////////////////////////////////
	@Override
	public Boolean call() throws Exception
	{
		// Initialize variables
		int tempI = 0;
		long tempL = 0;
		BlockSet toReturn = new BlockSet();
		
		// Loop through and add to the storage
		for( int i = 0 ; i < numBlocks ; i++ )
		{
			tempL = inputFile.nextLong();
			tempI = inputFile.nextInt();
			toReturn.addBlock( new BlockInfo(
				tempL, tempI) );
		}

		// Close the scanner
		inputFile.close();
		
		// Add the set to the player's clip board
		Bulldozer.core.setCopyBlocksFor( name, toReturn );
		
		// Send the player a completion message
		Bukkit.getPlayer( name ).sendMessage( Bulldozer.TAG_POSITIVE + 
			"File Load of (" + filename + ") Complete." );
		
		// Return true
		return new Boolean( true );
	}
}
