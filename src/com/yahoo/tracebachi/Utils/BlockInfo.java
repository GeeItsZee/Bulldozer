package com.yahoo.tracebachi.Utils;

public class BlockInfo
{
	// Class Variables
	private int x = 0;
	private int y = 0;
	private int z = 0;
	private int ID = 0;
	private byte subID = 0;
	
	/**
	 * Initializes the object with the data passed to the constructor.
	 * 
	 * @param xPos		: X-Coordinate
	 * @param yPos		: Y-Coordinate
	 * @param zPos		: Z-Coordinate
	 * @param type		: Block ID
	 * @param subType	: Block SubID
	 */
	public BlockInfo( int xPos, int yPos, int zPos, int type, byte subType )
	{
		// Set class variables
		x = xPos;
		y = yPos;
		z = zPos;
		ID = type ;
		subID = subType ;
	}
	
	// TODO: Compress
	
	public int[] getCoordinates()
	{
		return new int[] { x, y, z };
	}
	
	public int[] getData()
	{
		return new int[] { ID, subID };
	}
	
	public void subtractRelative( int xPos, int yPos, int zPos )
	{
		// Set class variables
		x -= xPos;
		y -= yPos;
		z -= zPos;
	}
	
	public void addRelative( int xPos, int yPos, int zPos )
	{
		// Set class variables
		x += xPos;
		y += yPos;
		z += zPos;
	}
	
	public void setX( int posX )
	{
		x = posX;
	}
	
	public void setY( int posY )
	{
		y = posY;
	}
	
	public void setZ( int posZ )
	{
		z = posZ;
	}
	
	// NOTE: This method ignores the block ID. It only checks coordinates.
	@Override
	public boolean equals( Object toCompare )
	{
		// Verify not null
		if( toCompare == null )
		{
			return false;
		}
		
		// Verify same instance
		if( !(toCompare instanceof BlockInfo) )
		{
			return false;
		}
		
		// Cast object
		BlockInfo temp = (BlockInfo) toCompare;
		
		// Verify data
		if( temp.x != x || temp.y != y || temp.z != z )
		{
			return false;
		}
		
		// Default to true
		return true;
	}
	
	// HashCode
	public int hashCode()
	{
		// Create a hash string
		String hashStr = new String( String.valueOf( x )
			+ String.valueOf( y )
			+ String.valueOf( z )
			+ String.valueOf( ID )
			+ String.valueOf( subID ) );
		
		// Return hash of string
		return hashStr.hashCode();
	}
	
	@Override
	public BlockInfo clone()
	{
		// Return the cloned object
		return new BlockInfo( x, y, z, ID, subID );
	}
}
