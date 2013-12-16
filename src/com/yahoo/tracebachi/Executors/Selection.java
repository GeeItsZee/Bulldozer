package com.yahoo.tracebachi.Executors;

//import org.bukkit.World;
//import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
//import com.yahoo.tracebachi.Utils.BlockGroup;

//@SuppressWarnings("deprecation")
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
	// Purpose: 	Handles "marker" , "clear" , and "clearall" commands
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
				Player commandSender = (Player) client;
				
				// Give the player the tool if they don't have one
				if( !( commandSender.getInventory().contains( core.selectionTool ) ) )
				{	
					// Give
					commandSender.getInventory().addItem( core.selectionTool );
					
					// Output that the marker and paste tool was given
					commandSender.sendMessage( core.TAG_POSITIVE + "Given \"Marker.\"" );
				}
				// Give the player the tool if they don't have one
				if( !( commandSender.getInventory().contains( core.pasteTool ) ) )
				{	
					// Give
					commandSender.getInventory().addItem( core.pasteTool );
					
					// Output that the marker and paste tool was given
					commandSender.sendMessage( core.TAG_POSITIVE + "Given \"Paste Wand.\"" );
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

				// Clear the clipboard
				core.playerCopy.clearBlocksFor( commandSender.getName() );
				
				// Clear the selection
				if( core.playerSelections.removeSelectionFor( commandSender.getName() ) )
				{
					// Output that the selection has been wiped
					commandSender.sendMessage( core.TAG_POSITIVE + "Selection and Clipboard Cleared" );
				}
				else
				{
					// Output that the selection has been wiped
					commandSender.sendMessage( core.TAG_NEGATIVE + "Clipboard cleared, but Selection was already Cleared" );
				}
				
				// Return
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
				if( core.verifyPerm( commandSender , "SquareRemoveChunk" ) )
				{
					// Clear all selections
					core.playerSelections.removeAll();
					core.getLogger().info( "All player selections cleared." );
					return true;
				}
				
				// Otherwise return false
				return false;
			}
			else
			{
				// Clear selection
				core.playerSelections.removeAll();
				core.getLogger().info( "All player selections cleared." );
				return true;
			}
		}
		// Return false
		return false;
	}
		
}
