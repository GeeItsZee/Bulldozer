package com.yahoo.tracebachi.Executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;

public class Undo implements CommandExecutor
{

	// Create the executor's plug-in class instance for linking
	private Bulldozer core;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Undo Default Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Undo( Bulldozer instance )
	{
		// Link the main instance with this executor
		core = instance;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles "undo" , "undo all" , "undo clear" , and "wipe" commands
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client, Command cmd , String label, String[] cmdArgs )
	{
		
		// Check for command
		if( cmd.getName().equalsIgnoreCase( "undo" ) )
		{
			
			// Check if the client is a player
			if( client instanceof Player )
			{
				
				// Set up the player variables
				Player commandSender = (Player) client;
				
				// Check if there is a second argument
				if( cmdArgs.length == 1 )
				{
					//----------------------------------------------------------------------//
					//----------- Undo All -------------------------------------------------//
					if( cmdArgs[ 0 ].equalsIgnoreCase( "all" ) )
					{
						// Initialize a counter
						int counter = 0;
						
						// Undo all edits
						while( core.playerUndo.revertBlocksFor( commandSender.getName() ) )
						{
							// Increment the counter
							counter++;
						}
						
						// Tell the player of completion
						commandSender.sendMessage( core.TAG_POSITIVE + "Undo of " + 
							ChatColor.LIGHT_PURPLE + counter + ChatColor.GREEN + " Complete." );	
					}
					//----------------------------------------------------------------------//
					//----------- Undo Clear -----------------------------------------------//
					else if( cmdArgs[ 0 ].equalsIgnoreCase( "final" ) )
					{
						// Clear all the storage for undo
						core.playerUndo.clearBlocksFor( commandSender.getName() );
					}
					//----------------------------------------------------------------------//
					//----------- Undo Help ------------------------------------------------//
					else
					{
						// Output possible commands
						commandSender.sendMessage(  ChatColor.YELLOW + "The possible commands are:" );
						commandSender.sendMessage(  ChatColor.GREEN + "    /undo (to undo one edit)" );
						commandSender.sendMessage(  ChatColor.GREEN + "    /undo all (to undo all edits)" );
						commandSender.sendMessage(  ChatColor.GREEN + "    /undo final (to finalize all saved undos)" );
					}
						
				}
				//---------------------------------------------------------------------------//
				//----------- Undo ----------------------------------------------------------//
				else
				{
					// Check if the list is null
					if( core.playerUndo.revertBlocksFor( commandSender.getName() ) )
					{
						// Tell the player there was an undo of one step
						commandSender.sendMessage( core.TAG_POSITIVE + "Undo of 1 Complete." );	
					}
					else
					{
						// Tell the player there was nothing to undo
						commandSender.sendMessage( core.ERROR_NO_UNDO );	
					}
				}
			}
			
			// Return true
			return true;
		}
		//-------------------------------------------------------------------------------------//
		//----------- Wipe --------------------------------------------------------------------//
		else if ( cmd.getName().equalsIgnoreCase( "wipe" ) )
		{
			// Check if the client has permission
			if( client instanceof Player )
			{
				// Cast the client as a player
				Player commandSender = (Player) client;
				
				// Verify the permissions of the client
				if( core.verifyPerm( commandSender , "SquareRemoveChunk" ) )
				{
					// Clear all undo storage
					core.playerUndo.clearAllStoredBlocks();
					core.getLogger().info( "Undo storage for all players cleared." );
					return true;
				}
			}
			else
			{
				// Clear undo storage
				core.playerUndo.clearAllStoredBlocks();
				core.getLogger().info( "Undo storage for all players cleared." );
				return true;
			}
		}
		// Return false otherwise
		return false;
	}
		
}
