package com.yahoo.tracebachi.Executors;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Managers.BlockSet;
import com.yahoo.tracebachi.Utils.InputParseUtil;

@SuppressWarnings("deprecation")
public class Cylinder implements CommandExecutor 
{
	// Class variables
	public static final String permName = "Cylinder";
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "cyl" command
	//////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender sender, Command baseCommand, 
		String arg2, String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length;
		int height = 1;
		int radius = 1;
		int[] blockType = null;
		String playerName = null;
		Player user = null;
		Block first = null;
		World playerWorld = null;
		BlockSet playerSelect = null;
		BlockSet changes = null;
		
		// Verify valid command
		if( ! baseCommand.getName().equalsIgnoreCase( "cyl" ) )
		{
			return true;
		}
		
		// Verify sender is a player
		if( ! (sender instanceof Player) )
		{
			sender.sendMessage( Bulldozer.ERROR_CONSOLE );
			return true;
		}
		
		// Verify permission
		if( ! Bulldozer.core.verifyPerm( sender, permName ) )
		{
			sender.sendMessage( Bulldozer.ERROR_NO_PERM );
			return true;
		}
		
		// Verify command size is valid
		if( argLen < 2 || argLen > 5 )
		{
			sender.sendMessage( ChatColor.YELLOW 
				+ "Command must be of the form:" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/cyl -f [Block Type] [Radius] [Height]" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/cyl -h [Block Type] [Radius] [Height]" );
			return true;
		}
		
		// Set player variables
		user = (Player) sender;
		playerName = user.getName();
		playerWorld = user.getWorld();
		playerSelect = Bulldozer.core.getSelectionFor( playerName );
		first = playerSelect.getKeyBlock( playerWorld );
		
		// Verify player has a selection
		if( playerSelect.getSize() < 1 )
		{
			user.sendMessage( Bulldozer.ERROR_NO_SELECTION );
			return true;
		}
		
		// Parse arguments
		switch( argLen )
		{
			case 4:
				height = InputParseUtil.parseSafeInt( 
					commandArgs[3], 
					1, 
					254 - first.getY(),
					1 );
			case 3:
				radius = InputParseUtil.parseSafeInt( 
					commandArgs[2], 
					1, 
					2000, 
					1 );
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
		// Cylinder - Filled
		if( commandArgs[0].equalsIgnoreCase( "-f" ) )
		{
			// Make a new group for the player
			changes = new BlockSet();
			
			// Revert the selection without clearing the selection
			playerSelect.restoreInWorld( false, playerWorld );
					
			// Execute Change
			setFilledCyl( playerWorld, changes, 
					first.getX(), first.getY(), first.getZ(), 
					height, radius, 
					blockType[0], (byte) blockType[1] );
			
			// Push the recorded blocks
			Bulldozer.core.pushIntoUndoFor( playerName, changes );
			changes = null;
			
			// Return for complete
			user.sendMessage( Bulldozer.TAG_POSITIVE + 
				"Cylinder [Fill] Complete." );
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Cylinder - Filled
		else if( commandArgs[0].equalsIgnoreCase( "-h" ) )
		{
			// Make a new group for the player
			changes = new BlockSet();
			
			// Revert the selection without clearing the selection
			playerSelect.restoreInWorld( false, playerWorld );
			
			// Execute Change
			setHollowCyl( playerWorld, changes, 
					first.getX(), first.getY(), first.getZ(), 
					height, radius, 
					blockType[0], (byte) blockType[1] );
			
			// Push the recorded blocks
			Bulldozer.core.pushIntoUndoFor( playerName, changes );
			changes = null;
			
			// Return for complete
			user.sendMessage( Bulldozer.TAG_POSITIVE + 
				"Cylinder [Hollow] Complete." );
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Default
		else
		{
			// Tell the player flag was invalid
			user.sendMessage( Bulldozer.ERROR_BAD_FLAG );
			return true;
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	setHollowCyl
	// Purpose: 	Set a hollow cylinder from parameters.
	//////////////////////////////////////////////////////////////////////////
	private void setHollowCyl( World curWorld, BlockSet blockStorage,
		int startX, int startY, int startZ, 
		int height, int radius, int blockType, byte bData )
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
			
			for( int yCoord = 0 ; yCoord < height ; yCoord++ )
			{
				// Get the block
				cursorBlock = curWorld.getBlockAt( 
					blockX, 
					startY + yCoord, 
					blockZ );
				
				// Record the data
				if( blockStorage.addBlock( cursorBlock ) )
				{
					// Change the data
					cursorBlock.setTypeId( blockType );
					cursorBlock.setData( bData );
				}
			}
		}	
	}
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	setFilledCyl
	// Purpose: 	Set a filled cylinder from the parameters.
	//////////////////////////////////////////////////////////////////////////
	private void setFilledCyl( World curWorld, BlockSet blockStorage, 
		int startX, int startY, int startZ,
		int height, int radius, int blockType, byte bData )
	{	
		// Method variables
		Block cursorBlock = null;
		
		// Loop through the area
		for( int xCoord = -radius ; xCoord <= radius ; xCoord++ )
		{
			for( int zCoord = -radius ; zCoord <= radius ; zCoord++ )
			{
				// Check if the coordinate is within the circle
				if( ((xCoord * xCoord) + (zCoord * zCoord)) < 
					(radius * radius) )
				{
					for( int yCoord = 0 ; yCoord < height ; yCoord++ )
					{
						// Get the block
						cursorBlock = curWorld.getBlockAt( 
							startX + xCoord, 
							startY + yCoord, 
							startZ + zCoord );
						
						// Record the data
						if( blockStorage.addBlock( cursorBlock ) )
						{
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
