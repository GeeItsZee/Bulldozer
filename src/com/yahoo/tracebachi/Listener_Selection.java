package com.yahoo.tracebachi;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class Listener_Selection implements Listener
	{

		// Class variables
		private Bulldozer mainPlugin = null ;
		
		// Default Constructor
		public Listener_Selection( Bulldozer instance )
			{
				
				// Link with the main class
				mainPlugin = instance;
				
			}
		
		// Player Click Check
		@EventHandler
		public void onPlayerInteract( PlayerInteractEvent playerInter )
			{
				
				// Get the variables to check
				String pName = playerInter.getPlayer().getName() ;
				
				// Verify a Right-Click on Block and Item used has Display Name
				if( playerInter.getAction() == Action.RIGHT_CLICK_BLOCK && playerInter.hasItem() )
					{
						
						// Verify if the Item has meta data to check
						if( playerInter.getItem().hasItemMeta() )
							{
							
								// Verify the Item is named "Marker" and Player has permission
								if( playerInter.getItem().getItemMeta().getDisplayName().equalsIgnoreCase( ChatColor.BLUE + "Marker" ) && 
										mainPlugin.verifyPerm( pName , "Select" ) )
									{
										
										// Add it to the selections
										if( mainPlugin.playerSelections.addSelectionFor( pName , playerInter.getClickedBlock().getLocation() ) )
											{
												
												// Tell the user block was added
												playerInter.getPlayer().sendMessage( ChatColor.GREEN + "Block added." );
												
											}
										else
											{
												
												// Tell the user block was already in the list
												playerInter.getPlayer().sendMessage( ChatColor.RED + "Block is already in the selection." );
												
											}
									}
							}
					}
			}
		
	}
