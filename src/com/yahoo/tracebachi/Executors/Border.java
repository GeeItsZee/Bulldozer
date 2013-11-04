package com.yahoo.tracebachi.Executors;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yahoo.tracebachi.Bulldozer;
import com.yahoo.tracebachi.Utils.BlockGroup;


public class Border implements CommandExecutor 
	{

		// Class variables
		private Bulldozer mainPlugin = null;
		
		// Default Constructor
		public Border( Bulldozer instance )
			{
				
				mainPlugin = instance;
				
			}

		// Override: onCommand
		@Override
		public boolean onCommand( CommandSender client , Command baseCommand , String arg2 , String[] commandArgs )
			{
				
				// Method variables
				int argLen = commandArgs.length ;
				
				// Check the command
				if( baseCommand.getName().equalsIgnoreCase( "border" ) )
					{
						
						// Check if client is a player
						if( client instanceof Player )
							{
								
								// Create/Set player variables
								Player cPlayer = (Player) client;
								World cPlayerWorld = cPlayer.getWorld();
								String cPlayerName = cPlayer.getName() ;
								List < Location > cPlayerSelection = mainPlugin.playerSelections.getSelectionFor( cPlayerName );
								BlockGroup blocksToStore = null;
								
								int[] maxCoord = null , minCoord = null ;
								int listSize = 0 , lowOffset = 0 , highOffset = 0 , desiredBlockID = 0 ;
								
								// Check One: Verify Player has a valid command
								if( argLen == 0 )
									{
										
										cPlayer.sendMessage( ChatColor.YELLOW + "The possible commands are:" );
										cPlayer.sendMessage( ChatColor.GREEN + "    /border -c [Block ID] [Low Offset] [High Offset]" );
										cPlayer.sendMessage( ChatColor.GREEN + "    /border -b [Block ID] [Low Offset] [High Offset]" );
										cPlayer.sendMessage( ChatColor.YELLOW + "Make sure you have a selection before running the command." );
										return true;
										
									}
								
								// Check Two: Verify Player has a selection
								if( cPlayerSelection == null )
									{
										
										cPlayer.sendMessage( mainPlugin.ERROR_SELECTION );
										return true;
										
									}
								
								// Check Three: Verify Player Permissions (Send error if false)
								if( !(mainPlugin.verifyPerm( cPlayerName , "Border" )) )
									{
										
										cPlayer.sendMessage( mainPlugin.ERROR_PERM );
										return true;
										
									}
								
								// Get the number of selections
								listSize = cPlayerSelection.size();
								
								// Get the minimum and maximum array
								maxCoord = mainPlugin.playerSelections.getMaximumsFor( cPlayerName );
								minCoord = mainPlugin.playerSelections.getMinimumsFor( cPlayerName );
								
								// Check Four: Verify Valid Values (Parse-able Values)
								try
									{
										switch( argLen )
											{
												case 4:
													highOffset = mainPlugin.safeInt( commandArgs[3] , 0 , 254 - maxCoord[1] );
												case 3:
													lowOffset = mainPlugin.safeInt( commandArgs[2] , 0 , minCoord[1] - 5 );
												case 2:
													desiredBlockID = mainPlugin.safeInt( commandArgs[1] , 0 , 173 );
													break;
												default:
													highOffset = lowOffset = desiredBlockID = 0 ;
													break;
											}
									}
								catch( NumberFormatException nfe )
									{
										
										cPlayer.sendMessage( mainPlugin.ERROR_INT );
										return true;
										
									}

								///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
								// Chunk Border
								///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
								if( commandArgs[0].equalsIgnoreCase( "-c" ) )
									{

										// Make a new group for the player
										blocksToStore = new BlockGroup();
										
										// Execute for chunks
										for( int listIndex = 0 ; listIndex < listSize ; listIndex++ )
											{
												
												// Set up chunk variables
												Block chunkMinBlock = cPlayerSelection.get( listIndex ).getChunk().getBlock( 0 , 1 , 0 );
												Block chunkMaxBlock = cPlayerSelection.get( listIndex ).getChunk().getBlock( 15 , 1 , 15 );
												
												// Execute Change ( X = 0 ; Y = 1 ; Z = 2 )
												setBorder( cPlayerWorld , blocksToStore , 
														chunkMinBlock.getX() , minCoord[1] - lowOffset , chunkMinBlock.getZ() , 
														chunkMaxBlock.getX() , maxCoord[1] + highOffset , chunkMaxBlock.getZ() , 
														desiredBlockID );
												
											}
										
										// Push the recorded blocks
										mainPlugin.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
										blocksToStore = null;
										
										// Return for complete
										cPlayer.sendMessage( ChatColor.GREEN + "[Bulldozer] Chunk Border Complete." );
										return true;
										
									}
								///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
								// Point Border
								///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
								else if( commandArgs[0].equalsIgnoreCase( "-p" ) )
									{
										
										// Make a new group for the player
										blocksToStore = new BlockGroup();
										
										// Execute Change ( X = 0 ; Y = 1 ; Z = 2 )
										setBorder( cPlayerWorld , blocksToStore , 
												minCoord[0] , minCoord[1] - lowOffset , minCoord[2] , 
												maxCoord[0] , maxCoord[1] + highOffset , maxCoord[2] , 
												desiredBlockID );
										
										// Push the recorded blocks
										mainPlugin.playerUndo.pushGroupFor( cPlayerName , blocksToStore );
										blocksToStore = null;
										
										// Return for complete
										cPlayer.sendMessage( ChatColor.GREEN + "[Bulldozer] Point Border Complete." );
										return true;
										
									}
								
							}
						else { client.sendMessage( mainPlugin.ERROR_CONSOLE ); }
						
					}
				
				// Return false by default
				return false;
			}
		
		// Method: setBarrier
		private void setBorder( World curWorld , BlockGroup blockStorage , int minX , int minY , int minZ , int maxX , int maxY , int maxZ , int blockType )
			{
				
				// Method variables
				Block cursorBlock = null;
				int blockX = minX , blockY = minY , blockZ = minZ ;
				
				// Loop from (minX,minZ) to (maxX,minZ) [top left to right]
				// At the end of the loop: minX -> maxX
				for( blockX = minX , blockZ = minZ ; blockX <= maxX ; blockX++ )
					{
						
						for( blockY = minY ; blockY <= maxY ; blockY++ )
							{
								
								// Get the block
								cursorBlock = curWorld.getBlockAt( blockX , blockY , blockZ );
								
								// If not same as the block type, change it and record the data
								if( cursorBlock.getTypeId() != blockType ) 
									{ 
										
										// Record the data
										blockStorage.addBlock( blockX , blockY , blockZ , blockType , (byte) 0 );
										
										// Change the data
										cursorBlock.setTypeId( blockType );
										
									}
							}
					}
				
				// Loop from (maxX,minZ) to (maxX,maxZ) [top right to bottom right]
				// At the end of the loop: minZ -> maxZ
				for( blockZ = minZ , blockX = maxX ; blockZ <= maxZ ; blockZ++ )
					{
						
						for( blockY = minY ; blockY <= maxY ; blockY++ )
							{
								
								// Get the block
								cursorBlock = curWorld.getBlockAt( blockX , blockY , blockZ );
								
								// If not same as the block type, change it and record the data
								if( cursorBlock.getTypeId() != blockType ) 
									{ 
										
										// Record the data
										blockStorage.addBlock( blockX , blockY , blockZ , blockType , (byte) 0 );
										
										// Change the data
										cursorBlock.setTypeId( blockType );
										
									}
							}
					}
				
				// Loop from (maxX,maxZ) to (minX,minZ) [bottom right to bottom left]
				// At the end of the loop: maxX -> minX
				for( blockX = maxX , blockZ = maxZ ; blockX >= minX ; blockX-- )
					{
						
						for( blockY = minY ; blockY <= maxY ; blockY++ )
							{
								
								// Get the block
								cursorBlock = curWorld.getBlockAt( blockX , blockY , blockZ );
								
								// If not same as the block type, change it and record the data
								if( cursorBlock.getTypeId() != blockType ) 
									{ 
										
										// Record the data
										blockStorage.addBlock( blockX , blockY , blockZ , blockType , (byte) 0 );
										
										// Change the data
										cursorBlock.setTypeId( blockType );
										
									}
							}
					}
				
				// Loop from (minX,maxZ) to (minX,minZ) [bottom left to top left]
				// At the end of the loop: maxZ -> minZ
				for( blockZ = maxZ , blockX = minX ; blockZ >= minZ ; blockZ-- )
					{
						
						for( blockY = minY ; blockY <= maxY ; blockY++ )
							{
								
								// Get the block
								cursorBlock = curWorld.getBlockAt( blockX , blockY , blockZ );
								
								// If not same as the block type, change it and record the data
								if( cursorBlock.getTypeId() != blockType ) 
									{ 
										
										// Record the data
										blockStorage.addBlock( blockX , blockY , blockZ , cursorBlock.getTypeId() , (byte) 0 );
										
										// Change the data
										cursorBlock.setTypeId( blockType );
										
									}
							}
					}
			}
		
	}