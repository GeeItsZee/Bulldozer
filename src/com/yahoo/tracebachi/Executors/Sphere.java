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
public class Sphere implements CommandExecutor 
{

	// Class variables
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Sphere Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Sphere( Bulldozer instance )
	{	
		core = instance;
	}

	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "sphere" command
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client , Command baseCommand , String arg2 , String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length ;
		
		// Check the command
		if( baseCommand.getName().equalsIgnoreCase( "sph" ) )
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
				
				int sphereRadius = 0;
				int[] desiredBlockID = new int[2];
				
				//---------------------------------------------------------------------------//
				// Check One: Verify Player has a valid command -----------------------------//
				if( argLen < 2 || argLen > 3 )
				{
					cPlayer.sendMessage( ChatColor.YELLOW + "The possible commands are:" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /sph -f [Block ID] [Radius]" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /sph -h [Block ID] [Radius]" );
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
				if( !(core.verifyPerm( cPlayer , "Sphere" )) )
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
						case 3:
							sphereRadius = core.safeInt( commandArgs[2] , 0 , 2000 );
						case 2:
							desiredBlockID = core.safeIntList( commandArgs[1] , 0 , 173 );
							break;
						default:
							sphereRadius = 0 ;
							break;
					}
				}
				catch( NumberFormatException nfe )
				{
					cPlayer.sendMessage( core.ERROR_INT );
					return true;
				}

				//---------------------------------------------------------------------------//
				//----------- Filled Sphere -------------------------------------------------//
				if( commandArgs[0].equalsIgnoreCase( "-f" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup();
					
					// Revert the selection without clearing the selection
					cPlayerSelection.restoreBlocks( cPlayerWorld , false );
							
					// Execute Change ( X = 0 ; Y = 1 ; Z = 2 )
					setFilledSphere( cPlayerWorld , blocksToStore , 
							firstBlock[0] , firstBlock[1] , firstBlock[2] ,
							sphereRadius , desiredBlockID[0] , (byte) desiredBlockID[1] );
					
					// Push the recorded blocks
					core.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( core.TAG_POSITIVE + "Filled Sphere Complete." );
					return true;
				}
				//---------------------------------------------------------------------------//
				//----------- Hollow Sphere -------------------------------------------------//
				else if( commandArgs[0].equalsIgnoreCase( "-h" ) )
				{
					// Make a new group for the player
					blocksToStore = new BlockGroup();
					
					// Revert the selection without clearing the selection
					cPlayerSelection.restoreBlocks( cPlayerWorld , false );
					
					// Execute Change ( X = 0 ; Y = 1 ; Z = 2 )
					setHollowSphere( cPlayerWorld , blocksToStore , 
							firstBlock[0] , firstBlock[1] , firstBlock[2] ,
							sphereRadius , desiredBlockID[0] , (byte) desiredBlockID[1] );
					
					// Push the recorded blocks
					core.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
					blocksToStore = null;
					
					// Return for complete
					cPlayer.sendMessage( core.TAG_POSITIVE + "Hollow Sphere Complete." );
					return true;
				}
					
			}
			else { client.sendMessage( core.ERROR_CONSOLE ); }
		}
		
		// Return false by default
		return false;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	setHollowSphere
	// Purpose: 	Converts the area selected into a hollow sphere using triple-nested for loops
	//////////////////////////////////////////////////////////////////////////////////////////////
	private void setHollowSphere( World curWorld , BlockGroup blockStorage , int startX , int startY , int startZ , int radius , int blockType , byte bData )
	{	
		// Method variables
		int coordSquareSum = 0 ;
		int upperRadiusSquare = (int) ((radius + 0.5) * (radius + 0.5));
		int lowerRadiusSquare = (int) ((radius - 0.5) * (radius - 0.5));
		Block cursorBlock = null;
		
		// Loop through the area
		for( int xModifier = -radius ; xModifier <= radius ; xModifier++ )
		{
			for( int zModifier = -radius ; zModifier <= radius ; zModifier++ )
			{
				for( int yModifier = -radius ; yModifier <= radius ; yModifier++ )
				{
					coordSquareSum = (xModifier * xModifier) + (yModifier * yModifier) + (zModifier * zModifier); 
					
					// Check if the point falls in the sphere
					if( coordSquareSum < upperRadiusSquare && coordSquareSum > lowerRadiusSquare )
					{
						// Get the block
						cursorBlock = curWorld.getBlockAt( startX + xModifier, startY + yModifier, startZ + zModifier );
						
						// If not same as the block type, change it and record the data
						if( cursorBlock.getTypeId() != blockType )
						{
							// Record the data
							blockStorage.addBlock( startX + xModifier, startY + yModifier, startZ + zModifier , cursorBlock.getTypeId() , cursorBlock.getData() );
							
							// Change the data
							cursorBlock.setTypeId( blockType );
							cursorBlock.setData( bData );
						}
					}
				}
			}
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	setFilledSphere
	// Purpose: 	Everything in the selection becomes a solid sphere
	//////////////////////////////////////////////////////////////////////////////////////////////
	private void setFilledSphere( World curWorld , BlockGroup blockStorage , int startX , int startY , int startZ , int radius , int blockType , byte bData )
	{	
		// Method variables
		int coordSquareSum = 0 , radiusSquare = radius * radius;
		Block cursorBlock = null;
		
		// Loop through the area
		for( int xModifier = -radius + 1 ; xModifier < radius ; xModifier++ )
		{
			for( int zModifier = -radius + 1 ; zModifier < radius ; zModifier++ )
			{
				for( int yModifier = -radius + 1 ; yModifier < radius ; yModifier++ )
				{
					coordSquareSum = (xModifier * xModifier) + (yModifier * yModifier) + (zModifier * zModifier); 
					
					// Check if the point falls in the sphere
					if( coordSquareSum <= radiusSquare )
					{
						// Get the block
						cursorBlock = curWorld.getBlockAt( startX + xModifier, startY + yModifier, startZ + zModifier );
						
						// If not same as the block type, change it and record the data
						if( cursorBlock.getTypeId() != blockType )
						{
							// Record the data
							blockStorage.addBlock( startX + xModifier, startY + yModifier, startZ + zModifier , cursorBlock.getTypeId() , cursorBlock.getData() );
							
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
