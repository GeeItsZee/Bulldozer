package com.yahoo.tracebachi.Executors;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Managers.BlockSet;
import com.yahoo.tracebachi.ThreadTasks.FileOutput_Block;

public class Save implements CommandExecutor
{
	// Create the executor's plug-in class instance for linking
	public static final String permName = "Save";

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
		BlockSet clipBoard = null;
		File fileToOpen = null; 
		
		// Verify valid command
		if( ! baseCommand.getName().equalsIgnoreCase( "save" ) )
		{
			return true;
		}
		
		// Verify sender is a player
		if( ! (sender instanceof Player) )
		{
			sender.sendMessage( Bulldozer.ERROR_CONSOLE );
			return true;
		}
		
		// Verify permission
		if( ! Bulldozer.core.verifyPerm( sender, permName ) )
		{
			sender.sendMessage( Bulldozer.ERROR_NO_PERM );
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
		clipBoard = Bulldozer.core.getClipboardFor( playerName );
		
		// Verify clipboard is not empty
		if( clipBoard.getSize() < 1 )
		{
			user.sendMessage( Bulldozer.ERROR_NO_CLIPBOARD );
			return true;
		}
				
		/////////////////////////////////////////////////////////////////////
		// Save
		try
		{
			// Set the file to open
			fileToOpen = new File( Bulldozer.ARCH_FOLDER 
				+ commandArgs[0] + ".arch" );
			
			// Check if the file is created
			if( ! fileToOpen.createNewFile() )
			{
				// Output to the user that the file already exists
				user.sendMessage( Bulldozer.TAG_NEGATIVE + "File (" 
					+ commandArgs[0] + ".arch) already exists!" );
				return true;
			}
			
			// Write Async
			Bulldozer.core.scheduleAsyncCallable( new FileOutput_Block(
				fileToOpen, playerName, commandArgs[1] ) );
		}
		catch( IOException e ) { e.printStackTrace(); }
		
		// Return true (file will be closed in the thread)
		return true;
	}
}
