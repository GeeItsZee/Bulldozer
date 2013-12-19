package com.yahoo.tracebachi.Executors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.Future;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.ThreadTasks.FileInput_Block;
import com.yahoo.tracebachi.ThreadTasks.FileStatus;
import com.yahoo.tracebachi.Utils.BlockGroup;


public class Load implements CommandExecutor
{
	
	// Create the executor's plug-in class instance for linking
	private Bulldozer core;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Selection Default Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Load( Bulldozer instance )
	{
		// Link the main instance with this executor
		core = instance;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "load" command
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client, Command cmd , String label, String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length ;
		
		// Check for command
		if( cmd.getName().equalsIgnoreCase( "load" ) )
		{
			// Check if the client is a player
			if( client instanceof Player )
			{
				// Create/Set variables
				Player cPlayer = (Player) client;
				BlockGroup container = core.playerCopy.getGroupFor( cPlayer.getName() );
				File fileToOpen = null; 
				Scanner fileScan = null;
				
				//---------------------------------------------------------------------------//
				// Check One: Verify Player has a valid command -----------------------------//
				if( argLen < 0 || argLen > 2 )
				{
					cPlayer.sendMessage( ChatColor.YELLOW + "The possible commands are:" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /load [File Name]" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /load -y [File Name]" );
					cPlayer.sendMessage( ChatColor.YELLOW + "Use the first command to check the file and contents." );
					cPlayer.sendMessage( ChatColor.YELLOW + "Use the second command to actually load contents." );
					return true;	
				}
				
				//---------------------------------------------------------------------------//
				// Check Two: Verify Player Permissions (Send error if false) -------------//
				if( !(core.verifyPerm( cPlayer , "Load" )) )
				{
					cPlayer.sendMessage( core.ERROR_PERM );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				//----------- Load the full file --------------------------------------------//
				if( commandArgs[0].equalsIgnoreCase( "-y" ) )
				{
					// Initialize Future
					Future< Boolean > threadResult = null;
					
					// Set the file to open
					fileToOpen = new File( core.PLAN_FOLDER + commandArgs[1] + ".arch" );
					
					// Attempt to open the file
					try
					{
						// Try to open the file
						fileScan = new Scanner( new BufferedReader( new FileReader( fileToOpen ) ) );
					}
					catch( FileNotFoundException e )
					{
						// Output to the user that the file was not found
						cPlayer.sendMessage( core.TAG_NEGATIVE + "File \"" + commandArgs[1] + ".arch\" was not found! (Check spelling?)" );
						return true;
					}
					
					// Run asynchronous file read
					fileScan.nextLine();
					threadResult = core.asyncExec.submit( new FileInput_Block( fileScan , container , fileScan.nextInt() ) );
					
					// Run a synchronous status informer
					core.getServer().getScheduler().runTask( core , 
						new FileStatus( threadResult , cPlayer , "File load of \"" + commandArgs[1] + ".arch\"" , core ) );
					
					// Return true (file will be closed in the thread)
					return true;
				}
				//---------------------------------------------------------------------------//
				//----------- Load the file information -------------------------------------//
				else
				{
					// Initialize variables 
					fileToOpen = new File( core.PLAN_FOLDER + commandArgs[0] + ".arch" );
					
					// Attempt to open the file
					try
					{
						// Try to open the file
						fileScan = new Scanner( new BufferedReader( new FileReader( fileToOpen ) ) );
					}
					catch( FileNotFoundException e )
					{
						// Output to the user that the file was not found
						cPlayer.sendMessage( core.TAG_NEGATIVE + "File \"" + commandArgs[0] + ".arch\" was not found! (Check spelling?)" );
						return true;
					}
					
					// Read first few lines of information
					cPlayer.sendMessage( core.TAG_POSITIVE + "File information loaded." );
					cPlayer.sendMessage( ChatColor.AQUA + "     File Name: " + ChatColor.WHITE + commandArgs[0] );
					cPlayer.sendMessage( ChatColor.AQUA + "     Description: " + ChatColor.WHITE + fileScan.nextLine() );
					cPlayer.sendMessage( ChatColor.AQUA + "     Number of Blocks: " + ChatColor.WHITE + fileScan.nextLine() );
					
					// Close the file (very important)
					fileScan.close();
					
					// Output help message
					cPlayer.sendMessage( core.TAG_POSITIVE + "To load blocks, do /load -y [File Name]" );
					return true;
				}
			}
			else { client.sendMessage( core.ERROR_CONSOLE ); }
		}
		
		// Return false by default
		return false;
	}

}
