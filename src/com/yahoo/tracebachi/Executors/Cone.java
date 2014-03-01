package com.yahoo.tracebachi.Executors;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Utils.BlockSet;
import com.yahoo.tracebachi.Utils.InputParseUtil;

@SuppressWarnings("deprecation")
public class Cone implements CommandExecutor 
{
	// Class variables
	public static final String permName = "Cone";
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Cone Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Cone( Bulldozer instance ) { core = instance; }
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "cone" command
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
		if( ! baseCommand.getName().equalsIgnoreCase( "cone" ) )
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
				+ "/cone -f [Block Type] [Radius] [Height]" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/cone -h [Block Type] [Radius] [Height]" );
			return true;
		}
		
		// Set player variables
		user = (Player) sender;
		playerName = user.getName();
		playerWorld = user.getWorld();
		playerSelect = core.playerSelection.getGroupFor( playerName );
		first = playerSelect.getKeyBlock( playerWorld );
		
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
				height = InputParseUtil.parseSafeInt( 
					commandArgs[3], 
					5 - first.getY(), 
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
		// Cone - Filled
		if( commandArgs[0].equalsIgnoreCase( "-f" ) )
		{
			// Make a new group for the player
			changes = new BlockSet();
			
			// Revert the selection without clearing the selection
			playerSelect.restoreInWorld( false, playerWorld );
					
			// Execute Change
			setFilledCone( playerWorld, changes,
				first.getX(), first.getY(), first.getZ(), 
				height, radius, 
				blockType[0], (byte) blockType[1] );
			
			// Push the recorded blocks
			core.playerUndo.pushGroupFor( playerName , changes );
			changes = null;
			
			// Return for complete
			user.sendMessage( core.TAG_POSITIVE 
				+ "Cone [Fill] Complete." );
			return true;
		}
		/////////////////////////////////////////////////////////////////////
		// Cone - Hollow
		else if( commandArgs[0].equalsIgnoreCase( "-h" ) )
		{
			// Make a new group for the player
			changes = new BlockSet();
			
			// Revert the selection without clearing the selection
			playerSelect.restoreInWorld( false, playerWorld );
			
			// Execute Change
			setHollowCone( playerWorld, changes,
				first.getX(), first.getY(), first.getZ(),
				height, radius, 
				blockType[0], (byte) blockType[1] );
			
			// Push the recorded blocks
			core.playerUndo.pushGroupFor( playerName, changes );
			System.out.print( changes.getSize() );
			changes = null;
			
			// Return for complete
			user.sendMessage( core.TAG_POSITIVE 
				+ "Cone [Hollow] Complete." );
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
	// Method: 	setHollowCone
	// Purpose: 	Set to hollow cone.
	//////////////////////////////////////////////////////////////////////////
	private void setHollowCone( World curWorld, BlockSet blockStorage, 
		int startX, int startY, int startZ, 
		int height, int radius, int blockType, byte bData )
	{
		// Method variables
		double stepX;
		double stepZ; 
		double stepY;
		int coneTip = startY + height;
		
		double twoPi = 2.000 * Math.PI;
		Block cursorBlock = null;
		
		// TODO: Faster circle math
		// Check the case
		if( height > 0 )
		{
			// Set the step
			stepY = (1.0 / height );
			
			// Loop through the area
			for( double radian = 0.001 ; radian <= twoPi ; radian += 0.001 )
			{
				// Calculate the coordinate changes per step
				stepX = stepY * (int) (radius * Math.sin( radian )); 
				stepZ = stepY * (int) (radius * Math.cos( radian ));

				// Step through the height
				for( double i = 0.0 ; i <= height ; i += stepY )
				{
					// Calculate the coordinates
					int offX = startX + (int) (i * stepX);
					int offZ = startZ + (int) (i * stepZ);
					
					// Get the block
					cursorBlock = curWorld.getBlockAt( 
						offX , coneTip - (int) i, offZ );
					
					// Check if in set
					if( blockStorage.addBlock( cursorBlock ) )
					{							
						// Change the data
						cursorBlock.setTypeId( blockType );
						cursorBlock.setData( bData );
					}
				}
			}
		}
		else if( height < 0 )
		{
			// Set the step
			stepY = (1.0 / height );
			
			// Loop through the area
			for( double radian = 0.001 ; radian <= twoPi ; radian += 0.001 )
			{
				// Calculate the coordinate changes per step
				stepX = stepY * (int) (radius * Math.sin( radian )); 
				stepZ = stepY * (int) (radius * Math.cos( radian ));

				// Step through the height
				for( double i = 0.0 ; i >= height ; i += stepY )
				{
					// Calculate the coordinates
					int offX = startX + (int) (i * stepX);
					int offZ = startZ + (int) (i * stepZ);
					
					// Get the block
					cursorBlock = curWorld.getBlockAt( 
						offX , coneTip - (int) i, offZ );
					
					// Check if in set
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
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	setFilledCone
	// Purpose: 	Set to filled cone.
	//////////////////////////////////////////////////////////////////////////
	private void setFilledCone( World curWorld, BlockSet blockStorage, 
		int startX, int startY, int startZ, 
		int height, int radius, int blockType, byte bData )
	{	
		// Method variables
		double stepX;
		double stepZ; 
		double stepY;
		int coneTip = startY + height;
		
		double twoPi = 2.000 * Math.PI;
		Block cursorBlock = null;
		
		// Check the case
		if( height > 0 )
		{
			// Set the step
			stepY = (1.0 / height );
			
			// Loop through the area
			for( double radian = 0.001 ; radian <= twoPi ; radian += 0.001 )
			{
				// Calculate the coordinate changes per step
				stepX = stepY * (int) (radius * Math.sin( radian )); 
				stepZ = stepY * (int) (radius * Math.cos( radian ));

				// Step through the height
				for( double i = 0.0 ; i <= height ; i += stepY )
				{
					// Calculate the coordinates
					int initY = coneTip - (int) i;
					int offX = startX + (int) (i * stepX);
					int offZ = startZ + (int) (i * stepZ);

					// Loop and change all the blocks below
					for( int blockY = initY ; blockY >= startY ; blockY-- )
					{
						// Get the block
						cursorBlock = curWorld.getBlockAt( 
							offX, blockY, offZ );
						
						// Check if in set
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
		else if( height < 0 )
		{
			// Set the step
			stepY = (1.0 / height );
			
			// Loop through the area
			for( double radian = 0.001 ; radian <= twoPi ; radian += 0.001 )
			{
				// Calculate the coordinate changes per step
				stepX = stepY * (int) (radius * Math.sin( radian )); 
				stepZ = stepY * (int) (radius * Math.cos( radian ));

				// Step through the height
				for( double i = 0.0 ; i >= height ; i += stepY )
				{
					// Calculate the coordinates
					int initY = coneTip - (int) i;
					int offX = startX + (int) (i * stepX);
					int offZ = startZ + (int) (i * stepZ);

					// Loop and change all the blocks below
					for( int blockY = initY ; blockY >= startY ; blockY-- )
					{
						// Get the block
						cursorBlock = curWorld.getBlockAt( 
							offX, blockY, offZ );
						
						// Check if in set
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
