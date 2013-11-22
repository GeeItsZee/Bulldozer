package com.yahoo.tracebachi.Executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;


public class Selection implements CommandExecutor
{
	
	// Create the executor's plug-in class instance for linking
	private Bulldozer mainPlugin;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Selection Default Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Selection( Bulldozer instance )
	{
		// Link the main instance with this executor
		mainPlugin = instance;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles "marker" , "clear" , and "clearall" commands
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client, Command cmd , String label, String[] cmdArgs )
	{
		
		//-------------------------------------------------------------------------------------//
		//----------- Marker ------------------------------------------------------------------//
		if( cmd.getName().equalsIgnoreCase( "marker" ) )
		{
			// Check if the client is a player
			if( client instanceof Player )
			{
				// Cast the client as a player
				Player commandSender = (Player) client;
				
				// Give the player the tool if they don't have one
				if( !( commandSender.getInventory().contains( mainPlugin.selectionTool ) ) )
				{
						
					// Give
					commandSender.getInventory().addItem( mainPlugin.selectionTool );
					
					// Output that the selection has been wiped
					commandSender.sendMessage( ChatColor.GREEN + "[Bulldozer] Given \"Marker\" " );
						
				}
				return true;
			}
				
		}
		//-------------------------------------------------------------------------------------//
		//----------- Clear -------------------------------------------------------------------//
		else if ( cmd.getName().equalsIgnoreCase( "clear" ) )
		{
			// Check if the client is a player
			if( client instanceof Player )
			{
				// Cast the client as a player
				Player commandSender = (Player) client;
				
				// Clear the selection
				mainPlugin.playerSelections.removeSelectionFor( commandSender.getName() );
				
				// Output that the selection has been wiped
				commandSender.sendMessage( ChatColor.GREEN + "[Bulldozer] Selection Cleared" );
				return true;
			}
				
		}
		//-------------------------------------------------------------------------------------//
		//----------- Clear All ---------------------------------------------------------------//
		else if ( cmd.getName().equalsIgnoreCase( "clearall" ) )
		{
			// Check if the client has permission
			if( client instanceof Player )
			{
				// Cast the client as a player
				Player commandSender = (Player) client;
				
				// Verify the permissions of the client
				if( mainPlugin.verifyPerm( commandSender , "SquareRemoveChunk" ) )
				{
					// Clear all selections
					mainPlugin.playerSelections.removeAll();
					mainPlugin.getLogger().info( "All player selections cleared." );
					return true;
				}
				
				// Otherwise return false
				return false;
			}
			else
			{
				// Clear selection
				mainPlugin.playerSelections.removeAll();
				mainPlugin.getLogger().info( "All player selections cleared." );
				return true;
			}
		}
		// Return false
		return false;
	}
		
}
