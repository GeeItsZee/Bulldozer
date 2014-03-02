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
import com.yahoo.tracebachi.Managers.BlockSet;
import com.yahoo.tracebachi.Utils.InputParseUtil;

@SuppressWarnings("deprecation")
public class Copy implements CommandExecutor
{
	// Create the executor's plug-in class instance for linking
	public static final String permName = "Copy";

	//////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles "copy" command
	//////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender sender, Command baseCommand, 
		String arg2, String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length;
		int lowOffset = 0;
		int highOffset = 0;
		String playerName = null;
		Player user = null;
		Location maxLoc = null;
		Location minLoc = null;
		World playerWorld = null;
		Block cursorBlock = null;
		BlockSet playerSelect = null;
		BlockSet clipBoard = null;
		
		// Verify valid command
		if( ! baseCommand.getName().equalsIgnoreCase( "copy" ) )
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
		if( argLen < 1 || argLen > 3 )
		{
			sender.sendMessage( ChatColor.YELLOW 
				+ "Command must be of the form:" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/copy -y [High Offset] [Low Offset]" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/copy -n [High Offset] [Low Offset]" );
			return true;
		}
		
		// Set player variables
		user = (Player) sender;
		playerName = user.getName();
		playerWorld = user.getWorld();
		playerSelect = Bulldozer.core.getSelectionFor( playerName );
		clipBoard = Bulldozer.core.getClipboardFor( playerName );
		maxLoc = playerSelect.getMaxLocation( playerWorld );
		minLoc = playerSelect.getMinLocation( playerWorld );
		
		// Verify player has a selection
		if( playerSelect.getSize() < 1 )
		{
			user.sendMessage( Bulldozer.ERROR_NO_SELECTION );
			return true;
		}
		else
		{
			// Initial setup
			cursorBlock = playerSelect.getKeyBlock( playerWorld );
			clipBoard.clearForReuse();
			clipBoard.setKeyBlock( 
				cursorBlock.getX(),
				cursorBlock.getY(),
				cursorBlock.getZ() );
			
			// Restore the selection
			playerSelect.restoreInWorld( false, playerWorld );
		}
		
		// Parse arguments
		switch( argLen )
		{
			case 3:
				lowOffset = InputParseUtil.parseSafeInt( 
					commandArgs[2], 
					0, 
					minLoc.getBlockY() - 2,
					0 );
			case 2:
				highOffset = InputParseUtil.parseSafeInt( 
					commandArgs[1],
					0, 
					254 - maxLoc.getBlockY(),
					0 );
				break;
			default:
				break;
		}
		
		/////////////////////////////////////////////////////////////////////
		// Copy - Air
		if( commandArgs[0].equalsIgnoreCase( "-y" ) )
		{
			// Store the selection
			for( int blockY = minLoc.getBlockY() - lowOffset; 
				blockY <= maxLoc.getBlockY() + highOffset ; blockY++ )
			{
				for( int blockX = minLoc.getBlockX() ; 
					blockX <= maxLoc.getBlockX() ; blockX++ )
				{
					for( int blockZ = minLoc.getBlockZ() ; 
						blockZ <= maxLoc.getBlockZ() ; blockZ++ )
					{
						// Get the block
						cursorBlock = playerWorld.getBlockAt( 
							blockX , blockY , blockZ );
						
						// Record the data into the clip-board
						clipBoard.addBlock( cursorBlock );
					}
				}
			}
			
			// Output that the selection was copied
			user.sendMessage( Bulldozer.TAG_POSITIVE 
				+ "Selection Copied With Air." );
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Copy - Not Air
		else if( commandArgs[0].equalsIgnoreCase( "-n" ) )
		{
			// Store the selection
			for( int blockY = minLoc.getBlockY() - lowOffset; 
				blockY <= maxLoc.getBlockY() + highOffset ; blockY++ )
			{
				for( int blockX = minLoc.getBlockX() ; 
					blockX <= maxLoc.getBlockX() ; blockX++ )
				{
					for( int blockZ = minLoc.getBlockZ() ; 
						blockZ <= maxLoc.getBlockZ() ; blockZ++ )
					{
						// Get the block
						cursorBlock = playerWorld.getBlockAt( 
							blockX , blockY , blockZ );
						
						// Check for air
						if( cursorBlock.getTypeId() != 0 )
						{
							// Record the data into the clip-board
							clipBoard.addBlock( cursorBlock );
						}
					}
				}
			}
			
			// Output that the selection was copied
			user.sendMessage( Bulldozer.TAG_POSITIVE 
				+ "Selection Copied Without Air." );
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
}
