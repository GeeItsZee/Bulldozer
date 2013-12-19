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
public class Cylinder implements CommandExecutor 
{

	// Class variables
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Cylinder Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Cylinder( Bulldozer instance )
	{	
		core = instance;
	}

	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "cyl" command
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client , Command baseCommand , String arg2 , String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length ;
		
		// Check the command
		if( baseCommand.getName().equalsIgnoreCase( "cyl" ) )
		{	
			// Check if client is a player
			if( client instanceof Player )
			{
				// Create/Set player variables
				Player cPlayer = (Player) client;
				World cPlayerWorld = cPlayer.getWorld();
				String cPlayerName = cPlayer.getName() ;
				BlockGroup cPlayerSelection = core.playerSelections.getGroupFor( cPlayerName );
				BlockGroup blocksToStore = null;
				int[] firstBlock = new int[5];
				
				int lowOffset = 0 , height = 0 , cylRadius = 0;
				int[] desiredBlockID = new int[2];
				
				//---------------------------------------------------------------------------//
				// Check One: Verify Player has a valid command -----------------------------//
				if( argLen < 2 || argLen > 5 )
				{
					cPlayer.sendMessage( ChatColor.YELLOW + "The possible commands are:" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /cyl -f [Block ID] [Radius] [Height] [Low Offset]" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /cyl -h [Block ID] [Radius] [Height] [Low Offset]" );
					cPlayer.sendMessage( ChatColor.YELLOW + "Make sure you have a selection before running the command." );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Two: Verify Player has a selection ---------------------------------//
				if( cPlayerSelection.isEmpty() )
				{
					cPlayer.sendMessage( core.ERROR_SELECTION );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Three: Verify Player Permissions (Send error if false) -------------//
				if( !(core.verifyPerm( cPlayer , "Cylinder" )) )
				{
					cPlayer.sendMessage( core.ERROR_PERM );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Four: Set up the data for manipulation -----------------------------//
				firstBlock = cPlayerSelection.get( 0 );
				
				//---------------------------------------------------------------------------//
				// Check Five: Verify Valid Values (Parse-able Values) ----------------------//
				try
				{
					switch( argLen )
					{
						case 5:
							lowOffset = core.safeInt( commandArgs[4] , 0 , firstBlock[2] - 5 );
						case 4:
							height = core.safeInt( commandArgs[3] , 0 , 254 - firstBlock[2] );
						case 3:
							cylRadius = core.safeInt( commandArgs[2] , 0 , 2000 );
						case 2:
							desiredBlockID = core.safeIntList( commandArgs[1] , 0 , 173 );
							break;
						default:
							lowOffset = height = cylRadius = 0 ;
							break;
					}
				}
				catch( NumberFormatException nfe )
				{
					cPlayer.sendMessage( core.ERROR_INT );
					return true;
				}

				//---------------------------------------------------------------------------//
				//----------- Filled Cylinder -----------------------------------------------//
				if( commandArgs[0].equalsIgnoreCase( "-f" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup();
					
					// Revert the selection without clearing the selection
					cPlayerSelection.restoreBlocks( cPlayerWorld , false );
							
					// Execute Change ( X = 0 ; Y = 1 ; Z = 2 )
					setFilledCyl( cPlayerWorld , blocksToStore , 
							firstBlock[0] , firstBlock[1] - lowOffset , firstBlock[2] , 
							firstBlock[1] + height , 
							cylRadius , desiredBlockID[0] , (byte) desiredBlockID[1] );
					
					// Push the recorded blocks
					core.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( core.TAG_POSITIVE + "Filled Cylinder Complete." );
					return true;
				}
				//---------------------------------------------------------------------------//
				//----------- Hollow Cylinder -----------------------------------------------//
				else if( commandArgs[0].equalsIgnoreCase( "-h" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup();
					
					// Revert the selection without clearing the selection
					cPlayerSelection.restoreBlocks( cPlayerWorld , false );
					
					// Execute Change ( X = 0 ; Y = 1 ; Z = 2 )
					setHollowCyl( cPlayerWorld , blocksToStore , 
						firstBlock[0] , firstBlock[1] - lowOffset , firstBlock[2] , 
						firstBlock[1] + height , 
							cylRadius , desiredBlockID[0] , (byte) desiredBlockID[1] );
					
					// Push the recorded blocks
					core.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( core.TAG_POSITIVE + "Hollow Cylinder Complete." );
					return true;
				}
					
			}
			else { client.sendMessage( core.ERROR_CONSOLE ); }
		}
		
		// Return false by default
		return false;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	setHollowCyl
	// Purpose: 	Converts the area selected into a hollow cylinder
	//////////////////////////////////////////////////////////////////////////////////////////////
	private void setHollowCyl( World curWorld , BlockGroup blockStorage , int startX , int startY , int startZ , int maxHeight , int radius , int blockType , byte bData )
	{
		// Method variables
		int blockX = 0 , blockZ = 0;
		double endVal = 2 * Math.PI;
		Block cursorBlock = null;
		
		// Loop through the area
		for( double start = 0.001 ; start <= endVal ; start += 0.001 )
		{
			blockX = startX + (int) (radius * Math.sin( start )); 
			blockZ = startZ + (int) (radius * Math.cos( start ));
			
			for( int blockY = startY ; blockY <= maxHeight ; blockY++ )
			{
				// Get the block
				cursorBlock = curWorld.getBlockAt( blockX , blockY , blockZ );
				
				// If not same as the block type, change it and record the data
				if( cursorBlock.getTypeId() != blockType ) 
				{
					// Record the data
					blockStorage.addBlock( blockX , blockY , blockZ , cursorBlock.getTypeId() , cursorBlock.getData() );
					
					// Change the data
					cursorBlock.setTypeId( blockType );
					cursorBlock.setData( bData );
				}
			}
		}
		
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	setFilledCyl
	// Purpose: 	Everything in the selection becomes a cylinder of the same block
	//////////////////////////////////////////////////////////////////////////////////////////////
	private void setFilledCyl( World curWorld , BlockGroup blockStorage , int startX , int startY , int startZ , int maxHeight , int radius , int blockType , byte bData )
	{	
		// Method variables
		Block cursorBlock = null;
		
		// Loop through the area
		for( int xCoord = -radius ; xCoord <= radius ; xCoord++ )
		{
			for( int zCoord = -radius ; zCoord <= radius ; zCoord++ )
			{
				// Check if the coordinate is within the circle
				if( ((xCoord * xCoord) + (zCoord * zCoord)) < (radius * radius) )
				{
					for( int blockY = startY ; blockY <= maxHeight ; blockY++ )
					{
						// Get the block
						cursorBlock = curWorld.getBlockAt( startX + xCoord , blockY , startZ + zCoord );
						
						// If not same as the block type, change it and record the data
						if( cursorBlock.getTypeId() != blockType ) 
						{
							// Record the data
							blockStorage.addBlock( startX + xCoord , blockY , startZ + zCoord , cursorBlock.getTypeId() , cursorBlock.getData() );
							
							// Change the data
							cursorBlock.setTypeId( blockType );
							cursorBlock.setData( bData );
						}
					}
				}
			}
		}
		
	}
	
}
