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


public class Cylinder implements CommandExecutor 
{

	// Class variables
	private Bulldozer mainPlugin = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Cylinder Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Cylinder( Bulldozer instance )
	{	
		mainPlugin = instance;
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
				List < SelBlock > cPlayerSelection = mainPlugin.playerSelections.getSelectionFor( cPlayerName );
				BlockGroup blocksToStore = null;
				Block firstBlock = null;
				
				int lowOffset = 0 , highOffset = 0 , desiredBlockID = 0 , cylRadius = 0;
				
				//---------------------------------------------------------------------------//
				// Check One: Verify Player has a valid command -----------------------------//
				if( argLen == 0 )
				{
					cPlayer.sendMessage( ChatColor.YELLOW + "The possible commands are:" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /cyl -f [Block ID] [Radius] [Height] [Low Offset]" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /cyl -h [Block ID] [Radius] [Height] [Low Offset]" );
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
				if( !(mainPlugin.verifyPerm( cPlayerName , "Cylinder" )) )
				{
					cPlayer.sendMessage( mainPlugin.ERROR_PERM );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Four: Set up the data for manipulation -----------------------------//
				firstBlock = cPlayerSelection.get(0).toStore;
				
				//---------------------------------------------------------------------------//
				// Check Five: Verify Valid Values (Parse-able Values) ----------------------//
				try
				{
					switch( argLen )
					{
						case 5:
							lowOffset = mainPlugin.safeInt( commandArgs[4] , 0 , firstBlock.getY() - 5 );
						case 4:
							highOffset = mainPlugin.safeInt( commandArgs[3] , 0 , 254 - firstBlock.getY() );
						case 3:
							cylRadius = mainPlugin.safeInt( commandArgs[2] , 0 , 2000 );
						case 2:
							desiredBlockID = mainPlugin.safeInt( commandArgs[1] , 0 , 173 );
							break;
						default:
							desiredBlockID = cylRadius = 0 ;
							break;
					}
				}
				catch( NumberFormatException nfe )
				{
					cPlayer.sendMessage( mainPlugin.ERROR_INT );
					return true;
				}

				//---------------------------------------------------------------------------//
				//----------- Filled Cylinder -----------------------------------------------//
				if( commandArgs[0].equalsIgnoreCase( "-f" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup();
							
					// Execute Change ( X = 0 ; Y = 1 ; Z = 2 )
					setFilledCyl( cPlayerWorld , blocksToStore , 
							firstBlock.getX() , firstBlock.getY() - lowOffset , firstBlock.getZ() , 
							firstBlock.getY() + highOffset , 
							cylRadius , desiredBlockID );
					
					// Push the recorded blocks
					mainPlugin.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( ChatColor.GREEN + "[Bulldozer] Filled Cylinder Complete." );
					return true;
				}
				//---------------------------------------------------------------------------//
				//----------- Hollow Cylinder -----------------------------------------------//
				else if( commandArgs[0].equalsIgnoreCase( "-h" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup();
					
					// Execute Change ( X = 0 ; Y = 1 ; Z = 2 )
					setHollowCyl( cPlayerWorld , blocksToStore , 
							firstBlock.getX() , firstBlock.getY() - lowOffset , firstBlock.getZ() , 
							firstBlock.getY() + highOffset , 
							cylRadius , desiredBlockID );
					
					// Push the recorded blocks
					mainPlugin.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( ChatColor.GREEN + "[Bulldozer] Hollow Cylinder Complete." );
					return true;
				}
					
			}
			else { client.sendMessage( mainPlugin.ERROR_CONSOLE ); }
		}
		
		// Return false by default
		return false;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	setHollowCyl
	// Purpose: 	Converts the area selected into a hollow cylinder
	//////////////////////////////////////////////////////////////////////////////////////////////
	private void setHollowCyl( World curWorld , BlockGroup blockStorage , int startX , int startY , int startZ , int maxHeight , int radius , int blockType )
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
					blockStorage.addBlock( blockX , blockY , blockZ , cursorBlock.getTypeId() , (byte) 0 );
					
					// Change the data
					cursorBlock.setTypeId( blockType );
				}
			}
		}
		
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	setFilledCyl
	// Purpose: 	Everything in the selection becomes a cylinder of the same block
	//////////////////////////////////////////////////////////////////////////////////////////////
	private void setFilledCyl( World curWorld , BlockGroup blockStorage , int startX , int startY , int startZ , int maxHeight , int radius , int blockType )
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
							blockStorage.addBlock( startX + xCoord , blockY , startZ + zCoord , cursorBlock.getTypeId() , (byte) 0 );
							
							// Change the data
							cursorBlock.setTypeId( blockType );
						}
					}
				}
			}
		}
		
	}
	
}
