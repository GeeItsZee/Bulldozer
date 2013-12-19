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
	private Bulldozer core;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Selection Default Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Save( Bulldozer instance )
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
		if( cmd.getName().equalsIgnoreCase( "save" ) )
		{
			// Check if the client is a player
			if( client instanceof Player )
			{
				// Create/Set variables
				Player cPlayer = (Player) client;
				BlockGroup cPlayerCopy = core.playerCopy.getGroupFor( cPlayer.getName() );
				File fileToOpen = null; 
				BufferedWriter fileSave = null;
				
				//---------------------------------------------------------------------------//
				// Check One: Verify Player has a valid command -----------------------------//
				if( argLen < 1 )
				{
					cPlayer.sendMessage( ChatColor.YELLOW + "The possible commands are:" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /save [File Name] [Description]" );
					cPlayer.sendMessage( ChatColor.YELLOW + "Make sure you have a selection before running the command." );
					return true;	
				}
				
				//---------------------------------------------------------------------------//
				// Check Two: Verify Player has a selection ---------------------------------//
				if( cPlayerCopy.isEmpty() )
				{
					cPlayer.sendMessage( core.ERROR_SELECTION );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Three: Verify Player Permissions (Send error if false) -------------//
				if( !(core.verifyPerm( cPlayer , "Save" )) )
				{
					cPlayer.sendMessage( core.ERROR_PERM );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				//----------- Save to the file ----------------------------------------------//
				// Initialize variables
				Future< Boolean > threadResult = null;
				
				// Set the file to open
				fileToOpen = new File( core.PLAN_FOLDER + commandArgs[0] + ".arch" );
				
				// Try to make the output file
				try
				{
					// Check if the file is created
					if( ! fileToOpen.createNewFile() )
					{
						// Output to the user that the file already exists
						cPlayer.sendMessage( core.TAG_NEGATIVE + "File \"" + commandArgs[0] + ".arch\" already exists! (Try a different name?)" );
						return true;
					}
					
					// Create the buffered stream
					fileSave = new BufferedWriter( new FileWriter( fileToOpen ) );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				
				// Check if there is a description
				if( argLen == 2 )
				{					
					// Run asynchronous file save
					threadResult = core.asyncExec.submit( new FileOutput_Block( fileSave , commandArgs[1] , cPlayerCopy ) );
				}
				else
				{
					// Run asynchronous file save
					threadResult = core.asyncExec.submit( new FileOutput_Block( fileSave , "Default Description" , cPlayerCopy ) );
				}
				
				// Run a synchronous status informer
				core.getServer().getScheduler().runTask( core , 
					new FileStatus( threadResult , cPlayer , "File save of \"" + commandArgs[0] + ".arch\"" , core ) );
				
				// Return true (file will be closed in the thread)
				return true;
			}
			else { client.sendMessage( core.ERROR_CONSOLE ); }
		}
		
		// Return false by default
		return false;
	}

}
