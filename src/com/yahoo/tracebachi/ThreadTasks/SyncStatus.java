package com.yahoo.tracebachi.ThreadTasks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Utils.BlockGroup;


public class SyncStatus implements Runnable
{
	// Class variables
	private Future< BlockGroup > callCheck = null;
	private String taskDesc = null;
	private Player playerToInform = null;
	private Bulldozer core = null;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	SyncStatus Constructor
	//////////////////////////////////////////////////////////////////////////////////////////////
	public SyncStatus( Future< BlockGroup > future , Player targetOfMessage , String taskName , Bulldozer callingPlugin )
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
	//			reschedule another task 5 ticks later
	//////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void run()
	{
		// Check the status of the future
		if( callCheck.isDone() )
		{
			// Initialize a temporary block group
			BlockGroup temp = null;
			
			// Try to get the result from the future
			try
			{
				// Get the block group
				temp = callCheck.get();
				
				// Load the blocks into the player copy storage
				core.playerCopy.pushGroupFor( playerToInform.getName() , temp );
				temp = null;
				
				// Inform the player
				playerToInform.sendMessage( taskDesc + " is complete!" );
			}
			catch (InterruptedException e)
			{
				// Inform the player
				playerToInform.sendMessage( taskDesc + " was interrupted." );
			}
			catch (ExecutionException e)
			{
				// Inform the player
				e.getCause().printStackTrace();
				playerToInform.sendMessage( taskDesc + " failed to return values." );
			}
		}
		else
		{
			playerToInform.sendMessage( taskDesc + " in progress..." );
			core.getServer().getScheduler().runTaskLater( core , 
				new SyncStatus( callCheck , playerToInform , taskDesc , core ) , 5 );
		}
	}
}
