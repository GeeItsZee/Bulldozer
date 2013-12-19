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
				// Initialize variables
				Player sender = (Player) client;
				
				// Check if there is a second argument
				if( cmdArgs.length == 1 )
				{
					//----------------------------------------------------------------------//
					//----------- Undo All -------------------------------------------------//
					if( cmdArgs[ 0 ].equalsIgnoreCase( "all" ) )
					{
						// Tell the player how many were restored
						sender.sendMessage( core.TAG_POSITIVE + "Undo of " + 
							ChatColor.LIGHT_PURPLE + 
							core.playerUndo.removePlayer( sender.getName() , true , sender.getWorld() )
							+ ChatColor.GREEN + " Complete." );	
					}
					//----------------------------------------------------------------------//
					//----------- Undo Final -----------------------------------------------//
					else if( cmdArgs[ 0 ].equalsIgnoreCase( "final" ) )
					{
						// Clear all the storage for undo without restoring
						core.playerUndo.removePlayer( sender.getName() , false , sender.getWorld() );
					}
					//----------------------------------------------------------------------//
					//----------- Undo Help ------------------------------------------------//
					else
					{
						// Output possible commands
						sender.sendMessage(  ChatColor.YELLOW + "The possible commands are:" );
						sender.sendMessage(  ChatColor.GREEN + "    /undo (to undo one edit)" );
						sender.sendMessage(  ChatColor.GREEN + "    /undo all (to undo all edits)" );
						sender.sendMessage(  ChatColor.GREEN + "    /undo final (to finalize all saved undos)" );
					}
						
				}
				//---------------------------------------------------------------------------//
				//----------- Undo ----------------------------------------------------------//
				else
				{
					// Check if the returned group is not null
					if( core.playerUndo.peekGroupFor( sender.getName() ) != null )
					{
						// Pop the group and restore it + clear it
						// At the end of the function, the reference is lost
						core.playerUndo.popGroupFor( sender.getName() ).restoreBlocks( sender.getWorld() , true );
						
						// Tell the player there was an undo of one step
						sender.sendMessage( core.TAG_POSITIVE + "Undo of 1 Complete." );	
					}
					else
					{
						// Tell the player there was nothing to undo
						sender.sendMessage( core.ERROR_NO_UNDO );	
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
				Player sender = (Player) client;
				
				// Verify the permissions of the client
				if( core.verifyPerm( sender , "SquareRemoveChunk" ) )
				{
					// Clear all undo storage by closing the manager
					core.playerUndo.closeManager();
					core.getLogger().info( "Undo storage for all players cleared." );
					return true;
				}
			}
			else
			{
				// Clear undo storage by closing the manager
				core.playerUndo.closeManager();
				core.getLogger().info( "Undo storage for all players cleared." );
				return true;
			}
		}
		// Return false otherwise
		return false;
	}
		
}
