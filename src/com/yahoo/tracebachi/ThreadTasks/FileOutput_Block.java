package com.yahoo.tracebachi.ThreadTasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Managers.BlockInfo;
import com.yahoo.tracebachi.Managers.BlockSet;


public class FileOutput_Block implements Callable< Boolean >
{
	// Class Variables
	private String filename = null;
	private String desc = null;
	private String name = null;
	private BufferedWriter outputFile = null;
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	FileLoader Constructor
	//////////////////////////////////////////////////////////////////////////
	public FileOutput_Block( File fileToOpen, String playerName,
		String description )
	{
		// Set class variables
		desc = description;
		name = playerName;
		
		try
		{
			// Try to open the file
			outputFile = new BufferedWriter( new FileWriter( fileToOpen ));
			filename = fileToOpen.getName();
		}
		catch( IOException excep ) { excep.printStackTrace(); }
	}

	//////////////////////////////////////////////////////////////////////////
	// Method: 	Call
	// Purpose:	Write the blocks to the file
	//////////////////////////////////////////////////////////////////////////
	@Override
	public Boolean call() throws Exception
	{
		// Method variables
		BlockSet container = Bulldozer.core.getClipboardFor( name );
		container.setRelativeToKeyBlock();
		
		// Write the description to the file
		outputFile.write( desc );
		outputFile.newLine();
		
		// Write the number of blocks to the file
		outputFile.write( String.valueOf( container.getSize() ) );
		outputFile.newLine();
		outputFile.flush();
		
		// Loop through and add
		for( BlockInfo iter : container.getImmutableVersion() )
		{
			outputFile.write( iter.compressToString() );
			outputFile.newLine();
		}

		// Close the file writing object (flushed by method)
		outputFile.close();
		
		// Send the player a completion message
		Bukkit.getPlayer( name ).sendMessage( Bulldozer.TAG_POSITIVE + 
			"File Load of (" + filename + ") Complete." );
		
		// Return a copy of the container
		return new Boolean( true );
	}
}
