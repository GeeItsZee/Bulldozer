package com.yahoo.tracebachi.Utils;

public class BlockInfo
{
	// Static class constants
	private static final int MASK_8 = 0xFF;
	private static final int MASK_16 = 0xFFFF;
	private static final int MASK_24 = 0xFFFFFF;
	
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
	
	public BlockInfo( long coordinates, int data )
	{		
		// Unload the z
		z = (int) (coordinates & MASK_24);
		System.out.print( (int) (coordinates & 0xFFFFFF) );
		coordinates = coordinates >>> 24;
		
		// Unload the y
		y = (int) (coordinates & MASK_16);
		System.out.print( (short) (coordinates & 0xFFFF) );
		coordinates = coordinates >>> 16;
		
		// Unload the x
		x = (int) (coordinates & MASK_24);
		System.out.print( (int) (coordinates & 0xFFFFFF) );
		coordinates = coordinates >>> 24;
		
		// Unload type and subtype
		subID = (byte) (data & MASK_8);
		data = data >>> 8;
		ID = (int) (data & MASK_24);
		data = data >>> 24;
	}
	
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
	
	public String compressToString()
	{
		// Method variables
		long xyz = 0;
		int data = 0;
		
		// Load x
		xyz = xyz | x;
		xyz = xyz << 24;
		
		// Load y
		xyz = xyz | y;
		xyz = xyz << 16;
		
		// Load z
		xyz = xyz | z;
		
		// Load type and sub type
		data = data | ID;
		data = data << 24;
		data = data | subID;
		
		// Return string
		return new String( String.valueOf( xyz ) + ' ' 
			+ String.valueOf( data ) );
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
			+ String.valueOf( z ) );
		
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
