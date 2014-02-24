package com.yahoo.tracebachi.Executors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Utils.BlockGroup;
import com.yahoo.tracebachi.Utils.InputParseUtil;

@SuppressWarnings("deprecation")
public class Sphere implements CommandExecutor 
{

	// Class variables
	public static final String permName = "Sphere";
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Sphere Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Sphere( Bulldozer instance ) { core = instance; }
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "sphere" command
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender sender, Command baseCommand,
		String arg2, String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length;
		int listSize = 0;
		int radius = 0;
		int[] blockType = null;
		String playerName = null;
		Player user = null;
		Location firstLoc = null;
		World playerWorld = null;
		BlockGroup playerSelect = null;
		BlockGroup blockChanges = null;
		
		// Verify valid command
		if( ! baseCommand.getName().equalsIgnoreCase( "sph" ) )
		{
			return true;
		}
		
		// Verify sender is a player
		if( ! (sender instanceof Player) )
		{
			sender.sendMessage( core.ERROR_CONSOLE );
			return true;
		}
		
		// Verify permission
		if( ! core.verifyPerm( sender, permName ) )
		{
			sender.sendMessage( core.ERROR_NO_PERM );
			return true;
		}
		
		// Verify command size is valid
		if( argLen < 2 || argLen > 3 )
		{
			sender.sendMessage( ChatColor.YELLOW 
				+ "Command must be of the form:" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/sph -f [Block Type] [Radius]" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/sph -h [Block Type] [Radius]" );
			return true;
		}
		
		// Set player variables
		user = (Player) sender;
		playerName = user.getName();
		playerWorld = user.getWorld();
		playerSelect = core.playerSelections.getGroupFor( playerName );
		listSize = (int) playerSelect.getSize();
		firstLoc = playerSelect.getFirstLocation( playerWorld );
		
		// Verify player has a selection
		if( listSize == 0 )
		{
			user.sendMessage( core.ERROR_NO_SELECTION );
			return true;
		}
		
		// Parse arguments
		switch( argLen )
		{
			case 3:
				radius = InputParseUtil.parseSafeInt( 
					commandArgs[2], 0, 2000, 0 );
			case 2:
				blockType = InputParseUtil.parseSafeIntPair(
					commandArgs[1], ":", 
					0, 173, 0,
					0, 16, 0 );
				break;
			default:
				break;
		}

		/////////////////////////////////////////////////////////////////////
		// Sphere - Filled
		if( commandArgs[0].equalsIgnoreCase( "-f" ) )
		{
			// Make a new group for the player
			blockChanges = new BlockGroup();
			
			// Revert the selection without clearing the selection
			playerSelect.restoreBlocks( playerWorld, false );
					
			// Execute Change
			setFilledSphere( playerWorld, blockChanges, 
				firstLoc.getBlockX(), firstLoc.getBlockY(),
				firstLoc.getBlockZ(),
				radius,
				blockType[0], (byte) blockType[1] );
			
			// Push the recorded blocks
			core.playerUndo.pushGroupFor( playerName, blockChanges );
			blockChanges = null;
			
			// Return for complete
			user.sendMessage( core.TAG_POSITIVE + 
				"Sphere [Fill] Complete." );
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Sphere - Filled
		else if( commandArgs[0].equalsIgnoreCase( "-h" ) )
		{
			// Make a new group for the player
			blockChanges = new BlockGroup();
			
			// Revert the selection without clearing the selection
			playerSelect.restoreBlocks( playerWorld, false );
			
			// Execute Change
			setHollowSphere( playerWorld, blockChanges, 
				firstLoc.getBlockX(), firstLoc.getBlockY(),
				firstLoc.getBlockZ(),
				radius,
				blockType[0], (byte) blockType[1] );
			
			// Push the recorded blocks
			core.playerUndo.pushGroupFor( playerName, blockChanges );
			blockChanges = null;
			
			// Return for complete
			user.sendMessage( core.TAG_POSITIVE + 
				"Sphere [Hollow] Complete." );
			return true;
		}
		
		// Return false by default
		return false;
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	setHollowSphere
	// Purpose: 	Set a hollow sphere.
	//////////////////////////////////////////////////////////////////////////
	private void setHollowSphere( World curWorld, BlockGroup blockStorage,
		int startX, int startY, int startZ,
		int radius, int blockType, byte bData )
	{	
		// Method variables
		int coordSquareSum = 0 ;
		int upperRadiusSquare = (int) ((radius + 0.5) * (radius + 0.5));
		int lowerRadiusSquare = (int) ((radius - 0.5) * (radius - 0.5));
		Block cursorBlock = null;
		
		// Loop through the area
		// TODO: Potential to eliminate iterations by calculating an area
		// TODO: where the points will always be 0
		for( int xMod = -radius ; xMod <= radius ; xMod++ )
		{
			for( int zMod = -radius ; zMod <= radius ; zMod++ )
			{
				for( int yMod = -radius ; yMod <= radius ; yMod++ )
				{
					coordSquareSum = (xMod * xMod) + (yMod * yMod)
						+ (zMod * zMod); 
					
					// Check if the point falls in the sphere
					if( coordSquareSum < upperRadiusSquare && 
						coordSquareSum > lowerRadiusSquare )
					{
						// Get the block
						cursorBlock = curWorld.getBlockAt( 
							startX + xMod, 
							startY + yMod, 
							startZ + zMod );
						
						// Record the data
						blockStorage.addBlock( cursorBlock );
						
						// Change the data
						cursorBlock.setTypeId( blockType );
						cursorBlock.setData( bData );
					}
				}
			}
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	setFilledSphere
	// Purpose: 	Set a filled sphere.
	//////////////////////////////////////////////////////////////////////////
	private void setFilledSphere( World curWorld, BlockGroup blockStorage,
		int startX, int startY, int startZ,
		int radius, int blockType, byte bData )
	{	
		// Method variables
		int coordSquareSum = 0 , radiusSquare = radius * radius;
		Block cursorBlock = null;
		
		// Loop through the area
		for( int xMod = -radius + 1 ; xMod < radius ; xMod++ )
		{
			for( int zMod = -radius + 1 ; zMod < radius ; zMod++ )
			{
				for( int yMod = -radius + 1 ; yMod < radius ; yMod++ )
				{
					coordSquareSum = (xMod * xMod) + (yMod * yMod)
						+ (zMod * zMod); 
					
					// Check if the point falls in the sphere
					if( coordSquareSum <= radiusSquare )
					{
						// Get the block
						cursorBlock = curWorld.getBlockAt( 
							startX + xMod, 
							startY + yMod, 
							startZ + zMod );
						
						// Record the data
						blockStorage.addBlock( cursorBlock );
						
						// Change the data
						cursorBlock.setTypeId( blockType );
						cursorBlock.setData( bData );
					}
				}
			}
		}
	}
	
}
