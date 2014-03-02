package com.yahoo.tracebachi.Executors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.ThreadTasks.FileInput_Block;


public class Load implements CommandExecutor
{
	// Create the executor's plug-in class instance for linking
	public static final String permName = "Load";

	//////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "load" command
	//////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender sender, Command baseCommand, 
		String arg2, String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length;
		String playerName = null;
		Player user = null;
		File fileToOpen = null; 
		Scanner fileScan = null;
		
		// Verify valid command
		if( ! baseCommand.getName().equalsIgnoreCase( "load" ) )
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
				+ "/load [File Name] -y" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/load [File Name] -n" );
			return true;
		}
		
		// Set player variables
		user = (Player) sender;
		playerName = user.getName();
				
		/////////////////////////////////////////////////////////////////////
		// Load - Load
		if( commandArgs[1].equalsIgnoreCase( "-y" ) )
		{			
			// Set the file to open
			fileToOpen = new File( Bulldozer.ARCH_FOLDER + 
				commandArgs[0] + ".arch" );
			
			// Check if found
			if( fileToOpen.exists() )
			{
				// Read Async
				Bulldozer.core.scheduleAsyncCallable( 
					new FileInput_Block( fileToOpen, playerName ) );
			}
			else
			{
				// Output to the user that the file was not found
				user.sendMessage( Bulldozer.TAG_NEGATIVE + "File (" 
					+ commandArgs[0] + ".arch) was not found." );
			}
			
			// Return true (file will be closed in the thread)
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Load - Check
		else if( commandArgs[1].equalsIgnoreCase( "-n" ) )
		{
			// Attempt to open the file
			try
			{
				// Set the file to open
				fileToOpen = new File( Bulldozer.ARCH_FOLDER + 
					commandArgs[0] + ".arch" );
				
				// Try to open the file
				fileScan = new Scanner( new BufferedReader( 
					new FileReader( fileToOpen ) ) );
			}
			catch( FileNotFoundException e )
			{
				// Output to the user that the file was not found
				user.sendMessage( Bulldozer.TAG_NEGATIVE + "File (" 
					+ commandArgs[0] + ".arch) was not found." );
				return true;
			}
			
			// Read first few lines of information
			user.sendMessage( Bulldozer.TAG_POSITIVE 
				+ "File information loaded." );
			user.sendMessage( ChatColor.AQUA + "     "
				+ "File Name: " + ChatColor.WHITE + commandArgs[0] );
			user.sendMessage( ChatColor.AQUA + "     "
				+ "Description: " + ChatColor.WHITE + fileScan.nextLine() );
			user.sendMessage( ChatColor.AQUA + "     "
				+ "Number of Blocks: " 
				+ ChatColor.WHITE + fileScan.nextLine() );
			
			// Close the file (very important)
			fileScan.close();
			
			// Output help message
			user.sendMessage( Bulldozer.TAG_POSITIVE + 
				"To load blocks, do /load [File Name] -y" );
			
			// Return
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Default
		else
		{
			// Tell the player flag was invalid
			user.sendMessage( Bulldozer.ERROR_BAD_FLAG );
			return true;
		}
	}
}
