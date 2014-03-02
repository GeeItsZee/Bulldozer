package com.yahoo.tracebachi.Executors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Managers.BlockSet;

public class Selection implements CommandExecutor
{	
	// Class Variables
	private ItemStack selectionTool = new ItemStack( 
		Material.WOOL , 1 , (byte) 15 );
	private ItemStack pasteTool = new ItemStack( 
		Material.WOOL , 1 , (byte) 9 );
	private ItemStack measureTool = new ItemStack( 
		Material.WOOL , 1 , (byte) 1 );
	
	// Constructor
	public Selection()
	{
		// Get the metadata
		ItemMeta select = selectionTool.getItemMeta();
		ItemMeta paste = pasteTool.getItemMeta();
		ItemMeta measure = measureTool.getItemMeta();
		
		// Set the item meta
		select.setDisplayName( ChatColor.YELLOW + "Select" );
		selectionTool.setItemMeta( select );
		
		paste.setDisplayName( ChatColor.YELLOW + "Paste" );
		pasteTool.setItemMeta( paste );
		
		measure.setDisplayName( ChatColor.YELLOW + "Measure" );
		measureTool.setItemMeta( measure );
	}
	
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
			client.sendMessage( Bulldozer.ERROR_CONSOLE );
			return true;
		}
		
		/////////////////////////////////////////////////////////////////////
		// Kit
		if( cmd.getName().equalsIgnoreCase( "bdkit" ) )
		{
			// Cast the client as a player
			Player sender = (Player) client;
			
			// Give the player the tool if they don't have one
			if( !( sender.getInventory().contains( selectionTool ) ) )
			{	
				// Give
				sender.getInventory().addItem( selectionTool );
				
				// Output that the marker and paste tool was given
				sender.sendMessage( Bulldozer.TAG_POSITIVE 
					+ "Given: Marker Block (Black Wool)" );
			}
			
			// Give the player the tool if they don't have one
			if( !( sender.getInventory().contains( pasteTool ) ) )
			{	
				// Give
				sender.getInventory().addItem( pasteTool );
				
				// Output that the marker and paste tool was given
				sender.sendMessage( Bulldozer.TAG_POSITIVE 
					+ "Given: Paste Block (Blue Wool)" );
			}
			
			// Give the player the tool if they don't have one
			if( !( sender.getInventory().contains( measureTool ) ) )
			{	
				// Give
				sender.getInventory().addItem( measureTool );
				
				// Output that the marker and paste tool was given
				sender.sendMessage( Bulldozer.TAG_POSITIVE 
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
			BlockSet group = Bulldozer.core.getSelectionFor( 
				sender.getName() );
			
			// Clear the selection
			if( group.getSize() > 0 )
			{
				// Clear the selection (needs restore)
				group.restoreInWorld( true, sender.getWorld() );
				
				// Notify the player
				sender.sendMessage( Bulldozer.TAG_POSITIVE 
					+ "Selection Cleared" );
			}
			else
			{
				// Notify the player
				sender.sendMessage( Bulldozer.TAG_NEGATIVE 
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
			BlockSet group = Bulldozer.core.getClipboardFor( 
				sender.getName() ); 
			
			// Clear the clipboard
			if( group.getSize() > 0 )
			{
				// Clear the clipboard (doesn't need a restore)
				group.clearForReuse();
				
				// Notify the player
				sender.sendMessage( Bulldozer.TAG_POSITIVE 
					+ "Clipboard Cleared" );
			}
			else
			{
				// Notify the player
				sender.sendMessage( Bulldozer.TAG_NEGATIVE 
					+ "Clipboard already clear." );
			}
			
			// Return
			return true;
		}
		
		// Return false
		return false;
	}
}
