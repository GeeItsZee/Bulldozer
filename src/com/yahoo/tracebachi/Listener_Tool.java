package com.yahoo.tracebachi;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.yahoo.tracebachi.Utils.BlockGroup;


public class Listener_Tool implements Listener
{

	// Class variables
	private Bulldozer core = null ;
	
	// Default Constructor
	public Listener_Tool( Bulldozer instance )
	{
		// Link with the main class
		core = instance;
	}
	
	// Player Click Check
	@EventHandler
	public void onPlayerInteract( PlayerInteractEvent event )
	{
		// Get the variables to check
		String pName = event.getPlayer().getName() ;
		
		// Verify a Right-Click on Block and Item used has Display Name
		if( event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem() )
		{
			// Verify if the Item has meta data to check
			if( event.getItem().hasItemMeta() )
			{
				// Execute for Select
				if( event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase( ChatColor.YELLOW + "Marker" ) && 
						core.verifyPerm( event.getPlayer() , "Select" ) )
				{
					// Add it to the selection list
					core.playerSelections.addBlockFor( pName , event.getClickedBlock() );
					
					// Advise the player that the block was added
					event.getPlayer().sendMessage( core.TAG_POSITIVE + "Block added to the selection." );
					
					// Cancel the event
					event.setCancelled( true );
				}
				// Execute for Paste
				else if( event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase( ChatColor.YELLOW + "Paste Block" ) && 
						core.verifyPerm( event.getPlayer() , "Paste" ) )
				{
					// Initialize variables
					Block target = event.getClickedBlock();
					BlockGroup tempGroup = core.playerCopy.getGroupFor( pName );
					
					// Recreate (move the reference)
					tempGroup = tempGroup.recreateAt( target.getWorld() , target.getX() , target.getY() , target.getZ() );
					
					// Store if not empty
					if( ! tempGroup.isEmpty() )
					{
						// Add the paste to the undo storage
						core.playerUndo.pushGroupFor( pName , tempGroup );
						
						// Advise the player
						event.getPlayer().sendMessage( core.TAG_POSITIVE + "Paste operation complete." );
						tempGroup = null;
					}
					else
					{
						// Advise the player
						event.getPlayer().sendMessage( core.TAG_NEGATIVE + "Nothing to paste!" );
					}
					
					// Cancel the event
					event.setCancelled( true );
				}
			}
			
		}
	}
}
