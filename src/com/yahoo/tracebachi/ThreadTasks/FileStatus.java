package com.yahoo.tracebachi.ThreadTasks;

import java.util.concurrent.Future;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;


public class FileStatus implements Runnable
{
	// Class variables
	private Future< Boolean > callCheck = null;
	private String taskDesc = null;
	private Player playerToInform = null;
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	SyncStatus Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public FileStatus( Future< Boolean > future , Player targetOfMessage , String taskName , Bulldozer callingPlugin )
	{
		// Copy variables
		callCheck = future;
		taskDesc = taskName;
		playerToInform = targetOfMessage;
		core = callingPlugin;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	Run
	// Purpose:	Check if the task is complete. If complete, notify the player otherwise
	//			reschedule another task 10 ticks later
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void run()
	{
		// Check the status of the future
		if( callCheck.isDone() )
		{
			// Inform the player
			playerToInform.sendMessage( ChatColor.GREEN + taskDesc + " is complete!" );
		}
		else
		{
			playerToInform.sendMessage( ChatColor.GREEN + taskDesc + " in progress..." );
			core.getServer().getScheduler().runTaskLater( core , 
				new FileStatus( callCheck , playerToInform , taskDesc , core ) , 10 );
		}
	}
}
