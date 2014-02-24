package com.yahoo.tracebachi.Executors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Future;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.ThreadTasks.FileOutput_Block;
import com.yahoo.tracebachi.ThreadTasks.FileStatus;
import com.yahoo.tracebachi.Utils.BlockGroup;


public class Save implements CommandExecutor
{
	// Create the executor's plug-in class instance for linking
	public static final String permName = "Save";
	private Bulldozer core;
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	Selection Default Constructor
	//////////////////////////////////////////////////////////////////////////
	public Save( Bulldozer instance ) { core = instance; }

	//////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "save" command
	//////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender sender, Command baseCommand, 
		String arg2, String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length;
		String playerName = null;
		Player user = null;
		BlockGroup clipBoard = null;
		File fileToOpen = null; 
		BufferedWriter fileSave = null;
		
		// Verify valid command
		if( ! baseCommand.getName().equalsIgnoreCase( "save" ) )
		{
			return true;
		}
		
		// Verify sender is a player
		if( ! (sender instanceof Player) )
		{
			sender.sendMessage( core.ERROR_CONSOLE );
			return true;
		}
		
		// Verify permission
		if( ! core.verifyPerm( sender, permName ) )
		{
			sender.sendMessage( core.ERROR_NO_PERM );
			return true;
		}
		
		// Verify command size is valid
		if( argLen < 2 || argLen > 4 )
		{
			sender.sendMessage( ChatColor.YELLOW 
				+ "Command must be of the form:" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/save [File Name] [Description]" );
			return true;
		}
		
		// Set player variables
		user = (Player) sender;
		playerName = user.getName();
		clipBoard = core.playerCopy.getGroupFor( playerName );
		
		// Verify clipboard is not empty
		if( clipBoard.isEmpty() )
		{
			user.sendMessage( core.ERROR_NO_CLIPBOARD );
			return true;
		}
				
		/////////////////////////////////////////////////////////////////////
		// Save
		
		// Initialize variables
		Future< Boolean > threadResult = null;
		
		// Try to make the output file
		try
		{
			// Set the file to open
			fileToOpen = new File( core.ARCH_FOLDER 
				+ commandArgs[0] + ".arch" );
			
			// Check if the file is created
			if( ! fileToOpen.createNewFile() )
			{
				// Output to the user that the file already exists
				user.sendMessage( core.TAG_NEGATIVE + "File (" 
					+ commandArgs[0] + ".arch) already exists!" );
				return true;
			}
			
			// Create the buffered stream
			fileSave = new BufferedWriter( new FileWriter( fileToOpen ) );
		}
		catch( IOException e ) { e.printStackTrace(); }
		
		// Run asynchronous file save
		threadResult = core.asyncExec.submit( new FileOutput_Block( 
			fileSave,
			commandArgs[1],
			clipBoard ) );
		
		// Run a synchronous status informer
		core.getServer().getScheduler().runTask( core, new FileStatus(
			threadResult, 
			user,
			"File save of (" + commandArgs[0] + ".arch)", core ) );
		
		// Return true (file will be closed in the thread)
		return true;
	}
}
