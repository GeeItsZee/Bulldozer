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
public class Cone implements CommandExecutor 
{

	// Class variables
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Cone Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Cone( Bulldozer instance )
	{	
		core = instance;
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
				BlockGroup cPlayerSelection = core.playerSelections.getSelectionFor( cPlayerName );
				BlockGroup blocksToStore = null;
				Block firstBlock = null;
				
				int height = 0 , cylRadius = 0;
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
					cPlayer.sendMessage( core.ERROR_SELECTION );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Three: Verify Player Permissions (Send error if false) -------------//
				if( !(core.verifyPerm( cPlayer , "Cone" )) )
				{
					cPlayer.sendMessage( core.ERROR_PERM );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Four: Set up the data for manipulation -----------------------------//
				firstBlock = cPlayerSelection.getFirst();
				
				//---------------------------------------------------------------------------//
				// Check Five: Verify Valid Values (Parse-able Values) ----------------------//
				try
				{
					switch( argLen )
					{
						case 4:
							height = core.safeInt( commandArgs[3] , 5 - firstBlock.getY() , 254 - firstBlock.getY() );
						case 3:
							cylRadius = core.safeInt( commandArgs[2] , 0 , 2000 );
						case 2:
							desiredBlockID = core.safeIntList( commandArgs[1] , 0 , 173 );
							break;
						default:
							cylRadius = 0 ;
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
					blocksToStore = new BlockGroup( cPlayerWorld );
					
					// Revert the selection without clearing the selection
					cPlayerSelection.revertBlocks( false );
							
					// Execute Change ( X = 0 ; Y = 1 ; Z = 2 )
					setFilledCone( cPlayerWorld , blocksToStore , 
							firstBlock.getX() , firstBlock.getY() , firstBlock.getZ() , 
							height , cylRadius , desiredBlockID[0] , (byte) desiredBlockID[1] );
					
					// Push the recorded blocks
					core.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( core.TAG_POSITIVE + "Filled Cone Complete." );
					return true;
				}
				//---------------------------------------------------------------------------//
				//----------- Hollow Cylinder -----------------------------------------------//
				else if( commandArgs[0].equalsIgnoreCase( "-h" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup( cPlayerWorld );
					
					// Revert the selection without clearing the selection
					cPlayerSelection.revertBlocks( false );
					
					// Execute Change ( X = 0 ; Y = 1 ; Z = 2 )
					setHollowCone( cPlayerWorld , blocksToStore , 
							firstBlock.getX() , firstBlock.getY() , firstBlock.getZ() , 
							height , cylRadius , desiredBlockID[0] , (byte) desiredBlockID[1] );
					
					// Push the recorded blocks
					core.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( core.TAG_POSITIVE + "Hollow Cone Complete." );
					return true;
				}
					
			}
			else { client.sendMessage( core.ERROR_CONSOLE ); }
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
		double stepX, stepZ, stepY = (1.0 / height);
		int coneTop = startY + height;
		
		double twoPi = 2.000 * Math.PI;
		Block cursorBlock = null;
		
		// Check the case
		if( height > 0 )
		{
			// Loop through the area
			for( double radian = 0.001 ; radian <= twoPi ; radian += 0.001 )
			{
				// Calculate the coordinate changes per step
				stepX = stepY * (int) (radius * Math.sin( radian )); 
				stepZ = stepY * (int) (radius * Math.cos( radian ));

				// Loop through to the height at the determined Y coordinate change
				for( double i = 0.0 ; i <= height ; i += stepY )
				{
					// Calculate the coordinates
					int offX = startX + (int) (i * stepX);
					int offZ = startZ + (int) (i * stepZ);
					
					// Get the block
					cursorBlock = curWorld.getBlockAt( offX , coneTop - (int) i, offZ );

					// If not same as the block type, change it and record the data
					if( cursorBlock.getTypeId() != blockType )
					{
						// Record the data
						blockStorage.addBlock( offX , coneTop - (int) i , offZ , cursorBlock.getTypeId() , cursorBlock.getData() );
						
						// Change the data
						cursorBlock.setTypeId( blockType );
						cursorBlock.setData( bData );
					}
				}
			}
		}
		else
		{
			// Loop through the area
			for( double radian = 0.001 ; radian <= twoPi ; radian += 0.001 )
			{
				// Calculate the coordinate changes per step
				stepX = stepY * (int) (radius * Math.sin( radian )); 
				stepZ = stepY * (int) (radius * Math.cos( radian ));

				// Loop through to the height at the determined Y coordinate change
				for( double i = 0.0 ; i >= height ; i += stepY )
				{
					// Calculate the coordinates
					int offX = startX + (int) (i * stepX);
					int offZ = startZ + (int) (i * stepZ);
					
					// Get the block
					cursorBlock = curWorld.getBlockAt( offX , coneTop - (int) i, offZ );

					// If not same as the block type, change it and record the data
					if( cursorBlock.getTypeId() != blockType )
					{
						// Record the data
						blockStorage.addBlock( offX , coneTop - (int) i , offZ , cursorBlock.getTypeId() , cursorBlock.getData() );
						
						// Change the data
						cursorBlock.setTypeId( blockType );
						cursorBlock.setData( bData );
					}
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
		double stepX, stepZ, stepY = (1.0 / height);
		int coneTop = startY + height;
		
		double twoPi = 2.000 * Math.PI;
		Block cursorBlock = null;
		
		// Check the case
		if( height > 0 )
		{
			// Loop through the area
			for( double radian = 0.001 ; radian <= twoPi ; radian += 0.001 )
			{
				// Calculate the coordinate changes per step
				stepX = stepY * (int) (radius * Math.sin( radian )); 
				stepZ = stepY * (int) (radius * Math.cos( radian ));

				// Loop through to the height at the determined Y coordinate change
				for( double i = 0.0 ; i <= height ; i += stepY )
				{
					// Calculate the coordinates
					int offX = startX + (int) (i * stepX);
					int offZ = startZ + (int) (i * stepZ);

					// Loop and change all the blocks below
					for( int blockY = coneTop - (int) i ; blockY >= startY ; blockY-- )
					{
						// Get the block
						cursorBlock = curWorld.getBlockAt( offX , blockY , offZ );
						
						// If not same as the block type, change it and record the data
						if( cursorBlock.getTypeId() != blockType )
						{
							// Record the data
							blockStorage.addBlock( offX , blockY , offZ , cursorBlock.getTypeId() , cursorBlock.getData() );
							
							// Change the data
							cursorBlock.setTypeId( blockType );
							cursorBlock.setData( bData );
						}
					}
				}
			}
		}
		else
		{
			// Loop through the area
			for( double radian = 0.001 ; radian <= twoPi ; radian += 0.001 )
			{
				// Calculate the coordinate changes per step
				stepX = stepY * (int) (radius * Math.sin( radian )); 
				stepZ = stepY * (int) (radius * Math.cos( radian ));

				// Loop through to the height at the determined Y coordinate change
				for( double i = 0.0 ; i >= height ; i += stepY )
				{
					// Calculate the coordinates
					int offX = startX + (int) (i * stepX);
					int offZ = startZ + (int) (i * stepZ);

					// Loop and change all the blocks below
					for( int blockY = coneTop - (int) i ; blockY >= startY ; blockY-- )
					{
						// Get the block
						cursorBlock = curWorld.getBlockAt( offX , blockY , offZ );
						
						// If not same as the block type, change it and record the data
						if( cursorBlock.getTypeId() != blockType )
						{
							// Record the data
							blockStorage.addBlock( offX , blockY , offZ , cursorBlock.getTypeId() , cursorBlock.getData() );
							
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
