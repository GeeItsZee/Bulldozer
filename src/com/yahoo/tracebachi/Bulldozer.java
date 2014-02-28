package com.yahoo.tracebachi;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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
import com.yahoo.tracebachi.Executors.Rotate;
import com.yahoo.tracebachi.Executors.Save;
import com.yahoo.tracebachi.Executors.Selection;
import com.yahoo.tracebachi.Executors.Sphere;
import com.yahoo.tracebachi.Executors.Undo;
import com.yahoo.tracebachi.Utils.MultiBlockGroupManager;
import com.yahoo.tracebachi.Utils.SingleBlockGroupManager;

public class Bulldozer extends JavaPlugin
{	
	// Initialize the custom item
	public ItemStack selectionTool = new ItemStack( 
		Material.WOOL , 1 , (byte) 15 );
	public ItemStack pasteTool = new ItemStack( 
		Material.WOOL , 1 , (byte) 9 );
	public ItemStack measureTool = new ItemStack( 
		Material.WOOL , 1 , (byte) 1 );
	public ItemMeta selectionToolMeta = selectionTool.getItemMeta();
	public ItemMeta pasteToolMeta = pasteTool.getItemMeta();
	public ItemMeta measureToolMeta = measureTool.getItemMeta();
	
	// Initialize the utilities
	public SingleBlockGroupManager playerSelections = null;
	public SingleBlockGroupManager playerCopy = null;
	public MultiBlockGroupManager playerUndo = null;
	public ExecutorService asyncExec = null;
	
	// Initialize message strings
	public final String TAG_POSITIVE = 
		ChatColor.YELLOW + "[BullDozer] " + ChatColor.GREEN;
	public final String TAG_NEGATIVE = 
		ChatColor.YELLOW + "[BullDozer] " + ChatColor.RED;
	public final String ERROR_NO_PERM = 
		ChatColor.RED + "You do not have the permission to do that.";
	public final String ERROR_BAD_FLAG = 
		ChatColor.RED + "Invalid flag for this command.";
	public final String ERROR_NO_SELECTION = 
		ChatColor.RED + "Block not selected!";
	public final String ERROR_NO_CLIPBOARD = 
		ChatColor.RED + "Selection not in the clipboard!";
	public final String ERROR_NO_UNDO = 
		ChatColor.RED + "There is nothing to undo!";
	public final String ERROR_CONSOLE = 
		"Command cannot be run in the console.";
	public final String ARCH_FOLDER = 
		"plugins" + File.separator + "ArchFiles" + File.separator;
	
	// Constructor
	public Bulldozer()
	{
		// Set up the custom items
		selectionToolMeta.setDisplayName( ChatColor.YELLOW + "Mark" );
		selectionTool.setItemMeta( selectionToolMeta );
		
		pasteToolMeta.setDisplayName( ChatColor.YELLOW + "Paste" );
		pasteTool.setItemMeta( pasteToolMeta );
		
		measureToolMeta.setDisplayName( ChatColor.YELLOW + "Measure" );
		measureTool.setItemMeta( measureToolMeta );
		
		// Create the Arch Folder if not already there
		File savedFolder = new File( ARCH_FOLDER );
		savedFolder.mkdir();
	}

	// Called on Plug-in Enable
	@Override
	public void onEnable()
	{
		// Utility Setup
		playerSelections = new SingleBlockGroupManager();
		playerCopy = new SingleBlockGroupManager();
		playerUndo = new MultiBlockGroupManager();
		asyncExec = Executors.newFixedThreadPool( 5 );
		
		// Log the enable
		getServer().getConsoleSender().sendMessage( 
			ChatColor.GREEN + "Running: Bulldozer Release v1" );
		
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
		getCommand( "bdkit" ).setExecutor( selectExec );
		getCommand( "clears" ).setExecutor( selectExec );
		getCommand( "clearc" ).setExecutor( selectExec );
		
		// Initialize the copy command executor
		getCommand( "copy" ).setExecutor( new Copy( this ) );
		
		// Initialize the load command executor
		getCommand( "load" ).setExecutor( new Load( this ) );
		
		// Initialize the save command executor
		getCommand( "save" ).setExecutor( new Save( this ) );
		
		// Initialize the rotate command executor
		getCommand( "rotate" ).setExecutor( new Rotate( this ) );
		
		// Initialize the undo command executor
		getCommand( "undo" ).setExecutor( new Undo( this ) );
		
		// Initialize the copy command executor
		getCommand( "copy" ).setExecutor( new Copy( this ) );
	
		// Register listener
		Bukkit.getServer().getPluginManager().registerEvents( 
			new Listener_Tool( this ) , this );
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
	public boolean verifyPerm( CommandSender user, String permission )
	{	
		// Check if player is OP
		return user.isOp();
	}	
}

		
