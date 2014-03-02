package com.yahoo.tracebachi;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.yahoo.tracebachi.Managers.BlockSet;

public class Listener_Tool implements Listener
{
	// Static class variables
	public static final String SELECT = ChatColor.YELLOW + "Select";
	public static final String PASTE = ChatColor.YELLOW + "Paste";
	public static final String MEASURE = ChatColor.YELLOW + "Measure";
	
	// Class variables
	private Bulldozer core = null ;
	
	// Default Constructor
	public Listener_Tool( Bulldozer instance ) { core = instance; }
	
	// Player Click Check
	@EventHandler
	public void onPlayerInteract( PlayerInteractEvent event )
	{
		// Method variables
		boolean wasAdded = false;
		String playerName = event.getPlayer().getName() ;
		String itemDisplayName = new String( "&" );
		Player user = event.getPlayer();
		Block clicked = event.getClickedBlock();
		
		// Try to get the name
		if( event.hasItem() )
		{
			if( event.getItem().hasItemMeta() )
			{
				if( event.getItem().getItemMeta().hasDisplayName() )
				{
					itemDisplayName = event.getItem()
						.getItemMeta().getDisplayName();
				}
			}
		}
		
		// Check on block type
		if( itemDisplayName.equalsIgnoreCase( SELECT ) && 
			core.verifyPerm( user, "Select" ) && event.hasBlock() )
		{
			// Add the block to the selection
			BlockSet result = 
				Bulldozer.core.getSelectionFor( playerName );
			
			// Add it to the set
			wasAdded = result.addBlock( clicked );
			
			// Tell the player if the block was added
			if( wasAdded )
			{
				// Check if it was the first block
				if( result.getSize() == 1 )
				{
					// Change the block to gold
					clicked.setType( Material.GOLD_BLOCK );
					result.setKeyBlock( 
						clicked.getX(),
						clicked.getY(),
						clicked.getZ() );
				}
								
				user.sendRawMessage( Bulldozer.TAG_POSITIVE
					+ "Block was added.");
			}
			else
			{
				user.sendRawMessage( Bulldozer.TAG_POSITIVE
					+ "Block already in selection.");
			}
			
			// Cancel the event
			event.setCancelled( true );
		}
		else if( itemDisplayName.equalsIgnoreCase( PASTE ) && 
			core.verifyPerm( user, "Paste" ) && event.hasBlock() )
		{
			// Get the current clip board
			BlockSet result = 
				Bulldozer.core.getClipboardFor( playerName );
			
			// Check if the clip board is empty
			if( result.getSize() > 0 )
			{
				// Recreate at location
				result = result.recreateInWorld( false, 
					clicked.getX(), clicked.getY(), clicked.getZ(), 
					clicked.getWorld() );
				
				// Add to the undo storage
				Bulldozer.core.pushIntoUndoFor( playerName, result );
				
				// Tell the player of the paste
				user.sendRawMessage( Bulldozer.TAG_POSITIVE
					+ "Paste Complete.");
			}
			else
			{
				// Tell the player paste failed
				user.sendRawMessage( Bulldozer.ERROR_NO_CLIPBOARD );
			}
			
			// Cancel the event
			event.setCancelled( true );
		}
		else if( itemDisplayName.equalsIgnoreCase( MEASURE ) && 
			core.verifyPerm( user, "Measure" ) && event.hasBlock() )
		{
			BlockSet result = 
				Bulldozer.core.getSelectionFor( playerName );
			Block first = result.getKeyBlock( clicked.getWorld() );
			
			// Verify selection is not empty
			if( result.getSize() > 0 )
			{
				// Tell the the distance
				user.sendRawMessage( ChatColor.GREEN 
					+ "Distance in x, y, z: "
					+ ChatColor.YELLOW + "[ " + ChatColor.LIGHT_PURPLE
					+ (clicked.getX() - first.getX()) + ", "
					+ (clicked.getY() - first.getY()) + ", "
					+ (clicked.getZ() - first.getZ())
					+ ChatColor.YELLOW + " ]" );
			}
			else
			{
				// Tell there is no selection
				user.sendRawMessage( Bulldozer.ERROR_NO_SELECTION );
			}
			
			// Cancel the event
			event.setCancelled( true );
		}
	}
}
