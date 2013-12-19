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
public class Replace implements CommandExecutor 
{

	// Class variables
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Box Default Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Replace( Bulldozer instance )
	{
		core = instance;
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
		if( baseCommand.getName().equalsIgnoreCase( "replace" ) )
		{
			// Check if client is a player
			if( client instanceof Player )
			{
				// Create/Set player variables
				Player cPlayer = (Player) client;
				World cPlayerWorld = cPlayer.getWorld();
				String cPlayerName = cPlayer.getName();
				BlockGroup cPlayerSelection = core.playerSelections.getGroupFor( cPlayerName );
				BlockGroup blocksToStore = null;
				
				int[] maxCoord = null , minCoord = null ;
				int listSize = 0 , lowOffset = 0 , highOffset = 0; 
				int[] originalBlockID = new int[2];
				int[] desiredBlockID = new int[2];
				
				//---------------------------------------------------------------------------//
				// Check One: Verify Player has a valid command -----------------------------//
				if( argLen < 2 || argLen > 5 )
				{
					cPlayer.sendMessage( ChatColor.YELLOW + "The possible commands are ( -c for chunk based , -p for inbetween blocks ):" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /replace -c [Original Block ID] [Final Block ID] [High Offset] [Low Offset]" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /replace -p [Original Block ID] [Final Block ID] [High Offset] [Low Offset]" );
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
				if( !(core.verifyPerm( cPlayer , "Replace" )) )
				{
					cPlayer.sendMessage( core.ERROR_PERM );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Four: Set up the data for manipulation -----------------------------//
				listSize = (int) cPlayerSelection.getSize();
				
				// Get the minimum and maximum array
				maxCoord = cPlayerSelection.getMaximums();
				minCoord = cPlayerSelection.getMinimums();
				
				//---------------------------------------------------------------------------//
				// Check Five: Verify Valid Values (Parse-able Values) ----------------------//
				try
				{
					switch( argLen )
					{
						case 5:
							lowOffset = core.safeInt( commandArgs[4] , 0 , minCoord[1] - 5 );
						case 4:
							highOffset = core.safeInt( commandArgs[3] , 0 , 254 - maxCoord[1] );
						case 3:
							desiredBlockID = core.safeIntList( commandArgs[2] , 0 , 173 );
						case 2:
							originalBlockID = core.safeIntList( commandArgs[1] , 0 , 173 );
							break;
						default:
							highOffset = lowOffset = 0;
							break;
					}
				}
				catch( NumberFormatException nfe )
				{
					cPlayer.sendMessage( core.ERROR_INT );
					return true;
				}

				//---------------------------------------------------------------------------//
				//----------- Chunk Fill ----------------------------------------------------//
				if( commandArgs[0].equalsIgnoreCase( "-c" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup();
					
					// Revert the selection without clearing the selection
					cPlayerSelection.restoreBlocks( cPlayerWorld , false );
					
					// Execute for chunks
					for( int listIndex = 0 ; listIndex < listSize ; listIndex++ )
					{
						// Set up chunk variables
						Block chunkMinBlock = cPlayerSelection.getChunkOfBlock( cPlayerWorld, listIndex ).getBlock( 0 , 1 , 0 );
						Block chunkMaxBlock = cPlayerSelection.getChunkOfBlock( cPlayerWorld, listIndex ).getBlock( 15 , 1 , 15 );
						
						// Execute Change
						setCuboid( cPlayerWorld , blocksToStore , 
							chunkMinBlock.getX() , minCoord[1] - lowOffset , chunkMinBlock.getZ() , 
							chunkMaxBlock.getX() , maxCoord[1] + highOffset , chunkMaxBlock.getZ() , 
							originalBlockID[0] , (byte) originalBlockID[1] , desiredBlockID[0] , (byte) desiredBlockID[1] );
					}
					
					// Push the recorded blocks
					core.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( core.TAG_POSITIVE + "Chunk Replace Complete." );
					return true;
				}
				//---------------------------------------------------------------------------//
				//----------- Point Fill ----------------------------------------------------//
				else if( commandArgs[0].equalsIgnoreCase( "-p" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup();
					
					// Revert the selection without clearing the selection
					cPlayerSelection.restoreBlocks( cPlayerWorld , false );
					
					// Execute Change
					setCuboid( cPlayerWorld , blocksToStore , 
						minCoord[0] , minCoord[1] - lowOffset , minCoord[2] , 
						maxCoord[0] , maxCoord[1] + highOffset , maxCoord[2] , 
						originalBlockID[0] , (byte) originalBlockID[1] , desiredBlockID[0] , (byte) desiredBlockID[1] );
					
					// Push the recorded blocks
					core.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( core.TAG_POSITIVE + "Point Box Complete." );
					return true;
				}
			}
			else { client.sendMessage( core.ERROR_CONSOLE ); }
		}
		// Return false by default
		return false;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	setCuboid
	// Purpose: 	Convert a rectangular prism of the selected blocks to a different ID
	//////////////////////////////////////////////////////////////////////////////////////////////
	private void setCuboid( World curWorld , BlockGroup blockStorage , int minX , int minY , int minZ , int maxX , int maxY , int maxZ , 
						int originalBlock , byte oData , int finalBlock , byte fData )
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
					if( cursorBlock.getTypeId() == originalBlock ) 
					{ 
						// Record the data
						blockStorage.addBlock( blockX , blockY , blockZ , originalBlock , oData );
						
						// Change the data
						cursorBlock.setTypeId( finalBlock );
						cursorBlock.setData( fData );
					}
				}
			}
		}	
	}
	
}
