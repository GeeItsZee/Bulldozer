package com.yahoo.tracebachi.Executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Utils.BlockGroup;

public class Rotate implements CommandExecutor 
{
	// Class variables
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Rotate Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public Rotate( Bulldozer instance )
	{	
		core = instance;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	onCommand
	// Purpose: 	Handles the "rotate" command
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCommand( CommandSender client , Command baseCommand , String arg2 , String[] commandArgs )
	{
		// Method variables
		int argLen = commandArgs.length ;
		
		// Check the command
		if( baseCommand.getName().equalsIgnoreCase( "rotate" ) )
		{	
			// Check if client is a player
			if( client instanceof Player )
			{
				// Create/Set player variables
				Player cPlayer = (Player) client;
				String cPlayerName = cPlayer.getName() ;
				BlockGroup clipBoard = core.playerCopy.getGroupFor( cPlayerName );
				int degree = 90;
				
				//---------------------------------------------------------------------------//
				// Check One: Verify Player has a valid command -----------------------------//
				if( argLen > 1 )
				{
					cPlayer.sendMessage( ChatColor.YELLOW + "The only possible command is:" );
					cPlayer.sendMessage( ChatColor.GREEN + "    /rotate [Angle-Degree]" );
					cPlayer.sendMessage( ChatColor.YELLOW + "Make sure you have a selection before running the command." );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Two: Verify Player has a selection ---------------------------------//
				if( clipBoard.isEmpty() )
				{
					cPlayer.sendMessage( core.ERROR_CLIPBOARD );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Three: Verify Player Permissions (Send error if false) -------------//
				if( !(core.verifyPerm( cPlayer , "Rotate" )) )
				{
					cPlayer.sendMessage( core.ERROR_PERM );
					return true;
				}
				
				//---------------------------------------------------------------------------//
				// Check Five: Verify Valid Values (Parse-able Values) ----------------------//
				try
				{
					switch( argLen )
					{
						case 1:
							degree = core.safeInt( commandArgs[0] , 0 , 360 );
							break;
						default:
							break;
					}
				}
				catch( NumberFormatException nfe )
				{
					cPlayer.sendMessage( core.ERROR_INT );
					return true;
				}

				//---------------------------------------------------------------------------//
				//----------- Rotate the Clipboard ------------------------------------------//
				clipBoard.rotateRelativeToY( degree );
				
				// Output that the clipboard was rotated
				cPlayer.sendMessage( core.TAG_POSITIVE + "Clipboard Rotated by " + 
					ChatColor.LIGHT_PURPLE + degree + ChatColor.GREEN + " Degrees." );
				return true;
					
			}
			else { client.sendMessage( core.ERROR_CONSOLE ); }
		}
		
		// Return false by default
		return false;
	}
}
