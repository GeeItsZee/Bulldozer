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
public class Replace implements CommandExecutor 
{
	// Class variables
	public static final String permName = "Replace";
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	Box Default Constructor
	//////////////////////////////////////////////////////////////////////////
	public Replace( Bulldozer instance ) { core = instance; }

	//////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "replace" command
	//////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender sender, Command baseCommand, 
		String arg2, String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length;
		int lowOffset = 0;
		int highOffset = 0;
		int[] oldBlockType = null;
		int[] newBlockType = null;
		String playerName = null;
		Player user = null;
		Location maxLoc = null;
		Location minLoc = null ;
		World playerWorld = null;
		BlockSet playerSelect = null;
		BlockSet changes = null;
		
		// Verify valid command
		if( ! baseCommand.getName().equalsIgnoreCase( "replace" ) )
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
		if( argLen < 3 || argLen > 5 )
		{
			sender.sendMessage( ChatColor.YELLOW 
				+ "Command must be of the form:" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/replace -c "
				+ "[Old Block] [New Block] [High Off] [Low Off]" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/replace -p "
				+ "[Old Block] [New Block] [High Off] [Low Off]" );
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
			case 5:
				lowOffset = InputParseUtil.parseSafeInt( 
					commandArgs[4], 
					0, 
					minLoc.getBlockY() - 5, 
					0 );
			case 4:
				highOffset = InputParseUtil.parseSafeInt( 
					commandArgs[3], 
					0, 
					254 - maxLoc.getBlockY(),
					0 );
			case 3:
				newBlockType = InputParseUtil.parseSafeIntPair(
					commandArgs[2], ":", 
					0, 173, 0,
					0, 16, 0 );
			case 2:
				oldBlockType = InputParseUtil.parseSafeIntPair(
					commandArgs[1], ":", 
					0, 173, 0,
					0, 16, 0 );
				break;
			default:
				break;
		}			
		
		/////////////////////////////////////////////////////////////////////
		// Replace - Chunk
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
					oldBlockType[0], (byte) oldBlockType[1],
					newBlockType[0], (byte) newBlockType[1] );
			}
			
			// Push the recorded blocks
			core.playerUndo.pushGroupFor( playerName, changes );
			changes = null;
			
			// Return for complete
			user.sendMessage( core.TAG_POSITIVE 
				+ "Replace [Chunk] Complete." );
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Replace - Point
		else if( commandArgs[0].equalsIgnoreCase( "-p" ) )
		{
			// Make a new group for the player
			changes = new BlockSet();
			
			// Revert the selection without clearing the selection
			playerSelect.restoreInWorld( false, playerWorld );
			
			// Execute Change
			setCuboid( playerWorld, changes, 
				minLoc.getBlockX(), minLoc.getBlockY() - lowOffset,
				minLoc.getBlockZ(), 
				maxLoc.getBlockX(), maxLoc.getBlockY() + highOffset,
				maxLoc.getBlockZ(),
				oldBlockType[0], (byte) oldBlockType[1],
				newBlockType[0], (byte) newBlockType[1] );
			
			// Push the recorded blocks
			core.playerUndo.pushGroupFor( playerName, changes );
			changes = null;
			
			// Return for complete
			user.sendMessage( core.TAG_POSITIVE 
				+ "Point Box Complete." );
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
	// Purpose: 	Replace blocks in the area.
	//////////////////////////////////////////////////////////////////////////
	private void setCuboid( World curWorld, BlockSet blockStorage, 
		int minX, int minY, int minZ, int maxX, int maxY, int maxZ, 
		int oBlock, byte oData, int fBlock, byte fData )
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
					cursorBlock = curWorld.getBlockAt( 
						blockX , blockY , blockZ );
					
					// If not same as the block type
					if( cursorBlock.getTypeId() == oBlock )
					{ 
						// Record the data
						blockStorage.addBlock( cursorBlock );
						
						// Change the data
						cursorBlock.setTypeId( fBlock );
						cursorBlock.setData( fData );
					}
				}
			}
		}	
	}
	
}
