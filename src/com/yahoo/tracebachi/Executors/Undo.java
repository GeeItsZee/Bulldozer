package com.yahoo.tracebachi.Executors;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;

public class Undo implements CommandExecutor
{

	// Create the executor's plug-in class instance for linking
	private Bulldozer mainPlugin;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Undo Default Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Undo( Bulldozer instance )
	{
		// Link the main instance with this executor
		mainPlugin = instance;
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
				World cPlayerWorld = commandSender.getWorld();
				
				// Check if there is a second argument
				if( cmdArgs.length == 1 )
				{
					//----------------------------------------------------------------------//
					//----------- Undo All -------------------------------------------------//
					if( cmdArgs[ 0 ].equalsIgnoreCase( "all" ) )
					{
						// Undo all edits
						while( mainPlugin.playerUndo.revertBlocksFor( commandSender.getName() , cPlayerWorld ) );
					}
					//----------------------------------------------------------------------//
					//----------- Undo Clear -----------------------------------------------//
					else if( cmdArgs[ 0 ].equalsIgnoreCase( "clear" ) )
					{
						// Clear all the storage for undo
						mainPlugin.playerUndo.clearAllStoredBlocksFor( commandSender.getName() );
					}
					//----------------------------------------------------------------------//
					//----------- Undo Help ------------------------------------------------//
					else
					{
						// Output possible commands
						commandSender.sendMessage(  ChatColor.YELLOW + "The possible commands are:" );
						commandSender.sendMessage(  ChatColor.GREEN + "    /undo all (to undo all edits)" );
						commandSender.sendMessage(  ChatColor.GREEN + "    /undo clear (to clear all saved undos)" );
					}
						
				}
				//---------------------------------------------------------------------------//
				//----------- Undo ----------------------------------------------------------//
				else
				{
					// Check if the list is null
					if( !(mainPlugin.playerUndo.revertBlocksFor( commandSender.getName() , cPlayerWorld ) ) )
					{
						// Tell the player there was nothing to undo
						commandSender.sendMessage( mainPlugin.ERROR_NO_UNDO );	
					}
				}
			}
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
				if( mainPlugin.verifyPerm( commandSender.getName() , "SquareRemoveChunk" ) )
				{
					// Clear all undo storage
					mainPlugin.playerUndo.clearAllStoredBlocks();
					mainPlugin.getLogger().info( "Undo storage for all players cleared." );
					return true;
				}
			}
			else
			{
				// Clear undo storage
				mainPlugin.playerUndo.clearAllStoredBlocks();
				mainPlugin.getLogger().info( "Undo storage for all players cleared." );
				return true;
			}
		}
		// Return false otherwise
		return false;
	}
		
}
