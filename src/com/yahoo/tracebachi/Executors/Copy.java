package com.yahoo.tracebachi.Executors;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Utils.BlockGroup;


@SuppressWarnings("deprecation")
public class Copy implements CommandExecutor
{
	
	// Create the executor's plug-in class instance for linking
	private Bulldozer core;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Selection Default Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Copy( Bulldozer instance )
	{
		// Link the main instance with this executor
		core = instance;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles "copy" command
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client, Command cmd , String label, String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length ;
		
		// Check for command
		if( cmd.getName().equalsIgnoreCase( "copy" ) )
		{
			// Check if the client is a player
			if( client instanceof Player )
			{
				// Create/Set player variables
				Player cPlayer = (Player) client;
				World cPlayerWorld = cPlayer.getWorld();
				String cPlayerName = cPlayer.getName();
				BlockGroup cPlayerSelection = core.playerSelections.getGroupFor( cPlayerName );
				BlockGroup clipBoard = core.playerCopy.getGroupFor( cPlayerName );
				Block cursorBlock = null;
				
				int highOffset = 0, lowOffset = 0;
				int[] maxCoord = null , minCoord = null ;
				
				//---------------------------------------------------------------------------//
				// Check One: Verify Player has a valid command -----------------------------//
				if( argLen < 1 || argLen > 3 )
				{
					cPlayer.sendMessage( ChatColor.YELLOW + "The possible commands are:" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /copy -y [High Offset] [Low Offset]" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /copy -n [High Offset] [Low Offset]" );
					cPlayer.sendMessage( ChatColor.YELLOW + "Make sure you have a selection before running the command." );
					return true;	
				}
				
				//---------------------------------------------------------------------------//
				// Check Two: Verify Player has a selection ---------------------------------//
				if( cPlayerSelection == null )
				{
					cPlayer.sendMessage( core.ERROR_SELECTION );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Three: Verify Player Permissions (Send error if false) -------------//
				if( !(core.verifyPerm( cPlayer , "Copy" )) )
				{
					cPlayer.sendMessage( core.ERROR_PERM );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Four: Set up the data for manipulation -----------------------------//
				maxCoord = cPlayerSelection.getMaximums();
				minCoord = cPlayerSelection.getMinimums();
				
				// Wipe the clip-board if not empty and set the key block
				clipBoard.clearBlockInfo();
				clipBoard.setKeyBlock( cPlayerSelection.getKeyBlock( cPlayerWorld ) );
				
				// Clear the selection of the player (and restore it)
				core.playerSelections.removeGroupFor( cPlayerName , true , cPlayerWorld );
				
				//---------------------------------------------------------------------------//
				// Check Five: Verify Valid Values (Parse-able Values) ----------------------//
				try
				{
					switch( argLen )
					{
						case 3:
							lowOffset = core.safeInt( commandArgs[2] , 0 , minCoord[1] - 5 );
						case 2:
							highOffset = core.safeInt( commandArgs[1] , 0 , 254 - maxCoord[1] );
							break;
						default:
							highOffset = lowOffset = 0 ;
							break;
					}
				}
				catch( NumberFormatException nfe )
				{
					cPlayer.sendMessage( core.ERROR_INT );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				//----------- Add Air -------------------------------------------------------//
				if( commandArgs[0].equalsIgnoreCase( "-y" ) )
				{
					// Store the selection
					for( int blockY = minCoord[1] - lowOffset; blockY <= maxCoord[1] + highOffset ; blockY++ )
					{
						for( int blockX = minCoord[0] ; blockX <= maxCoord[0] ; blockX++ )
						{
							for( int blockZ = minCoord[2] ; blockZ <= maxCoord[2] ; blockZ++ )
							{
								// Get the block
								cursorBlock = cPlayerWorld.getBlockAt( blockX , blockY , blockZ );
								
								// Record the data into the clip-board
								clipBoard.addBlock( cursorBlock );
							}
						}
					}
					
					// Output that the selection was copied
					cPlayer.sendMessage( core.TAG_POSITIVE + "Selection Copied" );
					return true;
				}
				else if( commandArgs[0].equalsIgnoreCase( "-n" ) )
				{
					// Store the selection
					for( int blockY = minCoord[1] - lowOffset; blockY <= maxCoord[1] + highOffset ; blockY++ )
					{
						for( int blockX = minCoord[0] ; blockX <= maxCoord[0] ; blockX++ )
						{
							for( int blockZ = minCoord[2] ; blockZ <= maxCoord[2] ; blockZ++ )
							{
								// Get the block
								cursorBlock = cPlayerWorld.getBlockAt( blockX , blockY , blockZ );
								
								// Verify not air
								if( cursorBlock.getTypeId() != 0 )
								{
									// Record the data
									clipBoard.addBlock( cursorBlock );
								}
							}
						}
					}
					
					// Output that the selection was copied
					cPlayer.sendMessage( core.TAG_POSITIVE + "Selection Copied" );
					return true;
				}
			}
			else { client.sendMessage( core.ERROR_CONSOLE ); }
		}
		
		// Return false by default
		return false;
	}

}
