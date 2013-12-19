package com.yahoo.tracebachi.Executors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Utils.BlockGroup;

public class Selection implements CommandExecutor
{
	// Create the executor's plug-in class instance for linking
	private Bulldozer core;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Selection Default Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Selection( Bulldozer instance )
	{
		// Link the main instance with this executor
		core = instance;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles "kit" and "clear" commands
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client, Command cmd , String label, String[] cmdArgs )
	{
		//-------------------------------------------------------------------------------------//
		//----------- Kit ---------------------------------------------------------------------//
		if( cmd.getName().equalsIgnoreCase( "kit" ) )
		{
			// Check if the client is a player
			if( client instanceof Player )
			{
				// Cast the client as a player
				Player sender = (Player) client;
				
				// Give the player the tool if they don't have one
				if( !( sender.getInventory().contains( core.selectionTool ) ) )
				{	
					// Give
					sender.getInventory().addItem( core.selectionTool );
					
					// Output that the marker and paste tool was given
					sender.sendMessage( core.TAG_POSITIVE + "Given \"Marker.\"" );
				}
				// Give the player the tool if they don't have one
				if( !( sender.getInventory().contains( core.pasteTool ) ) )
				{	
					// Give
					sender.getInventory().addItem( core.pasteTool );
					
					// Output that the marker and paste tool was given
					sender.sendMessage( core.TAG_POSITIVE + "Given \"Paste Wand.\"" );
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
				// Initialize variables
				Player sender = (Player) client;
				BlockGroup group = null;
				
				// Clear the selection
				group = core.playerSelections.getGroupFor( sender.getName() );
				if( ! group.isEmpty() )
				{
					// Clear the selection (needs restore)
					core.playerSelections.removeGroupFor( sender.getName() , true ,  sender.getWorld() );
					
					// Notify the player
					sender.sendMessage( core.TAG_POSITIVE + "Selection Cleared" );
				}
				
				// Clear the clipboard
				group = core.playerCopy.getGroupFor( sender.getName() );
				if( ! group.isEmpty() )
				{
					// Clear the clipboard (doesn't need a restore)
					core.playerCopy.removeGroupFor( sender.getName() , false ,  sender.getWorld() );
					
					// Notify the player
					sender.sendMessage( core.TAG_POSITIVE + "Clipboard Cleared" );
				}
				
				// Return
				return true;
			}
		}
		// Return false
		return false;
	}
		
}
