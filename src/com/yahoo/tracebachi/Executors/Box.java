package com.yahoo.tracebachi.Executors;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
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
public class Box implements CommandExecutor 
{

	// Create the executor's plug-in class instance for linking
	public static final String permName = "Box";
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	Box Default Constructor
	//////////////////////////////////////////////////////////////////////////
	public Box( Bulldozer instance ) { core = instance; }
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "box" command
	//////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender sender, Command baseCommand, 
		String arg2, String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length;
		int lowOffset = 0;
		int highOffset = 0;
		int[] blockType = null;
		String playerName = null;
		Player user = null;
		Location maxLoc = null;
		Location minLoc = null ;
		World playerWorld = null;
		BlockSet playerSelect = null;
		BlockSet changes = null;
		
		// Verify valid command
		if( ! baseCommand.getName().equalsIgnoreCase( "box" ) )
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
		if( argLen < 2 || argLen > 4 )
		{
			sender.sendMessage( ChatColor.YELLOW
				+ "Command must be of the form:" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/box -c [Block Type] [High Offset] [Low Offset]" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/box -p [Block Type] [High Offset] [Low Offset]" );
			return true;
		}
		
		// Set player variables
		user = (Player) sender;
		playerName = user.getName();
		playerWorld = user.getWorld();
		playerSelect = core.playerSelection.getGroupFor( playerName );
		maxLoc = playerSelect.getMaxLocation( playerWorld );
		minLoc = playerSelect.getMinLocation( playerWorld );
		
		// Verify player has a selection
		if( playerSelect.getSize() < 1 )
		{
			user.sendMessage( core.ERROR_NO_SELECTION );
			return true;
		}
		
		// Parse arguments
		switch( argLen )
		{
			case 4:
				lowOffset = InputParseUtil.parseSafeInt( 
					commandArgs[3], 
					0, 
					minLoc.getBlockY() - 2, 
					0 );
			case 3:
				highOffset = InputParseUtil.parseSafeInt( 
					commandArgs[2], 
					0, 
					254 - maxLoc.getBlockY(),
					0 );
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
		// Box - Chunk
		if( commandArgs[0].equalsIgnoreCase( "-c" ) )
		{
			// Make a new group for the player
			changes = new BlockSet();
			
			// Revert the selection without clearing the selection
			playerSelect.restoreInWorld( false, playerWorld );
			
			// Execute for chunks
			for( Chunk iter : playerSelect.getChunkSet( playerWorld ) )
			{
				// Loop variables
				Block low = iter.getBlock( 0, 1, 0 );
				Block high = iter.getBlock( 15, 1, 15 );
				
				// Run Edit
				setCuboid( playerWorld, changes,
					low.getX(), minLoc.getBlockY() - lowOffset,
					low.getZ(), 
					high.getX(), maxLoc.getBlockY() + highOffset,
					high.getZ(),
					blockType[0], (byte) blockType[1] );
			}
			
			// Push the recorded blocks
			core.playerUndo.pushGroupFor( playerName, changes );
			changes = null;
			
			// Return for complete
			user.sendMessage( core.TAG_POSITIVE + "Box [Chunk] Complete." );
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Border - Point
		else if( commandArgs[0].equalsIgnoreCase( "-p" ) )
		{
			// Make a new group for the player
			changes = new BlockSet();
			
			// Revert the selection without clearing the selection
			playerSelect.restoreInWorld( false, playerWorld );
			
			// Run Edit
			setCuboid( playerWorld, changes, 
				minLoc.getBlockX(), minLoc.getBlockY() - lowOffset, 
				minLoc.getBlockZ(), 
				maxLoc.getBlockX(), maxLoc.getBlockY() + highOffset, 
				maxLoc.getBlockZ(),
				blockType[0], (byte) blockType[1] );
			
			// Push the recorded blocks
			core.playerUndo.pushGroupFor( playerName, changes );
			changes = null;
			
			// Return for complete
			user.sendMessage( core.TAG_POSITIVE + "Box [Point] Complete." );
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Default
		else
		{
			// Tell the player flag was invalid
			user.sendMessage( core.ERROR_BAD_FLAG );
			return true;
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	setCuboid
	// Purpose: 	Set prism to parameter values.
	//////////////////////////////////////////////////////////////////////////
	private void setCuboid( World curWorld, BlockSet blockStorage,
		int minX, int minY, int minZ, 
		int maxX, int maxY, int maxZ, 
		int blockType , byte bData )
	{	
		// Method variables
		Block cursorBlock = null;
		
		// Loop through the area
		for( int iterY = minY ; iterY <= maxY ; iterY++ )
		{
			for( int iterX = minX ; iterX <= maxX ; iterX++ )
			{
				for( int iterZ = minZ ; iterZ <= maxZ ; iterZ++ )
				{
					// Get the block
					cursorBlock = curWorld.getBlockAt( 
						iterX , iterY , iterZ );
					
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
