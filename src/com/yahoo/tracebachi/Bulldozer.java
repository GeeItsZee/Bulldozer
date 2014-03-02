package com.yahoo.tracebachi;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.yahoo.tracebachi.Executors.Border;
import com.yahoo.tracebachi.Executors.Box;
import com.yahoo.tracebachi.Executors.Cone;
import com.yahoo.tracebachi.Executors.Copy;
import com.yahoo.tracebachi.Executors.Cylinder;
import com.yahoo.tracebachi.Executors.Load;
import com.yahoo.tracebachi.Executors.Replace;
import com.yahoo.tracebachi.Executors.Rotate;
import com.yahoo.tracebachi.Executors.Save;
import com.yahoo.tracebachi.Executors.Selection;
import com.yahoo.tracebachi.Executors.Sphere;
import com.yahoo.tracebachi.Executors.Undo;
import com.yahoo.tracebachi.Managers.BlockSet;
import com.yahoo.tracebachi.Managers.MultiBlockGroupManager;
import com.yahoo.tracebachi.Managers.SingleBlockGroupManager;

public class Bulldozer extends JavaPlugin
{
	// Static reference to main class
	public static Bulldozer core = null;
	
	// Static Class Constants
	public static final String TAG_POSITIVE = 
		ChatColor.YELLOW + "[BullDozer] " + ChatColor.GREEN;
	public static final String TAG_NEGATIVE = 
		ChatColor.YELLOW + "[BullDozer] " + ChatColor.RED;
	public static final String ERROR_NO_PERM = 
		ChatColor.RED + "[Error] You do not have the permission to do that.";
	public static final String ERROR_BAD_FLAG = 
		ChatColor.RED + "[Error] Invalid flag for this command.";
	public static final String ERROR_NO_SELECTION = 
		ChatColor.RED + "[Error] Selection is empty!";
	public static final String ERROR_NO_CLIPBOARD = 
		ChatColor.RED + "[Error] Clip Board is empty!";
	public static final String ERROR_NO_UNDO = 
		ChatColor.RED + "[Error] Nothing to undo!";
	public static final String ERROR_CONSOLE = 
		"[Error] Command cannot be run in the console.";
	public static final String ARCH_FOLDER = 
		"plugins" + File.separator + "ArchFiles" + File.separator;
	
	// Class variables
	private SingleBlockGroupManager playerSelection = null;
	private SingleBlockGroupManager playerCopy = null;
	private MultiBlockGroupManager playerUndo = null;
	private ExecutorService asyncExec = null;
	
	// Constructor
	public Bulldozer()
	{		
		// Create the Arch Folder if not already there
		File savedFolder = new File( ARCH_FOLDER );
		savedFolder.mkdir();
	}

	// Called on Plug-in Enable
	@Override
	public void onEnable()
	{
		// Set the static reference
		core = this;
		
		// Utility Setup
		playerSelection = new SingleBlockGroupManager();
		playerCopy = new SingleBlockGroupManager();
		playerUndo = new MultiBlockGroupManager();
		asyncExec = Executors.newFixedThreadPool( 5 );
		
		// Log the enable
		getServer().getConsoleSender().sendMessage( 
			ChatColor.GREEN + "Running: Bulldozer Release v1" );
		
		// Initialize the shape command executors
		getCommand( "box" ).setExecutor( new Box() );
		getCommand( "cyl" ).setExecutor( new Cylinder() );
		getCommand( "sph" ).setExecutor( new Sphere() );
		getCommand( "cone" ).setExecutor( new Cone() );
		getCommand( "border" ).setExecutor( new Border() );
		
		// Initialize the replacement command executor
		getCommand( "replace" ).setExecutor( new Replace() );
		
		// Initialize the selection command executor
		Selection selectExec = new Selection();
		getCommand( "bdkit" ).setExecutor( selectExec );
		getCommand( "clears" ).setExecutor( selectExec );
		getCommand( "clearc" ).setExecutor( selectExec );
		
		// Initialize the copy command executor
		getCommand( "copy" ).setExecutor( new Copy() );
		
		// Initialize the load command executor
		getCommand( "load" ).setExecutor( new Load() );
		
		// Initialize the save command executor
		getCommand( "save" ).setExecutor( new Save() );
		
		// Initialize the rotate command executor
		getCommand( "rotate" ).setExecutor( new Rotate() );
		
		// Initialize the undo command executor
		getCommand( "undo" ).setExecutor( new Undo() );
		
		// Initialize the copy command executor
		getCommand( "copy" ).setExecutor( new Copy() );
	
		// Register listener
		Bukkit.getServer().getPluginManager().registerEvents( 
			new Listener_Tool( this ) , this );
	}

	// Called on Plug-in Disable / Reload
	@Override
	public void onDisable()
	{
		// Remove the static reference
		core = null;
		
		// Shut down the thread service
		asyncExec.shutdownNow();
		
		// Disable the selection manager
		playerSelection.closeManager();
		playerSelection = null;
		
		// Clear the block storage
		playerUndo.closeManager();
		playerUndo = null;
		
		// Clear the block storage
		playerCopy.closeManager();
		playerCopy = null;
	}

	//////////////////////////////////////////////////////////////////////////
	// Method: 	verifyPerm
	// Purpose: 	Check the player's permissions
	//////////////////////////////////////////////////////////////////////////
	public boolean verifyPerm( CommandSender user, String permission )
	{	
		// Check if player is OP
		return user.isOp();
	}
	
	public BlockSet getSelectionFor( String playerName )
	{
		return playerSelection.getGroupFor( playerName );
	}
	
	public BlockSet getClipboardFor( String playerName )
	{
		return playerCopy.getGroupFor( playerName );
	}
	
	//
	public void setCopyBlocksFor( String playerName, BlockSet toReplaceWith )
	{
		// Remove the current
		playerCopy.replaceGroupFor( playerName, toReplaceWith );
	}
	
	public void pushIntoUndoFor( String playerName, BlockSet toPush )
	{
		// Push into
		playerUndo.pushGroupFor( playerName, toPush );
	}
	
	public void restoreOneFromUndoFor( String playerName, 
		World playerWorld )
	{
		playerUndo.popGroupFor( playerName )
			.restoreInWorld( true, playerWorld );
	}
	
	public int restoreAllFromUndoFor( String playerName,
		World playerWorld )
	{
		return playerUndo.removeGroupsAndRestoreFor( 
			playerName, playerWorld );
	}
	
	public void clearUndoFor( String playerName )
	{
		playerUndo.removeGroupsAndClearFor( playerName );
	}
	
	public < T > Future< T > scheduleAsyncCallable( Callable< T > toRun )
	{		
		// Check if task is not null
		if( toRun != null )
		{
			return asyncExec.submit( toRun );
		}
		
		// Return null for invalid task
		return null;
	}
}