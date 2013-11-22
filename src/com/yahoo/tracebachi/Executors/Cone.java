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

@SuppressWarnings("deprecation")
public class Cone implements CommandExecutor 
{

	// Class variables
	private Bulldozer mainPlugin = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Cone Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Cone( Bulldozer instance )
	{	
		mainPlugin = instance;
	}

	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "cone" command
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client , Command baseCommand , String arg2 , String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length ;
		
		// Check the command
		if( baseCommand.getName().equalsIgnoreCase( "cone" ) )
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
				
				int lowOffset = 0 , height = 0 , cylRadius = 0;
				int[] desiredBlockID = new int[2];
				
				//---------------------------------------------------------------------------//
				// Check One: Verify Player has a valid command -----------------------------//
				if( argLen < 2 || argLen > 4 )
				{
					cPlayer.sendMessage( ChatColor.YELLOW + "The possible commands are:" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /cone -f [Block ID] [Radius] [Height]" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /cone -h [Block ID] [Radius] [Height]" );
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
				if( !(mainPlugin.verifyPerm( cPlayer , "Cone" )) )
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
						case 4:
							height = mainPlugin.safeInt( commandArgs[3] , 0 , 254 - firstBlock.getY() );
						case 3:
							cylRadius = mainPlugin.safeInt( commandArgs[2] , 0 , 2000 );
						case 2:
							desiredBlockID = mainPlugin.safeIntList( commandArgs[1] , 0 , 173 );
							break;
						default:
							cylRadius = 0 ;
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
					setFilledCone( cPlayerWorld , blocksToStore , 
							firstBlock.getX() , firstBlock.getY() - lowOffset , firstBlock.getZ() , 
							height , cylRadius , desiredBlockID[0] , (byte) desiredBlockID[1] );
					
					// Push the recorded blocks
					mainPlugin.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( ChatColor.GREEN + "[Bulldozer] Filled Cone Complete." );
					return true;
				}
				//---------------------------------------------------------------------------//
				//----------- Hollow Cylinder -----------------------------------------------//
				else if( commandArgs[0].equalsIgnoreCase( "-h" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup();
					
					// Execute Change ( X = 0 ; Y = 1 ; Z = 2 )
					setHollowCone( cPlayerWorld , blocksToStore , 
							firstBlock.getX() , firstBlock.getY() - lowOffset , firstBlock.getZ() , 
							height , cylRadius , desiredBlockID[0] , (byte) desiredBlockID[1] );
					
					// Push the recorded blocks
					mainPlugin.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( ChatColor.GREEN + "[Bulldozer] Hollow Cone Complete." );
					return true;
				}
					
			}
			else { client.sendMessage( mainPlugin.ERROR_CONSOLE ); }
		}
		
		// Return false by default
		return false;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	setHollowCone
	// Purpose: 	Converts the area selected into a hollow cone (cone walls only)
	//////////////////////////////////////////////////////////////////////////////////////////////
	private void setHollowCone( World curWorld , BlockGroup blockStorage , int startX , int startY , int startZ , int height , int radius , int blockType , byte bData )
	{
		// Method variables
		int blockX = 0, blockZ = 0;
		double stepX, stepZ, stepY = (1.0 / height);
		
		double twoPi = 2.0 * Math.PI;
		Block cursorBlock = null;
		
		// Loop through the area
		for( double start = 0.001 ; start <= twoPi ; start += 0.001 )
		{
			// Calculate the coordinate to start from
			blockX = startX + (int) (radius * Math.sin( start )); 
			blockZ = startZ + (int) (radius * Math.cos( start ));
			
			// Calculate the X and Z changes per Y coordinate change
			stepX = stepY * ( blockX - startX );
			stepZ = stepY * ( blockZ - startZ );

			// Loop through to the height at the determined Y coordinate change
			for( double i = 0 ; i <= height ; i += stepY )
			{
				// Calculate the X, Y offsets
				int offX = (int) (i * stepX);
				int offZ = (int) (i * stepZ);
				
				// Get the block
				cursorBlock = curWorld.getBlockAt( startX + offX, startY + height - (int) i, startZ + offZ );

				// If not same as the block type, change it and record the data
				if( cursorBlock.getTypeId() != blockType )
				{
					// Record the data
					blockStorage.addBlock( startX + offX, startY + height - (int) i , startZ + offZ, cursorBlock.getTypeId() , cursorBlock.getData() );
					
					// Change the data
					cursorBlock.setTypeId( blockType );
					cursorBlock.setData( bData );
				}
			}
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	setFilledCone
	// Purpose: 	Converts the area into a filled / non-hollow cone
	//////////////////////////////////////////////////////////////////////////////////////////////
	private void setFilledCone( World curWorld , BlockGroup blockStorage , int startX , int startY , int startZ , int height , int radius , int blockType, byte bData)
	{	
		// Method variables
		int blockX = 0, blockZ = 0;
		double stepX, stepZ, stepY = (1.0 / height);
		
		double twoPi = 2.0 * Math.PI;
		Block cursorBlock = null;
		
		// Loop through the area
		for( double start = 0.001 ; start <= twoPi ; start += 0.001 )
		{
			// Calculate the coordinate to start from
			blockX = startX + (int) (radius * Math.sin( start )); 
			blockZ = startZ + (int) (radius * Math.cos( start ));
			
			// Calculate the X and Z changes per Y coordinate change
			stepX = stepY * ( blockX - startX );
			stepZ = stepY * ( blockZ - startZ );

			// Loop through to the height at the determined Y coordinate change
			for( double i = 0 ; i <= height ; i += stepY )
			{
				// Calculate the X, Z offsets
				int offX = (int) (i * stepX);
				int offZ = (int) (i * stepZ);

				// Loop and change all the blocks below
				for( int blockY = startY + height - (int) i ; blockY >= startY ; blockY-- )
				{
					// Get the block
					cursorBlock = curWorld.getBlockAt( startX + offX, blockY, startZ + offZ );
					
					// If not same as the block type, change it and record the data
					if( cursorBlock.getTypeId() != blockType )
					{
						// Record the data
						blockStorage.addBlock( startX + offX, blockY, startZ + offZ, cursorBlock.getTypeId() , cursorBlock.getData() );
						
						// Change the data
						cursorBlock.setTypeId( blockType );
						cursorBlock.setData( bData );
					}
				}
			}
		}
	}
	
}
