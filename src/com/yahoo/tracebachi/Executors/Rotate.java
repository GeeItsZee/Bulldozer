package com.yahoo.tracebachi.Executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Managers.BlockSet;
import com.yahoo.tracebachi.Utils.InputParseUtil;

public class Rotate implements CommandExecutor 
{
	// Class variables
	public static final String permName = "Rotate";
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	Rotate Constructor
	//////////////////////////////////////////////////////////////////////////
	public Rotate( Bulldozer instance ) { core = instance; }
	
	//////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "rotate" command
	//////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender sender, Command baseCommand, 
		String arg2, String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length;
		int numRot = 1;
		String playerName = null;
		Player user = null;
		BlockSet clipBoard = null;
		
		// Verify valid command
		if( ! baseCommand.getName().equalsIgnoreCase( "rotate" ) )
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
		if( argLen < 1 || argLen > 2 )
		{
			sender.sendMessage( ChatColor.YELLOW 
				+ "Command must be of the form:" );
			sender.sendMessage( ChatColor.GREEN + "     "
				+ "/rotate [Num of 90 Degree Rotations]" );
			return true;
		}
		
		// Set player variables
		user = (Player) sender;
		playerName = user.getName();
		clipBoard = core.playerCopy.getGroupFor( playerName );
		
		// Verify player has something in clipboard
		if( clipBoard.getSize() < 1 )
		{
			user.sendMessage( core.ERROR_NO_CLIPBOARD );
			return true;
		}
		
		// Parse arguments
		switch( argLen )
		{
			case 1:
				numRot = InputParseUtil.parseSafeInt( 
					commandArgs[0], -128, 127, 0 );
				break;
			default:
				break;
		}

		/////////////////////////////////////////////////////////////////////
		// Rotate
		clipBoard.rotateInPlane_XZ( numRot );
		
		// Output that the clipboard was rotated
		user.sendMessage( core.TAG_POSITIVE + "Clipboard Rotated by " 
			+ ChatColor.LIGHT_PURPLE + numRot 
			+ ChatColor.GREEN + "*90 Degrees." );
		return true;
	}
}
