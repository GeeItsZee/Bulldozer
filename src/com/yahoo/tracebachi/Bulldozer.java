package com.yahoo.tracebachi;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.yahoo.tracebachi.Executors.Border;
import com.yahoo.tracebachi.Executors.Box;
import com.yahoo.tracebachi.Executors.Cone;
import com.yahoo.tracebachi.Executors.Copy;
import com.yahoo.tracebachi.Executors.Cylinder;
import com.yahoo.tracebachi.Executors.Load;
import com.yahoo.tracebachi.Executors.Replace;
import com.yahoo.tracebachi.Executors.Save;
import com.yahoo.tracebachi.Executors.Selection;
import com.yahoo.tracebachi.Executors.Sphere;
import com.yahoo.tracebachi.Executors.Undo;
import com.yahoo.tracebachi.Utils.MultiBlockGroupManager;
import com.yahoo.tracebachi.Utils.SingleBlockGroupManager;

public class Bulldozer extends JavaPlugin
{
	
	// Initialize the custom item
	public ItemStack selectionTool = new ItemStack( Material.WOOL , 1 , (byte) 15 );
	public ItemStack pasteTool = new ItemStack( Material.WOOL , 1 , (byte) 9 );
	public ItemMeta selectionToolMeta = selectionTool.getItemMeta();
	public ItemMeta pasteToolMeta = pasteTool.getItemMeta();
	
	// Initialize the utilities
	public SingleBlockGroupManager playerSelections = null;
	public SingleBlockGroupManager playerCopy = null;
	public MultiBlockGroupManager playerUndo = null;
	public ExecutorService asyncExec = null;
	
	// Initialize message strings
	public final String TAG_POSITIVE = ChatColor.YELLOW + "[Bulldozer] " + ChatColor.GREEN;
	public final String TAG_NEGATIVE = ChatColor.YELLOW + "[Bulldozer] " + ChatColor.RED;
	public final String ERROR_PERM = ChatColor.RED + "You do not have the permission to do that." ;
	public final String ERROR_INT = ChatColor.RED + "You have entered an invalid value for an integer." ;
	public final String ERROR_SELECTION = ChatColor.RED + "You have not selected any block!" ;
	public final String ERROR_CONSOLE = "[Bulldozer Console] This command cannot be run in the console." ;
	public final String ERROR_NO_UNDO = ChatColor.RED + "There is nothing to undo!" ;
	public final String PLAN_FOLDER = "plugins" + File.separator + "ArchFiles" + File.separator;

	// Called on Plug-in Enable
	@Override
	public void onEnable()
	{
		// Utility Setup
		playerSelections = new SingleBlockGroupManager();
		playerCopy = new SingleBlockGroupManager();
		playerUndo = new MultiBlockGroupManager();
		asyncExec = Executors.newFixedThreadPool( 5 );
	
		// Set up the custom item
		selectionToolMeta.setDisplayName( ChatColor.YELLOW + "Marker" );
		selectionTool.setItemMeta( selectionToolMeta );
		
		pasteToolMeta.setDisplayName( ChatColor.YELLOW + "Paste Block" );
		pasteTool.setItemMeta( pasteToolMeta );
		
		// Broadcast the enable
		getServer().broadcastMessage( ChatColor.BLUE + "Running: Bulldozer Alpha v7" );
		
		// Create the Plan Folder if not already there
		File savedFolder = new File( getDataFolder().getParent() + File.separator + "ArchFiles" );
		savedFolder.mkdir();
		
		// Initialize the shape command executors
		getCommand( "box" ).setExecutor( new Box( this ) );
		getCommand( "cyl" ).setExecutor( new Cylinder( this ) );
		getCommand( "sph" ).setExecutor( new Sphere( this ) );
		getCommand( "cone" ).setExecutor( new Cone( this ) );
		getCommand( "border" ).setExecutor( new Border( this ) );
		
		// Initialize the replacement command executor
		getCommand( "replace" ).setExecutor( new Replace( this ) );
		
		// Initialize the selection command executor
		Selection selectExec = new Selection( this );
		getCommand( "kit" ).setExecutor( selectExec );
		getCommand( "clear" ).setExecutor( selectExec );
		
		// Initialize the copy command executor
		getCommand( "copy" ).setExecutor( new Copy( this ) );
		
		// Initialize the load command executor
		getCommand( "load" ).setExecutor( new Load( this ) );
		
		// Initialize the save command executor
		getCommand( "save" ).setExecutor( new Save( this ) );
		
		// Initialize the undo command executor
		Undo undoExec = new Undo( this );
		getCommand( "undo" ).setExecutor( undoExec );
		getCommand( "wipe" ).setExecutor( undoExec );
		
		// Initialize the copy command executor
		getCommand( "copy" ).setExecutor( new Copy( this ) );
	
		// Register listener
		Bukkit.getServer().getPluginManager().registerEvents( new Listener_Tool( this ) , this );
	}

	// Called on Plug-in Disable / Reload
	@Override
	public void onDisable()
	{
		// Shut down the thread service
		asyncExec.shutdownNow();
		
		// Disable the selection manager
		playerSelections.closeManager();
		playerSelections = null;
		
		// Clear the block storage
		playerUndo.closeManager();
		playerUndo = null;
		
		// Clear the block storage
		playerCopy.closeManager();
		playerCopy = null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	verifyPerm
	// Purpose: 	Check the player's permissions
	//////////////////////////////////////////////////////////////////////////////////////////////
	public boolean verifyPerm( Player user , String permission )
	{	
		// Check if player is OP
		return user.isOp();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	safeInteger
	// Purpose: 	Convert a string to an integer
	//////////////////////////////////////////////////////////////////////////////////////////////
	public int safeInt( String toParse , int minimumVal , int maximumVal ) throws NumberFormatException
	{
		// Initialize values
		int result = 0;
				
		// Try to parse
		result = Integer.parseInt( toParse );
		
		// Check for minimum or less
		if( result <= minimumVal )
		{
			// Set zero for invalid
			result = minimumVal;
		}
		else if( result >= maximumVal )
		{
			// Set to max
			result = maximumVal;
		}
		
		// Return
		return result;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// Method: 	safeIntList
	// Purpose: 	Convert a rectangular prism of the selected blocks to a different ID
	//////////////////////////////////////////////////////////////////////////////////////////////
	public int[] safeIntList( String toParse , int minVal , int maxVal ) throws NumberFormatException
	{
		// Split the strings if needed
		String[] values = toParse.split(":");

		// Initialize an integer array for return values
		int[] toReturn = new int[ 2 ];
		
		// Loop through and parse each of the values
		switch( values.length )
		{
			case 2:
				toReturn[1] = safeInt( values[1] , 0 , 15 );
			case 1:
				toReturn[0] = safeInt( values[0] , minVal , maxVal );
				break;
			default:
				toReturn[1] = toReturn [0] = 0;
				break;
		}
		
		// Return the integer array
		return toReturn;
	}
	
}

		
