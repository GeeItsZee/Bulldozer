package com.yahoo.tracebachi;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.yahoo.tracebachi.Executors.Border;
import com.yahoo.tracebachi.Executors.Box;
import com.yahoo.tracebachi.Executors.Cone;
import com.yahoo.tracebachi.Executors.Cylinder;
import com.yahoo.tracebachi.Executors.Replace;
import com.yahoo.tracebachi.Executors.Selection;
import com.yahoo.tracebachi.Executors.Sphere;
import com.yahoo.tracebachi.Executors.Undo;
import com.yahoo.tracebachi.Utils.BlockStorageManager;
import com.yahoo.tracebachi.Utils.DatabaseManager;
import com.yahoo.tracebachi.Utils.SelectionManager;

@SuppressWarnings("deprecation")
public class Bulldozer extends JavaPlugin
{
	
	// Initialize DB Variables
	/*private String dbUser = null;
	private String dbPassword = null;
	private String dbURL = null;*/

	// Initialize the custom item
	public ItemStack selectionTool = new ItemStack( 318 );
	public ItemMeta selectionToolMeta = selectionTool.getItemMeta();
	
	// Initialize the utilities
	public SelectionManager playerSelections = null;
	public BlockStorageManager playerUndo = null;
	public DatabaseManager pluginDB = null ;
	
	// Initialize the list of able players
	List < String > fullAccess = null;
	
	// Initialize errors strings
	public final String ERROR_PERM = ChatColor.RED + "You do not have the permission to do that." ;
	public final String ERROR_INT = ChatColor.RED + "You have entered an invalid value for an integer." ;
	public final String ERROR_COMMAND = ChatColor.RED + "You have entered a valid command." ;
	public final String ERROR_SELECTION = ChatColor.RED + "You have not selected any block!" ;
	public final String ERROR_CONSOLE = "[Bulldozer Console] This command cannot be run in the console." ;
	public final String ERROR_NO_UNDO = ChatColor.RED + "There is nothing to undo!" ;
	public final String MESSAGE_UNDO = ChatColor.GREEN + "Undo of one step." ;

	// Called on Plug-in Enable
	@Override
	public void onEnable()
	{
		// Get information from the config file
		//dbUser = this.getConfig().getConfigurationSection("").getString( "DatabaseUser" );
		//dbPassword = this.getConfig().getConfigurationSection("").getString( "DatabasePassword" );
		//dbURL = this.getConfig().getConfigurationSection("").getString( "DatabaseURL" );
		//fullAccess  = this.getConfig().getConfigurationSection("").getStringList( "Total_Access" );
		
		// Utility Setup
		playerSelections = new SelectionManager();
		playerUndo = new BlockStorageManager();
		/*pluginDB = new Util_Database( this , dbURL , dbUser , dbPassword );
		pluginDB.setupDatabase( "Bulldozer" );
		pluginDB.setupTable( "Permissions" );
		pluginDB.setupColumn( "Permissions" , "CanEdit" , false );
		
		// Make sure that permissions for the full access are set
		for( String toModify : fullAccess )
			{
				
				pluginDB.addPlayer( "Permissions" , toModify );
				pluginDB.setValue( "Permissions" , toModify , "CanEdit = '1'" );
				
			}*/
	
		// Set up the custom item
		selectionToolMeta.setDisplayName( ChatColor.BLUE + "Marker" );
		selectionTool.setItemMeta( selectionToolMeta );
		
		// Broadcast the enable
		getServer().broadcastMessage( ChatColor.BLUE + "Running: Bulldozer Alpha v6.2" );
		
		// Set the executors
		getCommand( "box" ).setExecutor( new Box( this ) );
		getCommand( "cyl" ).setExecutor( new Cylinder( this ) );
		getCommand( "sph" ).setExecutor( new Sphere( this ) );
		getCommand( "cone" ).setExecutor( new Cone( this ) );
		getCommand( "border" ).setExecutor( new Border( this ) );
		
		getCommand( "replace" ).setExecutor( new Replace( this ) );
		
		getCommand( "marker" ).setExecutor( new Selection( this ) );
		getCommand( "clear" ).setExecutor( new Selection( this ) );
		getCommand( "clearall" ).setExecutor( new Selection( this ) );
		
		getCommand( "undo" ).setExecutor( new Undo( this ) );
		getCommand( "wipe" ).setExecutor( new Undo( this ) );
	
		// Set listener
		Bukkit.getServer().getPluginManager().registerEvents( new Listener_Tool( this ) , this );
	}

	// Called on Plug-in Disable / Reload
	@Override
	public void onDisable()
	{
			
		// Disable the selection manager
		playerSelections.removeAll();
		playerSelections = null;
		
		// Clear the block storage
		playerUndo.clearEverythingForAll();
		playerUndo = null;
		
		// Close the database
		//pluginDB.close();
		
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

		
