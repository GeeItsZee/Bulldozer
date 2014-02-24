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
	public static final String permName = "Undo";
	private Bulldozer core;
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	Undo Default Constructor
	//////////////////////////////////////////////////////////////////////////
	public Undo( Bulldozer instance ) { core = instance; }

	//////////////////////////////////////////////////////////////////////////
	// Method: onCommand
	// Purpose: Handles "undo", "undo all", "undo final", and "wipe" commands
	//////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client, Command cmd,
		String label, String[] cmdArgs )
	{
		// Initialize variables
		String playerName = null;
		Player user = null;
		World playerWorld = null;
		
		// Verify valid command
		if( ! cmd.getName().equalsIgnoreCase( "undo" ) )
		{
			return true;
		}
		
		// Verify sender is a player
		if( ! (client instanceof Player) )
		{
			client.sendMessage( core.ERROR_CONSOLE );
			return true;
		}
		
		// Verify permission
		if( ! core.verifyPerm( client, permName ) )
		{
			client.sendMessage( core.ERROR_NO_PERM );
			return true;
		}
		
		// Set player variables
		user = (Player) client;
		playerName = user.getName();
		playerWorld = user.getWorld();
		
		// Check if there is a second argument
		if( cmdArgs.length > 0 )
		{
			////////////////////////////////////////////////////////////////
			// Undo - All
			if( cmdArgs[0].equalsIgnoreCase( "all" ) )
			{
				// Tell the player how many were restored
				user.sendMessage( core.TAG_POSITIVE + "Undo of " + 
					core.playerUndo.removePlayer( 
						playerName, true, playerWorld )
					+ ChatColor.GREEN + " steps complete." );	
			}
			////////////////////////////////////////////////////////////////
			// Undo - Final
			else if( cmdArgs[ 0 ].equalsIgnoreCase( "final" ) )
			{
				// Clear all the storage for undo without restoring
				core.playerUndo.removePlayer( 
					playerName, false, playerWorld );
			}		
		}
		/////////////////////////////////////////////////////////////////////
		// Undo - Single
		else
		{
			// Check if the returned group is not null
			if( core.playerUndo.peekGroupFor( playerName ) != null )
			{
				// Pop the group and restore it + clear it
				// At the end of the function, the reference is lost
				core.playerUndo.popGroupFor( playerName )
					.restoreBlocks( playerWorld, true );
				
				// Tell the player there was an undo of one step
				user.sendMessage( core.TAG_POSITIVE 
					+ "Undo of 1 Complete." );	
			}
			else
			{
				// Tell the player there was nothing to undo
				user.sendMessage( core.ERROR_NO_UNDO );	
			}
		}
		
		// Return
		return true;
	}
		
}
