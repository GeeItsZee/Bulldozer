package com.yahoo.tracebachi.Executors;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Utils.BlockGroup;
import com.yahoo.tracebachi.Utils.SelectionManager.SelBlock;


public class Box implements CommandExecutor 
{

	// Class variables
	private Bulldozer mainPlugin = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Box Default Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Box( Bulldozer instance )
	{
		mainPlugin = instance;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "box" command
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client , Command baseCommand , String arg2 , String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length ;
		
		// Check the command
		if( baseCommand.getName().equalsIgnoreCase( "box" ) )
		{
			// Check if client is a player
			if( client instanceof Player )
			{
				// Create/Set player variables
				Player cPlayer = (Player) client;
				World cPlayerWorld = cPlayer.getWorld();
				String cPlayerName = cPlayer.getName() ;
				List < SelBlock > cPlayerSelection = mainPlugin.playerSelections.getSelectionFor( cPlayerName );
				BlockGroup blocksToStore = null;
				
				int[] maxCoord = null , minCoord = null ;
				int listSize = 0 , lowOffset = 0 , highOffset = 0 , desiredBlockID = 0 ;
				
				//---------------------------------------------------------------------------//
				// Check One: Verify Player has a valid command -----------------------------//
				if( argLen == 0 )
				{
					cPlayer.sendMessage( ChatColor.YELLOW + "The possible commands are:" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /box -c [Block ID] [High Offset] [Low Offset]" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /box -b [Block ID] [High Offset] [Low Offset]" );
					cPlayer.sendMessage( ChatColor.YELLOW + "Make sure you have a selection before running the command." );
					return true;	
				}
				
				//---------------------------------------------------------------------------//
				// Check Two: Verify Player has a selection ---------------------------------//
				if( cPlayerSelection == null )
				{
					cPlayer.sendMessage( mainPlugin.ERROR_SELECTION );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Three: Verify Player Permissions (Send error if false) -------------//
				if( !(mainPlugin.verifyPerm( cPlayerName , "Box" )) )
				{
					cPlayer.sendMessage( mainPlugin.ERROR_PERM );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Four: Set up the data for manipulation -----------------------------//
				listSize = cPlayerSelection.size();
				
				// Get the minimum and maximum array
				maxCoord = mainPlugin.playerSelections.getMaximumsFor( cPlayerName );
				minCoord = mainPlugin.playerSelections.getMinimumsFor( cPlayerName );
				
				//---------------------------------------------------------------------------//
				// Check Five: Verify Valid Values (Parse-able Values) ----------------------//
				try
				{
					switch( argLen )
					{
						case 4:
							lowOffset = mainPlugin.safeInt( commandArgs[3] , 0 , minCoord[1] - 5 );
						case 3:
							highOffset = mainPlugin.safeInt( commandArgs[2] , 0 , 254 - maxCoord[1] );
						case 2:
							desiredBlockID = mainPlugin.safeInt( commandArgs[1] , 0 , 173 );
							break;
						default:
							highOffset = lowOffset = desiredBlockID = 0 ;
							break;
					}
				}
				catch( NumberFormatException nfe )
				{
					cPlayer.sendMessage( mainPlugin.ERROR_INT );
					return true;
				}

				//---------------------------------------------------------------------------//
				//----------- Chunk Fill ----------------------------------------------------//
				if( commandArgs[0].equalsIgnoreCase( "-c" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup();
					
					// Execute for chunks
					for( int listIndex = 0 ; listIndex < listSize ; listIndex++ )
					{
						// Set up chunk variables
						Block chunkMinBlock = cPlayerSelection.get( listIndex ).toStore.getChunk().getBlock( 0 , 1 , 0 );
						Block chunkMaxBlock = cPlayerSelection.get( listIndex ).toStore.getChunk().getBlock( 15 , 1 , 15 );
						
						// Execute Change
						setCuboid( cPlayerWorld , blocksToStore , 
							chunkMinBlock.getX() , minCoord[1] - lowOffset , chunkMinBlock.getZ() , 
							chunkMaxBlock.getX() , maxCoord[1] + highOffset , chunkMaxBlock.getZ() , 
							desiredBlockID );
					}
					
					// Push the recorded blocks
					mainPlugin.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( ChatColor.GREEN + "[Bulldozer] Chunk Box Complete." );
					return true;
				}
				//---------------------------------------------------------------------------//
				//----------- Point Fill ----------------------------------------------------//
				else if( commandArgs[0].equalsIgnoreCase( "-p" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup();
					
					// Execute Change
					setCuboid( cPlayerWorld , blocksToStore , 
						minCoord[0] , minCoord[1] - lowOffset , minCoord[2] , 
						maxCoord[0] , maxCoord[1] + highOffset , maxCoord[2] , 
						desiredBlockID );
					
					// Push the recorded blocks
					mainPlugin.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( ChatColor.GREEN + "[Bulldozer] Point Box Complete." );
					return true;
				}
			}
			else { client.sendMessage( mainPlugin.ERROR_CONSOLE ); }
		}
		// Return false by default
		return false;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	setCuboid
	// Purpose: 	Convert a rectangular prism of the selected blocks to a different ID
	//////////////////////////////////////////////////////////////////////////////////////////////
	private void setCuboid( World curWorld , BlockGroup blockStorage , int minX , int minY , int minZ , int maxX , int maxY , int maxZ , int blockType )
	{
			
		// Method variables
		Block cursorBlock = null;
		
		// Loop through the area
		for( int blockY = minY ; blockY <= maxY ; blockY++ )
		{
				
			for( int blockX = minX ; blockX <= maxX ; blockX++ )
			{
					
				for( int blockZ = minZ ; blockZ <= maxZ ; blockZ++ )
				{
						
					// Get the block
					cursorBlock = curWorld.getBlockAt( blockX , blockY , blockZ );
					
					// If not same as the block type, change it and record the data
					if( cursorBlock.getTypeId() != blockType ) 
					{ 
							
						// Record the data
						blockStorage.addBlock( blockX , blockY , blockZ , cursorBlock.getTypeId() , (byte) 0 );
						
						// Change the data
						cursorBlock.setTypeId( blockType );
							
					}
				}
			}
		}	
	}
	
}
