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
			client.sendMessage( Bulldozer.ERROR_CONSOLE );
			return true;
		}
		
		// Verify permission
		if( ! Bulldozer.core.verifyPerm( client, permName ) )
		{
			client.sendMessage( Bulldozer.ERROR_NO_PERM );
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
				user.sendMessage( Bulldozer.TAG_POSITIVE + "Undo of " + 
					Bulldozer.core.restoreAllFromUndoFor(
						playerName, playerWorld )
					+ ChatColor.GREEN + " steps complete." );	
			}
			////////////////////////////////////////////////////////////////
			// Undo - Final
			else if( cmdArgs[ 0 ].equalsIgnoreCase( "final" ) )
			{
				// Clear all the storage for undo without restoring
				Bulldozer.core.clearUndoFor( playerName );
			}		
		}
		/////////////////////////////////////////////////////////////////////
		// Undo - Single
		else
		{
			// Try to pop and restore
			try
			{
				// Pop the group and restore it
				Bulldozer.core.restoreOneFromUndoFor( 
					playerName, playerWorld );
				
				// Tell the player there was an undo of one step
				user.sendMessage( Bulldozer.TAG_POSITIVE 
					+ "Undo of 1 Complete." );	
			}
			catch( NullPointerException excep )
			{
				// Tell the player there was nothing to undo
				user.sendMessage( Bulldozer.ERROR_NO_UNDO );	
			}
		}
		
		// Return
		return true;
	}
}
