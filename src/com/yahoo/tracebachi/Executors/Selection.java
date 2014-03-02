package com.yahoo.tracebachi.Executors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Managers.BlockSet;

public class Selection implements CommandExecutor
{
	// Create the executor's plug-in class instance for linking
	private Bulldozer core;
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	Selection Default Constructor
	//////////////////////////////////////////////////////////////////////////
	public Selection( Bulldozer instance ) { core = instance; }

	//////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles "kit", "clears", "clearc" commands
	//////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client, Command cmd, 
		String label, String[] cmdArgs )
	{		
		// Verify sender is a player
		if( ! (client instanceof Player) )
		{
			client.sendMessage( core.ERROR_CONSOLE );
			return true;
		}
		
		/////////////////////////////////////////////////////////////////////
		// Kit
		if( cmd.getName().equalsIgnoreCase( "bdkit" ) )
		{
			// Cast the client as a player
			Player sender = (Player) client;
			
			// Give the player the tool if they don't have one
			if( !( sender.getInventory().contains( core.selectionTool ) ) )
			{	
				// Give
				sender.getInventory().addItem( core.selectionTool );
				
				// Output that the marker and paste tool was given
				sender.sendMessage( core.TAG_POSITIVE 
					+ "Given: Marker Block (Black Wool)" );
			}
			
			// Give the player the tool if they don't have one
			if( !( sender.getInventory().contains( core.pasteTool ) ) )
			{	
				// Give
				sender.getInventory().addItem( core.pasteTool );
				
				// Output that the marker and paste tool was given
				sender.sendMessage( core.TAG_POSITIVE 
					+ "Given: Paste Block (Blue Wool)" );
			}
			
			// Give the player the tool if they don't have one
			if( !( sender.getInventory().contains( core.measureTool ) ) )
			{	
				// Give
				sender.getInventory().addItem( core.measureTool );
				
				// Output that the marker and paste tool was given
				sender.sendMessage( core.TAG_POSITIVE 
					+ "Given: Measure Block (Orange Wool)" );
			}
			
			// Return
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Clear Selection
		else if ( cmd.getName().equalsIgnoreCase( "clears" ) )
		{
			// Initialize variables
			Player sender = (Player) client;
			BlockSet group = core.playerSelection.getGroupFor( 
				sender.getName() );
			
			// Clear the selection
			if( group.getSize() > 0 )
			{
				// Clear the selection (needs restore)
				core.playerSelection.removeGroupAndRestoreFor( 
					sender.getName(), sender.getWorld() );
				
				// Notify the player
				sender.sendMessage( core.TAG_POSITIVE 
					+ "Selection Cleared" );
			}
			else
			{
				// Notify the player
				sender.sendMessage( core.TAG_NEGATIVE 
					+ "Selection already clear." );
			}
			
			// Return
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Clear Clipboard
		else if ( cmd.getName().equalsIgnoreCase( "clearc" ) )
		{
			// Initialize variables
			Player sender = (Player) client;
			BlockSet group = core.playerCopy.getGroupFor( 
				sender.getName() );
			
			// Clear the clipboard
			if( group.getSize() > 0 )
			{
				// Clear the clipboard (doesn't need a restore)
				core.playerCopy.removeGroupAndClearFor( 
					sender.getName()  );
				
				// Notify the player
				sender.sendMessage( core.TAG_POSITIVE 
					+ "Clipboard Cleared" );
			}
			else
			{
				// Notify the player
				sender.sendMessage( core.TAG_NEGATIVE 
					+ "Clipboard already clear." );
			}
			
			// Return
			return true;
		}
		
		// Return false
		return false;
	}
}
