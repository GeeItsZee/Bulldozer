package com.yahoo.tracebachi.Managers;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.java.JavaPlugin;

public class DatabaseManager
	{

		// Class Variables
		public JavaPlugin basePlugin = null ;
		private Connection dbConnection = null ;
		private String dbURL = null;
		private String dbUsername = null;
		private String dbPassword = null;
		
		// Default Constructor
		public DatabaseManager( JavaPlugin callingPlugin , String url , String username , String password )
			{
				
				// Try to implement database
				try
					{
						
						// Load and initialize the JDBC Connector
						Class.forName( "com.mysql.jdbc.Driver" );
						
						// Connect to the calling plug-in
						basePlugin = callingPlugin;
						
						// Set the private variables
						dbURL = url;
						dbUsername = username;
						dbPassword = password;
						
						// Try to connect
						dbConnection = DriverManager.getConnection( "jdbc:mysql://" + dbURL , dbUsername , dbPassword );
						basePlugin.getLogger().info( "Connection to Database established." );
						
					}
				catch (ClassNotFoundException e)
					{ 
						
						// Print stack trace for class errors
						e.printStackTrace(); 
						
						// Tell the calling plug-in of the error
						basePlugin.getLogger().severe( "MySQL Driver is unavailable. Please post this issue on the plugin page." );
						
					}
				catch( SQLException connectFailed )
					{ 
						
						// Tell the calling plug-in of the error
						basePlugin.getLogger().severe( "Failed to Connect to the Database." );
						
					}	
				
			}
		
		// Method: Get Connection
		public Connection getConnection()
			{

				// Try
				try
					{
						
						// Check if the connection is live
						if( !(dbConnection.isValid( 10 )) )
							{
								
								// Reconnect
								dbConnection.close();
								dbConnection = DriverManager.getConnection( "jdbc:mysql://" + dbURL , dbUsername , dbPassword );
								
							}
						
					}
				catch( SQLException connectFailed )
					{ 
						
						// Tell the calling plug-in of the error
						basePlugin.getLogger().severe( "Failed to Connect to the Database." );
						return null;
						
					}
				
				// Return the connection
				return dbConnection ;
				
			}
		
		// Method: Setup Database
		public boolean setupDatabase( String nameOfDatabase )
			{
				
				// Method variables
				Statement toAsk = null;
				
				// Try
				try
					{
						
						// Create a statement
						toAsk = getConnection().createStatement();
						
						// Check if the database exists and create it if it doesn't exist
						toAsk.executeUpdate( "CREATE DATABASE IF NOT EXISTS " + nameOfDatabase + " ;" );
						toAsk.executeUpdate( "USE " + nameOfDatabase + " ;" ); 
						
						// Return true for now created / exists
						return true;
						
					}
				catch( SQLException connectFailed )
					{ 
						
						// Tell the calling plug-in of the error
						basePlugin.getLogger().severe( "Failed to create database or find the right one." );
						return false;
						
					}
				
			}
		
		// Method: Setup Table
		public boolean setupTable( String nameOfTable )
			{
				
				// Method variables
				Statement toAsk = null;
				ResultSet returnedRows = null;
				
				// Try
				try
					{
						
						// Create a statement
						toAsk = getConnection().createStatement();
						
						// Check if the table exists and create it if it doesn't exist
						returnedRows = toAsk.executeQuery( "SHOW TABLES LIKE '" + nameOfTable + "' ;" );
						if( !( returnedRows.next() ) )
							{
								
								// Create a table
								toAsk.executeUpdate( "CREATE TABLE " + nameOfTable + 
										"( PlayerName varchar(30) NOT NULL , PRIMARY KEY ( PlayerName ) ); " );
								
							}
						
						// Return true for now created / exists
						returnedRows.close();
						toAsk.close();
						return true;
						
					}
				catch( SQLException connectFailed )
					{ 
						
						// Tell the calling plug-in of the error
						basePlugin.getLogger().severe( "Failed to setup the " + nameOfTable + " table." );
						return false;
						
					}
				
			}
		
		// Method: setupColumn
		public boolean setupColumn( String nameOfTable , String nameOfColumn , boolean defaultVal )
			{
				
				// Method variables
				Statement toAsk = null;
				ResultSet returnedRows = null;
				int columnDefault = 0;
				
				// Translate the boolean
				if( defaultVal ) { columnDefault = 1; }
				
				// Try
				try
					{
						
						// Create a statement
						toAsk = getConnection().createStatement();
						
						// Check if the column exists, create one if not
						returnedRows = toAsk.executeQuery( "SHOW COLUMNS FROM " + nameOfTable + " LIKE '" + nameOfColumn + "' ;" );
						if( !( returnedRows.next() ) )
							{
								
								// Create a column
								toAsk.executeUpdate( "ALTER TABLE " + nameOfTable + " ADD " + nameOfColumn +
										" TINYINT(1) NOT NULL DEFAULT '" + columnDefault + "' ; " );
								
							}
						
						// Return true for now created / exists
						returnedRows.close();
						toAsk.close();
						return true;
						
					}
				catch( SQLException connectFailed )
					{ 
						
						// Tell the calling plug-in of the error
						basePlugin.getLogger().severe( "Failed to setup the " + nameOfColumn + " column." );
						return false;
						
					}
				
			}
		
		// Method: addPlayer
		public boolean addPlayer( String nameOfTable , String playerToAdd )
			{
				
				// Method variables
				Statement toAsk = null;
				ResultSet returnedRows = null;
				
				// Try
				try
					{
						
						// Create a statement
						toAsk = getConnection().createStatement();
						
						// Check if the player is already in the table, create if not
						returnedRows = toAsk.executeQuery( "SELECT PlayerName FROM " + nameOfTable + " WHERE PlayerName = '" + playerToAdd + "' ;" );
						if( !( returnedRows.next() ) )
							{
								
								// Create player
								toAsk.executeUpdate( "INSERT INTO " + nameOfTable + " (PlayerName) " +
										"VALUES ( '" + playerToAdd + "' ) ; " );
								
							}
						
						// Return true for now created / exists
						returnedRows.close();
						toAsk.close();
						return true;
						
					}
				catch( SQLException connectFailed )
					{ 
						
						// Tell the calling plug-in of the error
						basePlugin.getLogger().severe( "Failed to add " + playerToAdd + " to " + nameOfTable + '.' );
						return false;
						
					}
				
			}
		
		// Method: setValue
		public boolean setValue( String nameOfTable , String nameOfColumn , String playerToModify , boolean valueToSet )
			{
				
				// Method variables
				Statement toAsk = null;
				int finalValue = 0;
				
				// Translate the boolean
				if( valueToSet ) { finalValue = 1; }
				
				// Try
				try
					{
						
						// Create a statement
						toAsk = getConnection().createStatement();
						
						// Update the player
						toAsk.executeUpdate( "UPDATE " + nameOfTable + 
								" SET " + nameOfColumn + " = '" + finalValue + '\'' +
								" WHERE PlayerName = '" + playerToModify + "' ; " );
						
						// Return true for now created / exists
						toAsk.close();
						return true;
						
					}
				catch( SQLException connectFailed )
					{ 
						
						// Tell the calling plug-in of the error
						basePlugin.getLogger().severe( "Failed to update value of " + nameOfColumn + 
								" for " + playerToModify + " in " + nameOfTable + '.' );
						return false;
						
					}
				
			}
		
		// Method: setValue
		public boolean setValue( String nameOfTable , String playerToModify , String expressions )
			{
				
				// Method variables
				Statement toAsk = null;
				
				// Try
				try
					{
						
						// Create a statement
						toAsk = getConnection().createStatement();
						
						// Update the player
						toAsk.executeUpdate( "UPDATE " + nameOfTable + 
								" SET " + expressions +
								" WHERE PlayerName = '" + playerToModify + "' ; " );
						
						// Return true for now created / exists
						toAsk.close();
						return true;
						
					}
				catch( SQLException connectFailed )
					{ 
						connectFailed.printStackTrace();
						// Tell the calling plug-in of the error
						basePlugin.getLogger().severe( "Failed to update ( " + expressions + 
								" ) for " + playerToModify + " in " + nameOfTable + '.' );
						return false;
						
					}
				
			}
		
		// Method: getValue
		public int getValue( String nameOfTable , String nameOfColumn , String playerToCheck )
			{
				
				// Method variables
				Statement toAsk = null;
				ResultSet returnedRows = null;
				
				// Try
				try
					{
						
						// Create a statement
						toAsk = getConnection().createStatement();
						
						// Check the player
						returnedRows = toAsk.executeQuery( "SELECT " + nameOfColumn + "FROM " + nameOfTable + 
								"WHERE PlayerName = '" + playerToCheck + "' ; " );
						
						// Check if there is a value
						if( returnedRows.next() )
							{
								
								// Close and return
								returnedRows.close();
								toAsk.close();
								return returnedRows.getInt( 1 );
								
							}
						else
							{
								
								// Close and return
								returnedRows.close();
								toAsk.close();
								return -1 ;
								
							}
						
					}
				catch( SQLException connectFailed )
					{ 
						
						// Tell the calling plug-in of the error
						basePlugin.getLogger().severe( "Failed to find value of " + nameOfColumn + 
								" for " + playerToCheck + " in " + nameOfTable + '.' );
						return -1;
						
					}
				
			}
		
		// Method: close
		public boolean close()
			{
				
				// Try
				try
					{
						
						// Check if the connection object is live
						if( dbConnection != null )
							{
								
								// Close it
								dbConnection.close();
								dbConnection = null;
								basePlugin.getLogger().info( "Database connection succesfully closed." );
								
							}
						
						// Return true for closed
						return true;
						
					}
				catch( SQLException failToClose )
					{
						
						// Tell the logger of the exception
						basePlugin.getLogger().severe( "Database connection failed to close." );
						return false;
						
					}
				
			}
		
	}
